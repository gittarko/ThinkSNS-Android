package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterFindPeopleByVerify;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 官方认证 需要传入intent string verify_id
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-4
 */
public class FragmentFindPeopleByVerify extends FragmentSociax {

    protected ModelUser selectUser;
    protected int selectpostion;
    private String verify_id;

    private PullToRefreshListView pullToRefreshListView;

    private LinearLayout title_layout;

    @Override
    public void initView() {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setDivider(new ColorDrawable(0xffdddddd));
        listView.setDividerHeight(1);
        listView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));

        adapter = createAdapter();
        listView.setAdapter(adapter);
        title_layout = (LinearLayout) findViewById(R.id.title_layout);

        title_layout.setVisibility(View.GONE);
    }

    @Override
    public AdapterSociaxList createAdapter() {
        list = new ListData<SociaxItem>();
        return new AdapterFindPeopleByVerify(this, list, getActivity()
                .getIntent().getIntExtra("uid", Thinksns.getMy().getUid()),
                verify_id);
    }

    @Override
    public void initIntentData() {
        if (getActivity().getIntent().hasExtra("verify_id")) {
            verify_id = getActivity().getIntent().getStringExtra("verify_id");
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
                if (list.size() > 0) {
                    ModelSearchUser user = (ModelSearchUser) adapter.getItem((int) id);
                    Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
                    intent.putExtra("uid", user.getUid());
                    startActivity(intent);
                }
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
