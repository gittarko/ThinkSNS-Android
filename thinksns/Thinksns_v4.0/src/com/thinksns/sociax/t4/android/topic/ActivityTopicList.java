package com.thinksns.sociax.t4.android.topic;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentTopicList;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：话题
 * 
 * @author Zoey
 * @date 2015-7-1
 * @version 1.0
 */
public class ActivityTopicList extends ThinksnsAbscractActivity {

	protected ListData<SociaxItem> list;// 数据list
	protected ModelUser user;		// 当前界面所属用户
	protected int uid;				// 当前界面所属uid

	private Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFragment();
	}

	private void initFragment() {
		fragment = new FragmentTopicList();
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();
	}


	@Override
	public String getTitleCenter() {
		return "话题";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}


	@Override
	protected int getLayoutId() {
		return R.layout.activity_common;
	}

}
