package com.chopsticks.ehub.sdk.spring;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class LocalCache implements SdkCache {
	
	private Cache<String, Date> cache = CacheBuilder.newBuilder()
													.expireAfterWrite(5, TimeUnit.MINUTES)
													.removalListener(new RemovalListener<String, Date>() {
														@Override
														public void onRemoval(
																RemovalNotification<String, Date> notification) {
															if(new Date().before(notification.getValue())) {
																cache.put(notification.getKey(), notification.getValue());
															}
														}
													})
													.build();
																			;

	@Override
	public boolean contans(String key) {
		return cache.getIfPresent(key) != null;
	}

	@Override
	public void add(String key, Date exprie) {
		cache.put(key, exprie);
	}

}
