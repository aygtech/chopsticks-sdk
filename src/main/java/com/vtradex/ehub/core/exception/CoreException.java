package com.vtradex.ehub.core.exception;

public class CoreException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public static final int UNKNOW_EXCEPTION = -1; 
	
	private int code = UNKNOW_EXCEPTION;

	public CoreException() {
		super();
	}

	public CoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public CoreException(String message) {
		super(message);
	}

	public CoreException(Throwable cause) {
		super(cause);
	}

	public int getCode() {
		return code;
	}

	public CoreException setCode(int code) {
		this.code = code;
		return this;
	}
	
	public String getOriMessage() {
		return super.getMessage();
	}
	
	@Override
	public String getMessage() {
		return String.format("code : %s, msg : %s", getCode(), super.getMessage());
	}
}
