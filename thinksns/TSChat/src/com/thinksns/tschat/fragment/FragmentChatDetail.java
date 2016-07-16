package com.thinksns.tschat.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;
import com.thinksns.tschat.R;
import com.thinksns.tschat.adapter.AdapterChatDetailList;
import com.thinksns.tschat.api.MessageApi;
import com.thinksns.tschat.base.BaseListFragment;
import com.thinksns.tschat.base.ListBaseAdapter;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ListEntity;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.bean.ModelUser;
import com.thinksns.tschat.chat.CardMessageBody;
import com.thinksns.tschat.chat.ImageMessageBody;
import com.thinksns.tschat.chat.MessageBody;
import com.thinksns.tschat.chat.PositionMessageBody;
import com.thinksns.tschat.chat.ResponseParams;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.chat.TextMessageBody;
import com.thinksns.tschat.chat.VoiceMessageBody;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.db.SQLHelperChatMessage;
import com.thinksns.tschat.inter.ChatCoreResponseHandler;
import com.thinksns.tschat.listener.ChatCallBack;
import com.thinksns.tschat.listener.TSChatCallBack;
import com.thinksns.tschat.map.ActivityLocation;
import com.thinksns.tschat.mp3.Mp3EncodeClient;
import com.thinksns.tschat.ui.ActivityChatDetail;
import com.thinksns.tschat.ui.ActivitySelectUser;
import com.thinksns.tschat.unit.Bimp;
import com.thinksns.tschat.unit.TDevice;
import com.thinksns.tschat.unit.VoiceRecorder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 聊天详情fragment
 *
 */
public class FragmentChatDetail extends BaseListFragment<ModelChatMessage>{
	protected static final String TAG = "FragmentChatDetail";
	private static final int IDLE_TIME = 0x10;		//消息输入空闲
	private static final int GET_HISTORY = 0x11;		//用于获取历史记录
	private static final int SEND_INPUT_STATUS = 0x12;

	// 区分单聊/群聊
	private int chat_type = TSConfig.CHAT_SINGLE;
	//单人聊天时对方的uid,单人聊天时候to_uid必填，room_id可选，type为say
	protected int uid_chatUser;
	protected String chatFace;        //对方头像
	protected String chatName;        //对方姓名
	protected boolean isSingle = true;	//是否是单聊
	protected int room_id = 0;
	protected String title = "聊天详情";    // 群聊的title

	//标题栏
	private ImageView iv_back;
	private ImageView iv_more;
	private TextView tv_title;

	// 点击展开更多（位置/名片等）
	private ImageView bt_moreselects;
	private Button btn_send_chat;
	protected EditText tv_chatContext;            // 聊天编辑框
	private ImageView ivFace;                    // 取消语音
	// 取消语音
	private FrameLayout mCancelVoice, mEnsurecancele;
	private RelativeLayout recording_container;
	private ImageView iv_recorder;
	private TextView recordingHint;
	private ImageView iv_cancel;

	private ListFaceView tFaceView;// 表情框

	private ImageView img_take_picture,// 拍照按钮
			img_picture,                // 照片按钮
			img_location,                // 位置按钮
			img_card;                    // 名片按钮
	private LinearLayout mContainer;    // 位置/名片等包含框
	private LinearLayout btn_press_to_speak,    // 点击讲话
			normalchat;
	private ImageView btn_set_mode_voice;    // 设置语音
	private TextView tv_speak_tip;            // 按住说话

	private VoiceRecorder voiceRecorder;
	private MediaRecorder mMediaRecorder;    // 语音记录
	private Mp3EncodeClient mp3EncodeClient;
	private MediaPlayer mMediaPlayer;

	private File mRecAudioFile;         // 媒体文件
	private File cameraFile;            // 相片文件
	private static File imgFile;
	private static Uri photoUri;
	private static ArrayList<String> attachId_list;
	private static long times;
	private static String localPath = null;
	private String strTempFile = "recaudio_";    // 零时文件的前缀
	private static String LOCAL_IMG = "LOCAL";
	private static String SERVICE_IMG = "SERVICE";
	private final static int IMG_TO_BITMAP = 6;

