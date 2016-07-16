package com.thinksns.tschat.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.thinksns.sociax.thinksnsbase.activity.widget.GlideCircleTransform;
import com.thinksns.sociax.thinksnsbase.base.BaseApplication;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;
import com.thinksns.tschat.R;
import com.thinksns.tschat.api.MessageApi;
import com.thinksns.tschat.api.RequestResponseHandler;
import com.thinksns.tschat.base.BaseListFragment;
import com.thinksns.tschat.base.ListBaseAdapter;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSChat;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.db.SQLHelperChatMessage;
import com.thinksns.tschat.fragment.FragmentChatDetail;
import com.thinksns.tschat.listener.ChatCallBack;
import com.thinksns.tschat.listener.OnChatItemClickListener;
import com.thinksns.tschat.map.ActivityLocation;
import com.thinksns.tschat.unit.Bimp;
import com.thinksns.tschat.unit.SmileUtils;
import com.thinksns.tschat.unit.TDevice;
import com.thinksns.tschat.unit.TimeHelper;
import com.thinksns.tschat.widget.UIImageLoader;
import com.thinksns.tschat.widget.roundimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;

/**
 * 类说明：TS聊天房间适配器
 * @date 2015年8月23日
 * @version 1.0
 */
public class AdapterChatDetailList extends ListBaseAdapter<ModelChatMessage> {
	private static final String TAG = "AdapterChatDetailList";
	/***记录当前播放语音位置*****/
	private int playItemIndex = -1;
	/****语音播放器*****/
	private MediaPlayer mMediaPlayer;
	/****动画播放类*****/
	private AnimationDrawable ad = null;
	/****聊天房间是一对一聊还是群组聊天,默认为{$CHAT_SINGLE}****/
	private int chat_type = TSConfig.CHAT_SINGLE;
	/****单聊时的对方头像****/
	private String chatFace;
	/***当前聊天房间ID****/
	private int room_id = 0;
	/****查询消息记录的页数****/
	protected int page = 1;

	/***消息类型常量设置*****/
	/**
	 * <p>
	 *     TEXT_MSG：文本类型
	 *     VOICE_MSG:语音消息
	 *     IMAGE_MSG:图片消息
	 *     POSITION_MSG:位置消息
	 *     NOTIFY_MSG:系统通知消息
	 * </p>
	 */
	private final static int TEXT_MSG = 0;
	private final static int VOICE_MSG = 1;
	private final static int IMAGE_MSG = 2;
	private final static int POSITION_MSG = 3;
	private final static int CARD_MSG = 4;
	private final static int NOTIFY_MSG = 5;

	/***与适配器绑定的UI***/
	private FragmentChatDetail fragmentChatDetail;
	private LayoutInflater mInflater;
	private SQLHelperChatMessage msgSqlHelper;
	/**用于对消息时间间隔排序，起始值使用系统当前时间***/
	private long lastTime = System.currentTimeMillis() / 1000;
	/****语音消息、文字消息最大宽度以及图片最小高度****/
	private int maxWidth, minImgWidth;

	/***获取列表第一个内容****/
	public ModelChatMessage getFirst() {
		return mDatas.get(0);
	}

	/***获取列表最后一条内容****/
	public ModelChatMessage getLast() {
		if (mDatas.size() > 0) {
			return this.mDatas.get(mDatas.size()-1);
		} else
			return null;
	}
	
	/**
	 * 从Fragment中添加适配器
	 * @param fragment
	 * @param list
	 * @param chat_type
	 *  聊天类型 群聊/单聊
	 * @param room_id  房间id，群聊时候必须
	 *  @param chat_face	单聊时对方的
	 */
	public AdapterChatDetailList(BaseListFragment fragment,
								 ListData<Entity> list, MediaPlayer mediaPlayer,
								 int chat_type, int room_id, String chat_face) {
		super(fragment.getActivity());

		this.fragmentChatDetail = (FragmentChatDetail)fragment;
		this.chat_type = chat_type;
		this.mMediaPlayer = mediaPlayer;
		this.room_id = room_id;
		this.chatFace = chat_face;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		msgSqlHelper = SQLHelperChatMessage.getInstance(fragment.getContext());

		//头像35dp，左间距20+右间距60+10：
		maxWidth = UnitSociax.getWindowWidth(context) - UnitSociax.dip2px(context, 125);
		minImgWidth = UnitSociax.dip2px(context, 30);
	}

