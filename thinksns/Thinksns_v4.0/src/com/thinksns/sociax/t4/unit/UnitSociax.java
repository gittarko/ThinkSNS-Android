package com.thinksns.sociax.t4.unit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.BufferType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.Listener.onWebViewLoadListener;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.android.topic.ActivityTopicWeibo;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.model.ModelDiggUser;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.unit.ImageUtil;
import com.thinksns.sociax.unit.SociaxUIUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类说明： sociax工具类
 *
 * @author wz
 * @date 2014-10-23
 * @version 1.0
 */
public class UnitSociax {
	final static String TAG = "TSTAG_UnitSociax";
	private static Context context;
	private Thinksns application;

	public UnitSociax(Context context) {
		this.context = context;
		application = (Thinksns) context.getApplicationContext();
	}

	/**
	 * 实现文本复制功能
	 * add by wangqianzhou
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
	 * add by wangqianzhou
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
	 * Spannable内的表情高亮显示
	 *
	 * @param paramContext
	 * @param paramSpannable
	 */
	public static void showContentFaceView(Context paramContext,
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
				Integer localInteger = getResId(paramContext, str, "drawable");
				if ((localInteger.intValue() <= 0) || (localInteger == null))
					continue;

				// 修改输入框表情
				BitmapDrawable drawable = (BitmapDrawable) context
						.getResources().getDrawable(localInteger.intValue());
				Bitmap bitmap = scale2Bitmap(0.6f, drawable.getBitmap());
				paramSpannable.setSpan(new ImageSpan(paramContext, ImageUtil.makeGifTransparent(bitmap)),
						i, j, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		} catch (Exception e) {
			Log.d("TSUtils", e.toString());
		}
	}

