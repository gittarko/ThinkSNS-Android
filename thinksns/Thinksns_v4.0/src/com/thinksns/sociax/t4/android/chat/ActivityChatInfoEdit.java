package com.thinksns.sociax.t4.android.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 编辑修改群聊资料 需要传入原来的intent String title
 * 
 * @author wz
 * @date 2014-11-28
 * @version 1.0
 */
public class ActivityChatInfoEdit extends ThinksnsAbscractActivity {
	int type = 0;
	TextView tv_name;
	EditText ed_info;
	Button bt_save;
	ModelUser user = Thinksns.getMy();
	private static String preTitle, newTitle;// 原来的群名/新的群名
	private static int room_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initIntentData();
		initView();
		initListener();
		initData();
	}

	private void initData() {

	}

	private void initListener() {
		bt_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(bt_save.getWindowToken(), 0);
				Intent intent = new Intent();
				newTitle = ed_info.getText().toString().trim();
				if (newTitle==null) {
					Toast.makeText(getApplicationContext(), "群名称不能为空",Toast.LENGTH_SHORT).show();
					return;
				}
				if (preTitle.equals(newTitle)) {
					Toast.makeText(getApplicationContext(), "群名称还是一样哦",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (room_id!=-1) {
					//注释
//					((Thinksns) getApplicationContext()).getChatSocketClient().changeTitle(room_id, newTitle);
				}
				intent.putExtra("input", newTitle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void initView() {
		tv_name = (TextView) findViewById(R.id.tv_name);
		ed_info = (EditText) findViewById(R.id.edit_info);
		bt_save = (Button) findViewById(R.id.bt_save);
		bt_save.setText("完成");

		tv_name.setText("群名称");
		if (preTitle!=null) {
			ed_info.setText(preTitle);
		}
	}

	private void initIntentData() {
		preTitle = getIntent().getStringExtra("title");
		room_id = getIntent().getIntExtra("room_id",-1);
//		if (preTitle.equals("")||preTitle==null) {
//			finish();
//		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (bt_save != null)
		imm.hideSoftInputFromWindow(bt_save.getWindowToken(), 0);
	}

	@Override
	public String getTitleCenter() {
		return "群名称修改";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_edit_info_common_old;
	}
}
