package com.thinksns.sociax.t4.adapter;

import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.db.WeibaSqlHelper;
import com.thinksns.sociax.t4.android.data.AppendPost;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.view.IWeibaDetailView;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelWeiba;
import com.thinksns.sociax.t4.unit.DynamicInflateForWeiba;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 类说明： 帖子列表类，
 *
 * @author wz
 * @version 1.0
 * @date 2014-12-22
 */
public class AdapterPostList extends AdapterSociaxList {

    private JSONObject weibaDetil;     // 请求微吧详情返回的内容
    protected int weiba_id;
    protected AppendPost append;         // 头部内容拼接
    private IWeibaDetailView callBack;

    public AdapterPostList(FragmentSociax fragment, ListData<SociaxItem> list,
                           JSONObject weibaDetil) {
        super(fragment, list);
        this.append = new AppendPost(context, this);
        this.weibaDetil = weibaDetil;
    }

    /**
     * @param fragment
     * @param list
     */
    public AdapterPostList(FragmentSociax fragment, ListData<SociaxItem> list,
                           int weiba_id, JSONObject weibaDetil) {
        super(fragment, list);
        if(fragment instanceof IWeibaDetailView)
            callBack = (IWeibaDetailView)fragment;
        this.weiba_id = weiba_id;
        this.append = new AppendPost(context, this);
        this.weibaDetil = weibaDetil;
        if(weibaDetil != null) {
            this.list = parseWeibaPost();
        }

        isHideFootToast = true;
    }

    public void setListView(ListView listView) {
        this.mListView = listView;
    }

    @Override
    public int getCount() {
        if(list.size() == 0 && (adapterState == STATE_IDLE
            || adapterState == AdapterSociaxList.STATE_LOADING)) {
            //默认状态是加载中
            return 1;
        }else if(adapterState == AdapterSociaxList.NO_MORE_DATA
                && list.size() == 0) {
            return 1;
        }else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.size() == 0 && (adapterState == STATE_IDLE
            || adapterState == AdapterSociaxList.STATE_LOADING)) {
            return 0;
        }else if(adapterState == AdapterSociaxList.NO_MORE_DATA
                && list.size() == 0) {
            return 1;
        }else {
            return 2;
        }
    }

    @Override
    public ModelPost getItem(int position) {
        return (ModelPost) super.getItem(position);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        HolderSociax holder = null;
        int type = getItemViewType(position);
        if(type == 0
                || type == 1) {
            if(convertView == null) {
                convertView = new EmptyLayout(parent.getContext());
                ListView listView = (ListView)parent;
                int width = listView.getWidth();
                int height = listView.getHeight() - 100;
                int count = listView.getHeaderViewsCount();
                int headerH = 0;
                for(int i= 0; i < count; i++) {
                    headerH += listView.getChildAt(i).getBottom();
                }

                height -= headerH;
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
                convertView.setLayoutParams(params);
                ((EmptyLayout)convertView).setNoDataContent(context.getResources().getString(R.string.empty_post));
            }

            if(type == 0) {
                ((EmptyLayout)convertView).setErrorType(EmptyLayout.NETWORK_LOADING);
            }else {
                ((EmptyLayout)convertView).setErrorType(EmptyLayout.NODATA);
            }

        }else {
            if (convertView == null ||
                    convertView.getTag(R.id.tag_viewholder) == null) {
                convertView = inflater.inflate(R.layout.listitem_post, null);
                holder = append.initHolder(convertView);
                convertView.setTag(R.id.tag_viewholder, holder);
            } else {
                holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
            }

            convertView.setTag(R.id.tag_post, getItem(position));
            append.appendPostListData(holder, getItem(position));
            //点击帖子内容进入详情
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView)parent).performItemClick(v, position, position);
                }
            });

            //点击帖子内容进入详情
            holder.tv_post_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView)parent).performItemClick(v, position, position);
                }
            });

        }

        return convertView;
    }

    @Override
    public int getMaxid() {
        return getLast() == null ? 0 : getLast().getPost_id();
    }

    @Override
    public ModelPost getLast() {
        return (ModelPost) super.getLast();
    }

    @Override
    public ListData<SociaxItem> refreshHeader(SociaxItem obj) throws
            ListAreEmptyException, ApiException, VerifyErrorException, DataInvalidException {
        return refreshNew(PAGE_COUNT);
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj) throws
            ListAreEmptyException, ApiException, VerifyErrorException, DataInvalidException{
        getApi().getWeibaPostList(weiba_id, PAGE_COUNT, getMaxid(), mListener);
        return null;
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        getApi().getWeibaPostList(weiba_id, PAGE_COUNT, 0, mListener);
        return null;
    }

    protected Api.WeibaApi getApi() {
        return thread.getApp().getWeibaApi();
    }

    protected ApiHttpClient.HttpResponseListener mListener = new ApiHttpClient.HttpResponseListener() {

        @Override
        public void onSuccess(Object result) {
            weibaDetil = (JSONObject) result;
            try {
                ModelWeiba weiba = new ModelWeiba(weibaDetil.getJSONObject("weiba_info"));
                weiba.setWeibaJson(result.toString());
                //保存微吧详情至数据库
                WeibaSqlHelper.getInstance(context).addWeiba(weiba);
            }catch(JSONException e) {
                e.printStackTrace();
            }

            ListData<SociaxItem> postList = parseWeibaPost();
            if(postList != null) {
                httpListener.onSuccess(postList);
            }else {
                httpListener.onError("数据解析错误");
            }

            if(callBack != null) {
                callBack.setWeibaHeaderContent(weibaDetil);
            }
        }

        @Override
        public void onError(Object result) {
            httpListener.onError(result);
            Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 解析微吧帖子列表
     * @return
     */
    private ListData<SociaxItem> parseWeibaPost() {
        ListData<SociaxItem> returnlist = new ListData<SociaxItem>();
        if (weibaDetil.has("weiba_post")) {
            try {
                JSONArray postlist = weibaDetil.getJSONArray("weiba_post");
                for (int i = 0; i < postlist.length(); i++) {
                    ModelPost md = new ModelPost(postlist.getJSONObject(i));
                    returnlist.add(md);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return returnlist;
    }
}
