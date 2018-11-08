package com.vtradex.ehub.sdk.factory;

import java.util.Map;

import com.vtradex.ehub.sdk.SdkClient;
import com.vtradex.ehub.sdk.impl.DefaultSdkClient;

public class SdkClientFactory{

	public static SdkClient getSdkClient(String groupName, Map<Class<?>, Object> beans) {
		SdkClient client = new DefaultSdkClient(groupName);
		client.register(beans);
		return client;
	}
}
