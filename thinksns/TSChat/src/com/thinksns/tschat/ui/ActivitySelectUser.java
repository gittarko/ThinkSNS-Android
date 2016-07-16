package com.thinksns.tschat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.tschat.R;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.bean.ModelUser;
import com.thinksns.tschat.chat.ChatSocketClient;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.fragment.FragmentSelectUser;
import com.thinksns.tschat.inter.ChatCoreResponseHandler;
import com.thinksns.tschat.unit.FunctionCreateChat;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明： 选择好友
 * StaticInApp.CHAT_ADD_USER 群组添加成员时候选人
 *
 */
public class ActivitySelectUser extends FragmentActivity implements OnClickListener {
	private static final String TAG = ActivitySelectUser.class.getSimpleName();
	private String title = "";
	private int selectType;
	private Fragment fragment;

	private ImageView iv_back;
	private TextView tv_ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutId());
		selectType = getIntent().getIntExtra("select_type", TSConfig.SELECT_CHAT_USER);

		initView();
		initListener();
		initFragment();
	}

	private void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_ok = (TextView)findViewById(R.id.tv_ok);
		tv_ok.setAlpha(0.5f);
		//设置按钮不可用
		tv_ok.setEnabled(false);

		iv_back.setOnClickListener(this);
		tv_ok.setOnClickListener(this);

	}

	//改变按钮事件状态
	public void changeButtonState(boolean ok) {
		if(!ok) {
			tv_ok.setEnabled(false);
			tv_ok.setAlpha(0.5f);
		}else {
			tv_ok.setEnabled(true);
			tv_ok.setAlpha(1.0f);
		}
	}

	private void initListener() {

	}

	private void initFragment() {
		fragment = new FragmentSelectUser();
		getSupportFragmentManager().beginTransaction().add(R.id.ll_content, fragment)
				.commit();
	}

	protected int getLayoutId() {
		return R.layout.activity_select_user;
	}

	@Override
	public void onClick(final View v) {
		int id = v.getId();
		if (id == R.id.tv_ok) {
			if (selectType == TSConfig.SELECT_CHAT_USER) {
				List<ModelUser> selectUsers = ((FragmentSelectUser)fragment).getSelectUser();
				if(selectUsers == null || selectUsers.isEmpty()) {
					Toast.makeText(ActivitySelectUser.this, "请选择聊天对象", Toast.LENGTH_SHORT).show();
					return;
				}

				v.setEnabled(false);
				FunctionCreateChat fc = new FunctionCreateChat(selectUsers);
				fc.createChat(new ChatCoreResponseHandler() {
					@Override
					public void onSuccess(Object object) {
						Log.v(TAG, "CREATE ROOM--->onSuccess");
						//前往聊天详情
						ModelChatUserList chat = (ModelChatUserList)object;
						ActivityChatDetail.initChatInfo(chat);
						ActivityStack.startActivity(ActivitySelectUser.this, ActivityChatDetail.class);
						finish();
					}

					@Override
					public void onFailure(Object object) {
						Log.v(TAG, "CREATE ROOM--->onFailure");
						v.setEnabled(true);
					}
				});

			} else if (selectType == TSConfig.CHAT_ADD_USER) {
				// 群组添加成员时候选人
				List<ModelUser> selectlist = ((FragmentSelectUser) fragment).getSelectUser();
				if (selectlist == null || selectlist.isEmpty()) {
					//没有选人
					setResult(RESULT_CANCELED);
				} else {
					//注释
					Intent intent = new Intent();
					intent.putParcelableArrayListExtra("user", (ArrayList<ModelUser>)selectlist);
					setResult(RESULT_OK, intent);
				}

				finish();
			}
			//选择名片对象
			else if (selectType == TSConfig.SELECT_CARD) {
				if (((FragmentSelectUser) fragment).getSelectUser().size() > 1) {
					Toast.makeText(ActivitySelectUser.this, "一次只能发送一张名片", Toast.LENGTH_SHORT).show();
					return;
				}
				//没有选择名片
				else if (((FragmentSelectUser) fragment).getSelectUser().size() == 0) {
					Toast.makeText(ActivitySelectUser.this, "请选择您要发送的名片对象", Toast.LENGTH_SHORT).show();
					return;
				}
				ModelUser selectuser = (ModelUser) ((FragmentSelectUser) fragment).getSelectUser().get(0);
				if (selectuser != null) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", selectuser);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		} else if (id == R.id.iv_back)
		{
			finish();
		}

	}
}