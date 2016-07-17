package com.thinksns.sociax.t4.android.weibo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.ApiStatuses;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.modle.RecentTopic;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.db.SQLHelperWeiboDraft;
import com.thinksns.sociax.t4.android.img.Bimp;
import com.thinksns.sociax.t4.android.img.PhotoActivity;
import com.thinksns.sociax.t4.android.map.ActivityGetMyLocation;
import com.thinksns.sociax.t4.android.popupwindow.PopUpWindowAlertDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowLocation;
import com.thinksns.sociax.t4.android.temp.GetLocalImagePath;
import com.thinksns.sociax.t4.android.temp.SelectImageListener;
import com.thinksns.sociax.t4.android.topic.AtTopicActivity;
import com.thinksns.sociax.t4.android.user.ActivityAtUserSelect;
import com.thinksns.sociax.t4.android.video.ActivityVideoDetail;
import com.thinksns.sociax.t4.android.video.MediaRecorderActivity;
import com.thinksns.sociax.t4.android.video.ToastUtils;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.service.ServiceUploadWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.thinksnsbase.utils.FormFile;
import com.thinksns.sociax.thinksnsbase.utils.WordCount;
import com.thinksns.sociax.unit.Compress;
import com.thinksns.sociax.unit.SociaxUIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 微博发布/帖子发布
 *     1.来自草稿箱的微博：
 *     参数：int type=StaticInApp.WEIBO_DRAFT,
 *           ModelDraft draft
 *      2.来自照片、视频、文字的新建微博
 *         AppConstant.TRANSPOND
 *         、AppConstant.CREATE_WEIBO、AppConstant.CREATE_IMAGE_WEIBO
 *         、AppConstant.CREATE_VIDEO_WEIBO:
 *    3.来自频道的微博，传入String channel_id
 */
