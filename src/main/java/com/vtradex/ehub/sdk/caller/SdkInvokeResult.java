package com.vtradex.ehub.sdk.caller;

import java.util.Set;

import com.chopsticks.core.rocketmq.caller.BaseInvokeResult;
/**
 * 同步调用结果
 * @author zilong.li
 *
 */
public class SdkInvokeResult extends BaseInvokeResult {
	
	private BaseInvokeResult result;
	
	public SdkInvokeResult(BaseInvokeResult result) {
		super(null);
		this.result = result;
	}
	
	@Override
	public Set<String> getTraceNos() {
		return result.getTraceNos();
	}
	
	@Override
	public byte[] getBody() {
		return result.getBody();
	}
}
