package com.vtradex.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.modern.caller.impl.DefaultModernInvokeCommand;

public class SdkInvokeCommand extends DefaultModernInvokeCommand {

	public SdkInvokeCommand(String method, Object... params) {
		super(method, params);
	}
	
	public SdkInvokeCommand addExtParam(String key, String value) {
		getExtParams().put(key, value);
		return this;
	}
	public SdkInvokeCommand addTraceNo(String traceNo) {
		getTraceNos().add(traceNo);
		return this;
	}
	
}