	private boolean isEmpty = true;
	private int message_id = 0;   //记录消息列表第一条内容
	private int firstPos = 0;	//列表可见的第一个位置
	private int offsetTop = 0;	//列表第一个未知的偏移量

	private ListBaseAdapter<ModelChatMessage> adapter;

	private PowerManager.WakeLock wakeLock;
	private InputMethodManager manager;

	private Drawable[] micImages;        //录音动画资源

	private ChatCallBack callBack;	//聊天相关接口

	private Handler uiHandler = new Handler() {
		int idleTime = 0;
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					//获取更早历史记录
					getNetMessageList();
					break;
				case IDLE_TIME:
					if(idleTime > 8) {
						//结束输入状态
						removeMessages(IDLE_TIME);
						sendInputStatus(true);
					}else {
						idleTime += 2;
						//每隔2s发送一次检测输入状态
						sendEmptyMessageDelayed(IDLE_TIME, 2000);
					}
					break;
				case SEND_INPUT_STATUS:
					idleTime = 0;
					sendInputStatus((Boolean)msg.obj);
					break;
				default:
					// 切换msg切换图片
					iv_recorder.setImageDrawable(micImages[msg.what]);
					break;
			}
		}
	};

	//消息输入状态监听回调
	private ChatCoreResponseHandler inputResponseHandler = new ChatCoreResponseHandler() {
		@Override
		public void onSuccess(Object object) {
			Log.v(TAG, "INPUT STATUS-->onSuccess");
			if(object == null || object.toString().isEmpty()) {
				tv_title.setText(chatName);
			}else {
				tv_title.setText(object.toString());
			}
		}

		@Override
		public void onFailure(Object object) {
			Log.v(TAG, "INPUT STATUS-->onFailure");
			tv_title.setText(chatName);
		}
	};

	//消息接收监听回调
	private ChatCoreResponseHandler pushMessageHandler = new ChatCoreResponseHandler() {
		@Override
		public void onSuccess(Object object) {
			Log.v(TAG, "PUSH MESSAGE--->onSuccess");
			List<ModelChatMessage> pushList = (List<ModelChatMessage>)object;
			mAdapter.addData(pushList);
			mListView.setSelection(mAdapter.getDataSize());
			//收到消息时主动视为对方结束一次输入
			if(!isSingle)
				tv_title.setText(title);
			else
				tv_title.setText(chatName);
		}

		@Override
		public void onFailure(Object object) {
			Log.v(TAG, "PUSH_MESSAGE---->onFailure");
		}
	};

	@Override
	protected View getListHeaderView() {
		return null;
	}

	@Override
	protected View getListFooterView() {
		return null;
	}

	@Override
	protected boolean requestDataIfViewCreated() {
		return true;
	}

	public ListView getListView() {
		return mListView;
	}

	public AdapterChatDetailList getAdapter() {
		return (AdapterChatDetailList)mAdapter;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callBack = (ChatCallBack)activity;
		}catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " can not implements TSChatCallBack");
		}
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			Bundle bundle = getArguments();
			isSingle = bundle.getBoolean("issingle");
			if(isSingle) {
				//对方id
				uid_chatUser = bundle.getInt("to_uid", 0);
				chatFace = bundle.getString("to_face");
				chatName = bundle.getString("to_name");
				chat_type = TSConfig.CHAT_SINGLE;
			}else {
				chat_type = TSConfig.CHAT_GROUP;
				chatName = "group_" + room_id;
			}

			room_id = bundle.getInt("room_id", 0);
			title = getTitleCenterText(bundle.getString("title"));

		}

		EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 保存当前第一个可见的item的索引和偏移量
		firstPos = mListView.getFirstVisiblePosition();
		View v = mListView.getChildAt(0);
		offsetTop = (v == null) ? 0 : v.getTop();
	}

	@Override
	public void onResume() {
		super.onResume();
		//恢复列表之前的显示位置
		mListView.post(new Runnable() {
			@Override
			public void run() {
				if(firstPos != 0) {
					mListView.setSelectionFromTop(firstPos, offsetTop);
				}else {
					mListView.setSelection(mAdapter.getCount() - 1);
				}
			}
		});
	}

	//获取标题文字
	private String getTitleCenterText(String title) {
		if(TextUtils.isEmpty(title)) {
			title = "群组会话";
		}else {
			StringBuffer buffer = new StringBuffer(title);
			for (int i = 0; i < title.length(); i += 15) {
				if (i != 0) {
					buffer.insert(i, "\n");
				}
			}

			title = buffer.toString();
		}

		return title;
	}

	//设置房间标题
	public void setRoomTitle(String title) {
		tv_title.setText(title);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public void initView(View view) {
		super.initView(view);

		pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		mListView.setOnScrollListener(null);

		iv_back = (ImageView)findViewById(R.id.iv_back);
		tv_title = (TextView)findViewById(R.id.tv_title);
		iv_more = (ImageView)findViewById(R.id.iv_more);

		tv_title.setText(title);

		tv_speak_tip = (TextView) findViewById(R.id.tv_speak_tip);
		btn_send_chat = (Button) findViewById(R.id.btn_send_chat);
		bt_moreselects = (ImageView) findViewById(R.id.more_selects);
		tv_chatContext = (EditText) findViewById(R.id.text_chat_context);
		mCancelVoice = (FrameLayout) findViewById(R.id.cancelimage);
		mEnsurecancele = (FrameLayout) findViewById(R.id.ensurecancele);
		recording_container = (RelativeLayout) findViewById(R.id.recording_container);
		iv_recorder = (ImageView) findViewById(R.id.iv_recorder);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		iv_cancel = (ImageView) findViewById(R.id.iv_cancel);

		tFaceView = (ListFaceView) findViewById(R.id.face_view);
		ivFace = (ImageView) findViewById(R.id.changimg);
		mContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		img_card = (ImageView) findViewById(R.id.btn_file);

		img_location = (ImageView) findViewById(R.id.btn_location);
		img_picture = (ImageView) findViewById(R.id.btn_picture);
		img_take_picture = (ImageView) findViewById(R.id.btn_take_picture);

		btn_press_to_speak = (LinearLayout) findViewById(R.id.btn_press_to_speak);
		btn_set_mode_voice = (ImageView) findViewById(R.id.btn_set_mode_voice);
		normalchat = (LinearLayout) findViewById(R.id.normalchat);
		//初始化表情布局
		tFaceView.initSmileView(tv_chatContext);

		wakeLock = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "tschat");
		manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//加载录音动画资源
		micImages = new Drawable[]{getResources().getDrawable(R.drawable.ic_voice_1),
				getResources().getDrawable(R.drawable.ic_voice_2),
				getResources().getDrawable(R.drawable.ic_voice_3),
				getResources().getDrawable(R.drawable.ic_voice_4),
				getResources().getDrawable(R.drawable.ic_voice_5),
				getResources().getDrawable(R.drawable.ic_voice_6)};

		voiceRecorder = new VoiceRecorder(uiHandler);
		mp3EncodeClient = new Mp3EncodeClient();
//		sqlHelper = SQLHelperChatMessage.getInstance(getActivity());

		mListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyBoard();
				mContainer.setVisibility(View.GONE);
				tFaceView.setVisibility(View.GONE);
				ivFace.setImageResource(R.drawable.face_bar);
				return false;
			}
		});

	}

	@SuppressLint("NewApi")
	private void hideKeyBoard() {
		//隐藏键盘
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}


	@Override
	public void initData() {
		TSChatManager.initChat(room_id);
		TSChatManager.register("push_message", pushMessageHandler);
	}

	@Override
	protected String getCacheKeyPrefix() {
		return "chat_details_" + room_id;
	}

	//获取最新数据
	@Override
	public void sendRequestData() {
		if (mAdapter.getFirstItem() != null) {
			message_id = mAdapter.getFirstItem().getMessage_id();
		}

		new MessageApi(getActivity()).getMeessageByRoom(room_id, message_id, mHandler);
	}

	@Override
	protected ListEntity<ModelChatMessage> parseList(final Object reponseData) throws Exception {
		return new ListEntity<ModelChatMessage>() {
			@Override
			public List<ModelChatMessage> getList() {
				List<ModelChatMessage> list = (List<ModelChatMessage>) reponseData;
				if (list == null || list.size() == 0) {
					//从服务端获取历史聊天记录
					uiHandler.sendEmptyMessage(1);
				}
				return list;
			}
		};
	}

	//从服务器获取聊天消息记录
	private void getNetMessageList() {
		TSChatManager.getHistoryChatList(room_id, message_id, 20, new ChatCoreResponseHandler() {
			@Override
			public void onStart(Object object) {
				Log.v(TAG, "GET MESSAGE LIST--->onStart");
			}

			@Override
			public void onSuccess(Object object) {
				Log.v(TAG, "GET MESSAGE LIST--->onSuccess");
				List<ModelChatMessage> chatList = (List<ModelChatMessage>)object;
				if(chatList.size() == 0) {
					Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
				}else {
					mAdapter.addData(chatList, 0);
					mListView.setSelection(chatList.size());
				}
			}

			@Override
			public void onFailure(Object object) {
				Log.v(TAG, "GET MESSAGE LIST--->onFailure");
			}
		});
	}

	protected boolean compareTo(List<? extends Entity> data, Entity enity) {
		int s = data.size();
		if (enity != null) {
			for (int i = 0; i < s; i++) {
				if (((ModelChatMessage)enity).getMessage_id() == ((ModelChatMessage)data.get(i)).getMessage_id()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void executeOnLoadDataSuccess(List<ModelChatMessage> data) {
		if (data == null) {
			data = new ArrayList<ModelChatMessage>();
		}else if(needOverrideData()) {
			for (int i = 0; i < data.size(); i++) {
				if (compareTo(mAdapter.getData(), data.get(i))) {
					data.remove(i);
					i--;
				}
			}
		}
		int adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
		if ((mAdapter.getCount() + data.size()) == 0) {
			//列表与网络均无数据
			adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
		} else if (data.size() == 0
				|| (data.size() < getPageSize())) {
			//网络数据没有更多了
			adapterState = ListBaseAdapter.STATE_NO_MORE;
			mAdapter.notifyDataSetChanged();
		} else {
			adapterState = ListBaseAdapter.STATE_LOAD_MORE;
		}

		mAdapter.setState(adapterState);
		if(mCurrentPage != 0) {
			//头部填充数据
			mAdapter.addData(data, 0);
		}else {
			mAdapter.addData(data);
		}

		if(mListView.getAdapter() == null) {
			mListView.setAdapter(mAdapter);
		}
		if(mCurrentPage == 0) {
			mListView.setSelection(mAdapter.getCount() - 1);
		}else {
			final int newPos = (data.size()==0) ? 0 : data.size();
			mListView.post(new Runnable() {
				@Override
				public void run() {
					mListView.setSelection(newPos);
				}
			});
		}


	}

	@Override
	public void initIntentData() {

	}

	//发送输入状态
	private void sendInputStatus(boolean isEnd) {
		if(isEnd) {
			TSChatManager.sendChatingState(room_id, uid_chatUser, "结束输入...", 0, inputResponseHandler);
		}else {
			TSChatManager.sendChatingState(room_id, uid_chatUser, "对方正在输入...",
					1,inputResponseHandler);
		}
	}


	@Override
	public void onDestroy() {

		if(uid_chatUser != 0 && tv_chatContext.length() > 0) {
			Message msg = uiHandler.obtainMessage(IDLE_TIME, true);
			uiHandler.sendMessage(msg);
		}

		//释放语音播放器
		if(mMediaPlayer != null)
		{
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

		//退出房间清空id
		TSChatManager.initChat(0);
		EventBus.getDefault().unregister(this);
		super.onDestroy();

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_more.setOnClickListener(this);
		//输入框监听
		tv_chatContext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					btn_send_chat.setVisibility(View.VISIBLE);
					bt_moreselects.setVisibility(View.GONE);
				} else {
					btn_send_chat.setVisibility(View.GONE);
					bt_moreselects.setVisibility(View.VISIBLE);

				}

				if(uid_chatUser == 0)
					return;
				Message msg = null;
				msg = uiHandler.obtainMessage(SEND_INPUT_STATUS, false);
				uiHandler.sendMessage(msg);
				uiHandler.sendEmptyMessageDelayed(IDLE_TIME, 2000);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {


			}
		});

		//更多--发送名片
		img_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_press_to_speak.isPressed()) {
					// 正在录音
					return;
				}
				selectCardFromLocal();
			}
		});
		// 更多--发送位置
		img_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_press_to_speak.isPressed()) {
					// 正在录音
					return;
				}
				//注释
