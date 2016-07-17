package com.thinksns.sociax.t4.android.function;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.t4.adapter.AdapterTaskList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.function.FunctionChangeFollow.ListHandler;
import com.thinksns.sociax.t4.model.ModelTask;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

/**
 * 类说明： 礼物完成领取奖励
 * 
 * @author wz
 * @date 2014-11-25
 * @version 1.0
 */
public class FunctionTaskComplete extends FunctionSoicax {
	private AdapterTaskList adapter;
	private ModelTask completeTask;
	ListHandler handlerList;

	public FunctionTaskComplete(ThinksnsAbscractActivity context,
			AdapterTaskList adapterTaskList, ModelTask mdTask) {
		// TODO Auto-generated constructor stub
		super(context);
		this.adapter = adapterTaskList;
		this.completeTask = mdTask;
		this.handlerList = new ListHandler();
	}

	public void doCompleteStep() {
		if (completeTask.getStatus().equals("1")) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = new Message();
					msg.what = StaticInApp.CHANGE_TASKSTATUS;
					try {
						msg.obj = thread
								.getApp()
								.getTasksApi()
								.completeTask(completeTask.getTask_id(),
										completeTask.getTask_type(),
										completeTask.getTask_level());
					} catch (ApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handlerList.sendMessage(msg);
				}
			}).start();
		}
	}

	/**
	 * 用于修改列表数据
	 * 
	 * @author wz
	 * 
	 */
	@SuppressLint("HandlerLeak")
	public class ListHandler extends Handler {

		public ListHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StaticInApp.CHANGE_TASKSTATUS:
				if (msg.obj == null) {
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject result = new JSONObject(msg.obj.toString());
						if (result.getString("status").equals("1")) {
							adapter.doUpdataList();
						}
						Toast.makeText(context, result.getString("msg"),
								Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			}
		}
	}

	@Override
	protected void initUiHandler() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initActivtyHandler() {
		// TODO Auto-generated method stub
		
	}

}
