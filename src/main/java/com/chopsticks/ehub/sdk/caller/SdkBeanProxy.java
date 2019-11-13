package com.chopsticks.ehub.sdk.caller;

import java.lang.reflect.Method;

import com.chopsticks.core.rocketmq.modern.DefaultModernClient;
import com.chopsticks.core.rocketmq.modern.caller.BeanProxy;
import com.chopsticks.ehub.sdk.SdkClientProxy;

public class SdkBeanProxy extends BeanProxy{

	public SdkBeanProxy(Class<?> clazz, DefaultModernClient client) {
		super(clazz, client);
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(client.getModernClientProxy() instanceof SdkClientProxy) {
			return ((SdkClientProxy)client.getModernClientProxy()).beanInvoke(this, "sdkInnerInvoke", proxy, method, args);
		}else {
			return innerInvoke(proxy, method, args);
		}
	}
	
	public Object sdkInnerInvoke(Object proxy, Method method, Object[] args) throws Throwable {
		return super.invoke(proxy, method, args);
	}
}
