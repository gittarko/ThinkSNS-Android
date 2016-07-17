package com.thinksns.sociax.t4.android.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

/**
 * Created by ZhiYiForMac on 15/12/8.
 */
public class ActivityChat extends com.thinksns.tschat.ui.ActivityChatDetail {
    @Override
    public void onClickUserHead(View view) {
        //点击用户头像
        onClickUserCards(view);
    }

    @Override
    public void onClickUserCards(View view) {
        //点击用户名片
        int uid = (Integer)view.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt("uid", uid);
        ActivityStack.startActivity(ActivityChat.this, ActivityUserInfo_2.class, bundle);
    }

}
