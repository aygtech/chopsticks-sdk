package com.chopsticks.ehub.sdk.spring;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
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
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.extern.slf4j.Slf4j;

//此为固定的框架代码，业务开发无需考虑
@Slf4j
@ConfigurationProperties
public class EhubClientHelperStarter extends SdkClientProxy implements SdkTransactionChecker {
    
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
	@Autowired
	@Lazy
	private EhubClientHelperStarter helper;
	
	private static final ScheduledExecutorService POOL = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("tx-msg-clear").build());
	@PostConstruct
	public void aaaa() {
	    POOL.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                     helper.clear();
                }catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, 0, 5, TimeUnit.MINUTES);
	}
	@Transactional
	public void clear() throws Throwable {
	    Date sub = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1L));
        List<String> ids = jdbcTemplate.queryForList("select id from message_transaction_event where create_time < ?", String.class, sub);
        log.info("delete from message_transaction_event where id in (?) : {}", ids);
        if(!ids.isEmpty()) {
            int num = jdbcTemplate.update("delete from message_transaction_event where id in ('" + Joiner.on("','").join(ids) + "')");
            log.info("delete num : {}", num);
            
        }
	}
	
	@Override
	public <T> T extBeanInvoke(final Object obj, String method, Object... args) {
	    Object[] params = (Object[])args[2];
        boolean isTransaction = params.length > 0 
                                && params[0] instanceof SdkNoticeCommand 
                                && ((SdkNoticeCommand)params[0]).isTransaction(); 
        if(isTransaction && !TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new SdkException("not in transaction").setCode(SdkException.NOT_IN_TRANSACTION);
        }
        T t = super.extBeanInvoke(obj, method, args);
        if(isTransaction) {
            final SdkNoticeResult ret = (SdkNoticeResult)t;
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
                            log.info ("手工回滚 : {}, {}", ret.getId(), BIZ_VALUE.get());
                        }else {
                            log.info("TransactionSynchronization afterCompletion is unknow : {}", BIZ_VALUE.get());
                        }
                    } catch(Throwable e) {
                        log.info("手工事务异常 : " + BIZ_VALUE.get() +  e.getMessage(), e);
                    }
                }
            });
            // 保存到DB，此处的保存和业务方法是在同一事务中，所以只要成功，后续兜底回查就可以保证异步调用为可靠的
            saveTransactionEvent(ret);
        }
	    return t;
	}
	// 拦截所有的异步调用，如果是可靠调用，把结果塞到当前线程变量里面，等 spring 事务提交完毕后再手工提交事务调用
	@Override
	public <T> T noticeBeanInvoke(final Object obj, String method, Object... args) {
		Object[] params = (Object[])args[2];
		boolean isTransaction = params.length > 0 
								&& params[0] instanceof SdkNoticeCommand 
								&& ((SdkNoticeCommand)params[0]).isTransaction(); 
		if(isTransaction && !TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new SdkException(BIZ_VALUE.get() + "not in transaction").setCode(SdkException.NOT_IN_TRANSACTION);
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
							log.info("手工提交 : {}, {}", ret.getId(), BIZ_VALUE.get());
						}else if(TransactionSynchronization.STATUS_ROLLED_BACK == status) {
							client.transactionRollback(ret, null);
							log.info ("手工回滚 : {}, {}", ret.getId(), BIZ_VALUE.get());
						}else {
							log.error("TransactionSynchronization afterCompletion is unknow : {}", BIZ_VALUE.get());
						}
					} catch(Throwable e) {
						log.error("手工事务异常 : " + BIZ_VALUE.get() + e.getMessage(), e);
					}
					super.afterCompletion(status);
				}
			});
			// 保存到DB，此处的保存和业务方法是在同一事务中，所以只要成功，后续兜底回查就可以保证异步调用为可靠的
			saveTransactionEvent(ret);
		}
		return t; 
	}
	
	@Override
	public void noticeExecuteProxy(Object obj, String method, Object... args) throws Throwable {
		String id = SdkContextHolder.getSdkNoticeContext().getId();
		try {
			if(cache.contans(id)) {
				log.info("id exist : {}", id);
				return;
			}
			log.info("cache not get id : {}, cacheClass : {}", id, cache);
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		try {
		    super.noticeExecuteProxy(obj, method, args);
		}catch (Throwable e) {
		    log.error("busi error" + e.getMessage());
		    throw e;
		}
		Date exprie = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
		try {
			cache.add(id, exprie);
			log.info("cache set id : {}, cacheClass : {}-{}", id, cache, BIZ_VALUE.get());
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
	private void saveTransactionEvent(SdkNoticeResult ret){
		Date now = new Date();
	    log.info("insert into message_transaction_event(id,create_time) values(?,?),{}, {}, {}", ret.getId(), TimeUtils.yyyyMMddHHmmssSSS(now.getTime()), BIZ_VALUE.get());
	    jdbcTemplate.update("insert into message_transaction_event(id,create_time) values(?,?)", ret.getId(), now);
	}

	//一般在 spring 的事务结束的回调里面手工提交即可，这里只是用来做手工提交失败（网络异常，进程被kill，硬件出现问题）后的兜底保证
	@Override
	public SdkTransactionState check(SdkNoticeResult ret) {
		String id = ret.getId();
		// 根据 id 去兜底查询，看最终是要提交还是未知，业务一般很难确定回滚状态，所以一般只需返回 Commit 和 UNKNOW
		try {
			if(checkTransaction(ret)) {
				log.info("回查兜底 commit : {}", id);
				return SdkTransactionState.COMMIT;
			}
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		log.info("回查兜底 unknow : {}", id);
		return SdkTransactionState.UNKNOW;
	}

	private boolean checkTransaction(SdkNoticeResult ret) {
		String id = null;
		try {
			id = jdbcTemplate.queryForObject("select id from message_transaction_event where id = ?", new Object[] {ret.getId()}, String.class);
		}catch (EmptyResultDataAccessException e) {
			// 吃掉异常
		    log.info("select id from message_transaction_event where id = {}", ret.getId());
		}catch (Throwable e) {
		    log.error(e.getMessage(), e);
		}
		
		return !Strings.isNullOrEmpty(id);
	}
	
}