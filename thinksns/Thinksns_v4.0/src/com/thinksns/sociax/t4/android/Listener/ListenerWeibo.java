package com.thinksns.sociax.t4.android.Listener;
/** 
 * 类说明：   
 * @author  wz    
 * @date    2014-12-9
 * @version 1.0
 */
public abstract class ListenerWeibo {
	/**
	 * 微博内容被修改
	 */
	public abstract void onWeiboChange();
	/**
	 * 微博被删除
	 */
	public abstract void onWeiboRemove();
	/**
	 * 对微博没有任何操作或者修改
	 */
	public abstract void onCancle();
	/**
	 * 微博被转发
	 */
	public abstract void onWeiboTransport();
}
