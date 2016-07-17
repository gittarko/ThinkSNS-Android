package com.thinksns.sociax.t4.android.popupwindow;

import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/** 
 * 类说明：   提示信息
 * @author  wz    
 * @date    2015-2-5
 * @version 1.0
 */
public class PopupWindowDialog1 {
	AlertDialog.Builder builder ;
	Dialog dialog = null;
	Context context;
	TextView tv_title,tv_tips,tv_ok,tv_cancle;
	ListenerSociax listener;
	LayoutInflater inflater;
	View view;
	public ListenerSociax getListenerSociax() {
		return listener;
	}
	public void setListenerSociax(ListenerSociax listener) {
		this.listener = listener;
	}
	/**
	 * 提醒框
	 * @param context
	 * @param title 标题，如果为null 则不显示标题
	 * @param tips 提示信息，如果为null，则不显示提示信息
	 * @param str_first 第一个按钮字符，如果为null，则显示确定
	 * @param str_second 第二个按钮字符，如果为null，则显示再看看
	 * @return 
	 */
	public PopupWindowDialog1(Context context,String title,String tips,String str_first,String str_second) {
		this.context=context;
		this.inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.componeng_popupdialog_common, null);
		dialog = new Dialog(context, R.style.my_dialog);
		dialog.addContentView(view, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		dialog.setCanceledOnTouchOutside(true);
//		builder.setView(view);
		tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_cancle=(TextView) view.findViewById(R.id.tv_cancel);
		tv_title=(TextView) view.findViewById(R.id.tv_title);
		tv_tips=(TextView) view.findViewById(R.id.tv_tips);
		
		if(title.trim().equals("")){
			tv_title.setVisibility(View.GONE);
		}else{
			tv_title.setVisibility(View.VISIBLE);
			tv_title.setText(title);
		}
		if(tips.trim().equals("")){
			tv_tips.setVisibility(View.GONE);
		}else{
			tv_tips.setVisibility(View.VISIBLE);
			tv_tips.setText(tips);
		}
		if(!str_second.trim().equals("")){
			tv_ok.setText(str_first);
		}
		if(!str_first.trim().equals("")){
			tv_cancle.setText(str_second);
		}
		
		setOnClickListener();

	}
	
	/**
	 * 自定义菜单栏
	 * @param context
	 * @param content
	 */
	private LinearLayout ll_content;
	public PopupWindowDialog1(Context context, String[] content, boolean showCancel) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.componeng_dialog_common, null);
		builder = new Builder(context);
		ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
		
		if(content != null && content.length > 0) {
			for(int i=0; i<content.length; i++) {
				TextView tv = new TextView(context);//(TextView) LayoutInflater.from(context).inflate(R.layout.textview_common, null);
				tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				int top = UnitSociax.dip2px(context, 10);
				int bottom = UnitSociax.dip2px(context, 10);
				tv.setPadding(0, top, 0, bottom);
				tv.setGravity(Gravity.CENTER);
//				tv.setBackgroundColor(0xffffffff);
				tv.setText(content[i]);
				//设置字体大小
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				//设置字体颜色
				tv.setTextColor(context.getResources().getColor(R.color.black));
				tv.setTag(i);
				//添加监听事件
				tv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(listener != null) {
							listener.onDialogClick(v, (Integer)v.getTag());
						}
					}
				});
				
				ll_content.addView(tv);
			}
		}else {
			view.setVisibility(View.GONE);
		}
		
		if(showCancel) {
			tv_cancle = (TextView) view.findViewById(R.id.tv_cancel);
			tv_cancle.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					if(listener != null) {
						listener.onTaskCancle();
					}
				}
			});
			
			tv_cancle.setVisibility(View.VISIBLE);
		}
		
		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		
	}
	
	private void setOnClickListener() {
		tv_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(listener!=null){
					listener.onTaskSuccess();
				}
			}
		});
		tv_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(listener!=null){
					listener.onTaskCancle();
				}
			}
		});
	}

	/**
	 * 显示dialog窗口
	 */
	public void show() {
		if(!dialog.isShowing()) {
			dialog.show();
			Window window = dialog.getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.width = UnitSociax.getWindowWidth(context);
			params.height = UnitSociax.getWindowHeight(context);
			params.gravity = Gravity.CENTER;
			window.setAttributes(params);
		}
	}

	public void dimiss() {
		if(dialog != null)
			dialog.dismiss();
	}
	 
}
