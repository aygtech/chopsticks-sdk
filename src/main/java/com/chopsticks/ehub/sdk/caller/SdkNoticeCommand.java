package com.chopsticks.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.modern.caller.impl.DefaultModernNoticeCommand;
import com.chopsticks.ehub.sdk.impl.DefaultSdkClient;

public class SdkNoticeCommand extends DefaultModernNoticeCommand {
	
	public SdkNoticeCommand(String method, Object... params) {
		super(method, params);
	}
	
	public SdkNoticeCommand setOrgKey(String orgKey) {
		return addExtParam(DefaultSdkClient.ORG_KEY, orgKey);
	}
	public SdkNoticeCommand setUnikey(String uniKey) {
		return addExtParam(DefaultSdkClient.UNI_KEY, uniKey);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SdkNoticeCommand addExtParam(String key, String value) {
		return super.addExtParam(key, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SdkNoticeCommand addTraceNo(String traceNo) {
		return super.addTraceNo(traceNo);
	}
	
	public SdkNoticeCommand transaction() {
		super.setTransaction(true);
		return this;
	}
}
