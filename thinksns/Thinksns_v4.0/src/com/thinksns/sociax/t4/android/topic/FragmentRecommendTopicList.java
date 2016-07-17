package com.thinksns.sociax.t4.android.topic;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.modle.RecentTopic;
import com.thinksns.sociax.t4.adapter.AdapterTopicfromT3;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hedong on 16/3/9.
 * 创建微博时，选择插入一条话题
 */
public class FragmentRecommendTopicList extends BaseListFragment<RecentTopic> {
    private AtTopicActivity topicActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof AtTopicActivity)
            topicActivity = (AtTopicActivity)activity;
    }

    public static FragmentRecommendTopicList newInstance() {
        FragmentRecommendTopicList fragmentRecommendTopicList = new FragmentRecommendTopicList();
        return fragmentRecommendTopicList;
    }

    @Override
    protected ListBaseAdapter<RecentTopic> getListAdapter() {
        return new AdapterTopicfromT3(getActivity());
    }

    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));
    }

    @Override
    protected void initPresenter() {
        mPresenter = new BaseListPresenter<RecentTopic>(getActivity(), this) {
            @Override
            public ListData<RecentTopic> parseList(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    ListData<RecentTopic> listData = new ListData<RecentTopic>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonTopic = jsonArray.getJSONObject(j);
                        RecentTopic reTopic = new RecentTopic(jsonTopic);
                        listData.add(reTopic);
                    }
                    return listData;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected ListData<RecentTopic> readList(Serializable seri) {
                return (ListData<RecentTopic>)seri;
            }

            @Override
            public String getCachePrefix() {
                return "topic_list";
            }

            @Override
            public void loadNetData() {
                new Api.Users().getRecentTopic(getPageSize(),getMaxId(), mHandler);
            }
        };
        mPresenter.setCacheKey("recommend_topic");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecentTopic topic = mAdapter.getItem((int)id);
        if(topic == null) {
            return;
        }

        EventBus.getDefault().post(topic);
        if(topicActivity != null)
            topicActivity.setResult(topic.getName());
    }

    @Override
    public void onLoadDataSuccess(ListData<RecentTopic> data) {
        mEmptyLayout.setErrorImag(R.drawable.ic_no_nr);
        mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));
        super.onLoadDataSuccess(data);
    }
}