//				Intent intent = new Intent(getActivity(), ActivityBaiduMap.class);
				Intent intent = new Intent(getActivity(), ActivityLocation.class);
				intent.putExtra("latitude", 0 + "");
				if (room_id != 0) {
					intent.putExtra("room_id", room_id);
				}
				startActivityForResult(intent, TSConfig.REQUEST_CODE_MAP);
			}
		});
		//相册
		img_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_press_to_speak.isPressed()) {
					// 正在录音
					return;
				}

				Intent intent = new Intent(v.getContext(), MultiImageSelectorActivity.class);
				// whether show camera
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
				// max select image amount
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
				// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
				// default select images (support array list)
				intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
				startActivityForResult(intent, TSConfig.REQUEST_CHAT_CODE_LOCAL);

			}
		});

		// 拍照
		img_take_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_press_to_speak.isPressed()) {
					// 正在录音
					return;
				}
				selectPicFromCamera();// 点击照相图标
			}
		});
		
		// 发送语音
		btn_press_to_speak.setOnTouchListener(new PressToSpeakListener());

		// 点击表情
		ivFace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btn_press_to_speak.isPressed()) {
					// 正在录音
					return;
				}

				mContainer.setVisibility(View.GONE);
				normalchat.setVisibility(View.VISIBLE);
				//关闭语音
				btn_press_to_speak.setVisibility(View.GONE);
				btn_set_mode_voice.setImageResource(R.drawable.ic_chat_voice);

				if (tFaceView.getVisibility() == View.GONE) {
					tFaceView.setVisibility(View.VISIBLE);
					ivFace.setImageResource(R.drawable.key_bar);
				} else if (tFaceView.getVisibility() == View.VISIBLE) {
					tFaceView.setVisibility(View.GONE);
					ivFace.setImageResource(R.drawable.face_bar);
				}

				hideKeyBoard();
