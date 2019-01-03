package com.vtradex.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.modern.caller.impl.DefaultModernNoticeCommand;

public class SdkNoticeCommand extends DefaultModernNoticeCommand {
	
	public SdkNoticeCommand(String method, Object... params) {
		super(method, params);
	}
	
	public SdkNoticeCommand addTraceNo(String traceNo) {
		getTraceNos().add(traceNo);
		return this;
	}
	
	public SdkNoticeCommand addExtParam(String key, String value) {
		getExtParams().put(key, value);
		return this;
	}
}
