package com.thinksns.sociax.t4.android.weibo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewOutlineProvider;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.t4.adapter.AdapterCommentList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendWeibo;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.findpeople.ActivitySearchUser;
import com.thinksns.sociax.t4.android.function.FunctionChangeSociaxItemStatus;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowWeiboMore;
import com.thinksns.sociax.t4.android.presenter.WeiboDetailsPresenter;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.view.IWeiboDetailsView;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.eventbus.WeiboEvent;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelDiggUser;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelVideo;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.ButtonUtils;
import com.thinksns.sociax.t4.unit.DynamicInflateForWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.TimeIsOutFriendly;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.tschat.adapter.AdapterChatRoomList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 类说明：传入1. ModelWeibo weibo微博 2.传入int weibo_id，建议使用第二种
 * 传入weibo可以优先显示内存中的内容
 * @author wz
 * @version 1.0
 * @date 2014-9-23
 */
public class ActivityWeiboDetail extends ThinksnsAbscractActivity
        implements OnClickListener, PullToRefreshBase.OnRefreshListener2<ListView>,
        IWeiboDetailsView {
    private static final String TAG = "TSTAG_ActivityWeiboDetail";
    private AdapterCommentList adapter;
    private ListData<SociaxItem> list;
    private ModelWeibo weibo;

    private RelativeLayout rl_root, rl_title;
    private ImageView iv_back, iv_share;
    private ImageView img_user_header, tv_title_center;
    private View headerView;        // 评论list的headerview
    private TextView tv_weibo_uname, // 微博用户名
            tv_weibo_time, // 微博发布时间
            tv_weibo_from, // 微博来自
            tv_weibo_diggcount, // 微博赞数目
            tv_weibo_content,// 微博内容
            tv_all_comment;// 微博评论数
    // 下面这部分内容只在layoutid=avtivity_weibo_detail里面用到，暂时保留
    private FrameLayout rl_image;
    private ImageView iv_weibo_image;

    public LinearLayout ll_user_group;

    private ArrayList<ModelDiggUser> list_digguser;
    private ImageView img_digg, iv_arrow;// 赞状态图片
    private LinearLayout ll_digglist,// 赞列表
            ll_digg_info,ll_digg_users, ll_comment_info;           // 添加赞的按钮layout
    private LinearLayout ll_other_files_image;// 图片layout
    private LinearLayout ll_media;// 视频layout
    private GridView gv_weibo;

    private PullToRefreshListView pullRefresh;
    private ListView list_comment;

    protected EditText et_comment;// 评论输入框
    protected Button btn_send;// 评论发送按钮
    protected ImageView img_face;// 评论表情按钮
    protected ListFaceView list_face;// 表情框
    protected LinearLayout ll_transport;// 转发
    private LinearLayout ll_sociax;
    private ImageView iv_dig, iv_collect, iv_transport;
    private LinearLayout ll_adress;            //地理位置
    public LinearLayout ll_from_weibo_content;// 来自微博的时候内容
    public LinearLayout ll_from_weiba_content;// 来自微吧的时候内容
    public TextView tv_post_content;// 帖子内容
    public TextView tv_post_from;// 帖子来自的微吧
    public LinearLayout ll_post_no_delete, ll_comment;//
    public TextView tv_post_is_delete;  // 帖子已经删除

    protected int selectpostion;
    protected ListHandler mHandler;
    protected RelativeLayout rl_more;   // 用来隐藏more popwindow
    protected UnitSociax unit;
    private TextView tv_post_title;

    private int weibo_id;           // 传入的微博id
    private int position;           //记录微博列表的位置
    private AppendWeibo appendWeibo;
    private boolean changeData = false;    //用户是否对当前页面进行了操作，例如点在，评论,关注等
    private boolean autoComment = true;
    private int to_commentId = 0;   //回复的评论id
    private String to_name;         //回复的评论人姓名

    private PopupWindowWeiboMore popup;
    private WeiboDetailsPresenter detailsPresenter;

    //转发
    private ViewStub stub_transport_weibo;
    private ViewStub stub_transport_weiba;


    private ViewStub stub_uname_adn;
    private ViewStub stub_weiba;
    private ViewStub stub_image;
    private ViewStub stub_image_group;
    private ViewStub stub_media;
    private ViewStub stub_file;
    private ViewStub stub_address;
    private ViewStub stub_add_follow;
    private EmptyLayout error_layout;

    private static EventBus eventBus;

    public static void setEventBus(EventBus bus) {
        eventBus = bus;
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullRefresh;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        initIntentData();
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /************** test *************/
        if (requestCode == AppConstant.COMMENT) {
            if (resultCode == AppConstant.COMMENT_SUCCESS) {
                if (adapter != null) {
                    adapter.doUpdataList();
                    weibo.setCommentCount(weibo.getCommentCount() + 1);
                    setWeiboContent(weibo);
                }
            }
        }
    }

    /**
     * 载入数据
     */
    private void initData() {
        if (weibo != null) {
            setWeiboContent(weibo);
            //加载点赞数据
            if(weibo.getDiggUsers() != null && weibo.getDiggUsers().size() > 0)
                setDiggUsers(weibo.getDiggUsers());
            //加载评论数据
            setWeiboComments(weibo.getCommentList());
            if(weibo.getCommentCount() > 0) {
                //加载最新评论
                adapter.setState(AdapterSociaxList.STATE_LOADING);
                adapter.notifyDataSetChanged();
                adapter.loadInitData();
            }
        }else {
            error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        //请求微博详情
        detailsPresenter = new WeiboDetailsPresenter(this);
        detailsPresenter.loadWeiboDetails(weibo_id);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
//        detailsPresenter.loadWeiboDetails(weibo_id);
        if(adapter != null) {
            adapter.doUpdataList();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        if (adapter != null) {
            //加载更多评论
            adapter.doRefreshFooter();
        }
    }

    @Override
    public void setWeiboContent(final ModelWeibo weibo) {
        this.weibo = weibo;
        if (weibo == null) {
            error_layout.setErrorType(EmptyLayout.NODATA);
            return;
        }

        error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        tv_weibo_uname.setText(weibo.getUsername());
        // 显示图片的配置
        Glide.with(getApplicationContext()).load(weibo.getUserface())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_user)
                .transform(new GlideCircleTransform(this)).crossFade()
                .into(tv_title_center);

        tv_title_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ActivityWeiboDetail.this,
                        ActivityUserInfo_2.class);
                intent.putExtra("uid", weibo.getUid());
                ActivityWeiboDetail.this.startActivity(intent);
            }
        });

        if (weibo.getUid() != Thinksns.getMy().getUid()
                && weibo.getFollowing() == 0) {
            // 不是本人且未关注
            DynamicInflateForWeibo.addFollow(ActivityWeiboDetail.this, stub_add_follow,
                    new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            v.setClickable(false);
                            FunctionChangeSociaxItemStatus fc = new FunctionChangeSociaxItemStatus(ActivityWeiboDetail.this);
                            fc.setListenerSociax(new ListenerSociax() {

                                @Override
                                public void onTaskSuccess() {
                                    //更新UI
//                                    adapter.refreshNewSociaxList();
                                    v.setVisibility(View.GONE);
                                    weibo.setFollowing(1);
                                    //更新外部列表内容
                                    Intent intent = new Intent();
                                    intent.putExtra("uid", weibo.getUid());
                                    //设置更新类型为关注用户状态
                                    intent.putExtra("follow", 1);
                                    intent.setAction(StaticInApp.NOTIFY_FOLLOW_USER);
                                    sendBroadcast(intent);
                                }

                                @Override
                                public void onTaskError() {
                                    v.setClickable(true);
                                }

                                @Override
                                public void onTaskCancle() {
                                }
                            });

                            fc.changeUserInfoFollow(weibo.getUid(), false);
                        }
                    });
        } else {
            stub_add_follow.setVisibility(View.GONE);
        }

        try {
            tv_weibo_time.setText(TimeHelper.friendlyTime(weibo.getTimestamp()));
        } catch (TimeIsOutFriendly e) {
            tv_weibo_time.setText(weibo.getCtime());
        }

        // 来自哪里
        tv_weibo_from.setText(weibo.getFrom() == null ? "" : weibo.getFrom());
        tv_weibo_from.setTextColor(getResources().getColor(R.color.gray));

        // 微吧
        if (weibo.getType().equals("weiba_post")) {
            ll_from_weibo_content.setVisibility(View.GONE);
            DynamicInflateForWeibo.addWeiba(this, stub_weiba, weibo);
        } else {
            // 微博
            ll_from_weibo_content.setVisibility(View.VISIBLE);
            stub_weiba.setVisibility(View.GONE);
            // 内容
//            unit.showContentLinkViewAndLinkMovement(weibo.getContent(), tv_weibo_content);
//            tv_weibo_content.setTag(R.id.tag_weibo, weibo);
            // 内容
            if(!TextUtils.isEmpty(weibo.getContent())) {
                unit.showContentLinkViewAndLinkMovement(weibo.getContent(),
                        tv_weibo_content);
                tv_weibo_content.setVisibility(View.VISIBLE);
            }else {
                tv_weibo_content.setVisibility(View.GONE);
            }
            // 图片
            if (weibo.hasImage() && weibo.getAttachImage() != null) {
                    int gridWidth = UnitSociax.getWindowWidth(ActivityWeiboDetail.this) - UnitSociax.dip2px(ActivityWeiboDetail.this, 20);
                    DynamicInflateForWeibo.addImageGroup(this, stub_image_group, weibo.getAttachImage(), gridWidth);
                    stub_image.setVisibility(View.GONE);

            } else {
                stub_image.setVisibility(View.GONE);
                stub_image_group.setVisibility(View.GONE);
            }

            // 视频
            ModelVideo myVideo = weibo.getAttachVideo();
            if (weibo.hasVideo() && myVideo != null) {
                int gridWidth = UnitSociax.getWindowWidth(ActivityWeiboDetail.this) - UnitSociax.dip2px(ActivityWeiboDetail.this, 20);
                int maxImgWidth = gridWidth;
                int imgHeight = maxImgWidth / 16 * 9;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(maxImgWidth, imgHeight);
                stub_media.setLayoutParams(params);
                DynamicInflateForWeibo.addMedia(this, stub_media, myVideo, adapter, position);
            } else {
                stub_media.setVisibility(View.GONE);
            }
            // 文件
            if (weibo.hasFile()) {
                DynamicInflateForWeibo.addFile(this, stub_file, weibo.getAttachImage(), position);
            } else {
                stub_file.setVisibility(View.GONE);
            }

            // 转发原微博
            if (weibo.isNullForTranspond() || weibo.getIsRepost() > 0) {
                if (weibo.getType().equals("weiba_repost")) {
                    DynamicInflateForWeibo.addTransportWeiba(stub_transport_weiba, weibo);
                    stub_transport_weibo.setVisibility(View.GONE);
                } else {
                    DynamicInflateForWeibo.addTransportWeibo(stub_transport_weibo, weibo);
                    stub_transport_weiba.setVisibility(View.GONE);
                }
            } else {
                stub_transport_weibo.setVisibility(View.GONE);
                stub_transport_weiba.setVisibility(View.GONE);
            }

            hasInitHeaderContent = true;
        }

        //地址暂时屏蔽掉
        if (weibo.getAddress() != null && weibo.getLongitude() != null
                && weibo.getLatitude() != null) {
            DynamicInflateForWeibo.addAddress(this, stub_address, weibo);
        } else {
            ll_adress.setVisibility(View.GONE);
        }

        iv_collect.setImageResource(weibo.isFavorited() ? R.drawable.ic_share_detail_collect_blue : R.drawable.ic_share_detail_collect);

        if(list_comment.getHeaderViewsCount() == 1) {
            list_comment.addHeaderView(headerView);
            //必须要跟setAdapter
            list_comment.setAdapter(adapter);
        }

        if(!weibo.isCan_comment()) {
            et_comment.setEnabled(false);
            et_comment.setHint("您没有权限发表评论");
        }

        // 评论数量
        if(weibo.getCommentCount() > 0) {
            String comment = getResources().getString(R.string.tv_weibo_all_comment);
            comment = String.format(comment, weibo.getCommentCount() + "");
            tv_all_comment.setText(comment);
        }

    }

    @Override
    public void setDiggUsers(final ListData<ModelUser> users) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 是否已赞
                if (weibo.isDigg()) {
                    iv_dig.setImageResource(R.drawable.ic_share_detail_like_blue);
                } else {
                    iv_dig.setImageResource(R.drawable.ic_share_detail_like);
                }

                if(users.size() > 0) {
                    ll_digg_users.setVisibility(View.VISIBLE);
                    ll_digglist.post(new Runnable() {
                        @Override
                        public void run() {
                            addDigUsers(users);
                        }
                    });

                }else {
                    ll_digg_users.setVisibility(View.GONE);
                }

                // 点赞数量
                tv_weibo_diggcount.setText(Integer.toString(weibo.getDiggNum()) + "人喜欢了");

            }
        });
    }

    //添加点赞用户头像
    private void addDigUsers(ListData<ModelUser> users) {
        int width = ll_digglist.getWidth();
        //计算头像排列的个数:头像宽27dp,间隙4dp27x+(x-1)*4 = width;
        int headNum = width / 31;
        ll_digglist.removeAllViews();
        for (int i = 0; i < users.size() && i < headNum; i++) {
            ImageView img = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(users.get(i).getUserface())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideCircleTransform(getApplicationContext()))
                    .crossFade()
                    .into(img);

            //设置点赞用户头像
            int head_width = UnitSociax.dip2px(ActivityWeiboDetail.this, 27);
            LinearLayout.LayoutParams lp = new LayoutParams(head_width, head_width);
            lp.setMargins(0, 0, SociaxUIUtils.dip2px(getApplicationContext(), 4), 0);
            img.setLayoutParams(lp);
            ll_digglist.addView(img);
        }
    }

    //设置微博评论数和填充评论列表
    @Override
    public void setWeiboComments(ListData<SociaxItem> comments) {
        ll_comment_info.setVisibility(View.VISIBLE);
        // 评论数量
        tv_all_comment.setText("评论 " + weibo.getCommentCount());
        if(comments == null || comments.size() == 0) {
            //没有评论
            adapter.setState(AdapterSociaxList.NO_MORE_DATA);
            pullRefresh.setMode(Mode.PULL_FROM_START);
            adapter.notifyDataSetChanged();

        }else {
            for(int i=0; i<comments.size(); i++) {
                ModelComment comment = (ModelComment)comments.get(i);
                if(adapter.getData().contains(comment)) {
                    comments.remove(i);
                    i--;
                }
            }

            adapter.addData(comments);
        }

        //如果从评论页跳转来自动打开评论
        if(autoComment && getIntent().getSerializableExtra("comment") != null) {
            autoComment = false;
            ModelComment commentInfo = (ModelComment)getIntent().getSerializableExtra("comment");
            replyUser(commentInfo);
        }
    }

    @Override
    public void setErrorData(String error) {
        if(weibo == null) {
            //在没有详细数据情况下显示错误提示
            Toast.makeText(ActivityWeiboDetail.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void digWeiboUI(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(status == 1) {
                    //点赞成功
                }else {
                    //点赞失败
                    Toast.makeText(ActivityWeiboDetail.this,"操作失败", Toast.LENGTH_SHORT).show();
                }

                iv_dig.setEnabled(true);
            }
        });
    }

    @Override
    public void collectWeiboUI(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(status == -1
                        || status == 0) {
                    //网络或数据操作有误
                    String error = "";
                    if(weibo.isFavorited()) {
                        error = "收藏失败";
                    }else {
                        error = "取消收藏失败";
                    }
                    Toast.makeText(ActivityWeiboDetail.this, error, 500).show();
                    //切换回原先的状态
                    toggleCollectStatus();
                }else if(status == 1) {
                    //收藏成功
                    changeData = true;
                }

                iv_collect.setEnabled(true);
            }
        });
    }

    @Override
    public void toggleCollectStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(weibo.isFavorited()) {
                    iv_collect.setImageResource(R.drawable.ic_share_detail_collect);
                    weibo.setFavorited(false);
                }else {
                    iv_collect.setImageResource(R.drawable.ic_share_detail_collect_blue);
                    weibo.setFavorited(true);
                }
            }
        });
    }

    @Override
    public void commentWeiboUI(final int status, final String ctime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(status != 0) {
                    //更新评论列表
                    updateCommentList(status, ctime);
                    changeData = true;
                }else {
                    weibo.setCommentCount(weibo.getCommentCount() - 1);
                    tv_all_comment.setText("评论 " + weibo.getCommentCount());
                    Toast.makeText(ActivityWeiboDetail.this, "评论发表失败", Toast.LENGTH_SHORT).show();
                }

                btn_send.setEnabled(true);
                resetCommentUI();
            }

        });

    }

    //更新评论列表
    private void updateCommentList(int status, String ctime) {
        ListData<SociaxItem> list = ((AdapterCommentList)adapter).getData();
        for(int i=0; i<list.size(); i++) {
            ModelComment comment = (ModelComment) list.get(i);
            if(comment.getType() == ModelComment.Type.SENDING
                    && comment.getCtime().equals(ctime)) {
                comment.setComment_id(status);
                //根据评论的类型 微博评论和评论别人的评论
                if(comment.getReplyCommentId() == 0) {
                    comment.setCommentType("weibo");
                }else {
                    comment.setCommentType("comment");
                }

                weibo.getCommentList().set(i, comment);
                ((AdapterCommentList)adapter).setItem(i, comment);

                break;
            }

            continue;
        }
    }

    @Override
    public void addCommentWeibo(ModelComment comment) {
        ((AdapterCommentList)adapter).addItem(0, comment);
        weibo.getCommentList().add(0, comment);
        weibo.setCommentCount(weibo.getCommentCount() + 1);
        //设置微博评论数
        tv_all_comment.setText("评论 " + weibo.getCommentCount());
    }

    private TextWatcher commentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 0) {
                btn_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundbackground_blue_chat_item));
                btn_send.setClickable(true);
            } else {
                btn_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundbackground_gray_chat_item));
                btn_send.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    /**
     * 初始化监事件
     */
    private void initListener() {
        list_comment.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetCommentUI();
                return false;
            }

        });

        et_comment.addTextChangedListener(commentTextWatcher);

        et_comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    img_face.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.VISIBLE);
                    ll_sociax.setVisibility(View.GONE);
                } else {
                    img_face.setVisibility(View.GONE);
                }
            }
        });


        //点击表情
        img_face.setOnClickListener(this);

        //发送评论
        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                    if (et_comment.getText().toString().trim().equals("")) {
                        Toast.makeText(ActivityWeiboDetail.this, "您还没写评论哦",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        v.setEnabled(false);
                        detailsPresenter.postCommentWeibo(weibo, et_comment.getText().toString().trim(),
                                to_name, to_commentId);
                        resetCommentUI();
                    }
            }
        });

        //点赞
        iv_dig.setOnClickListener(this);
        //收藏微博
        iv_collect.setOnClickListener(this);
        //转发微博
        iv_transport.setOnClickListener(this);
        //返回
        iv_back.setOnClickListener(this);
        //弹出更多
        iv_share.setOnClickListener(this);

        list_comment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.size() > 0) {
                    ModelComment comment = (ModelComment)adapter.getItem((int) id);
                    getItemOptions(comment);
                }
            }
        });

    }

    private void resetCommentUI() {
        et_comment.clearFocus();
        //保留编辑框的输入内容
//        et_comment.setHint(getResources().getString(R.string.comment_hint_edit));
//        et_comment.setText("");
        et_comment.setTag(null);

        ll_sociax.setVisibility(View.VISIBLE);
        img_face.setVisibility(View.GONE);
        list_face.setVisibility(View.GONE);
        btn_send.setVisibility(View.GONE);

        SociaxUIUtils.hideSoftKeyboard(ActivityWeiboDetail.this, et_comment);
        to_commentId = 0;
        to_name = null;
    }

    //获取菜单选项
    private void getItemOptions(final ModelComment comment) {
        final boolean isMy = Integer.parseInt(comment.getUid()) == Thinksns.getMy().getUid();
        replyComment = comment;
        List<String> datas = new ArrayList<String>();
        if(isMy) {
            datas.add("删除");
        }else {
            datas.add("评论");
        }

        datas.add("复制");
        datas.add("取消");

        createOptionsMenu(datas);

    }

    PopupWindowListDialog.Builder builder = null;
    ModelComment replyComment = null;
    //创建菜单项
    private void createOptionsMenu(List<String> datas) {
        builder = new PopupWindowListDialog.Builder(ActivityWeiboDetail.this);
        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    if(Integer.parseInt(replyComment.getUid()) == Thinksns.getMy().getUid()) {
                        //删除评论
                        deleteComment(replyComment);
                    }else {
                        //回复别人的评论
                        replyUser(replyComment);
                    }
                }else if(position == 1) {
                    //复制评论
                    UnitSociax.copy(replyComment.getContent(), ActivityWeiboDetail.this);
                }

                builder.dimss();
            }
        });

        builder.create(datas);
    }


    //回复评论
    private void replyUser(ModelComment comment) {
        et_comment.requestFocus();
        et_comment.setVisibility(View.VISIBLE);
        to_commentId = comment.getComment_id();
        to_name = comment.getUname();

        et_comment.setHint("回复" + comment.getUname() + ":");
        et_comment.setSelection(et_comment.length());
        UnitSociax.showSoftKeyborad(ActivityWeiboDetail.this, et_comment);
    }

    /**
     * 修改收藏状态
     */
    protected void changeCollectionState() {
        iv_collect.setEnabled(false);
        detailsPresenter.postCollectWeibo(weibo);
    }

    /**
     * 初始化intent信息
     */
    private void initIntentData() {
        if (getIntent().hasExtra("weibo_id")) {
            this.weibo_id = getIntent().getIntExtra("weibo_id", 0);
        }
        if (getIntent().hasExtra("weibo")) {
            weibo = (ModelWeibo) getIntent().getSerializableExtra("weibo");
            if(weibo != null)
                this.weibo_id = weibo.getWeiboId();
        }
        if (weibo_id == 0) {
            Toast.makeText(this, "读取错误", Toast.LENGTH_SHORT).show();
            finish();
        }

        position = getIntent().getIntExtra("position", 0);
    }

    /**
     * 初始化页面
     */

    private void initView() {
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        rl_title = (RelativeLayout)findViewById(R.id.rl_title);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_share = (ImageView) findViewById(R.id.iv_share);

        tv_title_center = (ImageView) findViewById(R.id.tv_title_center);
        //评论缺省设置
        error_layout = (EmptyLayout)findViewById(R.id.error_layout);
        error_layout.setNoDataContent(getResources().getString(R.string.empty_user_comment));

        headerView = LayoutInflater.from(this).inflate(
                R.layout.header_activity_weibo_detail, null);
        //不显示用户头像
        ImageView imageView = (ImageView)headerView.findViewById(R.id.iv_weibo_user_head);
        imageView.setVisibility(View.GONE);
        ll_user_group = (LinearLayout) findViewById(R.id.ll_uname_adn);
        ll_other_files_image = (LinearLayout) findViewById(R.id.ll_image);
        gv_weibo = (GridView) findViewById(R.id.gv_weibo);

        //下拉刷新组件
        pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullRefresh.setOnRefreshListener(this);
        pullRefresh.setMode(Mode.BOTH);

        list_comment = pullRefresh.getRefreshableView();
        //设置列表项间隔线
        list_comment.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_ios)));
        list_comment.setDividerHeight(UnitSociax.dip2px(this, 0.5f));

        // 用户信息
        tv_weibo_from = (TextView) headerView.findViewById(R.id.tv_weibo_from);
        tv_weibo_time = (TextView) headerView.findViewById(R.id.tv_weibo_ctime);
        tv_weibo_uname = (TextView) headerView
                .findViewById(R.id.tv_weibo_user_name);
        ll_user_group = (LinearLayout) headerView
                .findViewById(R.id.ll_uname_adn);
        img_user_header = (ImageView) headerView
                .findViewById(R.id.iv_weibo_user_head);

        // 微博内容
        unit = new UnitSociax(this);
        tv_weibo_content = (TextView) headerView
                .findViewById(R.id.tv_weibo_content);
        rl_image = (FrameLayout) headerView.findViewById(R.id.rl_image);
        iv_weibo_image = (ImageView) headerView
                .findViewById(R.id.iv_weibo_image);
        gv_weibo = (GridView) headerView.findViewById(R.id.gv_weibo);
        ll_media = (LinearLayout) headerView.findViewById(R.id.ll_media);
        ll_other_files_image = (LinearLayout) headerView
                .findViewById(R.id.ll_image);
        //位置
        ll_adress = (LinearLayout)headerView.findViewById(R.id.ll_address);
        // 赞和评论列表
        ll_digg_users = (LinearLayout)headerView.findViewById(R.id.ll_digg_users);
        ll_digg_users.setOnClickListener(this);
        ll_digg_info = (LinearLayout) headerView.findViewById(R.id.ll_digg_info);
        img_digg = (ImageView) headerView.findViewById(R.id.iv_dig);
        ll_digglist = (LinearLayout) headerView.findViewById(R.id.ll_digglist);
        // 赞数目
        tv_weibo_diggcount = (TextView) headerView.findViewById(R.id.tv_weibo_diggcount);

        ll_comment_info = (LinearLayout)headerView.findViewById(R.id.ll_comment_info);

        // 评论数目
        tv_all_comment = (TextView) headerView
                .findViewById(R.id.tv_all_comment);
        ll_transport = (LinearLayout) headerView.findViewById(R.id.ll_transport);

        iv_arrow = (ImageView) headerView.findViewById(R.id.iv_arrow);
        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        // 更多s
        rl_more = (RelativeLayout) findViewById(R.id.rl_more);
        mHandler = new ListHandler();
        ll_sociax = (LinearLayout) findViewById(R.id.ll_sociax);
        //评论框
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.clearFocus();
        //发送评论
        btn_send = (Button) findViewById(R.id.btn_send_comment);
        //表情按钮
        img_face = (ImageView) findViewById(R.id.img_face);
        iv_dig = (ImageView) findViewById(R.id.iv_dig);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);
        iv_transport = (ImageView) findViewById(R.id.iv_transport);

        list_face = (ListFaceView) findViewById(R.id.face_view);
        list_face.initSmileView(et_comment);
        list = new ListData<SociaxItem>();
        adapter = new AdapterCommentList(this, list, weibo_id);
        adapter.setType("weibo");

        list_comment.setAdapter(adapter);
        // 下面的内容在没有背景图的列表中用到于根据type来自区分显示内容

        tv_post_is_delete = (TextView) headerView
                .findViewById(R.id.tv_post_is_delete);
        ll_post_no_delete = (LinearLayout) headerView
                .findViewById(R.id.ll_post_no_delete);
        ll_from_weibo_content = (LinearLayout) headerView
                .findViewById(R.id.ll_from_weibo_content);
        ll_from_weiba_content = (LinearLayout) headerView
                .findViewById(R.id.ll_from_weiba_content);
        tv_post_title = (TextView) headerView.findViewById(R.id.tv_post_title);
        tv_post_content = (TextView) headerView
                .findViewById(R.id.tv_post_content);
        tv_post_from = (TextView) headerView.findViewById(R.id.tv_post_from);

        appendWeibo = new AppendWeibo(ActivityWeiboDetail.this);

        stub_uname_adn = (ViewStub) findViewById(R.id.stub_uname_adn);
        stub_weiba = (ViewStub) headerView.findViewById(R.id.stub_weiba);
        stub_image = (ViewStub) headerView.findViewById(R.id.stub_image);
        //图片列表
        stub_image_group = (ViewStub) headerView.findViewById(R.id.stub_image_group);
        stub_media = (ViewStub) headerView.findViewById(R.id.stub_media);
        stub_file = (ViewStub) headerView.findViewById(R.id.stub_file);
        stub_address = (ViewStub) headerView.findViewById(R.id.stub_address);
        stub_add_follow = (ViewStub) headerView.findViewById(R.id.stub_add_follow);

        //转发
        stub_transport_weibo = (ViewStub)headerView.findViewById(R.id.stub_transport);
        stub_transport_weiba = (ViewStub)headerView.findViewById(R.id.stub_weiba_transport);


    }

    /**
     * 标记是否第一次设置头部，onresume时候头部内容会错误，重新处理
     */
    boolean hasInitHeaderContent = false;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public String getTitleCenter() {
        return "分享详情";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return null;
    }

    @Override
    public void finish() {
        if (changeData) {
            WeiboEvent event = new WeiboEvent(position, weibo);
            EventBus.getDefault().post(event);
        }
        resetCommentUI();
        super.finish();
    }

    @Override
    public void executeDataSuccess(ListData<SociaxItem> list) {
        if (list.size() < AdapterSociaxList.PAGE_COUNT){
            //如果是加载更多且返回内容条数少于一页,表示没有更多了
            if(adapter.getMaxid() != 0 &&
                    adapter.getAdapterState() == AdapterSociaxList.NO_MORE_DATA)
                Toast.makeText(ActivityWeiboDetail.this, "没有更多了", Toast.LENGTH_SHORT).show();
            pullRefresh.setMode(Mode.PULL_FROM_START);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_weibo_detail2;
    }


    public void refreshHeader() {
        if (adapter != null)
            adapter.doRefreshHeader();
    }

    @Override
    public void refreshFooter() {
        if (adapter != null)
            adapter.doRefreshFooter();
    }

    protected ListFaceView.FaceAdapter mFaceAdapter = new ListFaceView.FaceAdapter() {

        @Override
        public void doAction(int paramInt, String paramString) {
            EditText localEditDiggView = et_comment;
            int i = localEditDiggView.getSelectionStart();
            int j = localEditDiggView.getSelectionStart();
            String str1 = "[" + paramString + "]";
            String str2 = localEditDiggView.getText().toString();
            SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
            localSpannableStringBuilder.append(str2, 0, i);
            localSpannableStringBuilder.append(str1);
            localSpannableStringBuilder.append(str2, j, str2.length());
            UnitSociax.showContentFaceView(ActivityWeiboDetail.this,
                    localSpannableStringBuilder);
            localEditDiggView.setText(localSpannableStringBuilder,
                    TextView.BufferType.SPANNABLE);
            localEditDiggView.setSelection(i + str1.length());
            Log.v("Tag", localEditDiggView.getText().toString());
        }
    };

    @SuppressLint("HandlerLeak")
    public class ListHandler extends Handler {

        public ListHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pullRefresh.onRefreshComplete();
            switch (msg.what) {
                case AppConstant.COMMENT:
                    btn_send.setEnabled(true);
                    et_comment.setTag(null);
                    int result = msg.arg1;
                    if (result == 1) {
                        Toast.makeText(ActivityWeiboDetail.this, "评论成功", Toast.LENGTH_SHORT).show();
                        SociaxUIUtils.hideSoftKeyboard(ActivityWeiboDetail.this, et_comment);
                        weibo.setCommentCount(weibo.getCommentCount() + 1); // 评论
                        tv_all_comment.setText("全部评论(" + weibo.getCommentCount()
                                + ")");
                        et_comment.setText("");
                        if (adapter != null) {
                            adapter.doUpdataList();
                        }
                        changeData = true;
                    } else {
                        Toast.makeText(ActivityWeiboDetail.this, "评论失败",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConstant.GET_WEIBO_DIGG:
                    if (msg.arg1 == AppConstant.NO_DIGG_USER
                            || list_digguser == null || list_digguser.size() == 0) {
                        ll_digglist.setVisibility(View.GONE);
                        iv_arrow.setVisibility(View.GONE);
                    } else {
                        ll_digglist.removeAllViews();
                        for (int i = 0; i < list_digguser.size(); i++) {
                            ImageView img = new ImageView(getApplicationContext());

                            Glide.with(getApplicationContext()).load(list_digguser.get(i).getAvatar())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(new GlideCircleTransform(getApplicationContext()))
                                    .crossFade()
                                    .into(img);

                            //设置点赞用户头像
                            int width = UnitSociax.dip2px(ActivityWeiboDetail.this, 27);
                            LinearLayout.LayoutParams lp = new LayoutParams(width, width);
                            lp.setMargins(0, 0, SociaxUIUtils.dip2px(getApplicationContext(), 4), 0);
                            img.setLayoutParams(lp);
                            ll_digglist.addView(img);
                        }
                        ll_digglist.setVisibility(View.VISIBLE);
                        iv_arrow.setVisibility(View.VISIBLE);
                    }
//                    setWeiboHeaderContent();
                    break;
                case AppConstant.CHANGE_WEIBO_DIGG:
                    changeData = true;
//                    getDiggList();
                    break;
                case AppConstant.DEL_COMMENT:
                    if (msg.arg1 == 0) {
                        Toast.makeText(ActivityWeiboDetail.this, "操作失败", Toast.LENGTH_SHORT).show();
                    } else {
                        ((AdapterCommentList)adapter).removeItem((ModelComment)msg.obj);
                        //从微博中删除评论
                        weibo.setCommentCount(weibo.getCommentCount() - 1);
                        tv_all_comment.setText("评论 " + weibo.getCommentCount());
                        weibo.getCommentList().remove((ModelComment)msg.obj);
                        setWeiboContent(weibo);
                        changeData = true;
                    }
                    break;
            }
        }
    }

    /**
     * 回复评论
     */
    public void replay(ModelComment md) {
        String content = "回复" + md.getUname() + ":";
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        //用户名设置黑色高亮
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 2, 2 + md.getUname().length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        et_comment.setText(style);
        et_comment.setSelection(et_comment.length());
    }

    /**
     * 删除评论
     *
     * @param md
     */

    public void deleteComment(final ModelComment md) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                //删除评论
                Message message = mHandler.obtainMessage();
                message.what = AppConstant.DEL_COMMENT;
                message.arg1 = 0;
                try {
                    String result = (String) new Api.WeiboApi().deleteWeiboComment(md.getComment_id());
                    JSONObject json = new JSONObject(result);
                    if (json.has("status") && json.getInt("status") == 1) {
                        message.arg1 = 1;
                        message.obj = md;
                    } else {
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mHandler.sendMessage(message);
            }

        }).start();
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        switch (id) {
            case R.id.ll_digg_users:
                //查看点赞列表
                Intent intent = new Intent(ActivityWeiboDetail.this, ActivitySearchUser.class);
                intent.putExtra("type", StaticInApp.WEIBO_DIGG_LIST);
                intent.putExtra("title", "点赞列表");
                intent.putExtra("weibo_id", weibo_id);
                startActivity(intent);
                break;
            case R.id.iv_dig:
                iv_dig.setEnabled(false);
                detailsPresenter.postDigWeibo(weibo);
                break;
            case R.id.iv_collect:
                //收藏微博
                if (weibo == null) {
//                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                    return;
                }
                changeCollectionState();
                break;
            case R.id.iv_transport:
                if (weibo == null) {
                    Toast.makeText(this, "操作已阻止", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (popup == null)
                    popup = new PopupWindowWeiboMore(
                            ActivityWeiboDetail.this, weibo);
                popup.transport();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.img_face:
                if (list_face.getVisibility() == View.VISIBLE) {
                SociaxUIUtils.showSoftKeyborad(ActivityWeiboDetail.this, et_comment);
                img_face.setImageResource(R.drawable.face_bar);
                list_face.setVisibility(View.GONE);
            } else {
                SociaxUIUtils.hideSoftKeyboard(ActivityWeiboDetail.this,
                        et_comment);
                img_face.setImageResource(R.drawable.key_bar);
                list_face.setVisibility(View.VISIBLE);
            }
                break;
            case R.id.iv_share:
                if (popup == null) {
                    popup = new PopupWindowWeiboMore(
                            ActivityWeiboDetail.this, weibo);
                    popup.showBottom(ll_comment);
//                    popup.hideCollect();
//                    popup.hideTransport();
                } else {
                    popup.showBottom(ll_comment);
                }
                break;
        }
    }

}