public class ActivityCreateWeibo extends ThinksnsAbscractActivity implements OnClickListener,
        PopupWindowLocation.OnLocationClickListener {

    private static final String TAG = ActivityCreateWeibo.class.getSimpleName();
    // 发布微博/帖子/频道后台工作服务
    private ServiceUploadWeibo uploadService;
    private WordCount mWordCount;

    private TextView tv_cancel, tv_submit,
            tv_get_my_location;  // cancel:取消（返回） submit:提交（发布） 地理位置
    private EditText et_content; //文本框
    private ImageView img_camera, img_video, img_at,
            img_topic, img_face,
            img_fill_1, img_fill_2;     // 相机、录像、@、主题、表情、填充布局1、填充布局2
    private WeiboCreateHandler mHandler;

    private int type = AppConstant.CREATE_TEXT_WEIBO;       //发布类型:默认是文本微博
    private ModelWeibo weibo;
    private String feed_id;  //转发或来自某个频道的所在ID
    private String FLAG = null;

    private LinearLayout ll_content;        // 内容区域
    private LoadingView loadingView;        // 加载动画
    private int position;
    private static Worker thread;

    private ImageView preview;
    private FrameLayout videoPreview;

    private GridView noScrollgridview;
    private SelectedImgGridAdapter adapter;
    private HorizontalScrollView imageHs;
    private PopupWindowLocation popupWindowLocation;

    // private static EditText edit;
    private ListFaceView tFaceView;
    private static final int AT_REQUEST_CODE = 3;
    private static final int TOPIC_REQUEST_CODE = 4;
    private static final int GET_LOCATION = 5;

    private SelectImageListener listener_selectImage;
    Intent intent;

    public static String staticFrom;
    public static Long staticTime;
    public static String staticVideoPath = "";

    private static CreateHandler handler;
    public static FormFile[] imageList;

    private double latitude = 0.0f, longitude = 0.0f;

    /**
     * 包含图片标识
     */
    private static final int HAS_IMAGE = 0x00000001;
    /**
     * 包含视频标识符
     */
    public static final int HAS_VIDEO = 0x00000002;
    /**
     * 正在创建的微博的标识符,用来判断是否包含图片或视频
     */
    public static int MEDIA_TAG = 0x00000000;
    private int TRANSPANT = 0;

    /**
     * 来自草稿箱的内容
     */
    public ModelDraft md_draft;
    /**
     * 用于判断当前编辑的微博是否已经可以被放弃
     */
    boolean isFinishCurrentWeibo = false;
    private boolean isOriginal = false;     //是否发送原图

    //动态视图
    private ViewStub stub_post_title;       //帖子标题视图
    private EditText edit_post_title;       //帖子标题
    private int weiba_id;
    private ModelDraft modelDraft = null;

    @Override
    public String getTitleCenter() {
        return "";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(this,"取消", "发布");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        initView();         // 初始化view
        initIntentData();   // 初始化intent数据
        initPicviews();     // 图片预览
        setBottomClick();   // 设置点击事件
        initData(false);         // 初始化数据
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            List<String> list = intent.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            boolean original = intent.getBooleanExtra(MultiImageSelectorActivity.EXTRA_SELECT_ORIGIANL, false);
            if (Bimp.address.size() < 9) {
                for (String addr : list) {
                    if (!Bimp.address.contains(addr)) {
                        Bimp.address.add(addr);
                    }
                }
            }

            Bimp.isOriginal = original;
        }
    }

    /**
     * 设置正在创建的微博的标识符,确定是否包含图片或视频
     * 图片和视频标识符为互斥变量,不能共存
     * 此处通过直接赋值标识符来进行互斥
     *
     * @param tag {@value HAS_VIDEO}\{@value HAS_IMAGE}
     */
    public void setMedia(int tag) {
        MEDIA_TAG = tag;
        changeForStatus(MEDIA_TAG);
    }

    /**
     * 根据当前微博状态显示或隐藏控件
     * @param tag 当前微博的状态
     */
    private void changeForStatus(int tag) {
        int isCameraShow = View.VISIBLE;
        int isVideoShow = View.VISIBLE;
        if ((tag & HAS_IMAGE) != 0) {
            isVideoShow = View.GONE;
        } else if ((tag & HAS_VIDEO) != 0) {
            isCameraShow = View.GONE;
        }
        if (img_camera != null && img_camera.getVisibility() == View.VISIBLE) {
            img_camera.setVisibility(isCameraShow);
        }
        if (img_video != null && img_video.getVisibility() == View.VISIBLE) {
            img_video.setVisibility(isVideoShow);
        }
    }

    private void initIntentData() {
        intent = getIntent();
        type = intent.getIntExtra("type", AppConstant.CREATE_TEXT_WEIBO);
        switch (type) {
            case AppConstant.CREATE_VIDEO_WEIBO:
                break;
            case AppConstant.CREATE_IMAGE_WEIBO:
                break;
            case AppConstant.CREATE_WEIBA_POST:
                feed_id = String.valueOf(getIntent().getIntExtra("weiba_id", 0));
                break;
            case AppConstant.CREATE_TRANSPORT_WEIBO:
            case AppConstant.CREATE_TRANSPORT_POST:
                feed_id = String.valueOf(getIntent().getIntExtra("feed_id", 0));
                break;
            case AppConstant.WEIBO_EDIT_TEXT_DRAFT:
            case AppConstant.WEIBO_EDIT_IMAGE_DRAFT:
            case AppConstant.WEIBO_EDIT_VIDEO_DRAFT:
            case AppConstant.WEIBO_EDIT_TRANSPORT_DRAFT:
                md_draft = (ModelDraft) intent.getSerializableExtra("draft");
                setDraftContent();
                break;
            case AppConstant.CREATE_CHANNEL_WEIBO:
                this.feed_id = String.valueOf(getIntent().getIntExtra("channel_id", -1));
                break;

        }
        if (intent.hasExtra("draft")) {
            // 来自草稿箱,
            type = StaticInApp.WEIBO_EDIT_DRAFT;

        } else {
            weibo = (ModelWeibo) intent.getSerializableExtra("weibo");
            position = intent.getIntExtra("position", -1);
            if (getIntent().hasExtra("channel_id")) {

                this.FLAG = getIntent().getStringExtra("FLAG");
            }
        }
    }

    public void setDraftContent() {
        if (md_draft != null) {
            if (md_draft.isHasVideo()) {
                setMedia(HAS_VIDEO);
                staticVideoPath = md_draft.getVideoPath();
                if(TextUtils.isEmpty(staticVideoPath)) {
                    //改变草稿类型
                }
//                Bimp.address.addAll(md_draft.getImageList());
            } else if (md_draft.isHasImage()) {
                setMedia(HAS_IMAGE);
                Bimp.address.addAll(md_draft.getImageList());
                staticVideoPath = md_draft.getVideoPath() + "";
            }
//            if (md_draft.getChannel_id() != null) {
//                this.feed_id = md_draft.getChannel_id();
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetVideo();
        if (et_content != null && et_content.getVisibility() == View.VISIBLE) {

            et_content.setFocusable(true);
            et_content.setFocusableInTouchMode(true);
            et_content.requestFocus();
            et_content.requestFocusFromTouch();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager m = (InputMethodManager)
                            et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    SociaxUIUtils.showSoftKeyborad(ActivityCreateWeibo.this, et_content);
                }
            }, 200);
        }
    }

    @Override
    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_weibo_create;
    }

    //插入一条话题
    @Subscribe
    public void insertIntoTopic(RecentTopic topic) {
        if (topic == null)
            return;
        String topic_content = topic.getName().trim();
        if (topic_content.length() + et_content.length() > 140) {
            Toast.makeText(getApplicationContext(),
                    "已超过最大输入长度，请重新加入话题", Toast.LENGTH_SHORT).show();
        } else {
            et_content.getText().append("#" + topic_content + "#");
            et_content.setSelection(et_content.length());
        }
    }

    /**
     * 重置video
     */
    private void resetVideo() {
        if (!TextUtils.isEmpty(staticVideoPath)) {
            preview.setImageBitmap(getVideoThumbnail(staticVideoPath, 260, 260,
                    MediaStore.Images.Thumbnails.FULL_SCREEN_KIND));

            type = AppConstant.CREATE_VIDEO_WEIBO;
            videoPreview.setVisibility(View.VISIBLE);
            //不显示图片
            imageHs.setVisibility(View.GONE);
            img_camera.setVisibility(View.GONE);
            img_video.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SociaxUIUtils.hideSoftKeyboard(this, et_content);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            uploadService = ((ServiceUploadWeibo.LocalBinder) service)
                    .getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            uploadService = null;
        }
    };

    private void initView() {
        // new
        mHandler = new WeiboCreateHandler();
        tv_cancel = (TextView) findViewById(R.id.tv_create_cancel);
        tv_submit = (TextView) findViewById(R.id.tv_create_submit);
        //地理位置
        tv_get_my_location = (TextView) findViewById(R.id.tv_get_my_location);
        tv_get_my_location.setOnClickListener(this);

        //视频预览图标
        preview = (ImageView) findViewById(R.id.iv_video_pre);
        videoPreview = (FrameLayout) findViewById(R.id.fl_video_pre);
        //帖子标题
        stub_post_title = (ViewStub) findViewById(R.id.viewstub_post_title);
        //文本内容框
        et_content = (EditText) findViewById(R.id.et_send_content);
        //拍照
        img_camera = (ImageView) findViewById(R.id.img_camera);
        //视频
        img_video = (ImageView) findViewById(R.id.img_video);
        //@
        img_at = (ImageView) findViewById(R.id.img_at);
        //话题
        img_topic = (ImageView) findViewById(R.id.img_topic);
        //表情
        img_face = (ImageView) findViewById(R.id.img_face);
        loadingView = (LoadingView) findViewById(LoadingView.ID);
        //表情容器
        tFaceView = (ListFaceView) findViewById(R.id.face_view);

        this.setInputLimit();

        if (listener_selectImage == null)
            listener_selectImage = new SelectImageListener(this);

        tFaceView.setFaceAdapter(mFaceAdapter);

        popupWindowLocation = new PopupWindowLocation(this, findViewById(android.R.id.content));
        popupWindowLocation.setListener(this);

//        setDraftContent();
    }

    private void initPicviews() {
        imageHs = (HorizontalScrollView) findViewById(R.id.imageHoriScroll);
        noScrollgridview = (GridView) findViewById(R.id.gv_preview);
        adapter = new SelectedImgGridAdapter(this, noScrollgridview);
        noScrollgridview.setAdapter(adapter);
        adapter.update();

        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                SociaxUIUtils.hideSoftKeyboard(ActivityCreateWeibo.this,
                        et_content);
                if (arg2 == Bimp.bmp.size()) {
                    //选择图片
                    SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                    showSelectImagePopUpWindow(view);
                } else {
                    Intent intent = new Intent(ActivityCreateWeibo.this,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivityForResult(intent, StaticInApp.UPLOAD_WEIBO);
                }
            }
        });
    }

    private void setBottomClick() {
        // new
        tv_cancel.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        img_video.setOnClickListener(this);
        img_at.setOnClickListener(this);
        img_topic.setOnClickListener(this);
        img_face.setOnClickListener(this);
        et_content.setOnClickListener(this);
        et_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tFaceView.setVisibility(View.GONE);
                img_face.setImageResource(R.drawable.face_bar);
                return false;
            }
        });
        videoPreview.setOnClickListener(this);

    }

    /**
     * 初始化微博发布，根据进入的type显示执行下一步内容
     */
    private void initData(boolean hideOrShow) {
        switch (type) {
            case AppConstant.COMMENT:
                et_content.requestFocus();
                img_camera.setVisibility(View.GONE);
                img_video.setVisibility(View.GONE);
                imageHs.setVisibility(View.GONE);
                break;
            case AppConstant.CREATE_TRANSPORT_POST:
            case AppConstant.CREATE_TRANSPORT_WEIBO:
                et_content.requestFocus();
                img_camera.setVisibility(View.GONE);
                img_video.setVisibility(View.GONE);
                imageHs.setVisibility(View.GONE);

                if(type == AppConstant.CREATE_TRANSPORT_WEIBO) {
                    et_content.setHint("转发分享");
                }else if(type == AppConstant.CREATE_TRANSPORT_POST) {
                    et_content.setHint("转发帖子");
                    img_topic.setVisibility(View.GONE);
                    tv_get_my_location.setVisibility(View.GONE);
                }

                et_content.setHintTextColor(this.getResources().getColor(R.color.edit_hint));
                String contentStr = et_content.getText().toString().trim();
                if (contentStr != null) {
                    et_content.setSelection(contentStr.length());
                }

                //设置标题为转发
                ((TextView)getRightView()).setText("转发");
                TRANSPANT = 1;
                break;
            case AppConstant.CREATE_TOPIC_WEIBO:
                if (getIntent().hasExtra("topic")) {
                    et_content.append("#" + getIntent().getStringExtra("topic") + "#");
                    et_content.setSelection(et_content.getText().toString().length());
                }
                et_content.requestFocus();
                break;
            case AppConstant.CREATE_TEXT_WEIBO:
                break;
            case AppConstant.CREATE_ALBUM_WEIBO:
                //从相册选择
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                selectPhoto();
//                et_content.requestFocus();
                break;
            case AppConstant.CREATE_VIDEO_WEIBO:
                recordVideo();
//                et_content.requestFocus();
                break;
            case AppConstant.WEIBO_EDIT_TEXT_DRAFT:
            case AppConstant.WEIBO_EDIT_IMAGE_DRAFT:
            case AppConstant.WEIBO_EDIT_VIDEO_DRAFT:
            case AppConstant.WEIBO_EDIT_TRANSPORT_DRAFT:
            case AppConstant.POST_EDIT_TRANSPORT_DRAFT:
                et_content.setText(md_draft.getContent());
                et_content.setSelection(et_content.getText().toString().trim().length());
                et_content.requestFocus();
                //含图片
                if (type == AppConstant.WEIBO_EDIT_IMAGE_DRAFT) {
                    img_video.setVisibility(View.GONE);
                }
                //含视频
                else if (type == AppConstant.WEIBO_EDIT_VIDEO_DRAFT) {
                    imageHs.setVisibility(View.GONE);
                    img_camera.setVisibility(View.GONE);
                    resetVideo();
                }else if(type == AppConstant.POST_EDIT_TRANSPORT_DRAFT) {
                    img_video.setVisibility(View.GONE);
                    imageHs.setVisibility(View.GONE);
                    img_camera.setVisibility(View.GONE);
                    tv_get_my_location.setVisibility(View.GONE);
                }
                break;
            case AppConstant.CREATE_WEIBA_POST:
                //创建微吧帖子
                img_video.setVisibility(View.GONE);
                img_at.setVisibility(View.GONE);
                img_topic.setVisibility(View.GONE);
                tv_get_my_location.setVisibility(View.GONE);
                //加载帖子编辑标题
                inflatePostTitle();
                et_content.requestFocus();
                break;
            default:
                break;
        }
    }

    //选择相册
    private void selectPhoto() {
        Intent getImage = new Intent(ActivityCreateWeibo.this, MultiImageSelectorActivity.class);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
                new ArrayList<String>());
        startActivityForResult(getImage, StaticInApp.LOCAL_IMAGE);
    }

    //录制视频
    private void recordVideo() {
        SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
        //跳转视频录制
        Intent intentVideo = new Intent(ActivityCreateWeibo.this, MediaRecorderActivity.class);
        startActivity(intentVideo);
        type = AppConstant.CREATE_VIDEO_WEIBO;
        Anim.in(ActivityCreateWeibo.this);
    }

    //加载帖子标题视图
    private void inflatePostTitle() {
        try {
            stub_post_title.inflate();
            edit_post_title = (EditText) findViewById(R.id.et_post_title);
            edit_post_title.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                    return false;
                }
            });
        } catch (Exception e) {

        }

        stub_post_title.setVisibility(View.VISIBLE);
    }

    private ListFaceView.FaceAdapter mFaceAdapter = new ListFaceView.FaceAdapter() {

        @Override
        public void doAction(int paramInt, String paramString) {
            if (edit_post_title != null) {
                View rootView = ActivityCreateWeibo.this.getWindow().getDecorView();
                View focusView = rootView.findFocus();
                if (focusView.getId() == edit_post_title.getId()) {
                    Toast.makeText(ActivityCreateWeibo.this, "标题不支持添加表情", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            EditText localEditDiggView = et_content;
            int i = localEditDiggView.getSelectionStart();
            int j = localEditDiggView.getSelectionStart();
            String str1 = "[" + paramString + "]";
            String str2 = localEditDiggView.getText().toString();
            SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
            localSpannableStringBuilder.append(str2, 0, i);
            localSpannableStringBuilder.append(str1);
            localSpannableStringBuilder.append(str2, j, str2.length());

            UnitSociax.showContentFaceView(ActivityCreateWeibo.this,
                    localSpannableStringBuilder);
            localEditDiggView.setText(localSpannableStringBuilder,
                    TextView.BufferType.SPANNABLE);
            localEditDiggView.setSelection(i + str1.length());
            Log.v("Tag", localEditDiggView.getText().toString());
        }
    };

    //设置输入字数长度限制
    private void setInputLimit() {
        TextView overWordCount = (TextView) findViewById(R.id.overWordCount);
        mWordCount = new WordCount(et_content, overWordCount);
        overWordCount.setText(String.valueOf(mWordCount.getMaxCount()));
        et_content.addTextChangedListener(mWordCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weibo_create, menu);
        return true;
    }

    private int flag = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_create_cancel:
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                finish();
                Anim.exit(ActivityCreateWeibo.this);
                break;
            case R.id.img_camera:
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                showSelectImagePopUpWindow(view);
                break;
            case R.id.img_video:
                recordVideo();
                break;
            case R.id.img_at:

                Intent intent = new Intent(ActivityCreateWeibo.this, ActivityAtUserSelect.class);
                startActivityForResult(intent, AT_REQUEST_CODE);
                break;
            case R.id.img_topic:
                Intent topicIntent = new Intent(ActivityCreateWeibo.this, AtTopicActivity.class);
                startActivityForResult(topicIntent, TOPIC_REQUEST_CODE);
                break;
            case R.id.img_face:
                if (tFaceView.getVisibility() == View.GONE) {
                    SociaxUIUtils.hideSoftKeyboard(ActivityCreateWeibo.this,
                            et_content);
                    tFaceView.setVisibility(View.VISIBLE);
                    img_face.setImageResource(R.drawable.key_bar);
                } else if (tFaceView.getVisibility() == View.VISIBLE) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                    SociaxUIUtils.showSoftKeyborad(ActivityCreateWeibo.this,
                            et_content);
                }
                break;
            case R.id.et_send_content:
                if (tFaceView.getVisibility() == View.VISIBLE) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                    SociaxUIUtils.showSoftKeyborad(ActivityCreateWeibo.this,
                            et_content);
                }
                break;
            case R.id.tv_create_submit:
                // 发布微博
                tv_submit.setEnabled(false);
                submitWeibo();
                SociaxUIUtils.hideSoftKeyboard(ActivityCreateWeibo.this, et_content);
                if (tFaceView.getVisibility() == View.VISIBLE) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                }
                break;
            case R.id.tv_get_my_location:
                if (view.getTag() == null) {
                    startActivityForResult(new Intent(this, ActivityGetMyLocation.class), GET_LOCATION);
                } else {
                    popupWindowLocation.show();
                }
                break;
            case R.id.fl_video_pre:
                Intent videoIntent = new Intent(ActivityCreateWeibo.this, ActivityVideoDetail.class);
                videoIntent.putExtra("url", staticVideoPath);
                startActivity(videoIntent);
                break;
            default:
                break;
        }
    }

    private void showSelectImagePopUpWindow(final View v) {
        final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(v.getContext());
        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectPhoto();
                } else if (position == 1) {
                    //拍摄图片
                    listener_selectImage.cameraImage();
                    type = AppConstant.CREATE_ALBUM_WEIBO;
                } else {
                    builder.dimss();
                }
            }
        });

        List<String> datas = new ArrayList<String>();
        datas.add("本地图片");
        datas.add("相机拍照");
        datas.add("取消");
        builder.create(datas);
    }


    private void sendBroadCast() {
        //发送广播至朋友圈，更新页面
        Intent intent = new Intent();
        intent.setAction(StaticInApp.NOTIFY_WEIBO);
        intent.setAction(StaticInApp.NOTIFY_DRAFT);
        sendBroadcast(intent);
    }

    @Override
    public void onReLocationClick() {
        startActivityForResult(new Intent(this, ActivityGetMyLocation.class), GET_LOCATION);
    }

    @Override
    public void onDelLocationClick() {
        if (tv_get_my_location.getTag() != null) {
            tv_get_my_location.setTag(null);
            tv_get_my_location.setText(getResources().getText(R.string.show_current_location));
        }
    }

    @SuppressLint("HandlerLeak")
    public class WeiboCreateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    showErrorDialog();
                    break;
                case 0:
                    // 从activityhome里面点击了发布新的照片微博，直接进入拍照
                    listener_selectImage.cameraImage();
                    break;
                case 1:
                    Intent getImage = new Intent(ActivityCreateWeibo.this, MultiImageSelectorActivity.class);
                    getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
                    getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                    getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                    getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
                    startActivityForResult(getImage, StaticInApp.LOCAL_IMAGE);
                    break;
                case AppConstant.COMMENT:
                    int result = msg.arg1;
                    if (result == 1) {
                        Toast.makeText(getApplicationContext(), "评论成功",
                                Toast.LENGTH_SHORT).show();
                        Object obj[] = (Object[]) msg.obj;
                        Intent data = new Intent();
                        data.putExtra("weibo", (ModelWeibo) obj[0]);
                        data.putExtra("position", (Integer) obj[1]);
                        setResult(AppConstant.COMMENT_SUCCESS, data);
                        ActivityCreateWeibo.this.finish();
                        Anim.exit(ActivityCreateWeibo.this);

//					//发送广播至微博页面，刷新微博
//					sendBroadCast();

                    } else {
                        tv_submit.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "评论失败",
                                Toast.LENGTH_SHORT).show();
                    }

