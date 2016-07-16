package com.thinksns.sociax.thinksnsbase.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类说明： sociax工具
 * @version 1.0
 */
public class UnitSociax {
	final static String TAG = "TSTAG_UnitSociax";

	/**
	 * 实现文本复制功能
	 * @param content
	 */
	public static void copy(String content, Context context)
	{
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	/**
	 * 实现粘贴功能
	 * @param context
	 * @return
	 */
	public static String paste(Context context)
	{
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

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
	 * 判断是否存在sd卡
	 *
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 将文件读取成2进制
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static byte[] readFile(File file) throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(file));
		int len = bufferedInputStream.available();
		byte[] bytes = new byte[len];
		int r = bufferedInputStream.read(bytes);
		if (len != r) {
			bytes = null;
			throw new IOException("读取文件不正确");
		}
		bufferedInputStream.close();
		return bytes;
	}

	/**
	 *
	 * 根据图片地址，获取图片拍照时候的旋转的度数（横拍/竖拍/倒拍）
	 *
	 * @param absolutePath
	 */
	public int getLocalPicRaotate(String absolutePath) {
		int degree = 0;
		try {

			ExifInterface exifInterface = new ExifInterface(absolutePath);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
			return degree;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * 获取屏幕尺寸
	 *
	 * @param context
	 * @return {宽，高}
	 */
	public static int[] getDevicePix(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		metric = context.getApplicationContext().getResources()
				.getDisplayMetrics();
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		float density = metric.density;		// 每平方英寸中的像素数
		int densityDpi = metric.densityDpi;	// 没英寸中的像素数目
		Log.v("屏幕数据如下", width + "  " + height + "  " + density + " "
				+ densityDpi + "  ");
		int[] result = { width, height };
		return result;
	}

	/**
	 * 将二进制长度转换成文件大小
	 *
	 * @param length
	 * @return
	 */
	public static String formatFileSize(long length) {
		String result = null;
		int sub_string = 0;
		if (length >= 1073741824) {
			sub_string = String.valueOf((float) length / 1073741824).indexOf(
					".");
			result = ((float) length / 1073741824 + "000").substring(0,
					sub_string + 3) + "GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3) + "MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3) + "KB";
		} else if (length < 1024)
			result = Long.toString(length) + "B";
		return result;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片 记得使用之后调用Bitmap.recycle();及时释放内存
	 *
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		java.io.InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
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

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 * @param context
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 * @param context
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 根据资源的名字获取它的ID
	 *
	 * @param name
	 *            要获取的资源的名字
	 * @param defType
	 *            资源的类型，如drawable, string 。。。
	 * @return 资源的id
	 */
	public static int getResId(Context context, String name, String defType) {
		String packageName = context.getApplicationInfo().packageName;
		return context.getResources().getIdentifier(name, defType, packageName);

	}

	/**
	 *
	 * 基本功能：过滤所有以"<"开头以">"结尾的标签
	 *
	 * @param str
	 * @return String
	 */
	public String filterHtml(String str) {
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

	/**
	 * 自适应listview的高度，用于解决listview嵌套在scrollview内显示不完全的问题
	 *
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
					MeasureSpec.AT_MOST);
			listItem.measure(desiredWidth, 0); // 计算子项View 的宽高
			totalHeight += (listItem.getMeasuredHeight()); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		Log.v("params.height2=", String.valueOf(params.height));
		listView.setLayoutParams(params);

	}

	// 监听
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			view.loadUrl("javascript:(function(){"
					+ "var objs = document.getElementsByTagName(\"img\"); "
					+ "for(var i=0;i<objs.length;i++)  " + "{"
					+ "    objs[i].onclick=function()  " + "    {  "
					+ "        window.imagelistner.openImage(this.src);  "
					+ "    }  " + "}" + "})()");
		}

		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			super.onReceivedError(view, errorCode, description, failingUrl);

		}
	}

	/**
	 * 判断是否有网络
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkON(Context context) {
		boolean netSataus = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager.getActiveNetworkInfo() != null) {
			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		return netSataus;
	}

	/**
	 * 获取目录文件大小
	 *
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 *
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	public static File getExternalCacheDir(Context context) {
		// return context.getExternalCacheDir(); API level 8
		// e.g. "<sdcard>/Android/data/<package_name>/cache/"
		return context.getExternalCacheDir();
	}

	/**
	 * 清除缓存目录
	 *
	 * @param dir
	 *            目录
	 * @param curTime
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
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

	/**
	 * 产生范围以内的随机数
	 *
	 * @param start
	 *            最小默认为0
	 * @param end
	 *            最大 默认为100
	 * @return
	 */
	public static int getRandomInteger(int start, int end) {
		Random rand = new Random();
		return rand.nextInt(end) + start;
	}

	/**
	 * 把bitmap同比例放大
	 *
	 * @param scale
	 *            需要放大的倍数
	 * @param bitmap
	 *            源bitmap
	 * @return
	 */
	private static Bitmap scale2Bitmap(float scale, Bitmap bitmap) {
		Bitmap bitmap2;
		if (bitmap != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale); // 长和宽放大缩小的比例
			bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			return bitmap2;
		}
		return null;
	}


	/**
	 * 删除Html标签
	 *
	 * @param inputString
	 * @return
	 */
	public static String htmlRemoveTag(String inputString) {
		if (inputString == null)
			return null;
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;
		try {
			//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			//定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签
			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textStr;// 返回文本字符串
	}

	/**
	 * Spannable内的表情高亮显示
	 *
	 * @param paramContext
	 * @param paramSpannable
	 */
	public static Spannable showContentFaceView(Context paramContext,
										   Spannable paramSpannable) {
		try {
			Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]").matcher(
					paramSpannable);
			while (true) {
				if (!localMatcher.find())
					return paramSpannable;
				int faceStart = localMatcher.start();
				int faceStop = localMatcher.end();
				String str = localMatcher.group(1);

				Integer localInteger = getResId(paramContext, str, "drawable");
				if ((localInteger.intValue() <= 0) || (localInteger == null))
					continue;
				// 修改输入框表情
				BitmapDrawable drawable = (BitmapDrawable) paramContext
						.getResources().getDrawable(localInteger.intValue());
				Bitmap bitmap = scale2Bitmap(0.6f, drawable.getBitmap());
				if (bitmap != null) {
//					paramSpannable.setSpan(new ImageSpan(paramContext, ImageUtil.makeGifTransparent(paramContext, localInteger)),
//							faceStart, faceStop,
//							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					paramSpannable.setSpan(new ImageSpan(paramContext, bitmap),
							faceStart, faceStop, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		} catch (Exception e) {
			Log.d("TSUtils", e.toString());
		}

		return paramSpannable;
	}

	/**
	 * 判断应用是否已经启动
	 * @param context 一个context
	 * @param packageName 要判断应用的包名
	 * @return boolean
	 */
	public static boolean isAppAlive(Context context, String packageName){
		ActivityManager activityManager =
				(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfos
				= activityManager.getRunningAppProcesses();
		for(int i = 0; i < processInfos.size(); i++){
			if(processInfos.get(i).processName.equals(packageName)){
				Log.i(TAG,
						String.format("the %s is running, isAppAlive return true", packageName));
				return true;
			}
		}
		Log.i(TAG,
				String.format("the %s is not running, isAppAlive return false", packageName));
		return false;
	}

}
