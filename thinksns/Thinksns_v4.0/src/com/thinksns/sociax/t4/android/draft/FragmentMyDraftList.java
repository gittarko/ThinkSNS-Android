package com.thinksns.sociax.t4.android.draft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.adapter.AdapterDraftWeiboList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.db.SQLHelperWeiboDraft;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateBase;
import com.thinksns.sociax.t4.android.weibo.ActivityCreatePost;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityEditPostDraft;
import com.thinksns.sociax.t4.android.weibo.ActivityEditTransportDraft;
import com.thinksns.sociax.t4.android.weibo.ActivityEditWeiboDraft;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hedong on 16/3/1.
 * 草稿箱列表内容
 */
public class FragmentMyDraftList extends BaseListFragment<ModelDraft> {

    protected BroadcastReceiver updateWeibo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected ListBaseAdapter<ModelDraft> getListAdapter() {
        return new AdapterDraftWeiboList(getActivity());
    }

    @Override
    public void initReceiver() {
        updateWeibo = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(StaticInApp.NOTIFY_DRAFT)) {
                    setRefreshing(true);
                }
            }
        };

        IntentFilter filter_update_weibo = new IntentFilter();
        filter_update_weibo.addAction(StaticInApp.NOTIFY_DRAFT);
        getActivity().registerReceiver(updateWeibo, filter_update_weibo);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (updateWeibo != null) {
            getActivity().unregisterReceiver(updateWeibo);
        }
    }

    @Override
    protected void initPresenter() {
        mPresenter = new MyDraftPresenter(getActivity(), this);
        mPresenter.setCacheKey("my_draft");
    }

    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setSelector(getResources().getDrawable(R.drawable.list_selector));
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelDraft> data) {
        //设置缺省图
        mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));
        super.onLoadDataSuccess(data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
        ModelDraft draft = mAdapter.getItem((int)id);
        if(draft == null)
            return;
        Intent intent = null;
        if(draft.getType() != AppConstant.CREATE_TRANSPORT_WEIBO
                && draft.getType() != AppConstant.CREATE_TRANSPORT_POST) {
            //如果是频道内微博，编辑前改变类型
            if(draft.getType() == AppConstant.CREATE_CHANNEL_WEIBO) {
                if(draft.isHasImage()) {
                    draft.setType(AppConstant.CREATE_ALBUM_WEIBO);
                }else if(draft.isHasVideo()) {
                    draft.setType(AppConstant.CREATE_VIDEO_WEIBO);
                }else {
                    draft.setType(AppConstant.CREATE_TEXT_WEIBO);
                }

                intent = new Intent(getActivity(), ActivityEditWeiboDraft.class);
            }else if(draft.getType() == AppConstant.CREATE_WEIBA_POST) {
                intent = new Intent(getActivity(), ActivityEditPostDraft.class);
            }else {
                intent = new Intent(getActivity(), ActivityEditWeiboDraft.class);
            }
        }else {
            intent = new Intent(getActivity(), ActivityEditTransportDraft.class);
        }

        intent.putExtra("draft", mAdapter.getItem((int) id));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, final long id) {
        final ModelDraft draft = mAdapter.getItem((int) id);
        if (draft != null) {
            final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(getActivity());
            builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int arg0, long arg1) {
                    if (arg0 == 0) {
                        deleteDraftItem(draft.getId(), (int) id);
                    } else if (arg0 == 1) {
                        SQLHelperWeiboDraft sql = ((Thinksns) getActivity().getApplicationContext()).getWeiboDraftSQL();
                        sql.clearWeiboDraft();
                        sql.close();
                        mAdapter.clear();
                    } else {
                        builder.dimss();
                    }
                }
            });
            List<String> items = new ArrayList<String>();
            items.add("删除草稿");
            items.add("清空草稿箱");
            items.add("取消");
            builder.create(items);
        }
        return true;
    }

    @Subscribe
    public void removeDraft(ModelDraft draft) {
        int position = 0;
        for(SociaxItem draft1 : mAdapter.getData()) {
            if(((ModelDraft)draft).getId() == draft.getId())
                break;
            position++;
        }

        deleteDraftItem(draft.getId(), position);
    }

    //删除草稿
    public void deleteDraftItem(int weiboDraftID, int position) {
        if(position >= mAdapter.getDataSize())
            return;

        SQLHelperWeiboDraft sql = Thinksns.getWeiboDraftSQL();
        sql.delWeiboDraft(weiboDraftID);
        sql.close();
        mAdapter.getData().remove(position);
        mAdapter.notifyDataSetChanged();
    }

    private class MyDraftPresenter extends BaseListPresenter<ModelDraft> {
        private ListData<ModelDraft> draftListData;

        public MyDraftPresenter(Context context, IBaseListView baseListView) {
            super(context, baseListView);
            isReadCache = false;
        }


        @Override
        public ListData<ModelDraft> parseList(String result) {
            return draftListData;
        }

        @Override
        protected ListData<ModelDraft> readList(Serializable seri) {
            return (ListData<ModelDraft>) seri;
        }

        @Override
        public String getCachePrefix() {
            return "draft_list";
        }

        @Override
        public void loadNetData() {
            draftListData = Thinksns.getWeiboDraftSQL().getAllWeiboDraft(getPageSize(), getMaxId());
            //这里无需请求网络数据，直接进行本地数据解析
            executeParserTask(null);
        }
    }
}
