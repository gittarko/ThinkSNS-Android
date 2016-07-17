package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.t4.adapter.AdapterUserInfoAlbum;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.android.draft.ActivityMyDraft;
import com.thinksns.sociax.t4.android.erweima.ActivityScan;
import com.thinksns.sociax.t4.android.gift.ActivityMyScore;
import com.thinksns.sociax.t4.android.setting.ActivitySetting;
import com.thinksns.sociax.t4.android.task.ActivityMedalPavilion;
import com.thinksns.sociax.t4.android.task.ActivityTaskCenter;
import com.thinksns.sociax.t4.android.user.ActivityFollowUser;
import com.thinksns.sociax.t4.android.user.ActivityUserHome;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.weibo.ActivityCollectedWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityMyWeibo;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelNotification;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.tschat.widget.UIImageLoader;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 类说明： 我
 * @author wz
 * @date 2015-1-4
 * @version 1.0
 */
public class FragmentMy extends FragmentSociax{
	private TextView tv_center;
	private ImageView iv_erweima;
	
	// my用到的变量
	private LinearLayout 	ll_myinfo;		// 我的信息
	private LinearLayout 	rl_mycollection, // 我的收藏
							rl_myrescore,// 我的积分
							rl_mydraft,// 草稿箱
							rl_userinfo, // 用户信息
							rl_myweibo,// 我的微博
							rl_myfollowing,// 我的关注
							rl_mytask,//我的任务
							rl_mymedal,//我的勋章馆
							rl_setting;// 设置
	private RelativeLayout rl_myfollowed;// 我的粉丝
	
	private ImageView img_user_header;	//用户头像
	private TextView 	tv_count_weibo, 
						tv_count_follow, 
						tv_count_followed;
	private TextView tv_my_username, tv_my_usertag;
	private boolean isMyInit = false;					// 判断my是否初始化，如果初始化，则下次进入的时候可以去掉不用重复加载的内容
	private TextView tv_remind_follower;				// 新的粉丝
	private TextView tv_remind_draft; // 提示草稿数目

	private LinearLayout ll_user_group;
	private Handler mHandler;

	@Override
	public int getLayoutId() {
		return R.layout.fragment_my;
	}


