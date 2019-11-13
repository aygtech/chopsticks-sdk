package com.chopsticks.ehub.sdk.factory;

import com.chopsticks.ehub.sdk.caller.SdkExtBean;
import com.chopsticks.ehub.sdk.caller.SdkNoticeBean;
import com.chopsticks.ehub.sdk.impl.DefaultSdkClient;

/**
 * 工厂类，方便构建对象
 * @author zilong.li
 *
 */
public class SdkBeanFactory {
	
	public static SdkNoticeBean getSdkNoticeBean(DefaultSdkClient client, Class<?> clazz) {
		return client.getSdkNoticeBean(clazz);
	}
	
	public static SdkExtBean getSdkExtBean(DefaultSdkClient client, String clazzName) {
		return client.getSdkExtBean(clazzName);
	}
	
	public static SdkExtBean getSdkExtBean(DefaultSdkClient client, Class<?> clazz) {
		return client.getSdkExtBean(clazz);
	}
	
	public static <T> T getSdkBean(DefaultSdkClient client, Class<T> clazz) {
		return client.getSdkBean(clazz);
	}
	
	
}
