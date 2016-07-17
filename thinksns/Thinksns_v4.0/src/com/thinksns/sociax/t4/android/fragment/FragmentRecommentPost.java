package com.thinksns.sociax.t4.android.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterPostRecommendList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

/**
 * 类说明：推荐帖子
 *
 * @author wz
 * @version 1.0
 * @date 2014-9-2
 */
public class FragmentRecommentPost extends FragmentPostList implements OnRefreshListener2<ListView> {
    private PullToRefreshListView pullRefresh;
    //是否允许下拉刷新
    protected boolean downToRefresh = true;
    protected EmptyLayout emptyLayout;

    public static FragmentRecommentPost newInstance(Bundle args) {
        FragmentRecommentPost fragment = new FragmentRecommentPost();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            downToRefresh = getArguments().getBoolean("down_to_refresh", true);
        }
    }

    @Override
    public void initView() {
        pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        if(downToRefresh) {
            pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        }else {
            pullRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        }

        pullRefresh.setOnRefreshListener(this);
        listView = pullRefresh.getRefreshableView();
        //设置列表分割线
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_ios)));
        listView.setDividerHeight(UnitSociax.dip2px(getActivity(), 10));
        //设置列表点击 效果
        listView.setSelector(getResources().getDrawable(R.drawable.list_selector));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelPost md = (ModelPost) adapter.getItem((int)id);
                if (md == null) {
                    return;
                }

                Bundle data = new Bundle();
                data.putParcelable("post", md);
                ActivityStack.startActivity(getActivity(), ActivityPostDetail.class, data);
            }
        });

        //缺省控件初始化
        emptyLayout = (EmptyLayout)findViewById(R.id.empty_layout);
        emptyLayout.setNoDataContent(getResources().getString(R.string.empty_post));

        list = new ListData<SociaxItem>();
        adapter = new AdapterPostRecommendList(this, list);
        listView.setAdapter(adapter);

    }

    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
        //加载出错点击重新加载
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public EmptyLayout getEmptyLayout() {
        return emptyLayout;
    }

    @Override
    public void initData() {
        adapter.loadInitData();
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullRefresh;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_common_post_list;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (adapter != null) {
            adapter.doRefreshHeader();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (adapter != null) {
            adapter.doRefreshFooter();
        }
    }

    @Override
    public void executeDataSuccess(ListData<SociaxItem> list) {
        if(list == null
                || list.size() < AdapterSociaxList.PAGE_COUNT) {
            if(adapter.getLast() != null) {
                Toast.makeText(getActivity(), getResources().getString(R.string.empty_more_data), Toast.LENGTH_SHORT).show();
                pullRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }

            if(!downToRefresh) {
                pullRefresh.setMode(PullToRefreshBase.Mode.DISABLED);
            }
        }else {
        }

        super.executeDataSuccess(list);
    }
}
