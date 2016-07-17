package com.thinksns.sociax.t4.android.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.thinksns.sociax.t4.adapter.AdapterInformation;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.model.ModelInformationCateList;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 类说明：资讯类
 * Created by Zoey on 2016-04-27.
 */
public class FragmentInformation extends BaseListFragment<ModelInformationCateList> {

    private int cid = 0;
    private String message = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            cid = bundle.getInt("cid");
            Log.v("Information", "onCreate-->" + cid + "");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadNetData();
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        return false;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new InformationPresenter(getActivity(), this);
        mPresenter.setCacheKey("information" + cid);
    }

    @Override
    protected ListBaseAdapter<ModelInformationCateList> getListAdapter() {
        return new AdapterInformation(getActivity());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), NetActivity.class);
        intent.putExtra("url", mAdapter.getItem((int) id).getUrl());
        intent.putExtra("flag", StaticInApp.FROM_INFORMATION);
        getActivity().startActivity(intent);
    }

    private class InformationPresenter extends BaseListPresenter<ModelInformationCateList> {
        private ListData<ModelInformationCateList> informationListData;

        public InformationPresenter(Context context, IBaseListView baseListView) {
            super(context, baseListView);
            isReadCache = false;
        }

        @Override
        public ListData<ModelInformationCateList> parseList(String result) {
            ListData<ModelInformationCateList> list = new ListData<ModelInformationCateList>();
            try {
                JSONObject object = new JSONObject(result);
                message = object.getString("msg");
                int status = object.getInt("status");
                if (status == 1) {
                    JSONArray array = object.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        ModelInformationCateList cateList = new ModelInformationCateList(array.getJSONObject(i));
                        list.add(cateList);
                    }
                } else {
                    Log.v("Information", "parse list error");
                    return null;
//                    Toast.makeText(getActivity().getApplicationContext(), messsage, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (DataInvalidException e) {
                e.printStackTrace();
            }
            Log.v("Information", "parse list:" + list.size() + "");
            return list;
        }

        @Override
        protected ListData<ModelInformationCateList> readList(Serializable seri) {
            ListData<ModelInformationCateList> list = (ListData<ModelInformationCateList>) seri;
            return list;
        }

        @Override
        public String getCachePrefix() {
            return "information_list";
        }

        @Override
        public void loadNetData() {
            if (cid != 0) {
                Thinksns.getApplication().getInformationApi().getCateList(cid, getMaxId(), mHandler);
            }
            Log.v("Information", "loadNetData-->" + cid);
        }
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelInformationCateList> data) {
        if(data == null) {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            mEmptyLayout.showTvNoData(message);
        }else
            super.onLoadDataSuccess(data);
    }

    //页面中间显示加载进度
    @Override
    protected boolean loadingInPageCenter() {
        return true;
    }
}
