package com.thinksns.tschat.chat;


import com.thinksns.tschat.bean.ModelChatMessage;

/**
 * Created by hedong on 15/12/6.
 */
public class TextMessageBody extends MessageBody{
    public TextMessageBody(int room_id, String content) {
        super(room_id, "text");
        chatMessage.setContent(content);
        //文字默认发送成功
        chatMessage.setSendState(ModelChatMessage.SEND_STATE.SEND_OK);
    }

}
