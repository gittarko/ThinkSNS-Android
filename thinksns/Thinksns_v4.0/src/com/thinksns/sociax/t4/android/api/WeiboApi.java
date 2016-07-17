package com.thinksns.sociax.t4.android.api;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;

/** 
 * 类说明：   微博接口类
 * @author  dong.he    
 * @date    2015-8-31
 * @version 1.0
 */
public class WeiboApi {
	
	/**
	 * 获取公共/推荐微博
	 * @param count		分页大小
	 * @param max_id	
	 * @param handler	请求回调
	 */
	public static void publicTimeline(int count, int max_id, 
			AsyncHttpResponseHandler handler){
			RequestParams params = new RequestParams();
		    params.put("count", count);
		    params.put("max_id", max_id);
		    ApiHttpClient.post(new String[] {"Weibo", "public_timeline"}, params, handler);
	}

}
