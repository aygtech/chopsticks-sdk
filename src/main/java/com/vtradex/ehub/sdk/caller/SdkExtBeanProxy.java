package com.vtradex.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.DefaultClient;
import com.chopsticks.core.rocketmq.modern.caller.ExtBeanProxy;

public class SdkExtBeanProxy extends ExtBeanProxy{

	public SdkExtBeanProxy(String clazzName, DefaultClient client) {
		super(clazzName, client);
	}

}