//                    loadingView.hide(ll_content);
                    break;
                case AppConstant.TRANSPOND:
                    int transResult = msg.arg1;
                    if (transResult == 1) {
                        Toast.makeText(getApplicationContext(), "转发成功",
                                Toast.LENGTH_SHORT).show();
                        Object obj[] = (Object[]) msg.obj;
                        Intent data = new Intent();
                        data.putExtra("weibo", (ModelWeibo) obj[0]);
                        data.putExtra("position", (Integer) obj[1]);
                        data.putExtra("transWeiboId", (Integer) obj[2]);
                        setResult(RESULT_OK, data);

                        isFinishCurrentWeibo=true;
                        ActivityCreateWeibo.this.finish();
                        Anim.exit(ActivityCreateWeibo.this);

//					//发送广播至微博页面，刷新微博
//					sendBroadCast();

                    } else {
                        tv_submit.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "转发失败",
                                Toast.LENGTH_SHORT).show();
                    }
//                    loadingView.hide(ll_content);
                    break;
            }
            tv_submit.setEnabled(true);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap btp = null;
            switch (requestCode) {
                case AT_REQUEST_CODE:
                    if (data != null)
                        et_content.append("@" + data.getStringExtra("at_name") + " ");
                    et_content.setSelection(et_content.length());
                    break;
                case TOPIC_REQUEST_CODE:
                    if (data != null) {
                        if (data.getStringExtra("recent_topic").toString().length()
                                + et_content.length() > 140) {
                            Toast.makeText(getApplicationContext(),
                                    "已超过140，请重新加入话题", Toast.LENGTH_SHORT).show();
                        } else {
                            et_content.getText().append(
                                    "#" + data.getStringExtra("recent_topic")
                                            .toString() + "# ");
                            et_content.setSelection(et_content.length());
                        }
                    }
                    break;
                case StaticInApp.CAMERA_IMAGE:
                    try {
                        btp = Compress.compressPicToBitmap(new File(
                                listener_selectImage.getImagePath()));
                    } catch (Exception e) {
                        Log.e(TAG, "file saving..." + e.toString());
                    }
                    String path = listener_selectImage.getImagePath();
                    if (path != null) {
                        Bimp.address.add(path);
                        imageHs.setVisibility(View.VISIBLE);
                    }

                    isOriginal = false;     //默认对拍照的图片压缩
                    setMedia(HAS_IMAGE);

                    break;
                case StaticInApp.LOCAL_IMAGE:
                    List<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    boolean original = data.getBooleanExtra(MultiImageSelectorActivity.EXTRA_SELECT_ORIGIANL, false);
                    if (Bimp.address.size() < 9) {
                        for (String addr : list) {
                            if (!Bimp.address.contains(addr)) {
                                Bimp.address.add(addr);
                            }
                        }
                        imageHs.setVisibility(View.VISIBLE);
                    }

                    isOriginal = original;

                    setMedia(HAS_IMAGE);

                    break;
                case GET_LOCATION:
                    String address = data.getStringExtra("address");
                    latitude = data.getDoubleExtra("latitude", 0);
                    longitude = data.getDoubleExtra("longitude", 0);
                    if (address != null && !address.equals("")) {
                        tv_get_my_location.setText(address);
                        tv_get_my_location.setTag(address);
                    }
            }
        }
        if (requestCode == StaticInApp.UPLOAD_WEIBO) {
            if (Bimp.address.size() == 0) {
                img_video.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i("UserInfoActivity", "onConfigurationChanged");
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("UserInfoActivity", "横屏");
            Configuration o = newConfig;
            o.orientation = Configuration.ORIENTATION_PORTRAIT;
            newConfig.setTo(o);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("UserInfoActivity", "竖屏");
        }
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("finally")
    private Bitmap checkImage(Intent data) {
        Bitmap bitmap = null;
        try {
            Uri originalUri = data.getData();
            String path = GetLocalImagePath.getPath(getApplicationContext(),
                    originalUri);
            // String path = getRealPathFromURI(originalUri);
            bitmap = Compress.compressPicToBitmap(new File(path));
            if (bitmap != null) {
                listener_selectImage.setImagePath(path);
            }
        } catch (Exception e) {
            Toast.makeText(ActivityCreateWeibo.this, "图片加载失败",
                    Toast.LENGTH_SHORT).show();
            Log.e("checkImage", e.getMessage());
        } finally {
            return bitmap;
        }
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setMessage("输入的内容不能为空")
                .setPositiveButton(android.R.string.ok, null).show();
    }

    // 发布微博
    private void submitWeibo() {
        if (!UnitSociax.isNetWorkON(this)) {
            Toast.makeText(this, "请检查网络设置", Toast.LENGTH_SHORT).show();
        } else {
//            String content = et_content.getText().toString().trim();
//            if (type == AppConstant.COMMENT) {
//                isFinishCurrentWeibo = true;   // 评论不添加草稿
//                if (content.length() == 0) {
//                    showErrorDialog();
//                    tv_submit.setEnabled(true);
//                } else if (et_content.getText().toString().trim().length() > 140) {
//                    Toast.makeText(getApplicationContext(), R.string.word_limit, Toast.LENGTH_SHORT).show();
//                    tv_submit.setEnabled(true);
//                } else {
//                    loadingView.show(ll_content);
//                    final Comment comment = new Comment();
//                    comment.setContent(content);
//                    comment.setStatus(weibo);
//                    comment.setUname(Thinksns.getMy().getUserName());
//                    weibo.getComments().add(0, comment);
//                    weibo.setCommentCount(weibo.getCommentCount() + 1);
//                    final Object obj[] = new Object[]{weibo, position};
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Message msg = mHandler.obtainMessage();
//                            try {
//                                msg.what = AppConstant.COMMENT;
//                                msg.obj = obj;
//                                msg.arg1 = new Api.StatusesApi()
//                                        .comment(comment);
//                            } catch (VerifyErrorException e) {
//                                e.printStackTrace();
//                            } catch (ApiException e) {
//                                e.printStackTrace();
//                            } catch (UpdateException e) {
//                                e.printStackTrace();
//                            } catch (DataInvalidException e) {
//                                e.printStackTrace();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            mHandler.sendMessage(msg);
//                        }
//                    }).start();
//                }
//            } else if (type == AppConstant.TRANSPOND) {
//                isFinishCurrentWeibo = false;
//                if (et_content.getText().toString().trim().length() > 140) {
//                    Toast.makeText(getApplicationContext(), R.string.word_limit,
//                            Toast.LENGTH_SHORT).show();
//                    tv_submit.setEnabled(true);
//                } else {
//                    if (content == null) {
//                        return;
//                    }
//                    if (content.equals("null") || content.equals("")) {
//                        content = "转发分享";
//                    }
//                    final Comment comment = new Comment();
//                    comment.setContent(content);
//                    comment.setStatus(weibo);
//                    comment.setUname(Thinksns.getMy().getUserName());
//                    weibo.getComments().add(0, comment);
//                    weibo.setCommentCount(weibo.getCommentCount() + 1);
//                    new Thread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            ApiStatuses api = new Api.StatusesApi();
//                                Message msg = mHandler.obtainMessage();
//                                try {
//                                ModelBackMessage backMsg = api
//                                        .transpond(comment);
//                                Object transObj[] = new Object[]{weibo,
//                                        position, backMsg.getWeiboId()};
//                                msg.what = AppConstant.TRANSPOND;
//                                msg.obj = transObj;
//                                msg.arg1 = backMsg.getStatus();
//                            } catch (VerifyErrorException e) {
//                                e.printStackTrace();
//                            } catch (ApiException e) {
//                                e.printStackTrace();
//                            } catch (UpdateException e) {
//                                e.printStackTrace();
//                            } catch (DataInvalidException e) {
//                                e.printStackTrace();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            mHandler.sendMessage(msg);
//                        }
//                    }).start();
//                }
//            }else {
//                //发布微博/帖子
//                if (type == AppConstant.CREATE_WEIBO
//                        || type == StaticInApp.CREATE_POST) {
//                    if (et_content.getText().toString().trim().length() > 140) {
//                        Toast.makeText(getApplicationContext(),
//                                R.string.word_limit, Toast.LENGTH_SHORT).show();
//                        tv_submit.setEnabled(true);
//                        return;
//                    } else if (et_content.getText().toString().length() == 0) {
//                        Toast.makeText(getApplicationContext(),
//                                "请输入内容", Toast.LENGTH_SHORT).show();
//                        tv_submit.setEnabled(true);
//                        return;
//                    }
//
//                } else if (type == AppConstant.CREATE_IMAGE_WEIBO
//                        || type == AppConstant.CREATE_VIDEO_WEIBO
//                        || type == StaticInApp.WEIBO_EDIT_DRAFT) {
//                    if (et_content.getText().toString().trim().length() > 140) {
//                        Toast.makeText(getApplicationContext(),
//                                R.string.word_limit, Toast.LENGTH_SHORT).show();
//                        tv_submit.setEnabled(true);
//                        return;
//                    }
//                }
//
//                if (type == StaticInApp.CREATE_POST) {
//                    if (edit_post_title.getText().toString().trim().length() == 0) {
//                        Toast.makeText(getApplicationContext(), "标题不能为空",
//                                Toast.LENGTH_SHORT).show();
//                        tv_submit.setEnabled(true);
//                        return;
//                    }
//                }

                //执行发布操作
                Thinksns app = (Thinksns) ActivityCreateWeibo.this
                        .getApplicationContext();
                thread = new Worker(app, "Publish data");
                handler = new CreateHandler(thread.getLooper(),
                        ActivityCreateWeibo.this);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
    }

    private final class CreateHandler extends Handler {
        @SuppressWarnings("unused")
        private Context context;
        public CreateHandler(Looper looper, Context context) {
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent intent = new Intent(ActivityCreateWeibo.this,
                    ServiceUploadWeibo.class);

            String content = et_content.getText().toString().trim();
            switch (type) {
                case AppConstant.CREATE_TEXT_WEIBO:
                    if(TextUtils.isEmpty(content)) {
                        content = "发布分享";
                    }
                    break;
                case AppConstant.CREATE_ALBUM_WEIBO:
                    if(TextUtils.isEmpty(content)) {
                        content = "分享图片";
                    }
                    //准备发送的图片集合
                    imageList = new FormFile[Bimp.address.size()];
                    for (int i = 0; i < Bimp.max; i++) {
                        String path = Bimp.address.get(i);
                        String fileName = "";
                        int index = path.lastIndexOf("/");
                        if (index != -1)
                            fileName = path.substring(index + 1);
                        else {
                            index = path.lastIndexOf(".");
                            fileName = System.currentTimeMillis() + path.substring(index + 1);
                        }
                        imageList[i] = new FormFile(Bimp.getInputStreamFromLocal(path, isOriginal),
                                fileName, "pic", "application/octet-stream");
                    }
                    intent.putExtra("type", type);
                    intent.putExtra("tips", "正在上传图片...");
                case AppConstant.CREATE_VIDEO_WEIBO:
                    if(TextUtils.isEmpty(content)) {
                        content = "分享视频";
                    }
                    intent.putExtra("video_path", staticVideoPath);
                    intent.putExtra("type", type);
                    intent.putExtra("tips", "正在上传图片...");
                    break;
                case AppConstant.CREATE_WEIBA_POST:
                    intent.putExtra("post_title", edit_post_title.getText().toString());
                    intent.putExtra("feed_id", feed_id);
                    break;
                case AppConstant.CREATE_CHANNEL_WEIBO:
                    intent.putExtra("feed_id", feed_id);
                    break;
                case AppConstant.CREATE_TOPIC_WEIBO:
                    intent.putExtra("feed_id", feed_id);
                    break;
                case AppConstant.WEIBO_EDIT_IMAGE_DRAFT:
                    if(TextUtils.isEmpty(content))
                        content = "分享图片";
                    break;
                case AppConstant.WEIBO_EDIT_VIDEO_DRAFT:
                    if(TextUtils.isEmpty(content))
                        content = "分享视频";
                    break;
                case AppConstant.POST_EDIT_TRANSPORT_DRAFT:
                    break;
            }

            //锦适用与发布分享
            if (latitude != 0 && longitude != 0) {
                intent.putExtra("latitude", String.valueOf(latitude));
                intent.putExtra("longitude", String.valueOf(longitude));
                intent.putExtra("address", (String)tv_get_my_location.getTag());
            }

            ApiStatuses statuses = new Api.StatusesApi();
            try {
                boolean update = false;
                isFinishCurrentWeibo = true;
                String editContent = null;
                // 来自草稿箱的内容，删除草稿箱
                if (md_draft != null && md_draft.getId() != -1) {
                    SQLHelperWeiboDraft sql = ((Thinksns) getApplicationContext())
                            .getWeiboDraftSQL();
                    sql.delWeiboDraft(md_draft.getId());
                    sql.close();
                    sendBroadCast();
                }

                ModelPost post = null;
                ModelWeibo weibo = null;
                ModelBackMessage backMsg = null;
                if (type != StaticInApp.CREATE_POST) {
                    if (tv_get_my_location.getTag() != null
                                && longitude != 0
                                && latitude != 0) {
                            //发送位置微博
                            backMsg = statuses.createNewTextWeibo(weibo, longitude, latitude, (String) tv_get_my_location.getTag());
                    } else {
                            //发送普通文本微博
                            backMsg = statuses.createNewTextWeibo(weibo);
                    }
                } else {
                    //发布普通内容的帖子
                    backMsg = new ModelBackMessage(new Api.WeibaApi().creteNewPost(post).toString());
                }

                if (backMsg != null) {
                    Log.d(TAG, "backMsg=" + backMsg.toString());
                    update = backMsg.getStatus() >= 1;
                    checkSendResult(update, backMsg.getWeiboId());
                } else {
                    startService(intent);
                    ActivityCreateWeibo.this.finish();
                    Anim.exit(ActivityCreateWeibo.this);
                }

            } catch (VerifyErrorException e) {
                e.printStackTrace();
                Log.v(TAG, e.getMessage());
            } catch (ApiException e) {
                Log.v(TAG, e.getMessage());
                e.printStackTrace();
            } catch (UpdateException e) {
                Log.v(TAG, e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTextWeibo(String content) {
        if(TextUtils.isEmpty(content)) {
            content = "发布分享";
        }
    }


    private void createTopic() {

    }

    private void createChannel() {

    }

    private void createImageWeibo() {

    }

    private void createVideoWeibo() {

    }

    private void createDraftWeibo() {

    }

    private void createWeibaPost() {

    }

    private void checkSendResult(boolean update, int id) {
        System.err.println("result " + update);
        Message message = mainhandler.obtainMessage(2);
        if (update) {
            message.arg1 = 1;
            message.sendToTarget();
            staticVideoPath = null;
            setResult(RESULT_OK);
            finish();
        } else {
            message.arg1 = -1;
            message.sendToTarget();
        }
    }

    private Handler mainhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (msg.arg1 == -1) {
//                        ToastUtils.showLongToast("发布失败！");
                        Toast.makeText(ActivityCreateWeibo.this, "发布失败", Toast.LENGTH_SHORT).show();
                    } else {
                        if (FLAG != null) {
                            ToastUtils.showLongToast("审核后显示！");
                        } else {
//                            ToastUtils.showLongToast("发布成功！");
                        }

                        //发送广播至朋友圈，更新页面
                        sendBroadCast();
                    }
                    break;
            }
        }

        ;
    };

    @Override
    public void finish() {
        final Thinksns app = (Thinksns) this.getApplicationContext();
        if (!isFinishCurrentWeibo)// 判断是否可以直接finish如果不能，则检查草稿状态
            checkDraftState();
        if (isFinishCurrentWeibo) {
            // 如果已经发送微博，则清除数据
            // 清除图片
            Bimp.clear();
            // 清除视频
            staticVideoPath = "";
            staticTime = null;
            app.closeDb();
            UnitSociax.hideSoftKeyboard(this, et_content);
            MEDIA_TAG = 0;
            super.finish();
        } else {
            PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(this);
            builder.setMessage("保存草稿?", 18);
            builder.setTitle(null, 0);
            builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    app.getWeiboDraftSQL().addWeiboDraft(
                            md_draft.getId() == -1, md_draft);
                    app.closeDb();
                    isFinishCurrentWeibo = true;
                    finish();
                }
            });

            builder.setNegativeButton("不保存",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isFinishCurrentWeibo = true;
                            finish();
                        }
                    });

            builder.create();
        }
    }

    /**
     * 检测草稿状态
     */
    private void checkDraftState() {
        if (md_draft != null) {
            // 如果已经有草稿
            // 草稿内容没有修改过则直接可以退出
            if (((MEDIA_TAG & HAS_IMAGE) != 0) == md_draft.isHasImage()) {// 是否都有图片
                if (md_draft.getImageListToString().equals(
                        Bimp.getImageListToString())) {
                    // 图片是否同一个列表
                    if (((MEDIA_TAG & HAS_VIDEO) != 0) == md_draft.isHasVideo()) {// 是否都有视频
                        if (md_draft.getVideoPath().equals(staticVideoPath)) {// 视频地址是否都相同
                            if (et_content.getText().toString().trim()
                                    .equals(md_draft.getContent())) {// 内容是否相同
                                // 以上内容都相同的话，则表示对草稿箱没有任何修改
                                isFinishCurrentWeibo = true;
                            } else {
                                // 否则如果内容已经修改,则不能直接finish，先把草稿状态更新一下，然后提醒用户是否保存到草稿箱
                                resetDraftData();
                            }
                        } else {
                            resetDraftData();
                        }
                    } else {
                        resetDraftData();
                    }
                } else {
                    resetDraftData();
                }
            } else {
                resetDraftData();
            }
        } else {// 如果没有不是来自草稿箱，则新建一个草稿，内容也不能直接finish，需要提醒
            if ((Bimp.address == null || Bimp.address.size() == 0)
                    && (staticVideoPath == null || staticVideoPath.equals(""))
                    && et_content.getText().toString().trim().length() == 0 && TRANSPANT == 0) {// 没有任何内容，可以直接退出
                isFinishCurrentWeibo = true;
            } else {// 有内容，不能直接退出，需要提醒是否保存草稿箱
                md_draft = new ModelDraft();
                resetDraftData();
            }
        }
    }

    /**
     * 重置草稿内容
     */
    private void resetDraftData() {
        isFinishCurrentWeibo = false;
//        if (channel_category_id != null) {// 发布频道微博
//            md_draft.setChannel_id(channel_category_id);
//        }

        String editContent;
        if (((MEDIA_TAG & HAS_IMAGE) != 0)) {
            Log.v(TAG, "");
            md_draft.setHasImage(true);
            md_draft.setImageList(Bimp.getImageListToString() + "");
            editContent = et_content.getText().toString().trim().length() == 0 ? "发布图片"
                    : et_content.getText().toString().trim();
            md_draft.setHasVideo(false);
            md_draft.setVideoPath(staticVideoPath + "");
            md_draft.setContent(editContent);
        } else if (((MEDIA_TAG & HAS_VIDEO) != 0)) {
            md_draft.setHasVideo(true);
            md_draft.setVideoPath(staticVideoPath + "");
            editContent = et_content.getText().toString().trim().length() == 0 ? "发布视频"
                    : et_content.getText().toString().trim();
            md_draft.setHasImage(false);
            md_draft.setImageList("");
            md_draft.setContent(editContent);
        } else {
            md_draft.setHasVideo(false);
            md_draft.setVideoPath(staticVideoPath + "");
            md_draft.setHasImage(false);
            md_draft.setImageList("");
            editContent = et_content.getText().toString().trim();
            //如果是转发的分享，在草稿列表上，加上缺省文字
            if (TRANSPANT != 0) {
                if (editContent != null) {
                    if (editContent.equals("")) {
                        md_draft.setContent("转发分享");
                    } else {
                        md_draft.setContent(editContent);
                    }
                }
//                md_draft.setWeibo_id(weibo.getWeiboId());
            } else {
                md_draft.setContent(editContent);
            }
        }
    }

    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 图片加载
     */

    @SuppressLint("HandlerLeak")
    public class SelectedImgGridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private Context mContext;
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;
        private GridView mgridViwew;

        private int imgWidth = 0;       //图片显示宽高
        private int horizontalSpacing;  //图片左右间隙
        private int gridViewHeight;     //列表高度

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public SelectedImgGridAdapter(Context context, GridView gridView) {
            inflater = LayoutInflater.from(context);
            this.mContext = context;
            this.mgridViwew = gridView;
            imgWidth = UnitSociax.dip2px(context, 68);
            horizontalSpacing = UnitSociax.dip2px(context, 3);
            gridViewHeight = UnitSociax.dip2px(context, 70);

        }

        //更新已选图片列表
        public void update() {
            loading();
        }

        public int getCount() {
            int count = 0;
            if (Bimp.bmp.size() == 0)
                count = 0;
            else if (Bimp.bmp.size() == 9)
                count = 9;
            else {
                count = (Bimp.bmp.size() + 1);
            }

            //计算列表的宽度
            int gridviewWidth = count * imgWidth + (count - 1) * horizontalSpacing;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    gridviewWidth, gridViewHeight);
            mgridViwew.setLayoutParams(params); // 重点
            mgridViwew.setNumColumns(count);
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgWidth, imgWidth);
            holder.image.setLayoutParams(params);

            if (position == Bimp.bmp.size()) {
//                holder.image.setImageBitmap(BitmapFactory.decodeResource(
//                        getResources(), R.drawable.icon_addpic_unfocused));
                holder.image.setImageResource(R.drawable.icon_addpic_unfocused);
            } else {
//                holder.image.setImageBitmap(Bimp.bmp.get(position));
                Glide.with(parent.getContext())
                        .load(Bimp.address.get(position))
                        .into(holder.image);
            }

            return convertView;
        }

        public void clearBtm() {
            Bimp.bmp.clear();
            adapter.notifyDataSetChanged();
        }

        public class ViewHolder {
            public ImageView image;
        }

        private Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (Bimp.bmp.size() > 0) {
//                    setMedia(HAS_IMAGE);
                    type = AppConstant.CREATE_ALBUM_WEIBO;
                    imageHs.setVisibility(View.VISIBLE);
                }

                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        if (msg.arg1 == -1) {
                            ToastUtils.showLongToast("发布失败！");
                        } else {
                            ToastUtils.showLongToast("发布成功！");
                        }
                        break;
                }
            }
        };

        /**
         * 加载选中图片
         */
        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.address.size()) {
                            //图片读取完成
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else if (Bimp.max > Bimp.address.size()) {
                            break;
                        } else {
//                            try {
//                                String path = Bimp.address.get(Bimp.max);
//                                Bitmap bm = Bimp.revitionImageSize(path);
//                                Bimp.bmp.add(bm);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
                            Bimp.max += 1;
                        }
                    }
                }
            }).start();
        }
    }
}
