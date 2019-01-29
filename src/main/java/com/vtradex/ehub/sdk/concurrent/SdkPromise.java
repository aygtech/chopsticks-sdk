package com.vtradex.ehub.sdk.concurrent;

import com.chopsticks.core.concurrent.impl.GuavaPromise;

public class SdkPromise<V> extends GuavaPromise<V> {
	
	public void addListener(SdkListener<V> listener) {
		super.addListener(listener);
	}
	
}
