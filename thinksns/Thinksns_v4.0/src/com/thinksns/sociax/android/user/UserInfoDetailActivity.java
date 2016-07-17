package com.thinksns.sociax.android.user;

import android.os.Bundle;
import android.widget.TextView;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author povol
 * @date Jun 13, 2013
 * @version 1.0
 */
public class UserInfoDetailActivity extends ThinksnsAbscractActivity {

	private TextView username, userGen, loaction, userTag, userIntro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ModelUser user = (ModelUser) getIntentData().getSerializable("user");

		username = (TextView) findViewById(R.id.tv_nick);
		userGen = (TextView) findViewById(R.id.tv_gender);
		loaction = (TextView) findViewById(R.id.tv_area);
		userTag = (TextView) findViewById(R.id.tv_tag);
		userIntro = (TextView) findViewById(R.id.tv_intro);

		username.setText("昵称：");
		if (SociaxUIUtils.isNull(user.getUserName())) {
			username.setText(username.getText() + user.getUserName());
		}
		userGen.setText("性别：");
		if (SociaxUIUtils.isNull(user.getSex())) {
			userGen.setText(userGen.getText() + user.getSex());
		}
		loaction.setText("地区：");
		if (SociaxUIUtils.isNull(user.getLocation())) {
			loaction.setText(loaction.getText() + user.getLocation());
		}
		userTag.setText("标签：");
		if (SociaxUIUtils.isNull(user.getUserTag())) {
			userTag.setText(userTag.getText() + user.getUserTag());
		}
		userIntro.setText("简介：");
		if (SociaxUIUtils.isNull(user.getIntro())) {
			userIntro.setText(userIntro.getText() + user.getIntro());
		}
	}

	@Override
	public String getTitleCenter() {
		return getString(R.string.user_info_xx)
				+ getString(R.string.user_info_zl);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.user_detail;
	}

}
