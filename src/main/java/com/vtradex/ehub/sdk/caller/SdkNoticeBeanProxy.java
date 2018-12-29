package com.vtradex.ehub.sdk.caller;


import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chopsticks.core.rocketmq.DefaultClient;
import com.chopsticks.core.rocketmq.modern.caller.NoticeBeanProxy;

public class SdkNoticeBeanProxy extends NoticeBeanProxy {
	
	private static final Logger log = LoggerFactory.getLogger(SdkNoticeBeanProxy.class);
	
	private static final long MIN_DELAY_MLLLIS = TimeUnit.SECONDS.toMillis(10L);

	public SdkNoticeBeanProxy(Class<?> clazz, DefaultClient client) {
		super(clazz, client);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(args.length == 3) {
			Long delay = (Long)args[1];
			TimeUnit delayTimeUnit = (TimeUnit)args[2];
			long curDelayMillis = delayTimeUnit.toMillis(delay);
			if(curDelayMillis < MIN_DELAY_MLLLIS) {
//				log.warn("delay must be >= 10s, curDelayMillis(ms) : {}, method : {}, interface : {}", curDelayMillis, method.getName(), getClazz());
//				args[1] = MIN_DELAY_MLLLIS;
//				args[2] = TimeUnit.MILLISECONDS;
			}
		}
		return super.invoke(proxy, method, args);
	}
}
