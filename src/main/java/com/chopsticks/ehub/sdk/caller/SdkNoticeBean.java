package com.chopsticks.ehub.sdk.caller;

import java.util.concurrent.TimeUnit;

import com.chopsticks.core.rocketmq.modern.caller.BaseNoticeBean;

public interface SdkNoticeBean extends BaseNoticeBean{
	
	public SdkNoticeResult sdkNotice(SdkNoticeCommand cmd);
	public SdkNoticeResult sdkNotice(SdkNoticeCommand cmd, Object orderKey);
	public SdkNoticeResult sdkNotice(SdkNoticeCommand cmd, long delay, TimeUnit delayTimeUnit);
}
