package com.vtradex.ehub.sdk.factory;

import com.chopsticks.core.modern.caller.NoticeBean;
import com.vtradex.ehub.sdk.SdkClient;

public class SdkBeanFactory {

	public static NoticeBean getNoticeBean(SdkClient client, Class<?> clazz) {
		return client.getNoticeBean(clazz);
	}
}
