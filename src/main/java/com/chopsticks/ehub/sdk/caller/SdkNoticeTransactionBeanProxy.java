package com.chopsticks.ehub.sdk.caller;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SdkNoticeTransactionBeanProxy implements InvocationHandler{
	
	private SdkNoticeBean noticeBean;
	
	public SdkNoticeTransactionBeanProxy(SdkNoticeBean noticeBean) {
		this.noticeBean = noticeBean;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		noticeBean.sdkNotice(new SdkNoticeCommand(method.getName(), args).transaction());
		return null;
	}
}
