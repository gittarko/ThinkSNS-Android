package com.thinksns.sociax.t4.android.fragment;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterBlackList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

/**
 * 类说明：黑名单
 * @version 1.0
 */
public class FragmentBlackList extends BaseListFragment<ModelSearchUser> {
	@Override
	protected void initPresenter() {
		mPresenter = new BaseListPresenter<ModelSearchUser>(getActivity(), this) {
			@Override
			public ListData<ModelSearchUser> parseList(String result) {
				try {
					ListData<ModelSearchUser> listData = new ListData<ModelSearchUser>();
					JSONArray response = new JSONArray(result);
					for (int i = 0; i < response.length(); i++) {
						ModelSearchUser follow = new ModelSearchUser(
								response.getJSONObject(i));
						if (follow.getUid() != 0)
							listData.add(follow);
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
				new Api.Users().getUserBlackList(Thinksns.getMy().getUid(), getMaxId(), mHandler);
			}
		};

		mPresenter.setCacheKey("user_blacklist");
	}

	@Override
	protected ListBaseAdapter<ModelSearchUser> getListAdapter() {
		return new AdapterBlackList(getActivity());
	}

	@Override
	public void onLoadDataSuccess(ListData<ModelSearchUser> data) {
		mEmptyLayout.setErrorImag(R.drawable.ic_no_yh);
		mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_user));
		super.onLoadDataSuccess(data);
	}
}
