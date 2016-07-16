package com.thinksns.tschat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thinksns.tschat.R;
import com.thinksns.tschat.adapter.AdapterChatUserList;
import com.thinksns.tschat.api.MessageApi;
import com.thinksns.tschat.api.RequestResponseHandler;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.bean.ModelUser;
import com.thinksns.tschat.chat.ChatSocketClient;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSChat;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.db.SQLHelperChatMessage;
import com.thinksns.tschat.fragment.FragmentChatList;
import com.thinksns.tschat.inter.ChatCoreResponseHandler;
import com.thinksns.tschat.listener.ChatMembersInter;
import com.thinksns.tschat.listener.OnChatItemClickListener;
import com.thinksns.tschat.listener.OnPopupWindowClickListener;
import com.thinksns.tschat.popupwindow.PopupWindowListDialog;
import com.thinksns.tschat.unit.ImageUtil;
import com.thinksns.tschat.unit.TDevice;
import com.thinksns.tschat.widget.GridViewNoScroll;
import com.thinksns.tschat.widget.PopUpWindowAlertDialog;
import com.thinksns.tschat.widget.PopupWindowCommon;
import com.thinksns.tschat.widget.PopupWindowSelectImage;
import com.thinksns.tschat.widget.SelectImageListener;
import com.thinksns.tschat.widget.SmallDialog;
import com.thinksns.tschat.widget.UIImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;


/**
 * 类说明: 聊天对话详情Activity
 * 
 * @author wz
 * @date 2014-10-20
 * @version 1.0
 */
public class ActivityChatInfo extends FragmentActivity implements View.OnClickListener, ChatMembersInter{
	private static final String TAG = "ActivityChatInfo";
	private int to_uid = 0, room_id = 0;
	private boolean isSingle = true;

	private boolean hasRoomId = false;

	private TextView tv_chat_name;					// 群聊title
	private TextView tv_exit;						// 退出按钮
	private LinearLayout ll_clear_db, ll_exit_btn;	// 清理聊天记录
	private LinearLayout ll_change_chat_name;		// 修改群名
	private LinearLayout ll_content;
	private ImageView iv_back;
	private GridViewNoScroll gridView;				// 用户列表
	private RelativeLayout rl_change_group_face;
	private ImageView iv_grop_face;

	private AdapterChatUserList adapter;
	private SmallDialog dialog;
	private SelectImageListener changeListener;

	private static String preTitle, newTitle;
	private String groupFace = "";
	private int logoId = 0;
	private int type;		//群组修改类型

