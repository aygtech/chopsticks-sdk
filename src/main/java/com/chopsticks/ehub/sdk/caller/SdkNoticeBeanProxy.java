package com.chopsticks.ehub.sdk.caller;


import java.lang.reflect.Method;

import com.chopsticks.core.rocketmq.caller.BaseNoticeResult;
import com.chopsticks.core.rocketmq.modern.DefaultModernClient;
import com.chopsticks.core.rocketmq.modern.caller.NoticeBeanProxy;
import com.chopsticks.ehub.sdk.SdkClientProxy;

public class SdkNoticeBeanProxy extends NoticeBeanProxy {

	public SdkNoticeBeanProxy(Class<?> clazz, DefaultModernClient client) {
		super(clazz, client);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(client.getModernClientProxy() instanceof SdkClientProxy) {
			return ((SdkClientProxy)client.getModernClientProxy()).noticeBeanInvoke(this, "sdkInnerInvoke", proxy, method, args);
		}else {
			return innerInvoke(proxy, method, args);
		}
	}
	
	public Object sdkInnerInvoke(Object proxy, Method method, Object[] args) throws Throwable{
		Object ret = super.invoke(proxy, method, args);
		if(method.getName().startsWith("sdk")) {
			ret = new SdkNoticeResult((BaseNoticeResult)ret);
		}
		return ret;
	}
	
}
