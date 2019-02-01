package com.vtradex.ehub.sdk.impl;


import java.io.File;
import java.util.Map;

import org.apache.rocketmq.client.log.ClientLogger;

import com.chopsticks.core.modern.caller.ExtBean;
import com.chopsticks.core.modern.caller.NoticeBean;
import com.chopsticks.core.rocketmq.DefaultClient;
import com.chopsticks.core.rocketmq.modern.DefaultModernClient;
import com.chopsticks.core.rocketmq.modern.caller.BaseProxy;
import com.google.common.base.Strings;
import com.vtradex.ehub.sdk.SdkClient;
import com.vtradex.ehub.sdk.caller.SdkBeanProxy;
import com.vtradex.ehub.sdk.caller.SdkExtBean;
import com.vtradex.ehub.sdk.caller.SdkExtBeanProxy;
import com.vtradex.ehub.sdk.caller.SdkNoticeBean;
import com.vtradex.ehub.sdk.caller.SdkNoticeBeanProxy;
import com.vtradex.ehub.sdk.exception.SdkException;

/**
 * 默认客户端
 * @author zilong.li
 *
 */
public class DefaultSdkClient extends DefaultModernClient implements SdkClient{
	
	private String orgKey;
	private String uniKey;
	private volatile boolean started;
	
	public static final String ORG_KEY = "_ORG_KEY_";
	public static final String UNI_KEY = "_UNI_KEY_";
	
	static {
		System.setProperty("rocketmq.namesrv.domain", "ehub.server.com:18080");
		System.setProperty(ClientLogger.CLIENT_LOG_ROOT, System.getProperty("user.dir") + File.separator + "logs");
		System.setProperty(ClientLogger.CLIENT_LOG_MAXINDEX, "3");
		System.setProperty(ClientLogger.CLIENT_LOG_FILESIZE, (1024 * 1024 * 50) + "");
		System.setProperty(ClientLogger.CLIENT_LOG_FILENAME, "ehub-sdk.log");
	}
	
	public <T extends ExtBean> T getExtBean(Class<?> clazz) {
		return super.getExtBean(clazz.getName());
	}
	
	public DefaultSdkClient(String groupName) {
		super(groupName);
		setNoticeExecutable(true);
		setDelayNoticeExecutable(true);
		setInvokeExecutable(false);
		setOrderedNoticeExecutable(false);
		setInvokable(false);
	}
	
	public SdkNoticeBean getSdkNoticeBean(Class<?> clazz) {
		return getNoticeBean(clazz);
	}
	
	@Override
	protected Class<? extends NoticeBean> getNoticeBeanClazz() {
		return SdkNoticeBean.class;
	}
	@Override
	protected BaseProxy getNoticeBeanProxy(Class<?> clazz, DefaultClient client) {
		BaseProxy proxy = new SdkNoticeBeanProxy(clazz, client);
		Map<String, String> extParams = proxy.getExtParams();
		extParams.put(ORG_KEY, getOrgKey());
		extParams.put(UNI_KEY, getUniKey());
		return proxy;
	}
	
	public SdkExtBean getSdkExtBean(Class<?> clazz) {
		return getExtBean(clazz);
	}
	
	public SdkExtBean getSdkExtBean(String clazzName) {
		return getExtBean(clazzName);
	}
	
	@Override
	protected Class<? extends ExtBean> getExtBeanClazz() {
		return SdkExtBean.class;
	}
	@Override
	protected BaseProxy getExtBeanProxy(String clazzName, DefaultClient client) {
		BaseProxy proxy = new SdkExtBeanProxy(clazzName, client);
		Map<String, String> extParams = proxy.getExtParams();
		extParams.put(ORG_KEY, getOrgKey());
		extParams.put(UNI_KEY, getUniKey());
		return proxy;
	}
	
	@Override
	protected BaseProxy getBeanProxy(Class<?> clazz, DefaultClient client) {
		BaseProxy proxy = new SdkBeanProxy(clazz, client);
		Map<String, String> extParams = proxy.getExtParams();
		extParams.put(ORG_KEY, getOrgKey());
		extParams.put(UNI_KEY, getUniKey());
		return proxy;
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
	public String getOrgKey() {
		return orgKey;
	}
	public void setOrgKey(String orgKey) {
		this.orgKey = orgKey;
	}
	public String getUniKey() {
		return uniKey;
	}
	public void setUniKey(String uniKey) {
		this.uniKey = uniKey;
	}
	public void setServerPath(String serverPath) {
		if(!Strings.isNullOrEmpty(serverPath)) {
			if(!serverPath.contains(":")) {
				throw new SdkException("server path must set port").setCode(SdkException.SERVER_PATH_NOT_PORT);
			}else {
				System.setProperty("rocketmq.namesrv.domain", serverPath);
			}
		}
	}
	
	@Override
	public void setRetryCount(int retryCount) {
		super.setRetryCount(retryCount);
	}
	@Override
	public void setOrderedNoticeExecutableRetryCount(int orderedNoticeExecutableRetryCount) {
		super.setOrderedNoticeExecutableRetryCount(orderedNoticeExecutableRetryCount);
	}
	@Override
	public void setNoticeExcecutableRetryCount(int noticeExcecutableRetryCount) {
		super.setNoticeExcecutableRetryCount(noticeExcecutableRetryCount);
	}
	@Override
	public void setDelayNoticeExecutableRetryCount(int delayNoticeExecutableRetryCount) {
		super.setDelayNoticeExecutableRetryCount(delayNoticeExecutableRetryCount);
	}
	@Override
	public synchronized void start() {
		super.start();
		started = true;
	}
	public boolean isStarted() {
		return started;
	}
	@Override
	public void setMaxExecutableTime(long maxExecutableTime) {
		super.setMaxExecutableTime(maxExecutableTime);
	}
	@Override
	public synchronized void shutdown() {
		super.shutdown();
		started = false;
	}

	@Override
	public void setNoticeBeginExecutableTime(long noticeBeginExecutableTime) {
		super.setNoticeBeginExecutableTime(noticeBeginExecutableTime);
	}
	@Override
	public void setDelayNoticeBeginExecutableTime(long delayNoticeBeginExecutableTime) {
		super.setDelayNoticeBeginExecutableTime(delayNoticeBeginExecutableTime);
	}
	@Override
	public void setOrderedNoticeBeginExecutableTime(long orderedNoticeBeginExecutableTime) {
		super.setOrderedNoticeBeginExecutableTime(orderedNoticeBeginExecutableTime);
	}
	
}
