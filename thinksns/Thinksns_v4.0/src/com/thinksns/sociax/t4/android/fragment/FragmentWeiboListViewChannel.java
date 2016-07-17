package com.thinksns.sociax.t4.android.fragment;

import android.app.Activity;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterWeiboAll;
import com.thinksns.sociax.t4.android.channel.ActivityChannelWeibo;
import com.thinksns.sociax.t4.android.presenter.WeiboChannelListPresenter;
import com.thinksns.sociax.t4.eventbus.WeiboEvent;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hedong on 16/2/20.
 * 频道微博
 */
public class FragmentWeiboListViewChannel extends FragmentWeiboListViewNew {
    //手指最小滑动距离
    private static final int DEFAULT_SLIP_DISTANCE = 50;

    //微博类型 1：推荐频道微博 2：频道详情微博
    private int type = 1;
    private int channel_id;     //频道id

    private ActivityChannelWeibo channelWeibo;      //来自频道模块
    public int getType() {
        return type;
    }

    public int getChannelId() {
        return channel_id;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ActivityChannelWeibo) {
            channelWeibo = (ActivityChannelWeibo)activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            type = getArguments().getInt("type", 1);
            channel_id = getArguments().getInt("channel_id", -1);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    protected IntentFilter getIntentFilter() {
        return null;
    }

    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setDividerHeight(UnitSociax.dip2px(getActivity(), 0.5f));
        mListView.setSelector(R.drawable.list_selector);
    }

    /**
     * 加载进度在页面中部显示
     * @return
     */
    @Override
    protected boolean loadingInPageCenter() {
        if(type == 1) {
            return false;
        }else {
            return true;
        }
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        if(type == 1) {
            return false;
        }else {
            //从频道模块进入的自动获取网络内容
            isInHome = false;
            return true;
        }
    }

    @Override
    protected String getCacheKey() {
        if(type == 1) {
            return "channel_weibo_recommend";
        }else {
            return "channel_weibo_detail_" + channel_id;
        }
    }

    @Override
    protected void initPresenter() {
        mPresenter = new WeiboChannelListPresenter(getActivity(), this, this);
        mPresenter.setCacheKey(getCacheKey());
    }

    @Override
    protected void initListener() {
        super.initListener();
        if(type == 2) {
            mListView.setOnTouchListener(new View.OnTouchListener() {
                //手势上下滑动距离
                float distance, lastY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float currentY = event.getY();
                            if(lastY != 0) {
                                distance += (currentY - lastY);
                            }
                            //记录当前坐标
                            lastY = currentY;
                            break;
                        case MotionEvent.ACTION_UP:
                            if(distance > DEFAULT_SLIP_DISTANCE) {
                                //上滑
                                if(channelWeibo != null)
                                    channelWeibo.toggleCreateBtn(true);
                            }else if(distance < -DEFAULT_SLIP_DISTANCE) {
                                //下滑
                                if(channelWeibo != null)
                                    channelWeibo.toggleCreateBtn(false);
                            }

                            lastY = 0;
                            distance = 0;
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onTabClickListener() {
        if(mAdapter != null && mAdapter.getData().size() == 0){
            mPresenter.loadInitData(true);
        }
    }

    @Override
    protected ListBaseAdapter<ModelWeibo> getListAdapter() {
        return new AdapterWeiboAll(getActivity(), this, mListView);
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelWeibo> data) {
        super.onLoadDataSuccess(data);
        if(channelWeibo != null) {
            channelWeibo.toggleCreateBtn(true);
        }
    }

    @Override
    public void onCommentWeibo(ModelWeibo weibo, ModelComment comment) {
        super.onCommentWeibo(weibo, comment);
        if(channelWeibo != null) {
            channelWeibo.toggleCreateBtn(false);
        }
    }

    @Override
    protected void resetComentUI() {
        super.resetComentUI();
        if(channelWeibo != null) {
            channelWeibo.toggleCreateBtn(true);
        }
    }
}
