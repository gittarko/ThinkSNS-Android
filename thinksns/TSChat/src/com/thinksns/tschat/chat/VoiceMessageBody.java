package com.thinksns.tschat.chat;

import com.thinksns.tschat.bean.ModelChatMessage;

/**
 * Created by ZhiYiForMac on 15/12/7.
 */
public class VoiceMessageBody extends MessageBody {

    public VoiceMessageBody(int room_id, String path, int length) {
        super(room_id, "voice");
        this.content = "[语音]";
        chatMessage.setLength((int)length);
        chatMessage.setAttach_url(path);
        chatMessage.setContent(content);
    }
}
