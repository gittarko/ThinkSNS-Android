package com.thinksns.sociax.t4.android.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.*;
import com.thinksns.sociax.t4.android.interfaces.OnTabListener;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.ScrollViewSociax;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.unit.TabUtils;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.tschat.widget.UIImageLoader;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by hedong on 16/2/25.
 * 用户个人主页
 */
public class ActivityUserHome extends ThinksnsAbscractActivity implements View.OnClickListener{
    private final int SELETE_HOME = 0;
    private final int SELETE_WEIBO = 1;
    private final int SELETE_ALBUM = 2;

    //标题栏UI控件
    private RelativeLayout ll_title;
    private ImageView iv_back;
    private TextView tv_title;
    private View titleBottomLine;
    private ImageView img_more;

    //用户头信息UI控件
    private ImageView iv_userinfo_bg;       // 自定义背景
    private TextView tvName;                //用户昵称
    private ImageView imSex;                //用户性别
    private ImageView img_level;            //用户等级
    private ImageView header;               //用户头像
    private LinearLayout ll_user_group;     //用户认证标识
    private TextView tv_followed_count,     //用户关注数
                tv_follower_count;          //用户粉丝数
    private LinearLayout ll_change_info;   //编辑资料
    private TextView tv_change_info,        //编辑资料文本
            tv_removebacklist,              //拉入黑名单
            tv_intro_info;                  //用户简介

    //选项卡
    private RadioGroup rg_userinfo;
    private RadioButton rb_weibo, rb_home, rb_album, rb_gift;
    // Tab栏工具
    private TabUtils mTabUtils;

    private RelativeLayout rl_weibo, rl_home, rl_album, rl_gift;
    private ViewPager vp;
    private AdapterViewPager adapter_Home;

    //底部关注、聊天
    private TextView  tv_follow,                  //关注
                    tv_sendMessage;            //聊天
    private LinearLayout ll_bottom;

