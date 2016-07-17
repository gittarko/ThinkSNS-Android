package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.fragment.FragmentWeiboListViewChannel;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hedong on 16/2/20.
 * 频道presenter
 */
public class WeiboChannelListPresenter extends WeiboListListPresenter{
    FragmentWeiboListViewChannel fragmentChannel;

    public WeiboChannelListPresenter(Context context, IBaseListView<ModelWeibo> baseListView, WeiboListViewClickListener listViewClickListener) {
        super(context, baseListView, listViewClickListener);
        fragmentChannel = (FragmentWeiboListViewChannel)baseListView;
    }

    @Override
    public void loadNetData() {
        if(fragmentChannel.getType() == 1) {
            //获取推荐频道
            new Api.WeiboApi().channelsTimeline(getPageSize(), getMaxId(), mHandler);
        }else {
            //获取微博详情
            if(fragmentChannel.getChannelId() != -1) {
                new Api.ChannelApi().getChannelWeibo(String.valueOf(fragmentChannel.getChannelId()),
                        getMaxId(), getPageSize(), 0, mHandler);
            }
        }
    }


    @Override
    public ListData<ModelWeibo> parseList(String result) {
        if(fragmentChannel.getType() == 1) {
            return super.parseList(result);
        }else {
            try {
                //获取到频道详情信息
                JSONObject json = new JSONObject(result);
                JSONArray data = json.optJSONArray("feed_list");
                ListData<ModelWeibo> returnlist = new ListData<ModelWeibo>();
                if(data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        ModelWeibo weibo = new ModelWeibo(data.getJSONObject(i));
                        returnlist.add(weibo);
                    }
                }
                return returnlist;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
