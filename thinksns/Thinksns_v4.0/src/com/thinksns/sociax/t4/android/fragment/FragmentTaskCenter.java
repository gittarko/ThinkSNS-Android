package com.thinksns.sociax.t4.android.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.thinksns.sociax.t4.adapter.AdapterTaskList;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：任务中心 fragmnet
 * @author wz
 * @date 2014-11-19
 * @version 1.0
 */
public class FragmentTaskCenter extends FragmentSociax {
	protected ListHandler mHandler;

	@Override
	public void initView() {
		// TODO Auto-generated method stub
//		listView = (ListTask)findViewById(R.id.listView);
		list = new ListData<SociaxItem>();
		adapter = new AdapterTaskList(this, list);
		listView.setAdapter(adapter);
		mHandler = new ListHandler();
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
		// TODO Auto-generated method stub
		adapter.loadInitData();
	}

	@SuppressLint("HandlerLeak")
	public class ListHandler extends Handler {

		public ListHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
		}
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_tasklist;
	}

}
