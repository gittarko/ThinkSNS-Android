package com.thinksns.sociax.t4.android.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.t4.adapter.AdapterTagPerson;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.findpeople.ActivitySearchUser;
import com.thinksns.sociax.t4.model.ModelUserTagandVerify;
import com.thinksns.sociax.t4.model.ModelUserTagandVerify.Child;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 标签
 * 
 * @author Administrator
 * @date 2014-11-9
 * @version 1.0
 */
public class FragmentFindPeopleTagList extends FragmentSociax {
	protected int selectpostion;
	protected ListHandler mHandler;
	protected LoadingView loadingView;
	protected EmptyLayout empty_layout;
	private LinearLayout layout;
	private ScrollView sv_content;
	private int type;

	@Override
	public void initIntentData() {
		type = getActivity().getIntent().getIntExtra("type",
				StaticInApp.FINDPEOPLE_TAG);
	}

	@Override
	public void initListener() {
	}

	@Override
	public void initData() {
		empty_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
		getTagList();
	}

	//获取标签列表
	private void getTagList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.GET_TAG_LIST;
				try {
					if (type == StaticInApp.FINDPEOPLE_TAG)
						msg.obj = ((Thinksns) getActivity().getApplicationContext()).getUsers().getTagList();
					else if (type == StaticInApp.FINDPEOPLE_VERIFY)
						msg.obj = ((Thinksns) getActivity().getApplicationContext()).getUsers().getVerifyList();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	public class ListHandler extends Handler {

		public ListHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Thinksns app = (Thinksns) getActivity().getApplicationContext();
			switch (msg.what) {
			case StaticInApp.GET_TAG_LIST:
				ListData<SociaxItem> list = (ListData<SociaxItem>) msg.obj;
				if (list == null) {
//					Toast.makeText(getActivity(), "获取内容失败,请稍后重试", Toast.LENGTH_SHORT).show();
					empty_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
				}else if(list.size() == 0){
//					Toast.makeText(getActivity(), "没有更多内容", Toast.LENGTH_SHORT).show();
					empty_layout.setErrorType(EmptyLayout.NODATA);
				} else {
					empty_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
					for (int i = 0; i < list.size(); i++) {
						View view = inflater.inflate(R.layout.tag_item, null);
						TextView title = (TextView) view.findViewById(R.id.textView_tagtitle);
						GridView gridView = (GridView) view.findViewById(R.id.gridView_tag);
						AdapterTagPerson adapter = new AdapterTagPerson(inflater, type);
						ModelUserTagandVerify taglist = (ModelUserTagandVerify) list.get(i);
						final String tle = taglist.getTitle();
						final String ver_id = taglist.getId();
						title.setText(tle);
						if (getActivity().getIntent().getIntExtra("type",
								StaticInApp.FINDPEOPLE_TAG) == StaticInApp.FINDPEOPLE_VERIFY) {
							title.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(inflater.getContext(),ActivitySearchUser.class);
									intent.putExtra("type",StaticInApp.FINDPEOPLE_VERIFY);
									intent.putExtra("verify_id", ver_id);
									inflater.getContext().startActivity(intent);
								}
							});
						}
						List<Child> list2 = taglist.getChild();
						if (list2 != null) {
							adapter.bindData(list2);
							gridView.setAdapter(adapter);
							adapter.notifyDataSetChanged();
						}
						layout.addView(view, i);
					}
					break;
				}
			}
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_taglist;
	}

	@Override
	public void initView() {
		empty_layout = (EmptyLayout)findViewById(R.id.empty_layout);
		empty_layout.setNoDataContent(getResources().getString(R.string.empty_content));
		empty_layout.setOnLayoutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getTagList();
			}
		});

		layout = (LinearLayout) findViewById(R.id.tag_person);
		sv_content = (ScrollView) findViewById(R.id.sv_content);
		mHandler = new ListHandler();
	}
}
