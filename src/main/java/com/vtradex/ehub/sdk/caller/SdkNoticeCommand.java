package com.vtradex.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.modern.caller.impl.DefaultModernNoticeCommand;
import com.vtradex.ehub.sdk.impl.DefaultSdkClient;

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
}
