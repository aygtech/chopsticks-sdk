package com.vtradex.ehub.sdk.exception;

import com.chopsticks.core.rocketmq.modern.exception.ModernCoreException;

public class SdkException extends ModernCoreException{

	private static final long serialVersionUID = 1L;
	
	public static final int SERVER_PATH_NOT_PORT = 300000;
	
	public SdkException(String errorMsg) {
		super(errorMsg);
	}

	public SdkException() {
		super();
	}

	public SdkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SdkException(String message, Throwable cause) {
		super(message, cause);
	}

	public SdkException(Throwable cause) {
		super(cause);
	}
	
}