	/**
	 * 内容中的链接和表情高亮显示以及@用户高亮显示，仅仅显示但是没有添加点击效果需要点击效果使用
	 * showContentLinkViewAndLinkMovement方法
	 *
	 * @param context
	 * @param contetn
	 * @return
	 */
	public static SpannableString showContentLintView(Context context,
			String contetn) {
		Pattern pattern = Pattern
				.compile("((https?)://([a-zA-Z0-9\\-.]+)((?:/[a-zA-Z0-9\\-._?,;'+\\&%$=~*!():@\\\\]*)+)?)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)|(\\[(.+?)\\])");
		contetn = SociaxUIUtils.replaceBlank(contetn);
		Matcher matcher = pattern.matcher(contetn);

		List<String> list = new LinkedList<String>();
		while (matcher.find()) {
			if (!list.contains(matcher.group())) {
				if (matcher.group().contains("app=event")) {
					contetn = contetn.replace(matcher.group(), "[活动详情]");
				}
			}
			list.add(matcher.group());
		}
		SpannableString spannableString = new SpannableString(contetn);

		matcher = pattern.matcher(contetn);
		while (matcher.find()) {
			spannableString.setSpan(new ForegroundColorSpan(context
					.getResources().getColor(R.color.black)), matcher.start(),
					matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		showContentFaceView(context, spannableString);
		return spannableString;
	}

	/**
	 *
	 * 根据图片地址，获取图片拍照时候的旋转的度数（横拍/竖拍/倒拍）
	 *
	 * @param absolutePath
	 */
	public int getLocalPicRaotate(String absolutePath) {
		// TODO Auto-generated method stub
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
		float density = metric.density;// 每平方英寸中的像素数
		int densityDpi = metric.densityDpi;// 没英寸中的像素数目
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
	 * @param fontScale
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
	 * @param fontScale
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
	public int getResId(String name, String defType) {
		String packageName = context.getApplicationInfo().packageName;
		return context.getResources().getIdentifier(name, defType, packageName);

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
	 * 生成图片表格
	 *
	 * @param tl_imgs
	 *            表格layout
	 * @param imgUrls
	 *            图片地址集
	 * @param columns
	 *            列数
	 */
	public void appendGridImages(TableLayout tl_imgs, String[] imgUrls,
			int columns) {
		// 先清理一下图片，否则可能会重复
		tl_imgs.removeAllViews();

		if (columns <= 0) {
			return;
		}
		// 设置图片的大小和间距
		android.widget.TableRow.LayoutParams imlp = new android.widget.TableRow.LayoutParams(
				dip2px(context, 80), dip2px(context, 80));
		imlp.setMargins(8, 0, 0, 8);
		// 是否自动填充不足位置的空白
		tl_imgs.setStretchAllColumns(false);
		// 最好做一下异常捕捉
		try {
			List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
			// 生成3行3列的表格
			for (int row = 0; row < imgUrls.length; row = row + columns) {
				TableRow tableRow = new TableRow(context);

				for (int col = row; col < row + columns && col < imgUrls.length; col++) {
					ImageView image = new ImageView(context);
					image.setScaleType(ScaleType.CENTER_CROP);
					// ImageLoader.getInstance().displayImage(imgUrls[col].toString(),
					// image, Thinksns.getOptions());

					application.displayImage(imgUrls[col].toString(), image);

					ModelPhoto md = new ModelPhoto();
					md.setId(col);
					md.setUrl(imgUrls[col].toString());
					photoList.add(md);

					image.setTag(R.id.tag_position, col);
					image.setTag(R.id.tag_object, photoList);
					image.setOnClickListener(new OnClickListener() {
						@SuppressWarnings("unchecked")
						@Override
						public void onClick(View v) {
							Intent i = new Intent(context,
									ActivityViewPager.class);
							i.putExtra("index",
									(v.getTag(R.id.tag_position).toString()));
							i.putParcelableArrayListExtra(
									"photolist",
									(ArrayList<? extends Parcelable>) ((List<ModelPhoto>) (v
											.getTag(R.id.tag_object))));
							context.startActivity(i);
						}
					});
					// 最后添加到table
					tableRow.addView(image, imlp);
				}
				// 新建的TableRow添加到TableLayout
				tl_imgs.addView(tableRow, new TableLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 聊天内的@用户高亮显示
	 *
	 * @param paramContext
	 * @param paramSpannable
	 * @return
	 */
	public SpannableString showContentAtUser(String content) {
		SpannableString spannableString = new SpannableString(content);
		try {
			Matcher localMatcher = Pattern.compile("@[^@]+?(?=[\\s:：(),.。])")
					.matcher(content);

			while (true) {
				if (!localMatcher.find())
					return spannableString;

				spannableString.setSpan(new ForegroundColorSpan(context
						.getResources().getColor(R.color.black)), localMatcher
						.start(), localMatcher.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		} catch (Exception e) {
			Log.d("UnitSociax-->showContentFaceView", e.toString());
		}
		return spannableString;
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
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		Log.v("params.height2=", String.valueOf(params.height));
		listView.setLayoutParams(params);
	}

	/**
	 * webview显示内容
	 *
	 * @param wb_content
	 * @param content
	 */
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	public void appendWebViewContentGift(WebView webView, String content) {

		WebSettings webSetting = webView.getSettings();
		// webview的背景颜色设置透明
		webView.setBackgroundColor(0);
		// 设置图片自适应
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// 添加图片点击事件
		webSetting.setJavaScriptEnabled(true);

		webView.addJavascriptInterface(new JavascriptInterface(context, new ArrayList<ModelPhoto>(0)),
				"imagelistner");
		webView.setWebViewClient(new MyWebViewClient(null));

		//页面适配
		Document doc_Dis = Jsoup.parse(content);
		Elements ele_Img = doc_Dis.getElementsByTag("img");
		if (ele_Img.size() != 0){
			for (Element e_Img : ele_Img) {
				e_Img.attr("style", "width:100%");
			}
		}
		String newHtmlContent=doc_Dis.toString();
		webView.loadDataWithBaseURL(Api.getHost(), newHtmlContent, "text/html", "utf-8", null);
	}

	/**
	 * 帖子webview显示内容
	 *
	 * @param webView
	 * @param content
	 */
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	public void appendWebViewContent(WebView webView, String content, onWebViewLoadListener listener) {
		Document doc = Jsoup.parse(content);
		List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
		parseImageUrls(doc, photoList);
		WebSettings webSetting = webView.getSettings();
		// webview的背景颜色设置透明
		webView.setBackgroundColor(0);
		// 设置图片自适应
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// 添加图片点击事件
		webSetting.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JavascriptInterface(webView.getContext(), photoList),
				"imagelistner");
		webView.setWebViewClient(new MyWebViewClient(listener));
		webView.loadDataWithBaseURL(ApiHttpClient.HOST, doc.toString(), "text/html","utf-8", null);

	}

	/**
	 * 帖子webview显示内容
	 *
	 * @param webView
	 * @param content
	 */
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	public void appendWebViewContent(WebView webView, String content, onWebViewLoadListener listener,final ProgressBar pb_bar) {
		Document doc = Jsoup.parse(content);
		List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
		parseImageUrls(doc, photoList);
		WebSettings webSetting = webView.getSettings();
		// webview的背景颜色设置透明
		webView.setBackgroundColor(0);
		// 设置图片自适应
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// 添加图片点击事件
		webSetting.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JavascriptInterface(webView.getContext(), photoList),
				"imagelistner");
		webView.setWebViewClient(new MyWebViewClient(listener));
		webView.loadDataWithBaseURL(ApiHttpClient.HOST, doc.toString(), "text/html","utf-8", null);

		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
//				if (newProgress == 100) {
//					pb_bar.setVisibility(View.INVISIBLE);
//
//					Log.v("pb","/newProgress == 100/");
//				} else {
//					if (View.INVISIBLE == pb_bar.getVisibility()) {
//						pb_bar.setVisibility(View.VISIBLE);
//					}
//					pb_bar.setProgress(newProgress);
//
//					Log.v("pb","/newProgress/"+newProgress);
//				}
//				super.onProgressChanged(view, newProgress);
				pb_bar.setProgress(newProgress * 100);
			}
		});
	}

	public class JavascriptInterface {
		private Context context;
		private List<ModelPhoto> photoList;

		public JavascriptInterface(Context context, List<ModelPhoto> photoList) {
			this.context = context;
			this.photoList = photoList;
		}

		@android.webkit.JavascriptInterface
		public void openImage(String img) {
			int index = 0;
			if(photoList.size() == 0) {
				ModelPhoto p = new ModelPhoto();
				p.setUrl(img);
				p.setMiddleUrl(img);
				p.setOriUrl(img);
				photoList.add(p);
			}else {
				for (ModelPhoto photo : photoList) {
					if (photo.getUrl().equals(img))
						break;
					index++;
				}
			}

//			Intent i = new Intent(context,
//					ActivityViewPager.class);
//			i.putExtra("index", index);
//			i.putParcelableArrayListExtra(
//					"photolist",
//					(ArrayList<? extends Parcelable>) photoList);
//			context.startActivity(i);
			Bundle bundle = new Bundle();
			bundle.putInt("index", index);
			bundle.putParcelableArrayList("photolist", (ArrayList<? extends Parcelable>)photoList);
			ActivityStack.startActivity((Activity)context, ActivityViewPager.class, bundle);
		}
	}

	/**
	 * 适配html
	 * @param htmltext
	 * @return
	 */
	private String adaptationHTML(String htmltext){
		Document doc = Jsoup.parse(htmltext);
		Elements elements = doc.getElementsByTag("img");
		for (Element element : elements) {
			element.attr("width", "100%").attr("height", "auto");
			String src = element.attr("src");
			if(src != null && src.startsWith("/")) {
				String newSrc = "http://" + ApiHttpClient.HOST + src;
				element.attr("src", newSrc);
			}
		}

		return doc.toString();
	}

	public static void parseImageUrls(Document doc, final List<ModelPhoto> photos) {
		Elements elements = doc.getElementsByTag("img");
		for (Element element : elements) {
			String src = element.attr("src");
			if(src != null && src.startsWith("/")) {
				String newSrc = "http://" + ApiHttpClient.HOST + src;
				ModelPhoto photo = new ModelPhoto();
				photo.setUrl(newSrc);
				photo.setMiddleUrl(newSrc);
				photo.setOriUrl(newSrc);
				photos.add(photo);
				element.attr("src", newSrc);
			}
		}
	}

	// 监听
	public class MyWebViewClient extends WebViewClient {
		private onWebViewLoadListener listener;

		public MyWebViewClient(onWebViewLoadListener listener) {
			setOnLoadListener(listener);
		}

		public void setOnLoadListener(onWebViewLoadListener listener) {
			this.listener = listener;
		}

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
			if(listener != null)
				listener.onPageFinished();

		}

		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageStarted(view, url, favicon);
			if(listener != null)
				listener.onPageStarted();
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

	/***************************************************************/
	/**
	 * 计算缓存的大小,
	 *
	 * @return
	 */
	public String getCacheSize() {
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = context.getApplicationContext().getFilesDir();// /data/data/package_name/files
		File cacheDir = context.getCacheDir();// /data/data/package_name/cache
		fileSize += getDirSize(filesDir);
		fileSize += getDirSize(cacheDir);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCacheDir = getExternalCacheDir(context);// "<sdcard>/Android/data/<package_name>/cache/"
			fileSize += getDirSize(externalCacheDir);
		}
		if (fileSize > 0)
			cacheSize = formatFileSize(fileSize);
		return cacheSize;
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

	// 在项目中经常会使用到WebView 控件,当加载html
	// 页面时,会在/data/data/package_name目录下生成database与cache 两个文件夹。请求的url
	// 记录是保存在WebViewCache.db,而url 的内容是保存在WebViewCache 文件夹下
	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		// 清除数据缓存
		clearCacheFolder(context.getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(context.getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(getExternalCacheDir(context),
					System.currentTimeMillis());
		}
	}

	/**
	 * 清除缓存目录
	 *
	 * @param dir
	 *            目录
	 * @param numDays
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
	 * 设置网页连接、@用户、话题以及表情包高亮显示，并且带有点击事件 wz QQ 37717239 2015.1.31
	 *
	 * @param weiboContent
	 *            待显示的内容
	 * @param textView
	 *            需要显示内容的textview
	 * @return 使用方法直接new UnitSociax unit，然后调用此方法unit.dealWeiboContent
	 */
	public static SpannableStringBuilder showContentLinkViewAndLinkMovement(
			String weiboContent, TextView textView) {
		Pattern pattern = Pattern
				.compile("((https?)://([a-zA-Z0-9\\-.]+)((?:/[a-zA-Z0-9\\-._?,;'+\\&%$=~*!():@\\\\]*)+)?)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)|(\\[(.+?)\\])");
		weiboContent = SociaxUIUtils.filterHtml(weiboContent);
		Matcher matcher = pattern.matcher(weiboContent);
		List<String> list = new LinkedList<String>();
		while (matcher.find()) {
			if (matcher.group().contains("http")) {
				// 网站链接
				weiboContent = weiboContent.replace(matcher.group(), "访问网络+");
			}
			list.add(matcher.group());
		}

		textView.setMovementMethod(new LinkMovementMethod() {
			@Override
			public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
				boolean b = super.onTouchEvent(widget, buffer, event);
				if(!b && event.getAction() == MotionEvent.ACTION_UP){
					ViewParent parent = widget.getParent();	//处理widget的父控件点击事件
					if (parent instanceof ViewGroup) {
						return ((ViewGroup) parent).performClick();
					}
				}
				return b;

			}
		});
		SpannableString spanStr = new SpannableString(weiboContent);
		SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
		if (list.size() > 0) {
			try {
				boolean hasShowFaceView = false;// 用来标记是否执行过表情全部替换
				for (int i = 0; i < list.size(); i++) {
					final String name = list.get(i);
					// 起点，如果是http需要设置起点name=访问网络+
					final int start = weiboContent.indexOf((name
							.contains("http") ? "访问网络+" : name));
					if (name.contains("[") && !name.equals("访问网络+")) {
						// 如果带有[并且不是网站链接，则为表情，
						if (hasShowFaceView) {
							// 如果还没有执行过表情全替换hasShowFaceView，则执行替换表情,否则跳过
							continue;
						}

						Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]")
								.matcher(ssb);
						while (true) {
							if (!localMatcher.find())// 没找到表情
								break;
							if (localMatcher.group().equals("访问网络+")) {// 网站链接，跳过，执行全体换的时候又重新替换一次，可能其他地方会有网站链接
								continue;
							}
							int faceStart = localMatcher.start();
							int faceStop = localMatcher.end();
							String str = localMatcher.group(1);
							Integer localInteger = null;
							if ((localInteger = getResId(context, str, "drawable")) <= 0) {// 如果没有找到，跳过
								continue;
							}

							// 设置表情
							BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(localInteger);
							Bitmap bitmap = scale2Bitmap(0.6f, drawable.getBitmap());
							ssb.setSpan(new ImageSpan(context, ImageUtil.makeGifTransparent(bitmap)),
									faceStart, faceStop,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						// 最后标记一下已经替换全部表情
						hasShowFaceView = true;
					} else {
						// 其他内容 设置点击Span，注意获取clickbale的时候需要判断是不是网络链接
						ssb.setSpan(getClickableSpanByStr(name), start, start
								+ (name.contains("http") ? "访问网络+".length()
										: name.length()), 0);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		textView.setText(ssb, BufferType.SPANNABLE);

		return ssb;
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
			Log.i("bitmap", " bitmap.getWidth()=" + bitmap.getWidth());
			bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			Log.i("bitmap", "bitmap2.getWidth()=" + bitmap2.getWidth());
			return bitmap2;
		}
		return null;
	}

	/**
	 * 处理@、话题、链接点击事件，根据首字母来判断点击内容
	 */
	public static ClickableSpan getClickableSpanByStr(final String value) {
		char type = value.charAt(0);
		Log.v(TAG, "typeClick1 " + type + "  value=" + value);
		switch (type) {
		case '@':// @开头的@用户
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					String uname = value.substring(1, value.length());
					Log.v(TAG, "typeClick2 " + uname);
					Intent intent = new Intent(context,
							ActivityUserInfo_2.class);
					Bundle data = new Bundle();
					data.putString("uname", uname);
					intent.putExtras(data);
					context.startActivity(intent);
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setColor(Color.argb(255, 15, 129, 217));
					ds.setUnderlineText(false);
				}
			};
		case '#':// #开头的话题
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					String topic = value.substring(1, value.length() - 1);
					Log.v(TAG, "typeClick3 " + topic);
					Intent intent = new Intent(context,
							ActivityTopicWeibo.class);
					Bundle data = new Bundle();
					data.putString("topic_name", topic);
					intent.putExtras(data);
					context.startActivity(intent);
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setColor(Color.argb(255, 15, 129, 217));
					ds.setUnderlineText(false);
				}
			};
		case 'h':// http开头的超链接
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {

					// Intent intent = new Intent();
					// intent.setAction("android.intent.action.VIEW");
					// Uri url = Uri.parse(value);
					// intent.setData(url);
					// context.startActivity(intent);

					Uri url = Uri.parse(value);
					Intent intent = new Intent(context, NetActivity.class);
					intent.putExtra("url", url.toString());
					context.startActivity(intent);
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setColor(Color.argb(255, 15, 129, 217));
					ds.setUnderlineText(false);
				}
			};
		default:
			String unknow = value.substring(1, value.length());
			Log.v(TAG, "typeClick " + unknow);
			return null;
		}
	}

	// 这是新添加了字体点击 ，因为要求回复的@ 去掉，而且颜色需要改变，只能從新寫了 author qcj
	public ClickableSpan getClickableSpanByStrReplay(final String value) {
		char type = value.charAt(0);
		Log.v(TAG, "typeClick1 " + type + "  value=" + value);
		switch (type) {
		case '@':
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					String uname = value.substring(1, value.length());
					Log.v(TAG, "typeClick2 " + uname);
					Intent intent = new Intent(context,
							ActivityUserInfo_2.class);
					Bundle data = new Bundle();
					data.putString("uname", uname);
					intent.putExtras(data);
					context.startActivity(intent);
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setColor(0xff3C3C3C);
					ds.setUnderlineText(false);
				}
			};
		case '#':// #开头的话题
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					String topic = value.substring(1, value.length() - 1);
					Log.v(TAG, "typeClick3 " + topic);
					Intent intent = new Intent(context,
							ActivityTopicWeibo.class);
					Bundle data = new Bundle();
					data.putString("topic_name", topic);
					intent.putExtras(data);
					context.startActivity(intent);
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setColor(Color.GRAY);
					ds.setUnderlineText(false);
				}
			};
		case 'h':// http开头的超链接
			return new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					// Intent intent = new Intent();
					// intent.setAction("android.intent.action.VIEW");
					// Uri url = Uri.parse(value);
					// intent.setData(url);
					// context.startActivity(intent);

					Uri url = Uri.parse(value);
					Intent intent = new Intent(context, NetActivity.class);
					intent.putExtra("url", url.toString());
					context.startActivity(intent);
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setColor(Color.GRAY);
					ds.setUnderlineText(false);
				}
			};
		default:
			String unknow = value.substring(1, value.length());
			Log.v(TAG, "typeClick " + unknow);
			return null;
		}
	}

	/**
	 * 添加用户组标签
	 *
	 * @param ugroupUrlList
	 *            用户标签组
	 * @param ll_user_group
	 */
	public void addUserGroup(List<String> ugroupUrlList,
			LinearLayout ll_user_group) {
		ll_user_group.removeAllViews();
		ImageView smView = new ImageView(context);

		// 原来是与本地的进行对比，使用本地图片
		// smView.setImageResource(getResId(ugroupUrlList.get(i).substring(
		// ugroupUrlList.get(i).lastIndexOf("/")+1,
		// ugroupUrlList.get(i).length()-4),"drawable"));

		if (ugroupUrlList.size() != 0) {
			application.displayImage(ugroupUrlList.get(0), smView);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					UnitSociax.dip2px(context, context.getResources().getDimension(R.dimen.other_usergroup)),
					UnitSociax.dip2px(context, context.getResources().getDimension(R.dimen.other_usergroup)));
			lp.setMargins(0, 0, 0, 0);
			smView.setLayoutParams(lp);

			ll_user_group.addView(smView);
		}
	}

	public SpannableStringBuilder showContentLinkViewAndLinkMovementchat(
			final String username, String weiboContent, TextView textView, final int textSize) {
		//初始化文本控件
		textView.setText("");
		final float size = UnitSociax.dip2px(context, textSize);
		if (username != null) {
			//处理评论人姓名颜色
			//设置可点击用户名
			SpannableString spanString = new SpannableString(username);
			spanString.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					Intent intent = new Intent(context,
							ActivityUserInfo_2.class);
					Bundle data = new Bundle();
					data.putString("uname", username);
					intent.putExtras(data);
					context.startActivity(intent);
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					//设置用户名的颜色
					ds.setColor(0xff3C3C3C);
					ds.setTextSize(size);
					ds.setUnderlineText(false);
				}
			}, 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			textView.append(spanString);
		}

		// ----------------------------
		if (weiboContent != null) {
			SpannableStringBuilder ssbcontent = getDealedString(weiboContent, textView);
			if (ssbcontent.toString().contains("回复@")) {
				int index = ssbcontent.toString().indexOf("回复@");
				SpannableString replay = new SpannableString(" 回复 ");
				//改变"回复"文字颜色
				ForegroundColorSpan replayspan = new ForegroundColorSpan(
						Color.GRAY);
				replay.setSpan(replayspan, 0, replay.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				//将"回复@"替换成"回复 "
				ssbcontent.replace(index, index + 3, replay);
			} else if (ssbcontent.toString().contains("delete@")) {
				int index = ssbcontent.toString().indexOf("delete@");
				SpannableString replay = new SpannableString("");
				ForegroundColorSpan replayspan = new ForegroundColorSpan(
						Color.GRAY);
				replay.setSpan(replayspan, 0, replay.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssbcontent.replace(index, index + 7, replay);
//				textView.append(ssbcontent);
			} else {
				//
//				textView.append(ssbcontent);
			}

			//设置尾串的字体大小
			AbsoluteSizeSpan span = new AbsoluteSizeSpan((int)size);
			SpannableString tailSpan = new SpannableString(ssbcontent);
			tailSpan.setSpan(span, 0, tailSpan.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			textView.append(tailSpan);

			return ssbcontent;
		}

		return null;
	}

	// 获取处理过的字体 qcj
	private SpannableStringBuilder getDealedString(String weiboContent,
			TextView textView) {
		Pattern pattern = Pattern
				.compile("((https?)://([a-zA-Z0-9\\-.]+)((?:/[a-zA-Z0-9\\-._?,;'+\\&%$=~*!():@\\\\]*)+)?)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)|(\\[(.+?)\\])");
		weiboContent = SociaxUIUtils.filterHtml(weiboContent);
		Matcher matcher = pattern.matcher(weiboContent);
		List<String> list = new LinkedList<String>();
		while (matcher.find()) {
			if (matcher.group().contains("http")) {// 网站链接
				weiboContent = weiboContent.replace(matcher.group(), "访问网络+");
			}
			list.add(matcher.group());
		}
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		// 设置了link之后要重写一下自己的touch事件，如果touch到点击部分，则执行点击，否则执行textview的touch事件
		textView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView widget = (TextView) v;
				CharSequence text = widget.getText();
				Spannable buffer = Spannable.Factory.getInstance()
						.newSpannable(text);
				int action = event.getAction();
				if (action == MotionEvent.ACTION_UP
						|| action == MotionEvent.ACTION_DOWN) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					x -= widget.getTotalPaddingLeft();
					y -= widget.getTotalPaddingTop();
					x += widget.getScrollX();
					y += widget.getScrollY();
					Layout layout = widget.getLayout();
					int line = layout.getLineForVertical(y);
					int off = layout.getOffsetForHorizontal(line, x);
					ClickableSpan[] link = buffer.getSpans(off, off,
							ClickableSpan.class);
					if (link.length != 0) {// 如果textview包含点击事件并且刚好点击了带有点击事件的部分，则执行点击，并且return，否则return
											// TextView的点击
						if (action == MotionEvent.ACTION_UP) {// 如果是离开屏幕，则点击
							// Log.v(TAG, "textview link[0].onClick(widget)");
							link[0].onClick(widget);
							return true;
						}
					}
				}
				return false;
			}
		});

		SpannableString spanStr = new SpannableString(weiboContent);
		SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
		if (list.size() > 0) {
			try {
				boolean hasShowFaceView = false;// 用来标记是否执行过表情全部替换
				for (int i = 0; i < list.size(); i++) {
					final String name = list.get(i);
					// 起点，如果是http需要设置起点name=访问网络+
					final int start = weiboContent.indexOf((name
							.contains("http") ? "访问网络+" : name));
					if (name.contains("[") && !name.equals("访问网络+")) {
						// 如果带有[并且不是网站链接，则为表情，
						if (hasShowFaceView) {
							// 如果还没有执行过表情全替换hasShowFaceView，则执行替换表情,否则跳过
							continue;
						}

						Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]")
								.matcher(ssb);
						while (true) {
							if (!localMatcher.find())// 没找到表情
								break;
							if (localMatcher.group().equals("访问网络+")) {// 网站链接，跳过，执行全体换的时候又重新替换一次，可能其他地方会有网站链接
								continue;
							}
							int faceStart = localMatcher.start();
							int faceStop = localMatcher.end();
							String str = localMatcher.group(1);
							// 获取表情
							Integer localInteger = getResId(context, str, "drawable");
							if ((localInteger.intValue() <= 0)
									|| (localInteger == null))
								continue;
							// 设置表情
							BitmapDrawable drawable = (BitmapDrawable) context
									.getResources().getDrawable(localInteger.intValue());
							Bitmap bitmap = scale2Bitmap(0.6f, drawable.getBitmap());
							ssb.setSpan(
									new ImageSpan(context, ImageUtil.makeGifTransparent(bitmap)),
									faceStart,
									faceStop,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						// 最后标记一下已经替换全部表情
						hasShowFaceView = true;
					} else {
						// 其他内容 设置点击Span，注意获取clickbale的时候需要判断是不是网络链接
						ssb.setSpan(getClickableSpanByStrReplay(name),
								start,start + (name.contains("http") ? "访问网络+"
												.length() : name.length()), 0);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		AbsoluteSizeSpan span1 = new AbsoluteSizeSpan(10, true);
		ssb.setSpan(span1, 0, spanStr.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ssb;
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
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
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
	 * 修改键盘功能
	 */
	public static void setSoftKeyBoard(EditText editText, final Context context) {
		editText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {//修改回车键功能
					// 先隐藏键盘
					((InputMethodManager) ((Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
	}
}
