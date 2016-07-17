package com.thinksns.sociax.t4.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;

import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

/**
 * 类说明： 某个用户的微博列表,需要传入uid
 * 个人头像无需使用点击效果
 * @author wz
 * @date 2014-10-17
 * @version 1.0
 */
public class AdapterUserWeiboList extends AdapterWeiboList {
	/**
	 * Fragment生成
	 * 
	 * @param fragment
	 * @param list
	 */
	public AdapterUserWeiboList(FragmentSociax fragment,
								ListData<SociaxItem> list, int uid) {
		super(fragment, list, uid);
		this.uid = uid;
		clickHead = false;
		isHideFootToast = true;
	}

	public AdapterUserWeiboList(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list, int uid) {
		super(context, list, uid);
		this.uid = uid;
		clickHead = false;
		isHideFootToast = true;

	}

	public void setContentHeight(int height) {
		this.contentHeight = height;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = super.getView(position, convertView, parent);
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ModelWeibo weibo = (ModelWeibo)v.getTag(R.id.tag_weibo);
				if(weibo != null) {
					Intent intent = new Intent(context, ActivityWeiboDetail.class);
					Bundle data = new Bundle();
					data.putSerializable("weibo", weibo);
					intent.putExtras(data);
					context.startActivity(intent);
				}
			}
		});
		return itemView;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApiWeibo().myWeibo(uid, PAGE_COUNT, 0, httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return refreshNew(PAGE_COUNT);
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApiWeibo().myWeibo(uid, PAGE_COUNT, getMaxid(), httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void addFooter(ListData<SociaxItem> list) {
		super.addFooter(list);
		if(context.getPullRefreshView() != null) {
			context.getPullRefreshView().onRefreshComplete();
			if(this.list.size() < PAGE_COUNT) {
				//返回的内容少于一页
				context.getPullRefreshView().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
			}else {
				context.getPullRefreshView().setMode(PullToRefreshBase.Mode.BOTH);
			}
		}
	}

	public void loadInitDataWithNoLoadingView() {
		if (!UnitSociax.isNetWorkON(context)) {
			Toast.makeText(context, R.string.net_fail, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (this.getCount() == 0) {
			ListData<SociaxItem> cache = Thinksns.getLastWeiboList();
			if (cache != null) {
				this.addHeader(cache);
			} else {
				setLoadingView();
				refreshNewSociaxList();
			}
		}
	}
}
