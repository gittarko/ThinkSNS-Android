package com.thinksns.sociax.t4.unit;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.sociax.android.R;

/**
 * 类说明： listview数据操作类
 * 
 * @author lhz
 * @date 2014-9-1
 * @version 1.0
 */
public class UtilsListViewData {

	// weibo content
	public static void setWeiboContent(Context paramContext, TextView tv,
			String content) {
		try {
			Pattern pattern = Pattern
					.compile("((https?)://([a-zA-Z0-9\\-.]+)((?:/[a-zA-Z0-9\\-._?,;'+\\&%$=~*!():@\\\\]*)+)?)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)");

			String contetn = SociaxUIUtils.filterHtml(content);
			Matcher matcher = pattern.matcher(contetn);
			SpannableString spannableString = new SpannableString(contetn);
			while (matcher.find()) {
				spannableString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 54, 92, 124)),
						matcher.start(), matcher.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			SociaxUIUtils.highlightContent(paramContext, spannableString);
			tv.setText(spannableString);
		} catch (Exception ex) {
			tv.setText(content.trim());
		}
	}

	// 转发微博content
	public static TextView setWeiboTransportContent(Context context, ModelWeibo weibo) {
		TextView tv_content = new TextView(context);
		tv_content.setPadding(0, 0, 0, 10);
		String content = SociaxUIUtils.filterHtml(weibo.getContent());
		content = SociaxUIUtils.replaceBlank(content);
		if (content.length() > 140) {
			content = content.substring(0, 140);
		}
		String patternStr = ('@' + weibo.getUsername() + ": " + content);
		try {
			Pattern pattern = Pattern
					.compile("((https?)://([a-zA-Z0-9\\-.]+)((?:/[a-zA-Z0-9\\-._?,;'+\\&%$=~*!():@\\\\]*)+)?)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)|(\\[(.+?)\\])");

			Matcher matcher = pattern.matcher(patternStr);

			List<String> list = new LinkedList<String>();
			while (matcher.find()) {
				if (!list.contains(matcher.group())) {
					if (matcher.group().contains("app=event")) {
						patternStr = patternStr.replace(matcher.group(),
								"[活动详情]");
					}
				}
				list.add(matcher.group());
			}
			SpannableString spannableString = new SpannableString(patternStr);
			matcher = pattern.matcher(patternStr);
			while (matcher.find()) {
				spannableString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 54, 92, 124)),
						matcher.start(), matcher.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			SociaxUIUtils.highlightContent(context, spannableString);
			tv_content.setText(spannableString);
		} catch (Exception ex) {
			tv_content.setText(weibo.getContent());
		}
		tv_content.setTextColor(context.getResources().getColor(
				R.color.tranFontColor));
		tv_content.setTextSize(14);
		return tv_content;
	}

	/**
	 * 评论内容
	 * 
	 * @param paramContext
	 * @param tv
	 * @param content
	 */
	public static void setCommentContent(Context paramContext, TextView tv,
			String content) {
		try {
			Pattern pattern = Pattern
					.compile("((https?)://([a-zA-Z0-9\\-.]+)((?:/[a-zA-Z0-9\\-._?,;'+\\&%$=~*!():@\\\\]*)+)?)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)");

			String contetn = SociaxUIUtils.filterHtml(content);
			Matcher matcher = pattern.matcher(contetn);
			SpannableString spannableString = new SpannableString(contetn);
			while (matcher.find()) {
				spannableString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 54, 92, 124)),
						matcher.start(), matcher.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			spannableString.setSpan(
					new ForegroundColorSpan(Color.argb(255, 54, 92, 124)), 0,
					contetn.indexOf(":"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			SociaxUIUtils.highlightContent(paramContext, spannableString);
			tv.setText(spannableString);
		} catch (Exception ex) {
			tv.setText(content.trim());
		}
	}

	/**
	 * 评论列表显示转发微博
	 * 
	 * @param context
	 * @param tv
	 * @param weibo
	 * @return
	 */
	// 转发微博content
	public static void setT4WeiboTransportContent(Context context, TextView tv,
			ModelWeibo weibo) {
		String content = SociaxUIUtils.filterHtml(weibo.getContent());
		content = SociaxUIUtils.replaceBlank(content);
		if (content.length() > 140) {
			content = content.substring(0, 140);
		}
		if(weibo.getUsername().equals("")||weibo.getUsername().equals("null")){
			tv.setText("内容已经删除");
			tv.setTextColor(context.getResources().getColor(R.color.red));
			tv.setTextSize(14);
		}
		String patternStr = ('@' + weibo.getUsername() + ": " + content);
		try {
			Pattern pattern = Pattern
					.compile("((https?)://([a-zA-Z0-9\\-.]+)((?:/[a-zA-Z0-9\\-._?,;'+\\&%$=~*!():@\\\\]*)+)?)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)|(\\[(.+?)\\])");
			Matcher matcher = pattern.matcher(patternStr);
			List<String> list = new LinkedList<String>();
			while (matcher.find()) {
				if (!list.contains(matcher.group())) {
					if (matcher.group().contains("app=event")) {
						patternStr = patternStr.replace(matcher.group(),
								"[活动详情]");
					}
				}
				list.add(matcher.group());
			}
			SpannableString spannableString = new SpannableString(patternStr);
			matcher = pattern.matcher(patternStr);
			while (matcher.find()) {
				spannableString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 54, 92, 124)),
						matcher.start(), matcher.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			SociaxUIUtils.highlightContent(context, spannableString);
			tv.setText(spannableString);
		} catch (Exception ex) {
			tv.setText(weibo.getContent());
		}
		tv.setTextColor(context.getResources().getColor(R.color.tranFontColor));
		tv.setTextSize(14);
	}

}
