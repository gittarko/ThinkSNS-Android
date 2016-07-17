package com.thinksns.sociax.t4.android.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterUserFollowingListNew;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

/**
 * 类说明： 找人一级目录
 * 
 * @author wz
 * @date 2014-11-4
 * @version 1.0
 */
public class FragmentFindPeople extends BaseListFragment<ModelSearchUser> {

	@Override
	protected boolean requestDataIfViewCreated() {
		return false;
	}

	@Override
	protected void setRefreshMode(Mode mode) {
		super.setRefreshMode(Mode.DISABLED);
	}


	@Override
	protected ListBaseAdapter<ModelSearchUser> getListAdapter() {
		return new AdapterUserFollowingListNew(getActivity()) {
			@Override
			public int getMaxId() {
				return (mDatas == null || mDatas.size() == 0)? 0 :
						((ModelSearchUser)mDatas.get(mDatas.size() -1)).getUid();
			}

			@Override
			protected boolean hasFooterView() {
				return false;
			}
		};
	}

	@Override
	protected void initListViewAttrs() {
		super.initListViewAttrs();
	}

	@Override
	protected void initPresenter() {
		mPresenter = new BaseListPresenter<ModelSearchUser>(getActivity(), this) {

			@Override
			public ListData<ModelSearchUser> parseList(String result) {
				try {


					ListData<ModelSearchUser> listData = new ListData<ModelSearchUser>();
					if(result.startsWith("{") && result.endsWith("}")) {
						//数据异常
						return listData;
					}

					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++) {
						try {
							ModelSearchUser follow = new ModelSearchUser(
									jsonArray.getJSONObject(i));
							listData.add(follow);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
					return listData;
				}catch(JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected ListData<ModelSearchUser> readList(Serializable seri) {
				return (ListData<ModelSearchUser>)seri;
			}

			@Override
			public String getCachePrefix() {
				return "user_list";
			}

			@Override
			public void loadNetData() {
				new Api.Users().searchUser(mHandler, 8);
			}
		};

		mPresenter.setCacheKey("find_people");
	}

	@Override
	public void initData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		mPresenter.loadNetData();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	//请求换一换数据
	public void requestData() {
		//主动刷新，优先从网络获取数据
		mPresenter.loadInitData(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ModelSearchUser user = mAdapter.getItem((int)id);
		if(user != null) {
			Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
			int uid = user.getUid();
			intent.putExtra("uid", uid);
			startActivity(intent);
		}
	}

	@Override
	public void onLoadDataSuccess(ListData<ModelSearchUser> data) {
		mEmptyLayout.setErrorImag(R.drawable.ic_no_yh);
		mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_user));
		super.onLoadDataSuccess(data);
	}

	@Override
	public void onLoadComplete() {
		super.onLoadComplete();
	}
}
