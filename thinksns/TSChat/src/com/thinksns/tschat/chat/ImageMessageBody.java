package com.thinksns.tschat.chat;

import com.thinksns.tschat.bean.ModelChatMessage;

/**
 * Created by ZhiYiForMac on 15/12/7.
 */
public class ImageMessageBody extends MessageBody {
    public ImageMessageBody(int room_id, String path, float width, float height) {
        super(room_id, "image");
        this.content = "[图片]";

        chatMessage.setLocalPath(path);
        chatMessage.setImgHeight(height);
        chatMessage.setImgWidth(width);
        chatMessage.setContent(content);
    }
}
