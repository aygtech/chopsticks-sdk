package com.vtradex.ehub.sdk.handler;

import com.chopsticks.core.rocketmq.modern.handler.ModernContextHolder;
import com.vtradex.ehub.sdk.impl.DefaultSdkClient;

public class SdkContextHolder extends ModernContextHolder {
	private SdkContextHolder() {}
	
	public static String getOrgKey(){
		return getExtParams().get(DefaultSdkClient.ORG_KEY);
	}
	public static String getUnikey() {
		return getExtParams().get(DefaultSdkClient.UNI_KEY);
	}
}
