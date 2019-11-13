package com.chopsticks.ehub.sdk.http.entity;

import com.chopsticks.http.entity.HttpResponse;

public class SdkHttpResponse extends HttpResponse {
	
	public SdkHttpResponse() {}
	
	public SdkHttpResponse(HttpResponse resp) {
		super();
		setBody(resp.getBody());
		setHeaders(resp.getHeaders());
		setStatus(resp.getStatus());
	}
}