//				tv_chatContext.requestFocus();
			}
		});

		// 点击"+"更多
		bt_moreselects.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_press_to_speak.isPressed()) {
					// 正在录音
					return;
				}
				tFaceView.setVisibility(View.GONE);
				if (mContainer.getVisibility() == View.GONE) {
					mContainer.setVisibility(View.VISIBLE);
					//收起键盘
					UnitSociax.hideSoftKeyboard(v.getContext(), tv_chatContext);
				} else {
					mContainer.setVisibility(View.GONE);
				}
			}
		});

		//发送文本
		btn_send_chat.setOnClickListener(this);

		btn_set_mode_voice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_press_to_speak.isPressed()) {
					// 正在录音
					return;
				}

				mContainer.setVisibility(View.GONE);
				tFaceView.setVisibility(View.GONE);
				if (normalchat.getVisibility() == View.GONE) {
					btn_press_to_speak.setVisibility(View.GONE);
					normalchat.setVisibility(View.VISIBLE);
					btn_set_mode_voice.setImageResource(R.drawable.ic_chat_voice);
					tv_chatContext.requestFocus();
					TDevice.showSoftKeyboard(getActivity(), tv_chatContext);
				} else {
					btn_press_to_speak.setVisibility(View.VISIBLE);
					normalchat.setVisibility(View.GONE);
					btn_set_mode_voice.setImageResource(R.drawable.key_bar);
					ivFace.setImageResource(R.drawable.face_bar);
					TDevice.hideSoftKeyboard(getActivity(), tv_chatContext);
				}
			}
		});


		tv_chatContext.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mContainer.setVisibility(View.GONE);
				tFaceView.setVisibility(View.GONE);
				return false;
			}
		});

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btn_send_chat) {
			//发送文本消息
			if (btn_press_to_speak.isPressed()) {
				return;
			}
			//文本内容
			String content = tv_chatContext.getText().toString().trim();
			MessageBody body = new TextMessageBody(room_id, content);
			ModelChatMessage msg = body.getMessageBody();
			mAdapter.addItem(msg);
			mListView.setSelection(mAdapter.getDataSize());
			if(callBack != null)
				callBack.sendMessage(msg, 0);
			//清空发送框
			tv_chatContext.setText("");
		}else if(id == R.id.iv_back) {
			tv_chatContext.clearFocus();
			UnitSociax.hideSoftKeyboard(getActivity(), tv_chatContext);
			getActivity().finish();
		}else if(id == R.id.iv_more) {
			if (btn_press_to_speak.isPressed()) {
				// 正在录音
				return;
			}
			if(callBack != null) {
				callBack.onDetailsInfoSelected();
			}
		}
	}

	private class PressToSpeakListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!TDevice.isExitsSdcard()) {
                        Toast.makeText(getActivity(), "发送语音需要sdcard支持",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        //显示取消录音标识
						recording_container.setVisibility(View.VISIBLE);
						iv_recorder.setVisibility(View.VISIBLE);
						iv_cancel.setVisibility(View.INVISIBLE);
						recordingHint.setText("手指上滑，取消发送");
						recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(chatName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
						recording_container.setVisibility(View.INVISIBLE);
                        tv_speak_tip.setText("按住 说话");
                        Toast.makeText(getActivity(), "录音失败,请重试...", Toast.LENGTH_SHORT).show();
						v.postDelayed(new Runnable() {
							@Override
							public void run() {
								//结束语音
								TSChatManager.sendChatingState(room_id, uid_chatUser, "对方结束说话...",
										0,inputResponseHandler);
							}
						}, 1000);
                        return false;
                    }

                    tv_speak_tip.setText("松开 结束");
					v.postDelayed(new Runnable() {
						@Override
						public void run() {
							//发送语音
							TSChatManager.sendChatingState(room_id, uid_chatUser, "对方正在说话...", 1, inputResponseHandler);
						}
					}, 500);
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    Log.d(TAG, "event.getY()=" + event.getY());
                    if (event.getY() < 0) {
                        // 往上滑动，则隐藏提示取消，显示确认取消
						iv_recorder.setVisibility(View.INVISIBLE);
						iv_cancel.setVisibility(View.VISIBLE);
						recordingHint.setText("松开手指，取消发送");
						recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    }
					else {
						iv_recorder.setVisibility(View.VISIBLE);
						iv_cancel.setVisibility(View.INVISIBLE);
						recordingHint.setText("手指上滑，取消发送");
						recordingHint.setBackgroundColor(Color.TRANSPARENT);
					}
                }
                return true;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
					recording_container.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();
                    } else {
                        // stop recording and send voice file
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                String voicePath = voiceRecorder.getVoiceFilePath();
                                MessageBody body = new VoiceMessageBody(room_id, voicePath, length);
                                ModelChatMessage message = body.getMessageBody();
                                mAdapter.addItem(message);
                                mListView.setSelection(mAdapter.getCount() - 1);
								if(getActivity() instanceof ChatCallBack)
									((ChatCallBack)getActivity()).sendMessage(message, 0);
                            } else {
                                //录音时间太短
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    tv_speak_tip.setText("按住说话");
					v.postDelayed(new Runnable() {
						@Override
						public void run() {
							TSChatManager.sendChatingState(room_id, uid_chatUser, "对方结束说话...", 0,inputResponseHandler);
						}
					}, 1000);
                    return true;
                default:
					recording_container.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
					v.postDelayed(new Runnable() {
						@Override
						public void run() {
							TSChatManager.sendChatingState(room_id, uid_chatUser, "对方结束说话...", 0,inputResponseHandler);
						}
					}, 1000);
                    return false;
            }
        }
    }

	@Override
	public ListBaseAdapter<ModelChatMessage> getListAdapter() {
		ListData<Entity> list = new ListData<Entity>();
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		return new AdapterChatDetailList(this, list, mMediaPlayer, chat_type,
				room_id, chatFace);

	}

	/**
	 * 点击更多内的照相图标
	 */
	private void selectPicFromCamera() {
		if (!TDevice.isExitsSdcard()) {
			Toast.makeText(getActivity().getApplicationContext(),
					"SD卡不存在，不能拍照", 0).show();
			return;
		}
		cameraFile = new File(Environment.getExternalStorageDirectory(),
				TSConfig.CACHE_PATH);
		if (!cameraFile.exists())
			cameraFile.mkdirs();
		cameraFile = new File(cameraFile, System.currentTimeMillis() + ".jpg");
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				TSConfig.REQUEST_CODE_CAMERA);
	}

	/**
	 * 相片处理操作
	 */
	public static String getSDPath() {
		File sdDir = null;
		if (TDevice.isExitsSdcard()) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString() + "/";
	}

	public static String saveFilePaht(String picName)
			throws FileNotFoundException {

		File dir = new File(getSDPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String tmpFilePath = getSDPath() + picName;
		return tmpFilePath;
	}


	/**
	 * 选择用户卡片发送
	 * */
	private void selectCardFromLocal() {
		//注释
		Intent intent = new Intent(getActivity(), ActivitySelectUser.class);
		intent.putExtra("select_type", TSConfig.SELECT_CARD);
		startActivityForResult(intent, TSConfig.REQUEST_CODE_SELECT_CARD);
	}

	/**
	 * 发送图片url
	 * 
	 * @param selectedImage
	 */
	private String getPicByUri(Uri selectedImage) {
		Cursor cursor = getActivity().getContentResolver().query(selectedImage,
				null, null, null, null);
		String filepath = null;
		if (cursor != null) {
			cursor.moveToFirst();
			filepath = cursor.getString(cursor.getColumnIndex("_data"));
			cursor.close();
			cursor = null;
		} else {
			filepath = selectedImage.getPath();
		}
		localPath=filepath;
		return localPath;
	}
	
	/**
	 * 重写activity的方法
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

			if (requestCode == TSConfig.REQUEST_CODE_CAMERA) {
				// 发送拍照的照片
				if (cameraFile != null && cameraFile.exists()) {
					//注释
					String cameraPath = cameraFile.getAbsolutePath();
					BitmapFactory.Options newOpts = new BitmapFactory.Options();
					//开始读入图片，此时把options.inJustDecodeBounds  设回true了
					newOpts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(cameraPath,newOpts);//此时返回bm为空
					newOpts.inJustDecodeBounds = false;
					int width = newOpts.outWidth;
					int height = newOpts.outHeight;
					//设置缩放比
					int maxWidth = (int)TDevice.getScreenWidth(context) / 3;
					float picNh = 0f, picNw = 0f;

					float scale = width / height;
					if(scale < 1) {
						//宽小与高
						picNw = width * maxWidth / height;
						picNh = maxWidth;
					}else {
						picNh = height * maxWidth / width;
						picNw = maxWidth;
					}

					if(picNw > maxWidth)
						picNw = maxWidth;
					if(picNh > maxWidth)
						picNh = maxWidth;

					MessageBody body = new ImageMessageBody(room_id, cameraPath, picNw, picNh);
					ModelChatMessage message = body.getMessageBody();
					message.setOriginal(false);		//压缩拍照后的图片再上传
					mAdapter.addItem(message);
					firstPos = 0;
					if(callBack != null)
						callBack.sendMessage(message,0);
				}
			}
			//发送本地图片（多图）
			else if (requestCode == TSConfig.REQUEST_CHAT_CODE_LOCAL) {
				if(data == null)
					return;
				List<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				boolean isOriginal = data.getBooleanExtra(MultiImageSelectorActivity.EXTRA_SELECT_ORIGIANL, false);
				if (list == null) {
					Log.e("FragmentChatDetail","没有选择图片");
					return;
				}

				List<ModelChatMessage> msgList = new ArrayList<ModelChatMessage>();
				for (int i = 0; i < list.size(); i++) {
					String path = list.get(i);
					float [] size = computeImageSize(path);
					String tmpPath = saveImage(path);
					if(tmpPath == null) {
						Toast.makeText(getActivity(), "文件" + path + "准备上传失败", Toast.LENGTH_SHORT).show();
						continue;
					}
					//设置图片的最小宽、高
					if(size[0] < UnitSociax.dip2px(getActivity(), 20))
						size[0] = UnitSociax.dip2px(getActivity(), 20);
					if(size[1] < UnitSociax.dip2px(getActivity(), 20))
						size[1] = UnitSociax.dip2px(getActivity(), 20);
					MessageBody body = new ImageMessageBody(room_id, tmpPath, size[0], size[1]);
					Log.v(TAG, "tmp path:" + tmpPath);
					ModelChatMessage msg = body.getMessageBody();
					msg.setOriginal(isOriginal);
					msgList.add(msg);
				}

				mAdapter.addData(msgList);
				firstPos = 0;
				int delay = 0;
				for(ModelChatMessage msg : msgList) {
					//图片每张500ms的延迟发送
					if(callBack != null) {
						callBack.sendMessage(msg, delay);
						delay += 1000;
					}
				}

			} else if (requestCode == TSConfig.REQUEST_CODE_SELECT_CARD) {
				if(data == null)
					return;
				// 发送名片
				ModelUser seleceuser = (ModelUser) data.getSerializableExtra("user");
				MessageBody body = new CardMessageBody(room_id, seleceuser);
				ModelChatMessage msg = body.getMessageBody();
				mAdapter.addItem(msg);
				firstPos = 0;
				if(callBack != null)
					callBack.sendMessage(msg, 0);

			} else if (requestCode == TSConfig.REQUEST_CODE_MAP) {
				if(data != null) {
					// 发送地址
					double latitude = data.getDoubleExtra("latitude", 0);
					double longitude = data.getDoubleExtra("longitude", 0);
					String location = data.getStringExtra("location");
					String local_path = data.getStringExtra("path");
					MessageBody body = new PositionMessageBody(room_id, location, latitude, longitude);
					ModelChatMessage msg = body.getMessageBody();
					msg.setAttach_url(local_path);
					mAdapter.addItem(msg);
					firstPos = 0;
					if(callBack != null)
						callBack.sendMessage(msg, 0);
				}
			}
//		}
	}

	//计算选择图片的宽高比
	private float[] computeImageSize(String path) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds  设回true了
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path,newOpts);//此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int width = newOpts.outWidth;
		int height = newOpts.outHeight;
		//设置图片宽高
		int maxWidth = (int)TDevice.getScreenWidth(context) / 3;
		float picNh = 0f, picNw = 0f;

		float scale = width / height;
		if(scale < 1) {
			//宽小与高
			picNw = width * maxWidth / height;
			picNh = maxWidth;
		}else {
			picNh = height * maxWidth / width;
			picNw = maxWidth;
		}
		if(picNw > maxWidth)
			picNw = maxWidth;
		if(picNh > maxWidth)
			picNh = maxWidth;
		return new float[]{picNw, picNh};
	}

	private String saveImage(String path) {
		try {
			Bitmap bitmap = Bimp.revitionImageSize(path);
			String cache_path = Environment.getExternalStorageDirectory() + "/" + TSConfig.CACHE_PATH ;
			File file = new File(cache_path);
			if(!file.exists()) {
				//创建目录
				file.mkdirs();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			cache_path += "/tschat_" + sdf.format(new Date()) + path.hashCode() + ".png";
			FileOutputStream fos = new FileOutputStream(cache_path);
			boolean result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			if(result)
				return cache_path;
		} catch (IOException e) {
			Toast.makeText(getActivity(), "文件" + path + "准备上传失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_chatdetail;
	}

	/**
	 * 是否正在录音
	 * 
	 * @return
	 */
	public boolean isRecoding() {
		return btn_press_to_speak.isPressed();
	}

	@Override
	public void onDetach() {
		TSChatManager.initChat(0);
		super.onDetach();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		//获取更早之前的记录
		mCurrentPage++;
		sendRequestData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}


	@Subscribe(threadMode = ThreadMode.POSTING)
	public void updateMessaegItem(final ModelChatMessage message) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				getAdapter().updateSingleItem(message, mListView);
			}
		});
	}

}
