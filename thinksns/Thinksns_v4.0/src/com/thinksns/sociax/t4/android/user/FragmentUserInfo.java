package com.thinksns.sociax.t4.android.user;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterUserInfo;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.interfaces.OnTabListener;
import com.thinksns.sociax.t4.android.presenter.UserInfoPresenter;

import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by hedong on 16/2/25.
 */
public class FragmentUserInfo extends BaseListFragment<ModelUser> implements IBaseListView<ModelUser>,
        OnTabListener{
    int userId;
    String userName;
    private Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
            userName = getArguments().getString("userName");
        }
    }

    @Override
    protected void initPresenter() {
        String key = "";
        if(userName != null) {
            key = userName;
        }else{
            if(userId == -1 || userId == 0)
                userId = Thinksns.getMy().getUid();
            key = String.valueOf(userId);
        }

        mPresenter = new UserInfoPresenter(getActivity(), this, userId, userName);
        mPresenter.setCacheKey(key);
    }

    @Override
    protected void setRefreshMode(PullToRefreshBase.Mode mode) {
        super.setRefreshMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    protected ListBaseAdapter<ModelUser> getListAdapter() {
        return new AdapterUserInfo(getActivity());
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        return false;
    }

    @Override
    protected boolean needShowEmptyNoData() {
        return false;
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelUser> data) {
        super.onLoadDataSuccess(data);
    }

    public  int setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View listItem = mAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT,
                        AbsListView.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        return totalHeight;
//        listView.setLayoutParams(params);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//        int distance = getScroll();
//        ScrollViewSociax scrollViewSociax = ((ActivityUserHome)getActivity()).getScrollView();
//        scrollViewSociax.scrollTo(0, distance);
//        scrollViewSociax.setY(-distance);
    }

    private int getScroll() {
        View c = mListView.getChildAt(0);   //this is the first visible row
        int scrollY = 0;
        if(c != null) {
            scrollY = -c.getTop();
            int firstPosition = mListView.getFirstVisiblePosition();
            listViewItemHeights.put(firstPosition, c.getHeight());
            for (int i = 0; i < firstPosition; ++i) {
                if (listViewItemHeights.get(i) != null)         // (this is a sanity check)
                    scrollY += listViewItemHeights.get(i);      //add all heights of the views that are gone
            }
        }
        Log.e("FragmentUserInfo", "scrolly " + scrollY);
        return scrollY;
    }

    @Override
    public void onTabClickListener() {
        if(mAdapter.getData().size() == 0) {
            mPresenter.loadInitData(true);
        }
    }
}
