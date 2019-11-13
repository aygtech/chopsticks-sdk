package com.chopsticks.ehub.sdk.caller.impl;

import com.chopsticks.core.rocketmq.caller.BaseNoticeResult;
import com.chopsticks.core.rocketmq.caller.TransactionChecker;
import com.chopsticks.core.rocketmq.caller.TransactionState;
import com.chopsticks.ehub.sdk.caller.SdkNoticeResult;
import com.chopsticks.ehub.sdk.caller.SdkTransactionChecker;
import com.chopsticks.ehub.sdk.caller.SdkTransactionState;
import com.chopsticks.ehub.sdk.exception.SdkException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SdkTransactionCheckWapper implements TransactionChecker {

	private final SdkTransactionChecker checker;

	public SdkTransactionCheckWapper(SdkTransactionChecker checker) {
		this.checker = checker;
	}

	@Override
	public TransactionState check(BaseNoticeResult noticeResult) {
		SdkTransactionState state = null;
		try {
			state = checker.check(new SdkNoticeResult(noticeResult));
		} catch (Throwable e) {
			log.error("user transaction check error", new SdkException(e).setCode(SdkException.USER_TRANSACTION_CHECK_ERROR));
			state = SdkTransactionState.UNKNOW;
		}
		switch(state) {
			case UNKNOW:
				return TransactionState.UNKNOW;
			case COMMIT:
				return TransactionState.COMMIT;
			case ROLLBACK:
				return TransactionState.ROLLBACK;
			default:
				log.warn("noticeId : {}, state : {} undefined", noticeResult.getId(), state);
				return TransactionState.UNKNOW;
			}
		}

}
