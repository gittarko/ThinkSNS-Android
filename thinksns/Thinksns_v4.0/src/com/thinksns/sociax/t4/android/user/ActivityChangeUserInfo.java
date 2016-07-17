package com.thinksns.sociax.t4.android.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.img.RoundImageView;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowSelectImage;
import com.thinksns.sociax.t4.android.temp.SelectImageListener;
import com.thinksns.sociax.t4.android.widget.roundimageview.RoundedImageView;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.Region;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.t4.unit.UriUtils;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.unit.Compress;
import com.thinksns.tschat.widget.UIImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 类说明：
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-10
 */
public class ActivityChangeUserInfo extends ThinksnsAbscractActivity {
    LinearLayout ll_uploadFace, ll_change_name, ll_change_city, ll_change_intro, ll_change_sex, ll_change_tag;
    TextView tv_intro, tv_uname, tv_city, tv_sex, tv_tag;
    TextView tv_score;
    ImageView img_level;
    RoundImageView tv_face;
    TextView tv_uploadFace;

    private SelectImageListener changeListener;
    public int UPLOAD_FACE = 4;
    private Bitmap newHeader;
    private SmallDialog smallDialog;
    private UIHandler uiHandler;// 处理ui县城
    private String input;
    protected BroadcastReceiver updateTag;
    private ModelUser user = Thinksns.getMy();
    private String city_name;
    private File cameraFile;// 相片文件

    private boolean isChanged = false;
    Thinksns app = null;
    private int uid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntentData();
        initView();
        initOnClickListener();
        initData();
    }

    private void initData() {
        tv_uname.setText(user.getUserName());
        String location = user.getLocation();
        if (location == null || location.isEmpty() || location.equals("null"))
            location = "来自星星的你";
        tv_city.setText(location);

        if (user.getIntro() == null || user.getIntro().isEmpty())
            tv_intro.setText("这个人很懒，什么也没留下");
        else
            tv_intro.setText(user.getIntro());

        UIImageLoader.getInstance(this).displayImage(user.getUserface(), tv_face);
        tv_score.setText(user.getUserCredit() == null ? "" : user.getUserCredit().getScore_value());
        //个人标签
        String tags = user.getUserTag();
        if (tags != null) {
            tv_tag.setText(tags.replaceAll("、", "  "));
        }

        if (user.getUserLevel() != null) {
            img_level.setVisibility(View.VISIBLE);
            img_level.setImageResource(UnitSociax.getResId(this, "icon_level" + user.getUserLevel().getLevel(), "drawable"));
        } else {
            img_level.setVisibility(View.GONE);
        }

        tv_sex.setText(user.getSex());
    }

    private void initOnClickListener() {
        ll_uploadFace.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(v.getContext());
                builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0) {
                            //相册选择
                            Intent getImage = new Intent(v.getContext(), MultiImageSelectorActivity.class);
                            getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                            getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                            getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
                            startActivityForResult(getImage, StaticInApp.LOCAL_IMAGE);
                        }else if(position == 1) {
                            //拍摄图片
                            selectPicFromCamera();
                        }else {
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
        });
        ll_change_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityEditInfo.CENTER_TITLE = "修改昵称";
                Intent intent = new Intent(ActivityChangeUserInfo.this, ActivityEditInfo.class);
                intent.putExtra("type", StaticInApp.CHANGE_USER_NAME);
                startActivityForResult(intent, StaticInApp.CHANGE_USER_NAME);
            }
        });
        ll_change_intro.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityChangeUserInfo.this, ActivityEditInfo.class);
                ActivityEditInfo.CENTER_TITLE = "修改简介";
                intent.putExtra("type", StaticInApp.CHANGE_USER_INTRO);
                startActivityForResult(intent, StaticInApp.CHANGE_USER_INTRO);
            }
        });
        ll_change_city.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityChangeUserInfo.this, ActivityEditLocationInfo.class);
                startActivityForResult(intent, StaticInApp.CHANGE_USER_CITY);
            }
        });
        //修改性别
        ll_change_sex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityChangeUserInfo.this, ActivityChangeSex.class);
                startActivityForResult(intent, StaticInApp.CHANGE_USER_SEX);
            }
        });
        ll_change_tag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择标签
                Intent intent = new Intent(ActivityChangeUserInfo.this, ActivityMyTag.class);
