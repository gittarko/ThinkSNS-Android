package com.thinksns.tschat.chat;

import com.thinksns.tschat.bean.ModelChatMessage;

/**
 * Created by ZhiYiForMac on 15/12/8.
 */
public class PositionMessageBody extends MessageBody {

    public PositionMessageBody(int room_id, String location, double latitude, double lngitude) {
        super(room_id, "position");
        this.content = "[位置]";
        chatMessage.setLatitude(latitude);
        chatMessage.setLongitude(lngitude);
        chatMessage.setPoi_name("点击获取详情");
        chatMessage.setLocation(location);
        chatMessage.setContent(content);
    }
}
