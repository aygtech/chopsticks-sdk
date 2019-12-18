package com.chopsticks.ehub.sdk.spring;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.chopsticks.common.utils.Reflect;
import com.chopsticks.common.utils.TimeUtils;
import com.chopsticks.core.rocketmq.modern.DefaultModernClient;
import com.chopsticks.ehub.sdk.SdkClientProxy;
import com.chopsticks.ehub.sdk.caller.SdkNoticeCommand;
import com.chopsticks.ehub.sdk.caller.SdkNoticeResult;
import com.chopsticks.ehub.sdk.caller.SdkTransactionChecker;
import com.chopsticks.ehub.sdk.caller.SdkTransactionState;
import com.chopsticks.ehub.sdk.exception.SdkException;
import com.chopsticks.ehub.sdk.handler.SdkContextHolder;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EhubClientHelper extends SdkClientProxy implements SdkTransactionChecker, ApplicationContextAware {
    
    private ApplicationContext ctx;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
    
    public static final ThreadLocal<String> BIZ_VALUE = new ThreadLocal<String>();
    
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private SdkCache cache = new LocalCache();
	public void setCache(SdkCache cache) {
		this.cache = cache;
	}
	public SdkCache getCache() {
		return cache;
	}
	
	private static final ScheduledExecutorService POOL = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("tx-msg-clear").build());
	@PostConstruct
	public void post() {
	    POOL.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    ctx.getBean(EhubClientHelper.class).clear();
                }catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
	}
	@Transactional
	public void clear() throws Throwable {
	    Date sub = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1L));
        List<String> ids = jdbcTemplate.queryForList("select id from message_transaction_event where create_time < ?", String.class, sub);
        log.trace("delete from message_transaction_event where id in (?) : {}", ids);
        if(!ids.isEmpty()) {
            List<List<String>> parts = Lists.partition(ids, 1000);
            for(List<String> part : parts) {
                int num = jdbcTemplate.update("delete from message_transaction_event where id in ('" + Joiner.on("','").join(part) + "')");
                log.info("delete num : {}", num);
            }
        }
	}
	private boolean isTransaction(Object[] args) {
	    Object[] params = (Object[])args[2];
        boolean isTransaction = params.length > 0 
                                && params[0] instanceof SdkNoticeCommand 
                                && ((SdkNoticeCommand)params[0]).isTransaction();
        if(isTransaction && !TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new SdkException("not in transaction").setCode(SdkException.NOT_IN_TRANSACTION);
        }
        return isTransaction;
	}
	private void InTransaction(final Object obj, final SdkNoticeResult ret) {
	    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                DefaultModernClient client = (DefaultModernClient)Reflect.on(obj).field("client").get();
                try {
                    if(TransactionSynchronization.STATUS_COMMITTED == status) {
                        client.transactionCommit(ret);
                        log.info("手工提交 : {}, {}", ret.getId(), BIZ_VALUE.get());
                    }else if(TransactionSynchronization.STATUS_ROLLED_BACK == status) {
                        client.transactionRollback(ret, null);
                        log.warn("手工回滚 : {}, {}", ret.getId(), BIZ_VALUE.get());
                    }else {
                        log.error("TransactionSynchronization afterCompletion is unknow : {}", BIZ_VALUE.get());
                    }
                } catch(Throwable e) {
                    log.error("手工事务异常 : " + BIZ_VALUE.get() +  e.getMessage(), e);
                }
            }
        });
        saveTransactionEvent(ret);
	}
	@Override
	public <T> T extBeanInvoke(final Object obj, String method, Object... args) {
        boolean isTransaction = isTransaction(args);
        T t = super.extBeanInvoke(obj, method, args);
        if(isTransaction) {
            final SdkNoticeResult ret = (SdkNoticeResult)t;
            InTransaction(obj, ret);
        }
	    return t;
	}
	@Override
	public <T> T noticeBeanInvoke(final Object obj, String method, Object... args) {
		boolean isTransaction = isTransaction(args);
		T t = super.noticeBeanInvoke(obj, method, args);
		if(isTransaction) {
			final SdkNoticeResult ret = (SdkNoticeResult)t;
			InTransaction(obj, ret);
		}
		return t; 
	}
	
	@Override
	public void noticeExecuteProxy(Object obj, String method, Object... args) throws Throwable {
		String id = SdkContextHolder.getSdkNoticeContext().getId();
		try {
			if(cache.contans(id)) {
				log.trace("id exist : {}", id);
				return;
			}
			log.trace("cache not get id : {}, cacheClass : {}", id, cache);
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	    super.noticeExecuteProxy(obj, method, args);
		Date exprie = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
		try {
			cache.add(id, exprie);
			log.trace("cache set id : {}, cacheClass : {}-{}", id, cache, BIZ_VALUE.get());
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
	private void saveTransactionEvent(SdkNoticeResult ret){
		Date now = new Date();
	    log.trace("insert into message_transaction_event(id,create_time) values(?,?),{}, {}, {}", ret.getId(), TimeUtils.yyyyMMddHHmmssSSS(now.getTime()), BIZ_VALUE.get());
	    jdbcTemplate.update("insert into message_transaction_event(id,create_time) values(?,?)", ret.getId(), now);
	}

	@Override
	public SdkTransactionState check(SdkNoticeResult ret) {
		String id = ret.getId();
		try {
			if(checkTransaction(ret)) {
				log.warn("回查兜底 commit : {}", id);
				return SdkTransactionState.COMMIT;
			}
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		Reflect.on(ret.getMessageExt()).field("msgId").get();
		log.warn("回查兜底 unknow : {}-{}-{}", id, Reflect.on(ret.getMessageExt()).field("msgId").get(), ret.getMessageExt().getPreparedTransactionOffset());
		return SdkTransactionState.UNKNOW;
	}

	private boolean checkTransaction(SdkNoticeResult ret) {
		String id = null;
		try {
			id = jdbcTemplate.queryForObject("select id from message_transaction_event where id = ?", new Object[] {ret.getId()}, String.class);
		}catch (EmptyResultDataAccessException e) {
			// 吃掉异常
		    log.trace("select id from message_transaction_event where id = {}", ret.getId());
		}
		return !Strings.isNullOrEmpty(id);
	}
	
}