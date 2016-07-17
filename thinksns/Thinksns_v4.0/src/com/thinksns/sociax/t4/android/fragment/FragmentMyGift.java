package com.thinksns.sociax.t4.android.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterMyGetGift;
import com.thinksns.sociax.t4.android.function.FunctionMyGiftDialog;
import com.thinksns.sociax.t4.model.ModelMyGifts;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 我的礼物
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年9月21日
 */
public class FragmentMyGift extends FragmentSociax {

    protected static final String ARGS_TYPE = "type";
    public static final String TYPE_GET = "0";
    public static final String TYPE_SEND = "1";

    private String type="";
    private PullToRefreshGridView pullToRefreshGridView;
    private GridView gridView;
    private FunctionMyGiftDialog dialog;

    public static FragmentMyGift newInstance(String type) {
        FragmentMyGift fragment = new FragmentMyGift();

        Bundle args = new Bundle();
        args.putString(ARGS_TYPE, type);
        fragment.setArguments(args);

        return fragment;
    }

    public void setType(String type) {
        adapter = new AdapterMyGetGift(this, list, type);
        gridView.setAdapter(adapter);
        adapter.doUpdataList();
        this.type=type;
    }

    @SuppressLint("NewApi") @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARGS_TYPE, null);
            Log.v("giftTest","FragmentMyGift/type/"+type);
        } else {
            throw new UnsupportedOperationException("Couldn't use new Fragment() to get this case," +
                    " try to use Fragment.newInstance().");
        }
    }

    @Override
    public void initView() {
        pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_list);
        pullToRefreshGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        loadingView = (LoadingView) findViewById(LoadingView.ID);
        gridView = pullToRefreshGridView.getRefreshableView();
        list = new ListData<SociaxItem>();
        adapter = new AdapterMyGetGift(this, list, type);
        gridView.setAdapter(adapter);
        dialog = new FunctionMyGiftDialog(getActivity());
    }

    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
        pullToRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.setGift((ModelMyGifts) adapter.getItem((int) id), "",type);
                Log.v("giftTest","initListener/type/"+type);
                dialog.getDialog().show();
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
}
