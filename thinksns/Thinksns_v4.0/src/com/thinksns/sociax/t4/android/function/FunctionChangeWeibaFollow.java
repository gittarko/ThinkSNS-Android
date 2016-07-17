package com.thinksns.sociax.t4.android.function;

import com.thinksns.sociax.t4.android.weiba.ActivityWeibaDetail;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/** 
 * 类说明：   微吧修改关注功能
 * @author  wz    
 * @date    2014-12-23
 * @version 1.0
 */
public class FunctionChangeWeibaFollow extends FunctionSoicax{
	protected boolean isfollow=false;
	protected int weiba_id;
	protected AdapterSociaxList adapter;
	
	public FunctionChangeWeibaFollow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 从微吧详情内帖子列表生成的取消关注功能，操作成功之后需要修改adapter
	 * @param context
	 * @param isfollow
	 * @param weiba_id
	 * @param adapter
	 */
	public FunctionChangeWeibaFollow(Context context,boolean isfollow,int weiba_id,AdapterSociaxList adapter) {
		super(context);
		this.isfollow=isfollow;
		this.weiba_id=weiba_id;
		this.adapter=adapter;
	}
	
	public void changeFollow(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg=new Message();
				msg.what = StaticInApp.CHANGE_WEIBA_FOLLOW;
				try {
					msg.obj = app.getWeibaApi().changeWeibaFollow(weiba_id,isfollow);
				} catch (ApiException e) {
					e.printStackTrace();
				}
				handlerUI.sendMessage(msg);
			}
		}).start();
	}

	@Override
	protected void initUiHandler() {
		// TODO Auto-generated method stub
		handlerUI=new UIHandler();
	}

	@Override
	protected void initActivtyHandler() {
		// TODO Auto-generated method stub
	}
	
	
	public class UIHandler extends Handler {

		public UIHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StaticInApp.CHANGE_WEIBA_FOLLOW:
				if(msg.obj==null){
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				}else{
					try {
						JSONObject result = new JSONObject(msg.obj.toString());
						if(result.getInt("status") == 1) {
							if(listener != null) {
								listener.onTaskSuccess();
							}
						}else {
							Toast.makeText(context, result.getString("msg"), Toast.LENGTH_SHORT).show();
						}
//
					} catch (Exception e) {
						e.printStackTrace();
//						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
		}
	}

}
