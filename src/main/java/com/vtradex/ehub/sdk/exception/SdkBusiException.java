package com.vtradex.ehub.sdk.exception;

/**
 * 业务异常
 * @author zilong.li
 *
 */
public class SdkBusiException extends SdkException {
	
	private static final long serialVersionUID = 1L;
	
	public static final int BUSI_EXCEPTION = 400000;

	public SdkBusiException() {
		super();
		setCode(BUSI_EXCEPTION);
	}

	public SdkBusiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		setCode(BUSI_EXCEPTION);
	}

	public SdkBusiException(String message, Throwable cause) {
		super(message, cause);
		setCode(BUSI_EXCEPTION);
	}

	public SdkBusiException(String errorMsg) {
		super(errorMsg);
		setCode(BUSI_EXCEPTION);
	}

	public SdkBusiException(Throwable cause) {
		super(cause);
		setCode(BUSI_EXCEPTION);
	}
	
	@Override
	public SdkBusiException setCode(int code) {
		if(code < BUSI_EXCEPTION) {
			throw new SdkBusiException("busiException code must >= 400000");
		}
		return (SdkBusiException)super.setCode(code);
	}
	
}
