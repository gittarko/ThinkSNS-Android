package com.thinksns.sociax.t4.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.chat.ActivityChat;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.weibo.ActivityAtMeWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityCommentMeWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityDiggMeWeibo;
import com.thinksns.sociax.t4.model.ModelNotification;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.fragment.FragmentChatList;
import com.thinksns.tschat.ui.ActivityChatDetail;

/**
 * Created by dong.he on 15/12/4.
 * 消息主页面继承自TSChat
 */
public class FragmentRoomList extends FragmentChatList {
    private RelativeLayout rl_commentme, rl_digg_me, rl_at_me;
    private TextView       tv_remind_comment,tv_remind_digg,
            tv_remind_at;
    private int newMsgCount = 0;        //未读新消息个数
    private ModelNotification notification;

    public static FragmentRoomList newInstance(ModelNotification notification) {
        FragmentRoomList fragment = new FragmentRoomList();
        Bundle args = new Bundle();
        args.putSerializable("notice", notification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(getArguments() != null) {
            notification = (ModelNotification)getArguments().getSerializable("notice");
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        //设置列表样式
        mListView.setDivider(new ColorDrawable(getActivity().getResources().getColor(R.color.bg_listview_divider)));
		mListView.setDividerHeight(1);
    }

    @Override
    public void initListener() {
        // 消息内点击事件
		rl_commentme.setOnClickListener(this);
		rl_digg_me.setOnClickListener(this);
        rl_at_me.setOnClickListener(this);
    }

    @Override
    public void onDeleteChat(ModelChatUserList chat, boolean isSuccess) {
        super.onDeleteChat(chat, isSuccess);
    }

    @Override
    protected View getListHeaderView() {
        View header = getActivity().getLayoutInflater().inflate(R.layout.headerview_chat, null);
        // 评论我的
        rl_commentme = (RelativeLayout) header.findViewById(R.id.rl_comment_me);
        //赞我的
        rl_digg_me = (RelativeLayout) header.findViewById(R.id.rl_digg);
        //提到我的
        rl_at_me = (RelativeLayout)header.findViewById(R.id.rl_at_me);
        //新评论
        tv_remind_comment = (TextView) header.findViewById(R.id.tv_remind_comment);
        //收到新的赞
        tv_remind_digg = (TextView) header.findViewById(R.id.tv_remind_digg);
        //新提到我的
        tv_remind_at = (TextView)header.findViewById(R.id.tv_remind_at);
        //如果有消息提醒则显示
        if(notification != null) {
            setUnReadUi(notification);
        }

        return header;
    }

    @Override
    protected View getListFooterView() {
        return null;
    }

    //消息长按事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //必须调用super方法
        return super.onItemLongClick(parent, view, position, id);
    }

    @Override
    public Class<? extends Activity> getChatDetailActivity() {
        return ActivityChat.class;
    }

    //消息
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        //必须调用super方法
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.rl_comment_me) {
            Intent intent1 = new Intent(getActivity(), ActivityCommentMeWeibo.class);
            startActivity(intent1);
            if(notification != null) {
                //设置消息已读
                tv_remind_comment.setVisibility(View.GONE);
                //消除评论提醒
                FragmentMessage.newInstance(notification).clearUnreadMsg(StaticInApp.UNREAD_COMMENT, notification.getComment());
            }
        }else if(id == R.id.rl_digg) {

            Intent intent2 = new Intent(getActivity(), ActivityDiggMeWeibo.class);
            startActivity(intent2);
            if(notification != null) {
                //设置消息已读
                tv_remind_digg.setVisibility(View.GONE);
                //消除评论提醒
                FragmentMessage.newInstance(notification).clearUnreadMsg(StaticInApp.UNREAD_DIGG, notification.getDigg());
            }
        }else if(id == R.id.rl_at_me) {
            tv_remind_at.setVisibility(View.GONE);
            Intent intent3 = new Intent(v.getContext(), ActivityAtMeWeibo.class);
            startActivity(intent3);
        }
    }

    //设置消息未读数，@我的，赞我的，评论我的
    public void setUnReadUi(ModelNotification notification) {
        this.notification = notification;
        int comment = notification.getComment() > 99 ? 99 : notification.getComment();
        if(comment > 0) {
            tv_remind_comment.setVisibility(View.VISIBLE);
            tv_remind_comment.setText(String.valueOf(notification.getComment()));
        }else {
            tv_remind_comment.setVisibility(View.GONE);
        }

        int digg = notification.getDigg() > 99 ? 99 : notification.getDigg();
        if(digg > 0) {
            tv_remind_digg.setVisibility(View.VISIBLE);
            tv_remind_digg.setText(String.valueOf(notification.getDigg()));
        }else {
            tv_remind_digg.setVisibility(View.GONE);
        }
    }
}
