package com.thinksns.sociax.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.JSONHelper;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class Post extends Request {

	String queryString = "";
	private List<NameValuePair> params;

	public Post() {
		super();
		params = new ArrayList<NameValuePair>();
	}

	public Post(Builder uri) {
		super(uri);
		params = new ArrayList<NameValuePair>();
	}

	public Post(String Url) {
		super(Url);
	}

	@Override
	public Request append(String name, Object value) {
		if (JSONHelper.isArray(value.getClass())
				|| JSONHelper.isCollection(value.getClass())) {
			this.params.add(new BasicNameValuePair(name, JSONHelper.toJSON(value)));

			queryString += name + "=" + value + "&";
		} else {
			this.params.add(new BasicNameValuePair(name, value + ""));

			queryString += name + "=" + value + "&";
		}
		return this;
	}

	@Override
	protected HttpRequestBase executeObject() {
		String url;
		if (this.url != null && !this.url.equals("")) {
			url = this.url;
		} else {
			Uri uriObj = uri.build();
			url = uriObj.toString();
		}
		if (fixTheBug(url)) {
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Accept-Encoding", "gzip");
			// 设置字符集
			HttpEntity entity;
			try {
				if (params != null) {
					entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
					httpPost.setEntity(entity);
					// 清空参数
					params.clear();
				}
			} catch (UnsupportedEncodingException e) {
				Log.e(Request.TAG, "error,unsupported encoding");
			}
			return httpPost;
		}
		// 设置参数实体
		return null;
	}

	// 修复这个bug，這個bug就是軟件退出后主機為為空
	private boolean fixTheBug(String url) {
		String[] configHost = Thinksns.getContext().getResources().getStringArray(
				R.array.site_url);
		
		if (url.contains(configHost[0])) {
			return true;
		}

		return false;
	}
}
