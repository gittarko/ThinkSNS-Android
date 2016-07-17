package com.thinksns.sociax.t4.android.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-20
 * @version 1.0
 */
public class PopupWindowAddBackList extends PopupWindow {
	private Context context;
	private int type = 1;// 1.添加到黑名单；2取消黑名单

	/**
	 * 他人主页下点击之后加入黑名单
	 * 
	 * @param mContext
	 * @param parent
	 */
	public PopupWindowAddBackList(Activity mContext, View parent, int type) {
		this.context = mContext;
		this.type = type;
		View view = View.inflate(mContext, R.layout.popupwindows_addbacklist,
				null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_up_in));
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();
		//提示框
		LinearLayout ll_tips = (LinearLayout) view.findViewById(R.id.ll_tips);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);

		if (type == 2) {
			ll_tips.setVisibility(View.GONE);
			bt2.setText("解除黑名单");
		}
		
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addBackListThread();
				dismiss();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});

	}

	private final int TAKE_PICTURE = 0x000012;
	private String path = "";
	/**
	 * 添加，移除黑名单
	 */
	public void addBackListThread() {
//		if (type == 1) {
//			((ActivityUserInfo_2) context).addBackListThread();
//		} else {
//
//			((ActivityUserInfo_2) context).delBackListThread();
//		}
	}
}
