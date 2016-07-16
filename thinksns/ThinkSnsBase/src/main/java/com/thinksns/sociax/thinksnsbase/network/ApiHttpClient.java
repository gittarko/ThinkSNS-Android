package com.thinksns.sociax.thinksnsbase.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thinksns.sociax.thinksnsbase.R;
import com.thinksns.sociax.thinksnsbase.base.BaseApplication;

import org.apache.http.client.params.ClientPNames;

import java.util.Locale;

/** 
 * 类说明：   TS网络请求封装
 * @author  dong.he    
 * @date    2015-8-31
 * @version 4.0
 */
public class ApiHttpClient {

    public static AsyncHttpClient client;
    private static ApiHttpClient httpClient;
    private static final String TAG = ApiHttpClient.class.getSimpleName();

	//接口域名
	public static String HOST = "demo.thinksns.com";
	//更换接口只需要改变这部分
    private static String API;
    private static String API_URL = "http://demo.thinksns.com/ts4/api.php";
    private static String MOD_ACT = "?mod=%s&act=%s";
    private static String SOCKET_SERVER;
    public static String TOKEN;
    public static String TOKEN_SECRET;

    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    
    public ApiHttpClient() {
    	/**
    	 * 这里可以放置一些网络请求的准备工作，例如设置接口地址，设置网络缓存存放地址等
    	 * 
    	 */
	}

    public static String[] getHostInfo() {
        String [] infos = new String[2];
        infos[0] = HOST;
        infos[1] = API;
        return infos;
    }

    public static String getApiName() {
        return ApiHttpClient.API_URL;
    }

    public static String getSocketUrl() {
        return ApiHttpClient.SOCKET_SERVER;
    }

    public static void setTokenInfo(String token, String tokenSecret) {
        ApiHttpClient.TOKEN = token;
        ApiHttpClient.TOKEN_SECRET = tokenSecret;
    }

    public static AsyncHttpClient getHttpClient() {
        return client;
    }


    /**
     * 取消某个Context上下文的所有网络请求
     * @param context
     */
    public static void cancelAll(Context context) {
        client.cancelRequests(context, true);
    }

    public static void get(String url, AsyncHttpResponseHandler handler) {
        client.get(url, handler);
    }
    /**
     * 不带参数的get请求
     * @param mod_act
     * @param handler
     */
    public static void get(String[] mod_act, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(mod_act[0], mod_act[1]), handler);
        log(new StringBuilder("GET ").append(mod_act.toString()).toString());
    }

    /**
     * 带参数的get请求
     * @param mod_act
     * @param params
     * @param handler
     */
    public static void get(String[] mod_act, RequestParams params,
            AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(mod_act[0], mod_act[1]), params, handler);
        log(new StringBuilder("GET ").append("mod=" + mod_act[0] + "&act=" + mod_act[1]).append("&")
                .append(params).toString());
    }

    /**
     * 拼接完整的接口地址,域名+接口+用户认证信息
     * @param mode
     * @param act
     * @return 
     */
    public static String getAbsoluteApiUrl(String mode, String act) {
    	String mod_act = String.format(MOD_ACT, mode, act);
        String url = API_URL + mod_act;
        url += "&oauth_token=" + TOKEN;
        url += "&oauth_token_secret=" + TOKEN_SECRET;
        Log.e("ApiHttpClient", "url:" + url);
        return url;
    }

    /*
     * 获取当前的API地址
     */
    public static String getApiUrl() {
        return API_URL;
    }

    /**
     * 获取当前请求的ModeName和Act
     * @return
     */
    public static String getModAct() {
    	return MOD_ACT;
    }
    
    public static void getDirect(String url, AsyncHttpResponseHandler handler) {
        client.get(url, handler);
        log(new StringBuilder("GET ").append(url).toString());
    }

    /**
     * 打印日志
     * @param log
     */
    public static void log(String log) {
        Log.d("BaseApi", log);
    }

    /**
     * 不带参数的Post请求
     * @param mod_act
     * @param handler
     */
    public static void post(String[] mod_act, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl(mod_act[0], mod_act[1]), handler);
        log(new StringBuilder("POST ").append("mod=" + mod_act[0] + "&act=" + mod_act[1]).toString());
    }

    /**
     * 带参数的Post请求
     * @param mod_act
     * @param params
     * @param handler
     */
    public static void post(String[] mod_act, RequestParams params,
            AsyncHttpResponseHandler handler) {
    	if(mod_act == null || mod_act.length != 2) {
    		return;
    	}
        String url = getAbsoluteApiUrl(mod_act[0], mod_act[1]);
        client.post(url, params, handler);
        log(new StringBuilder("POST " + url + " ").append("&").append(params).toString());
    }
    
    public static void postDirect(String url, RequestParams params,
            AsyncHttpResponseHandler handler) {
        client.post(url, params, handler);
        log(new StringBuilder("POST ").append(url).append("&").append(params)
                .toString());
    }

    public static void put(String[] mod_act, AsyncHttpResponseHandler handler) {
        client.put(getAbsoluteApiUrl(mod_act[0], mod_act[1]), handler);
        log(new StringBuilder("PUT ").append("mod=" + mod_act[0] + "&act=" + mod_act[1]).toString());
    }

    public static void put(String[] mod_act, RequestParams params,
            AsyncHttpResponseHandler handler) {
        client.put(getAbsoluteApiUrl(mod_act[0], mod_act[1]), params, handler);
        log(new StringBuilder("PUT ").append("mod=" + mod_act[0] + "&act=" + mod_act[1]).append("&")
                .append(params).toString());
    }

    public static void setApiUrl(String apiUrl) {
        API_URL = apiUrl;
    }

    public static void setModAct(String[] mod_act) {
    	MOD_ACT = String.format(MOD_ACT, mod_act[0], mod_act[1]);
    }
    
    /**
     * 设置AsyncHttpClient
     * @param context
     */
    public static void newHttpClient(BaseApplication context) {
        if(client != null)
            client = null;

        client = new AsyncHttpClient();
        HOST = context.getResources().getStringArray(R.array.site_url)[0];
        API =  context.getResources().getStringArray(R.array.site_url)[1];
        SOCKET_SERVER = context.getResources().getStringArray(R.array.site_url)[2];
        API_URL = "http://" + HOST + "/" + API;

        client.addHeader("Accept-Language", Locale.getDefault().toString());
        client.addHeader("Host", HOST);
        client.addHeader("Connection", "Keep-Alive");
        //设置超时10s
        client.setTimeout(10000);
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        //初始化client头信息
        StringBuilder ua = new StringBuilder(ApiHttpClient.HOST);
        ua.append('/' + context.getPackageInfo().versionName + '_'
                +  context.getPackageInfo().versionCode);// app版本信息
        ua.append("/Android");	// 手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
        ua.append("/" + android.os.Build.MODEL); // 手机型号
        client.setUserAgent(ua.toString());
    }

    public static Uri.Builder createUrlBuild(String app, String mod, String act) {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority(HOST);
        uri.appendEncodedPath(API);
        uri.appendQueryParameter("app", app);
        uri.appendQueryParameter("mod", mod);
        uri.appendQueryParameter("act", act);
        uri.appendQueryParameter("oauth_token", TOKEN);
        uri.appendQueryParameter("oauth_token_secret", TOKEN_SECRET);

        Log.d(TAG, " url " + uri.toString());
        return uri;
    }

    public static interface HttpResponseListener {
    	public void onSuccess(Object result);
    	
    	public void onError(Object result);
    }
}
