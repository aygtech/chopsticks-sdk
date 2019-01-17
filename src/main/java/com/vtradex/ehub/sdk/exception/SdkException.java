package com.vtradex.ehub.sdk.exception;

import com.chopsticks.core.rocketmq.modern.exception.ModernCoreException;

public class SdkException extends ModernCoreException{

	private static final long serialVersionUID = 1L;
	
	public static final int SERVER_PATH_NOT_PORT = 300000;
	
	public SdkException(String errorMsg) {
		super(errorMsg);
	}

}
