package com.thinksns.sociax.t4.android.weiba;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentPostDetail;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowPostMore;

/** 
 * 类说明：   帖子详情
 * 需要传入帖子  ModelPost post或者int post_id；
 * 传入post可能会序列化错误，建议传入post_id
 * @author  wz    
 * @date    2014-12-24
 * @version 1.0
 */
public class ActivityPostDetail extends ThinksnsAbscractActivity{
	FragmentPostDetail fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFragment();
	}

	/**
	 * 根据传入的title获取对应的fragment
	 */
	private void initFragment() {
		fragment = new FragmentPostDetail();
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this,R.drawable.img_back,R.drawable.ic_share_more);
	}
	@Override
	public OnTouchListListener getListView() {
		return fragment.getListView();
	}

	@Override
	public void refreshHeader() {
		fragment.doRefreshHeader();
	}

	@Override
	public void refreshFooter() {
		fragment.doRefreshFooter();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_common_noloadingview;
	}

	@Override
	public String getTitleCenter() {
		return "帖子详情";
	}

	@Override
	public View.OnClickListener getRightListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(fragment.getPost() == null)
					return;

				PopupWindowPostMore pup = new PopupWindowPostMore(v.getContext(), fragment.getPost());
				PopupWindow popupWindow = pup.getPopupWindowInstance();
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				} else {
					popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				}
			}
		};
	}
}
