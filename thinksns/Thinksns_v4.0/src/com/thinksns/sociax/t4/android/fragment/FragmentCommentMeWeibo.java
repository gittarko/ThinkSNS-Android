package com.thinksns.sociax.t4.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterCommentMeWeiboList;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.model.ModelComment;
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
 * 类说明：评论我的列表
 * @author dong.he
 */
public class FragmentCommentMeWeibo extends BaseListFragment<ModelComment> {
    private int type = 1;

    public static FragmentCommentMeWeibo newInstance(int type) {
        FragmentCommentMeWeibo commentMeWeibo = new FragmentCommentMeWeibo();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        commentMeWeibo.setArguments(bundle);
        return commentMeWeibo;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(getArguments() != null) {
            type = getArguments().getInt("type", 1);
        }
    }

    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setSelector(getResources().getDrawable(R.drawable.list_selector));
    }

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
                if(type == 1)
                    return "weibo_list";
                else
                    return "weiba_list";
            }

            @Override
            public void loadNetData() {
                if(type == 1) {
                    new Api.WeiboApi().commentMeWeibo(getPageSize(), getMaxId(), null, mHandler);
                }else {
                    new Api.WeiboApi().commentMeWeibo(getPageSize(), getMaxId(), "weiba_post", mHandler);
                }
            }
        };

        mPresenter.setCacheKey("comment_me");
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ModelComment md = mAdapter.getItem((int)id);
        if (md == null || md.getWeibo() == null) {
            return;
        }

        Bundle data = new Bundle();
        Intent intent = null;
        if (md.getWeibo().getType().equals("weiba_post")) {
            //来自微吧的评论
            intent = new Intent(view.getContext(),ActivityPostDetail.class);
            data.putInt("post_id", md.getWeibo().getSid());
        }else {
            //来自微博的评论
            intent = new Intent(view.getContext(), ActivityWeiboDetail.class);
            data.putInt("weibo_id", md.getWeibo() == null ? md.getFeed_id() : md
                    .getWeibo().getWeiboId());
        }

        data.putSerializable("comment", md);

        intent.putExtras(data);
        startActivity(intent);
    }

    @Override
    protected ListBaseAdapter<ModelComment> getListAdapter() {
        return new AdapterCommentMeWeiboList(getActivity(), "comment_me", mListView);
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelComment> data) {
//        mEmptyLayout.setErrorImag(R.drawable.ic_no_pl);
        mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_user_comment));
        super.onLoadDataSuccess(data);
    }

}
