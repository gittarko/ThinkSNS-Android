package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterTopicList;
import com.thinksns.sociax.t4.android.topic.ActivityTopicWeibo;
import com.thinksns.sociax.t4.model.ModelTopic;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 类说明： 话题列表
 */
public class FragmentTopicList extends BaseListFragment<ModelTopic> {

	@Override
	protected ListBaseAdapter<ModelTopic> getListAdapter() {
		return new AdapterTopicList(getActivity());
	}

	@Override
	protected void initPresenter() {
		mPresenter = new BaseListPresenter<ModelTopic>(getActivity(), this) {
			@Override
			public ListData<ModelTopic> parseList(String result) {
				ListData<ModelTopic> returnlist = new ListData<ModelTopic>();
				JSONArray commends = null, lists = null;// 推荐话题、普通话题
				try{
					JSONObject response = new JSONObject(result);
					if (response.has("commends")) {
						commends = response.getJSONArray("commends");
						for (int i = 0; i < commends.length(); i++) {
							ModelTopic mdi = new ModelTopic(
									commends.getJSONObject(i));
							if (i == 0 && maxId == 0) {// 只有第一页才显示标题
								mdi.setFirst(true);
							}
							returnlist.add(mdi);
						}
					}
					if (response.has("lists")) {
						lists = response.getJSONArray("lists");
						for (int i = 0; i < lists.length(); i++) {
							ModelTopic mdi = new ModelTopic(lists.getJSONObject(i));
							if (i == 0 && maxId == 0) {
								mdi.setFirst(true);
							}
							returnlist.add(mdi);
						}
					}
					return returnlist;
				}catch(JSONException e) {
					e.printStackTrace();
				}

				return  null;
			}

			@Override
			protected ListData<ModelTopic> readList(Serializable seri) {
				return (ListData<ModelTopic>)seri;
			}

			@Override
			public String getCachePrefix() {
				return "weibo_list";
			}

			@Override
			public void loadNetData() {
				new Api.WeiboApi().getAllTopic(getPageSize(), getMaxId(), mHandler);
			}
		};

		mPresenter.setCacheKey("topic_list");
	}

	@Override
	protected void initListViewAttrs() {
		super.initListViewAttrs();
		mListView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ModelTopic md = (ModelTopic)mAdapter.getItem((int)id);
		if(md == null)
			return;

		Intent data = new Intent(getActivity(), ActivityTopicWeibo.class);
		data.putExtra("topic_id", md.getTopic_id());
		data.putExtra("topic_name", md.getTopic_name());
		data.putExtra("count", md.getCount());
		startActivity(data);

	}

	@Override
	public void onLoadDataSuccess(ListData<ModelTopic> data) {
		mEmptyLayout.setErrorImag(R.drawable.ic_no_nr);
		mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));
		super.onLoadDataSuccess(data);
	}
}