	List<ModelUser> chatUserList = new ArrayList<ModelUser>();	// 原有聊天成员
	protected  OnChatItemClickListener mListener ;
	private SQLHelperChatMessage sqlHelperChatMessage;
	private File cameraFile;// 相片文件

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutId());
		initIntentData();
		initView();
		initListener();
		initData();

		sqlHelperChatMessage = SQLHelperChatMessage.getInstance(this);

		dialog = new SmallDialog(this, "加载中...");
		dialog.setCanceledOnTouchOutside(false);
		getChatInfoThread();

	}

	protected void initData() {
		adapter = new AdapterChatUserList(this, chatUserList,
					"show", room_id);
		adapter.setOnChatItemClickListener((OnChatItemClickListener) ActivityChatDetail.activity);
		gridView.setAdapter(adapter);
	}

	private void initIntentData() {
		room_id = getIntent().getIntExtra("room_id", 0);
		if (room_id == 0) {
			// 如果room_id为0的话，则尝试获取to_uid
			to_uid = getIntent().getIntExtra("to_uid", 0);
			if (to_uid == 0) {// 如果to_uid也为0，错误数据
				Toast.makeText(this, "用户信息未知错误", Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			hasRoomId = true;
		}
		isSingle = getIntent().getBooleanExtra("is_single", true);
	}

	private void initView() {
		ll_exit_btn = (LinearLayout)findViewById(R.id.ll_exit_btn);
		tv_exit = (TextView) findViewById(R.id.tv_exit);
		ll_change_chat_name = (LinearLayout) findViewById(R.id.ll_change_chat_name);
		ll_clear_db = (LinearLayout) findViewById(R.id.ll_clear_db);
		gridView = (GridViewNoScroll) findViewById(R.id.listView);
		ll_content = (LinearLayout) findViewById(R.id.ll_content);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_chat_name = (TextView) findViewById(R.id.tv_chat_name);
		//修改群头像相关
		rl_change_group_face = (RelativeLayout)findViewById(R.id.rl_change_group_face);
		iv_grop_face = (ImageView)findViewById(R.id.iv_group_face);
		changeListener = new SelectImageListener(this,
				null);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

		if (isSingle) {
			ll_change_chat_name.setVisibility(View.GONE);
			ll_exit_btn.setVisibility(View.GONE);
		}

	}

	/**
	 * 获取聊天房间成员信息
	 */
	private void getChatInfoThread() {
		if (room_id == 0) {
			return;
		}

		dialog.show();
		MessageApi.getMembers(room_id, new RequestResponseHandler() {
			@Override
			public void onSuccess(Object data) {
				JSONObject result = (JSONObject)data;
				String error = "";
				try {
					if (result.getString("status").equals("1")) {
						preTitle = result.getString("title");
						if (preTitle != null) {
							tv_chat_name.setText(preTitle);
						} else {
							tv_chat_name.setText("群组会话");
						}

						if(result.has("logo")) {
							if(!result.get("logo").toString().equals("null")) {
								logoId = result.getInt("logo");
								if(logoId == 0) {

								}else {
									//获取群聊头像
									getGroupFace(logoId, iv_grop_face);
								}
							}
						}

						chatUserList.clear();
						if (result.getString("room_type").equals("group")) {
							// 群聊
							JSONArray userList = result.getJSONArray("memebrs");
							for (int i = 0; i < userList.length(); i++) {
								ModelUser mdUser = new ModelUser(userList.getJSONObject(i));
								chatUserList.add(mdUser);
							}
							//添加成员操作
							ModelUser add = new ModelUser();
							add.setUid(-1);
							chatUserList.add(add);
							if (result.getInt("from_uid") == TSChatManager.getLoginUser().getUid()
									&& userList.length() > 2) {
								// 如果自己是群主，且用户数量大于2则有删除功能
								ModelUser delete = new ModelUser();
								delete.setUid(-2);
								chatUserList.add(delete);
							}
						} else {
							// 单人聊天
							JSONArray userList = result.getJSONArray("memebrs");
							for (int i = 0; i < userList.length(); i++) {
								ModelUser mdUser = new ModelUser(userList.getJSONObject(i));
								chatUserList.add(mdUser);
							}
						}

						adapter = new AdapterChatUserList(
								ActivityChatInfo.this, chatUserList,"show", room_id);
						adapter.setOnChatItemClickListener((OnChatItemClickListener) ActivityChatDetail.activity);

						gridView.setAdapter(adapter);
						dialog.dismiss();
						return;
					}else {
						error = "加载数据失败";

					}
				}catch(JSONException e) {
					e.printStackTrace();
					error = "解析数据失败";
				}

				dialog.setContent(error);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
					}
				}, 500);
			}

			@Override
			public void onFailure(Object errorResult) {
				dialog.setContent("网络连接异常，请稍后重试");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
					}
				}, 500);
			}
		});
	}

	protected void initListener() {
		iv_back.setOnClickListener(this);
		ll_change_chat_name.setOnClickListener(this);
		ll_clear_db.setOnClickListener(this);
		tv_exit.setOnClickListener(this);
		//群头像设置
		rl_change_group_face.setOnClickListener(this);
	}


	public String getTitleCenter() {
		return "聊天信息";
	}

	private void getGroupFace(int logoId, final ImageView img_rounduser_header) {
		new MessageApi(img_rounduser_header.getContext()).getAttach(null, "url", logoId + "",
				new RequestResponseHandler() {
					@Override
					public void onSuccess(Object result) {
						JSONObject json = (JSONObject)result;
						try {
							if (json.getInt("status") == 1) {
								groupFace = json.getString("url");

								if(groupFace != null || !groupFace.isEmpty() || !groupFace.equals("null")) {
									UIImageLoader.getInstance(img_rounduser_header.getContext()).displayImage(groupFace,
											img_rounduser_header);
									//更新数据库
									sqlHelperChatMessage.updateRommUserFaceById(room_id, groupFace);
								}
							}else {
								img_rounduser_header.setImageResource(R.drawable.ic_chat_group);
							}
						}catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Object errorResult) {

					}
				});
	}

	@Override
	public void finish() {
		// 设置一下intent
		if(type == 1) {
			Intent intent = new Intent();
			intent.putExtra("newTitle", newTitle);
			setResult(RESULT_OK, intent);
		}else if(type == 2){
			Intent intent = new Intent();
			intent.putExtra("logo", logoId);
			setResult(RESULT_OK, intent);
		}

		super.finish();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();

	}

	protected int getLayoutId() {
		return R.layout.activity_chat_info;
	}

	private Bitmap btp;
	private String selectPath;

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == TSConfig.CHANGE_CHAT_NAME) {
			// 修改群名字
			if (resultCode == RESULT_OK) {
				if (intent.hasExtra("input")) {
					newTitle = intent.getStringExtra("input");
					//设置新的群标题
					if(!TextUtils.isEmpty(newTitle)) {
						tv_chat_name.setText(newTitle);
						type = 1;
					}
				}
			}
			return;
		} else if (requestCode == TSConfig.CHAT_ADD_USER) {
			// 添加用户
			if (resultCode == RESULT_OK) {
				List<ModelUser> returnList = intent.getParcelableArrayListExtra("user");
				if(returnList == null)
					returnList = new ArrayList<ModelUser>();
				// 用户是否已经在聊天列表内
				for (int j = 0; j < returnList.size(); j++) {
					ModelUser user = returnList.get(j);
					if(chatUserList.contains(user)) {
						returnList.remove(user);
						j--;
					}
				}

				addMember(returnList);
			}
		} else if (requestCode == TSConfig.CHAT_DELETE_USER) {

		} else if (requestCode == TSConfig.CAMERA_IMAGE) {
			if (cameraFile != null && cameraFile.exists()) {
				String cameraPath = cameraFile.getAbsolutePath();
				changeListener.setImagePath(cameraPath);
				changeListener.startPhotoZoom(Uri.fromFile(new File(cameraPath)));
			}
		}
		else if (requestCode == TSConfig.LOCAL_IMAGE) {
			if(intent == null)
				return;
			//修改群头像
			List<String> list = intent.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
			if (list != null && list.size() > 0) {
				selectPath = list.get(0);
				changeListener.startPhotoZoom(ImageUtil.pathToUri(this, selectPath));
			}
		} else if (requestCode == TSConfig.ZOOM_IMAGE) {
			if (intent != null) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					btp = extras.getParcelable("data");
					iv_grop_face.setImageBitmap(btp);
					uploadGroupFace(selectPath);
				} else {
				}
			}
		}
	}

		@Override
		public void onClick (View view){
			int id = view.getId();
			if (id == R.id.iv_back) {
				finish();
			}else if(id == R.id.ll_change_chat_name) {
				//修改群标题
				if (preTitle!=null) {
					Intent intent = new Intent(ActivityChatInfo.this,
							ActivityChatInfoEdit.class);
					intent.putExtra("title", preTitle);
					intent.putExtra("room_id", room_id);
					startActivityForResult(intent, TSConfig.CHANGE_CHAT_NAME);
				}
			}else if(id == R.id.ll_clear_db) {
				PopupWindowCommon pop = new PopupWindowCommon(ActivityChatInfo.this, view, "清除聊天记录", "确定", "取消");
				pop.setOnPopupWindowClickListener(new OnPopupWindowClickListener() {

					@Override
					public void firstButtonClick() {
						// 通过client获取对应的adapter，然后执行清理操作，分两种：清理个人/清理群聊，
						clearChatHistory(room_id);
					}

					@Override
					public void secondButtonClick() {

					}
				});
			}else if(id == R.id.tv_exit) {
				//退出房间
				PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(this);
				builder.setMessage("确定退出群组吗?", 18);
				builder.setTitle(null, 0);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitGroupChat(room_id);
					}
				});

				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create();
			} else if(id == R.id.rl_change_group_face) {
				//设置群头像
				final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(ActivityChatInfo.this);
				builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if(position == 0) {
							//相册选择
							selectAlbum(view);
						}else {
							selectPicFromCamera();
						}

						builder.dimss();
					}
				});

				List<String> datas = new ArrayList<String>();
				datas.add("本地图片");
				datas.add("相机拍摄");
				datas.add("取消");
				builder.create(datas);
			}
		}

	private void selectAlbum(View v) {
		//相册选择
		Intent getImage = new Intent(v.getContext(), MultiImageSelectorActivity.class);
		getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
		getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
		getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
		startActivityForResult(getImage, TSConfig.LOCAL_IMAGE);
	}

	private void selectPicFromCamera() {
		if (!TDevice.isExitsSdcard()) {
			Toast.makeText(this.getApplicationContext(),
					"SD卡不存在，不能拍照", Toast.LENGTH_SHORT).show();
			return;
		}

		cameraFile = new File(Environment.getExternalStorageDirectory(),
				TSConfig.CACHE_PATH);

		if (!cameraFile.exists())
			cameraFile.mkdirs();
		cameraFile = new File(cameraFile, System.currentTimeMillis() + ".jpg");
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				TSConfig.CAMERA_IMAGE);
	}


	@Override
	public void deleteMember(final ModelUser user) {
		dialog.show();
		dialog.setContent("请稍后...");

		TSChatManager.deleteMembers(room_id, String.valueOf(user.getUid()), new ChatCoreResponseHandler() {
			@Override
			public void onSuccess(Object object) {
				//删除成员成功
				Log.v(TAG, "DELETE MEMBER---->onSuccess");
				dialog.dismiss();
				chatUserList.remove(user);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Object object) {
				Log.v(TAG, "DELETE MEMBER---->onFailure");
				dialog.dismiss();
				Toast.makeText(ActivityChatInfo.this, "删除成员失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void exitGroupChat(final int roomId) {
		TSChatManager.exitGroupChat(roomId, new ChatCoreResponseHandler() {
			@Override
			public void onSuccess(Object object) {
				Log.v(TAG, "QUIT GROUP ROOM--->onSuccess");
				//退出群聊
//				clearChatHistory(roomId);
				TSChatManager.clearUnreadMessage(roomId,"all");
				FragmentChatList.getInstance().onDeleteChat(roomId);
				if (ActivityChatDetail.activity != null){
					ActivityChatDetail.activity.finish();
				}
				finish();
			}

			@Override
			public void onFailure(Object object) {
				Log.v(TAG, "QUIT GROUP ROOM--->onFailure");
			}
		});
	}

	@Override
	public void clearChatHistory(final int roomId) {
		TSChatManager.sendClearUnreadMsg(roomId, "all",
				new ChatCoreResponseHandler() {
					@Override
					public void onSuccess(Object object) {
						//清理未读成功
						Log.v(TAG, "CLEAR MESSAGE,CLEAR TYPE:all--->onSuccess");
						TSChatManager.clearUnreadMessage(roomId,"all");
					}

					@Override
					public void onFailure(Object object) {
						Log.v(TAG, "CLEAR MESSAGE, CLEAR TYPE:all--->onFailure");
					}
				});
	}

	@Override
	public void addMember(List<ModelUser> users) {
		if(users != null) {
			String ids = "";
			for (int i = 0; i < users.size(); i++) {
				ids += users.get(i).getUid() + ",";
			}

			if(TextUtils.isEmpty(ids))
				return;

			dialog.show();
			dialog.setContent("请稍后...");
			TSChatManager.addMembers(room_id, ids, new ChatCoreResponseHandler() {
				@Override
				public void onSuccess(Object object) {
					Log.v(TAG, "ADD GROUP MEMBER---->onSuccess");
					dialog.dismiss();
					Toast.makeText(ActivityChatInfo.this, "添加成员成功!", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(Object object) {
					Log.v(TAG, "ADD GROUP MEMBER---->onFailure");
					dialog.dismiss();
					Toast.makeText(ActivityChatInfo.this, "添加成员失败!", Toast.LENGTH_SHORT).show();
				}
			});

			//刷新成员列表
			ModelUser delete = new ModelUser();
			delete.setUid(-2);
			if(chatUserList.contains(delete)) {
				chatUserList.addAll(chatUserList.size() - 2, users);
			}else {
				chatUserList.addAll(chatUserList.size() - 1, users);
			}

			adapter.notifyDataSetChanged();

		}else {
			//选择联系人
			Intent intent = new Intent(this, ActivitySelectUser.class);
			intent.putExtra("select_type", TSConfig.CHAT_ADD_USER);
			startActivityForResult(intent,TSConfig.CHAT_ADD_USER);
		}
	}

	@Override
	public void changeRoomTitle(String name) {
	}

	/**
	 * 设置群头像
	 * @param path
     */
	public void uploadGroupFace(String path) {
		if(!dialog.isShowing()) {
			dialog.show();
		}
		dialog.setContent("请稍后...");
		ModelChatUserList room = ActivityChatDetail.getCurrentRoom();
		room.setGroupFace(path);
		TSChatManager.changeRoomTitle(room, 2, new ChatCoreResponseHandler() {
			@Override
			public void onSuccess(Object object) {
				//头像修改成功
				Log.v(TAG, "CHANGE GROUP FACE--->onSuccess");
				dialog.dismiss();
				type = 2;
				logoId = (int)object;
				Toast.makeText(ActivityChatInfo.this, "群组头像设置成功!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Object object) {
				//头像修改失败
				Log.v(TAG, "CHANGE GROUP FACE--->onFailure");
				dialog.dismiss();
				Toast.makeText(ActivityChatInfo.this, "群组头像设置失败!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStart(Object object) {
				Log.v(TAG, "CHANGE GROUP FACE--->onStart");
			}
		});
	}

//	@Override
//	public void onClickUserHead(View view) {
//		Log.v("clickHead","chainfo");
//	}
//
//	@Override
//	public void onClickUserCards(View view) {
//
//	}

}
