package com.vtradex.ehub.sdk.impl;


import java.io.File;
import java.util.Map;

import org.apache.rocketmq.client.log.ClientLogger;

import com.chopsticks.core.modern.caller.ExtBean;
import com.chopsticks.core.modern.caller.NoticeBean;
import com.chopsticks.core.rocketmq.modern.DefaultModernClient;
import com.chopsticks.core.rocketmq.modern.caller.BaseProxy;
import com.google.common.base.Strings;
import com.vtradex.ehub.sdk.SdkClient;
import com.vtradex.ehub.sdk.SdkClientProxy;
import com.vtradex.ehub.sdk.caller.SdkBeanProxy;
import com.vtradex.ehub.sdk.caller.SdkExtBean;
import com.vtradex.ehub.sdk.caller.SdkExtBeanProxy;
import com.vtradex.ehub.sdk.caller.SdkNoticeBean;
import com.vtradex.ehub.sdk.caller.SdkNoticeBeanProxy;
import com.vtradex.ehub.sdk.exception.SdkException;
import com.vtradex.ehub.sdk.http.SdkHttpClient;

/**
 * 默认客户端实现
 * @author zilong.li
 *
 */
public class DefaultSdkClient implements SdkClient{
	
	private String orgKey;
	private String uniKey;
	private volatile boolean started;
	
	private DefaultModernClient innerClient;
	
	public static final String ORG_KEY = "_ORG_KEY_";
	public static final String UNI_KEY = "_UNI_KEY_";
	
