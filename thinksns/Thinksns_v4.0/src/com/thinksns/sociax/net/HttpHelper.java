package com.thinksns.sociax.net;

import com.thinksns.sociax.android.R;

import android.content.Context;

public class HttpHelper {
	private static String host;
	private static String path;
	private static Context context;

	public static void initHttp(Context context) {
		HttpHelper.setContext(context);
		String[] configHttp = context.getResources().getStringArray(
				R.array.site_url);
		HttpHelper.host = configHttp[0];
		HttpHelper.path = configHttp[1];
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		HttpHelper.host = host;
	}

	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		HttpHelper.path = path;
	}

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		HttpHelper.context = context;
	}

}