    private ProgressDialog prDialog;
    private ScrollViewSociax svSociax;
    private ModelUser user = new ModelUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        initIntent();
        initView();
        initListener();
        initData();
        initFragments();
    }

    private void initIntent() {
        int uid = getIntent().getIntExtra("uid", -1);
        String name = getIntentData().getString("uname");
        if(name != null) {
            if(name.equals(Thinksns.getMy().getUserName())) {
                user = null;
                user = Thinksns.getMy();
            }else {
                user.setUserName(name);
            }
        }else if(uid == -1 || uid == Thinksns.getMy().getUid()){
            user = null;
            user = Thinksns.getMy();
        }else {
            user.setUid(uid);
        }
    }

    private void initData() {
        if(user.getUid() == Thinksns.getMy().getUid()) {
            setUerInfoData(user);
        }
    }

    //初始化视图
    private void initView() {
        svSociax = (ScrollViewSociax)findViewById(R.id.scrollView);
        //标题栏控件初始化
        iv_back = (ImageView)findViewById(R.id.iv_back);
        tv_title = (TextView)findViewById(R.id.tv_title);
        img_more = (ImageView) findViewById(R.id.img_more);
        ll_title = (RelativeLayout) findViewById(R.id.ll_title);
        titleBottomLine = findViewById(R.id.title_bottom_line);

        //个人封面
        iv_userinfo_bg = (ImageView) findViewById(R.id.iv_userinfo_bg);
        iv_userinfo_bg.setOnClickListener(this);
        //相册
        rb_album = (RadioButton) findViewById(R.id.rb_album);
        //主页
        rb_home = (RadioButton) findViewById(R.id.rb_home);
        //分享
        rb_weibo = (RadioButton) findViewById(R.id.rb_weibo);
        //编辑资料
        tv_change_info = (TextView) findViewById(R.id.tv_change_info);
        ll_change_info = (LinearLayout)findViewById(R.id.ll_change_info);
        //用户头像
        header = (ImageView) findViewById(R.id.iv_user_header);
        header.setOnClickListener(this);
        //用户昵称
        tvName = (TextView) findViewById(R.id.tv_user_name);
        //性别
        imSex = (ImageView) findViewById(R.id.im_sex);
        //等级
        img_level = (ImageView) findViewById(R.id.img_level);
        //关注
        tv_followed_count = (TextView) findViewById(R.id.tv_followed_count);
        //粉丝
        tv_follower_count = (TextView) findViewById(R.id.tv_follower_count);
        //简介
        tv_intro_info = (TextView) findViewById(R.id.tv_intro_info);
        tv_intro_info.setOnClickListener(this);
        ll_user_group = (LinearLayout) findViewById(R.id.ll_uname_adn);

        //底部操作栏
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        //加关注
        tv_follow = (TextView) findViewById(R.id.tv_follow);
        //发私信
        tv_sendMessage = (TextView) findViewById(R.id.tv_chat);

        vp = (ViewPager) findViewById(R.id.vp_home);
        rg_userinfo = (RadioGroup)findViewById(R.id.rg_userinfo);

    }

    public ScrollViewSociax getScrollView() {
        return svSociax;
    }

    public ViewPager getViewPager() {
        return vp;
    }

    //初始化监听事件
    private void initListener() {
        iv_back.setOnClickListener(this);
    }

    private void initFragments() {
        // 初始化适配器
        adapter_Home = new AdapterViewPager(getSupportFragmentManager());
        // 初始化Tab
        mTabUtils = new TabUtils();
        Fragment userInfo = new com.thinksns.sociax.t4.android.user.FragmentUserInfo();
        Bundle bundle = new Bundle();
        bundle.putInt("userId", user.getUid());
        bundle.putString("userName", user.getUserName());
        userInfo.setArguments(bundle);

        //设置分享是否自动刷新
        Bundle bundle1 = new Bundle();
        if(user.getUid() == Thinksns.getMy().getUid()) {
            bundle1.putBoolean("refresh", true);
        }
        Fragment weibo = new FragmentWeiboListViewFriends();
        weibo.setArguments(bundle1);

        mTabUtils.addFragments(userInfo, weibo
                ,new FragmentWeiboListViewChannel()
        );

        mTabUtils.addButtons(rg_userinfo);
        mTabUtils.setButtonOnClickListener(tabOnClickListener);

        initViewPager();

    }

    private void initViewPager() {
        // 绑定adapter
        adapter_Home.bindData(mTabUtils.getFragments());
        // 设置viewPager
        vp.setOffscreenPageLimit(mTabUtils.getFragments().size() - 1);
        vp.setAdapter(adapter_Home);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                mTabUtils.setDefaultUI(ActivityUserHome.this, index);
                if(mTabUtils.getFragments().get(index) instanceof OnTabListener) {
                    ((OnTabListener)mTabUtils.getFragments().get(index)).onTabClickListener();
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int index, float arg1, int arg2) {
            }
        });
    }

    //设置用户头部信息
    public void setUerInfoData(final ModelUser user) {
        this.user = null;
        this.user = user;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //设置用户名
                tvName.setText(user.getUserName());
                if (user.getSex().equals("1") || user.getSex().equals("男")) {
                    imSex.setImageResource(R.drawable.tv_user_info_man);
                } else {
                    imSex.setImageResource(R.drawable.tv_user_info_woman);
                }
                if (user.getUserLevel() != null) {
                    img_level.setVisibility(View.VISIBLE);
                    img_level.setImageResource(UnitSociax.getResId(ActivityUserHome.this, "icon_level"
                            + user.getUserLevel().getLevel(), "drawable"));
                } else {
                    img_level.setVisibility(View.GONE);
                }

                if (user.getUserApprove() != null
                        && user.getUserApprove().getApprove() != null) {
                    UnitSociax unit = new UnitSociax(ActivityUserHome.this);
                    unit.addUserGroup(user.getUserApprove().getApprove(), ll_user_group);
                }

                //设置用户头像
                Glide.with(Thinksns.getContext())
                        .load(user.getUserface())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new GlideCircleTransform(ActivityUserHome.this))
                        .crossFade()
                        .into(header);

                // 显示自定义封面
                if (user.getCover() != null && !user.getCover().equals("") && !user.getCover().equals("false")) {
                    UIImageLoader.getInstance(ActivityUserHome.this).displayImage(user.getCover(), iv_userinfo_bg);
                } else {
                    ((Thinksns)ActivityUserHome.this.getApplicationContext()).displayDrawable(R.drawable.bg_home8, iv_userinfo_bg);
                }

                //设置关注数、粉丝数
                tv_followed_count.setText("关注 " + user.getFollowersCount() + " ");
                tv_follower_count.setText("粉丝 " + user.getFollowedCount() + " ");
                //设置简介
                if (user.getIntro() == null || user.getIntro().isEmpty())
                    tv_intro_info.setText("这家伙很懒，什么也没留下");
                else {
                    tv_intro_info.setText(user.getIntro());
                }

                //显示编辑资料
                if(user.getUid() == Thinksns.getMy().getUid()) {
                    ll_change_info.setVisibility(View.VISIBLE);
                    ll_bottom.setVisibility(View.GONE);
                }else {
                    ll_change_info.setVisibility(View.GONE);
                    ll_bottom.setVisibility(View.VISIBLE);
                }

                if (user.isFollowed()) {
//                    tv_follow.setTag(ThinksnsUserInfo.FollowedStatus.YES);
                    tv_follow.setText("已关注");
                    tv_follow.setTextColor(getResources().getColor(R.color.gray));
                    setLeftDrawable(null, tv_follow);
                } else {
//                    tv_follow.setTag(ThinksnsUserInfo.FollowedStatus.NO);
                    tv_follow.setText("关注");
                    //加关注图标
                    Drawable drawable_following = ActivityUserHome.this.getResources().getDrawable(R.drawable.ic_fllow);
                    setLeftDrawable(drawable_following, tv_follow);
                }

                if (user.getIsInBlackList()) {
                    tv_follow.setText("已在黑名单");
                    tv_follow.setTextColor(getResources().getColor(R.color.gray));
                    setLeftDrawable(null, tv_follow);
                }

            }
        });
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

    private void setSelect(int index) {
        setRadioButtonBackGround(index);
        vp.setCurrentItem(index);
    }

    private void setRadioButtonBackGround(int selete) {
        rb_album.setChecked(false);
        rb_home.setChecked(false);
        rb_weibo.setChecked(false);

        rb_home.setTextSize(14);
        rb_weibo.setTextSize(14);
        rb_album.setTextSize(14);

        rb_album.setTextColor(getResources().getColor(R.color.title_black));
        rb_weibo.setTextColor(getResources().getColor(R.color.title_black));
        rb_home.setTextColor(getResources().getColor(R.color.title_black));

        rb_home.setBackground(null);
        rb_weibo.setBackground(null);
        rb_album.setBackground(null);

        switch (selete) {
            case SELETE_HOME:
                rb_home.setChecked(true);
                rb_home.setTextColor(getResources().getColor(R.color.title_blue));
                rb_home.setBackgroundResource(R.drawable.bottom_border_blue);
                break;

            case SELETE_WEIBO:
                rb_weibo.setChecked(true);
                rb_weibo.setTextColor(getResources().getColor(
                        R.color.title_blue));
                rb_weibo.setBackgroundResource(R.drawable.bottom_border_blue);
                break;
            case SELETE_ALBUM:
                rb_album.setChecked(true);
                rb_album.setTextColor(getResources().getColor(
                        R.color.title_blue));
                rb_album.setBackgroundResource(R.drawable.bottom_border_blue);

                break;
        }
    }

    /**
     * Tab的单击事件
     */
    private final View.OnClickListener tabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (Integer)v.getTag();
            setSelect(index);
//            vp.setCurrentItem(index);
//            if(mTabUtils.getFragments().get(index) instanceof OnTabListener) {
//                ((OnTabListener)mTabUtils.getFragments().get(index)).onTabClickListener();
//            }
        }
    };

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_home;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(user.getUid() == Thinksns.getMy().getUid()) {
            setSelect(1);
        }else {
            setSelect(0);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_userinfo_bg:
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_user_header:
                break;
        }
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
     * 点击更多内的照相图标
     */
    File cameraFile;
    private void selectPicFromCamera() {
        if (!UnitSociax.isExitsSdcard()) {
            Toast.makeText(this.getApplicationContext(),
                    "SD卡不存在，不能拍照", 0).show();
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

}
