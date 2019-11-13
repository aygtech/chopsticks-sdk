package com.chopsticks.ehub.sdk.exception;

import com.chopsticks.core.rocketmq.modern.exception.ModernCoreException;

public class SdkException extends ModernCoreException{

	private static final long serialVersionUID = 1L;
	
	public static final int SERVER_PATH_NOT_PORT = 300000;
	public static final int USER_TRANSACTION_CHECK_ERROR = 300001;
	public static final int NOT_IN_TRANSACTION = 300002;
	
	public SdkException(String errorMsg) {
		super(errorMsg);
	}

	public SdkException() {
		super();
	}

	public SdkException(String message, Throwable cause) {
		super(message, cause);
	}

	public SdkException(Throwable cause) {
		super(cause);
	}

	public SdkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
