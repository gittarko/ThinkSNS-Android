package com.thinksns.sociax.t4.android.function;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.db.SQLHelperChatMessage;
import com.thinksns.sociax.t4.model.ModelChatMessage;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-26
 * @version 1.0
 */
public class FunctionCreateChat extends FunctionSoicax {
	protected static final String TAG = "FunctionCreateChat";
	private ListData<SociaxItem> userList;
	String member, title;
     int touid = 0;
     String name;
     String userface;
	//注释
//     private ChatSocketClient mChatSocketClient;
 	protected Thinksns app;// app

	public FunctionCreateChat(Context context) {
		super(context);

	}

	public FunctionCreateChat(Context context, ListData<SociaxItem> list) {
		super(context);
		this.userList = list;
		app = (Thinksns)context.getApplicationContext();
	}

	public class UIHandler extends Handler {

		public UIHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.obj == null) {
				return;
			}
			switch (msg.what) {
			case StaticInApp.CREATE_GROUP_CHAT:
				try {
					JSONObject result = new JSONObject(msg.obj.toString());
					if (result.getString("status").equals("1")) {
						Intent intent = null;//new Intent(context,ActivityChatDetail.class);
						int room_id = 0;
						 room_id =  result.getInt("list_id");
						intent.putExtra("room_id", room_id);
						intent.putExtra("members", member);
						intent.putExtra("title", title);
						intent.putExtra("to_uid", touid);
						intent.putExtra("to_name", name);
						intent.putExtra("to_userface", userface);
						intent.putExtra("to_uid", touid);
						intent.putExtra("needCreate", isCreateRoomBefore(room_id));
						intent.putExtra("issingle", msg.arg1 == 0 ? true :false);
						
						context.startActivity(intent);
						((Activity) context).finish();
					} else {
						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
		
		public boolean isCreateRoomBefore(int roomid){
			boolean isNewRoom = false;
			SQLHelperChatMessage chatHelper = app.getSQLHelperChatMessage();
			List<SociaxItem> list = chatHelper.getChatList();
			for (int i = 0; i < list.size(); i++) {
				ModelChatMessage chatMessage = (ModelChatMessage) list.get(i);
				if (chatMessage.getRoom_id() ==roomid ){
					isNewRoom = true;
					break;
				}
			}
			if (!isNewRoom ||roomid == 0 ){
				Toast.makeText(context, "房间创建成功", Toast.LENGTH_SHORT)
				.show();
			}else{
				Toast.makeText(context, "该房间已经存在", Toast.LENGTH_SHORT)
				.show();
			}
			return isNewRoom;
//			List<ModelChatMessage> list = chatHelper.getChatList();
		}
	}

	@Override
	protected void initUiHandler() {
		handlerUI = new UIHandler();
	}

	public void createChat(final boolean isSingle) {
		if (this.userList.size() == 0)
			return;
		final int from_uid = app.getMy().getUid();
		String temmember = null;
		String temtitle = null;
		
		if (isSingle==true){
			temmember = ((ModelSearchUser) userList.get(0)).getUid() + ",";
			touid = ((ModelSearchUser) userList.get(0)).getUid();
			temtitle = ((ModelSearchUser) userList.get(0)).getUname() + ",";
			name = ((ModelSearchUser) userList.get(0)).getUname() ;
			userface = ((ModelSearchUser) userList.get(0)).getUserface();
			Log.d(TAG, "((ModelSearchUser) userList.get(0))="+((ModelSearchUser) userList.get(0)).toString());
			
			//创建单聊房间
			//注释
//			mChatSocketClient = app.getChatSocketClient();
//			try {
//				if (mChatSocketClient!=null&&touid!=0) {
//					mChatSocketClient.creatRoom(touid);
//				}
//			} catch (Exception e) {
//
//			}
		}else if(isSingle==false){
			String uids = "";
			for (int i = 0; i < userList.size(); i++) {
				uids += ((ModelSearchUser) userList.get(i)).getUid()+ ",";
			}
			
			//创建群聊房间
			//注释
//			mChatSocketClient = app.getChatSocketClient();
//			try {
//				if (mChatSocketClient!=null&&uids!=null) {
//					mChatSocketClient.createGroupChat(uids);
//				}
//			} catch (Exception e) {
//
//			}
		}
	}

	@Override
	protected void initActivtyHandler() {
		
	}
}
