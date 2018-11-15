package com.vtradex.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.modern.caller.impl.DefaultModernInvokeCommand;

public class SdkInvokeCommand extends DefaultModernInvokeCommand {

	public SdkInvokeCommand(String method, Object... params) {
		super(method, params);
	}
}
