package com.thinksns.sociax.t4.android.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.service.AddRcdFriendService;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentRecommendFriend;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.io.Serializable;
import java.util.List;

public class ActivityRecommendFriend extends ThinksnsAbscractActivity {

    private Button btn_finish;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentRecommendFriend fragment = new FragmentRecommendFriend();
        fragmentTransaction.add(R.id.fl_content, fragment);
        fragmentTransaction.commit();


        btn_finish = (Button) findViewById(R.id.btn_finish);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentRecommendFriend fragment = (FragmentRecommendFriend) fragmentManager.findFragmentById(R.id.fl_content);
                Intent followIntent = new Intent(ActivityRecommendFriend.this, AddRcdFriendService.class);
                List<SociaxItem> data = fragment.getData();
                followIntent.putExtra("users", (Serializable) data);
                startService(followIntent);
                startActivity(new Intent(ActivityRecommendFriend.this, ActivityRecommendChannel.class));
            }
        });


    }

    @Override
    public String getTitleCenter() {
        return "好友推荐";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recommend_friend;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(this, R.drawable.img_back, "跳过");
    }

    @Override
    public View.OnClickListener getRightListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityRecommendFriend.this, ActivityRecommendChannel.class));
//                finish();
            }
        };
    }
}
