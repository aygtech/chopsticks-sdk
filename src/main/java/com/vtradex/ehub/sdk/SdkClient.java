package com.vtradex.ehub.sdk;


import java.util.Map;

import com.vtradex.ehub.sdk.caller.SdkExtBean;
import com.vtradex.ehub.sdk.caller.SdkNoticeBean;

/**
 * 客户端
 * @author zilong.li
 *
 */
public interface SdkClient{
	/**
	 * 获取同步调用 bean
	 * @param clazz 接口
	 * @param <T> 接口
	 * @return 代理实现类
	 */
	public <T> T getSdkBean(Class<T> clazz);
	/**
	 * 获取异步调用 bean
	 * @param clazz 接口
	 * @param <T> {@link SdkNoticeBean} 接口
	 * @return 代理实现类
	 */
	public <T extends SdkNoticeBean> T getSdkNoticeBean(Class<?> clazz);
	/**
	 * 获取超级调用 bean
	 * @param clazz 接口
	 * @param <T> {@link SdkExtBean} 接口
	 * @return 代理实现类
	 */
	public <T extends SdkExtBean> T getSdkExtBean(Class<?> clazz);
	/**
	 * 获取超级调用 bean
	 * @param clazzName 接口全类名
	 * @param <T> {@link SdkExtBean} 接口
	 * @return 代理实现类
	 */
	public <T extends SdkExtBean> T getSdkExtBean(String clazzName);
	/**
	 * 注册服务
	 * @param services 服务，key 为 接口类定义，value 为接口实现类实例
	 */
	public void setServices(Map<Class<?>, Object> services);
	/**
	 * 启动客户端
	 */
	public void start();
	/**
	 * 停止客户端
	 */
	public void shutdown();
}