//				startActivity(intent);
                startActivityForResult(intent, StaticInApp.CHANGE_MY_TAG);
            }
        });
    }

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
                StaticInApp.CAMERA_IMAGE);
    }

    private void initIntentData() {
        initReceiver();
        Intent intent = getIntent();
        if (intent != null) {
            uid = intent.getIntExtra("uid", -1);
        }
    }

    private void initView() {

        ll_change_city = (LinearLayout) findViewById(R.id.ll_change_city);
        ll_change_name = (LinearLayout) findViewById(R.id.ll_change_name);
        ll_uploadFace = (LinearLayout) findViewById(R.id.ll_uploadFace);
        ll_change_intro = (LinearLayout) findViewById(R.id.ll_change_intro);
        ll_change_sex = (LinearLayout) findViewById(R.id.ll_change_sex);
        ll_change_tag = (LinearLayout) findViewById(R.id.ll_change_tag);
        tv_uploadFace = (TextView) findViewById(R.id.tv_uploadFace);

        tv_face = (RoundImageView) findViewById(R.id.tv_face);
        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_tag = (TextView) findViewById(R.id.tv_tag);
        img_level = (ImageView) findViewById(R.id.img_level);

        changeListener = new SelectImageListener(ActivityChangeUserInfo.this, tv_uploadFace);

        smallDialog = new SmallDialog(this, getString(R.string.please_wait));

        uiHandler = new UIHandler();

        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_intro = (TextView) findViewById(R.id.tv_intro);
        tv_uname = (TextView) findViewById(R.id.tv_uname);
    }

    public void initReceiver() {
        updateTag = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(StaticInApp.UPDATE_USER_HOME_TAG)) {
                    ModelUser modelUser = (ModelUser) intent.getSerializableExtra("user");
                    tv_tag.setText(modelUser.getUserTag().replaceAll("、", "  "));
                }
            }
        };

        IntentFilter filter_update_tag = new IntentFilter();
        filter_update_tag.addAction(StaticInApp.UPDATE_USER_HOME_TAG);
        registerReceiver(updateTag, filter_update_tag);
    }

    @Override
    public OnClickListener getLeftListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChanged) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateTag);
    }

    @Override
    public String getTitleCenter() {
        return "基本信息";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_userinfo;
    }

    Bitmap btp = null;
    String selectPath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isChanged = true;
            switch (requestCode) {
                case StaticInApp.CAMERA_IMAGE:
                    if (cameraFile != null && cameraFile.exists()) {
                        String cameraPath = cameraFile.getAbsolutePath();
                        changeListener.setImagePath(cameraPath);
                        changeListener.startPhotoZoom(Uri.fromFile(new File(cameraPath)), 0, 0);
                    }
                    break;
                case StaticInApp.LOCAL_IMAGE:
                    List<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (list != null && list.size() > 0) {
                        selectPath = list.get(0);
                        changeListener.setImagePath(selectPath);
                        changeListener.startPhotoZoom(UriUtils.pathToUri(this, selectPath), 0, 0);
                    }
                    break;
                case StaticInApp.ZOOM_IMAGE:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            btp = extras.getParcelable("data");
                            loadFaceThread();
                        }
                    } else {
                        Log.d(AppConstant.APP_TAG, "data is null  .... ");
                    }
                    break;
                case StaticInApp.CHANGE_USER_NAME:
                    input = data.getStringExtra("input");
                    if (input == null) {
                    } else {
                        saveUserInfoThread(StaticInApp.CHANGE_USER_NAME, input);
                    }
                    break;
                case StaticInApp.CHANGE_USER_CITY:
                    String[] abbrIds = data.getStringArrayExtra(ActivityEditLocationInfo.EXTRA_ABBR_IDS);
                    String[] abbrNames = data.getStringArrayExtra(ActivityEditLocationInfo.EXTRA_ABBR_NAMES);
                    for (int i = abbrIds.length - 1; i >= 0; --i) {
                        if (!TextUtils.isEmpty(abbrIds[i])) {
                            input = abbrIds[i];
                            break;
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    for (String s : abbrNames) {
                        if (!TextUtils.isEmpty(s)) {
                            sb.append(s).append(" ");
                        }
                    }
                    city_name = sb.toString();
                    if (input != null) {
                        saveUserInfoThread(StaticInApp.CHANGE_USER_CITY, input);
                    }
                    break;
                case StaticInApp.CHANGE_USER_INTRO:

                    input = data.getStringExtra("input");
                    if (input == null) {
                    } else {
                        saveUserInfoThread(StaticInApp.CHANGE_USER_INTRO, input);
                    }
                    break;
                case StaticInApp.CHANGE_USER_SEX:
                    //修改性别
                    input = data.getStringExtra("input");
                    if (input == null) {
                    } else {
                        saveUserInfoThread(StaticInApp.CHANGE_USER_SEX, input);
                    }
                    break;
                case StaticInApp.CHANGE_MY_TAG:

                    ModelUser userTag = (ModelUser) data.getSerializableExtra("user");
                    if (userTag != null) {
                        tv_tag.setText(userTag.getUserTag().replaceAll("、", "  "));
                    }
                    break;
            }
        }
    }

    // ***************修改信息*****************//
    private void saveUserInfoThread(final int changetype, final String input) {
        smallDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                Thinksns app = (Thinksns) getApplication();
                Message msg = uiHandler.obtainMessage();
                msg.what = changetype;
                Object result = false;
                try {
                    result = app.getUsers().saveUserInfo(changetype, input, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    smallDialog.dismiss();
                }
                msg.obj = result;
                uiHandler.sendMessage(msg);
            }
        }).start();
    }

    // ***************商船照片*****************//
    private void loadFaceThread() {
        smallDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                Thinksns app = (Thinksns) getApplication();
                Message msg = uiHandler.obtainMessage();
                msg.what = UPLOAD_FACE;
                Object result = false;
                try {
                    result = app.getApi().changeFace(btp,
                            new File(changeListener.getImagePath()));
                } catch (ApiException e) {
                    e.printStackTrace();
                    smallDialog.dismiss();
                }
                msg.obj = result;
                uiHandler.sendMessage(msg);
            }
        }).start();
    }

    private Bitmap checkImage(Intent data) {
        if (changeListener == null)
            changeListener = new SelectImageListener(
                    ActivityChangeUserInfo.this, tv_uploadFace);
        Bitmap bitmap = null;
        try {
            Uri originalUri = data.getData();
            String path = UriUtils.uriToPath(this, originalUri);
            // path = path.substring(path.indexOf("/sdcard"), path.length());
            Log.d(TAG, "imagePath" + path);
            bitmap = Compress.compressPicToBitmap(new File(path));
            if (bitmap != null) {
                changeListener.setImagePath(path);
            }

        } catch (Exception e) {
            Log.e("checkImage", e.getMessage());
        } finally {
            newHeader = bitmap;
            return bitmap;
        }
    }

    class UIHandler extends Handler {

        Thinksns app = (Thinksns) getApplicationContext();

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPLOAD_FACE) {
                try {
                    JSONObject jsonResult = new JSONObject(msg.obj.toString());
                    int status = jsonResult.getInt("status");
                    if (status == 0) {
                        Toast.makeText(ActivityChangeUserInfo.this,
                                "更换头像失败", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        String faceUrl = jsonResult.getJSONObject("data").getString("middle");
                        user.setFace(faceUrl);
                        Thinksns app = (Thinksns) getApplicationContext();
                        int i = app.getUserSql().updateUserFace(user);
                        //显示头像
                        tv_face.setImageBitmap(btp);
                        tv_uploadFace.setText("重新上传");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityChangeUserInfo.this,
                            R.string.upload_false, Toast.LENGTH_LONG).show();
                }
                smallDialog.dismiss();
            } else if (msg.what == StaticInApp.CHANGE_USER_INTRO) {

                try {
                    JSONObject jsonResult = new JSONObject(msg.obj.toString());
                    int status = jsonResult.getInt("status");
                    if (status == 0) {
                        Toast.makeText(ActivityChangeUserInfo.this,
                                jsonResult.getString("msg"), Toast.LENGTH_LONG)
                                .show();
                    } else {
                        user.setIntro(input);
                        tv_intro.setText(input);
                        Thinksns app = (Thinksns) getApplicationContext();
                        int i = app.getUserSql().updateUser(user);
                        Toast.makeText(ActivityChangeUserInfo.this, jsonResult.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(ActivityChangeUserInfo.this, "操作失败",
                            Toast.LENGTH_LONG).show();
                }
                smallDialog.dismiss();
            } else if (msg.what == StaticInApp.CHANGE_USER_NAME) {
                try {
                    JSONObject jsonResult = new JSONObject(msg.obj.toString());
                    int status = jsonResult.getInt("status");
                    if (status == 0) {
                        Toast.makeText(ActivityChangeUserInfo.this,
                                jsonResult.getString("msg"), Toast.LENGTH_LONG)
                                .show();
                    } else {
                        user.setUserName(input);
                        tv_uname.setText(input);
                        Thinksns app = (Thinksns) getApplicationContext();
                        int i = app.getUserSql().updateUser(user);
                        Toast.makeText(ActivityChangeUserInfo.this,
                                jsonResult.getString("msg"), Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(ActivityChangeUserInfo.this, "操作失败",
                            Toast.LENGTH_LONG).show();
                }
                smallDialog.dismiss();

            } else if (msg.what == StaticInApp.CHANGE_USER_SEX) {

                try {
                    JSONObject jsonResult = new JSONObject(msg.obj.toString());
                    int status = jsonResult.getInt("status");
                    if (status == 0) {
                        Toast.makeText(ActivityChangeUserInfo.this,
                                jsonResult.getString("msg"), Toast.LENGTH_LONG)
                                .show();
                    } else {
                        user.setSex(input);
                        tv_sex.setText(input);
                        Thinksns app = (Thinksns) getApplicationContext();
                        int i = app.getUserSql().updateUser(user);
                        Toast.makeText(ActivityChangeUserInfo.this, jsonResult.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(ActivityChangeUserInfo.this, "操作失败",
                            Toast.LENGTH_LONG).show();
                }
                smallDialog.dismiss();
            } else if (msg.what == StaticInApp.CHANGE_USER_CITY) {
                try {
                    JSONObject jsonResult = new JSONObject(msg.obj.toString());
                    int status = jsonResult.getInt("status");
                    if (status == 0) {
                        Toast.makeText(ActivityChangeUserInfo.this,
                                jsonResult.getString("msg"), Toast.LENGTH_LONG)
                                .show();
                    } else {

//						// 修改成功之后调用show修改用户
//						new Thread(new Runnable() {
//
//							@Override
//							public void run() {
//								Message msg1 = new Message();
//								msg1.what = StaticInApp.SHOW_USER;
//								try {
//									user = app.getUsers().show(user);
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//								uiHandler.sendMessage(msg1);
//							}
//						}).start();
                        user.setLocation(city_name);
                        tv_city.setText(city_name);
                        int i = app.getUserSql().updateUser(user);
                        Thinksns.getMy().setLocation(user.getLocation());
                        Toast.makeText(ActivityChangeUserInfo.this, "修改成功", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(ActivityChangeUserInfo.this, "操作失败",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == StaticInApp.SHOW_USER) {
                tv_city.setText(user.getLocation());
                int i = app.getUserSql().updateUser(user);
                Thinksns.getMy().setLocation(user.getLocation());
                Toast.makeText(ActivityChangeUserInfo.this, "修改成功", Toast.LENGTH_LONG).show();
            }
            smallDialog.dismiss();
        }
    }
}
