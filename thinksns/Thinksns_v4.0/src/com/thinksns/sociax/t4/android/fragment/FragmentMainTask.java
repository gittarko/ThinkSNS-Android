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

import com.thinksns.sociax.t4.adapter.AdapterMainTask;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;

import com.thinksns.sociax.t4.model.ModelDailyOrMainTask;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;

/**
 * 类说明：主线任务
 * 
* @author  Zoey    
 * @date    2015年9月7日
 * @version 1.0
 */
public class FragmentMainTask extends FragmentSociax {

	private ListView lv_main_task;

	private AdapterMainTask adapterDailyTask;
	private TaskHandler mHandler = new TaskHandler();
	private ArrayList<ModelDailyOrMainTask> taskList = null;
	private LoadingView loadingView;

	public void getAllMedals() {

		loadingView.show(lv_main_task);

		new Thread(new Runnable() {
			@Override
			public void run() {

				Message msg = new Message();
				msg.what = StaticInApp.GET_MAIN_TASK;
				try {
					msg.obj = ((Thinksns) (getActivity()
							.getApplicationContext())).getTasksApi()
							.getMainTask();
				} catch (Exception e) {
					e.printStackTrace();
					loadingView.hide(lv_main_task);
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
			case StaticInApp.GET_MAIN_TASK:

				taskList = (ArrayList<ModelDailyOrMainTask>) msg.obj;
				
				if (taskList != null && getActivity() != null) {
					adapterDailyTask = new AdapterMainTask(getActivity(),taskList);
					lv_main_task.setAdapter(adapterDailyTask);
				}

				loadingView.hide(lv_main_task);

				break;
			}
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_main_task;
	}

	@Override
	public void initView() {
		lv_main_task = (ListView)findViewById(R.id.lv_main_task);
		lv_main_task.setDivider(null);
		lv_main_task.setVerticalScrollBarEnabled(false); // 设置滑动条垂直不显示
		loadingView = (LoadingView)findViewById(LoadingView.ID);

	}

	@Override
	public void initIntentData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		getAllMedals();
	}
}
