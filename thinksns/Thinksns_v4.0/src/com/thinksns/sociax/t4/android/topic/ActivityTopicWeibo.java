package com.thinksns.sociax.t4.android.topic;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentTopicWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateTopicWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateWeibo;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 某个话题里面的微博，需要传入String topic_name
 * 
 * @author wz
 * @date 2014-12-15
 * @version 1.0
 */
public class ActivityTopicWeibo extends ThinksnsAbscractActivity {
	FragmentTopicWeibo fragment;
	private ImageButton ib_new;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ib_new = (ImageButton) findViewById(R.id.ib_new);
		ib_new.setOnClickListener(getRightListener());

		fragment = FragmentTopicWeibo.newInstance(getIntent().getIntExtra("topic_id", 0),
				getIntent().getStringExtra("topic_name"),
				getIntent().getStringExtra("count"));
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();
	}

	@Override
	public String getTitleCenter() {
		return getIntent().getStringExtra("topic_name");
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this, R.drawable.img_back, null);
	}

	@Override
	public OnClickListener getRightListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
				data.putInt("type", AppConstant.CREATE_TEXT_WEIBO);
				data.putString("topic", getIntent().getStringExtra("topic_name"));
				ActivityStack.startActivity(ActivityTopicWeibo.this, ActivityCreateTopicWeibo.class, data);
			}
		};

	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_topic_weibo;
	}

	/**
	 * 隐藏或显示发布微博按钮
	 * @param isShow
     */
	public void toggleCreateBtn(boolean isShow) {
		if(isShow && ib_new != null) {
			ib_new.setVisibility(View.VISIBLE);
		}else {
			ib_new.setVisibility(View.GONE);
		}
	}

}
