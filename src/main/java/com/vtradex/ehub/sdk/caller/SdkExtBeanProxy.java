package com.vtradex.ehub.sdk.caller;

import java.lang.reflect.Method;

import com.chopsticks.common.concurrent.Promise;
import com.chopsticks.common.concurrent.PromiseListener;
import com.chopsticks.core.caller.InvokeResult;
import com.chopsticks.core.caller.NoticeResult;
import com.chopsticks.core.rocketmq.DefaultClient;
import com.chopsticks.core.rocketmq.caller.BaseInvokeResult;
import com.chopsticks.core.rocketmq.caller.BaseNoticeResult;
import com.chopsticks.core.rocketmq.modern.caller.ExtBeanProxy;
import com.vtradex.ehub.sdk.concurrent.SdkPromise;

public class SdkExtBeanProxy extends ExtBeanProxy{

	public SdkExtBeanProxy(String clazzName, DefaultClient client) {
		super(clazzName, client);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	    Object ret = super.invoke(proxy, method, args);
	    if(method.getName().startsWith("sdk")) {
	    	if(method.getName().startsWith("sdkAsyncInvoke")) {
	    		@SuppressWarnings("unchecked")
				Promise<InvokeResult> invokeRet = (Promise<InvokeResult>)ret;
	    		final SdkPromise<SdkInvokeResult> newRet = new SdkPromise<SdkInvokeResult>();
	    		invokeRet.addListener(new PromiseListener<InvokeResult>() {
	    			@Override
	    			public void onSuccess(InvokeResult result) {
	    				newRet.set(new SdkInvokeResult((BaseInvokeResult)result));
	    			}
	    			@Override
	    			public void onFailure(Throwable t) {
	    				newRet.setException(t);
	    			}
				});
	    		ret = newRet;
	    	}else if(method.getName().startsWith("sdkAsyncNotice")){
	    		@SuppressWarnings("unchecked")
				Promise<NoticeResult> noticeRet = (Promise<NoticeResult>)ret;
	    		final SdkPromise<SdkNoticeResult> newRet = new SdkPromise<SdkNoticeResult>();
	    		noticeRet.addListener(new PromiseListener<NoticeResult>() {
	    			@Override
	    			public void onSuccess(NoticeResult result) {
	    				newRet.set(new SdkNoticeResult((BaseNoticeResult)result));
	    			}
	    			@Override
	    			public void onFailure(Throwable t) {
	    				newRet.setException(t);
	    			}
				});
	    		ret = newRet;
	    	}else {
	    		if(ret instanceof BaseInvokeResult) {
		    		ret = new SdkInvokeResult((BaseInvokeResult)ret);
		    	}else {
		    		ret = new SdkNoticeResult((BaseNoticeResult)ret);
		    	}
	    	}
	    }
		return ret;
	}

}
