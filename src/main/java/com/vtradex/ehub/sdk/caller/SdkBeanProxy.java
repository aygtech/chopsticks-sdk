package com.vtradex.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.DefaultClient;
import com.chopsticks.core.rocketmq.modern.caller.BeanProxy;

public class SdkBeanProxy extends BeanProxy{

	public SdkBeanProxy(Class<?> clazz, DefaultClient client) {
		super(clazz, client);
	}

}
