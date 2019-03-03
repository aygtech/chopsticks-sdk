package com.vtradex.ehub.sdk.caller;

import com.chopsticks.core.rocketmq.modern.caller.impl.DefaultModernInvokeCommand;
import com.vtradex.ehub.sdk.impl.DefaultSdkClient;

/**
 * 同步调用参数
 * @author zilong.li
 *
 */
public class SdkInvokeCommand extends DefaultModernInvokeCommand {
	
	/**
	 * @param method 方法名
	 * @param params 参数
	 */
	public SdkInvokeCommand(String method, Object... params) {
		super(method, params);
	}
	
	/**
	 * 指定单次方法调用身份
	 * @param orgKey 身份
	 * @return 当前对象，可连续调用
	 */
	public SdkInvokeCommand setOrgKey(String orgKey) {
		return addExtParam(DefaultSdkClient.ORG_KEY, orgKey);
	}
	/**
	 * 指定单次方法调用身份
	 * @param uniKey 身份
	 * @return 当前对象，可连续调用
	 */
	public SdkInvokeCommand setUnikey(String uniKey) {
		return addExtParam(DefaultSdkClient.UNI_KEY, uniKey);
	}
	
}
