package com.thinksns.sociax.t4.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.ImageAdapter;
import com.thinksns.sociax.t4.android.Listener.UnreadMessageListener;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.channel.ActivityChannel;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.findpeople.ActivityFindPeople;
import com.thinksns.sociax.t4.android.findpeople.ActivityFindPeopleDetails;
import com.thinksns.sociax.t4.android.findpeople.ActivitySearchUser;
import com.thinksns.sociax.t4.android.function.FunctionAdvertise;
import com.thinksns.sociax.t4.android.gift.ActivityScoreShop;
import com.thinksns.sociax.t4.android.info.ActivityInformation;
import com.thinksns.sociax.t4.android.mp3.JNIMp3Encode;
import com.thinksns.sociax.t4.android.temp.T4GroupActivity;
import com.thinksns.sociax.t4.android.topic.ActivityTopicList;
import com.thinksns.sociax.t4.android.weiba.ActivityWeiba;
import com.thinksns.sociax.t4.component.CircleFlowIndicator;
import com.thinksns.sociax.t4.component.ViewFlow;
import com.thinksns.sociax.t4.component.ViewFlow.ViewSwitchListener;
import com.thinksns.sociax.t4.model.ModelAds;
import com.thinksns.sociax.t4.model.ModelNotification;
import com.thinksns.sociax.thinksnsbase.activity.widget.BadgeView;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

import java.util.ArrayList;

/**
 * 类说明：发现模块
 *
 * @author wz
 * @date 2015-1-4
 * @version 1.0
 */
public class FragmentFind extends FragmentSociax {

	private LinearLayout rl_find_weiba, // 发现--微吧
			rl_find_topic, rl_find_channal, rl_find_info,rl_find_gift,
			rl_find_find,rl_find_top, rl_find_near;
	private LinearLayout weiba_remind;		//微吧提示
	private BadgeView weibaMsg;

	private RelativeLayout rl_title;
	private TextView tv_center;
	private FunctionAdvertise fc_ads;
	private LinearLayout ll_ads;
	ImageAdapter viewFlowAdapter;

	private int weibaUnread = 0;		//微吧消息未读数
	private UnreadMessageListener listener;
	private static FragmentFind fragmentFind;

	public static FragmentFind newInstance(int unread) {
		if(fragmentFind == null) {
			fragmentFind = new FragmentFind();
		}
		if(!fragmentFind.isAdded()) {
			Bundle bundle = new Bundle();
			bundle.putInt("unread", unread);
			fragmentFind.setArguments(bundle);
		}
		return fragmentFind;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(getArguments() != null) {
			weibaUnread = getArguments().getInt("unread", 0);
		}
		if(activity instanceof UnreadMessageListener)
			this.listener = (UnreadMessageListener)activity;
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_find;
	}

	/**
	 * 设置微吧消息未读数
	 * @param notification
	 */
	public void setWeibaUnreadCount(ModelNotification notification) {
		int count = notification.getWeibaComment();
		if(count > 99)
			count = 99;
		weibaMsg.setBadgeCount(count);
	}

	@Override
	public void initView() {
		tv_center = (TextView) findViewById(R.id.tv_center);
		tv_center.setText("发现");
		// 发现
		rl_find_topic = (LinearLayout) findViewById(R.id.rl_topic);
		rl_find_channal = (LinearLayout) findViewById(R.id.rl_channel);
		rl_find_info = (LinearLayout) findViewById(R.id.rl_info);
		rl_find_find = (LinearLayout) findViewById(R.id.rl_find);
		rl_find_gift = (LinearLayout) findViewById(R.id.rl_gift);
		rl_find_weiba = (LinearLayout) findViewById(R.id.rl_weiba);
		rl_find_top = (LinearLayout) findViewById(R.id.rl_top);
		rl_find_near = (LinearLayout) findViewById(R.id.rl_near);
		//微吧新消息提示
		weibaMsg = (BadgeView)findViewById(R.id.badgeWeiba);
		weibaMsg.setBadgeCount(weibaUnread);
		initAds();
		inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private void initAds() {
		//广告栏
		fc_ads = (FunctionAdvertise)findViewById(R.id.fc_ads);
		//设置广告栏高度
		int width = UnitSociax.getWindowWidth(getActivity());
		int height = (int) (((double) 9) / ((double) 16) * width);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
		fc_ads.setLayoutParams(params);
		fc_ads.initAds();
	}

	@Override
	public void initIntentData() {

	}

	@Override
	public void initListener() {
		//话题
		rl_find_topic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),ActivityTopicList.class);
				startActivity(intent);
			}
		});
		//频道
		rl_find_channal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), ActivityChannel.class);
				startActivity(intent);
			}
		});
		//资讯
		rl_find_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), ActivityInformation.class);
				startActivity(intent);
			}
		});
		//找人
		rl_find_find.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ActivityFindPeople.class);
				startActivity(intent);
			}
		});
		//礼物/积分商城
		rl_find_gift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), ActivityScoreShop.class);
				startActivity(intent);

			}
		});

		//微吧
		rl_find_weiba.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ActivityWeiba.class);
				intent.putExtra("unread", weibaUnread);
				startActivity(intent);

			}
		});

		//风云榜、排行榜
		rl_find_top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),ActivityFindPeopleDetails.class);
				intent.putExtra("type", StaticInApp.FINDPEOPLE_TOPLIST);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		//附近的人
		rl_find_near.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ActivitySearchUser.class);
				intent.putExtra("type", StaticInApp.FINDPEOPLE_NEARBY);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

	}

	@Override
	public void initData() {
		fc_ads.initAds();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		fc_ads.stopAutoCycle();
		super.onStop();
	}

	@Override
	public void onResume() {
		if(!fc_ads.isCycling())
			fc_ads.startCycle();
		super.onResume();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	/**
	 * 清除新消息提醒
	 */
	public void clearUnreadMsg() {
		//消除主界面的消息未读数
		weibaUnread = 0;
		if(weibaMsg != null) {
			weibaMsg.setBadgeCount(weibaUnread);
		}
		if(listener != null) {
			listener.clearUnreadMessage(StaticInApp.UNREAD_WEIBA, weibaUnread);
		}
	}
}
