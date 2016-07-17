package com.thinksns.sociax.t4.android.weibo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.img.Bimp;
import com.thinksns.sociax.t4.android.img.PhotoActivity;
import com.thinksns.sociax.t4.android.map.ActivityGetMyLocation;
import com.thinksns.sociax.t4.android.popupwindow.PopUpWindowAlertDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowLocation;
import com.thinksns.sociax.t4.android.temp.SelectImageListener;
import com.thinksns.sociax.t4.android.topic.AtTopicActivity;
import com.thinksns.sociax.t4.android.user.ActivityAtUserSelect;
import com.thinksns.sociax.t4.android.video.ActivityVideoDetail;
import com.thinksns.sociax.t4.android.video.MediaRecorderActivity;
import com.thinksns.sociax.t4.android.video.ToastUtils;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.t4.model.ModelWeiba;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.service.ServiceUploadWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.thinksnsbase.utils.WordCount;
import com.thinksns.sociax.unit.Compress;
import com.thinksns.sociax.unit.SociaxUIUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 发布分享/帖子/频道/话题/草稿的基类
 * 该类默认只支持发布文字、图片、视频类
 */
public class ActivityCreateBase extends ThinksnsAbscractActivity implements View.OnClickListener,
        PopupWindowLocation.OnLocationClickListener {
    private static final String TAG = ActivityCreateBase.class.getSimpleName();
    //常亮数据类型
    public static final String INTENT_VIDEO_PATH = "video_path";
    public static final String INTENT_IAMGE_LIST = "image_list";
    public static final String INTENT_TIPS = "tips";
    public static final String INTENT_DRAFT = "draft";
    public static final String INTENT_TYPE = "type";
    public static final String INTENT_DATA = "data";
    public static final String INTENT_ORIGINAL = "is_original";

    protected static final int AT_REQUEST_CODE = 3;
    protected static final int TOPIC_REQUEST_CODE = 4;
    protected static final int GET_LOCATION = 5;

    protected WordCount mWordCount;             //文字字数限制
    //控件初始化
    protected TextView tv_get_my_location;     // 地理位置
    protected EditText et_content;             //文本内容框
    protected ImageView img_camera,             //拍照图标
            img_video,              //视频图标
            img_at,                 //@图标
            img_topic,              //话题图标
            img_face;               //表情图标
    protected ImageView preview;                //视频预览图
    protected FrameLayout videoPreview;           //视频预览父布局
    protected GridView noScrollgridview;       //noused
    protected ListFaceView tFaceView;              //表情布局
    protected ViewStub stub_post_title;        //帖子标题viewStub
    protected EditText edit_post_title;        //帖子标题编辑框

    protected HorizontalScrollView imageHs;        //图片列表

    //工具类
    protected PopupWindowLocation popupWindowLocation;    //地理位置提示窗口
    protected SelectImageListener listener_selectImage;   //拍照工具
    protected SelectedImgGridAdapter adapter;   //图片显示适配器
    protected SociaxItem data;                            //发布内容封装
    //基础数据类型
    public static String staticVideoPath = "";           //视频拍摄后的文件地址
    protected String content = "";                   //编辑内容
    protected double latitude = 0.0f,
            longitude = 0.0f;
    protected String address = "";                   //地址
    protected boolean isOriginal = false;             //是否发送原图
    protected ModelDraft mDraft;                         //草稿内容
    //发布文章类型，默认为纯文本
    protected int type = AppConstant.CREATE_TEXT_WEIBO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initIntent();
        super.onCreate(savedInstanceState);
        initView();
        initPicviews();
        initListener();
        initData();
    }

    //普通发布没有标题提示,设置标题为空即可
    @Override
    public String getTitleCenter() {
        return "";
    }

    //顶部左侧的文字
    protected String getLeftBtnText() {
        return "取消";
    }

    //顶部右侧的按钮文字
    protected String getRightBtnText() {
        return "发布";
    }

    //设置顶部标题
    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(this, getLeftBtnText(), getRightBtnText());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_weibo_create;
    }

    @Override
    public View.OnClickListener getRightListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发布微博
                v.setEnabled(false);
                submitWeibo(v);
                if (tFaceView.getVisibility() == View.VISIBLE) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                }
            }
        };
    }

    @Override
    public View.OnClickListener getLeftListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消发布
                if (needSaveDraft()) {
                    if (type == AppConstant.CREATE_TEXT_WEIBO) {
                        if (et_content.getText().toString().trim().length() > 0) {
                            //提示保存草稿
                            saveDraft();
                            return;
                        }
                    } else if (type == AppConstant.CREATE_ALBUM_WEIBO) {
                        if (Bimp.address.size() > 0) {
                            //提示保存图片
                            saveDraft();
                            return;
                        }
                    } else if (type == AppConstant.CREATE_VIDEO_WEIBO) {
                        if (!TextUtils.isEmpty(staticVideoPath)) {
                            //提示保存视频
                            saveDraft();
                            return;
                        }
                    } else {
                        //其他类型直接保存草稿
                        saveDraft();
                        return;
                    }
                }

                //隐藏键盘
                et_content.clearFocus();
                UnitSociax.hideSoftKeyboard(v.getContext(), et_content);
                finish();
            }
        };
    }

    //初始化intent
    protected void initIntent() {
        //如果是发布图片微博，是否设置原图发送
        isOriginal = getIntent().getBooleanExtra(INTENT_ORIGINAL, false);
        type = getIntent().getIntExtra(INTENT_TYPE, AppConstant.CREATE_TEXT_WEIBO);
    }

    protected void initView() {
        //地理位置
        tv_get_my_location = (TextView) findViewById(R.id.tv_get_my_location);
        tv_get_my_location.setOnClickListener(this);
        if (!needLocation()) {
            tv_get_my_location.setVisibility(View.GONE);
        }

        imageHs = (HorizontalScrollView) findViewById(R.id.imageHoriScroll);
        noScrollgridview = (GridView) findViewById(R.id.gv_preview);
        //拍照
        img_camera = (ImageView) findViewById(R.id.img_camera);
        if (!needPicture()) {
            imageHs.setVisibility(View.GONE);
            img_camera.setVisibility(View.GONE);
        }
        //视频预览图标
        preview = (ImageView) findViewById(R.id.iv_video_pre);
        videoPreview = (FrameLayout) findViewById(R.id.fl_video_pre);
        //视频
        img_video = (ImageView) findViewById(R.id.img_video);
        if (!needVideo()) {
            videoPreview.setVisibility(View.GONE);
            img_video.setVisibility(View.GONE);
        }

        //帖子标题
        stub_post_title = (ViewStub) findViewById(R.id.viewstub_post_title);
        if (needEditTitle()) {
            inflatePostTitle();
        }
        //文本内容框
        et_content = (EditText) findViewById(R.id.et_send_content);
        if(!TextUtils.isEmpty(content)) {
            setTextContent(content);
            //光标定位之最开始
            et_content.setSelection(0);
        }
        //@
        img_at = (ImageView) findViewById(R.id.img_at);
        //话题
        img_topic = (ImageView) findViewById(R.id.img_topic);
        //表情
        img_face = (ImageView) findViewById(R.id.img_face);
        //表情容器
        tFaceView = (ListFaceView) findViewById(R.id.face_view);
        tFaceView.initSmileView(et_content);
        this.setInputLimit();

        if (listener_selectImage == null)
            listener_selectImage = new SelectImageListener(this);


        popupWindowLocation = new PopupWindowLocation(this, findViewById(android.R.id.content));
        popupWindowLocation.setListener(this);
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

    //获取标题文本内容，一般只用于帖子发布
    protected String getEditTitle() {
        if (edit_post_title != null) {
            return edit_post_title.getText().toString().trim();
        }
        return "";
    }

    //设置标题框内容
    protected void setEditTitle(String title) {
        if (edit_post_title != null) {
            edit_post_title.setText(title);
            edit_post_title.setSelection(title.length());
        }
    }

    //是否需要地理位置视图
    protected boolean needLocation() {
        return true;
    }

    //是否需要标题框
    //默认不需要，只有在发表帖子的时候出现
    protected boolean needEditTitle() {
        return false;
    }

    //初始化控件监听事件
    protected void initListener() {
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

    protected void initData() {
        if (data == null) {
            data = new ModelWeibo();
        }
        setTextContent(content);
        //设置地理位置
        if (latitude > 0 && longitude > 0
                && TextUtils.isEmpty(address)) {
            tv_get_my_location.setText(address);
        }
    }

    /**
     * 设置正文内容
     *
     * @param content
     */
    protected void setTextContent(String content) {
        //设置内容
        et_content.setText(content);
        et_content.setSelection(content.length());
    }

    //图片选择不使用
    protected void unUsePhoto() {
        img_camera.setVisibility(View.GONE);
        imageHs.setVisibility(View.GONE);
    }

    //视频拍摄不使用
    protected void unUseVideo() {
        img_video.setVisibility(View.GONE);
        videoPreview.setVisibility(View.GONE);
        staticVideoPath = "";
    }

    //初始化图片控件
    private void initPicviews() {
        adapter = new SelectedImgGridAdapter(this, noScrollgridview);
        noScrollgridview.setAdapter(adapter);
        if (needPicture()) {
            //刷新照片列表
            adapter.update();
        }
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                SociaxUIUtils.hideSoftKeyboard(ActivityCreateBase.this,
                        et_content);
                if (arg2 == Bimp.address.size()) {
                    //选择图片
                    SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                    showSelectImagePopUpWindow(view);
                } else {
                    Intent intent = new Intent(ActivityCreateBase.this,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    //预览选择的照片
                    startActivityForResult(intent, StaticInApp.UPLOAD_WEIBO);
                }
            }
        });

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

    //选择相册
    private void selectPhoto() {
        Intent getImage = new Intent(this, MultiImageSelectorActivity.class);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
                Bimp.address);
        startActivityForResult(getImage, StaticInApp.LOCAL_IMAGE);
    }

    //录制视频
    private void recordVideo() {
        SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
        //跳转视频录制
        Intent intentVideo = new Intent(this, MediaRecorderActivity.class);
        startActivityForResult(intentVideo, AppConstant.CREATE_VIDEO_WEIBO);
        type = AppConstant.CREATE_VIDEO_WEIBO;
        Anim.in(this);
    }

    //设置输入字数长度限制
    private void setInputLimit() {
        TextView overWordCount = (TextView) findViewById(R.id.overWordCount);
        mWordCount = new WordCount(et_content, overWordCount);
        overWordCount.setText(String.valueOf(mWordCount.getMaxCount()));
        et_content.addTextChangedListener(mWordCount);
    }

    //标题框是否需要支持表情输入
    protected boolean editTitleNeedEmotion() {
        return false;
    }

    private ListFaceView.FaceAdapter mFaceAdapter = new ListFaceView.FaceAdapter() {

        @Override
        public void doAction(int paramInt, String paramString) {
            if (edit_post_title != null) {
                View rootView = ActivityCreateBase.this.getWindow().getDecorView();
                View focusView = rootView.findFocus();
                if (focusView.getId() == edit_post_title.getId()
                        && !editTitleNeedEmotion()) {
                    Toast.makeText(ActivityCreateBase.this, "标题不支持添加表情", Toast.LENGTH_SHORT).show();
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

            UnitSociax.showContentFaceView(ActivityCreateBase.this,
                    localSpannableStringBuilder);
            localEditDiggView.setText(localSpannableStringBuilder,
                    TextView.BufferType.SPANNABLE);
            localEditDiggView.setSelection(i + str1.length());
            Log.v("Tag", localEditDiggView.getText().toString());
        }
    };

    @Override
    protected void onRestart() {
        if (needPicture()) {
            adapter.update();
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v("testChannel", "/onResume/");

        if (needVideo()) {
            //刷新视频预览图
            resetVideo();
        }
        et_content.setFocusable(true);
        et_content.setFocusableInTouchMode(true);
        et_content.requestFocus();
        et_content.requestFocusFromTouch();

        InputMethodManager m = (InputMethodManager)
                et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    InputMethodManager m = (InputMethodManager)
//                            et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                    SociaxUIUtils.showftKeyborad(this, et_content);
//                }
//            }, 200);
    }

    @Override
    public void finish() {
        Log.v("testChannel", "/finish/");

        Bimp.address.clear();
        staticVideoPath = "";
        et_content.clearFocus();
        UnitSociax.hideSoftKeyboard(this, et_content);
        super.finish();
    }

    //是否需要保存草稿箱
    protected boolean needSaveDraft() {
        return true;
    }

    //是否需要拍照、选相册
    protected boolean needPicture() {
        return true;
    }

    //是否需要视频
    protected boolean needVideo() {
        return true;
    }

    //保存草稿
    private void saveDraft() {
        PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(this);
        builder.setMessage("保存草稿?", 18);
        builder.setTitle(null, 0);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                initDraft();
                addCacheDraft();
                finish();
            }
        });

        builder.setNegativeButton("不保存",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        builder.create();
    }

    //配置草稿箱内容
    protected void initDraft() {
        getDraft();
        mDraft.setType(type);
    }

    //设置草稿箱内容
    protected void getDraft() {
        if (mDraft == null) {
            mDraft = new ModelDraft();
        }

        content = getTextContent();
        mDraft.setContent(content);
        if (type == AppConstant.CREATE_ALBUM_WEIBO) {
            mDraft.setHasImage(true);
            //保存已选照片
            mDraft.setImageList(Bimp.address);
        } else if (type == AppConstant.CREATE_VIDEO_WEIBO) {
            mDraft.setHasVideo(true);
            mDraft.setVideoPath(staticVideoPath);
        }

        if (latitude > 0 && longitude > 0
                && !TextUtils.isEmpty(address)) {
            mDraft.setLatitude(latitude + "");
            mDraft.setLongitude(longitude + "");
            mDraft.setAddress(address);
        }

    }

    //获取文本框内容
    protected String getTextContent() {
        return et_content.getText().toString().trim();
    }

    //添加草稿至数据库
    protected void addCacheDraft() {
        Thinksns.getWeiboDraftSQL().addWeiboDraft(mDraft.getId() == -1, mDraft);
//                Thinksns.closeDb();
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
            img_video.setVisibility(View.VISIBLE);
            //不显示图片
            unUsePhoto();
        } else {
            videoPreview.setVisibility(View.GONE);
            img_camera.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取视频的预览图
     *
     * @param videoPath
     * @param width
     * @param height
     * @param kind
     * @return
     */
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
        private LayoutInflater inflater;        // 视图容器
        private Context mContext;
        private int selectedPosition = -1;      // 选中的位置
        private boolean shape;
        private GridView mgridViwew;

        private int imgWidth = 0;   //图片显示宽高
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
            if (Bimp.address.size() == 0) {
                count = 1;
            } else if (Bimp.address.size() == 9)
                count = 9;
            else {
                count = (Bimp.address.size() + 1);
            }

            //计算列表的宽度
            int gridviewWidth = count * imgWidth + (count - 1) * horizontalSpacing;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    gridviewWidth, gridViewHeight);
            mgridViwew.setLayoutParams(params);         // 重点
            mgridViwew.setNumColumns(count);

            return count;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            final int coord = position;
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgWidth, imgWidth);
            holder.image.setLayoutParams(params);

            if (position == Bimp.address.size()) {
//                holder.image.setImageBitmap(BitmapFactory.decodeResource(
//                        getResources(), R.drawable.icon_addpic_unfocused));
                holder.image.setImageResource(R.drawable.icon_addpic_unfocused);
            } else {
//                holder.image.setImageBitmap(Bimp.bmp.get(position));
                Glide.with(mContext)
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
            if (Bimp.address.size() == 0) {
                type = AppConstant.CREATE_TEXT_WEIBO;
                imageHs.setVisibility(View.GONE);
                if (needVideo())
                    img_video.setVisibility(View.VISIBLE);
            } else {
                type = AppConstant.CREATE_ALBUM_WEIBO;
                imageHs.setVisibility(View.VISIBLE);
                unUseVideo();
                adapter.notifyDataSetChanged();
            }

//            new Thread(new Runnable() {
//                public void run() {
//                    while (true) {
//                        if (Bimp.max == Bimp.address.size()) {
//                            //图片读取完成
//                            Message message = new Message();
//                            message.what = 1;
//                            handler.sendMessage(message);
//                            break;
//                        } else if (Bimp.max > Bimp.address.size()) {
//                            break;
//                        } else {
////                            try {
////                                String path = Bimp.address.get(Bimp.max);
////                                Bitmap bm = Bimp.revitionImageSize(path);
////                                Bimp.bmp.add(bm);
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
//                            photoList.add(Bimp.address.get(Bimp.max));
//                            Bimp.max += 1;
//                        }
//                    }
//                }
//            }).start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_camera:
                SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), et_content);
                showSelectImagePopUpWindow(v);
                break;
            case R.id.img_video:
                if (staticVideoPath != null && !staticVideoPath.equals("null") && !staticVideoPath.equals("")) {
                    showSelectVideoPopUpWindow(v);
                } else {
                    recordVideo();
                }
                break;
            case R.id.img_at:
                Intent intent = new Intent(this, ActivityAtUserSelect.class);
                startActivityForResult(intent, AT_REQUEST_CODE);
                break;
            case R.id.img_topic:
                Intent topicIntent = new Intent(this, AtTopicActivity.class);
                startActivityForResult(topicIntent, TOPIC_REQUEST_CODE);
                break;
            case R.id.img_face:
                if (tFaceView.getVisibility() == View.GONE) {
                    SociaxUIUtils.hideSoftKeyboard(this, et_content);
                    tFaceView.setVisibility(View.VISIBLE);
                    img_face.setImageResource(R.drawable.key_bar);
                } else if (tFaceView.getVisibility() == View.VISIBLE) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                    SociaxUIUtils.showSoftKeyborad(this,
                            et_content);
                }
                break;
            case R.id.et_send_content:
                if (tFaceView.getVisibility() == View.VISIBLE) {
                    tFaceView.setVisibility(View.GONE);
                    img_face.setImageResource(R.drawable.face_bar);
                    SociaxUIUtils.showSoftKeyborad(this,
                            et_content);
                }
                break;
            case R.id.tv_get_my_location:
                if (v.getTag() == null) {
                    startActivityForResult(new Intent(this, ActivityGetMyLocation.class), GET_LOCATION);
                } else {
                    popupWindowLocation.show();
                }
                break;
            case R.id.fl_video_pre:
                Intent videoIntent = new Intent(this, ActivityVideoDetail.class);
                videoIntent.putExtra("url", staticVideoPath);
                startActivity(videoIntent);
                break;
            default:
                break;
        }
    }

    //检验内容发布的合格性
    protected boolean checkDataReady() {
        content = getContent();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!mWordCount.inputValid()) {
            Toast.makeText(this, "内容长度不能超过" + mWordCount.getMaxCount(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 视频点击图标弹出菜单
     *
     * @param v
     */
    private void showSelectVideoPopUpWindow(final View v) {
        final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(v.getContext());
        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // 清除视频
                    staticVideoPath = "";
                    UnitSociax.hideSoftKeyboard(ActivityCreateBase.this, et_content);
                    resetVideo();
                } else if (position == 1) {
                    recordVideo();
                } else {
                    builder.dimss();
                }
            }
        });

        List<String> datas = new ArrayList<String>();
        datas.add("删除");
        datas.add("重拍");
        datas.add("取消");
        builder.create(datas);
    }

    // 发布微博
    private void submitWeibo(View view) {
        if (!UnitSociax.isNetWorkON(this)) {
            Toast.makeText(this, "请检查网络设置", Toast.LENGTH_SHORT).show();
            return;
        }

        //执行发布操作
        if (checkDataReady()) {
            startUploadService(getUploadIntent());
            finish();
            Anim.exit(this);
        }

        //按钮可点击
        view.setEnabled(true);
    }

    //设置文章内容
    protected String getContent() {
        String content = et_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            if (type == AppConstant.CREATE_TEXT_WEIBO) {
            } else if (type == AppConstant.CREATE_ALBUM_WEIBO) {
                content = "分享图片";
            } else if (type == AppConstant.CREATE_VIDEO_WEIBO) {
                content = "分享视频";
            }
        }

        return content;
    }

    //设置发布前的Intent
    protected Intent getUploadIntent() {
        Intent intent = new Intent(Thinksns.getContext(), ServiceUploadWeibo.class);
        Bundle bundle = new Bundle();
        if (type == AppConstant.CREATE_ALBUM_WEIBO) {
            bundle.putString(INTENT_TIPS, "正在上传图片...");
            bundle.putBoolean(INTENT_ORIGINAL, isOriginal);
            //将所选图片拼接成字符串,否则在草稿类集成后无法传递到Service
            String imageStr = "";
            for (String str : Bimp.address) {
                imageStr += str;
                imageStr += ",";
            }
            bundle.putString(INTENT_IAMGE_LIST, imageStr);
        } else if (type == AppConstant.CREATE_VIDEO_WEIBO) {
            bundle.putString(INTENT_TIPS, "正在上传视频");
            bundle.putString(INTENT_VIDEO_PATH, staticVideoPath);
        }

        intent.putExtras(bundle);

        return intent;
    }

    //开启服务后台发送文章
    protected void startUploadService(Intent intent) {
        //创建草稿箱
        initDraft();
        //封装发送的内容
        packageData();
        intent.putExtra(INTENT_TYPE, type);
        intent.putExtra(INTENT_DATA, data);
        intent.putExtra(INTENT_DRAFT, mDraft);
        startService(intent);
    }

    //打包公共数据
    protected void packageData() {
        if (data instanceof ModelWeibo) {
            if (latitude != 0 && longitude != 0) {
                ((ModelWeibo) data).setLatitude(String.valueOf(latitude));
                ((ModelWeibo) data).setLongitude(String.valueOf(longitude));
                ((ModelWeibo) data).setAddress(address);
            }

            ((ModelWeibo) data).setContent(getTextContent());
        }
    }

    @Override
    public void onReLocationClick() {

    }

    @Override
    public void onDelLocationClick() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AT_REQUEST_CODE:
                    if (data != null) {
                        et_content.append("@" + data.getStringExtra("at_name") + " ");
                        et_content.setSelection(et_content.length());
                    }
                    break;
                case TOPIC_REQUEST_CODE:
                    if (data != null) {
                        et_content.append("#" + data.getStringExtra("recent_topic") + "#");
                        et_content.setSelection(et_content.length());
                    }
                    break;
                case StaticInApp.CAMERA_IMAGE:
                    String path = listener_selectImage.getImagePath();
                    if (path != null
                            && Bimp.address.size() < 9) {
                        Bimp.address.add(path);
                        imageHs.setVisibility(View.VISIBLE);
                    }

                    isOriginal = false;             //默认对拍照的图片压缩
//                    setMedia(HAS_IMAGE);
                    break;
                case StaticInApp.LOCAL_IMAGE:
                    List<String> photoList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    boolean original = data.getBooleanExtra(MultiImageSelectorActivity.EXTRA_SELECT_ORIGIANL, false);

                    for (String addr : photoList) {
                        if (Bimp.address.size() == 9)
                            break;
                        if (!Bimp.address.contains(addr)) {
                            Bimp.address.add(addr);
                        }
                    }

                    isOriginal = original;
                    break;
                case GET_LOCATION:
                    address = data.getStringExtra("address");
                    latitude = data.getDoubleExtra("latitude", 0);
                    longitude = data.getDoubleExtra("longitude", 0);
                    if (!TextUtils.isEmpty(address)) {
                        tv_get_my_location.setText(address);
                    }
                    break;
                case AppConstant.CREATE_VIDEO_WEIBO:
                    //视频拍摄
                    break;
            }
        }
//        if (requestCode == StaticInApp.UPLOAD_WEIBO) {
//            if (Bimp.address.size() == 0) {
//                img_video.setVisibility(View.VISIBLE);
//            }
//        }
    }

}
