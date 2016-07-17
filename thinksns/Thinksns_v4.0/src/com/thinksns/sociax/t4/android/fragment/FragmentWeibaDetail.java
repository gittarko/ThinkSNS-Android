package com.thinksns.sociax.t4.android.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.db.WeibaSqlHelper;
import com.thinksns.sociax.t4.adapter.AdapterPostList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.function.FunctionChangeWeibaFollow;
import com.thinksns.sociax.t4.android.popupwindow.PopUpWindowAlertDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowCommon;
import com.thinksns.sociax.t4.android.view.IWeibaDetailView;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weiba.ActivityPostList;
import com.thinksns.sociax.t4.android.weiba.ActivityWeibaCommon;
import com.thinksns.sociax.t4.android.weiba.WeibaDetailPresenter;
import com.thinksns.sociax.t4.component.GlideRoundTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelWeiba;
import com.thinksns.sociax.t4.unit.ButtonUtils;
import com.thinksns.sociax.t4.unit.DynamicInflateForWeiba;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.tschat.widget.SmallDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 类说明：  微吧详情，需要传入int weiba_id,String weiba_name

 */
public class FragmentWeibaDetail extends FragmentSociax implements OnRefreshListener2<ListView>
        , IWeibaDetailView{
    private ModelWeiba weiba;
    private View header;//微吧头部
    private HolderSociax holder;

    private ViewStub stub_weiba_follow, stub_weiba_new,
            followStub, unfollowStub;
    private View weiba_follow_view, weiba_unfollow_view;
    private TextView tv_add_follow,tv_play;
    private PullToRefreshListView pullRefresh;

    private WeibaDetailPresenter presenter;
    private SmallDialog smallDialog;
    //手指点下坐标
    private float lastY;
    private BroadcastReceiver mReceiver;
    //手指最小滑动距离
    private static final int DEFAULT_SLIP_DISTANCE = 50;

    public static FragmentWeibaDetail newInstance(ModelWeiba weiba) {
        FragmentWeibaDetail fragment = new FragmentWeibaDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable("weiba", weiba);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(getArguments() != null) {
            weiba = (ModelWeiba)getArguments().getSerializable("weiba");
        }

        if(weiba == null) {
            getActivity().finish();
            return;
        }

        presenter = new WeibaDetailPresenter(weiba, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化广播接受
     */
    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String action = arg1.getAction();
                if (action.equals(StaticInApp.UPDATA_WEIBA)) {
                    if (adapter != null) {
                        adapter.doRefreshHeader();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticInApp.UPDATA_WEIBA);
        getActivity().registerReceiver(mReceiver, filter);

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Subscribe
    public void refreshPostList(ModelPost post) {
        if(adapter != null) {
            adapter.doUpdataList();
        }
    }

    @Override
    public void initView() {
        smallDialog = new SmallDialog(getActivity(), "请稍后");
        smallDialog.setCanceledOnTouchOutside(false);

        //设置列表属性
        pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        listView = pullRefresh.getRefreshableView();
        pullRefresh.setMode(Mode.PULL_FROM_START);
        pullRefresh.setOnRefreshListener(this);

        stub_weiba_follow = (ViewStub) findViewById(R.id.stub_weiba_follow);
        stub_weiba_new = (ViewStub) findViewById(R.id.stub_weiba_new);

        list = new ListData<SociaxItem>();
        //设置列表样式
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_ios)));
        listView.setDividerHeight(UnitSociax.dip2px(getActivity(), 10));
        //设置列表点击效果
        listView.setSelector(getResources().getDrawable(R.drawable.list_selector));
        //设置listView滑动事件
        listView.setOnTouchListener(touchListener);
        //加载头部视图
        initHeader();
        listView.addHeaderView(header);
        createAdapter();
        listView.setAdapter(adapter);

        //初始化微吧详情内容
        setWeibaHeaderContent(null);
        initReceiver();
    }

    @Override
    public void initIntentData() {

    }

    /**
     * 监听列表滚动，控制底部栏的显示与隐藏
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        //手势上下滑动距离
        float distance;
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
                        toggleBottomBar(true);
                    }else if(distance < -DEFAULT_SLIP_DISTANCE) {
                        //下滑
                        toggleBottomBar(false);
                    }

                    lastY = 0;
                    distance = 0;
                    break;
            }
            return false;
        }
    };

    /**
     * 显示底部操作栏：关注或发布帖子按钮
     * @param isShow 是否显示
     * 可以自行添加显示与隐藏的动画效果
     */
    private void toggleBottomBar(boolean isShow) {
        //如果列表没有内容不做任何效果
        if(adapter.getFirst() == null)
            return;

        if(isShow) {
            if(weiba.isFollow()) {
                //显示发布按钮
                if(stub_weiba_new.getVisibility() == View.GONE) {
                    stub_weiba_new.setVisibility(View.VISIBLE);
                }
            }else {
                //显示关注按钮
                if(stub_weiba_follow.getVisibility() == View.GONE) {
                    stub_weiba_follow.setVisibility(View.VISIBLE);
                }
            }
        }else {
            if(weiba.isFollow()) {
                //隐藏发布按钮
                stub_weiba_new.setVisibility(View.GONE);
            }else {
                //隐藏关注按钮
                stub_weiba_follow.setVisibility(View.GONE);
            }
        }
    }

    //初始化头部视图
    private void initHeader() {
        header = inflater.inflate(R.layout.header_postlist, null);
        holder = new HolderSociax();
        followStub = (ViewStub) header.findViewById(R.id.follow_stub);
        unfollowStub = (ViewStub)header.findViewById(R.id.unfollow_stub);
        inflateFollow(weiba.isFollow());
    }

    //加载关注或未关注视图
    private void inflateFollow(boolean isFollow) {
        try {
            if (isFollow) {
                unfollowStub.setVisibility(View.GONE);
                followStub.inflate();
                weiba_follow_view = header.findViewById(R.id.weiba_follow_view);
                holder.tv_weiba_title1 = (TextView) header.findViewById(R.id.tv_weiba_title1);
                holder.img_weiba_icon1 = (ImageView)header.findViewById(R.id.iv_weiba_logo1);
                holder.img_weiba_bg = (ImageView)weiba_follow_view.findViewById(R.id.img_weiba_bg);
                holder.tv_weiba_isfollow = (TextView) weiba_follow_view.findViewById(R.id.tv_weiba_isfollow);
                holder.ll_weiba_top = (LinearLayout) weiba_follow_view.findViewById(R.id.ll_weiba_top);
                holder.ll_weiba_digest = (LinearLayout) weiba_follow_view.findViewById(R.id.ll_weiba_digest);
            } else {
                followStub.setVisibility(View.GONE);
                unfollowStub.inflate();
                weiba_unfollow_view = header.findViewById(R.id.weiba_unfollow_view);
                holder.tv_weiba_title2 = (TextView) header.findViewById(R.id.tv_weiba_title2);
                holder.tv_weiba_des = (TextView) header.findViewById(R.id.tv_weiba_des);
                holder.img_weiba_icon2 = (ImageView)header.findViewById(R.id.iv_weiba_logo2);
                holder.tv_weiba_intro = (TextView) weiba_unfollow_view.findViewById(R.id.tv_weiba_intro);
                holder.tv_hot_title = (TextView) weiba_unfollow_view.findViewById(R.id.tv_hot_title);
            }

            //初始化公共UI
            holder.tv_member_count = (TextView)header.findViewById(R.id.tv_member_count);
            holder.tv_post_count = (TextView)header.findViewById(R.id.tv_post_count);

        }catch(Exception e) {
            //动态视图已经被加载起来
            if(isFollow) {
                weiba_follow_view.setVisibility(View.VISIBLE);
            }else {
                weiba_unfollow_view.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public AdapterSociaxList createAdapter() {
        ModelWeiba localWeiba = WeibaSqlHelper.getInstance(getActivity()).getWeibaInfo(weiba.getWeiba_id());
        if(localWeiba != null) {
            adapter = new AdapterPostList(this, null, weiba.getWeiba_id(),
                    localWeiba.getJsonObject());
        }else {
            adapter = new AdapterPostList(this, null, weiba.getWeiba_id(),
                    null);
        }

        ((AdapterPostList)adapter).setListView(listView);
        return adapter;
    }

    @Override
    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(adapter.getAdapterState() == AdapterSociaxList.STATE_LOADING
                        || adapter.getAdapterState() == AdapterSociaxList.NO_MORE_DATA)
                    return;
                if(arg3 == -1)
                    return;

                ModelPost md = (ModelPost) adapter.getItem((int)arg3);
                if (md == null || md.getPost_id() == 0) {
                    return;
                }
                md.setWeiba(weiba);
                md.setFromWeiba(true);
                Bundle data = new Bundle();
                data.putSerializable("post", md);
                ActivityStack.startActivity(getActivity(), ActivityPostDetail.class, data);
            }
        });

    }

    @Override
    public void initData() {
        adapter.loadInitData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_weiba_detail;
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullRefresh;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (adapter != null) {
            adapter.doRefreshHeader();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (adapter != null) {
            adapter.doRefreshFooter();
        }
    }

    //加载未关注视图
    private void inflateUnFollowView() {
        try{
            stub_weiba_follow.inflate();
            tv_add_follow = (TextView)findViewById(R.id.tv_follow);
            tv_play = (TextView)findViewById(R.id.tv_play);
            tv_add_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ButtonUtils.isFastDoubleClick()) {
                        return;
                    }
                    smallDialog.show();
                    presenter.changeWeibaFollow(weiba.isFollow());
                }
            });
            //逛一逛
            tv_play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ActivityWeibaCommon.class);
                    intent.putExtra("name", "逛一逛");
                    intent.putExtra("weiba_id", weiba.getWeiba_id());
                    startActivity(intent);
                }
            });
        }catch(Exception e) {
            stub_weiba_follow.setVisibility(View.VISIBLE);
        }
    }

    //添加置顶帖子
    private void addStickyPost(JSONObject weibaDetil) {
        try {
            JSONArray topArray = weibaDetil.getJSONArray("weiba_top");
            int topsize = topArray.length();
            if (topsize > 0) {
                holder.ll_weiba_top.setVisibility(View.VISIBLE);
                holder.ll_weiba_top.removeAllViews();

                for (int i = 0; i < topsize; i++) {
                    View parent = LayoutInflater.from(getActivity()).inflate(R.layout.layout_zhiding, null);
                    TextView tv_post = (TextView) parent.findViewById(R.id.tv_post_title);
                    JSONObject postObject = topArray.getJSONObject(i);
                    tv_post.setText(postObject.getString("title"));
                    final int post_id = postObject.getInt("post_id");
                    tv_post.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(
                                    v.getContext(),
                                    ActivityPostDetail.class);
                            Bundle data = new Bundle();
                            data.putInt("post_id", post_id);
                            intent.putExtras(data);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

                    holder.ll_weiba_top.addView(parent);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWeibaHeaderContent(final JSONObject detail) {
        if(getActivity() == null || getActivity().isFinishing())
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (detail != null) {
                        weiba = new ModelWeiba(detail.getJSONObject("weiba_info"));
                        boolean isFollow = detail.getInt("follow") == 1 ? true : false;
                        weiba.setFollow(isFollow);
                        //重新加载关注或未关注视图
                        inflateFollow(isFollow);
                        if (isFollow) {
                            // 置顶帖
                            if (detail.has("weiba_top")) {
                                addStickyPost(detail);
                            }

                            //精华帖
                            int diggest = (Integer) detail.getJSONArray("weiba_digest").get(0);
                            if (diggest > 0) {
                                holder.ll_weiba_digest.setVisibility(View.VISIBLE);
                                ((TextView) holder.ll_weiba_digest.findViewById(R.id.tv_diggest_num)).setText("精华帖 (" + diggest + ")");
                                holder.ll_weiba_digest.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(v.getContext(),
                                                ActivityPostList.class);
                                        intent.putExtra("weiba_id", weiba.getWeiba_id());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("type", StaticInApp.POST_DIGEST);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }

                    holder.tv_post_count.setText("帖子  " + weiba.getThread_count());

                    if (weiba.isFollow()) {
                        holder.tv_weiba_title1.setText(weiba.getWeiba_name());
                        //显示微吧LOGO
                        Glide.with(getActivity())
                                .load(weiba.getAvatar_big())
                                .transform(new GlideRoundTransform(getActivity()))
                                .crossFade()
                                .placeholder(R.drawable.default_image_small)
                                .into(holder.img_weiba_icon1);
                        holder.tv_member_count.setText("成员  " + weiba.getFollower_count());
                        holder.tv_weiba_isfollow.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(final View v) {
                                PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(v.getContext());
                                builder.setMessage("确认取消关注吗?", 18);
                                builder.setTitle(null, 0);
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        smallDialog.show();
                                        presenter.changeWeibaFollow(true);
                                    }
                                });

                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.create();
                            }
                        });

                    } else {
                        //未关注时的微吧信息
                        holder.tv_weiba_title2.setText(weiba.getWeiba_name());
                        //显示微吧LOGO
                        Glide.with(getActivity())
                                .load(weiba.getAvatar_middle())
                                .transform(new GlideRoundTransform(getActivity()))
                                .crossFade()
                                .placeholder(R.drawable.default_image_small)
                                .into(holder.img_weiba_icon2);
                        holder.tv_member_count.setText("关注 "
                                + weiba.getFollower_count());
                        holder.tv_weiba_intro.setText(weiba.getIntro());
                        if (Integer.parseInt(weiba.getThread_count()) == 0) {
                            holder.tv_hot_title.setVisibility(View.GONE);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (weiba.isFollow()) {
                    stub_weiba_follow.setVisibility(View.GONE);
                    DynamicInflateForWeiba.addNew(getActivity(), stub_weiba_new, weiba.getWeiba_id());
                    //已关注支持上拉加载更多
                    pullRefresh.setMode(Mode.BOTH);
                } else {
                    inflateUnFollowView();
                    stub_weiba_new.setVisibility(View.GONE);
                    //未关注不支持上拉加载更多
                    pullRefresh.setMode(Mode.PULL_FROM_START);
                }
            }
        });
    }

    @Override
    public void changeWeibaFollow(final int status, final String message) {
        if(status == 1) {
            if(weiba.isFollow()) {
                //取消关注成功
            }else {
                //关注成功
            }
            EventBus.getDefault().post(weiba);
            adapter.doUpdataList();
        }else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        smallDialog.dismiss();

    }

    @Override
    public void executeDataSuccess(ListData<SociaxItem> list) {
        super.executeDataSuccess(list);
        if(list.size() < AdapterSociaxList.PAGE_COUNT) {
            if(adapter.getRefreshState() == AdapterSociaxList.REFRESH_FOOTER) {
                Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
                pullRefresh.setMode(Mode.PULL_FROM_START);
            }
        }
    }
}
