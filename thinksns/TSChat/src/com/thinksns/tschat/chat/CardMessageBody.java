package com.thinksns.tschat.chat;

import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelUser;

/**
 * Created by ZhiYiForMac on 15/12/8.
 */
public class CardMessageBody extends MessageBody {
    public CardMessageBody(int room_id, ModelUser user) {
        super(room_id, "card");
        this.content = "[名片]";
        chatMessage.setCard_uid(user.getUid());
        chatMessage.setCard_uname(user.getUserName());
        chatMessage.setCard_avatar(user.getUserface());
        chatMessage.setCard_intro(user.getIntro());
        chatMessage.setContent(content);
    }
}
