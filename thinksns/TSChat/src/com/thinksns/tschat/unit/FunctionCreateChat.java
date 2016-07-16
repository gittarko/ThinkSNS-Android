package com.thinksns.tschat.unit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.bean.ModelUser;
import com.thinksns.tschat.chat.ChatSocketClient;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.db.SQLHelperChatMessage;
import com.thinksns.tschat.inter.ChatCoreResponseHandler;

import org.json.JSONObject;

import java.util.List;

/**
 * 类说明：
 */
public class FunctionCreateChat {
	protected static final String TAG = "FunctionCreateChat";
	private List<ModelUser> list;
	private String touid;		//聊天对象id
	private String name;		//房间名称，单聊为用户名称，群聊为群组会话
	private ModelChatUserList room;

	public FunctionCreateChat() {
		room = new ModelChatUserList();
	}

	public FunctionCreateChat(List<ModelUser> list) {
		room = new ModelChatUserList();
		this.list = list;
		if(list.size() > 1) {
			//群聊
			room.setIs_group(0);
		}else {
			//单聊
			room.setIs_group(1);
		}

	}

	protected void initUiHandler() {
//		handlerUI = new UIHandler();
	}

	//创建聊天
	public void createChat(ChatCoreResponseHandler handler) {
		if (this.list.size() == 0)
			return;

		if (room.isSingle()){
			//创建单聊
			room.setTo_uid(list.get(0).getUid());
			room.setTo_name(list.get(0).getUserName());
			room.setFrom_uface_url(list.get(0).getFace());
		}else {
			touid = "";
			name = "群组会话";
			for(int i=0,j=list.size(); i<j; i++) {
				if(i != j -1)
					touid += list.get(i).getUid() + ",";
				else
					touid += list.get(i).getUid();
			}

			room.setTitle(name);
			room.setGroupId(touid);

		}
		//创建房间
		startCreateChat(room, handler);
	}

	private void startCreateChat(ModelChatUserList room,  ChatCoreResponseHandler handler) {
		TSChatManager.createNewChat(room, handler);
	}

	public interface OnCreateChatListener {
		//聊天创建成功，返回房间信息
		public void onSuccess(int room_id);
		public void onError(String error);

	}
}
