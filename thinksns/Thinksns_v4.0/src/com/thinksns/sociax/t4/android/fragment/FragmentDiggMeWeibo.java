package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterDiggMeWeiboList;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 类说明：赞我的列表
 * @author dong.he
 * @date 2016-3-01
 * @version 1.0
 */
public class FragmentDiggMeWeibo extends BaseListFragment<ModelComment> {

	@Override
	protected void initListViewAttrs() {
		super.initListViewAttrs();
		//设置列表项点击效果
		mListView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));
	}

	//业务控制器，建议与Fragment分离
	@Override
	protected void initPresenter() {
		mPresenter = new BaseListPresenter<ModelComment>(getActivity(), this) {
			@Override
			public ListData<ModelComment> parseList(String result) {
				try{
					JSONArray response = new JSONArray(result);
					return getListData(response);
				}catch(JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			private ListData<ModelComment> getListData(JSONArray jsonArray) {
				ListData<ModelComment> list = new ListData<ModelComment>();
				for (int i = 0; i < jsonArray.length(); i++) {
					try {
						JSONObject itemData = jsonArray.getJSONObject(i);
						list.add(new ModelComment(itemData));
					} catch (DataInvalidException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return list;
			}

			@Override
			protected ListData<ModelComment> readList(Serializable seri) {
				return (ListData<ModelComment>)seri;
			}

			@Override
			public String getCachePrefix() {
				return "weibo_list";
			}

			@Override
			public void loadNetData() {
				new Api.WeiboApi().diggMeWeibo(getPageSize(), getMaxId(), mHandler);
			}
		};

		mPresenter.setCacheKey("digger_me");

	}

	@Override
	protected ListBaseAdapter<ModelComment> getListAdapter() {
		return new AdapterDiggMeWeiboList(getActivity(), "digger", mListView);
	}

	@Override
	public void onLoadDataSuccess(ListData<ModelComment> data) {
		mEmptyLayout.setErrorImag(R.drawable.ic_no_zan);
		mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_user_digg));
		super.onLoadDataSuccess(data);
	}

	//点击跳转到微博详情或者帖子详情
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ModelComment md = mAdapter.getItem((int)id);
		if (md == null || md.getWeibo() == null) {
			return;
		}
		Bundle data = new Bundle();
		Intent intent = null;
		if (md.getWeibo().getType().equals("weiba_post")) {
			intent = new Intent(view.getContext(),ActivityPostDetail.class);
			data.putInt("post_id", md.getWeibo().getSid());
		}else {
			intent = new Intent(view.getContext(), ActivityWeiboDetail.class);
			data.putInt("weibo_id", md.getWeibo() == null ? md.getFeed_id() : md
							.getWeibo().getWeiboId());
		}

		intent.putExtras(data);
		startActivity(intent);
	}
}
