package com.thinksns.sociax.t4.android.setting;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.component.RightIsButton;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.function.FunctionVerifyCode;
import com.thinksns.sociax.t4.android.temp.T4ForgetPasswordActivity;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 绑定手机号，成功将返回Result_OK
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-23
 */
public class ActivityBindPhone extends ThinksnsAbscractActivity {

    private Button bt_next_step;
    private MyHandler myHandler;
    private int STEP_ONE = 1, STEP_TWO = 2;
    protected static final int OAUTH_CODE = 5;
    protected static final int BIND_PHONE = 6;
    private int doWhat = STEP_ONE;
    private SmallDialog smallDialog;
    private TextView tv_title_left;
    private EditText ed_phone, ed_verifycode;
    private TextView tv_getVerify;
    private int FAILED = 0;// 失败
    private int SUCCESS = 1;// 请求中
    boolean isOauthVerifycodeSuccess = false;

    String inputPhoneNum = "", inputCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        smallDialog = new SmallDialog(this, getString(R.string.please_wait));
        myHandler = new MyHandler();
        initView();
        initListener();
    }

    @Override
    public OnClickListener getLeftListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv_getVerify.getWindowToken(), 0);
                finish();
            }
        };
    }

    private void initListener() {

//        tv_title_left.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(tv_getVerify.getWindowToken(), 0);
//                finish();
//            }
//        });

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
        tv_getVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv_getVerify.getWindowToken(), 0);
                FunctionVerifyCode verify = new FunctionVerifyCode(
                        ActivityBindPhone.this, ed_phone, tv_getVerify);
                if (verify.checkPhoneNumber()) {
                    verify.getRegisterVerify();
                }
            }
        });
    }

    /**
     * 第二步，确认修改密码
     */
    protected void doStepTwo() {
    }

    /**
     * 第一步，检测验证码
     */
    protected void doStepOne() {
        FunctionVerifyCode verify = new FunctionVerifyCode(ed_phone,
                ed_verifycode, isOauthVerifycodeSuccess, ActivityBindPhone.this);
        if (verify.checkPhoneNumber()) {
            checkVerifyCode();
        }
    }

    /**
     * 检验验证码线程
     */
    protected void checkVerifyCode() {
        inputPhoneNum = ed_phone.getText().toString().trim();
        inputCode = ed_verifycode.getText().toString().trim();

        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // // TODO Auto-generated method stub
        // Thinksns app = (Thinksns) getApplication();
        // try {
        // Message msg = myHandler.obtainMessage();
        // msg.arg1 = OAUTH_CODE;
        // JSONObject jo = new JSONObject(app
        // .getUsers().checkVerifyCode(
        // ed_phone.getText().toString(),
        // ed_verifycode.getText().toString())
        // .toString());
        // msg.what = jo.getInt("status");
        // msg.obj = jo.getString("msg");
        // myHandler.sendMessage(msg);
        // } catch (Exception e) {
        // Log.d(this.getClass().toString(), e.toString());
        // }
        // }
        // }).start();
        smallDialog.show();
        bindTask();
    }

    private void initView() {

        final TextView passTv = (TextView) findViewById(R.id.tv_pass);
        bt_next_step = (Button) findViewById(R.id.bt_next_step);
//        tv_title_left = (TextView) findViewById(R.id.tv_title_left);
        ed_phone = (EditText) findViewById(R.id.ed_phone);
        ed_verifycode = (EditText) findViewById(R.id.ed_verifyCode);
        tv_getVerify = (TextView) findViewById(R.id.tv_getVerify);
    }

    @Override
    public String getTitleCenter() {
        return "绑定手机";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_phone;
    }

    @Override
    public int getRightRes() {
        return R.drawable.find_btn_bg;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == OAUTH_CODE) {
                if (msg.what == SUCCESS) {// 验证成功
                    bindTask();
                } else {
                    Toast.makeText(ActivityBindPhone.this, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            } else if (msg.arg1 == BIND_PHONE) {
                if (msg.obj != null) {
                    try {
                        JSONObject data = new JSONObject(msg.obj.toString());

                        if (data.getInt("status")==1) {
                            Intent intent = new Intent();
                            intent.putExtra("input", inputPhoneNum);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        Toast.makeText(getApplicationContext(),
                                data.getString("msg"), Toast.LENGTH_SHORT)
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.v("ActivityBindPhone--handler", "bind info return null");
                }
            }
            smallDialog.dismiss();
        }
    }

    private void bindTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thinksns app = (Thinksns) getApplication();
                Message msg = new Message();
                msg.arg1 = BIND_PHONE;
                msg.obj = app.getUsers().bindPhone(inputPhoneNum, inputCode);
                myHandler.sendMessage(msg);
            }
        }).start();
    }
}
