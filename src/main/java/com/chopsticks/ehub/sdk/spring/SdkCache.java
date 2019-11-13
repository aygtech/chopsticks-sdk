package com.chopsticks.ehub.sdk.spring;

import java.util.Date;

public interface SdkCache {

	public boolean contans(String key);
	
	public void add(String key, Date exprie);
	
}
