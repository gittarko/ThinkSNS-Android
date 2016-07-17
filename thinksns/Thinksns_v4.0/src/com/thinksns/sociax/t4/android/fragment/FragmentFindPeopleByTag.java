package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterFindPeopleByTag;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 根据标签找人
 *
 * 需要传入intent int tag_id
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-4
 */
public class FragmentFindPeopleByTag extends FragmentSociax {
    protected int selectpostion;

    private int tag_id;
    private PullToRefreshListView pullToRefreshListView;

    @Override
    public void initView() {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_ios)));
        listView.setDividerHeight(1);
        listView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));

        adapter = createAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    public AdapterSociaxList createAdapter() {
        list = new ListData<SociaxItem>();
        return new AdapterFindPeopleByTag(this, list, getActivity()
                .getIntent().getIntExtra("uid", Thinksns.getMy().getUid()),
                tag_id);
    }

    @Override
    public void initIntentData() {
        if (getActivity().getIntent().hasExtra("tag_id")) {
            tag_id = getActivity().getIntent().getIntExtra("tag_id", 0);
        } else {
            Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Override
    public void initListener() {
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelSearchUser user = (ModelSearchUser) adapter.getItem((int) id);
                Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
                intent.putExtra("uid", user.getUid());
                startActivity(intent);
            }
        });
    }

    @Override
    public View getDefaultView() {
        return findViewById(R.id.default_people_bg);
    }

    @Override
    public void initData() {
        adapter.loadInitData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chat_userlist;
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullToRefreshListView;
    }
}
