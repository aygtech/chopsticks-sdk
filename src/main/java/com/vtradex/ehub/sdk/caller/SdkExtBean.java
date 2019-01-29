package com.vtradex.ehub.sdk.caller;

import java.util.concurrent.TimeUnit;

import com.chopsticks.core.rocketmq.modern.caller.BaseExtBean;
import com.vtradex.ehub.sdk.concurrent.SdkPromise;

public interface SdkExtBean extends BaseExtBean {

	public SdkInvokeResult sdkInvoke(SdkInvokeCommand cmd);
	public SdkPromise<SdkInvokeResult> sdkAsyncInvoke(SdkInvokeCommand cmd);

	public SdkInvokeResult sdkInvoke(SdkInvokeCommand cmd, long timeout, TimeUnit timeoutUnit);
	public SdkPromise<SdkInvokeResult> sdkAsyncInvoke(SdkInvokeCommand cmd, long timeout, TimeUnit timeoutUnit);

	public SdkNoticeResult sdkNotice(SdkNoticeCommand cmd);
	public SdkPromise<SdkNoticeResult> sdkAsyncNotice(SdkNoticeCommand cmd);

	public SdkNoticeResult sdkNotice(SdkNoticeCommand cmd, Object orderKey);
	public SdkPromise<SdkNoticeResult> sdkAsyncNotice(SdkNoticeCommand cmd, Object orderKey);
	
	public SdkNoticeResult sdkNotice(SdkNoticeCommand cmd, long delay, TimeUnit delayTimeUnit);
	public SdkPromise<SdkNoticeResult> sdkAsyncNotice(SdkNoticeCommand cmd, Long delay, TimeUnit delayTimeUnit);
}
