package com.vtradex.ehub.sdk.handler;

import com.chopsticks.core.rocketmq.handler.BaseNoticeContext;
import com.chopsticks.core.rocketmq.modern.handler.DefaultModerNoticeContext;

public class SdkNoticeContext extends DefaultModerNoticeContext{
	
	public SdkNoticeContext(BaseNoticeContext ctx) {
		super(ctx);
	}

	public SdkNoticeContext(String id, String originId, int reconsumeTimes) {
		super(id, originId, reconsumeTimes);
	}

}
