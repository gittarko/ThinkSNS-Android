package com.thinksns.sociax.t4.android.login;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.user.ActivityChangeUserInfo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.function.FunctionVerifyCode;
import com.thinksns.sociax.t4.android.img.RoundImageView;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowSelectImage;
import com.thinksns.sociax.t4.android.temp.SelectImageListener;
import com.thinksns.sociax.t4.android.user.ActivityEditLocationInfo;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.t4.model.ModelAreaInfo;
import com.thinksns.sociax.t4.model.ModelRegister;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.Region;
import com.thinksns.sociax.t4.unit.PrefUtils;
import com.thinksns.sociax.t4.unit.UriUtils;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.unit.Compress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 类说明： 注册类
 * 第一步：填写手机号并验证手机短信 第二步：填写用户基本资料 第三部：选择标签 第四步：好友推荐
 * 第一步与第二步UI布局使用同一个页面，通过doStep决定显示第一步或第二步
 *
 * @author wz
 * @version 1.0
 * @date 2014-9-5
 */

public class ActivityRegister extends ThinksnsAbscractActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPasswd, ed_intro;
    private RadioGroup rgSex;
    private RadioButton rbMan;  // 选择框
    private RadioButton rbWoman;    // 选择框
    private Button bt_next_step;    // 下一个步按钮
    private UIHandler uiHandler;    // 处理ui县城
    private LinearLayout ll_step_one, ll_step_two, ll_city;
    private TextView tv_city;

    private boolean hasImage = false;
    private int FAILED = 0;         // 失败
    private int SUCCESS = 1;        // 请求中
    private TextView tv_getVerify, tv_uploadFace;
    private RoundImageView tv_face;                    // 点击获取验证码
    private EditText ed_phone, ed_verifycode;// 第一步输入框
    private EditText ed_name, ed_pwd;
    private boolean isOauthVerifycodeSuccess = false;// 是否已经成功发送验证码
    public int UPLOAD_FACE = 4;
    private int STEP_ONE = 5, STEP_TWO = 6,
            OAUTH_CODE = 7, REGISTER = 8;           // 第一步第二步
    private SelectImageListener changeListener;

    private int doWhat = STEP_ONE;
    private String sex, faceUrl, faceWidth, faceHeight;

    private SmallDialog smallDialog;

    private String[] thirdUserData;
    private String[] abbrIds;
    private boolean isThirdReg = false;

    private ApiHttpClient.HttpResponseListener cityListener;

    SelectImageListener listener;

    public enum REQUEST_TYPE {
        REQUEST_PROVINCE, REQUEST_CITY, REQUEST_ZONE
    }

    private REQUEST_TYPE mRequestType = REQUEST_TYPE.REQUEST_PROVINCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isThirdReg = getIntent().hasExtra("reg_data");
        thirdUserData = getIntent().getStringArrayExtra("reg_data");

        smallDialog = new SmallDialog(this, getString(R.string.please_wait));
        smallDialog.setCanceledOnTouchOutside(false);
        uiHandler = new UIHandler();

        initView();
        initListener();
        initHandler();
        initData();
    }


    private void initHandler() {
        cityListener = new ApiHttpClient.HttpResponseListener() {
            @Override
            public void onSuccess(Object result) {
                List<ModelAreaInfo> list = (List<ModelAreaInfo>) result;
                if (list == null)
                    return;
                if (list.size() > 0) {
                    ActivitySelectCity.fillCityData(mRequestType, list);
                }
                if (mRequestType == REQUEST_TYPE.REQUEST_PROVINCE) {
                    mRequestType = REQUEST_TYPE.REQUEST_CITY;

                } else if (mRequestType == REQUEST_TYPE.REQUEST_CITY) {
                    mRequestType = REQUEST_TYPE.REQUEST_ZONE;
                }

                if (list.size() >= 0)
                    getArea(Integer.parseInt(list.get(0).getArea_id()));

            }

            @Override
            public void onError(Object result) {

            }
        };
    }

    private void initData() {
        //获取地区列表
//        getArea(0);  //后期为了实现当前页面获取地区添加在此
    }

    private void getArea(int pid) {
        new Api.Users().getAreaById(pid, cityListener);
    }

    private void initListener() {
        setKeyBoardStyle();
        tv_face.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                new PopupWindowSelectImage(ActivityRegister.this,
//                        findViewById(android.R.id.content), changeListener);
                showSelectImgPopUpWindow(v);
            }
        });
        tv_getVerify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv_getVerify.getWindowToken(), 0);
                FunctionVerifyCode verify = new FunctionVerifyCode(
                        ActivityRegister.this, ed_phone, tv_getVerify);
                if (verify.checkPhoneNumber()) {
                    verify.getRegisterVerify();
                }
            }
        });

        bt_next_step.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv_getVerify.getWindowToken(), 0);
                if (doWhat == STEP_ONE) {
                    doStepOne();
                } else {
                    doStepTwo();
                }
            }
        });

        //选择地区
        ll_city.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRegister.this, ActivityEditLocationInfo.class);
