package com.thinksns.tschat.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.thinksns.tschat.R;
import com.thinksns.tschat.listener.OnPopupWindowClickListener;

/**
 * 类说明： 公共的弹出窗口
 * @author wz
 * @date 2014-12-1
 * @version 1.0
 */
public class PopupWindowCommon extends PopupWindow {
	private Context context;
	private int type = 1;
	private TextView bt_first, bt_second;
	private TextView tv_tips;

	public PopupWindowCommon(Context mContext, View parent , String tips) {
		this.context = mContext;
		View view = View.inflate(mContext, R.layout.ts_chat_pupupwindow_common, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_ins));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_bottom_in_2));
		ll_popup.getBackground().setAlpha(160);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		tv_tips = (TextView) view.findViewById(R.id.item_popupwindows_camera);
		bt_first = (TextView) view.findViewById(R.id.item_popupwindows_Photo);
		bt_second = (TextView) view.findViewById(R.id.item_popupwindows_cancel);

		tv_tips.setText(tips);
		bt_first.setText("确定");
		bt_second.setText("再想想");

		bt_first.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (listner != null)
					listner.firstButtonClick();
				dismiss();
			}
		});
		bt_second.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (listner != null)
					listner.secondButtonClick();
				dismiss();
			}
		});
	}
	/**
	 * 各种页面弹出窗口
	 * 
	 * @param mContext
	 * @param parent
	 * @param tips
	 * @param bt1_str
	 * @param bt2_str
	 */
	public PopupWindowCommon(Activity mContext, View parent, String tips,
							 String bt1_str, String bt2_str) {
		this.context = mContext;
		View view = View.inflate(mContext, R.layout.ts_chat_pupupwindow_common, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_ins));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_bottom_in_2));
		ll_popup.getBackground().setAlpha(160);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();
		tv_tips = (TextView) view.findViewById(R.id.item_popupwindows_camera);
		bt_first = (TextView) view.findViewById(R.id.item_popupwindows_Photo);
		bt_second = (TextView) view.findViewById(R.id.item_popupwindows_cancel);

		tv_tips.setText(tips);
		bt_first.setText(bt1_str);
		bt_second.setText(bt2_str);

		bt_first.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (listner != null)
					listner.firstButtonClick();
				dismiss();
			}
		});
		bt_second.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (listner != null)
					listner.secondButtonClick();
				dismiss();
			}
		});
	}

	public OnPopupWindowClickListener listner;

	public void setOnPopupWindowClickListener(OnPopupWindowClickListener listner) {
		this.listner = listner;
	}
}