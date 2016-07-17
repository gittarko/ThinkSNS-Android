package com.thinksns.sociax.t4.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterPostDiggUsers;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelDiggUser;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hedong on 16/3/31.
 * 当前帖子的点赞用户列表
 */
public class FragmentPostDigg extends BaseListFragment<ModelDiggUser> {
    private int post_id;

    public static FragmentPostDigg newInstance(int post_id) {
        FragmentPostDigg fragmentPostDigg = new FragmentPostDigg();
        Bundle bundle = new Bundle();
        bundle.putInt("post_id", post_id);
        fragmentPostDigg.setArguments(bundle);
        return fragmentPostDigg;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(getArguments() != null) {
            post_id = getArguments().getInt("post_id", 0);
        }
    }

    //构造数据适配器
    @Override
    protected ListBaseAdapter<ModelDiggUser> getListAdapter() {
        return new AdapterPostDiggUsers(getActivity());
    }

    //构造业务请求类
    @Override
    protected void initPresenter() {
        mPresenter = new BaseListPresenter<ModelDiggUser>(getActivity(), this) {
            @Override
            public ListData<ModelDiggUser> parseList(String result) {
                try{
                    JSONArray jsonObject = new JSONArray(result);
                    return getListData(jsonObject);
                }catch(JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            private ListData<ModelDiggUser> getListData(JSONArray jsonArray) {
                ListData<ModelDiggUser> list = new ListData<ModelDiggUser>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject itemData = jsonArray.getJSONObject(i);
                        list.add(new ModelDiggUser(itemData));
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return list;
            }

            @Override
            protected ListData<ModelDiggUser> readList(Serializable seri) {
                return (ListData<ModelDiggUser>)seri;
            }

            @Override
            public String getCachePrefix() {
                return "digg_list";
            }

            @Override
            public void loadNetData() {
                new Api.WeibaApi().getPostDiggUserList(post_id, getMaxId(), mHandler);
            }
        };

        mPresenter.setCacheKey("post" + post_id);
    }

    //设置列表基础样式
    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setSelector(R.drawable.list_selector);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ModelDiggUser user = mAdapter.getItem((int)id);
        if(user == null)
            return;

        Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }
}
