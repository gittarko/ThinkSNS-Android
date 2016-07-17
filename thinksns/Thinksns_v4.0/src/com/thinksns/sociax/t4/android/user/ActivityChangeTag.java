package com.thinksns.sociax.t4.android.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年10月14日
 * @version 1.0
 */
public class ActivityChangeTag extends ThinksnsAbscractActivity {

	private ImageView iv_back;
	ModelUser user = Thinksns.getMy();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		initIntentData();
		initView();
		initListener();
		initData();
	}
	
	private void initIntentData() {
		
	}

	private void initView() {
		iv_back=(ImageView)this.findViewById(R.id.iv_back);
		
	}

	private void initListener() {
		iv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initData() {
		
	}

	@Override
	public String getTitleCenter() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_edit_tag;
	}
}
