package com.thinksns.sociax.t4.android.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thinksns.sociax.t4.adapter.AdapterCopyTask;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelCopyTask;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;

/** 
 * 类说明：副本任务
 * 
 * @author Zoey
 * @date 2015年9月7日
 * @version 1.0
 */
public class FragmentCopyTask extends FragmentSociax {

	private ListView lv_copy_task;

	private AdapterCopyTask adapterDailyTask;
	private TaskHandler mHandler = new TaskHandler();
	private ArrayList<ModelCopyTask> taskList = null;
	private LoadingView loadingView;

	public void getCopyTask() {

		loadingView.show(lv_copy_task);

		new Thread(new Runnable() {
			@Override
			public void run() {

				Message msg = new Message();
				msg.what = StaticInApp.GET_COPY_TASK;
				try {
					msg.obj = ((Thinksns) (getActivity()
							.getApplicationContext())).getTasksApi()
							.getCopyTask();
				} catch (Exception e) {
					e.printStackTrace();
					loadingView.hide(lv_copy_task);
				}
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	public class TaskHandler extends Handler {

		public TaskHandler() {
			super();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StaticInApp.GET_COPY_TASK:

				taskList = (ArrayList<ModelCopyTask>) msg.obj;
				
				if (taskList != null) {
					adapterDailyTask = new AdapterCopyTask(getActivity(),taskList);
					lv_copy_task.setAdapter(adapterDailyTask);
				}
				
				loadingView.hide(lv_copy_task);

				break;
			}
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_copy_task;
	}

	@Override
	public void initView() {
		lv_copy_task = (ListView) findViewById(R.id.lv_copy_task);
		lv_copy_task.setDivider(null);
		lv_copy_task.setVerticalScrollBarEnabled(false); // 设置滑动条垂直不显示
		loadingView = (LoadingView) findViewById(LoadingView.ID);

	}

	@Override
	public void initIntentData() {
		
	}

	@Override
	public void initListener() {
		
	}

	@Override
	public void initData() {
		getCopyTask();
	}

}
