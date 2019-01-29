package com.vtradex.ehub.sdk.caller;

import java.util.Set;

import com.chopsticks.core.rocketmq.caller.BaseNoticeResult;

public class SdkNoticeResult extends BaseNoticeResult {
	
	public BaseNoticeResult result;
	
	public SdkNoticeResult(BaseNoticeResult result) {
		super(null);
		this.result = result;
	}
	@Override
	public String getId() {
		return result.getId();
	}
	@Override
	public Set<String> getTraceNos() {
		return result.getTraceNos();
	}
	@Override
	public String getOriginId() {
		return result.getOriginId();
	}
	@Override
	public String toString() {
		return "SdkNoticeResult [getId()=" + getId() + ", getTraceNos()=" + getTraceNos() + ", getOriginId()="
				+ getOriginId() + "]";
	}
}
