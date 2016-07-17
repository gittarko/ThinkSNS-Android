package com.thinksns.sociax.t4.android.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.Listener.UnreadMessageListener;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.findpeople.ActivitySelectUser;
import com.thinksns.sociax.t4.model.ModelNotification;
import com.thinksns.tschat.chat.TSChatManager;

/** 
 * 类说明：   消息页

 * @author  Zoey    
 * @date    2015年11月10日
 * @version 1.0
 */
public class FragmentMessage extends FragmentSociax implements OnClickListener{

	public AdapterViewPager adapter_vp_msg;
	public ArrayList<Fragment> frag_list;
	public ViewPager vp_message;
	public Fragment currentFragment = null;
	private FragmentRoomList roomFragment;
	private FragmentMyFriends friendsFragment;

	private static final int SELECTED_MESSAGE=0;
	private static final int SELECTED_FRIENDS=1;
	private RadioButton rb_message,rb_friends;
	private Button btn_chat;
	private int currentIndex = 0;

	private static FragmentMessage instance;
	private ModelNotification notification;
	private UnreadMessageListener listener;

	//实例化Fragment，并传入消息未读数
	public static FragmentMessage newInstance(ModelNotification notification) {
		if(instance == null) {
			instance = new FragmentMessage();
			Bundle args = new Bundle();
			args.putSerializable("notice", notification);
			instance.setArguments(args);
		}
		return instance;
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_message;
	}

	public FragmentRoomList getFragMsg(){
		if (frag_list != null && frag_list.size()!=0)
			return (FragmentRoomList) frag_list.get(0);
		return null;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof UnreadMessageListener)
			this.listener = (UnreadMessageListener)activity;
		if(getArguments() != null) {
			notification = (ModelNotification)getArguments().getSerializable("notice");
		}
	}

	@Override
	public void initView() {
		rb_message = (RadioButton)findViewById(R.id.rb_message);
		rb_friends = (RadioButton)findViewById(R.id.rb_friends);
		vp_message = (ViewPager)findViewById(R.id.vp_message);
		btn_chat = (Button)this.findViewById(R.id.btn_chat);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_message:
			setSelected(SELECTED_MESSAGE);
			break;
		case R.id.rb_friends:
			setSelected(SELECTED_FRIENDS);
			break;
		case R.id.btn_chat:
			TSChatManager.createChat(getActivity());
		default:
			break;
		}
	}

	/**
	 * 清除消息未读数
	 * @param type
	 * @param unread
     */
	public void clearUnreadMsg(int type, int unread) {
		if(listener != null) {
			listener.clearUnreadMessage(type, unread);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (adapter!=null) {
			adapter.notifyDataSetChanged();
		}
	}
	@Override
	public void initData() {
		if (adapter_vp_msg==null) {
			initFragments();
		}
		setSelected(currentIndex);
	}
	
	public void initFragments(){
		adapter_vp_msg = new AdapterViewPager(getChildFragmentManager());
		frag_list = new ArrayList<Fragment>();
		roomFragment = FragmentRoomList.newInstance(notification);
		friendsFragment = new FragmentMyFriends();
		frag_list.add(roomFragment);
		frag_list.add(friendsFragment); // 好友
		adapter_vp_msg.bindData(frag_list);
		
		vp_message.setOffscreenPageLimit(frag_list.size());
		initMsgViewPagerListener();
		vp_message.setAdapter(adapter_vp_msg);
	}
	
	public void setSelected(int selected){
		currentIndex = selected;
		showFragment(selected);
		switch (selected) {
		case SELECTED_MESSAGE:
			setTitleBgUi(rb_message);
			break;
		case SELECTED_FRIENDS:
			setTitleBgUi(rb_friends);
			break;
		default:
			break;
		}
	}
	
	public void showFragment(int selected){
		if (frag_list!=null&&frag_list.size()!=0) 
			currentFragment = frag_list.get(selected);
		if(vp_message!=null)
			vp_message.setCurrentItem(selected);
	}

	//设置未读提示，包含点赞，@我，评论我的
	public void setUnreadNotice(ModelNotification notice) {
		if(roomFragment != null) {
			roomFragment.setUnReadUi(notice);
		}
	}

	@Override
	public void initIntentData() {
		
	}

	protected void initMsgViewPagerListener() {
		vp_message.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				showFragment(index);
				switch (index) {
				case 0:
					setTitleBgUi(rb_message);
					break;
				case 1:
					setTitleBgUi(rb_friends);
					break;
				default:
					break;
				}
			}
			@Override
			public void onPageScrolled(int index, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int index) {
			}
		});
	}
	
	@Override
	public void initListener() {
		rb_message.setOnClickListener(this);
		rb_friends.setOnClickListener(this);
		btn_chat.setOnClickListener(this);
	}
	
	//修改Tab的显示状态
	public void setTitleBgUi(RadioButton selected){
		RadioButton[] rbtitle = { rb_message, rb_friends};
		// 遍历按钮，把被选中的id对应的按钮修改掉，再把其他的修改成非选择状态
		for (int i = 0; i < 2; i++) {
			if (rbtitle[i].getId() != selected.getId()){
				rbtitle[i].setChecked(false);
				rbtitle[i].setTextColor(getActivity().getResources().getColor(R.color.title_blue));
			}else{
				rbtitle[i].setChecked(true);
				rbtitle[i].setTextColor(getActivity().getResources().getColor(R.color.white));
			}
			continue;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

}
