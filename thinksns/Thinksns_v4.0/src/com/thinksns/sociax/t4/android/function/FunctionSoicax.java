package com.thinksns.sociax.t4.android.function;

import android.content.Context;
import android.os.Handler;

import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;

/**
 * 类说明： 所有功能的基类
 * 如果需要用到异步线程处理（推荐耗时操作/网络请求等用异步线程，两个初始化initHandler内设置Handler，使用handlerActivity执行耗时操作，使用UiHandler执行更新ui）
 * 
 * @author wz
 * @date 2014-10-30
 * @version 1.0
 */
public abstract class FunctionSoicax {
	protected Worker thread;
	protected Context context;
	protected static Handler handlerUI;// 执行更新UI的handler；
	protected Handler handlerActivity;// 执行耗时操作的handler
	protected  static  Thinksns app;
	protected static ListenerSociax listener;//注册回掉监听事件

	public FunctionSoicax(Context context) {
		this.context = context;
		this.thread = new Worker((Thinksns) context.getApplicationContext(),
				"FunctionSociax");
		this.app = (Thinksns) context.getApplicationContext();
		this.initUiHandler();
		this.initActivtyHandler();
	}

	/**
	 * 给功能注册监听事件
	 * @param listener
	 */
	public void setListenerSociax(ListenerSociax listener) {
		this.listener = listener;
	}

	/**
	 * 初始化uihandler用于UI操作， 如果需要的话用到uiHandler的话，否则本方法内不作任何处理，基类中有注释掉的DEMO可以直接复制
	 */
	protected abstract void initUiHandler();

	/**
	 * uiHandler demo
	 * 复制到子Fuction下即可
	 * 
	 *
	public class UIHandler extends Handler {

		public UIHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Thinksns app = (Thinksns) context.getApplicationContext();
			switch (msg.what) {
			}
		}
	}
	*/

	/**
	 * 初始化handlerActivity用于耗时异步线程，
	 * 如果需要的话用到ActivityHandler的话在本类下构建ActivityHandler，否则本方法内不作任何处理
	 * ，基类中有注释掉的Demo可以直接复制使用
	 */
	protected abstract void initActivtyHandler();
	/**
	 * ActivityHandler Demo
	 * 复制到子Fuction下即可
	 * @author wz
	 *
	private static final class ActivityHandler extends Handler {
		private static final long SLEEP_TIME = 2000;
		private static Context context = null;
		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			ActivityHandler.context = context;
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}
	 */

}
