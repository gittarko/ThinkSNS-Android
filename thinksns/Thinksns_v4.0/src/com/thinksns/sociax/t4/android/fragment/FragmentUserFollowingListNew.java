package com.thinksns.sociax.t4.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterUserFollowingListNew;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hedong on 16/2/29.
 * 用户关注、粉丝列表
 */
public class FragmentUserFollowingListNew extends BaseListFragment<ModelSearchUser> {
    private int type;
    private int uid;
    public String name="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        /**搜索无需保存缓存**/
        mPresenter.setSaveCache(false);
        mPresenter.loadInitData(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            type = getArguments().getInt("type", 0);  //获取用户列表 1：关注 0：粉丝列表
            uid = getArguments().getInt("uid", 0);
        }
        if(uid == 0) {
            uid = Thinksns.getMy().getUid();
        }
        name=getName();
    }

    @Override
    protected ListBaseAdapter<ModelSearchUser> getListAdapter() {
        return new AdapterUserFollowingListNew(getActivity());
    }

    @Override
    protected void initPresenter() {
        mPresenter = new UserFollowingPresenter(getActivity(), this);
        if(type == 1) {
            //关注
            mPresenter.setCacheKey("following");
        }else {
            //粉丝
            mPresenter.setCacheKey("follow");
        }
    }

    @Override
    protected void initListViewAttrs() {
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_ios)));
        mListView.setDividerHeight(UnitSociax.dip2px(getActivity(), 0.5f));
        mListView.setSelector(R.drawable.listitem_selector);
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelSearchUser> data) {
        //设置缺省图文
        if(type == 1) {
            mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_user_followed));
        }else {
            mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_user_following));
        }
//        mEmptyLayout.setErrorImag(R.drawable.ic_no_yh);

        super.onLoadDataSuccess(data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ModelSearchUser user = mAdapter.getItem((int)id);
        if(user != null) {
            //点击列表进入用户主页
            Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
            intent.putExtra("uid", user.getUid());
            startActivity(intent);
        }
    }

    private class UserFollowingPresenter extends BaseListPresenter<ModelSearchUser> {

        public UserFollowingPresenter(Context context, IBaseListView<ModelSearchUser> baseListView) {
            super(context, baseListView);
        }

        @Override
        public ListData<ModelSearchUser> parseList(String result) {
            ListData<ModelSearchUser> listData = new ListData<ModelSearchUser>();
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelSearchUser follow;
                    try {
                        follow = new ModelSearchUser(jsonArray.getJSONObject(i));
                        if (follow.getUid() != 0)
                            listData.add(follow);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            return listData;
        }

        @Override
        protected ListData<ModelSearchUser> readList(Serializable seri) {
            return (ListData<ModelSearchUser>)seri;
        }

        @Override
        public String getCachePrefix() {
            return "user_list_";
        }

        @Override
        public void loadNetData() {
            if(type == 1) {
                //获取关注的人列表
                new Api.Users().getUserFollowingList(uid,name, getMaxId(), mHandler);
            }else {
                new Api.Users().getUserFollowList(uid,name, getMaxId(), mHandler);
            }
        }
    }
}
