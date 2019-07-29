package com.vtradex.ehub.sdk;

import com.chopsticks.core.rocketmq.modern.ModernClientProxy;

/**
 * 客户端代理类，可以拦截所有调用和执行
 * @author lzl
 *
 */
public class SdkClientProxy extends ModernClientProxy {
	@Override
	public void beforeBeanInvoke(Object obj, String method, Object... args) {
		super.beforeBeanInvoke(obj, method, args);
	}

	@Override
	public void beforeExtBeanInvoke(Object obj, String method, Object... args) {
		super.beforeExtBeanInvoke(obj, method, args);
	}

	@Override
	public void beforeNoticeBeanInvoke(Object obj, String method, Object... args) {
		super.beforeNoticeBeanInvoke(obj, method, args);
	}

	@Override
	public <T> T invokeExecuteProxy(Object obj, String method, Object... args) throws Throwable {
		return super.invokeExecuteProxy(obj, method, args);
	}

	@Override
	public void noticeExecuteProxy(Object obj, String method, Object... args) throws Throwable {
		super.noticeExecuteProxy(obj, method, args);
	}

}
