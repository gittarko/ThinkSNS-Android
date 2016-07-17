package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.adapter.AdapterWeibaList;
import com.thinksns.sociax.t4.android.weiba.ActivitySearchWeiba;
import com.thinksns.sociax.t4.android.weiba.ActivityWeibaCommon;
import com.thinksns.sociax.t4.android.weiba.ActivityWeibaDetail;
import com.thinksns.sociax.t4.model.ModelWeiba;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;

/**
 * 类说明：我加入的微吧
 *
 * @author wz
 * @version 1.0
 * @date 2014-9-2
 */
public class FragmentWeibaList extends FragmentSociax implements OnRefreshListener2<ListView> {
    protected PullToRefreshListView pullRefresh;
    //是否允许下拉刷新
    protected boolean downToRefresh = true;

    private View headerView;
    private Button btn_add_weiba;
    private EmptyLayout emptyLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {
        headerView = getActivity().getLayoutInflater().inflate(R.layout.header_my_follow_is_null, null);
        //添加微吧
        btn_add_weiba = (Button) headerView.findViewById(R.id.btn_add_weiba);
        pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        if(downToRefresh) {
            pullRefresh.setMode(Mode.PULL_FROM_START);
        }else {
            pullRefresh.setMode(Mode.PULL_FROM_END);
        }

        pullRefresh.setOnRefreshListener(this);
        listView = pullRefresh.getRefreshableView();
        //设置列表分割线
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_listview_divider)));
        listView.setDividerHeight(UnitSociax.dip2px(getActivity(), 0.3f));
        //设置列表点击效果
        listView.setSelector(getResources().getDrawable(R.drawable.list_selector));
        listView.addHeaderView(headerView);

        //空置页面设置
        emptyLayout = (EmptyLayout)findViewById(R.id.empty_layout);
        emptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));
        adapter = createAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    public AdapterSociaxList createAdapter() {
        list = new ListData<SociaxItem>();
        return new AdapterWeibaList(this, list, headerView, listView);
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullRefresh;
    }

    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                ModelWeiba md = (ModelWeiba) listView.getAdapter().getItem((int)position);
                if (md == null || md.getWeiba_name() == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), ActivityWeibaDetail.class);
                intent.putExtra("weiba", (Serializable)md);
                startActivity(intent);
            }
        });

        btn_add_weiba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), ActivitySearchWeiba.class));
                Intent intent = new Intent(v.getContext(), ActivityWeibaCommon.class);
                intent.putExtra("name", "全部微吧");
                intent.putExtra("type", ActivityWeibaCommon.FRAGMENT_WEIBA_ALL);
                v.getContext().startActivity(intent);
            }
        });

        //设置加载出错时点击重新加载
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getMaxid() == 0)
                    adapter.loadInitData();
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
    public int getLayoutId() {
        return R.layout.fragment_common_weiba_list;
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //收到其他更新通知刷新微吧列表
    @Subscribe
    public void refreshWeibaList(ModelWeiba weiba) {
        if(getActivity() == null
                || getActivity().isFinishing())
            return;
        if(adapter != null) {
            adapter.doUpdataList();
        }
    }

    @Override
    public void executeDataSuccess(ListData<SociaxItem> list) {
        super.executeDataSuccess(list);
    }
}
