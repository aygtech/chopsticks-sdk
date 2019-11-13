package com.chopsticks.ehub.sdk;

import java.lang.reflect.InvocationTargetException;

import com.chopsticks.common.utils.Reflect;
import com.chopsticks.common.utils.Reflect.ReflectException;
import com.chopsticks.core.exception.CoreException;
import com.chopsticks.core.rocketmq.modern.ModernClientProxy;
import com.chopsticks.ehub.sdk.exception.SdkException;

/**
 * 客户端代理类，可以拦截所有调用和执行
 * @author lzl
 *
 */
public class SdkClientProxy extends ModernClientProxy {
	public <T> T beanInvoke(Object obj, String method, Object... args) {
		return invoke(obj, method, args);
	}
	public <T> T noticeBeanInvoke(Object obj, String method, Object... args) {
		return invoke(obj, method, args);
	}
	public <T> T extBeanInvoke(Object obj, String method, Object... args) {
		return invoke(obj, method, args);
	}
	
	private <T> T invoke(Object obj, String method, Object... args) {
		try {
			return Reflect.on(obj).call(method, args).get();
		}catch (Throwable e) {
			while(e instanceof ReflectException || e instanceof InvocationTargetException) {
				e = e.getCause();
			}
			if(e instanceof CoreException) {
				throw (CoreException)e;
			}else {
				throw new SdkException(e);
			}
		}
	}
}
