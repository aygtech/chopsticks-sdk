package com.vtradex.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.modern.DefaultModernClient;
import com.chopsticks.core.rocketmq.modern.caller.BeanProxy;

public class SdkBeanProxy extends BeanProxy{

	public SdkBeanProxy(Class<?> clazz, DefaultModernClient client) {
		super(clazz, client);
	}

}