//                startActivityForResult(intent, StaticInApp.CHANGE_USER_CITY);
//                Intent intent = new Intent(v.getContext(), ActivitySelectCity.class);
                //传入默认地区首选项
                startActivityForResult(intent, StaticInApp.CHANGE_USER_CITY);
            }
        });
    }

    /**
     * 点击图标弹出菜单选择图片的菜单
     *
     * @param v
     */
    private void showSelectImgPopUpWindow(final View v) {
        final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(v.getContext());
        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent getImage = new Intent(ActivityRegister.this, MultiImageSelectorActivity.class);
                    getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                    getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                    getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                    getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
                    startActivityForResult(getImage, StaticInApp.LOCAL_IMAGE);
                } else if (position == 1) {
                    changeListener.cameraImage();
                } else {
                    builder.dimss();
                }
            }
        });

        List<String> datas = new ArrayList<String>();
        datas.add("本地");
        datas.add("拍照");
        datas.add("取消");
        builder.create(datas);
    }

    /**
     * 执行获取验证码
     */
    protected void doStepOne() {
        if (ed_verifycode.length() == 0) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        FunctionVerifyCode verify = new FunctionVerifyCode(ed_phone,
                ed_verifycode, isOauthVerifycodeSuccess,
                ActivityRegister.this);
        if (verify.checkPhoneNumber()) {
            checkVerifyCode();
        }
    }

    /**
     * 执行完成
     */
    protected void doStepTwo() {
        if (ed_name.getText().toString().trim().length() == 0
                || ed_pwd.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "昵称或密码不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tv_city.getText().toString().trim()) || abbrIds.length == 0) {
            Toast.makeText(this, "请选择地址", Toast.LENGTH_SHORT).show();
        }
        if (faceUrl == null || faceHeight == null || faceWidth == null) {
            Toast.makeText(this, "请上传头像", Toast.LENGTH_SHORT).show();
            return;
        }

        t4register();
    }

    /**
     * 注册线程
     */
    private void t4register() {
        smallDialog.setContent("请稍后...");
        smallDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Thinksns app = (Thinksns) getApplication();
                ModelRegister userInfo = new ModelRegister();
                userInfo.setUsername(ed_name.getText().toString().trim());
                userInfo.setPassword(ed_pwd.getText().toString().trim());
                userInfo.setSex(Integer.parseInt(sex));
                userInfo.setPhone(ed_phone.getText().toString().trim());
                userInfo.setCode(Integer.parseInt(ed_verifycode.getText().toString().trim()));
                userInfo.setAvatarUrl(faceUrl);
                userInfo.setAvatarH(Integer.parseInt(faceWidth));
                userInfo.setAvatarW(Integer.parseInt(faceHeight));
                userInfo.setLocation(tv_city.getText().toString().trim());

                String intro = ed_intro.getText().toString().trim();

                if (intro != null && !intro.equals("null") && !intro.equals("")) {
                    userInfo.setIntro(intro);
                } else {
                    userInfo.setIntro("");
                }

                userInfo.setProvince(Integer.parseInt(abbrIds[Region.PROVINCE.ordinal()]));
                if(!TextUtils.isEmpty(abbrIds[Region.CITY.ordinal()]))
                    userInfo.setCity(Integer.parseInt(abbrIds[Region.CITY.ordinal()]));
                if (!TextUtils.isEmpty(abbrIds[Region.AREA.ordinal()]))
                    userInfo.setArea(Integer.parseInt(abbrIds[Region.AREA.ordinal()]));
                try {
                    Message msg = uiHandler.obtainMessage();
                    JSONObject jo = new JSONObject(app.getOauth().signIn(userInfo).toString());
                    msg.arg1 = REGISTER;
                    msg.obj = jo;
                    uiHandler.sendMessage(msg);
                } catch (Exception e) {
                    Log.d(this.getClass().toString(), e.toString());
                }
            }
        }).start();
        smallDialog.show();
    }

    /**
     * 检验验证码线程
     */
    protected void checkVerifyCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thinksns app = (Thinksns) getApplication();
                try {
                    Message msg = uiHandler.obtainMessage();
                    msg.arg1 = OAUTH_CODE;
                    JSONObject jo = new JSONObject(app
                            .getOauth().oauthRegisterVerifyCode(
                                    ed_phone.getText().toString(),
                                    ed_verifycode.getText().toString())
                            .toString());
                    msg.what = jo.getInt("status"); // //成功：1 失败：0
                    msg.obj = jo.getString("msg"); // 邮箱不合格：2
                    uiHandler.sendMessage(msg);
                } catch (Exception e) {
                    Log.d(this.getClass().toString(), e.toString());
                }
            }
        }).start();
        smallDialog.show();
    }

    private void initView() {
        bt_next_step = (Button) findViewById(R.id.bt_next_step);
        ll_step_one = (LinearLayout) findViewById(R.id.ll_step_one);
        ll_step_two = (LinearLayout) findViewById(R.id.ll_step_two);
        tv_getVerify = (TextView) findViewById(R.id.tv_getVerify);
        ed_phone = (EditText) findViewById(R.id.ed_phone);
        ed_verifycode = (EditText) findViewById(R.id.ed_verifycode);
        tv_uploadFace = (TextView) findViewById(R.id.tv_uploadFace);
        changeListener = new SelectImageListener(ActivityRegister.this,
                tv_uploadFace);
        tv_face = (RoundImageView) findViewById(R.id.tv_face);

        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_pwd = (EditText) findViewById(R.id.ed_password);

        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPasswd = (EditText) findViewById(R.id.et_passwd);

        rgSex = (RadioGroup) findViewById(R.id.rg_sex);
        rbMan = (RadioButton) findViewById(R.id.rb_man);
        rbWoman = (RadioButton) findViewById(R.id.rb_woman);

        ll_city = (LinearLayout) findViewById(R.id.ll_city);
        tv_city = (TextView) findViewById(R.id.tv_city);
        ed_intro = (EditText) findViewById(R.id.ed_intro);

        sex = ((RadioButton) findViewById(rgSex.getCheckedRadioButtonId())).getTag().toString();
        rgSex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sex = ((RadioButton) findViewById(checkedId)).getTag().toString();
            }
        });


        TextView passTv = (TextView) findViewById(R.id.tv_pass);
        if (isThirdReg) {
            etName.setText(thirdUserData[0]);
            etEmail.setBackgroundResource(R.drawable.reg_buttom_bg);
//			if (thirdUserData[1].equals("1"))
//				rbMan.setChecked(true);
//			else
//				rbWoman.setChecked(true);

            etPasswd.setVisibility(View.GONE);
            passTv.setVisibility(View.GONE);
        }
    }

    @Override
    public String getTitleCenter() {
        return getString(R.string.register);
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == OAUTH_CODE) {
                if (msg.what == SUCCESS) {
                    // 验证成功
                    ll_step_one.setVisibility(View.GONE);
                    ll_step_two.setVisibility(View.VISIBLE);
                    doWhat = STEP_TWO;
                } else {
                    Toast.makeText(ActivityRegister.this, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            } else if (msg.arg1 == REGISTER) {
//                msg.what = jo.getInt("status"); // //成功：1 失败：0
//                msg.obj = jo.getString("msg"); // 邮箱不合格：2
                JSONObject jo = (JSONObject) msg.obj;
                if (jo != null) {
                    try {
                        int status = jo.getInt("status");
                        if (status == 1) {
                            new Api.Oauth().authorize(ed_phone.getText().toString(), ed_pwd.getText().toString(), mListener);
                        } else {
                            String resultMsg = "";
                            if (jo.has("message")) {
                                resultMsg = jo.getString("message");
                            } else {
                                resultMsg = jo.getString("msg");
                            }
                            Toast.makeText(ActivityRegister.this, resultMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                smallDialog.dismiss();
            } else if (msg.arg1 == UPLOAD_FACE) {
                if (msg.obj != null) {
                    try {
                        JSONObject jsonResult = new JSONObject(msg.obj.toString());
                        int status = jsonResult.getInt("status");
                        if (status == 0) {
                            Toast.makeText(ActivityRegister.this,
                                    R.string.upload_false, Toast.LENGTH_LONG).show();
                        } else {
                            faceUrl = jsonResult.getJSONObject("data").getString(
                                    "picurl");
                            faceWidth = jsonResult.getJSONObject("data").getString(
                                    "picwidth");
                            faceHeight = jsonResult.getJSONObject("data")
                                    .getString("picheight");
                            //显示图片
                            tv_face.setImageBitmap(btp);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ActivityRegister.this, R.string.upload_false, Toast.LENGTH_LONG).show();
                    }
                }

            }

            smallDialog.dismiss();
        }
    }

    final ApiHttpClient.HttpResponseListener mListener = new ApiHttpClient.HttpResponseListener() {

        @Override
        public void onSuccess(final Object result) {
            if (result instanceof ModelUser) {
                ModelUser authorizeResult = (ModelUser) result;
                new Api.Users().show(authorizeResult, this);
            } else if (result instanceof ListData<?>) {
                ListData<SociaxItem> list = (ListData<SociaxItem>) result;
                if (list != null && list.size() == 1) {
                    ModelUser loginedUser = (ModelUser) list.get(0);
                    //保存用户信息
                    Thinksns.setMy(loginedUser);
                    UserSqlHelper db = UserSqlHelper.getInstance(ActivityRegister.this);
                    db.addUser(loginedUser, true);
                    String username = loginedUser.getUserName();
                    if (!db.hasUname(username))
                        db.addSiteUser(username);
                    Intent intent = new Intent(ActivityRegister.this, ActivityHome.class);
                    intent.putExtra("new_user", true);
                    startActivity(intent);
                    //关闭首页
                    ThinksnsActivity.getInstance().finish();
                    finish();
                }
            }

        }

        @Override
        public void onError(Object result) {
            startActivity(new Intent(ActivityRegister.this, ActivityLogin.class));
            finish();
        }
    };

    //修改软键盘样式
    public void setKeyBoardStyle() {
        UnitSociax.setSoftKeyBoard(ed_phone, this);
        UnitSociax.setSoftKeyBoard(ed_verifycode, this);
        UnitSociax.setSoftKeyBoard(ed_name, this);
        UnitSociax.setSoftKeyBoard(ed_pwd, this);
        UnitSociax.setSoftKeyBoard(etName, this);
        UnitSociax.setSoftKeyBoard(etEmail, this);
        UnitSociax.setSoftKeyBoard(etPasswd, this);
        UnitSociax.setSoftKeyBoard(ed_intro, this);
    }

    // ***************照片处理*****************//

    private Bitmap checkImage(Intent data) {
        if (changeListener == null)
            changeListener = new SelectImageListener(ActivityRegister.this,
                    tv_uploadFace);
        Bitmap bitmap = null;
        try {
            Uri originalUri = data.getData();
            String path = getRealPathFromURI(originalUri);
            bitmap = Compress.compressPicToBitmap(new File(path));
            if (bitmap != null) {
                changeListener.setImagePath(path);
            }

        } catch (Exception e) {
            Log.e("checkImage", e.getMessage());
        } finally {
            return bitmap;
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        String result = contentUri.toString();
        String[] proj = {MediaColumns.DATA};
        cursor = managedQuery(contentUri, proj, null, null, null);
        if (cursor == null)
            throw new NullPointerException("reader file field");
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            // 最后根据索引值获取图片路径
            result = cursor.getString(column_index);
            try {
                // 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
                if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "error:" + e);
            }
        }
        return result;
    }

    Bitmap btp = null;
    String selectPath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case StaticInApp.CAMERA_IMAGE:
                    changeListener.startPhotoZoom(Uri.fromFile(new File(
                            changeListener.getImagePath())), 0, 0);
                    selectPath = changeListener.getImagePath();
                    break;
                case StaticInApp.LOCAL_IMAGE:
                    List<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (list != null && list.size() > 0) {
                        selectPath = list.get(0);
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
                case StaticInApp.CHANGE_USER_CITY:
                    String[] abbrNames = data.getStringArrayExtra(ActivityEditLocationInfo.EXTRA_ABBR_NAMES);
                    abbrIds = data.getStringArrayExtra(ActivityEditLocationInfo.EXTRA_ABBR_IDS);
                    StringBuilder sb = new StringBuilder();
                    for (String s : abbrNames) {
                        if (!TextUtils.isEmpty(s)) {
                            sb.append(s).append(" ");
                        }
                    }
                    tv_city.setText(sb.toString());
                    break;
            }
            if (btp != null) {
                this.hasImage = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadFaceThread() {
        smallDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                Thinksns app = (Thinksns) getApplication();
                Message msg = uiHandler.obtainMessage();
                msg.arg1 = UPLOAD_FACE;
                Object result = null;
                try {
                    result = app.getApi().uploadRegisterFace(btp,
                            new File(selectPath));
                } catch (ApiException e) {
                    e.printStackTrace();
                    result = null;
                }

                msg.obj = result;
                uiHandler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btp != null)
            btp.recycle();
    }

}
