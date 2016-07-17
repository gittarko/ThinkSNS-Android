package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterShopGift;
import com.thinksns.sociax.t4.android.gift.ActivityGiftDetail;
import com.thinksns.sociax.t4.model.ModelShopGift;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 所有礼物
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年9月21日
 */
public class FragmentShopGift extends FragmentSociax {

    private PullToRefreshGridView pullToRefreshGridView;
    private GridView gridView;

    protected static final String ARGS_TYPE = "type";
    public static final String TYPE_ALL = "";
    public static final String TYPE_ENTITY = "2";
    public static final String TYPE_VIRTUAL = "1";

    private String type;

    public static FragmentShopGift newInstance(String type) {
        FragmentShopGift fragment = new FragmentShopGift();

        Bundle args = new Bundle();
        args.putString(ARGS_TYPE, type);
        fragment.setArguments(args);

        return fragment;
    }

    public void setType(String type) {
        adapter = new AdapterShopGift(this, list, type);
        gridView.setAdapter(adapter);
        adapter.doUpdataList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARGS_TYPE, null);
        } else {
            throw new UnsupportedOperationException("Couldn't use new Fragment() to get this case," +
                    " try to use Fragment.newInstance().");
        }
    }

    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
        pullToRefreshGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityGiftDetail.class);
                intent.putExtra("modelGift", (ModelShopGift) view.getTag(R.id.gift));
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        adapter.loadInitData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.t4_fragment_all_gift;
    }

    @Override
    public void initView() {
        pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_list);
        pullToRefreshGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        gridView = pullToRefreshGridView.getRefreshableView();
        list = new ListData<SociaxItem>();
        adapter = new AdapterShopGift(this, list, type);
        gridView.setAdapter(adapter);
    }
}
