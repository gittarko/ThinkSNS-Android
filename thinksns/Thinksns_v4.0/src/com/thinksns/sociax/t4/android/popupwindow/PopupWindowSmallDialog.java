package com.thinksns.sociax.t4.android.popupwindow;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.thinksns.sociax.t4.android.Listener.TaskListener;
import com.thinksns.sociax.android.R;

public class PopupWindowSmallDialog {
	AlertDialog.Builder builder ;
	AlertDialog dialog = null;
	Context context;
	TextView tv_title,tv_tips,tv_ok,tv_cancle;
	LayoutInflater inflater;
	View view;
	TaskListener listener;
	
	public TaskListener getTaskListener() {
		return listener;
	}
	
	public void setTaskListener(TaskListener listener) {
		this.listener = listener;
	}
	
	/**
	 * 提醒框
	 * @param context
	 * @param title 标题，如果为null 则不显示标题
	 * @param tips 提示信息，如果为null，则不显示提示信息
	 * @param str_cancel 第一个按钮取消字符，如果为null，则显示确定
	 * @param str_ok 第二个按钮确认字符，如果为null，则显示再看看
	 * @return 
	 */
	public PopupWindowSmallDialog(Context context,String title,String tips,
								  String str_cancel, String str_ok) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.componeng_popupdialog_common, null);
		
		builder = new Builder(context);
		builder.setView(view);

		tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_cancle = (TextView) view.findViewById(R.id.tv_cancel);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_tips = (TextView) view.findViewById(R.id.tv_tips);
		
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
		if(!str_ok.trim().equals("")){
			tv_ok.setText(str_ok);
		}
		if(!str_cancel.trim().equals("")){
			tv_cancle.setText(str_cancel);
		}

		setOnClickListener();
		dialog = builder.create();
	}
	
	private void setOnClickListener() {
		tv_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(listener!=null){
					listener.onSuccess();
				}
			}
		});
		
		tv_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(listener!=null){
					listener.onCancel();
				}
			}
		});
	}
	public void show(){
		if(dialog !=null) {
			dialog.show();
			Window window = dialog.getWindow();

		}
	}

	public void dimiss() {
		if(dialog != null)
			dialog.dismiss();
	}
}
