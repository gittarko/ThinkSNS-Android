package com.thinksns.tschat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.tschat.R;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.inter.ChatCoreResponseHandler;
import com.thinksns.tschat.widget.SmallDialog;


/**
 * 类说明：编辑修改群聊资料 需要传入原来的intent String title
 */
public class ActivityChatInfoEdit extends FragmentActivity implements OnClickListener{
	private static final String TAG = ActivityChatInfoEdit.class.getSimpleName();

	TextView tv_name;
	EditText ed_info;
	Button bt_save;
	private static String preTitle, newTitle;		// 原来的群名/新的群名
	private static int room_id;

	private SmallDialog dialog;
	private ImageView iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutId());
		//初始化提示框
		dialog = new SmallDialog(this, "加载中...");
		dialog.setCanceledOnTouchOutside(false);

		initIntentData();
		initView();
		initListener();
	}


	private void initListener() {
		bt_save.setOnClickListener(this);
		iv_back.setOnClickListener(this);
	}

	private void initView() {
		tv_name = (TextView) findViewById(R.id.tv_name);
		ed_info = (EditText) findViewById(R.id.edit_info);
		bt_save = (Button) findViewById(R.id.bt_save);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		bt_save.setText("完成");
		tv_name.setText("群名称");
		if (preTitle!=null && preTitle.length() > 0) {
			ed_info.setText(preTitle);
			ed_info.setSelection(preTitle.length());
		}
	}

	private void initIntentData() {
		preTitle = getIntent().getStringExtra("title");
		room_id = getIntent().getIntExtra("room_id",-1);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (bt_save != null)
		imm.hideSoftInputFromWindow(bt_save.getWindowToken(), 0);
	}

	protected int getLayoutId() {
		return R.layout.activity_edit_info_common;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if(id == R.id.bt_save) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(bt_save.getWindowToken(), 0);
			newTitle = ed_info.getText().toString().trim();
			if (newTitle == null) {
				Toast.makeText(getApplicationContext(), "群名称不能为空",Toast.LENGTH_SHORT).show();
				return;
			}
			if (preTitle.equals(newTitle)) {
				Toast.makeText(getApplicationContext(), "群名称还是一样哦",Toast.LENGTH_SHORT).show();
				return;
			}
			if (room_id!=-1) {
				dialog.show();
				dialog.setContent("请稍后...");

				ModelChatUserList room = ActivityChatDetail.getCurrentRoom();
				room.setTitle(newTitle);
				TSChatManager.changeRoomTitle(room, 1, new ChatCoreResponseHandler() {
					@Override
					public void onSuccess(Object object) {
						Log.v(TAG, "CHANGE ROOM TITLE---->onSuccess");
						Intent intent = new Intent();
						intent.putExtra("input", newTitle);
						setResult(RESULT_OK, intent);
						dialog.dismiss();
						dialog = null;
						finish();
					}

					@Override
					public void onFailure(Object object) {
						Log.v(TAG, "CHANGE ROOM TITLE---->onFailure");
						dialog.dismiss();
						Toast.makeText(ActivityChatInfoEdit.this, "标题设置失败", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onStart(Object object) {
						Log.v(TAG, "CHANGE ROOM TITLE---->onStart");
					}
				});
			}
		}else if(id == R.id.iv_back){
			finish();
		}
	}
}