	@SuppressWarnings("unused")
	private SdkHttpClient sdkHttpClient = new SdkHttpClient();
	
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
		innerClient = buildInnerClient(groupName);
		this.setNoticeExecutable(true);
		this.setDelayNoticeExecutable(true);
		this.setInvokeExecutable(false);
		this.setOrderedNoticeExecutable(false);
		this.setInvokable(false);
	}
	
	private DefaultModernClient buildInnerClient(String groupName) {
		DefaultModernClient innerClient = new DefaultModernClient(groupName) {
			@Override
			protected BaseProxy getBeanProxy(Class<?> clazz, DefaultModernClient client) {
				BaseProxy proxy = new SdkBeanProxy(clazz, client);
				Map<String, String> extParams = proxy.getExtParams();
				extParams.put(ORG_KEY, getOrgKey());
				extParams.put(UNI_KEY, getUniKey());
				return proxy;
			}
			@Override
			protected Class<? extends NoticeBean> getNoticeBeanClazz() {
				return SdkNoticeBean.class;
			}
			@Override
			protected BaseProxy getNoticeBeanProxy(Class<?> clazz, DefaultModernClient client) {
				BaseProxy proxy = new SdkNoticeBeanProxy(clazz, client);
				Map<String, String> extParams = proxy.getExtParams();
				extParams.put(ORG_KEY, getOrgKey());
				extParams.put(UNI_KEY, getUniKey());
				return proxy;
			}
			@Override
			protected Class<? extends ExtBean> getExtBeanClazz() {
				return SdkExtBean.class;
			}
			@Override
			protected BaseProxy getExtBeanProxy(String clazzName, DefaultModernClient client) {
				BaseProxy proxy = new SdkExtBeanProxy(clazzName, client);
				Map<String, String> extParams = proxy.getExtParams();
				extParams.put(ORG_KEY, getOrgKey());
				extParams.put(UNI_KEY, getUniKey());
				return proxy;
			}
		};
		return innerClient;
	}
	/**
	 * 废弃，版本变更会直接删除
	 * {@link #getSdkBean(Class)}
	 * @param clazz 接口
	 * @param <T> 接口
	 * @return 代理实现类
	 */
	@Deprecated
	public <T> T getBean(Class<T> clazz) {
		return getSdkBean(clazz);
	}
	@Override
	public <T> T getSdkBean(Class<T> clazz) {
		return innerClient.getBean(clazz);
	}
	/**
	 * 废弃，版本变更会直接删除
	 * {@link #getSdkNoticeBean(Class)}
	 * @param clazz 接口
	 * @param <T> {@link NoticeBean} 接口
	 * @return 代理实现类
	 */
	@Deprecated
	public <T extends NoticeBean> T getNoticeBean(Class<?> clazz) {
		return getSdkNoticeBean(clazz);
	}
	@Override
	public <T extends SdkNoticeBean> T getSdkNoticeBean(Class<?> clazz) {
		return innerClient.getNoticeBean(clazz);
	}
	/**
	 * 废弃，版本变更会直接删除
	 * {@link #getSdkExtBean(Class)}
	 * @param clazz 接口
	 * @param <T> {@link ExtBean} 接口
	 * @return 代理实现类
	 */
	@Deprecated
	public <T extends ExtBean> T getExtBean(Class<?> clazz) {
		return getSdkExtBean(clazz);
	}
	@Override
	public <T extends SdkExtBean> T getSdkExtBean(Class<?> clazz) {
		return innerClient.getExtBean(clazz.getName());
	}
	/**
	 * 废弃，版本变更会直接删除
	 * {@link #getSdkExtBean(String)}
	 * @param clazzName 接口全类名
	 * @param <T> {@link ExtBean} 接口
	 * @return 代理实现类
	 */
	@Deprecated
	public <T extends ExtBean> T getExtBean(String clazzName) {
		return getSdkExtBean(clazzName);
	}
	@Override
	public <T extends SdkExtBean> T getSdkExtBean(String clazzName) {
		return innerClient.getExtBean(clazzName);
	}
	/**
	 * 废弃，版本变更会直接删除
	 * {@link #setServices(Map)}
	 * @param services services 服务，key 为 接口类定义，value 为接口实现类实例
	 */
	@Deprecated
	public void register(Map<Class<?>, Object> services) {
		setServices(services);
	}
	@Override
	public void setServices(Map<Class<?>, Object> services){
		innerClient.register(services);
	}
	@Override
	public synchronized void start() {
		innerClient.start();
		started = true;
	}
	@Override
	public synchronized void shutdown() {
		innerClient.shutdown();
		started = false;
	}
	/**
	 * 获取客户端启动状态
	 * @return 是否启动
	 */
	public boolean isStarted() {
		return started;
	}
	/**
	 * 设置是否支持异步执行，默认 true
	 * @param noticeExecutable 是否支持
	 */
	public void setNoticeExecutable(boolean noticeExecutable) {
		innerClient.setNoticeExecutable(noticeExecutable);
	}
	/**
	 * 设置延迟执行的线程数，默认 10
	 * @param noticeExecutableNum 执行线程数
	 */
	public void setNoticeExecutableNum(int noticeExecutableNum) {
		innerClient.setNoticeExecutableNum(noticeExecutableNum);
	}
	/**
	 * 设置是否支持延迟异步执行，默认 true
	 * @param delayNoticeExecutable 是否支持
	 */
	public void setDelayNoticeExecutable(boolean delayNoticeExecutable) {
		innerClient.setDelayNoticeExecutable(delayNoticeExecutable);
	}
	/**
	 * 设置延迟异步执行的线程数，默认 10
	 * @param delayNoticeExecutableNum 执行线程数
	 */
	public void setDelayNoticeExecutableNum(int delayNoticeExecutableNum) {
		innerClient.setDelayNoticeExecutableNum(delayNoticeExecutableNum);
	}
	/**
	 * 设置是否支持同步执行，默认 false
	 * @param invokeExecutable 是否支持
	 */
	public void setInvokeExecutable(boolean invokeExecutable) {
		innerClient.setInvokeExecutable(invokeExecutable);
	}
	/**
	 * 设置同步的执行线程数量，默认为 15 个
	 * @param invokeExecutableNum 执行线程数
	 */
	public void setInvokeExecutableNum(int invokeExecutableNum) {
		innerClient.setInvokeExecutableNum(invokeExecutableNum);
	}
	/**
	 * 设置是否支持顺序异步执行，默认 false
	 * @param orderedNoticeExecutable 是否支持
	 */
	public void setOrderedNoticeExecutable(boolean orderedNoticeExecutable) {
		innerClient.setOrderedNoticeExecutable(orderedNoticeExecutable);
	}
	/**
	 * 设置顺序异步的执行线程数量，默认为 5 个
	 * @param orderedNoticeExecutableNum 执行线程数
	 */
	public void setOrderedNoticeExecutableNum(int orderedNoticeExecutableNum) {
		innerClient.setOrderedNoticeExecutableNum(orderedNoticeExecutableNum);
	}
	/**
	 * 设置是否支持同步调用，默认 false
	 * @param invokable 是否支持
	 */
	public void setInvokable(boolean invokable) {
		innerClient.setInvokable(invokable);
	}
	/**
	 * 指定默认调用身份
	 * @param orgKey 身份
	 */
	public void setOrgKey(String orgKey) {
		this.orgKey = orgKey;
	}
	public String getOrgKey() {
		return orgKey;
	}
	/**
	 * 指定默认调用身份
	 * @param uniKey 身份
	 */
	public void setUniKey(String uniKey) {
		this.uniKey = uniKey;
	}
	public String getUniKey() {
		return uniKey;
	}
	/**
	 * 设置服务器地址，格式为 ip:port
	 * @param serverPath 服务器地址
	 */
	public void setServerPath(String serverPath) {
		if(!Strings.isNullOrEmpty(serverPath)) {
			serverPath = serverPath.replace("http://", "").replace("https://", "");
			if(!serverPath.contains(":")) {
				throw new SdkException("server path must set port").setCode(SdkException.SERVER_PATH_NOT_PORT);
			}else {
				
				System.setProperty("rocketmq.namesrv.domain", serverPath);
			}
		}
	}
	/**
	 * 设置异步执行失败重试次数, 默认都是 Integer.MAX_VALUE
	 * {@link #setNoticeExcecutableRetryCount(int)}
	 * {@link #setDelayNoticeExecutableRetryCount(int)}
	 * {@link #setOrderedNoticeExecutableRetryCount(int)}
	 * @param retryCount 重试次数
	 */
	public void setRetryCount(int retryCount) {
		innerClient.setRetryCount(retryCount);
	}
	/**
	 * 单独设置无序异步重试次数
	 * @param noticeExcecutableRetryCount 重试次数
	 */
	public void setNoticeExcecutableRetryCount(int noticeExcecutableRetryCount) {
		innerClient.setNoticeExcecutableRetryCount(noticeExcecutableRetryCount);
	}
	/**
	 * 单独设置延迟无序异步执行失败重试次数
	 * @param delayNoticeExecutableRetryCount 重试次数
	 */
	public void setDelayNoticeExecutableRetryCount(int delayNoticeExecutableRetryCount) {
		innerClient.setDelayNoticeExecutableRetryCount(delayNoticeExecutableRetryCount);
	}
	/**
	 * 单独设置顺序异步执行失败重试次数
	 * @param orderedNoticeExecutableRetryCount 重试次数
	 */
	public void setOrderedNoticeExecutableRetryCount(int orderedNoticeExecutableRetryCount) {
		innerClient.setOrderedNoticeExecutableRetryCount(orderedNoticeExecutableRetryCount);
	}
	/**
	 * 设置执行时间，超过则失败，默认 15 分钟
	 * @param maxExecutableTime 最大执行时间
	 */
	public void setMaxExecutableTime(long maxExecutableTime) {
		innerClient.setMaxExecutableTime(maxExecutableTime);
	}
	/**
	 * 设置无序异步最早开始处理的毫秒时间，小于此毫秒数的直接结束，不经过业务处理
	 * @param noticeBeginExecutableTime 开始执行时间
	 */
	public void setNoticeBeginExecutableTime(long noticeBeginExecutableTime) {
		innerClient.setNoticeBeginExecutableTime(noticeBeginExecutableTime);
	}
	/**
	 * 设置延迟无序异步最早开始处理的毫秒时间，小于此毫秒数的直接结束，不经过业务处理
	 * @param delayNoticeBeginExecutableTime 开始执行时间
	 */
	public void setDelayNoticeBeginExecutableTime(long delayNoticeBeginExecutableTime) {
		innerClient.setDelayNoticeBeginExecutableTime(delayNoticeBeginExecutableTime);
	}
	/**
	 * 设置顺序异步最早开始处理的毫秒时间，小于此毫秒数的直接结束，不经过业务处理
	 * @param orderedNoticeBeginExecutableTime 开始执行时间
	 */
	public void setOrderedNoticeBeginExecutableTime(long orderedNoticeBeginExecutableTime) {
		innerClient.setOrderedNoticeBeginExecutableTime(orderedNoticeBeginExecutableTime);
	}
	
	public void setClientProxy(SdkClientProxy clientProxy) {
		innerClient.setModernClientProxy(clientProxy);
	}
}
