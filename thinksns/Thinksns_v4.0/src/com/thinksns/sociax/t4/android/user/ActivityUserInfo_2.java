package com.thinksns.sociax.t4.android.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.concurrent.BitmapDownloaderTask;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.db.WeiboSqlHelper;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.adapter.AdapterUserInfoAlbum;
import com.thinksns.sociax.t4.adapter.AdapterUserInfoGift;
import com.thinksns.sociax.t4.adapter.AdapterUserInfoHome;
import com.thinksns.sociax.t4.adapter.AdapterUserWeiboList;
import com.thinksns.sociax.t4.adapter.AdapterWeiboList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.android.img.Bimp;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowWeiboMore;
import com.thinksns.sociax.t4.android.presenter.UserHomePresenter;
import com.thinksns.sociax.t4.android.temp.SelectImageListener;
import com.thinksns.sociax.t4.android.view.IUserHomeView;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.ScrollViewSociax;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.unit.ImageUtil;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.widget.SmallDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 类说明： 个人主页， 需要传入int uid，或者String uname
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-19
 */
public class ActivityUserInfo_2 extends ThinksnsAbscractActivity implements OnClickListener,
        PullToRefreshBase.OnRefreshListener2<ListView>, IUserHomeView {
    private static final String TAG = "ActivityUserInfo2";

    private static final int ADD_FOLLOWED = 20;        //加关注
    private static final int DEL_FOLLOWED = 21;        //取消关注
    private static final int LOAD_USER_INFO = 22;    //加载用户数据
    private static final int ADD_BLACKLIST = 23;    //拉黑
    private static final int DEL_BLACKLIST = 24;    //取消拉黑
    private static final int ADD_CONTACT = 25;
    private static final int DEL_CONTACT = 26;
    private static final int SELETE = 27;
    private static final int UPLOAD_FACE = 11;
    private static final int UPLOAD_COVER = 12;
    private static final int LOCATION = 1;
    private static final int CAMERA = 0;
    private static final int IO_BUFFER_SIZE = 4 * 1024;
    private static final int FLOAT_HEADER_VIEW = 28;
    private static final int FLOAT_GONE = 29;
    private static final int FLOAT_VISIBILTY = 30;

    private final int SELETE_HOME = 0;
    private final int SELETE_WEIBO = 1;
    private final int SELETE_ALBUM = 2;
    private final int SELETE_GIFT = 3;
    // 点击更多用到的内容
    protected RelativeLayout rl_comment;
    protected EditText et_comment;
    protected Button btn_send;
    protected ImageView img_face;
    protected ListFaceView list_face;
    protected ModelWeibo selectWeibo;
    protected int selectpostion;
    protected ListHandler mHandler;
    protected RelativeLayout rl_more;           // 用来隐藏more popwindow
    protected String isCanSendMessage = "INIT";
    protected LinearLayout ll_user_group;       // caoligai 添加用户组图标

    protected Thinksns app;     // app

    /**
     * 相册列表datper
     */
    AdapterUserInfoAlbum adapterAlbum;
    /**
     * 主页adapter
     */
    AdapterUserInfoHome adapterHome;
    ListData<SociaxItem> userList = new ListData<SociaxItem>();
    AdapterSociaxList adapter;
    // 礼物已弃用，直接放在home内，暂时保留
    AdapterUserInfoGift adapterGift;
    private ListData<SociaxItem> listWeibo, listGift;
    private boolean refreshing = false, isInitData = false;
    private boolean loadFinish = false; //数据是否加载完毕

    private int selected = -1;
    private ActivityHandler handler;
    private ResultHandler resultHandler;
    private ImageView header;
    private Bitmap newHeader;
    private ImageView /* followButton, */img_back, /* img_change_info, */
            img_right/* , img_removebacklist */;
    private TextView tv_change_info, tv_follow,
            tv_sendMessage,
            tv_removebacklist,
            tv_followed_count,
            tv_follower_count,
            tv_intro_info,
            tv_title;
    private TextView tvName;
    private ImageView imSex;
    private ImageView img_level;
    private ProgressDialog prDialog;
    final HttpResponseListener userListener = new HttpResponseListener() {

        @Override
        public void onSuccess(Object result) {
            ListData<SociaxItem> list = (ListData<SociaxItem>) result;
            if (list != null && list.size() == 1) {
                ModelUser user = (ModelUser) list.get(0);
                tv_title.setText(user.getUserName());
                // 是否可以发私信
//                getSendMessagePower(user.getUid());
                if (user.getUid() == Thinksns.getMy().getUid()) {
                    //更新本地用户数据
                    Thinksns.setMy(user);
                    Thinksns app = (Thinksns) ActivityUserInfo_2.this.getApplicationContext();
                    int i = app.getUserSql().updateUser(user);
                }

                Message mainMsg = new Message();
                mainMsg.what = ResultHandler.SUCCESS;
                mainMsg.obj = user;
                mainMsg.arg1 = LOAD_USER_INFO;
                resultHandler.sendMessage(mainMsg);
                if (adapter != null && adapter instanceof AdapterUserInfoAlbum) {
                    adapter.notifyDataSetChanged();
                }
            }
        }


        @Override
        public void onError(Object result) {
            hideProgressDialog();
        }
    };
    private LoadingView mLoadingView;
    private LinearLayout mLyUserInfoView, ll_change_info;
    private LinearLayout infoUtilLayout;
    //底部加关注、聊天
    private LinearLayout ll_bottom;
    private ModelUser user;
    private ScrollViewSociax svSociax;
    private RadioButton rb_weibo, rb_home, rb_album, rb_gift;
    private RelativeLayout rl_weibo, rl_home, rl_album, rl_gift;
    // 自定义背景
    private ImageView iv_userinfo_bg;
    private LinearLayout ll_tabs;
    private LinearLayout ll_tab_title;

    private LoadingView loadingView;
    private RelativeLayout ll_title;
    private View titleBottomLine;

    /**
     * 顶部的用户列表
     */
    private View headerView;
    //选项卡视图
    private View tabView;

    /**
     * 中间的内容部分
     */
    private ListView listView;
    private PullToRefreshListView pullRefresh;
    /**
     * 分享列表adapter
     */
    private AdapterUserWeiboList adapterWeibo;
    private Drawable drawable_following;
    private FragmentSociax fragment;
    private File cameraFile;// 相片文件
    private UIImgHandler uiImgHandler;// 处理图片ui线程
    private AlertDialog.Builder builder = null;
    private Button btn_no_authority;
    private Dialog dialog = null;
    private Thinksns application;
    private boolean isChanged = false;
    private int uid = -1;
    private headImageChangeListener changeListener;
    private SelectImageListener selectImageListener;
    private boolean hasImage;
    private SmallDialog smallDialog;

    private UserHomePresenter mPrenster;
    private int contentHeight;      //中间主体内容高度
    private LinearLayout ll_follow_info;//关注，粉丝

    private View v_home,v_share,v_ablume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        mPrenster = new UserHomePresenter(this, this);
        initIntentData();
        initView();
        initOnClickListener();
        initData();
    }


    private void initIntentData() {
        Thinksns app = (Thinksns) this.getApplicationContext();
        Worker thread = new Worker(app, "Loading UserInfo");
        handler = new ActivityHandler(thread.getLooper(), this);
        resultHandler = new ResultHandler();
        uiImgHandler = new UIImgHandler();

    }

    /**
     * 选择主页
     */
    private void selectHome() {
        listView.setBackgroundColor(this.getResources().getColor(R.color.bg_ios));
        if (adapterHome == null || adapterHome.getCount() == 0) {
            mPrenster.loadUserInfo(user);
            adapterHome = new AdapterUserInfoHome(ActivityUserInfo_2.this, userList, user);
            adapterHome.setState(AdapterSociaxList.STATE_LOADING);
        }
        //主页无需上拉加载更多
        pullRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setAdapter(adapterHome);
    }

    /**
     * 选择分享
     */
    private void selectWeibo() {
        listView.setBackgroundColor(this.getResources().getColor(R.color.white));
        if (adapterWeibo == null) {
            listWeibo = WeiboSqlHelper.getInstance(this).getWeiboListByUid(user.getUid());
            adapterWeibo = new AdapterUserWeiboList(this, listWeibo, user.getUid());
        }

        if (listWeibo.size() == 0) {
            //获取最新内容
            adapterWeibo.loadInitData();
        } else {
            adapterWeibo.doUpdataList();
        }

        listView.setAdapter(adapterWeibo);
        //设置列表正文内容高度
        if (contentHeight != 0) {
            adapterWeibo.setContentHeight(contentHeight);
        }

        if (listWeibo.size() < AdapterSociaxList.PAGE_COUNT) {
            pullRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        } else {
            pullRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        }

    }

    /**
     * 选择相册
     */
    private void selectAlbum() {
        listView.setBackgroundColor(this.getResources().getColor(R.color.bg_ios));
        if (adapterAlbum == null) {
            adapterAlbum = new AdapterUserInfoAlbum(
                    ActivityUserInfo_2.this, new ListData<SociaxItem>(),
                    user);
        }

        //主页无需上拉加载更多
        pullRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setAdapter(adapterAlbum);
    }


    /**
     * 设置ListView 的heaerview-->radiogroup的背景
     *
     * @param selete
     */
    @SuppressLint("NewApi")
    void setRadioButtonBackGround(int selete) {
        rb_album.setChecked(false);
        rb_home.setChecked(true);
        rb_weibo.setChecked(false);
        rb_home.setTextSize(14);
        rb_weibo.setTextSize(14);
        rb_album.setTextSize(14);
        rb_album.setTextColor(getResources().getColor(R.color.title_black));
        rb_weibo.setTextColor(getResources().getColor(R.color.title_black));
        rb_home.setTextColor(getResources().getColor(R.color.title_black));
//        rb_home.setBackground(null);
//        rb_weibo.setBackground(null);
//        rb_album.setBackground(null);

        switch (selete) {
            case SELETE_HOME:
                rb_home.setChecked(true);
                rb_home.setTextColor(getResources().getColor(R.color.title_blue));
//                rb_home.setBackgroundResource(R.drawable.bottom_border_blue);

                v_home.setVisibility(View.VISIBLE);
                v_share.setVisibility(View.INVISIBLE);
                v_ablume.setVisibility(View.INVISIBLE);
                break;

            case SELETE_WEIBO:
                rb_weibo.setChecked(true);
                rb_weibo.setTextColor(getResources().getColor(
                        R.color.title_blue));
//                rb_weibo.setBackgroundResource(R.drawable.bottom_border_blue);

                v_home.setVisibility(View.INVISIBLE);
                v_share.setVisibility(View.VISIBLE);
                v_ablume.setVisibility(View.INVISIBLE);
                break;
            case SELETE_ALBUM:
                rb_album.setChecked(true);
                rb_album.setTextColor(getResources().getColor(
                        R.color.title_blue));
//                rb_album.setBackgroundResource(R.drawable.bottom_border_blue);

                v_home.setVisibility(View.INVISIBLE);
                v_share.setVisibility(View.INVISIBLE);
                v_ablume.setVisibility(View.VISIBLE);
                break;
        }
    }

    @SuppressLint("NewApi")
    private void initView() {
        //顶部栏
        img_back = (ImageView) findViewById(R.id.img_back);
        img_right = (ImageView) findViewById(R.id.img_more);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ll_tab_title = (LinearLayout) findViewById(R.id.ll_tab_title);
        ll_title = (RelativeLayout) findViewById(R.id.ll_title);
        titleBottomLine = findViewById(R.id.title_bottom_line);

        //底部操作栏、发私信、加关注
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tv_follow = (TextView) findViewById(R.id.tv_follow);
        tv_sendMessage = (TextView) findViewById(R.id.tv_chat);

        builder = new AlertDialog.Builder(ActivityUserInfo_2.this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.dialog_no_authority, null);
        builder.setView(view1);
        btn_no_authority = (Button) view1.findViewById(R.id.btn_no_authority);

        //添加列表头部数据
        initHeader();
        //初始化下拉刷新列表
        initListView();
        //加关注图标
        drawable_following = ActivityUserInfo_2.this.getResources().getDrawable(R.drawable.ic_fllow);

        rl_more = (RelativeLayout) findViewById(R.id.rl_more);
        mHandler = new ListHandler();
        rl_comment = (RelativeLayout) findViewById(R.id.ll_send_comment);
        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_send = (Button) findViewById(R.id.btn_send_comment);
        img_face = (ImageView) findViewById(R.id.img_face);
        list_face = (ListFaceView) findViewById(R.id.face_view);
        list_face.initSmileView(et_comment);

        application = (Thinksns) ActivityUserInfo_2.this.getApplicationContext();
        smallDialog = new SmallDialog(this, "加载中...");
        smallDialog.setCanceledOnTouchOutside(false);
        selectImageListener = new SelectImageListener(
                ActivityUserInfo_2.this, null);

    }

    private void initListView() {
        pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        //设置刷新模式
        pullRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullRefresh.setOnRefreshListener(this);
        listView = pullRefresh.getRefreshableView();

        listView.addHeaderView(headerView);
        listView.setTranslationY(-10);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (resultHandler != null) {
                    Message msg = resultHandler.obtainMessage(FLOAT_HEADER_VIEW);
                    if (headerView.getBottom() < ll_title.getBottom() + tabView.getHeight() && loadFinish) {
                        msg.arg1 = FLOAT_VISIBILTY;
                    } else {
                        msg.arg1 = FLOAT_GONE;
                    }
                    resultHandler.sendMessageDelayed(msg, 20);
                }
            }
        });
        headerView.post(new Runnable() {
            @Override
            public void run() {
                contentHeight = UnitSociax.getWindowHeight(ActivityUserInfo_2.this) - headerView.getHeight() - ll_bottom.getHeight();
            }
        });

    }


    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullRefresh;
    }

    private void initOnClickListener() {
        //编辑资料
        tv_change_info.setOnClickListener(this);

        img_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isChanged) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        });

        rb_album.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setSelected(SELETE_ALBUM);
            }
        });
        rb_home.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setSelected(SELETE_HOME);
            }
        });
        rb_weibo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setSelected(SELETE_WEIBO);
            }
        });

        //菜单项
        img_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popUpBlackList();
            }

        });

        //关注、拉黑
        tv_follow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setClickable(false);
                if (user.getIsInBlackList()) {
                    mPrenster.postUserBlackList(user);
                } else {
                    mPrenster.postUserFollow(user);
                }
            }
        });
        //私聊
        tv_sendMessage.setOnClickListener(this);
    }

    //弹出黑名单选择列表
    private void popUpBlackList() {
        final PopupWindowListDialog.Builder options = new PopupWindowListDialog.Builder(this);
        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (user.getIsInBlackList()) {
                    if (position == 0) {
                        //移除黑名单,关注按钮设置不可点击
                        tv_follow.setEnabled(false);
                        mPrenster.postUserBlackList(user);
                    }
//                    else if (position == 1) {
//                        //举报
//                    }
                    else if (position == 1) {
//                    else if (position == 2) {
                        //取消
                        options.dimss();
                    }
                } else {
                    if (position == 0) {
                        //加入黑名单,关注按钮设置不可点击
                        tv_follow.setEnabled(false);
                        mPrenster.postUserBlackList(user);
                    } else if (position == 1) {
                        //加关注/取消关注
                        mPrenster.postUserFollow(user);
                    }
//                    else if (position == 2) {
//                            //举报
//                    }
                    else if (position == 2) {
//                    } else if (position == 3) {
                        //取消
                        options.dimss();
                    }
                }
            }
        });

        List<String> datas = new ArrayList<String>();
        if (user.getIsInBlackList()) {
            datas.add("移除黑名单");
        } else {
            datas.add("加入黑名单");
            if (user.isFollowed()) {
                datas.add("取消关注");
            } else {
                datas.add("加关注");
            }
        }

