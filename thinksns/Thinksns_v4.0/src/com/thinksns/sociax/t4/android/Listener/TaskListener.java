package com.thinksns.sociax.t4.android.Listener;

public abstract class TaskListener {
	/**
	 * 任务执行成功回调接口
	 */
	public abstract void onSuccess();
	/**
	 * 任务执行错误回调接口
	 */
	public abstract void onError();
	/**
	 * 任务执行取消回调接口
	 */
	public abstract void onCancel();
}
