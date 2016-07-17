package com.thinksns.sociax.t4.android.findpeople;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentFindPeople;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;

/**
 * 类说明：找人
 *
 * @author wz
 * @version 1.0
 * @date 2014-9-3
 */
public class ActivityFindPeople extends ThinksnsAbscractActivity implements OnClickListener{
    protected static final String TAG = "TSTAG_ActivityFindPeople";
    private Handler handler;

    // RelativeLayout 里面，点击事件也是监听 RelativeLayout
    private LinearLayout ll_search_tips, ll_search;
    private TextView tv_area, tv_tag, tv_auther, tv_contact;
    // 声明被选择的值
    private Button btnRefresh;

    private FragmentFindPeople fragmentFindPeople;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntentData();
        initView();
        initListener();
        initData();
    }

    /**
     * 载入数据
     */
    private void initData() {
        new Thread(new Runnable() {
            public void run() {
                Message msg = new Message();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 初始化监事件
     */
    private void initListener() {
        //搜索地区
        tv_area.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityFindPeople.this, ActivityFindPeopleDetails.class);
                intent.putExtra("type", StaticInApp.FINDPEOPLE_AREA);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //按认证找人
        tv_auther.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityFindPeople.this, ActivityFindPeopleDetails.class);
                intent.putExtra("type", StaticInApp.FINDPEOPLE_VERIFY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //按标签找人
        tv_tag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityFindPeople.this, ActivityFindPeopleDetails.class);
                intent.putExtra("type", StaticInApp.FINDPEOPLE_TAG);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //按通讯录找人
        tv_contact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityFindPeople.this, ActivitySearchUser.class);
                intent.putExtra("type", StaticInApp.FINDPEOPLE_CONTACTS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //跳转搜索
        ll_search_tips.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityFindPeople.this, ActivitySearchUser.class);
                intent.putExtra("type", StaticInApp.FINDPEOPLE_KEY);
                startActivity(intent);
            }
        });

        btnRefresh.setOnClickListener(this);
    }

    /**
     * 初始化intent信息
     */
    private void initIntentData() {
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //搜索提示框
        ll_search_tips = (LinearLayout) findViewById(R.id.ll_search_tips);

        tv_tag = (TextView) findViewById(R.id.tv_tag);
        tv_auther = (TextView) findViewById(R.id.tv_auther);
        tv_contact = (TextView) findViewById(R.id.tv_contact);
        tv_area = (TextView) findViewById(R.id.tv_area);
        btnRefresh = (Button)findViewById(R.id.buttononrefresh);

        // 首页
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.arg1 == 1) {
                    fragmentFindPeople = new FragmentFindPeople();
                    fragmentTransaction.replace(R.id.ll_content, fragmentFindPeople,
                            "FragmentFindPeople");
                    fragmentTransaction.commit();
                }
            }

            ;
        };

    }

    @Override
    public String getTitleCenter() {
        return "找人";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buttononrefresh:
                fragmentFindPeople.requestData();
                break;
        }
    }
}