//        datas.add("举报");
        datas.add("取消");

        options.create(datas);
    }

    private void initHeader() {
        //用户头部信息
        headerView = LayoutInflater.from(this).inflate(R.layout.activity_user_info_header, null);
        //个人封面
        iv_userinfo_bg = (ImageView) headerView.findViewById(R.id.iv_userinfo_bg);
        ll_follow_info = (LinearLayout) headerView.findViewById(R.id.ll_follow_info);
        iv_userinfo_bg.setOnClickListener(this);
        ll_tabs = (LinearLayout) headerView.findViewById(R.id.ll_tabs);
        tabView = LayoutInflater.from(this).inflate(R.layout.user_home_tab_items, null);
        //相册
        rb_album = (RadioButton) tabView.findViewById(R.id.rb_album);
        //主页
        rb_home = (RadioButton) tabView.findViewById(R.id.rb_home);
        //分享
        rb_weibo = (RadioButton) tabView.findViewById(R.id.rb_weibo);

        v_home=(View)tabView.findViewById(R.id.v_home);
        v_share=(View)tabView.findViewById(R.id.v_share);
        v_ablume=(View)tabView.findViewById(R.id.v_ablume);

        //编辑资料
        tv_change_info = (TextView) headerView.findViewById(R.id.tv_change_info);
        ll_change_info = (LinearLayout) headerView.findViewById(R.id.ll_change_info);
        //用户头像
        header = (ImageView) headerView.findViewById(R.id.iv_user_header);
        header.setOnClickListener(this);
        //用户昵称
        tvName = (TextView) headerView.findViewById(R.id.tv_user_name);
        //性别
        imSex = (ImageView) headerView.findViewById(R.id.im_sex);
        //等级
        img_level = (ImageView) headerView.findViewById(R.id.img_level);
        //关注
        tv_followed_count = (TextView) headerView.findViewById(R.id.tv_followed_count);
        //粉丝
        tv_follower_count = (TextView) headerView.findViewById(R.id.tv_follower_count);
        //简介
        tv_intro_info = (TextView) headerView.findViewById(R.id.tv_intro_info);
        tv_intro_info.setOnClickListener(this);
        // caoligai 修改
        ll_user_group = (LinearLayout) headerView.findViewById(R.id.ll_uname_adn);
    }

    /**
     * 动态设置控件左边的图片
     */
    public void setLeftDrawable(Drawable drawable, TextView textView) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
            textView.setCompoundDrawables(drawable, null, null, null);
        } else {
            textView.setCompoundDrawables(null, null, null, null);
        }
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(final int selected) {
        this.selected = selected;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = resultHandler.obtainMessage();
                msg.arg1 = selected;
                msg.what = SELETE;
                msg.sendToTarget();
            }
        }).start();
    }

    //初始化数据
    private void initData() {
        if (refreshing) {
            Toast.makeText(this, R.string.re_load, Toast.LENGTH_LONG).show();
            return;
        }

        int uid = getIntent().getIntExtra("uid", -1);
        String name = getIntent().getStringExtra("uname");
        if (name != null && !name.equals(Thinksns.getMy().getUserName())) {
            user = UserSqlHelper.getInstance(this).getUserByName(name);
            if (user == null) {
                user = new ModelUser();
                user.setUserName(name);
            } else {
                setUserHeadInfo(user);
                userList.add(user);
            }
        } else if (uid != -1 && uid != Thinksns.getMy().getUid()) {
            user = UserSqlHelper.getInstance(this).getUserById(uid);
            if (user == null) {
                user = new ModelUser();
                user.setUid(uid);
            } else {
                setUserHeadInfo(user);
                userList.add(user);
            }
        } else {
            user = Thinksns.getMy();
            setUserHeadInfo(user);
            userList.add(user);
        }

        //是否关注
        boolean isFollow = getIntent().getBooleanExtra("is_follow", false);
        user.setFollowed(isFollow);
        user.setToken(Thinksns.getMy().getToken());
        user.setSecretToken(Thinksns.getMy().getSecretToken());

        if (user.getUid() != Thinksns.getMy().getUid()
                || !user.getUserName().equals(Thinksns.getMy().getUserName())) {
            //访问他人的主页
            img_right.setVisibility(View.VISIBLE);
            ll_bottom.setVisibility(View.VISIBLE);
            iv_userinfo_bg.setEnabled(false);
            //设置用户关注状态
            setUserFollow(-1, user.isFollowed());
            setSelected(SELETE_HOME);
        } else {
            //个人主页不显示简介、不显示菜单项、不显示底部栏
            img_right.setVisibility(View.GONE);
            ll_bottom.setVisibility(View.GONE);
            //可更换自己封面
            iv_userinfo_bg.setEnabled(true);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pullRefresh.getLayoutParams();
            params.bottomMargin = 0;

            setSelected(SELETE_WEIBO);
        }

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        if (selected == SELETE_WEIBO) {
            selectWeibo();
        } else {
            mPrenster.loadUserInfo(user);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        //上拉加载更多
        if (selected == SELETE_WEIBO) {
            adapterWeibo.doRefreshFooter();
        }
    }


    /**
     * 点击更多内的照相图标
     */
    private void selectPicFromCamera() {
        if (!UnitSociax.isExitsSdcard()) {
            Toast.makeText(this.getApplicationContext(),
                    "SD卡不存在，不能拍照", Toast.LENGTH_SHORT).show();
            return;
        }
        cameraFile = new File(Environment.getExternalStorageDirectory(),
                StaticInApp.cache);
        if (!cameraFile.exists())
            cameraFile.mkdirs();
        cameraFile = new File(cameraFile, System.currentTimeMillis() + ".jpg");
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                StaticInApp.REQUEST_CODE_CAMERA);
    }

    /**
     * 从本地选择图片
     */
    private void selectPicFromLocal() {
        Intent getImage = new Intent(this, MultiImageSelectorActivity.class);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
        startActivityForResult(getImage, StaticInApp.LOCAL_IMAGE);
    }

    /**
     * 设置listView包含内容
     */
    public void setListViewContent() {
        if (selected == SELETE_GIFT) {
        } else if (selected == SELETE_ALBUM) {
            //家在相册
            selectAlbum();
        } else if (selected == SELETE_WEIBO) {
            //加载微博
            selectWeibo();
        } else {
            //加载主页
            selectHome();
        }
    }

    // ///////////////////********** 相片处理 **************************///////////

    /**
     * 设置列表更多是否可见
     */
    public void setListFooterViewVisibility() {
        HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) listView
                .getAdapter();
        AdapterSociaxList adapter = (AdapterSociaxList) headerAdapter
                .getWrappedAdapter();
        if (adapter != null) {
            if (adapter.isShowFooter()) {
//				listView.showFooterView();
            } else {
//				listView.hideFooterView();
            }
        }
    }

    final protected void dowloaderTask(String url, ImageView image,
                                       BitmapDownloaderTask.Type type) {
        BitmapDownloaderTask task = new BitmapDownloaderTask(image, type);
        task.execute(url);
    }

    final protected void dowloaderTask(String url, ImageView image,
                                       BitmapDownloaderTask.Type type, Context context) {
        BitmapDownloaderTask task = new BitmapDownloaderTask(image, type, context);
        task.execute(url);
    }

    // //************************************
    private void startProgressDialog(String info) {
        smallDialog.setContent(info);
        smallDialog.show();
    }

    private void hideProgressDialog() {
        smallDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap btp = null;
            switch (requestCode) {
                case StaticInApp.CAMERA_IMAGE:
                    try {
                        File file = new File(selectImageListener.getImagePath());
                        int imageSize[] = Bimp.getLocalImageSize(selectImageListener.getImagePath());
                        selectImageListener.startPhotoZoom(Uri.fromFile(file), 0, 0);
                    } catch (Exception e) {
                        Log.e(TAG, "file saving..." + e.toString());
                    }
                    break;
                case StaticInApp.LOCAL_IMAGE:
                    List<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (list != null && list.size() > 0) {
                        String path = list.get(0);
                        selectImageListener.setImagePath(path);
//                        int imageSize[] = Bimp.getLocalImageSize(path);
//                        selectImageListener.startPhotoZoom(UriUtils.pathToUri(this, path),
//                                0, 0);
                        setImgFileMessage(path);
                        startProgressDialog("上传中...");
                    }
                    break;
                case StaticInApp.ZOOM_IMAGE:
                    //上传自定义封面
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            btp = extras.getParcelable("data");
                            setImgFileMessage(selectImageListener.getImagePath());
                            iv_userinfo_bg.setImageBitmap(btp);
                            startProgressDialog("正在上传...");
                        }
                    } else {
                        Log.d(AppConstant.APP_TAG, "data is null  .... ");
                    }
                    break;
