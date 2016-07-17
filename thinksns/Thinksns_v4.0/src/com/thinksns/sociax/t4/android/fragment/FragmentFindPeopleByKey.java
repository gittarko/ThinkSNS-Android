package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterFindPeopleByKey;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;

import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class FragmentFindPeopleByKey extends FragmentSociax implements View.OnClickListener{
    private PullToRefreshListView pullToRefreshListView;
    private Button btn_search;
    private EditText et_search;

    @Override
    public void initView() {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView = pullToRefreshListView.getRefreshableView();
        //设置列表分割线样式
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_ios)));
        listView.setDividerHeight(1);
        listView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));

        list = new ListData<SociaxItem>();
        btn_search = (Button) findViewById(R.id.btn_search);
        et_search = (EditText) findViewById(R.id.et_search);
    }


    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.getDataSize() == 0)
                    return;
                Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
                int uid = ((ModelSearchUser) adapter.getItem((int) id)).getUid();
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_search.length() == 0) {
                    Toast.makeText(v.getContext(), "请输入您要搜索的用户名", Toast.LENGTH_SHORT).show();
                    return ;
                }

                searchPeople(et_search.getText().toString().trim());
            }
        });

        // 键盘搜索点击事件
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {

                }
                return false;
            }
        });

    }


    //按关键词搜索用户
    private void searchPeople(String key) {
        if(adapter == null) {
            adapter = new AdapterFindPeopleByKey(this, list, getActivity().getIntent().getIntExtra("uid",
                    Thinksns.getMy().getUid()), key);
            listView.setAdapter(adapter);
        }
        adapter.clearList();
        adapter.loadInitData();
    }

    @Override
    public void initData() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_findpeople_bykey;
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullToRefreshListView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }
}
