package com.thinksns.tschat.chat;

import com.thinksns.tschat.bean.ModelChatMessage;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hedong on 15/12/6.
 * 聊天消息体
 */
public abstract  class MessageBody {
    protected ModelChatMessage chatMessage;
    //公共属性
    protected int from_uid;
    protected String from_uname;
    protected String from_uface;    //发送人头像
    protected String pack_id;       //随机发送id
    protected String content;

    public MessageBody(int room_id, String type) {
        chatMessage = new ModelChatMessage();
        //发送消息是自己
        from_uid = TSChatManager.getLoginUser().getUid();
        from_uface = TSChatManager.getLoginUser().getUserFace();
        from_uname = TSChatManager.getLoginUser().getUserName();

        long sysTime = System.currentTimeMillis();
        int mtime = (int)(sysTime / 1000);
        pack_id = String.valueOf(sysTime);

        //创建消息对象
        chatMessage.setMtime(mtime);
        chatMessage.setRoom_id(room_id);
        chatMessage.setType(type);
        chatMessage.setFrom_uid(from_uid);
        chatMessage.setFrom_uname(from_uname);
        chatMessage.setFrom_uface(from_uface);
        chatMessage.setPackid(pack_id);
        chatMessage.setSendState(ModelChatMessage.SEND_STATE.SENDING);
        chatMessage.setSend(true);
    }


    public String createPackId(){
        //加入发送时间
        return String.valueOf(System.currentTimeMillis());
    }


    public ModelChatMessage getMessageBody() {
        return chatMessage;
    }

}