	/***
	 * 单条更新列表某一项
	 * @param message
	 * @param listView
     */
	public void  updateSingleItem(ModelChatMessage message, ListView listView) {
		if(((Activity)context).isDestroyed())
			return;

		//因为listView包含头部所以要减去一个位置
		int start = listView.getFirstVisiblePosition() - 1;
		int i = start;
		if(start == -1)
			i = 0;
		for(int j=listView.getLastVisiblePosition(); i<j && i<mDatas.size(); i++) {
			if(TextUtils.isEmpty(mDatas.get(i).getPackid()))
				continue;
			if(mDatas.get(i).getPackid().equals(message.getPackid())) {
				mDatas.set(i, message);
				View convertView = null;
				if(start == -1) {
					convertView = listView.getChildAt(i - start + 1);
				}else {
					convertView = listView.getChildAt(i - start);
				}

				Log.v(TAG, "更新消息状态");
				getView(i, convertView, listView);
				break;
			}
		}
	}

	@Override
	public int getCount() {
		//当context退出时不再刷新列表
		if(((Activity)context).isFinishing())
			return 0;
		return super.getCount();
	}

	@Override
	public ModelChatMessage getItem(int position) {
		return mDatas.get(position);
	}

	/**
	 * 根据列表条目判断消息类型
	 * @param position
	 * @return
     */
	@Override
	public int getItemViewType(int position) {
		ModelChatMessage msg = mDatas.get(position);
		String type = msg.getType();
		if (type == null || type.equals("text")) {
			return TEXT_MSG;
		} else if (type.equals("voice")) {
			return VOICE_MSG;
		} else if (type.equals("image")) {
			return IMAGE_MSG;
		} else if (type.equals("position")) {
			return POSITION_MSG;
		} else if (type.equals("card")) {
			return CARD_MSG;
		} else if (type.equals("notify")) {
			return NOTIFY_MSG;
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 6;
	}

	private class ViewHolder {
		ImageView iv_chat_head;
		TextView tv_chat_content;
		//语音视图
		TextView tv_voice_length;
		ImageView iv_voice;
		RelativeLayout rl_chat_voice;
		//图片视图
		ImageView iv_chat_pic;
		ImageView iv_chat_pic_bg;
		ProgressBar progress_pic;

		TextView tv_chat_position;
		//卡片视图
		RoundedImageView iv_card_pic;
		TextView tv_chat_card_uname;
		TextView tv_chat_card_detail;
		RelativeLayout rl_chat_card;

		TextView tv_chat_time, tv_chat_notify;

		ProgressBar send_progress;
		ImageView iv_send_failed;
		ViewGroup fl_process_failed;

		public ViewHolder() {

		}
	}

	/****
	 * 为不同的消息类型加载不同的布局
	 * @param isSend		消息是否为发送方
	 * @param MSG_TYPE		消息类型
	 * @param holder
	 * @param context
     * @return
     */
	private View initConverView(boolean isSend, int MSG_TYPE, ViewHolder holder, Context context) {
		View convertView = null;
		switch (MSG_TYPE) {
			case TEXT_MSG:
				//加载文字布局
				if(isSend) {
					convertView = LayoutInflater.from(context).inflate(R.layout.chat_item_text_right, null);
				}else {
					convertView = LayoutInflater.from(context).inflate(R.layout.chat_item_text_left, null);
				}

				holder.tv_chat_content = (TextView)convertView.findViewById(R.id.tv_chat_content);
				//设置文本内容最大宽度
				holder.tv_chat_content.setMaxWidth(maxWidth);
				break;
			case VOICE_MSG:
				if(isSend) {
					convertView = mInflater.inflate(R.layout.chat_item_voice_right, null);
				}else {
					convertView = mInflater.inflate(R.layout.chat_item_voice_left, null);
				}

				//语音长度
				holder.tv_voice_length = (TextView) convertView.findViewById(R.id.tv_voice_length);
				holder.iv_voice = (ImageView) convertView.findViewById(R.id.iv_voice);
				holder.rl_chat_voice = (RelativeLayout) convertView.findViewById(R.id.rl_chat_voice);
				break;
			case IMAGE_MSG:
				//加载图片布局
				if(isSend) {
					convertView = mInflater.inflate(R.layout.chat_item_image_right, null);
				}else {
					convertView = mInflater.inflate(R.layout.chat_item_image_left, null);
				}
				//图片
				holder.iv_chat_pic = (ImageView) convertView.findViewById(R.id.iv_chat_pic);
				//发送状态图
				holder.progress_pic = (ProgressBar)convertView.findViewById(R.id.progress_pic);
				holder.iv_chat_pic_bg = (ImageView)convertView.findViewById(R.id.iv_chat_pic_bg);
				break;
			case POSITION_MSG:
				if(isSend) {
					convertView = mInflater.inflate(R.layout.chat_item_position_right, null);
				}else {
					convertView = mInflater.inflate(R.layout.chat_item_position_left, null);
				}

				holder.iv_chat_pic = (ImageView) convertView
						.findViewById(R.id.iv_chat_pic);
				holder.tv_chat_position = (TextView) convertView
						.findViewById(R.id.tv_chat_position);
				//发送状态图
				holder.progress_pic = (ProgressBar)convertView.findViewById(R.id.progress_pic);
				break;
			case CARD_MSG:
				if(isSend) {
					convertView = mInflater.inflate(R.layout.chat_item_card_right, null);
				}else {
					convertView = mInflater.inflate(R.layout.chat_item_card_left, null);
				}
				holder.iv_card_pic = (RoundedImageView) convertView
						.findViewById(R.id.iv_card_pic);

				holder.tv_chat_card_uname = (TextView) convertView
						.findViewById(R.id.tv_chat_card_uname);

				holder.tv_chat_card_detail = (TextView) convertView
						.findViewById(R.id.tv_chat_card_detail);
				holder.rl_chat_card = (RelativeLayout)convertView.findViewById(R.id.rl_chat_card);

				break;
			case NOTIFY_MSG:
				convertView = mInflater.inflate(R.layout.chat_item_notify, null);
				holder.tv_chat_notify = (TextView)convertView.findViewById(R.id.tv_chat_notify);
				break;
		}

		//公共视图控件初始化
		holder.iv_chat_head = (ImageView)convertView.findViewById(R.id.chat_item_head);
		holder.tv_chat_time = (TextView)convertView.findViewById(R.id.tv_chat_time);
		holder.send_progress = (ProgressBar)convertView.findViewById(R.id.msg_send_progress);
		holder.iv_send_failed = (ImageView)convertView.findViewById(R.id.iv_send_failed);
		holder.fl_process_failed = (ViewGroup)convertView.findViewById(R.id.fl_process_failed);

		//设置TAG以识别当前列表项是否被创建
		convertView.setTag(R.id.tag_chat, holder);
		convertView.setTag(R.id.tag_position, MSG_TYPE);
		convertView.setTag(R.id.tag_chat_message, isSend);

		return convertView;
	}

	@Override
	public View getRealView(final int position, View convertView, final ViewGroup parent) {
		int MSG_TYPE = getItemViewType(position);
		ViewHolder holder = null;
		final ModelChatMessage message = getItem(position);
		boolean isSend = message.isSend();
		if (convertView == null || convertView.getTag(R.id.tag_chat) == null
				|| (Integer)convertView.getTag(R.id.tag_position) != MSG_TYPE
				|| (Boolean)convertView.getTag(R.id.tag_chat_message) != isSend) {
			//是否是同一种消息类型，同一个发送方
			holder = new ViewHolder();
			convertView = initConverView(isSend,MSG_TYPE,holder, parent.getContext());
		} else {
			holder = (ViewHolder) convertView.getTag(R.id.tag_chat);
		}

		convertView.setTag(message);
		//设置消息时间
		compareMsgTime(message, holder, position);

		//填充消息内容
		switch (MSG_TYPE) {
		case TEXT_MSG:
			setTextContent(position, holder);
			break;
		case VOICE_MSG:
			setVoiceContent(position, holder);
			break;
		case IMAGE_MSG:
			setImageContent(position, holder);
			break;
		case POSITION_MSG:
			setPositionContent(position, holder);
			break;
		case CARD_MSG:
			setCardContent(position, holder);
			break;
		case NOTIFY_MSG:
			//设置系统通知消息
			if (message.getNotify_type().equals("create_group_room")) {
				holder.tv_chat_notify.setText(message.getMaster_uname()+ "创建了群房间");
			} else if (message.getNotify_type().equals("add_group_member")) {
				holder.tv_chat_notify.setText(message.getRoom_add_uname() + "加入了群房间");
			} else if (message.getNotify_type().equals("remove_group_member")) {
				holder.tv_chat_notify.setText(message.getRoom_del_uname() + "被移除了群房间");
			} else if (message.getNotify_type().equals("set_room")) {
				holder.tv_chat_notify.setText(message.getFrom_uname() + message.getDescription());
			} else if (message.getNotify_type().equals("quit_group_room")) {
				holder.tv_chat_notify.setText(message.getQuit_uname()+ "退出了群房间");
			}
			return convertView;
		}

		//消息处于待发送状态则执行消息发送
		if (message.getSendState() == ModelChatMessage.SEND_STATE.SENDING) {
			if(context instanceof ChatCallBack) {
				((ChatCallBack)context).sendMessage(message, 0);
			}
			if(holder.fl_process_failed != null) {
				holder.fl_process_failed.setEnabled(false);
			}

		}else if(message.getSendState() == ModelChatMessage.SEND_STATE.SEND_ERROR) {
			if(holder.fl_process_failed != null) {
				holder.fl_process_failed.setEnabled(true);
				//消息发送失败，点击重发
				holder.fl_process_failed.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//重发消息
						if (context instanceof ChatCallBack) {
							((ChatCallBack) context).retrySendMessage(message);
						}
					}
				});
			}
		}


		if(holder.fl_process_failed != null) {
			//消息发送状态
			switch (message.getSendState()) {
				case SEND_ERROR:
					holder.fl_process_failed.setVisibility(View.VISIBLE);
					holder.iv_send_failed.setVisibility(View.VISIBLE);
					holder.send_progress.setVisibility(View.GONE);
					break;
				case SENDING:
					//图片消息与位置消息不显示左边的进度图标
					if (MSG_TYPE != IMAGE_MSG
								&& MSG_TYPE != POSITION_MSG) {
						holder.fl_process_failed.setVisibility(View.VISIBLE);
						holder.iv_send_failed.setVisibility(View.GONE);
						holder.send_progress.setVisibility(View.VISIBLE);
					} else {
						holder.fl_process_failed.setVisibility(View.GONE);
					}
					break;
				default:
					holder.fl_process_failed.setVisibility(View.GONE);
					holder.send_progress.setVisibility(View.GONE);
					Log.v(TAG, "消息已发送");
					break;
			}
		}

		//设置头像事件
		holder.iv_chat_head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(context instanceof OnChatItemClickListener) {
					arg0.setTag(message.getFrom_uid());
					((OnChatItemClickListener)context).onClickUserHead(arg0);
				}
			}
		});

		//加载头像
		String face = "";
		if(message.isSend()) face = TSChatManager.getLoginUser().getUserFace();
		else if(chat_type == TSConfig.CHAT_SINGLE)
			face = chatFace;

		if(TextUtils.isEmpty(face))
			face = message.getFrom_uface();

		if(!TextUtils.isEmpty(face)) {
			Log.e(TAG, "LOAD FACE:" + face);
			UIImageLoader.getInstance(context).displayImage(face, holder.iv_chat_head);
		}else {
			getFace(holder.iv_chat_head, message);
		}

		return convertView;
	}

	/**
	 * 前后时间比较,默认每间隔3分钟消息上方不显示时间
	 * @param message		消息数据
	 * @param holder
	 * @param position		消息位置
     */
	private void compareMsgTime(ModelChatMessage message, ViewHolder holder, int position) {
		try {
			int msgTime = message.getMtime();
			int posTime = 0;
			if(position > 0)
				posTime = getItem(position-1).getMtime();
			else
				posTime = (int)lastTime;
			if(Math.abs(posTime - msgTime) > 180) {
				holder.tv_chat_time.setVisibility(View.VISIBLE);
				holder.tv_chat_time.setText(TimeHelper.friendlyTime(msgTime));
			}else {
				holder.tv_chat_time.setVisibility(View.GONE);
			}
		} catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	//设置普通文本消息内容
	private void setTextContent(final int position, final ViewHolder holder) {
		final ModelChatMessage message = getItem(position);
		if(holder.tv_chat_content != null) {
			// 设置内容
			Spannable spannable = UnitSociax.showContentFaceView(context, new SpannableString(message.getContent()));
			holder.tv_chat_content.setText(spannable, TextView.BufferType.SPANNABLE);
		}
		//设置文本长按事件
		holder.tv_chat_content.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if(context instanceof ChatCallBack) {
					((ChatCallBack)context).copyTextMsg(message.getContent());
				}
				return false;
			}
		});
	}

	//设置语音消息内容
	private void setVoiceContent(int position, ViewHolder holder) {
		final ModelChatMessage message = getItem(position);
		showVoiceView(position, message.isSend(), holder);
		if (!TextUtils.isEmpty(message.getAttach_id()) && !isSqlHasAttachUrl(message.getMessage_id(),
				message.getRoom_id())) {
			//请求附件
			getAttach(context, position, null, null, null);
		}

		//根据时间设置语音的长度
		int length	= message.getLength();
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.rl_chat_voice.getLayoutParams();
		int minWidth = UnitSociax.dip2px(context, 60);
		int uiWidth = minWidth;
		if(length > 1) {
			int width = UnitSociax.getWindowWidth(context) - UnitSociax.dip2px(context, 52) - UnitSociax.dip2px(context, 60);
			uiWidth = minWidth + (width * length) / 60;
		}
		if(uiWidth > maxWidth)
			uiWidth = maxWidth;
		//显示语音长度
		params.width = uiWidth;
		holder.rl_chat_voice.setLayoutParams(params);
		holder.tv_voice_length.setText(length + "s");
		holder.iv_voice.setTag(R.id.tag_position, position);
	}


	//设置位置消息内容
	private void setPositionContent(int position, final ViewHolder holder) {
		final ModelChatMessage message = getItem(position);
		//显示地址
		if(message.getLocation() != null) {
			holder.tv_chat_position.setText(message.getLocation() + "");
		}

		if(!TextUtils.isEmpty(message.getAttach_url())) {
			UIImageLoader.getInstance(context).displayImage(message.getAttach_url(),
					holder.iv_chat_pic);
		}else if(message.getAttach_id() != null) {
			//请求附件
			getAttach(context, position,holder.iv_chat_pic, null, null);
		}

		if (message.getSendState() == ModelChatMessage.SEND_STATE.SENDING) {
			//设置文件处于发送状态
			holder.progress_pic.setVisibility(View.VISIBLE);
			holder.iv_chat_pic.setAlpha(0.5f);
		}else {
			//没有要发送的图片
			holder.progress_pic.setVisibility(View.GONE);
			holder.iv_chat_pic.setAlpha(1f);
		}

		holder.iv_chat_pic.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context,ActivityLocation.class);
						intent.putExtra("latitude", message.getLatitude() + "");
						intent.putExtra("longitude", message.getLongitude());
						intent.putExtra("address", message.getLocation());
						context.startActivity(intent);
					}});

	}

	/**
	 * 设置图片类型内容
	 * @param position
	 * @param holder
     */
	private void setImageContent(final int position, final ViewHolder holder) {
		final ModelChatMessage message = getItem(position);
		//初始化视图
		holder.iv_chat_pic.setImageBitmap(null);
		holder.progress_pic.setVisibility(View.GONE);
		String path = message.getLocalPath();
		if (path != null && new File(path).exists()) {
			//显示图片
			UIImageLoader.getInstance(context).displayImage(path, holder.iv_chat_pic);
			if(message.getImgHeight() < minImgWidth)
				message.setImgHeight(minImgWidth);
			if(message.getImgWidth() < minImgWidth)
				message.setImgWidth(minImgWidth);

			//本地存在图片地址
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)message.getImgWidth(),
					(int)message.getImgHeight());
			holder.iv_chat_pic.setLayoutParams(lp);
			holder.iv_chat_pic_bg.setLayoutParams(lp);
			//延迟显示消息发送进度
			holder.iv_chat_pic.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (message.getSendState() == ModelChatMessage.SEND_STATE.SENDING) {
						//设置文件处于发送状态
						holder.progress_pic.setVisibility(View.VISIBLE);
						holder.iv_chat_pic.setAlpha(0.5f);
					}else {
						//没有要发送的图片
						holder.progress_pic.setVisibility(View.GONE);
						holder.iv_chat_pic.setAlpha(1f);
					}
				}
			}, 500);
		}else {
			if (!TextUtils.isEmpty(message.getAttach_url())) {
				//已经有网络地址
				path = message.getAttach_url();
				if(message.getImgHeight() < minImgWidth)
					message.setImgHeight(minImgWidth);
				if(message.getImgWidth() < minImgWidth)
					message.setImgWidth(minImgWidth);
				//设置图片的显示尺寸
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)message.getImgWidth(),
						(int)message.getImgHeight());
				holder.iv_chat_pic.setLayoutParams(lp);
				holder.iv_chat_pic_bg.setLayoutParams(lp);
				//下载网络图片
				downloadImage(position, path, holder.iv_chat_pic);
			}else if (message.getFrom_uid() !=0 ) {
				//没有网络地址从服务器获取
				//请求附件
				getAttach(context, position, holder.iv_chat_pic, holder.iv_chat_pic_bg, holder.progress_pic);
				//显示进度
				holder.progress_pic.setVisibility(View.VISIBLE);
			}
		}

		//点击预览图片
		holder.iv_chat_pic.setTag(R.id.tag_image_path, path);
		holder.iv_chat_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String path = (String)v.getTag(R.id.tag_image_path);
				if(context instanceof ChatCallBack) {
					((ChatCallBack)context).onImageScreen(v, path);
				}
			}
		});
	}

	/**
	 * 设置名片消息内容
	 * @param position
	 * @param holder
     */
	private void setCardContent(int position, ViewHolder holder) {
		final ModelChatMessage message = getItem(position);
		if(message.getCard_avatar() != null) {
			UIImageLoader.getInstance(context).displayImage(message.getCard_avatar(),
					holder.iv_card_pic);
		}else {
			getUserCard(message);
		}

		if(message.getCard_uname() != null) {
			holder.tv_chat_card_uname.setText(message.getCard_uname());
		}

		if (TextUtils.isEmpty(message.getCard_intro())
				|| message.getCard_intro().equals("null")) {
			holder.tv_chat_card_detail.setText(context.getResources().getString(R.string.empty_intro));
		} else {
			holder.tv_chat_card_detail.setText(message.getCard_intro());
		}

		holder.rl_chat_card.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(context instanceof OnChatItemClickListener) {
					v.setTag(message.getCard_uid());
					((OnChatItemClickListener) context).onClickUserCards(v);
				}
			}
		});

	}

	/**
	 *加载语音布局
	 */
	private void showVoiceView(final int position, final boolean isMy, final ViewHolder holder) {
		final ModelChatMessage message = getItem(position);
		int yuyinDrawable = 0;
		if(isMy) {
			yuyinDrawable = R.drawable.chat_yuyin_wo2x;
		}else {
			yuyinDrawable = R.drawable.chat_yuyin_ta2x;
		}

		holder.iv_voice.setImageResource(yuyinDrawable);
		message.setIvAudio(holder.iv_voice);

		holder.rl_chat_voice.setTag(yuyinDrawable);
		holder.rl_chat_voice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int drawableId = (Integer)v.getTag();
				try {
					if (playItemIndex == position && mMediaPlayer.isPlaying()) {
						//如果当前正在播放则暂停
						mMediaPlayer.pause(); // 暂停
						ad.stop();
						message.getIvAudio().setImageResource(drawableId);
					} else {
						//如果播放的语音位置改变了或第一次播放
						if (playItemIndex != -1 && playItemIndex != position) {
							ModelChatMessage chat = getItem(playItemIndex);
							chat.getIvAudio().setImageResource(drawableId);
							chat.getAnimation().stop();
						}

						mMediaPlayer.reset();
						Log.v(TAG, "PLAY VOICE:" + message.getAttach_url());
						mMediaPlayer.setDataSource(message.getAttach_url());
						// 准备播放
						mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								mMediaPlayer.stop();
								// 停止播放
								message.getIvAudio().setImageResource(drawableId);
							}
						});
						mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
							@Override
							public void onPrepared(MediaPlayer mp) {
								mMediaPlayer.start(); // 播放
								if(isMy) {
									message.getIvAudio().setImageResource(R.anim.record_play_r_process);
								}else {
									message.getIvAudio().setImageResource(R.anim.record_play_l_process);
								}
								ad = (AnimationDrawable) (message.getIvAudio()).getDrawable();
								ad.start();
								message.setAnimation(ad);
								playItemIndex = position;
							}
						});

						mMediaPlayer.prepareAsync();
						mMediaPlayer.setLooping(false);
						mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

							@Override
							public boolean onError(MediaPlayer mp, int what, int extra) {
								mMediaPlayer.stop();
								mMediaPlayer.release();
								Log.i(TAG, "Error on Listener,what:" + what + "extra:" + extra);
								return false;
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 获取用户名片信息
	 * @param message
     */
	private void getUserCard(final ModelChatMessage message) {
		//请名片用户信息
		MessageApi.getUserCard(message.getCard_uid(), new RequestResponseHandler() {
			@Override
			public void onSuccess(Object result) {
				JSONObject card_obj = (JSONObject)result;
				if (card_obj != null) {
					try {
						message.setCard_uname(card_obj.getString("uname"));
						message.setCard_avatar(card_obj.getString("avatar"));
						message.setCard_intro(card_obj.getString("intro"));
						notifyDataSetChanged();
						//更新本地数据
						msgSqlHelper.addChatMessagetoChatList(message, message.getMessage_id());
					}catch(JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(Object errorResult) {
				Log.e("AdapterChatDetail", "请求名片错误:" + errorResult);
			}
		});
	}

	public int getMaxid() {
		return 0;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	/**
	 * 获取图片附件
	 */
	public void getAttach(final Context context, final int postion, final ImageView preImage,
						  final ImageView bgImage, final ProgressBar progress) {
		final ModelChatMessage msg = getItem(postion);
		String attach_id = msg.getAttach_id();
		if (!TextUtils.isEmpty(attach_id)) {
			new MessageApi(context).getAttachUrl(attach_id, new RequestResponseHandler() {
				@Override
				public void onSuccess(Object result) {
					JSONObject json = (JSONObject)result;
					try {
						String status = json.getString("status");
						String url = json.getString("url");
						int imgWidth = json.getInt("width");
						int imgHeight = json.getInt("height");

						if (status != null && status.equals("1")) {
							getItem(postion).setAttach_url(url);
							if (msg.getType().equals("image")) {
								//图片或位置
								resizeImageSize(preImage, bgImage, imgWidth, imgHeight, postion);
								downloadImage(postion, url, preImage);
								progress.setVisibility(View.GONE);
							}else if(msg.getType().equals("position")) {
								downloadImage(postion, url, preImage);
							}
							else if (msg.getType().equals("voice")) {
							}
						} else {
							//请求失败
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(Object errorResult) {

				}
			});
		}
	}

	/**
	 *下载图片
	 * @param position
	 * @param url 附件地址
     */
	private void downloadImage(final int position, String url, final ImageView imageView) {
		UIImageLoader.getInstance(imageView.getContext()).displayImage(url, imageView,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String s, View view) {
					}

					@Override
					public void onLoadingFailed(String s, View view, FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String s, View view, Bitmap bitmap) {
						Log.e("AdapterChatDetails", "下载bitmap完成");
						String cache_path = Environment.getExternalStorageDirectory() + "/" + TSConfig.CACHE_PATH + "/chat";
						File dir = new File(cache_path);
						if(!dir.exists()) {
							//创建目录
							dir.mkdirs();
						}

						String attach_id = getItem(position).getAttach_id();
						attach_id = attach_id.replace("/", "_");
						cache_path = dir.getAbsolutePath() + "/IMAGE_" + attach_id + ".png";
						Log.e("AdapterChatDetail", "cache path is " + cache_path);
						FileOutputStream fos = null;
						try {
							File file = new File(cache_path);
							boolean result = false;
							if(!file.exists()) {
								file.createNewFile();
								fos = new FileOutputStream(file);
								result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
								fos.flush();
							}
							else {
								result = true;
							}

							if(result) {
								getItem(position).setLocalPath(cache_path);
								//保存至数据库
								msgSqlHelper.updateMessageImageInfo(getItem(position));
							}
						}catch(IOException e) {
							e.printStackTrace();
						}finally {
							try{
								if(fos != null)
									fos.close();
							}catch(IOException e) {
								e.printStackTrace();
							}
						}

						//显示图片
						imageView.setImageBitmap(bitmap);
					}

					@Override
					public void onLoadingCancelled(String s, View view) {
					}
				});
	}

	private void resizeImageSize(final ImageView preImg, final ImageView bgImg,
								 int picWidth, int picHeight, int position) {
		//设置图片宽高
		int maxWidth = (int)TDevice.getScreenWidth(context) / 3;
		float picNh = 0f, picNw = 0f;
		float scale = picWidth / picHeight;

		if(scale < 1) {
			//宽小与高
			picNw = picWidth * maxWidth / picHeight;
			picNh = maxWidth;
		}else {
			picNh = picHeight * maxWidth / picWidth;
			picNw = maxWidth;
		}
		if(picNw > maxWidth)
			picNw = maxWidth;
		if(picNh > maxWidth)
			picNh = maxWidth;

		if(picNh < minImgWidth) {
			picNh = minImgWidth;
		}
		if(picNw < minImgWidth) {
			picNw = minImgWidth;
		}

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)picNw, (int)picNh);
		if (preImg != null){
			preImg.setLayoutParams(params);
			if (bgImg!= null)
				bgImg.setLayoutParams(params);
		}

		getItem(position).setImgWidth(picNw);
		getItem(position).setImgHeight(picNh);

	}

	/**
	 * 获取聊天对象头像信息
	 * @param head
	 * @param message
     */
		public void getFace(final ImageView head,final ModelChatMessage message) {
			//获取对方头像
			new MessageApi(context).getUserFace(message.getFrom_uid(),
						new RequestResponseHandler() {
							@Override
					public void onSuccess(Object result) {
						try{
							int status = ((JSONObject)result).getInt("status");
							if (status == 1) {
								String userFace = ((JSONObject)result).getString("url");
								//保存用户头像并更新数据库
								message.setFrom_uface(userFace);
								//更新头像地址
								msgSqlHelper.addChatMessagetoChatList(message, message.getMessage_id());
									Glide.with(BaseApplication.getContext()).load(userFace)
											.transform(new GlideCircleTransform(context))
											.crossFade()
											.error(R.drawable.default_user)
											.placeholder(R.drawable.default_user)
											.into(head);
								//保存单聊时的头像
								if(chat_type == TSConfig.CHAT_SINGLE)
									chatFace = userFace;
							}
						}catch(JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Object errorResult) {

					}
						});
		}
		

		/**
		 * 下载附件  暂时弃用
		 * @author Zoey
		 * 
		 */
		class DownAttachHandler extends Handler {

			ModelChatMessage message = null;

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		}


		class IntentHandler extends Handler {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		}
		
		//查询数据库有没有attach_url
		public boolean isSqlHasAttachUrl(int message_id,int room_id){
			String attach_url = msgSqlHelper.getMessageAttachUrl(message_id,room_id);
			if(attach_url!=null&&!attach_url.equals("")) 
				return true;
			return false;
		}
		
		//查询数据库有没有card_id
		public boolean isSqlHasCardId(int message_id,int room_id){
			int card_uid=msgSqlHelper.getMessageCardUid(message_id,room_id);
			if(card_uid!=0) 
				return true;
			return false;
		}
		
		//查询数据库有没有localPath
		public boolean isSqlHasLocalPath(int message_id,int room_id){
			String localPath=msgSqlHelper.getMessageLocalPath(message_id,room_id);
			if(localPath!=null&&!localPath.equals("")) 
				return true;
			return false;
		}
}
