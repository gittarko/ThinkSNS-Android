package com.thinksns.sociax.t4.android.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentUserFollowingListNew;
import com.thinksns.sociax.t4.unit.UnitSociax;

/**
 * 类说明： 关注/粉丝 intent String type=following 关注的人，follow粉丝 ，默认为粉丝 需要传入 int uid
 * 用户id，默认为当前登录用户
 * 
 * @author wz
 * @date 2014-10-30
 * @version 1.0
 */
public class ActivityFollowUser extends ThinksnsAbscractActivity {
	private String type = "following";
	private int uid;
	private Fragment fragment;
	private RelativeLayout searchBarContainer;
	private EditText input_search_query;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        initIntent();
		super.onCreate(savedInstanceState);

		fragment = new FragmentUserFollowingListNew();
		Bundle bundle = new Bundle();
		bundle.putInt("uid", uid);
		if (type.equals("following")) {
			bundle.putInt("type", 1);
		}
		else {
			bundle.putInt("type", 0);
		}

		fragment.setArguments(bundle);
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();

        initView();
		initListener();
	}

    public void initIntent(){
        if (getIntent().hasExtra("type")) {
            type = getIntent().getStringExtra("type");
        }
        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getIntExtra("uid",-1);
            this.setUid(uid);
        }
    }

    public void initView(){
        searchBarContainer=(RelativeLayout)this.findViewById(R.id.searchBarContainer);
        searchBarContainer.setVisibility(View.VISIBLE);
        input_search_query=(EditText) this.findViewById(R.id.input_search_query);
    }

    public void initData(){

    }

	@Override
	public String getTitleCenter() {
		return type.equals("following") ? "关注" : "粉丝";
	}

	public void initListener(){
        //修改软键盘样式为搜索样式
        UnitSociax.setSoftKeyBoard(input_search_query, this);
		//重写软键盘的确定键改为搜索键
		input_search_query.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“SEARCH”键*/
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*隐藏软键盘*/
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}

					initEdit();
					//按下软键盘上的搜索按钮时，执行搜索功能
                    ((FragmentUserFollowingListNew)fragment).setName(input_search_query.getText().toString().trim());

					return true;
				}
				return false;
			}
		});
	}

	public void initEdit() {
		if (TextUtils.isEmpty(input_search_query.getText().toString().trim())) {
			Toast.makeText(ActivityFollowUser.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_common;
	}

	@Override
	public void refreshHeader() {
//		fragment.doRefreshHeader();
	}

	@Override
	public void refreshFooter() {
//		fragment.doRefreshFooter();
	}
}
