package com.thinksns.sociax.t4.android.findpeople;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentSelectUser;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionCreateChat;
import com.thinksns.sociax.t4.model.ModelGift;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 选择好友 应用：礼物选择好友
 * 需要传入select_type,默认StaticInApp.SELECT_GIFT_RECEIVER，选人用于礼物接收，
 * StaticInApp.CHAT_ADD_USER 群组添加成员时候选人， StaticInApp.SELECT_GIFT_RESEND_USER单选赠送
 * 
 * @author wz
 * @date 2014-11-18
 * @version 1.0
 */
public class ActivitySelectUser extends ThinksnsAbscractActivity {
	String title = "";
	int type;
	FragmentSociax fragment;
	ModelGift gift;
	private int selectType = StaticInApp.SELECT_GIFT_RECEIVER;//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gift = (ModelGift) getIntent().getSerializableExtra("gift");
		selectType = getIntent().getIntExtra("select_type",
				StaticInApp.SELECT_GIFT_RECEIVER);
		// intent 没有传礼物过来
		if ((selectType == StaticInApp.SELECT_GIFT_RECEIVER) && (gift == null)) {
			Toast.makeText(this, "请先选择礼物", Toast.LENGTH_SHORT).show();
			finish();
		}
		fragment = new FragmentSelectUser();
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();

	}

	@Override
	public String getTitleCenter() {
		return "选择好友";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this, R.drawable.img_back, "确定");
	}

	@Override
	public OnClickListener getRightListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果是礼物选人，则直接发送礼物
				if (selectType == StaticInApp.SELECT_GIFT_RECEIVER) {
					((FragmentSelectUser) fragment).sendGift();
					finish();
				} else if (selectType == StaticInApp.SELECT_GIFT_RESEND) {
					((FragmentSelectUser) fragment).resendGift();
					finish();
				}
				// 创建群组时候选人
				else if (selectType == StaticInApp.SELECT_CHAT_USER) {
					FunctionCreateChat fc = new FunctionCreateChat(
							ActivitySelectUser.this,((FragmentSelectUser) fragment).getSelectUser());
					//群聊
					if (((FragmentSelectUser) fragment).getSelectUser().size() > 1) {
						fc.createChat(false);
						finish();
						return;
					} 
					//没有选择聊天对象
					else if(((FragmentSelectUser) fragment).getSelectUser().size() == 0){
						Toast.makeText(ActivitySelectUser.this, "请选择聊天对象", Toast.LENGTH_SHORT).show();
						return;
					}
					//一对一聊天
					else {
						fc.createChat(true);
					}
					finish();
				} else if (selectType == StaticInApp.CHAT_ADD_USER) {
					// 群组添加成员时候选人
					ListData<SociaxItem> selectlist = ((FragmentSelectUser) fragment).getSelectUser();
					if (selectlist.size() == 0) {
						setResult(RESULT_CANCELED);
					} else {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("input", selectlist);
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
					}
					finish();
				} 
				//选择名片对象
				else if (selectType == StaticInApp.SELECT_CARD) {
					if (((FragmentSelectUser) fragment).getSelectUser().size() > 1) {
						Toast.makeText(ActivitySelectUser.this, "一次只能发送一张名片", Toast.LENGTH_SHORT).show();
						return;
					} 
					//没有选择聊天对象
					else if(((FragmentSelectUser) fragment).getSelectUser().size() == 0){
						Toast.makeText(ActivitySelectUser.this, "请选择您要发送的名片对象", Toast.LENGTH_SHORT).show();
						return;
					}
					ModelSearchUser selectuser =(ModelSearchUser) ((FragmentSelectUser) fragment).getSelectUser().get(0);
					if (selectuser!=null) {
						Intent intent=new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("user", selectuser);
						intent.putExtras(bundle);
						intent.putExtra("uid", selectuser.getUid());
						setResult(RESULT_OK, intent);
						finish();
					}
				}
			}
		};
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_common;
	}

	@Override
	public void refreshHeader() {
		fragment.doRefreshHeader();
	}

	@Override
	public void refreshFooter() {
		fragment.doRefreshFooter();
	}
}