	@Override
	public void initView() {
		tv_center = (TextView) findViewById(R.id.tv_center);
		iv_erweima = (ImageView) findViewById(R.id.iv_erweima);
		tv_center.setText("我");
		// my
		rl_mycollection = (LinearLayout) findViewById(R.id.rl_mycollection);
		//草稿
		rl_mydraft = (LinearLayout) findViewById(R.id.rl_mydraft);
		//积分
		rl_myrescore = (LinearLayout) findViewById(R.id.rl_myscore);
		//个人信息
		rl_userinfo = (LinearLayout) findViewById(R.id.rl_userinfo);
		rl_setting = (LinearLayout) findViewById(R.id.rl_setting);
		
		rl_mytask = (LinearLayout) findViewById(R.id.rl_my_task);
		rl_mymedal = (LinearLayout) findViewById(R.id.rl_my_medal);

		rl_myfollowing = (LinearLayout) findViewById(R.id.rl_myfollow);
		rl_myfollowed = (RelativeLayout) findViewById(R.id.rl_myfollowed);
		rl_myweibo = (LinearLayout) findViewById(R.id.rl_myweibo);
		img_user_header = (ImageView) findViewById(R.id.img_user_header);
		tv_count_follow = (TextView) findViewById(R.id.tv_count_follow);
		tv_count_followed = (TextView) findViewById(R.id.tv_count_followed);
		tv_count_weibo = (TextView) findViewById(R.id.tv_count_weibo);

		tv_my_username = (TextView) findViewById(R.id.tv_my_username);
		tv_my_usertag = (TextView) findViewById(R.id.tv_my_usertag);

		tv_remind_follower = (TextView) findViewById(R.id.tv_remind_follower);
		tv_remind_draft = (TextView) findViewById(R.id.tv_remind_draft);
		ll_user_group = (LinearLayout) findViewById(R.id.ll_uname_adn);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ModelUser user = (ModelUser) msg.obj;
				if (user != null) {
					tv_count_weibo.setText(Integer.toString(user.getWeiboCount()));
					tv_count_follow.setText(Integer.toString(user.getFollowersCount()));
					tv_count_followed.setText(Integer.toString(user.getFollowedCount()));
				}
			}
		};

	}
	
	@Override
	public void initIntentData() {
	}

	@Override
	public void initListener() {
		//个人二维码
		iv_erweima.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ModelUser user = Thinksns.getMy();
				Intent intent = new Intent(getActivity(), ActivityScan.class);
				intent.putExtra("userImg", user.getFace());
				intent.putExtra("userName", user.getUserName());
				intent.putExtra("userIntro", user.getIntro());
				intent.putExtra("uid", user.getUid());
				startActivity(intent);
			}
			
		});
		//我的设置
		rl_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ActivitySetting.class);
				startActivity(intent);
			}
		});
		//我的分享
		rl_myweibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ActivityMyWeibo.class);
				startActivity(intent);
			}
		});
		//个人信息
		rl_userinfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),	ActivityUserInfo_2.class);
				startActivityForResult(intent, 200);
			}
		});
		//草稿箱
		rl_mydraft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ActivityMyDraft.class);
				startActivity(intent);
			}
		});
		//任务中心
		rl_mytask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),ActivityTaskCenter.class);
				startActivity(intent);
			}
		});
		//勋章馆
		rl_mymedal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityStack.startActivity(getActivity(), ActivityMedalPavilion.class);
			}
		});

		rl_myrescore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityStack.startActivity(getActivity(), ActivityMyScore.class);
			}
		});

		//我的收藏
		rl_mycollection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityStack.startActivity(getActivity(),ActivityCollectedWeibo.class);
			}
		});

		// 我关注的人
		rl_myfollowing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),ActivityFollowUser.class);
				intent.putExtra("uid", Thinksns.getMy().getUid());
				intent.putExtra("type", "following");
				startActivity(intent);

			}
		});

		//我的粉丝
		rl_myfollowed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),ActivityFollowUser.class);
				intent.putExtra("type", "follow");
				intent.putExtra("uid", Thinksns.getMy().getUid());
				startActivity(intent);
			}
		});
	}

	@Override
	public void initData() {
		showBasicInfo(Thinksns.getMy());
	}
	
	public void showBasicInfo(ModelUser user){
		if (user!=null) {
			//设置头像
			UIImageLoader.getInstance(getActivity()).displayImage(user.getFace(),img_user_header);
			//用户名
			tv_my_username.setText(user.getUserName());
			//用户简介
			tv_my_usertag.setText(TextUtils.isEmpty(user.getIntro())||
					user.getIntro().equals("null") ? getResources().getString(R.string.empty_user_intro) : user.getIntro());
			//粉丝数
			tv_count_follow.setText(user.getFollowersCount() + "");
			//分享数
			tv_count_weibo.setText(user.getWeiboCount() + "");
			//关注数
			tv_count_followed.setText(user.getFollowedCount() + "");
			
			if (ll_user_group != null && 
					user.getUserApprove() != null
					&& user.getUserApprove().getApprove().size() > 0) {
				UnitSociax unit = new UnitSociax(getActivity());
				unit.addUserGroup(user.getUserApprove().getApprove(),ll_user_group);
			} else {
				if (ll_user_group != null) {
					ll_user_group.removeAllViews();
				}
			}
			
		}
	}

	/**
	 * 显示草稿数目提示
	 */
	private void showDraftCount() {
		Thinksns app = (Thinksns) getActivity().getApplicationContext();
		if (app != null) {
			List<SociaxItem> drafts = app.getWeiboDraftSQL().getAllWeiboDraft(
					20, 0);
			if (drafts.isEmpty()) {
				tv_remind_draft.setVisibility(View.GONE);
			} else {
				tv_remind_draft.setVisibility(View.VISIBLE);
				tv_remind_draft.setText(drafts.size() + "");
			}
			app.closeDb();
		}

		//更新用户基本资料
		new Api.Users().show(user, userListener);
	}


	private HttpResponseListener userListener = new HttpResponseListener() {
		@Override
		public void onSuccess(Object result) {
			ListData<SociaxItem> list = (ListData<SociaxItem>) result;
			if (list != null && list.size() == 1) {
				ModelUser user = (ModelUser) list.get(0);
				if (user.getUid() == Thinksns.getMy().getUid()) {
					//更新本地用户数据
					Thinksns.setMy(user);
					UserSqlHelper.updateUser(user);
				}

				Message mainMsg = Message.obtain();
				mainMsg.obj = user;
				mHandler.sendMessage(mainMsg);
				if (adapter != null && adapter instanceof AdapterUserInfoAlbum) {
					adapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void onError(Object result) {

		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// 每次切换到该页面的时候刷新数据
		showDraftCount();
	}

	/**
	 * 热门电台
	 * 采用AsyncHttpClient的Post方式进行实现
	 */
	public void getRefreshCount(final ModelUser user) {
			new Api.Users().show(user, new HttpResponseListener() {
				
				@Override
				public void onSuccess(Object result) {
					ListData<SociaxItem> list = (ListData<SociaxItem>) result;
					if(list != null && list.size() == 1)
						showBasicInfo((ModelUser)list.get(0));
				}
				
				@Override
				public void onError(Object result) {
					Toast.makeText(getActivity(), result.toString(), 0).show();
				}
			});
	}
	
	// 将byte数组转换成字符串
	public String byteToString(byte[] responseBody) {
		String strRead = null;
		try {
			strRead = new String(responseBody, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return strRead;
	}
	
	/**
	 * 设置未读消息
	 * 
	 * @param mdNotification
	 */
	public void setUnReadUi(ModelNotification mdNotification) {
		if (tv_remind_follower != null)
			if (mdNotification.getFollower() > 0) {
				tv_remind_follower.setText(mdNotification.getFollower() > 99 ? "99": (mdNotification.getFollower() + ""));
				tv_remind_follower.setVisibility(View.VISIBLE);
			} else {
				tv_remind_follower.setText(0 + "");
				tv_remind_follower.setVisibility(View.GONE);
			}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
}
