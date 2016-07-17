package com.thinksns.sociax.t4.android.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.model.ModelAreaInfo;
import com.thinksns.sociax.t4.model.ModelCityInfo;

//import net.simonvt.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hedong on 16/1/27.
 */
public class ActivitySelectCity extends FragmentActivity implements View.OnClickListener {
//    private NumberPicker province, city, zone;			//地区选择器
    private TextView tv_ok, tv_cancel;                  //取消/确定
    private LinearLayout ll_province, ll_city, ll_zone;

    private static List<ModelAreaInfo> pMap = new ArrayList<ModelAreaInfo>();  //省份列表
    private static List<ModelAreaInfo> cMap = new ArrayList<ModelAreaInfo>();  //城市列表
    private static List<ModelAreaInfo> zMap = new ArrayList<ModelAreaInfo>();  //地区列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());

        initWindow();
        initIntent();
        initView();
        initListener();
        initData();
    }

    private void initWindow() {
        //设置窗口宽、高
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager m = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        lp.width = (int)(d.getWidth() * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
    }

    private void initIntent() {

    }

    protected int getLayoutId() {
        return R.layout.dialog_city_selected;
    }

    private void initView() {
//        province = (NumberPicker) findViewById(R.id.province);
//        city = (NumberPicker) findViewById(R.id.city);
//        zone = (NumberPicker) findViewById(R.id.zone);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        ll_province = (LinearLayout) findViewById(R.id.ll_province);
        ll_city = (LinearLayout) findViewById(R.id.ll_city);
        ll_zone = (LinearLayout) findViewById(R.id.ll_zone);
    }

    private void initListener() {
        tv_cancel.setOnClickListener(this);
        tv_ok.setOnClickListener(this);

    }

    private void initData() {
//        if(pMap.size() > 0) {
//            setData(province, pMap);
//        }
//        if(cMap.size() > 0) {
//            setData(city, cMap);
//        }
//        if(zMap.size() > 0) {
//            setData(zone, zMap);
//        }
    }

//    private void setData(NumberPicker picker, List<ModelAreaInfo> list) {
//
//        String [] pro = new String[list.size() == 0 ? 1 : list.size()];
//        int index = 0;
//        for(ModelAreaInfo info : list) {
//            if(info.getName() != null)
//                pro[index] = info.getName();
//        }
//        if(index == 0)
//            pro[0] = "---";
//
//        picker.setMinValue(0);
//        picker.setMaxValue(pro.length - 1);
//        picker.setValue(0);
//        picker.setDisplayedValues(pro);
//    }


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.tv_cancel) {
            finish();
        }
    }

    public static void fillCityData(ActivityRegister.REQUEST_TYPE type, List<ModelAreaInfo> data) {
        if(type == ActivityRegister.REQUEST_TYPE.REQUEST_PROVINCE)
            ActivitySelectCity.pMap = data;

        else if(type == ActivityRegister.REQUEST_TYPE.REQUEST_CITY) {
            ActivitySelectCity.cMap = data;
        }else if(type == ActivityRegister.REQUEST_TYPE.REQUEST_ZONE) {
            ActivitySelectCity.zMap = data;
        }
    }


}
