package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterChannelList;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.channel.ActivityChannelWeibo;
import com.thinksns.sociax.t4.android.function.FunctionChangeSociaxItemStatus;
import com.thinksns.sociax.t4.android.interfaces.ChannelViewInterface;

import com.thinksns.sociax.t4.model.ModelChannel;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

/**
 * 类说明： 所有频道列表fragment/探索频道
 */
public class FragmentChannelList extends BaseListFragment<ModelChannel> implements ChannelViewInterface {

    protected boolean autoRefresh = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onUpdateList(ModelChannel channel) {
        mPresenter.loadNetData();
    }

    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));
    }

    @Override
    protected boolean loadingInPageCenter() {
        return true;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new BaseListPresenter<ModelChannel>(getActivity(), this) {
            @Override
            public ListData<ModelChannel> parseList(String result) {
                try {
                    JSONArray response = new JSONArray(result);
                    int length = response.length();
                    ListData<ModelChannel> list = new ListData<ModelChannel>();
                    for (int i = 0; i < length; i++) {
                        ModelChannel c;
                        try {
                            c = new ModelChannel(response.getJSONObject(i));
                            list.add(c);
                        } catch (DataInvalidException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    return list;
                }catch(JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected ListData<ModelChannel> readList(Serializable seri) {
                return (ListData<ModelChannel>)seri;
            }

            @Override
            public String getCachePrefix() {
                return "channel_list";
            }

            @Override
            public void loadNetData() {
                new Api.ChannelApi().getAllChannel(getPageSize(), getMaxId(), mHandler);
            }
        };
        mPresenter.setCacheKey("all_channel");
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ModelChannel channel = mAdapter.getItem((int)id);
        if(channel == null)
            return;

        Intent intent = new Intent(getActivity(), ActivityChannelWeibo.class);
        intent.putExtra("channel_id", channel.getId());
        intent.putExtra("channel_name", channel.getcName());
        startActivity(intent);
    }

    @Override
    protected ListBaseAdapter<ModelChannel> getListAdapter() {
        return new AdapterChannelList(getActivity(), this);
    }


    @Override
    public void onLoadDataSuccess(ListData<ModelChannel> data) {
        mEmptyLayout.setErrorImag(R.drawable.ic_no_nr);
        mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));
        super.onLoadDataSuccess(data);
    }

    @Override
    public void postAddFollow(final View view, final ModelChannel channel) {
        view.setEnabled(false);
        FunctionChangeSociaxItemStatus fc = new FunctionChangeSociaxItemStatus(
                getActivity());
        fc.setListenerSociax(new ListenerSociax() {

            @Override
            public void onTaskSuccess() {

                channel.setIs_follow(channel.getIs_follow() == 1 ? 0 : 1);

                mAdapter.notifyDataSetChanged();
                view.setEnabled(true);
                autoRefresh = false;    //不需要请求服务器刷新
                EventBus.getDefault().post(channel);
            }

            @Override
            public void onTaskError() {
                view.setEnabled(true);
            }

            @Override
            public void onTaskCancle() {
                view.setEnabled(true);
            }
        });

        fc.changeChannelFollow(channel.getId(),
                channel.getIs_follow());
    }

    @Override
    public void responseAddFollow() {

    }
}
