package com.thinksns.sociax.t4.android.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.interfaces.OnPopupWindowClickListener;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowCommon;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;

import java.util.ArrayList;
import java.util.List;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年10月14日
 * @version 1.0
 */
public class ActivityChangeSex extends ThinksnsAbscractActivity {

	private TextView tv_sex;
	private LinearLayout ll_change_sex;
	private Button btn_save;
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
		tv_sex=(TextView)this.findViewById(R.id.tv_sex);
		ll_change_sex=(LinearLayout)this.findViewById(R.id.ll_change_sex);
		btn_save=(Button)this.findViewById(R.id.btn_save);
		iv_back=(ImageView)this.findViewById(R.id.iv_back);
		
		tv_sex.setText(user.getSex());
	}

	private void initListener() {
		iv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ll_change_sex.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(v.getContext());
				builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if(position == 0) {
							tv_sex.setText("男");
						}else {
							tv_sex.setText("女");
						}
					}
				});
				List<String> datas = new ArrayList<String>();
				datas.add("男");
				datas.add("女");
				builder.create(datas);
			}
		});

		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("input", tv_sex.getText().toString().trim());
				setResult(RESULT_OK, intent);
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
		return R.layout.activity_edit_sex;
	}
}
