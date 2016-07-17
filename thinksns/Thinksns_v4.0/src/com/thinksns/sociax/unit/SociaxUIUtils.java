package com.thinksns.sociax.unit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.thinksnsbase.utils.WordCount;

/**
 * 用于SociaxUI的工具集合类
 * 
 * @author Povol
 * 
 */
public class SociaxUIUtils {

	private static final String TAG = "SociaxUIUtils";

	/**
	 * 隐藏输入法
	 * 
	 * @param paramContext
	 * @param paramEditText
	 */
	public static void hideSoftKeyboard(Context paramContext,
			EditText paramEditText) {
		((InputMethodManager) paramContext
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
	}

	/**
	 * 显示输入法
	 * 
	 * @param paramContext
	 * @param paramEditText
	 */
	public static void showSoftKeyborad(Context paramContext,
			EditText paramEditText) {
		((InputMethodManager) paramContext
				.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(
				paramEditText, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 检测用户输入的Email格式
	 * 
	 * @param mail
	 * @return
	 */
	public static boolean checkEmail(String mail) {
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.find();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static void setInputLimit(TextView tv, EditText et) {
		WordCount wordCount = new WordCount(et, tv);
		tv.setText(wordCount.getMaxCount() + "");
		et.addTextChangedListener(wordCount);
	}

	/**
	 * 创建快捷方式
	 * 
	 * @param context
	 */
	public static void addShortcut(Context context) {

		String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
		// 快捷方式要启动的包
		// Intent intent = gotoWhere(context);
		Intent intent = new Intent(context, ThinksnsActivity.class);
		// 设置快捷方式的参数
		Intent shortcutIntent = new Intent(ACTION_INSTALL_SHORTCUT);
		// 设置名称
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context
				.getResources().getString(R.string.app_name));
		// 设置启动 Intent
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 设置图标
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context,
						R.drawable.icon));
		// 只创建一次快捷方式
		shortcutIntent.putExtra("duplicate", false);
		context.sendBroadcast(shortcutIntent);
	}

	/**
	 * 获取网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetworkType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 判断网络连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static void highlightContent(Context paramContext,
			Spannable paramSpannable) {
		try {
			Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]").matcher(
					paramSpannable);

			while (true) {
				if (!localMatcher.find())
					return;
				int i = localMatcher.start();
				int j = localMatcher.end();
				String str = localMatcher.group(1);
				Integer localInteger = null;
				if ((localInteger.intValue() <= 0) || (localInteger == null))
					continue;
				Log.d(TAG, "i=" + i + ",j=" + j);
				paramSpannable.setSpan(
						new ImageSpan(paramContext, localInteger.intValue()),
						i, j, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		} catch (Exception e) {
			Log.d("TSUtils", e.toString());
		}
	}

	public static String getFromString(int from) {
		String fromString = "来自网站";
		switch (from) {
		case 0:
			fromString = "来自网站";
			break;
		case 1:
			fromString = "来自手机网页版";
			break;
		case 2:
			fromString = "来自Android客户端";
			break;
		case 3:
			fromString = "来自iPhone客户端";
			break;
		case 4:
			fromString = "来自iPad客户端";
			break;
		case 5:
			fromString = "来自Windows.Phone客户端";
			break;
		}
		return fromString;
	}

	/**
	 * 过滤 bom
	 * 
	 * @param in
	 * @return
	 */
	public static String JSONFilterBom(String in) {
		// consume an optional byte order mark (BOM) if it exists
		if (in != null && in.startsWith("\ufeff")) {
			in = in.substring(1);
		}
		return in;
	}

	/**
	 * 
	 * 基本功能：过滤所有以"<"开头以">"结尾的标签
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterHtml(String str) {
		if (str == null) {
			return "";
		}
		String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签
		Pattern pattern = Pattern.compile(regxpForHtml);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result1 = matcher.find();
		while (result1) {
			matcher.appendReplacement(sb, "");
			result1 = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	@SuppressLint("NewApi")
	public static void initTraffic(Context context) {
		if (Build.VERSION.SDK_INT < 8) {
			return;
		}
		PackageManager packageManager = context.getPackageManager();
		long receive = 0;
		long send = 0;
		try {
			ApplicationInfo applicationInfo = packageManager
					.getApplicationInfo("com.thinksns.sociax.android",
							ApplicationInfo.FLAG_SYSTEM);
			int uid = applicationInfo.uid;
			receive = TrafficStats.getUidRxBytes(uid);
			send = TrafficStats.getUidTxBytes(uid);
			System.err.println("receive " + receive);
			System.err.println("send " + send);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void startActivity(Activity context, Class<?> cls) {
		Intent intent = new Intent(context, cls);
		context.startActivity(intent);
		Anim.in(context);
	}

	public static void startActivity(Activity context, Class<?> cls, Bundle data) {
		Intent intent = new Intent(context, cls);
		intent.putExtras(data);
		context.startActivity(intent);
		Anim.in(context);
	}

	public static boolean isNull(String str) {
		if (str != null && str != "" && !str.equals("null")) {
			return true;
		} else {
			return false;
		}
	}

	public static String isNullStr(String str) {
		if (str != null && str != "" && !str.equals("null")) {
			return str;
		} else {
			return "";
		}
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getWindowWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */

	public static int getWindowHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

}
