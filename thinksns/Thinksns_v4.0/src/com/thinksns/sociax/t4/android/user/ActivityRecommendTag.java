package com.thinksns.sociax.t4.android.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.adapter.AdapterMyTag;
import com.thinksns.sociax.t4.adapter.AdapterTagList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentTag;
import com.thinksns.sociax.t4.android.interfaces.OnPopupWindowClickListener;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowCommon;
import com.thinksns.sociax.t4.model.ModelMyTag;
import com.thinksns.sociax.t4.model.ModelUserTagandVerify;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类说明：
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年10月25日
 */
public class ActivityRecommendTag extends ThinksnsAbscractActivity {

    private Button btn_next;
    private ImageView iv_back;
    private PopupWindowCommon backTip;
    private ExecutorService single;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTag fragment = new FragmentTag();
        fragmentTransaction.add(R.id.fl_content, fragment);
        fragmentTransaction.commit();

        single = Executors.newSingleThreadExecutor();

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //把tag存起来
                SharedPreferences preferences= Thinksns.getContext().getSharedPreferences(StaticInApp.TAG_CLOUD, Context.MODE_PRIVATE);
                final String ids=preferences.getString("title","");

                if (ids!=null&&!ids.equals("null")&&!ids.equals("")){
                    single.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Message msg = new Message();
                                msg.what = StaticInApp.ADD_MY_TAG;
                                try {
                                    Thinksns app = (Thinksns)getApplicationContext();
                                    msg.obj = app.getTagsApi().addTag(ids);
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                }
                                handler.sendMessage(msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"请至少选择一个标签",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    OnPopupWindowClickListener listener = new OnPopupWindowClickListener() {
        @Override
        public void firstButtonClick() {
            backTip.dismiss();
        }

        @Override
        public void secondButtonClick() {
            ActivityRecommendTag.this.finish();
        }
    };

    @Override
    public String getTitleCenter() {
        return "选择标签";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recommend_tag;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(this, null, "跳过");
    }

    @Override
    public View.OnClickListener getRightListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityRecommendTag.this, ActivityRecommendFriend.class));
            }
        };
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticInApp.ADD_MY_TAG:

                    startActivity(new Intent(ActivityRecommendTag.this, ActivityRecommendFriend.class));

                    break;
            }
        }
    };
}
