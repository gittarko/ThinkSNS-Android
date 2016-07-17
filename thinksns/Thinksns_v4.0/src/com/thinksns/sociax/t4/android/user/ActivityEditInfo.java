package com.thinksns.sociax.t4.android.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author Administrator
 * @date 2014-11-10
 * @version 1.0
 */
public class ActivityEditInfo extends ThinksnsAbscractActivity {
	int type = 0;
	TextView tv_name;
	EditText ed_info;
	Button bt_save;
	ModelUser user = Thinksns.getMy();
	public static String CENTER_TITLE = "修改昵称";

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
				Intent intent = new Intent();
				intent.putExtra("input", ed_info.getText().toString().trim());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void initView() {
		tv_name = (TextView) findViewById(R.id.tv_name);
		ed_info = (EditText) findViewById(R.id.edit_info);
		bt_save = (Button) findViewById(R.id.bt_save);
		
		//设置光标在文本的末尾
		if (type == StaticInApp.CHANGE_USER_NAME) {
			tv_name.setText("昵称");
			ed_info.setText(user.getUserName());
			
			ed_info.setSelection(user.getUserName().length());
		} else if (type == StaticInApp.CHANGE_USER_INTRO) {
			tv_name.setText("简介");
			ed_info.setText(user.getIntro());
			ed_info.setMinLines(5);
			
			ed_info.setSelection(user.getIntro().length());
		}
	}

	private void initIntentData() {
		type = getIntent().getIntExtra("type", 0);
		if (type == 0)
			finish();
	}

	@Override
	public String getTitleCenter() {
		return CENTER_TITLE;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_edit_info_common_old;
	}
	@Override
	protected void onPause() {
		super.onPause();
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		if (ed_info != null)
			imm.hideSoftInputFromWindow(ed_info.getWindowToken(), 0);
	}
}
