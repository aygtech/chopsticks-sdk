package com.chopsticks.ehub.sdk.caller;

import java.util.Set;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;

import com.chopsticks.core.rocketmq.caller.BaseNoticeResult;
import com.google.common.base.Strings;

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
	public String getTransactionId() {
		return result.getTransactionId();
	}
	@Override
	public SendResult getSendResult() {
		return result.getSendResult();
	}
	@Override
	public String getOriginId() {
		return result.getOriginId();
	}
	public Integer getTransactionCheckNum() {
		Integer ret = null;
		if(result.getMessageExt() != null) {
			String num = result.getMessageExt().getUserProperty(MessageConst.PROPERTY_TRANSACTION_CHECK_TIMES);
			if(!Strings.isNullOrEmpty(num)) {
				ret = Integer.valueOf(num);
			}
		}
		return ret;
	}
	@Override
	public String toString() {
		return "SdkNoticeResult [getId()=" + getId() + ", getTraceNos()=" + getTraceNos() + ", getOriginId()="
				+ getOriginId() + "]";
	}
}