//                case LOCATION:
//                    btp = checkImage(data);
//                    startPhotoZoom(data.getData());
//                    break;
//                case 3:
//                    if (data != null) {
//                        Bundle extras = data.getExtras();
//                        if (extras != null) {
//                            btp = extras.getParcelable("data");
//                            Log.d(AppConstant.APP_TAG, "sava cut ....");
//                            Message msg = handler.obtainMessage();
//                            msg.what = UPLOAD_FACE;
//                            msg.arg1 = UPLOAD_FACE;
//                            msg.obj = btp;
//                            loadingView = (LoadingView) findViewById(LoadingView.ID);
//                            startProgressDialog("正在上传...");
//                            handler.sendMessage(msg);
//                        }
//                    } else {
//                        Log.d(AppConstant.APP_TAG, "data is null  .... ");
//                    }
//                    break;
                case StaticInApp.REQUEST_CODE_CAMERA:
                    if (cameraFile != null && cameraFile.exists()) {
                        String cameraPath = cameraFile.getAbsolutePath();
                        setImgFileMessage(cameraPath);
                    }

                    break;
                case StaticInApp.REQUEST_CODE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            setPicByUri(selectedImage);
                        }
                    }
                    break;
                case StaticInApp.CHANGE_USER:
                    setUserHeadInfo(Thinksns.getMy());
                    isChanged = true;
                    if (adapterHome != null) {
                        pullRefresh.setRefreshing();
                    }
                    break;
            }

            if (btp != null) {
                this.hasImage = true;
            }
        }
    }

    // 发送图片文件
    private void setImgFileMessage(final String img_path) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                Thinksns app = (Thinksns) getApplication();
                Message message = uiImgHandler.obtainMessage(StaticInApp.UPLOAD_PIC);
                message.obj = app.getUsers().changeBackGround(img_path);
                uiImgHandler.sendMessage(message);

            }
        }).start();
    }

    /**
     * 发送图片url
     *
     * @param selectedImage
     */
    private void setPicByUri(Uri selectedImage) {
        Cursor cursor = this.getContentResolver().query(selectedImage,
                null, null, null, null);
        String filepath = null;
        if (cursor != null) {
            cursor.moveToFirst();
            filepath = cursor.getString(cursor.getColumnIndex("_data"));
            cursor.close();
            cursor = null;
        } else {
            filepath = selectedImage.getPath();
        }
        setImgFileMessage(filepath);
    }

    protected Activity getTabActivity() {
        return this;
    }

    @Override
    public String getTitleCenter() {
        return getString(R.string.user_info_tit);
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info_2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (smallDialog.isShowing())
            smallDialog.dismiss();
    }

    @Override
    public void refreshHeader() {
        if (adapter != null) {
            adapter.doRefreshHeader();
        }
    }

    @Override
    public void refreshList() {
        if (adapter != null) {
            adapter.doUpdataList();
        }
    }

    /**
     * 点击某个微博的评论
     *
     * @param i
     */
    public void clickComment(int i) {
        selectWeibo = (ModelWeibo) listWeibo.get(i);
        if (selectWeibo.isCan_comment()) {
            setCommentVisible();
            selectpostion = i;
            btn_send.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    img_face.setImageResource(R.drawable.key_bar);
                    list_face.setVisibility(View.GONE);
                    String content = et_comment.getText().toString().trim();
                    if (content.length() == 0) {
                        et_comment.setError("评论不能为空");
                    } else {
                        final Comment comment = new Comment();
                        comment.setContent(content);
                        comment.setStatus(selectWeibo);
                        comment.setUname(Thinksns.getMy().getUserName());
                        mHandler = new ListHandler();
                        selectWeibo.getComments().add(0, comment);
                        selectWeibo.setCommentCount(selectWeibo
                                .getCommentCount() + 1);
                        final Object obj[] = new Object[]{selectWeibo,
                                rl_comment, et_comment, selectpostion};
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                Message msg = mHandler.obtainMessage();
                                try {
                                    msg.what = AppConstant.COMMENT;
                                    msg.obj = obj;
                                    msg.arg1 = new Api.StatusesApi()
                                            .comment(comment);
                                } catch (VerifyErrorException e) {
                                    e.printStackTrace();
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                } catch (UpdateException e) {
                                    e.printStackTrace();
                                } catch (DataInvalidException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mHandler.sendMessage(msg);
                            }
                        }).start();
                    }
                }
            });
        } else {
            Toast.makeText(this, "您没有权限评论TA的分享", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 设置评论框可见
     */
    public void setCommentVisible() {
        if (rl_comment != null) {
            rl_comment.setVisibility(View.VISIBLE);
            rl_comment.setFocusable(true);
        }
        et_comment.setFocusable(true);
        et_comment.setClickable(true);
        et_comment.setSelected(true);
        et_comment.setFocusableInTouchMode(true);
        et_comment.requestFocus();
        et_comment.requestFocusFromTouch();
        et_comment.setText("");
        if (list_face.getVisibility() != View.VISIBLE) {
            SociaxUIUtils.showSoftKeyborad(this, et_comment);
        }
        img_face.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (list_face.getVisibility() == View.VISIBLE) {
                    SociaxUIUtils.showSoftKeyborad(ActivityUserInfo_2.this,
                            et_comment);
                    img_face.setImageResource(R.drawable.face_bar);
                    list_face.setVisibility(View.GONE);
                } else {
                    SociaxUIUtils.hideSoftKeyboard(ActivityUserInfo_2.this,
                            et_comment);
                    img_face.setImageResource(R.drawable.key_bar);
                    list_face.setVisibility(View.VISIBLE);
                }
            }
        });
        et_comment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                img_face.setImageResource(R.drawable.face_bar);
                list_face.setVisibility(View.GONE);
            }
        });
        rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                rl_comment.setVisibility(View.GONE);
                SociaxUIUtils.hideSoftKeyboard(ActivityUserInfo_2.this,
                        et_comment);
            }
        });
    }

    public void updateComment4Weibo(ModelWeibo weibo, int position) {
        this.listWeibo.set(position, weibo);
        adapter.notifyDataSetChanged();
    }

    /******************* 点击评论 *****************/

    /**
     * 点击更多之后的操作
     */
    public void clickMore(final int position) {
        selectWeibo = (ModelWeibo) listWeibo.get(position);
        selectpostion = position;
        PopupWindowWeiboMore popup = new PopupWindowWeiboMore(this,
                selectWeibo, selectpostion, adapterWeibo);
        final PopupWindow popupWindow = popup.getPopupWindowInstance();
        popupWindow.showAtLocation(listView, Gravity.BOTTOM, 0, 0);
        if (rl_more == null) {
            rl_more = (RelativeLayout) findViewById(R.id.rl_more);
        }
        rl_more.setVisibility(View.VISIBLE);
        rl_more.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                popupWindow.dismiss();
                rl_more.setVisibility(View.GONE);
                return true;
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        switch (id) {
            case R.id.iv_userinfo_bg:
                final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(this);
                builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            selectPicFromLocal(); // 相册更换
                        } else if (position == 1) {
                            selectPicFromCamera();// 照相更换
                        } else {
                            builder.dimss();
                        }
                    }
                });
                List<String> datas = new ArrayList<String>();
                datas.add("本地图片");
                datas.add("拍照上传");
                datas.add("取消");
                builder.create(datas);

                break;
            case R.id.tv_intro_info:
                break;
            case R.id.tv_change_info:
                //编辑基本信息
                Intent intent = new Intent(this, ActivityChangeUserInfo.class);
                startActivityForResult(intent, StaticInApp.CHANGE_USER);
                break;
            case R.id.iv_user_header:
                //点击用户头像看大图
                Intent i = new Intent(this, ActivityViewPager.class);
                i.putExtra("index", "0");
                final List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
                ModelPhoto p = new ModelPhoto();
                p.setId(0);
                //设置头像的三种尺寸
                p.setUrl(user.getFace());
                p.setMiddleUrl(user.getFace());
                p.setOriUrl(user.getFace());
                photoList.add(p);
                ActivityViewPager.imageSize = new ImageSize(header.getMeasuredWidth(), header.getMeasuredHeight());
                i.putParcelableArrayListExtra("photolist", (ArrayList<? extends Parcelable>) photoList);
                startActivity(i);
                break;
            case R.id.tv_chat:
                if (isCanSendMessage.equals("YES")) {
                    smallDialog.show();
                    TSChatManager.createSingleChat(user.getUid(), user.getUserName(), user.getFace());
                } else if (isCanSendMessage.equals("NO")) {
                    Toast.makeText(ActivityUserInfo_2.this, "您没有权限给TA发私信", Toast.LENGTH_SHORT).show();
                } else {
                    smallDialog.show();
                    mPrenster.postUserMessage(user.getUid());
                }
                break;
        }
    }

    @Override
    public void setUserHeadInfo(final ModelUser user) {
        this.user = user;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUserNameIntro();
                setUserImageInfo();
                //设置认证标识
                if (user.getUserApprove() != null
                        && user.getUserApprove().getApprove() != null) {
                    UnitSociax unit = new UnitSociax(ActivityUserInfo_2.this);
                    unit.addUserGroup(user.getUserApprove().getApprove(), ll_user_group);
                }
                setUserFollowNumber();
                setUserFollow(-1, user.isFollowed());
                loadFinish = true;
            }
        });
    }

    @Override
    public void setUserFollow(int status, boolean isFollow) {
        if (status != -1) {
            //设置按钮可点击
            tv_follow.setClickable(true);
        }

        user.setFollowed(isFollow);

        if (user.isFollowed()) {
            tv_follow.setText("已关注");
            tv_follow.setTextColor(getResources().getColor(R.color.gray));
            tv_follow.setCompoundDrawables(null, null, null, null);
        } else {
            tv_follow.setText("关注");
            tv_follow.setTextColor(getResources().getColor(R.color.title_blue));
            setLeftDrawable(drawable_following, tv_follow);
        }

        //发送通知更新其他界面用户关系状态
        Intent intent = new Intent();
        intent.putExtra("uid", user.getUid());
        intent.putExtra("follow", user.isFollowed() ? 1 : 0);
        intent.setAction(StaticInApp.NOTIFY_FOLLOW_USER);
        sendBroadcast(intent);

    }

    @Override
    public void setUserBlackList(boolean status, final boolean isBlack) {
        user.setIsInBlackList(isBlack);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isBlack) {
                    user.setFollowed(false);
                    tv_follow.setTextColor(getResources().getColor(R.color.gray));
                    tv_follow.setText("已拉黑");
                    setLeftDrawable(null, tv_follow);
                } else {
                    user.setFollowed(false);
                    Toast.makeText(getApplicationContext(), "解除成功",
                            Toast.LENGTH_SHORT).show();
                    tv_follow.setText("关注");
                    tv_follow.setTextColor(getResources().getColor(R.color.title_blue));
                    setLeftDrawable(drawable_following, tv_follow);
                }
                //恢复关注按钮可点击
                tv_follow.setEnabled(true);

            }
        });

    }

    @Override
    public void loadUserInfoComplete(ListData<SociaxItem> list) {
        if (list != null && adapterHome != null) {
            adapterHome.addHeader(list);
        }
        pullRefresh.onRefreshComplete();
    }

    @Override
    public void setUserMessagePower(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == 0) {
                    //没有发私信权限
                    isCanSendMessage = "NO";
                } else if (status == 1) {
                    isCanSendMessage = "YES";
                    TSChatManager.createSingleChat(user.getUid(),
                            user.getUserName(), user.getFace());
                } else {
                    //API请求错误或者数据解析错误
                    Toast.makeText(ActivityUserInfo_2.this, "请求发送私信权限错误,请稍后重试", Toast.LENGTH_SHORT).show();
                }
                smallDialog.dismiss();
            }
        });
    }

    @Override
    public void loadUserInfoError(String msg) {
        //加载用户信息失败
        final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(this);
        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    builder.dimss();
                }
            }
        });
        List<String> data = new ArrayList<String>();
        data.add(msg);
        data.add("确定");
        builder.create(data);
    }

    private void setUserNameIntro() {
        //设置用户名
        tvName.setText(user.getUserName());
        tv_title.setText(user.getUserName());
        if (user.getSex().equals("1") || user.getSex().equals("男")) {
            imSex.setImageResource(R.drawable.tv_user_info_man);
        } else {
            imSex.setImageResource(R.drawable.tv_user_info_woman);
        }
        if (user.getUserLevel() != null) {
            img_level.setVisibility(View.VISIBLE);
            img_level.setImageResource(UnitSociax.getResId(ActivityUserInfo_2.this, "icon_level"
                    + user.getUserLevel().getLevel(), "drawable"));
        } else {
            img_level.setVisibility(View.GONE);
        }
        //设置简介
        String intro = user.getIntro();
        if (intro == null || intro.isEmpty() || intro.equals("null") || intro.equals("暂无简介")) {
//            if (user.getUid() == Thinksns.getMy().getUid()) {
//                tv_intro_info.setText("暂无简介");
//            } else {
                tv_intro_info.setText("这家伙很懒，什么也没留下");
//            }
        } else {
            tv_intro_info.setText(user.getIntro());
        }
    }

    //设置用户头像、封面
    private void setUserImageInfo() {
        //设置用户头像
        Glide.with(Thinksns.getContext())
                .load(user.getUserface())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(ActivityUserInfo_2.this))
                .crossFade()
                .into(header);
        // 显示自定义封面
        if (!TextUtils.isEmpty(user.getCover())) {
            Glide.with(Thinksns.getContext()).load(user.getCover())
                    .crossFade()
                    .placeholder(R.drawable.bg_home8)
                    .error(R.drawable.bg_home8)
                    .into(iv_userinfo_bg);
        } else {
            //没有封面的设置系统默认封面
            application.displayDrawable(R.drawable.bg_home8, iv_userinfo_bg);
        }
    }

    //设置用户关注数、粉丝数
    private void setUserFollowNumber() {

        //显示编辑资料
        if (user.getUid() == Thinksns.getMy().getUid()) {
            ll_change_info.setVisibility(View.VISIBLE);
            //设置关注数、粉丝数
            ll_follow_info.setVisibility(View.GONE);
        } else {
            ll_change_info.setVisibility(View.GONE);
            //设置关注数、粉丝数
            ll_follow_info.setVisibility(View.VISIBLE);
            tv_followed_count.setText("关注 " + user.getFollowersCount() + " ");
            tv_follower_count.setText("粉丝 " + user.getFollowedCount() + " ");
        }
    }

    private class ActivityHandler extends Handler {
        private Context context = null;

        public ActivityHandler(Looper looper, Context context) {
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean newData = false;
            Message mainMsg = new Message();
            mainMsg.what = ResultHandler.ERROR;
            Thinksns app = (Thinksns) this.context.getApplicationContext();
            Api.Friendships friendships = app.getFriendships();
            Api.STContacts stContacts = app.getContact();
            Api.Users userApi = app.getUsers();
            Api.Message messageApi = app.getMessages();
            try {
                switch (msg.what) {
                    case ADD_FOLLOWED:
                        newData = friendships.create((ModelUser) msg.obj);
                        mainMsg.what = ResultHandler.SUCCESS;
                        mainMsg.obj = newData;
                        mainMsg.arg1 = msg.what;
                        break;
                    case DEL_FOLLOWED:
                        newData = friendships.destroy((ModelUser) msg.obj);
                        mainMsg.what = ResultHandler.SUCCESS;
                        mainMsg.obj = newData;
                        mainMsg.arg1 = msg.what;
                        break;
                    case ADD_CONTACT:
                        newData = stContacts.contacterCreate((ModelUser) msg.obj);
                        mainMsg.what = ResultHandler.SUCCESS;
                        mainMsg.obj = newData;
                        mainMsg.arg1 = msg.what;
                        break;
                    case DEL_CONTACT:
                        newData = stContacts.contacterDestroy((ModelUser) msg.obj);
                        mainMsg.what = ResultHandler.SUCCESS;
                        mainMsg.obj = newData;
                        mainMsg.arg1 = msg.what;
                        break;
                    case UPLOAD_FACE:
                        boolean result = userApi.uploadFace((Bitmap) msg.obj,
                                new File(selectImageListener.getImagePath()));

                        ModelUser iduser = Thinksns.getMy();
                        iduser.setFace(selectImageListener.getImagePath());
                        int i = app.getUserSql().updateUserFace(iduser);
                        mainMsg.what = ResultHandler.SUCCESS;
                        mainMsg.obj = result;
                        resultHandler.resultUser = iduser;
                        mainMsg.arg1 = msg.what;
                        mainMsg.arg2 = i;
                        break;
                    case ADD_BLACKLIST:
                        newData = friendships.addBlackList((ModelUser) msg.obj);
                        mainMsg.what = ResultHandler.SUCCESS;
                        mainMsg.obj = newData;
                        mainMsg.arg1 = msg.what;
                        break;
                    case DEL_BLACKLIST:
                        newData = friendships.delBlackList((ModelUser) msg.obj);
                        mainMsg.what = ResultHandler.SUCCESS;
                        mainMsg.obj = newData;
                        mainMsg.arg1 = msg.what;
                        break;
                }
            } catch (VerifyErrorException e) {
                mainMsg.obj = e.getMessage();
                mainMsg.what = ResultHandler.ERROR;
                refreshing = false;
                Log.e(TAG,
                        "ActivityHandler--VerifyErrorException"
                                + e.getMessage());
            } catch (ApiException e) {
                mainMsg.what = ResultHandler.ERROR;
                mainMsg.obj = e.getMessage();
                refreshing = false;
                Log.e(TAG, "ActivityHandler--ApiException" + e.getMessage());
            } catch (DataInvalidException e) {
                mainMsg.what = ResultHandler.ERROR;
                mainMsg.obj = e.getMessage();
                refreshing = false;
                Log.e(TAG,
                        "ActivityHandler--DataInvalidException"
                                + e.getMessage());
            }

            resultHandler.sendMessage(mainMsg);
        }
    }

    private class ResultHandler extends Handler {
        private static final int SUCCESS = 0;
        private static final int ERROR = 1;

        private ModelUser resultUser = null;

        @SuppressLint("NewApi")
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            String info = "";
            if (msg.what == FLOAT_HEADER_VIEW) {
                if (msg.arg1 == FLOAT_VISIBILTY) {
                    ll_title.setBackgroundColor(getResources().getColor(R.color.bg_ios));
//                    ll_title.setBackgroundColor(Color.WHITE);
                    img_back.setImageDrawable(getResources().getDrawable(R.drawable.img_back));
                    img_right.setImageDrawable(getResources().getDrawable(R.drawable.ico_more_blue));
                    tv_title.setVisibility(View.VISIBLE);
                    titleBottomLine.setVisibility(View.VISIBLE);
                    if (tabView.getParent() != ll_tab_title) {
                        ll_tabs.removeView(tabView);
                        ll_tab_title.addView(tabView);
                    }
                } else {
                    ll_title.setBackground(getResources().getDrawable(R.drawable.ic_black_top));
                    img_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_white));
                    img_right.setImageDrawable(getResources().getDrawable(R.drawable.ico_more_white));
                    tv_title.setVisibility(View.GONE);
                    titleBottomLine.setVisibility(View.GONE);
                    if (tabView.getParent() != ll_tabs) {
                        ll_tab_title.removeView(tabView);
                        ll_tabs.addView(tabView);
                    }
                }
            } else if (msg.what == SELETE) {
                setRadioButtonBackGround(selected);
                setListViewContent();
                return;
            } else if (msg.what == SUCCESS) {
                switch (msg.arg1) {
                    case ADD_BLACKLIST:
                        boolean result2 = (Boolean) msg.obj;
                        if (!result2) {
                            ActivityUserInfo_2.this.user.setIsInBlackList(true);
                            Toast.makeText(getApplicationContext(), "添加成功",
                                    Toast.LENGTH_SHORT).show();
//                            tv_follow.setTag(ThinksnsUserInfo.FollowedStatus.NO);
                            // tv_follow
                            // .setBackgroundResource(R.drawable.tv_user_info_follow);
                            tv_follow.setTextColor(getResources().getColor(R.color.gray));
                            tv_follow.setText("已在黑名单");
                            setLeftDrawable(null, tv_follow);
                        } else {
                            Toast.makeText(getApplicationContext(), "操作失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case DEL_BLACKLIST:
                        boolean result3 = (Boolean) msg.obj;
                        if (!result3) {
                            ActivityUserInfo_2.this.user.setIsInBlackList(false);
                            Toast.makeText(getApplicationContext(), "解除成功",
                                    Toast.LENGTH_SHORT).show();
//                            tv_follow.setTag(ThinksnsUserInfo.FollowedStatus.NO);
                            // tv_follow
                            tv_follow.setText(" 关注");
                            tv_follow.setTextColor(getResources().getColor(R.color.title_blue));
                            setLeftDrawable(drawable_following, tv_follow);
                        } else {
                            Toast.makeText(getApplicationContext(), "操作失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            } else {// 错误
                info = (String) msg.obj;
                hideProgressDialog();
                if (info.equals("您没有权限进入TA的个人主页")) {

                    dialog = builder.show();
                    Window window = dialog.getWindow();
                    ColorDrawable colorDrawable = new ColorDrawable(0);
                    window.setBackgroundDrawable(colorDrawable);

                    btn_no_authority.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityUserInfo_2.this.finish();
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(ActivityUserInfo_2.this, info, Toast.LENGTH_SHORT).show();
                }

                tv_follow.setClickable(false);
            }

        }
    }

    /*******************
     * 点击更多
     *****************/

    class UIImgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == StaticInApp.UPLOAD_PIC) {
                try {

                    JSONObject object = new JSONObject(msg.obj.toString());
                    String status = object.getString("status");
                    String image = object.getString("image");
                    //更新成功
                    if (status.equals("1") && image != null) {
//                        Toast.makeText(ActivityUserInfo_2.this, "更换成功", Toast.LENGTH_SHORT).show();
                        Thinksns application = (Thinksns) ActivityUserInfo_2.this.getApplicationContext();
                        application.displayImage(image, iv_userinfo_bg);
                    }
                    //更新失败
                    else {
                        Toast.makeText(ActivityUserInfo_2.this, "更换失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                smallDialog.dismiss();
            }
        }
    }

    /**
     * 照片来源
     */
    class headImageChangeListener implements DialogInterface.OnClickListener {
        private String imagePath = "";

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    cameraImage();
                    break;
                case 1:
                    locationImage();
                    break;
                default:
                    dialog.dismiss();
            }
        }

        private void locationImage() {
            Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
            getImage.addCategory(Intent.CATEGORY_OPENABLE);
            getImage.setType("image/*");
            startActivityForResult(Intent.createChooser(getImage, "选择照片"), 1);

        }

        // 获取相机拍摄图片
        private void cameraImage() {
            if (!ImageUtil.isHasSdcard()) {
                // Toast.makeText(this.ThinksnsCreate,"" ,T );//.show();
                Toast.makeText(ActivityUserInfo_2.this, "请检查存储卡",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (changeListener == null)
                changeListener = new headImageChangeListener();
            // 启动相机
            Intent myIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            String picName = System.currentTimeMillis() + ".jpg";
            try {
                String path = ImageUtil.saveFilePaht(picName);
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                changeListener.setImagePath(path);
                myIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "file saving...");
            }
            startActivityForResult(myIntent, 0);
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }

    @SuppressLint("HandlerLeak")
    public class ListHandler extends Handler {

        public ListHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object digMsg[] = null;
            if (msg.obj instanceof Object[]) {
                digMsg = (Object[]) msg.obj;
            }
            Thinksns app = (Thinksns) ActivityUserInfo_2.this
                    .getApplicationContext();
            switch (msg.what) {
                // case AppConstant.ADD_DIG:
                // app.getWeiboSql().updateDigg(msg.arg2, msg.arg1);
                // app.getAtMeSql().updateDigg(msg.arg2, msg.arg1);
                // app.getFavoritWeiboSql().updateDigg(msg.arg2, msg.arg1);
                // updateList4Dig((Integer) digMsg[2]);
                // break;
                // case AppConstant.DEL_DIG:
                // app.getWeiboSql().updateDigg(msg.arg2, msg.arg1);
                // app.getAtMeSql().updateDigg(msg.arg2, msg.arg1);
                // app.getFavoritWeiboSql().updateDigg(msg.arg2, msg.arg1);
                // delList4Dig((Integer) digMsg[2]);
                // break;
                case AppConstant.COMMENT:
                    int result = msg.arg1;
                    if (result == 1) {
                        Toast.makeText(ActivityUserInfo_2.this, "评论成功",
                                Toast.LENGTH_SHORT).show();
                        Object obj[] = (Object[]) msg.obj;
                        ModelWeibo weibo = (ModelWeibo) obj[0];
                        RelativeLayout rl_comment = (RelativeLayout) obj[1];
                        EditText et_comment = (EditText) obj[2];
                        int position = (Integer) obj[3];
                        updateComment4Weibo(weibo, position);
                        rl_comment.setVisibility(View.GONE);
                        SociaxUIUtils.hideSoftKeyboard(ActivityUserInfo_2.this,
                                et_comment);
                    } else {
                        Toast.makeText(ActivityUserInfo_2.this, "评论失败",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                // case AppConstant.GETWEIBO:
                // if (msg.arg1 == 1) {
                // Weibo transWeibo = (Weibo) msg.obj;
                // updateTranspondWeibo(transWeibo);
                // }
                // break;
            }
        }
    }

    @Override
    public View getDefaultView() {
//        return findViewById(R.id.default_personal_share_bg);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.default_personal_share_bg, null);
        return emptyView;
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        super.onDestroy();
    }
}