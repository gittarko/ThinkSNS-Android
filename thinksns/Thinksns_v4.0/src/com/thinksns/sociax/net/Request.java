package com.thinksns.sociax.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import android.net.Uri;
import android.util.Log;

import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.exception.HostNotFindException;
import com.thinksns.sociax.android.R;

public abstract class Request {
	protected static final String TAG = "HttpRequest";
	protected HttpClient httpClient;
	protected Uri.Builder uri;
	protected static String token;
	protected static String secretToken;
	protected String url;
	protected ThinksnsHttpClient thinsnsHttpClient;

	public Request() {
		thinsnsHttpClient = new ThinksnsHttpClient();
		httpClient = ThinksnsHttpClient.getHttpClient();
	}

	public Request(String url) {
		this.url = url;
		thinsnsHttpClient = new ThinksnsHttpClient();
		httpClient = ThinksnsHttpClient.getHttpClient();
	}

	public Request(Uri.Builder uri) {
		this.uri = uri;
		thinsnsHttpClient = new ThinksnsHttpClient();
		httpClient = ThinksnsHttpClient.getHttpClient();
	}

	public synchronized void setUri(Uri.Builder uri) {
		this.uri = uri;
		// 在有token的时候每次请求都追加一个token;
		if (!"".equals(token)) {
			uri.appendQueryParameter("oauth_token", token);
		}
		if (!"".equals(secretToken)) {
			uri.appendQueryParameter("oauth_token_secret", secretToken);
		}
	}
	
	public static String getTokenString() {
		String url = "";
		if(!"".equals(token))
			url += "&oauth_token=" + token;
		if(!"".equals(secretToken))
			url += "&oauth_token_secret=" + secretToken;
		
		return url;
	}

	public static String getToken() {
		return Request.token;
	}

	public static String getSecretToken() {
		return Request.secretToken;
	}

	public static void setToken(String token) {
		Request.token = token;
	}

	public static void setSecretToken(String secretToken) {
		Request.secretToken = secretToken;
	}

	/**
	 * 已经创建好了http对象。对象开始进行调用和请求运行
	 * 
	 * @return JSONArray
	 * @throws ResponseTimeoutException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public synchronized Object run() throws ClientProtocolException,
			IOException, HostNotFindException {
		if ("".equals(uri))throw new ClientProtocolException("非法调用，执行请求时必须设置uri对象");
		HttpRequestBase http = this.executeObject();
		HttpResponse httpResp = null;
		String result = "ERROR";

		try {
			if (httpClient!=null&&http!=null) {
				httpResp = httpClient.execute(http);
				int code = httpResp.getStatusLine().getStatusCode();
				Log.d(TAG, "code="+code);
				if (HttpStatus.SC_OK == code) {
					result = getJsonStringFromGZIP(httpResp);
					// result =
					// EntityUtils.toString(httpResp.getEntity(),HTTP.UTF_8);
				} else if (HttpStatus.SC_NOT_FOUND == code) {
					throw new HostNotFindException(HttpHelper.getContext()
							.getString(R.string.host_not_find));
				}
				Log.d("Request", "Request" + code+",result="+result);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Log.e(AppConstant.APP_TAG, e.toString());
		}
		return result;
	}

	private String getJsonStringFromGZIP(HttpResponse response) {
		String jsonString = null;
		try {
			Log.d("Request", " use   ");
			InputStream is = response.getEntity().getContent();
			Log.d("Request", " use   ");
			BufferedInputStream bis = new BufferedInputStream(is);
			Log.d("Request", " use   ");
			bis.mark(2);
			// 取前两个字节
			byte[] header = new byte[2];
			int result = bis.read(header);
			// reset输入流到开始位置
			Log.d("Request", " use   ");
			bis.reset();
			Log.d("Request", " use   ");
			// 判断是否是GZIP格式
			int headerData = getShort(header);
			Log.d("Request", " use   ");
			// Gzip 流 的前两个字节是 0x1f8b
			if (result != -1 && headerData == 0x1f8b) {
				Log.d("Request", " use GZIPInputStream  ");
				is = new GZIPInputStream(bis);
			} else {
				Log.d("Request", " not use GZIPInputStream");
				is = bis;
			}
			InputStreamReader reader = new InputStreamReader(is, "utf-8");
			char[] data = new char[100];
			int readSize;
			StringBuffer sb = new StringBuffer();
			while ((readSize = reader.read(data)) > 0) {
				sb.append(data, 0, readSize);
			}
			jsonString = sb.toString();
			bis.close();
			reader.close();
		} catch (Exception e) {
			Log.e("Request", e.toString());
		}
		return jsonString;
	}

	private int getShort(byte[] data) {
		return (data[0] << 8) | data[1] & 0xFF;
	}

	public abstract Request append(String name, Object value);

	protected abstract HttpRequestBase executeObject();

	public synchronized void setEwmUri(Uri.Builder uri) {
		this.uri = uri;
	}
}
