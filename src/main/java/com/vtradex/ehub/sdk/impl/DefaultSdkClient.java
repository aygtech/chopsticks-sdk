package com.vtradex.ehub.sdk.impl;


import java.util.Map;

import com.chopsticks.core.modern.caller.ExtBean;
import com.chopsticks.core.modern.caller.NoticeBean;
import com.chopsticks.core.rocketmq.DefaultClient;
import com.chopsticks.core.rocketmq.modern.DefaultModernClient;
import com.chopsticks.core.rocketmq.modern.caller.BaseProxy;
import com.vtradex.ehub.sdk.SdkClient;
import com.vtradex.ehub.sdk.caller.SdkExtBean;
import com.vtradex.ehub.sdk.caller.SdkNoticeBean;
import com.vtradex.ehub.sdk.caller.SdkNoticeBeanProxy;

public class DefaultSdkClient extends DefaultModernClient implements SdkClient{
	
	static {
		System.setProperty("rocketmq.namesrv.domain", "ehub.server.com:18080");
	}

	public DefaultSdkClient(String groupName) {
		super(groupName);
	}
	
	@Override
	protected BaseProxy getNoticeBeanProxy(Class<?> clazz, DefaultClient client) {
		return new SdkNoticeBeanProxy(clazz, client);
	}
	
	@Override
	protected Class<? extends NoticeBean> getNoticeBeanClazz() {
		return SdkNoticeBean.class;
	}
	
	@Override
	protected Class<? extends ExtBean> getExtBeanClazz() {
		return SdkExtBean.class;
	}
	
	public void setServices(Map<Class<?>, Object> services){
		super.register(services);
	}
}
