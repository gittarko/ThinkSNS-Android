package com.thinksns.tschat.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;
import com.thinksns.tschat.R;
import com.thinksns.tschat.adapter.AdapterChatDetailList;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.fragment.FragmentChatDetail;
import com.thinksns.tschat.fragment.FragmentChatList;
import com.thinksns.tschat.inter.ChatCoreResponseHandler;
import com.thinksns.tschat.listener.ChatCallBack;
import com.thinksns.tschat.listener.OnChatItemClickListener;
import com.thinksns.tschat.popupwindow.PopupWindowListDialog;
import com.thinksns.tschat.widget.PopUpWindowAlertDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.ImagePagerFragment;

/**
 * 聊天详情
 */
public class ActivityChatDetail extends FragmentActivity implements OnClickListener,
		OnChatItemClickListener, ChatCallBack{
	private static final String TAG = ActivityChatDetail.class.getSimpleName();
	private static final int CHANGE_TITLE = 0001;

	private static String mTitle = "聊天详情";

	private FragmentChatDetail fragment;
	private ImagePagerFragment imagePagerFragment;		//图片选择
	private static Bundle data;
	private static ModelChatUserList currentRoom;		//当前聊天房间内容
	public static Activity activity = null;

	public static void initChatInfo(ModelChatUserList chatUserList) {
		currentRoom = chatUserList;
		//单聊
		data = new Bundle();
		if (chatUserList.isSingle()) {
			data.putString("to_name", chatUserList.getTo_name());
			data.putInt("to_uid", chatUserList.getTo_uid());
			data.putString("to_face", chatUserList.getFrom_uface_url());
			data.putBoolean("issingle", true);
			mTitle = chatUserList.getTo_name();
		}else {
			mTitle = chatUserList.getTitle();
		}

		data.putInt("room_id", chatUserList.getRoom_id());
		data.putString("title", mTitle);

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutId());
		initView();
		initFragment();
	}

	private void initView() {
		activity = this;
	}

	//初始化聊天详情房间
	private void initFragment() {
		fragment = new FragmentChatDetail();
		//传入intent
		fragment.setArguments(data);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.ll_content, fragment).commit();
	}

	protected int getLayoutId() {
		return R.layout.activity_chat_details_layout;
	}

	public static ModelChatUserList getCurrentRoom() {
		return currentRoom;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHANGE_TITLE) {
			if (data != null) {
				if(data.hasExtra("newTitle")) {
					String title = data.getStringExtra("newTitle");
					fragment.setRoomTitle(title);
					//更新房间列表
					currentRoom.setTitle(title);
				}else if(data.hasExtra("logo")){
					int logoId = data.getIntExtra("logo", 0);
					currentRoom.setGroupFace(null);		//刷新房间头像
					currentRoom.setLogoId(logoId);
				}

				EventBus.getDefault().post(currentRoom);
			}
		} else {
			if (fragment instanceof FragmentChatDetail) {
				fragment.onActivityResult(requestCode, resultCode, data);
			}

			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View view) {

	}

	@Override
	public void onClickUserHead(View view) {

	}

	@Override
	public void onClickUserCards(View view) {

	}


	/**
	 * 全屏预览大图
	 * @param path
     */
	@Override
	public void onImageScreen(View v, String path) {
        //选取屏幕中心点
		int [] screenLocation = new int[2];
		v.getLocationOnScreen(screenLocation);
		//添加图片集合
		List<String> resultList = new ArrayList<String>();
		resultList.add(path);
		int width = v.getWidth();
		int height = v.getHeight();
        imagePagerFragment =
                ImagePagerFragment.newInstance(resultList, 0, screenLocation, width, height);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.ll_content, imagePagerFragment)
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void sendMessage(final ModelChatMessage message, int delay) {
		if(TSChatManager.requestExist(message)) {
			//请求已存在
			return;
		}

		if(currentRoom.isSingle()) {
			//群聊发送不指定to_uid
			message.setTo_uid(currentRoom.getTo_uid());
			message.setTo_uname(currentRoom.getTo_name());
			message.setTo_uface(currentRoom.getFrom_uface_url());
		}

		//更新消息所在的房间内容
		currentRoom.setMtime(message.getMtime());
		currentRoom.setContent(message.getContent());
		currentRoom.setLastMessage(message);

		//设置消息所在房间
		message.setCurrentRoom(currentRoom);
		//开始发送消息
		TSChatManager.sendMessage(message, new ChatCoreResponseHandler() {
			@Override
			public void onStart(Object object) {
				Log.v(TAG, "SEND MESSAGE-->onStart");
			}

			@Override
			public void onSuccess(Object object) {
				Log.v(TAG, "SEND MESSAGE-->onSuccess");
				message.setSendState(ModelChatMessage.SEND_STATE.SEND_OK);
				//更新UI列表
				fragment.getAdapter().updateSingleItem(message, fragment.getListView());
			}

			@Override
			public void onFailure(Object object) {
				Log.v(TAG, "SEND MESSAGE-->onFailure");
				if(object instanceof ModelChatMessage) {
					//更新UI列表
					fragment.getAdapter().updateSingleItem((ModelChatMessage)object, fragment.getListView());
				}
			}


		}, delay);

		//更新消息列表对应房间的最后一条消息
		FragmentChatList.getInstance().updateLastMessage(currentRoom);

	}

	@Override
	public void retrySendMessage(final ModelChatMessage message) {
		if(TSChatManager.requestExist(message)) {
			return;
		}

		PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(this);
		builder.setMessage("确定重发该条消息?", 18);
		builder.setTitle(null, 0);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendMessage(message, 0);
				//更新UI列表
				fragment.getAdapter().updateSingleItem(message, fragment.getListView());
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create();
	}

	@Override
	public void onDetailsInfoSelected() {
		Intent intent = new Intent(ActivityChatDetail.this, ActivityChatInfo.class);
		intent.putExtra("room_id", currentRoom.getRoom_id());				// 如果有room_id的话
		intent.putExtra("to_uid", currentRoom.getTo_uid());					// 没有room_id的情况下会用到to_uid
		intent.putExtra("is_single", currentRoom.isSingle());
		startActivityForResult(intent, CHANGE_TITLE);
	}

	/**
	 * 复制文本消息
	 * @param text
     */
	@Override
	public void copyTextMsg(final String text) {
		//长按复制内容
		final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(this);
		builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if(position == 0) {
					//复制评论
					UnitSociax.copy(text, ActivityChatDetail.this);
				}

				builder.dimss();
			}
		});

		List<String> datas = new ArrayList<String>();
		datas.add("复制");
		builder.create(datas);

	}

	@Override
	public void onBackPressed() {
		if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
			imagePagerFragment.runExitAnimation(new Runnable() {
				public void run() {
					if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
						getSupportFragmentManager().popBackStack();
					}
				}
			});

		}else {
			super.onBackPressed();
		}
	}
}
