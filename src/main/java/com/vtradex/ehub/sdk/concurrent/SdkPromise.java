package com.vtradex.ehub.sdk.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.chopsticks.common.concurrent.PromiseListener;
import com.chopsticks.common.concurrent.impl.DefaultTimeoutPromise;

/**
 * 通用异步保证，可自定义执行线程，支持自动超时通知
 * @author zilong.li
 *
 * @param <V> 异步结果类型
 */
public class SdkPromise<V> extends DefaultTimeoutPromise<V> {
	
	public SdkPromise() {
		super();
	}
	
	/**
	 * 设置默认超时通知
	 * @param timeout 超时时间
	 * @param timeoutUnit 超时单位
	 */
	public SdkPromise(long timeout, TimeUnit timeoutUnit) {
		super(timeout, timeoutUnit);
	}
	
	@Deprecated
	@Override
	public void addListener(PromiseListener<? super V> listener) {
		super.addListener(listener);
	}
	
	@Deprecated
	@Override
	public void addListener(Runnable listener, Executor executor) {
		super.addListener(listener, executor);
	}
	
	/**
	 * 添加完成监听，默认执行监听线程池为 Executors.newCachedThreadPool()
	 * @param listener 监听
	 */
	public void addListener(SdkListener<? super V> listener) {
		super.addListener(listener);
	}
	
	/**
	 * 添加完成监听，可指定监听执行线程池
	 * @param listener 监听
	 * @param executor 执行线程池
	 */
	public void addListener(SdkListener<? super V> listener, Executor executor) {
		super.addListener(listener, executor);
	}
}
