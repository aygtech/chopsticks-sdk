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

/**
 * 默认客户端
 * @author zilong.li
 *
 */
public class DefaultSdkClient extends DefaultModernClient implements SdkClient{
	
	static {
		System.setProperty("rocketmq.namesrv.domain", "ehub.server.com:18080");
	}
	
	public DefaultSdkClient(String groupName) {
		super(groupName);
		setNoticeExecutable(true);
		setDelayNoticeExecutable(true);
		setInvokeExecutable(false);
		setOrderedNoticeExecutable(false);
		setInvokable(false);
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
	
	@Override
	public void setNoticeExecutableNum(int noticeExecutableNum) {
		super.setNoticeExecutableNum(noticeExecutableNum);
	}
	@Override
	public void setNoticeExecutable(boolean noticeExecutable) {
		super.setNoticeExecutable(noticeExecutable);
	}
	@Override
	public void setDelayNoticeExecutableNum(int delayNoticeExecutableNum) {
		super.setDelayNoticeExecutableNum(delayNoticeExecutableNum);
	}
	@Override
	public void setDelayNoticeExecutable(boolean delayNoticeExecutable) {
		super.setDelayNoticeExecutable(delayNoticeExecutable);
	}
	@Override
	public void setInvokable(boolean invokable) {
		super.setInvokable(invokable);
	}
	@Override
	public void setInvokeExecutable(boolean invokeExecutable) {
		super.setInvokeExecutable(invokeExecutable);
	}
	@Override
	public void setInvokeExecutableNum(int invokeExecutableNum) {
		super.setInvokeExecutableNum(invokeExecutableNum);
	}
	@Override
	public void setOrderedNoticeExecutable(boolean orderedNoticeExecutable) {
		super.setOrderedNoticeExecutable(orderedNoticeExecutable);
	}
	@Override
	public void setOrderedNoticeExecutableNum(int orderedNoticeExecutableNum) {
		super.setOrderedNoticeExecutableNum(orderedNoticeExecutableNum);
	}
}
