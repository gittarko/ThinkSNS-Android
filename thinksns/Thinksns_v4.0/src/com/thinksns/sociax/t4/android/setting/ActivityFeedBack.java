package com.thinksns.sociax.t4.android.setting;

import org.json.JSONException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelFeedBack;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 意见反馈
 * @author wz
 * @date 2015-1-26
 * @version 1.0
 */
public class ActivityFeedBack extends ThinksnsAbscractActivity {
	LinearLayout ll_feedback_type;
	TextView tv_feedback_type;
	int type_id = -1;
	EditText ed_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initListener();
	}

	private void initListener() {
		ll_feedback_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityFeedBack.this,ActivityFeedBackType.class);
				startActivityForResult(intent, StaticInApp.GET_FEEDBACK_TYPE);
			}
		});
	}

	private void initView() {
		ll_feedback_type = (LinearLayout) findViewById(R.id.ll_feedback_type);
		tv_feedback_type = (TextView) findViewById(R.id.tv_feedback_type);
		ed_content = (EditText) findViewById(R.id.et_feedback_content);
	}

	@Override
	public OnClickListener getRightListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				doSublicTask();
			}
		};
	}

	private void doSublicTask() {
		UnitSociax.hideSoftKeyboard(this, ed_content);
		new FeedBackTask().execute(ed_content.getText().toString().trim());
	}

	class FeedBackTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				return new Api.Users().addFeedBack(params[0]);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			ModelBackMessage md;
			try {
				md = new ModelBackMessage(result);
				Toast.makeText(ActivityFeedBack.this, md.getMsg(),
						Toast.LENGTH_SHORT).show();
				if (md.getStatus() == 1) {
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(ActivityFeedBack.this, "数据解析错误",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public String getTitleCenter() {
		return "意见反馈";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this, R.drawable.img_back, "发送");
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_feed_back;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == StaticInApp.GET_FEEDBACK_TYPE) {
			if (arg2 != null && arg2.hasExtra("type")) {
				ModelFeedBack md = (ModelFeedBack) arg2
						.getSerializableExtra("type");
				tv_feedback_type.setText(md.getType_name());
				type_id = md.getType_id();
			}
		}
	}
}
