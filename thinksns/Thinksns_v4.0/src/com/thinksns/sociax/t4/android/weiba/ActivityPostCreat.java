package com.thinksns.sociax.t4.android.weiba;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.ApiWeiba;
import com.thinksns.sociax.api.Api.WeibaApi;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.CommentPost;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.img.Bimp;
import com.thinksns.sociax.t4.android.img.FileUtils;
import com.thinksns.sociax.t4.android.img.PhotoActivity;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowSelectImage;
import com.thinksns.sociax.t4.android.temp.GetLocalImagePath;
import com.thinksns.sociax.t4.android.temp.SelectImageListener;
import com.thinksns.sociax.t4.android.user.ActivityAtUserSelect;
//import com.thinksns.sociax.t4.android.video.MediaRecorderActivity;
import com.thinksns.sociax.t4.android.video.RecorderVideoActivity;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.service.ServiceUploadWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.thinksnsbase.utils.FormFile;
import com.thinksns.sociax.thinksnsbase.utils.WordCount;
import com.thinksns.sociax.unit.Compress;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.sociax.android.R;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 类说明： 帖子操作 需要传入内容:
 * 1.发布新的帖子：intent int weiba_id;
 * 2.评论和转发直接传入序列化的int post_id
 * 如成功发布之后会setActivity Result_OK 以更新列表
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-8
 */
