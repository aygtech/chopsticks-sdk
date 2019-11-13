package com.chopsticks.ehub.sdk.spring;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.chopsticks.common.utils.Reflect;
import com.chopsticks.core.rocketmq.modern.DefaultModernClient;
import com.chopsticks.ehub.sdk.SdkClientProxy;
import com.chopsticks.ehub.sdk.caller.SdkNoticeCommand;
import com.chopsticks.ehub.sdk.caller.SdkNoticeResult;
import com.chopsticks.ehub.sdk.caller.SdkTransactionChecker;
import com.chopsticks.ehub.sdk.caller.SdkTransactionState;
import com.chopsticks.ehub.sdk.exception.SdkException;
import com.chopsticks.ehub.sdk.handler.SdkContextHolder;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

//此为固定的框架代码，业务开发无需考虑
@Slf4j
@ConfigurationProperties
@Aspect
@Component
public class EhubClientHelper extends SdkClientProxy implements SdkTransactionChecker {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private SdkCache cache = new LocalCache();
	public void setCache(SdkCache cache) {
		this.cache = cache;
	}
	public SdkCache getCache() {
		return cache;
	}
	
	// 拦截所有的异步调用，如果是可靠调用，把结果塞到当前线程变量里面，等 spring 事务提交完毕后再手工提交事务调用
	@Override
	public <T> T noticeBeanInvoke(final Object obj, String method, Object... args) {
		Object[] params = (Object[])args[2];
		boolean isTransaction = params.length > 0 
								&& params[0] instanceof SdkNoticeCommand 
								&& ((SdkNoticeCommand)params[0]).isTransaction(); 
		if(isTransaction && !TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new SdkException("not in transaction").setCode(SdkException.NOT_IN_TRANSACTION);
		}
		T t = super.noticeBeanInvoke(obj, method, args);
		if(isTransaction) {
			final SdkNoticeResult ret = (SdkNoticeResult)t;
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCompletion(int status) {
					DefaultModernClient client = (DefaultModernClient)Reflect.on(obj).field("client").get();
					try {
						if(TransactionSynchronization.STATUS_COMMITTED == status) {
							client.transactionCommit(ret);
							log.trace("手工提交 : {}", ret.getId());
						}else if(TransactionSynchronization.STATUS_ROLLED_BACK == status) {
							client.transactionRollback(ret, null);
							log.trace("手工回滚 : {}", ret.getId());
						}else {
							log.error("TransactionSynchronization afterCompletion is unknow");
						}
					} catch(Throwable e) {
						log.error(e.getMessage(), e);
					}
				}
			});
			// 保存到DB，此处的保存和业务方法是在同一事务中，所以只要成功，后续兜底回查就可以保证异步调用为可靠的
			saveTransactionEvent(ret);
		}
		return t; 
	}
	
	@Override
	public void noticeExecuteProxy(Object obj, String method, Object... args) throws Throwable {
		String id = SdkContextHolder.getNoticeContext().getId();
		try {
			if(cache.contans(id)) {
				log.trace("id exist : {}", id);
				return;
			}
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		super.noticeExecuteProxy(obj, method, args);
		Date exprie = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
		try {
			cache.add(id, exprie);
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void saveTransactionEvent(SdkNoticeResult ret) {
		jdbcTemplate.execute("create table if not exists message_transaction_event(id varchar(50) primary key not null, create_time timestamp not null)");
		jdbcTemplate.update("insert into message_transaction_event(id,create_time) values(?,?)", ret.getId(), new Date());
		jdbcTemplate.update("delete from message_transaction_event where create_time < ?", new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1L)));
	}

	//一般在 spring 的事务结束的回调里面手工提交即可，这里只是用来做手工提交失败（网络异常，进程被kill，硬件出现问题）后的兜底保证
	@Override
	public SdkTransactionState check(SdkNoticeResult ret) {
		String id = ret.getId();
		log.trace("回查兜底 : {}", id);
		// 根据 id 去兜底查询，看最终是要提交还是未知，业务一般很难确定回滚状态，所以一般只需返回 Commit 和 UNKNOW
		try {
			if(checkTransaction(ret)) {
				return SdkTransactionState.COMMIT;
			}
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return SdkTransactionState.UNKNOW;
	}

	private boolean checkTransaction(SdkNoticeResult ret) {
		String id = jdbcTemplate.queryForObject("select id from message_transaction_event where id = ?", new Object[] {ret.getId()}, String.class);
		return !Strings.isNullOrEmpty(id);
	}
}