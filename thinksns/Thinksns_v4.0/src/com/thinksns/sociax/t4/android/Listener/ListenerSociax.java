package com.thinksns.sociax.t4.android.Listener;

import android.view.View;

/** 
 * 类说明：   监听目标页面的事件监听器
 * @author  wz    
 * @date    2014-12-5
 * @version 1.0
 */
public abstract class ListenerSociax {
	
	/**
	 * 目标页面操作成功的回调
	 */
	public abstract void onTaskSuccess();
	/**
	 * 目标页面操作失败的回调
	 */
	public abstract void onTaskError();
	/**
	 * 目标页面操作取消的回调
	 */
	public abstract void onTaskCancle();
	
	/**
	 * 自定义对话框
	 * @param v
	 * @param witch
	 */
	public void onDialogClick(View v, int witch) {
		
	}
	
}