public class ActivityPostCreat extends ThinksnsAbscractActivity implements
        OnClickListener {
    // new
    private ServiceUploadWeibo uploadService;
    private TextView tv_cancel, tv_submit;// cancel:取消（返回） submit:提交（发布）
    private static EditText et_content, et_title;
    private ImageView img_camera, img_video, img_at, img_topic, img_face,
            img_fill_1, img_fill_2;// 相机、录像、@、主题、表情、填充布局1、填充布局2

    int weiba_id;//如果发布新的帖子必须传入weiba_id
    LoadingView loadingView;
    LinearLayout ll_content, btn_layout;
    private int post_id;// 帖子id

    private ResultHandler handlerResult;
    private int type;// 判断进入方式：评论、转发或者发布微博
    private int position;
    private static Worker thread;

    private ImageView preview;
    private GridView noScrollgridview;
    private SelectedImgGridAdapter adapter;
    private HorizontalScrollView imageHs;

    private boolean hasImage = false;
    private boolean hasVideo = false;
    // old
    private static final String TAG = "PostCreate";

    private ListFaceView tFaceView;
    private static final int AT_REQUEST_CODE = 3;
    private static final int TOPIC_REQUEST_CODE = 4;

    private SelectImageListener listener_SelectImage;
    Intent intent;

    public static String staticFrom;
    public static Long staticTime;
    public static String staticVideoPath;

    private static CreateHandler handlerCreate;
    public static FormFile[] imageList;

    //评论
    private String comment_user;        //评论人姓名
    private int comment_id;
    private ModelComment commentModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        initIntentData();
        initView();
        initListener();
        initData();
        checkIntentData();
    }

    /**
     * 初始化数据状态
     */
    private void initData() {
        if (type == StaticInApp.POST_TRANSPORT) {
            // 转发
            et_content.setText("转发帖子");
            et_title.setVisibility(View.GONE);
            img_camera.setVisibility(View.GONE);
            img_at.setVisibility(View.VISIBLE);
            img_topic.setVisibility(View.VISIBLE);
        } else if (type == StaticInApp.POST_COMMENT) {// 评论
            et_content.setHint("请输入评论内容");
            et_title.setVisibility(View.GONE);
            img_camera.setVisibility(View.GONE);
            img_at.setVisibility(View.VISIBLE);
            img_topic.setVisibility(View.VISIBLE);
        } else if (type == StaticInApp.WEIBA_COMMENT_REPLY) {
            //回复评论
            et_content.setText("回复@" + comment_user + ":");
            et_title.setVisibility(View.GONE);
            img_camera.setVisibility(View.GONE);
            img_at.setVisibility(View.GONE);
            img_topic.setVisibility(View.GONE);
        } else {// 发布新的帖子
            // setMediaStatus();
            // 需要用到照片
            initPicManager();
        }
        //setInputLimit();
    }

    /**
     * 设置视频媒体状态 微博发布才需要用到，暂时保留在帖子发布里面方便调用
     */
    private void setMediaStatus() {
        if (staticVideoPath != null && !staticVideoPath.equals("")) {
            preview.setImageBitmap(getVideoThumbnail(staticVideoPath, 260, 260,
                    MediaStore.Images.Thumbnails.FULL_SCREEN_KIND));
            preview.setVisibility(View.VISIBLE);
            tv_submit.setEnabled(true);
            type = AppConstant.CREATE_TEXT_WEIBO;
            hasVideo = true;
            hasImage = false;
        }
    }

    private void initListener() {
        tv_cancel.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        img_video.setOnClickListener(this);
        img_at.setOnClickListener(this);
        img_topic.setOnClickListener(this);
        img_face.setOnClickListener(this);
        et_content.setOnClickListener(this);
        et_title.setOnClickListener(this);

        et_title.setOnFocusChangeListener(etOnFocusChangeListener);
        et_content.setOnFocusChangeListener(etOnFocusChangeListener);
    }

    private View.OnFocusChangeListener etOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (tFaceView.getVisibility() == View.VISIBLE) {
                tFaceView.setVisibility(View.GONE);
            }
            // 标题不允许输入表情
            if (v.getId() == R.id.et_sent_title) {
                if (hasFocus) {
                    btn_layout.setVisibility(View.GONE);
                } else {
                    btn_layout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private void initIntentData() {
        intent = getIntent();
        type = intent.getIntExtra("type", -1);
        if (intent.hasExtra("post_id")) {
            post_id = intent.getIntExtra("post_id", 0);
        }
        if (intent.hasExtra("weiba_id")) {
            weiba_id = intent.getIntExtra("weiba_id", 0);
        }
        if (intent.hasExtra("commentModel")) {
            commentModel = (ModelComment) intent.getSerializableExtra("commentModel");
        }
        if (type == StaticInApp.WEIBA_COMMENT_REPLY) {
            comment_user = intent.getStringExtra("comment_user");
            comment_id = intent.getIntExtra("comment_id", 0);
        }
        if (type == -1) {
            // 没有传入转发或者评论或者创建，类型错误
            Log.e("ActivityCreatePost--initIntentData", "err need intent type");
            finish();
        } else if (((type == StaticInApp.POST_TRANSPORT || type == StaticInApp.POST_COMMENT)
                && post_id == 0) || (type == StaticInApp.CREATE_POST && weiba_id == 0)) {
            Log.e("ActivityCreatePost--initIntentData",
                    "err  needs intent  post_id");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (adapter != null)
            adapter.update();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            uploadService = ((ServiceUploadWeibo.LocalBinder) service)
                    .getService();
            Toast.makeText(ActivityPostCreat.this, "正在后台上传视频",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            uploadService = null;
            Toast.makeText(ActivityPostCreat.this, "jiebang",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void initView() {
        // new
        handlerResult = new ResultHandler();
        tv_cancel = (TextView) findViewById(R.id.tv_create_cancel);
        tv_submit = (TextView) findViewById(R.id.tv_create_submit);
        preview = (ImageView) findViewById(R.id.preview);
        et_content = (EditText) findViewById(R.id.et_send_content);
        et_title = (EditText) findViewById(R.id.et_sent_title);
        img_camera = (ImageView) findViewById(R.id.img_camera);
        img_video = (ImageView) findViewById(R.id.img_video);
        img_at = (ImageView) findViewById(R.id.img_at);
        img_topic = (ImageView) findViewById(R.id.img_topic);
        img_face = (ImageView) findViewById(R.id.img_face);
        img_fill_1 = (ImageView) findViewById(R.id.img_fill_layout_1);
        img_fill_2 = (ImageView) findViewById(R.id.img_fill_layout_2);
        tFaceView = (ListFaceView) findViewById(R.id.face_view);

        loadingView = (LoadingView) findViewById(LoadingView.ID);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        btn_layout = (LinearLayout) findViewById(R.id.btn_layout);

        imageHs = (HorizontalScrollView) findViewById(R.id.imageHoriScroll);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);

        et_content.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                for (int i = arg0.length(); i > 0; i--) {
                    if (arg0.subSequence(i - 1, i).toString().equals("\n"))
                        arg0.replace(i - 1, i, " ");
                }
            }
        });

        tFaceView.initSmileView(et_content);
    }

    /**
     * 照片编辑初始化
     */
    private void initPicManager() {
        if (listener_SelectImage == null)
            listener_SelectImage = new SelectImageListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (105 * 10 * density);
        int itemWidth = (int) (100 * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        noScrollgridview.setLayoutParams(params); // 重点
        noScrollgridview.setColumnWidth(itemWidth); // 重点
        noScrollgridview.setHorizontalSpacing(5); // 间距
        noScrollgridview.setStretchMode(GridView.NO_STRETCH);
        noScrollgridview.setNumColumns(10); // 重点
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new SelectedImgGridAdapter(this);
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                SociaxUIUtils.hideSoftKeyboard(ActivityPostCreat.this,
                        et_content);
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindowSelectImage(ActivityPostCreat.this,
                            noScrollgridview, listener_SelectImage);
                } else {
                    Intent intent = new Intent(ActivityPostCreat.this,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
    }

    private void checkIntentData() {
        switch (type) {
            case AppConstant.COMMENT:
                img_camera.setVisibility(View.GONE);
                img_video.setVisibility(View.GONE);
                img_fill_1.setVisibility(View.INVISIBLE);
                img_fill_2.setVisibility(View.INVISIBLE);
                break;
            case AppConstant.TRANSPOND:
                img_camera.setVisibility(View.GONE);
                img_video.setVisibility(View.GONE);
                img_fill_1.setVisibility(View.INVISIBLE);
                img_fill_2.setVisibility(View.INVISIBLE);
                et_content.setText("转发帖子");
                et_content.setSelection(et_content.getText().toString().trim()
                        .length());
                break;
            case AppConstant.CREATE_TEXT_WEIBO:
                img_camera.setVisibility(View.VISIBLE);
                img_video.setVisibility(View.VISIBLE);
                img_fill_1.setVisibility(View.GONE);
                img_fill_2.setVisibility(View.GONE);
                break;
            case AppConstant.CREATE_IMAGE_WEIBO:
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                handlerResult.sendEmptyMessageDelayed(0, 1000);
                type = AppConstant.CREATE_TEXT_WEIBO;
                break;
            case AppConstant.CREATE_VIDEO_WEIBO:
                onClick(img_video);
                break;
            default:
                break;
        }
    }

    /**
     * 设置字数限制
     */
    private void setInputLimit() {
        TextView overWordCount = (TextView) findViewById(R.id.overWordCount);
        WordCount wordCount = new WordCount(et_content, overWordCount);
        overWordCount.setText(wordCount.getMaxCount() + "");
        et_content.addTextChangedListener(wordCount);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_create_cancel:
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                if (hasVideo) {
                    hasVideo = false;
                    staticVideoPath = null;
                    staticTime = null;
                }
                finish();
                Anim.exit(ActivityPostCreat.this);
                break;
            // 拍照
            case R.id.img_camera:
                new PopupWindowSelectImage(ActivityPostCreat.this,
                        noScrollgridview, listener_SelectImage);
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                tFaceView.setVisibility(View.GONE);
                imageHs.setVisibility(View.VISIBLE);
                break;
            // 视频
            case R.id.img_video:
                imageHs.setVisibility(View.GONE);
//			Intent intentVideo = new Intent(ActivityPostCreat.this,
//					MediaRecorderActivity.class);
                Intent intentVideo = new Intent(this, RecorderVideoActivity.class);
//			intentVideo.putExtra("from", "weibo");
                startActivity(intentVideo);
                type = AppConstant.CREATE_VIDEO_WEIBO;
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                tFaceView.setVisibility(View.GONE);
                Anim.in(ActivityPostCreat.this);
                break;
            // @好友
            case R.id.img_at:
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                tFaceView.setVisibility(View.GONE);
                Intent intent = new Intent(ActivityPostCreat.this,
                        ActivityAtUserSelect.class);
                startActivityForResult(intent, AT_REQUEST_CODE);
                break;
            // 话题
            case R.id.img_topic:
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                tFaceView.setVisibility(View.GONE);
                Intent topicIntent = null;//new Intent(ActivityPostCreat.this, AtTopicActivity.class);
                startActivityForResult(topicIntent, TOPIC_REQUEST_CODE);
                break;
            // 表情
            case R.id.img_face:
                if (tFaceView.getVisibility() == View.GONE) {
                    SociaxUIUtils.hideSoftKeyboard(ActivityPostCreat.this,
                            et_content);
                    tFaceView.setVisibility(View.VISIBLE);
                    img_face.setImageResource(R.drawable.key_bar);
                } else if (tFaceView.getVisibility() == View.VISIBLE) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                    SociaxUIUtils.showSoftKeyborad(ActivityPostCreat.this,
                            et_content);
                }
                break;
            // 内容编辑框
            case R.id.et_send_content:
                if (tFaceView.getVisibility() == View.VISIBLE) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                    SociaxUIUtils.showSoftKeyborad(ActivityPostCreat.this,
                            et_content);
                }
                break;
            // 标题编辑框
            case R.id.et_sent_title:
                tFaceView.setVisibility(View.GONE);
                img_face.setImageResource(R.drawable.face_bar);
                SociaxUIUtils.showSoftKeyborad(ActivityPostCreat.this,
                        et_content);
                break;
            case R.id.tv_create_submit:
                // 发布微博
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                tFaceView.setVisibility(View.GONE);
                tv_submit.setEnabled(false);
                doSubmit();
                break;
            default:
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    public class ResultHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ActivityPostCreat.this.onClick(img_camera);
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
                        ActivityPostCreat.this.finish();
                        Anim.exit(ActivityPostCreat.this);
                    } else {
                        tv_submit.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "评论失败",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConstant.TRANSPOND:
                    int transResult = msg.arg1;
                    if (transResult == 1) {
                        Toast.makeText(getApplicationContext(), "转发成功", Toast.LENGTH_SHORT).show();
                        Object obj[] = (Object[]) msg.obj;
                        Intent data = new Intent();
                        data.putExtra("weibo", (ModelWeibo) obj[0]);
                        data.putExtra("position", (Integer) obj[1]);
                        data.putExtra("transWeiboId", (Integer) obj[2]);
                        setResult(RESULT_OK, data);
                        ActivityPostCreat.this.finish();
                        Anim.exit(ActivityPostCreat.this);
                    } else {
                        tv_submit.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "转发失败",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StaticInApp.POST_COMMENT:
                    checkSendResult((ModelBackMessage) msg.obj);
                    break;
                case StaticInApp.POST_TRANSPORT:
                    checkSendResult((ModelBackMessage) msg.obj);
                    break;
                case StaticInApp.WEIBA_COMMENT_REPLY:
                    JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json.has("status")
                                && json.getInt("status") == 1) {
                            Toast.makeText(getApplicationContext(), "评论成功",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StaticInApp.CREATE_NEW_WEIBA_COMMENT);
                            sendBroadcast(intent);
                            ActivityPostCreat.this.finish();
                            Anim.exit(ActivityPostCreat.this);
                        } else {
                            Toast.makeText(getApplicationContext(), "评论失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                        et_content.append("@"
                                + data.getStringExtra("at_name").toString() + " ");
                    et_content.setSelection(et_content.length());
                    break;
                case TOPIC_REQUEST_CODE:
                    if (data != null) {
                        et_content.append("#"
                                + data.getStringExtra("recent_topic")
                                .toString() + "# ");
                        et_content.setSelection(et_content.length());
                    }
                    break;
                case StaticInApp.CAMERA_IMAGE:
                    if (Bimp.address.size() < 9 && resultCode == -1) {
                        Bimp.address.add(listener_SelectImage.getImagePath());
                    }
                    break;
                case StaticInApp.LOCAL_IMAGE:
//                    btp = checkImage(data);
                    List<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (Bimp.address.size() < 9) {
                        for (String addr : list) {
                            if (!Bimp.address.contains(addr)) {
                                Bimp.address.add(addr);
                            }
                        }
                    }
                    break;
                case StaticInApp.ZOOM_IMAGE:
                    if (Bimp.address.size() < 9 && resultCode == -1) {
                        Bimp.address.add(listener_SelectImage.getImagePath());
                    }
                    break;
            }
        }
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
                listener_SelectImage.setImagePath(path);
            }
        } catch (Exception e) {
            Toast.makeText(ActivityPostCreat.this, "图片加载失败",
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

    /**
     * 提交
     */
    private void doSubmit() {
        String content = et_content.getText().toString().trim();
        if (content.length() == 0) {
            showErrorDialog();
            tv_submit.setEnabled(true);
            return;
        }
        if (type == AppConstant.CREATE_TEXT_WEIBO
                || type == AppConstant.CREATE_IMAGE_WEIBO
                || type == AppConstant.CREATE_VIDEO_WEIBO) {
            if (et_content.getText().toString().trim().length() > 140) {
                Toast.makeText(getApplicationContext(), R.string.word_limit,
                        Toast.LENGTH_SHORT).show();
            } else {
                Thinksns app = (Thinksns) ActivityPostCreat.this
                        .getApplicationContext();
                thread = new Worker(app, "Publish data");
                handlerCreate = new CreateHandler(thread.getLooper(),
                        ActivityPostCreat.this);
                Message msg = handlerCreate.obtainMessage();
                handlerCreate.sendMessage(msg);
            }
        }
        // 创建新的帖子
        if (type == StaticInApp.CREATE_POST) {
            if (et_title.getText().toString().trim().length() == 0) {
                Toast.makeText(getApplicationContext(), "标题不能为空",
                        Toast.LENGTH_SHORT).show();
                tv_submit.setEnabled(true);
            } else {
                loadingView.show(ll_content);
                Thinksns app = (Thinksns) ActivityPostCreat.this
                        .getApplicationContext();
                thread = new Worker(app, "Publish data");
                handlerCreate = new CreateHandler(thread.getLooper(),
                        ActivityPostCreat.this);
                Message msg = handlerCreate.obtainMessage();
                handlerCreate.sendMessage(msg);
            }
        } else if (type == StaticInApp.POST_COMMENT
                || type == StaticInApp.POST_TRANSPORT) {
            final ModelComment comment = new ModelComment();
            comment.setContent(content);
            comment.setComment_id(post_id);
            if (type == StaticInApp.POST_TRANSPORT) {// 转发必须同时评论
                comment.setIsShareFeed("1");
            }
            loadingView.show(ll_content);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ApiWeiba api = new Api.WeibaApi();
                    Message msg = handlerResult.obtainMessage();
                    try {
                        msg.what = type;
                        ModelBackMessage backMsg = api.replyPost(comment);
                        msg.obj = backMsg;
                    } catch (VerifyErrorException e) {
                        e.printStackTrace();
                    } catch (ApiException e) {
                        e.printStackTrace();
                    } catch (UpdateException e) {
                        e.printStackTrace();
                    }
                    handlerResult.sendMessage(msg);
                }
            }).start();
        } else if (type == StaticInApp.WEIBA_COMMENT_REPLY) {
            //回复帖子的评
//			final CommentPost comment = new CommentPost();
//			comment.setPostId(post_id);
//			comment.setReplyId(comment_id);
//			comment.setContent(content);
            loadingView.show(ll_content);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = handlerResult.obtainMessage();
                    try {
                        if (comment_user != null) {
                            msg.what = type;
                            msg.obj = new Api.WeibaApi().replyComment(commentModel, comment_user);
                        }
                    } catch (ApiException e) {
                        e.printStackTrace();
                    } catch (VerifyErrorException e) {
                        e.printStackTrace();
                    } catch (UpdateException e) {
                        e.printStackTrace();
                    }
                    handlerResult.sendMessage(msg);
                }
            }).start();
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
            // 获取数据
            Thinksns app = thread.getApp();
            WeibaApi statuses = app.getWeibaApi();
            try {
                /******************** 整理好新的post ******************/
                String editContent, editTitle = null;
                // 发布新的帖子必须要有标题
                if (et_title.length() == 0) {
                    return;
                } else {
                    editTitle = et_title.getText().toString().trim();
                }

                // 是否带有图片
                if (hasImage) {
                    editContent = et_content.getText().toString().trim()
                            .length() == 0 ? "发布图片" : et_content.getText()
                            .toString().trim();
                } else if (hasVideo) {
                    editContent = et_content.getText().toString().trim()
                            .length() == 0 ? "发布视频" : et_content.getText()
                            .toString().trim();
                } else {
                    editContent = et_content.getText().toString().trim()
                            .length() == 0 ? "发布帖子" : et_content.getText()
                            .toString().trim();
                }

                ModelPost post = new ModelPost();
                // if (staticTime != null && staticTime != 0)
                // post.setTimeLine(staticTime / 1000);
                post.setWeiba_id(weiba_id);
                post.setContent(editContent);
                post.setTitle(editTitle);

                /******************** 判断调用发送接口 ******************/
                ModelBackMessage backMsg = null;
                if (hasImage) {
                    imageList = new FormFile[Bimp.address.size()];
                    for (int i = 0; i < Bimp.max; i++) {
                        File filei = new File(Bimp.address.get(i));
                        imageList[i] = new FormFile(
                                Compress.compressPic(filei), filei.getName(),
                                "pic", "application/octet-stream");
                    }
                    try {
                        backMsg = statuses.createNewPostWithImage(post,
                                imageList);
                    } catch (VerifyErrorException e) {
                        e.printStackTrace();
                    } catch (UpdateException e) {
                        e.printStackTrace();
                    }
                } else if (hasVideo) {
                } else {
                    try {
                        backMsg = new ModelBackMessage(statuses.creteNewPost(
                                post).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                /******************** 最后检测发送成功还是发送失败 ******************/
                checkSendResult(backMsg);

            } catch (ApiException e) {
                Log.v(TAG, e.getMessage());
            }
            thread.quit();
        }
    }

    /**
     * 判断是否发送成功
     *
     */
    private void checkSendResult(ModelBackMessage backMsg) {
        boolean isSuccess = false;
        loadingView.hide(ll_content);
        if (backMsg != null) {
            isSuccess = backMsg.getStatus() >= 1;
            if (isSuccess) {
//				setResult(RESULT_OK);

                Intent intent = new Intent();
                intent.setAction(StaticInApp.CREATE_NEW_WEIBA_COMMENT);
                sendBroadcast(intent);

                finish();
            }
            Toast.makeText(this, backMsg.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        Bimp.bmp.clear();
        Bimp.address.clear();
        Bimp.max = 0;
        if (hasImage) {
            hasImage = false;
        }
        Thinksns app = (Thinksns) this.getApplicationContext();
        app.closeDb();
        super.finish();
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
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public SelectedImgGridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == Bimp.bmp.size()) {// 最后一个位置
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {// 如果最后一个位置是9，最大值，则隐藏
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
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

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (Bimp.bmp.size() > 0)
                    hasImage = true;
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        noScrollgridview.setAdapter(adapter);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        /**
         * 加载图片地址里面的图片
         */
        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    int i = 0;
                    while (true) {
//						Log.v("ActivityCreatePost--SelectedImgGridAdapter", "第"
//								+ i + "次执行while");
                        i++;
                        if (Bimp.max == Bimp.address.size()) {
//							Log.v("ActivityCreatePost--SelectedImgGridAdapter",
//									"Bimp.max=Bimp.address.size");
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else if (Bimp.max > Bimp.address.size()) {
//							Log.v("ActivityCreatePost--SelectedImgGridAdapter",
//									"Bimp.max>Bimp.address.size");
                            break;
                        } else {
//							Log.v("ActivityCreatePost--SelectedImgGridAdapter",
//									"Bimp.max<Bimp.address.size");
                            try {
                                String path = Bimp.address.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_post;
    }


    /**
     * 表情Adapter
     */
    private ListFaceView.FaceAdapter mFaceAdapter = new ListFaceView.FaceAdapter() {

        @Override
        public void doAction(int paramInt, String paramString) {
            EditText localEditBlogView = et_content;
            int i = localEditBlogView.getSelectionStart();
            int j = localEditBlogView.getSelectionStart();
            String str1 = "[" + paramString + "]";
            String str2 = localEditBlogView.getText().toString();
            SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
            localSpannableStringBuilder.append(str2, 0, i);
            localSpannableStringBuilder.append(str1);
            localSpannableStringBuilder.append(str2, j, str2.length());
            UnitSociax.showContentFaceView(ActivityPostCreat.this,
                    localSpannableStringBuilder);
            localEditBlogView.setText(localSpannableStringBuilder,
                    TextView.BufferType.SPANNABLE);
            localEditBlogView.setSelection(i + str1.length());
            Log.v("Tag", localEditBlogView.getText().toString());
        }
    };
}
