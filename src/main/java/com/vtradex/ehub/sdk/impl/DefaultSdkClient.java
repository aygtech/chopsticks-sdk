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
 * 默认客户端实现
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
	
	/**
	 * 默认只开启了无序异步和延迟异步支持
	 * 
	 * {@link #setOrderedNoticeExecutable(boolean)}
	 * {@link #setNoticeExecutable(boolean)}
	 * {@link #setInvokeExecutable(boolean)}
	 * {@link #setDelayNoticeExecutable(boolean)}
	 * 
	 * @param groupName 集群名
	 */
	public DefaultSdkClient(String groupName) {
		super(groupName);
		setNoticeExecutable(true);
		setDelayNoticeExecutable(true);
		setInvokeExecutable(false);
		setOrderedNoticeExecutable(false);
		setInvokable(false);
	}
	
	/**
	 * 获取异步调用bean
	 * @param clazz 类型，支持方法，参数校验
	 * @return 异步调用bean
	 */
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
	
	/**
	 * 获取扩展bean，可实现无桩化调用，不支持方法，参数校验
	 * {@link #getSdkExtBean(String)}
	 * @param clazz 接口类
	 * @return 扩展调用bean
	 */
	public SdkExtBean getSdkExtBean(Class<?> clazz) {
		return getExtBean(clazz.getName());
	}
	/**
	 * 获取扩展bean，可实现无桩化调用，不支持方法，参数校验
	 * @param clazzName 接口类全名
	 * @return 扩展调用bean
	 */
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
	
	/**
	 * 设置无序异步的执行线程数量，默认为 10 个
	 */
	@Override
	public void setNoticeExecutableNum(int noticeExecutableNum) {
		super.setNoticeExecutableNum(noticeExecutableNum);
	}
	/**
	 * 设置是否支持无序异步执行，默认 true
	 */
	@Override
	public void setNoticeExecutable(boolean noticeExecutable) {
		super.setNoticeExecutable(noticeExecutable);
	}
	/**
	 * 设置无序延迟异步的执行线程数量，默认为 10 个
	 */
	@Override
	public void setDelayNoticeExecutableNum(int delayNoticeExecutableNum) {
		super.setDelayNoticeExecutableNum(delayNoticeExecutableNum);
	}
	/**
	 * 设置是否支持无序延迟异步执行，默认 true
	 */
	@Override
	public void setDelayNoticeExecutable(boolean delayNoticeExecutable) {
		super.setDelayNoticeExecutable(delayNoticeExecutable);
	}
	/**
	 * 设置是否支持同步调用，默认 false
	 */
	@Override
	public void setInvokable(boolean invokable) {
		super.setInvokable(invokable);
	}
	/**
	 * 设置是否支持同步执行，默认 false
	 */
	@Override
	public void setInvokeExecutable(boolean invokeExecutable) {
		super.setInvokeExecutable(invokeExecutable);
	}
	/**
	 * 设置同步的执行线程数量，默认为 15 个
	 */
	@Override
	public void setInvokeExecutableNum(int invokeExecutableNum) {
		super.setInvokeExecutableNum(invokeExecutableNum);
	}
	@Override
	public void setOrderedNoticeExecutable(boolean orderedNoticeExecutable) {
		super.setOrderedNoticeExecutable(orderedNoticeExecutable);
	}
	/**
	 * 设置顺序异步的执行线程数量，默认为 5 个
	 */
	@Override
	public void setOrderedNoticeExecutableNum(int orderedNoticeExecutableNum) {
		super.setOrderedNoticeExecutableNum(orderedNoticeExecutableNum);
	}
	public String getOrgKey() {
		return orgKey;
	}
	/**
	 * 指定默认调用身份
	 * @param orgKey 身份
	 */
	public void setOrgKey(String orgKey) {
		this.orgKey = orgKey;
	}
	public String getUniKey() {
		return uniKey;
	}
	/**
	 * 指定默认调用身份
	 * @param uniKey 身份
	 */
	public void setUniKey(String uniKey) {
		this.uniKey = uniKey;
	}
	/**
	 * 设置服务器地址，格式为 ip:port
	 * @param serverPath 服务器地址
	 */
	public void setServerPath(String serverPath) {
		if(!Strings.isNullOrEmpty(serverPath)) {
			if(!serverPath.contains(":")) {
				throw new SdkException("server path must set port").setCode(SdkException.SERVER_PATH_NOT_PORT);
			}else {
				System.setProperty("rocketmq.namesrv.domain", serverPath);
			}
		}
	}
	
	/**
	 * 设置重试次数, 同时调用三种异步的重试次数
	 * {@link #setNoticeExcecutableRetryCount(int)}
	 * {@link #setDelayNoticeExecutableRetryCount(int)}
	 * {@link #setOrderedNoticeExecutableRetryCount(int)}
	 */
	@Override
	public void setRetryCount(int retryCount) {
		super.setRetryCount(retryCount);
	}
	/**
	 * 设置顺序异步的重试次数，默认为无限次
	 */
	@Override
	public void setOrderedNoticeExecutableRetryCount(int orderedNoticeExecutableRetryCount) {
		super.setOrderedNoticeExecutableRetryCount(orderedNoticeExecutableRetryCount);
	}
	/**
	 * 设置无序异步的重试次数，默认为无限次
	 */
	@Override
	public void setNoticeExcecutableRetryCount(int noticeExcecutableRetryCount) {
		super.setNoticeExcecutableRetryCount(noticeExcecutableRetryCount);
	}
	/**
	 * 设置无序延迟异步的重试次数，默认为无限次
	 */
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
	public synchronized void shutdown() {
		super.shutdown();
		started = false;
	}
	
	/**
	 * 设定最大执行时间，超过此时间服务器默认认为失败触发重试
	 */
	@Override
	public void setMaxExecutableTime(long maxExecutableTime) {
		super.setMaxExecutableTime(maxExecutableTime);
	}
	/**
	 * 指定无序异步开始处理的时间，参数为毫秒
	 */
	@Override
	public void setNoticeBeginExecutableTime(long noticeBeginExecutableTime) {
		super.setNoticeBeginExecutableTime(noticeBeginExecutableTime);
	}
	/**
	 * 指定延迟异步开始处理的时间，参数为毫秒
	 */
	@Override
	public void setDelayNoticeBeginExecutableTime(long delayNoticeBeginExecutableTime) {
		super.setDelayNoticeBeginExecutableTime(delayNoticeBeginExecutableTime);
	}
	/**
	 * 指定顺序异步开始处理的时间，参数为毫秒
	 */
	@Override
	public void setOrderedNoticeBeginExecutableTime(long orderedNoticeBeginExecutableTime) {
		super.setOrderedNoticeBeginExecutableTime(orderedNoticeBeginExecutableTime);
	}
	
}
