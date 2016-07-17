package com.thinksns.sociax.t4.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.ApiStatuses;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.img.Bimp;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateBase;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateWeibo;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.utils.FormFile;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

/**
 * 类说明：后台上传微博的service
 * 
 * 需要传入intent ModelWeibo weibo; type:image/video区分上传的是微博还是图片
 * 
 * String tips 上传提示（可选，默认为正在上传...）
 * 
 * @author PC
 * @date 2014-9-17
 * @version 1.0
 */
public class ServiceUploadWeibo extends Service {
	private NotificationManager notificationManager;// 消息通知管理类
	private ModelWeibo weibo;						// 需要发布的微博
	private ModelPost post;							//发布帖子对象
	private ModelBackMessage backMsg;				//发送后服务端反馈信息
	private ModelDraft draft;						//草稿箱

	private List<String> photoList = new ArrayList<String>();
	private FormFile [] formFiles;
	private String tips = "正在上传...";				// 上传的时候的提示信息,默认为正在上传
	private int type;								// 默认上传图片
	private UIHandler handler;

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new UIHandler();
	}

	/**
	 * 显示Notification.
	 * 
	 * @param tips
	 *            提示的消息
	 */
	private void showNotification(String tips) {
		Log.v("UpLoadMedia", "showUpLoadingNotification"+tips);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ActivityHome.class), 0);
		notification.icon = R.drawable.app_load;
		notification.tickerText = tips;
		notification.contentView = new RemoteViews(getPackageName(),
				R.layout.video_progress_item);
		notification.contentIntent = contentIntent;
		notificationManager.notify(0, notification);
	}

	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 上传视频
	 * @return
	 */
	public ModelBackMessage uploadVideo(String videoPath) {
		if(videoPath == null) {
			//存入草稿
			return null;
		}

		try {
			ModelBackMessage message = new Api.StatusesApi().createNewVideoWeibo(
						weibo, getVideoThumbnail(videoPath,
									260,
									260,MediaStore.Images.Thumbnails.FULL_SCREEN_KIND),
							new File(videoPath));
			return message;
		} catch (VerifyErrorException e) {
			e.printStackTrace();
		} catch (ApiException e) {
			e.printStackTrace();
		} catch (UpdateException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 上传图片
	 * 
	 * @return
	 */
	public ModelBackMessage uploadImage() {
		try {
			ModelBackMessage message = null;
			if(type != AppConstant.CREATE_WEIBA_POST) {
				message = new Api.StatusesApi().createNewImageWeibo(weibo, formFiles);
			}else {
				message = new Api.WeibaApi().createNewPostWithImage(post, formFiles);
			}

			return message;
		} catch (VerifyErrorException e) {
			e.printStackTrace();
		} catch (ApiException e) {
			e.printStackTrace();
		} catch (UpdateException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startUploadWeibo(intent);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private final IBinder mBinder = new LocalBinder();

	// 定义内容类继承Binder
	public class LocalBinder extends Binder {
		// 返回本地服务
		public ServiceUploadWeibo getService() {
			return ServiceUploadWeibo.this;
		}
	}

	@Override
	public IBinder onBind(final Intent intent) {
		startUploadWeibo(intent);
		return mBinder;
	}

	private void startUploadWeibo(Intent intent) {
		if(intent == null) {
			//结束自己或通知UI错误提示
			return;
		}

		initIntent(intent);

		//执行上传
		Runnable runnable = createWorkThread(intent);
		new Thread(runnable).start();

	}

	private void initIntent(Intent intent) {
		SociaxItem data = (SociaxItem)intent.getSerializableExtra(ActivityCreateBase.INTENT_DATA);
		if(data == null) {
			return;
		}
		if(data instanceof  ModelWeibo) {
			weibo = (ModelWeibo)data;
		}else if(data instanceof ModelPost) {
			post= (ModelPost)data;
		}

		draft = (ModelDraft)intent.getSerializableExtra(ActivityCreateBase.INTENT_DRAFT);

	}

	private Runnable createWorkThread(final Intent intent) {
		type = intent.getIntExtra(ActivityCreateBase.INTENT_TYPE, AppConstant.CREATE_TEXT_WEIBO);
		tips = intent.getStringExtra(ActivityCreateBase.INTENT_TIPS);

		Runnable uploadThread = new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					msg.what = StaticInApp.UPLOAD_WEIBO;
					msg.arg1 = 1;	//默认发送成功
					switch (type) {
						case AppConstant.CREATE_TEXT_WEIBO:
							if (weibo.getAddress() != null) {
								//发送位置微博
								double latitude = Double.parseDouble(weibo.getLatitude());
								double longitude = Double.parseDouble(weibo.getLongitude());
								String address = weibo.getAddress();
								backMsg = new Api.StatusesApi().createNewTextWeibo(weibo,
										longitude, latitude, address);
							} else {
								//发送普通文本微博
								backMsg = new Api.StatusesApi().createNewTextWeibo(weibo);
							}
							break;
						case AppConstant.CREATE_ALBUM_WEIBO:
							//发送图片
							if(intent.hasExtra(ActivityCreateBase.INTENT_IAMGE_LIST)) {
								parseImageList(intent);
							}
							backMsg = uploadImage();
							break;
						case AppConstant.CREATE_VIDEO_WEIBO:
							backMsg = uploadVideo(intent.getStringExtra(ActivityCreateBase.INTENT_VIDEO_PATH));
							break;
						case AppConstant.CREATE_TRANSPORT_POST:
							//转发帖子，帖子ID
							backMsg = new Api.StatusesApi().transpondPost(post.getPost_id(), post.getContent());
							break;
						case AppConstant.CREATE_TRANSPORT_WEIBO:
							//转发微博，微博ID
							backMsg = new Api.StatusesApi().transpond(weibo.getWeiboId(), weibo.getContent());
							break;
						case AppConstant.CREATE_CHANNEL_WEIBO:
							//频道微博，频道ID
							break;
						case AppConstant.CREATE_TOPIC_WEIBO:
							//话题微博，话题ID
							break;
						case AppConstant.CREATE_WEIBA_POST:
							if(intent.hasExtra(ActivityCreateBase.INTENT_IAMGE_LIST)) {
								parseImageList(intent);
								backMsg = uploadImage();
							}else {
								backMsg = new ModelBackMessage(new Api.WeibaApi().creteNewPost(post).toString());
							}

							break;
					}
				}catch(ApiException e) {
					e.printStackTrace();
					msg.arg1 = 0;
				} catch (VerifyErrorException e) {
					e.printStackTrace();
					msg.arg1 = 0;
				} catch (UpdateException e) {
					e.printStackTrace();
					msg.arg1 = 0;
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (DataInvalidException e) {
					e.printStackTrace();
				}

				handler.sendMessage(msg);

			}
		};

		return uploadThread;
	}

	//将Intent中的图片字符串解析到集合中
	private void parseImageList(Intent intent) {
		String imageList = intent.getStringExtra(ActivityCreateBase.INTENT_IAMGE_LIST);
		String[] imageArray = imageList.split(",");
		photoList = Arrays.asList(imageArray);
		//准备发送的图片集合
		formFiles = new FormFile[photoList.size()];
		for (int i = 0; i < photoList.size(); i++) {
			String path = photoList.get(i);
			String fileName = "";
			int index = path.lastIndexOf("/");
			if (index != -1)
				fileName = path.substring(index + 1);
			else {
				index = path.lastIndexOf(".");
				fileName = System.currentTimeMillis() + path.substring(index + 1);
			}

			formFiles[i] = new FormFile(Bimp.getInputStreamFromLocal(path, intent.getBooleanExtra("is_original", false)),
					fileName, "pic", "application/octet-stream");
		}
	}

	public class UIHandler extends Handler {

		public UIHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case StaticInApp.UPLOAD_WEIBO:
					if(backMsg == null) {
						if(addCacheDraft(draft) > 0) {
							Toast.makeText(getApplicationContext(), "发布失败，已加入草稿箱", Toast.LENGTH_SHORT).show();
						}
						return;
					}

					if(backMsg.getStatus() != 1) {
						//任务请求失败
						Toast.makeText(getApplicationContext(), backMsg.getMsg(), Toast.LENGTH_SHORT).show();
						return;
					}

					tips = backMsg.getMsg();
					if(weibo != null) {
//						ActivityCreateWeibo.staticVideoPath = "";
						if (!TextUtils.isEmpty(weibo.getType())) {
							//频道微博需要有审核提示，提示信息最好由后台可配置
							tips = "发布成功，请等待后台审核";
						}
					}else if(post != null) {
						EventBus.getDefault().post(post);
					}

					Toast.makeText(getApplicationContext(), backMsg.getMsg(), Toast.LENGTH_SHORT).show();

					//发送广播至朋友圈，更新页面
					Intent intent = new Intent();
					intent.setAction(StaticInApp.NOTIFY_CREATE_WEIBO);
					sendBroadcast(intent);

//					showNotification(backMsg.getMsg());
					stopSelf();

					break;
			}
		}
	}

	/**
	 * 加入草稿箱
	 */
	private long addCacheDraft(ModelDraft draft) {
		return Thinksns.getWeiboDraftSQL().addWeiboDraft(draft.getId() == -1, draft);
	}
}
