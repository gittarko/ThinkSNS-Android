package com.thinksns.sociax.t4.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.thinksns.sociax.t4.adapter.AdapterUserWeiboList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.t4.component.ListUserInfoWeibo;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

/**
 * 类说明：用户信息微博列表 本类由于ActivityUserInfo弃用而弃用，但是暂时保留11/24
 * 
 * @author wz
 * @date 2014-11-5
 * @version 1.0
 */
public class FragmentUserInfoWeibo extends FragmentWeibo {
	int uid;
	ModelUser user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initIntentData();
		Log.v("FragmentUserInfoWeibo", "wztest oncreate");
	}
	@Override
	public void initView() {
		rl_more = (RelativeLayout)findViewById(R.id.rl_more);
		listView = (ListUserInfoWeibo)findViewById(R.id.listView);
//		list=new ListData<SociaxItem>();
		list = DbHelperManager.getInstance(getActivity(), ListData.DataType.WEIBO).getHeaderDataByUser(10, uid);
		adapter = new AdapterUserWeiboList(this, list, uid);
		listView.setAdapter(adapter);
		
		rl_more = (RelativeLayout) findViewById(R.id.rl_more);
		mHandler = new ListHandler();
		rl_comment = (RelativeLayout) findViewById(R.id.ll_send_comment);
		et_comment = (EditText) findViewById(R.id.et_comment);
		btn_send = (Button) findViewById(R.id.btn_send_comment);
		
		img_face = (ImageView) findViewById(R.id.img_face);
		list_face = (ListFaceView) findViewById(R.id.face_view);
		list_face.initSmileView(et_comment);
		
		initReceiver();
	}

	@Override
	public void initReceiver() {
		super.initReceiver();
	}
	
	@Override
	public void initIntentData() {
		if (getActivity().getIntent().hasExtra("user")) {// 首先判断是否传入了user，如果有，则获取user的uid
			this.user = (ModelUser) getActivity().getIntent().getSerializableExtra(
					"user");
			if (user.getUid() != 0) {// 如果用户id不为0则设置uid
				this.uid = user.getUid();
			}
		} else if (uid == 0 && getActivity().getIntent().hasExtra("uid")) {// 如果uid为0，则表示没传入user
																			// 尝试获取uid
			this.uid = getActivity().getIntent().getIntExtra("uid", 0);
		}

		if (uid == 0 || uid == Thinksns.getMy().getUid()) {// 如果uid仍然为0或者就是本人的uid，，那么使用当前登录用户信息，不需要再获取
			uid = Thinksns.getMy().getUid();
			user = Thinksns.getMy();
		} else {// 如果uid为他人uid，则根据uid获取他人信息，这一步一般不会执行,因为一般传入详细的user
			
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		adapter.refreshNewSociaxList();
	}

	public static FragmentUserInfoWeibo newInstance(ModelUser user) {
		FragmentUserInfoWeibo fragment = new FragmentUserInfoWeibo();
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", user);
		fragment.setArguments(bundle);
		return fragment;
	}
	@Override
	public int getLayoutId() {
		return R.layout.fragment_userinfo_weibo;
	}
}