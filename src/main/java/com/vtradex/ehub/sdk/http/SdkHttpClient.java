package com.vtradex.ehub.sdk.http;

import java.util.concurrent.atomic.AtomicInteger;

import com.chopsticks.common.concurrent.Promise;
import com.chopsticks.common.concurrent.PromiseListener;
import com.chopsticks.http.HttpClient;
import com.chopsticks.http.entity.HttpRequest;
import com.chopsticks.http.entity.HttpResponse;
import com.google.common.base.Throwables;
import com.vtradex.ehub.sdk.concurrent.SdkListener;
import com.vtradex.ehub.sdk.concurrent.SdkPromise;
import com.vtradex.ehub.sdk.http.entity.SdkHttpRequest;
import com.vtradex.ehub.sdk.http.entity.SdkHttpResponse;

public class SdkHttpClient extends HttpClient{
	
	public SdkHttpResponse sdkInvoke(SdkHttpRequest req) {
		try {
			return sdkAsyncInvoke(req).get();
		}catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	public SdkPromise<SdkHttpResponse> sdkAsyncInvoke(SdkHttpRequest req) {
		final SdkPromise<SdkHttpResponse> ret = new SdkPromise<SdkHttpResponse>();
		asyncInvoke(req).addListener(new PromiseListener<HttpResponse>() {
			@Override
			public void onFailure(Throwable t) {
				ret.setException(t);
			}
			@Override
			public void onSuccess(HttpResponse result) {
				ret.set(new SdkHttpResponse(result));
			}
		});;
		return ret;
	}
	@Override
	@Deprecated
	public HttpResponse invoke(HttpRequest req) {
		return super.invoke(req);
	}
	@Override
	@Deprecated
	public Promise<HttpResponse> asyncInvoke(HttpRequest req) {
		return super.asyncInvoke(req);
	}
	public static void main(String[] args) throws Throwable{
		SdkHttpClient testClient = new SdkHttpClient();
		testClient.start();
		SdkHttpRequest req = new SdkHttpRequest();
		req.setUrl("http://www.baidu.com");
		final AtomicInteger ii = new AtomicInteger();
		for(int i = 0; i < 1000; i++) {
			testClient.sdkAsyncInvoke(req).addSdkListener(new SdkListener<SdkHttpResponse>() {
				@Override
				public void onFailure(Throwable t) {
					System.out.println(ii.decrementAndGet());
					t.printStackTrace();
				}
				@Override
				public void onSuccess(SdkHttpResponse result) {
					if(result.getBody() != null) {
						try {
							result.getBody().close();
						}catch (Throwable e) {
							e.printStackTrace();
						}
					}
					System.out.println(ii.decrementAndGet() + " " + result.getStatus());
					
				}
			});
		}
	}
}
