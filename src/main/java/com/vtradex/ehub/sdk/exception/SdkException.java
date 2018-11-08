package com.vtradex.ehub.sdk.exception;

public class SdkException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public SdkException(String errorMsg) {
		super(errorMsg);
	}

}
