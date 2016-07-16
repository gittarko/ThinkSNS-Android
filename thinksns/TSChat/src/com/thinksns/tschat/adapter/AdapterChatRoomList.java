package com.thinksns.tschat.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.thinksns.sociax.thinksnsbase.activity.widget.BadgeView;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.GlideCircleTransform;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;
import com.thinksns.tschat.R;
import com.thinksns.tschat.api.MessageApi;
import com.thinksns.tschat.api.RequestResponseHandler;
import com.thinksns.tschat.base.BaseListFragment;
import com.thinksns.tschat.base.ListBaseAdapter;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.chat.ChatSocketClient;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.db.SQLHelperChatMessage;
import com.thinksns.tschat.fragment.FragmentChatList;
import com.thinksns.tschat.unit.SmileUtils;
import com.thinksns.tschat.unit.TimeHelper;
import com.thinksns.tschat.widget.UIImageLoader;
import com.thinksns.tschat.widget.roundimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 类说明：房间列表适配器
 *
 */
public class AdapterChatRoomList extends ListBaseAdapter<ModelChatUserList> {
	private SQLHelperChatMessage sqlHelperChatMessage;
	private FragmentChatList fragmentChatList;


	public AdapterChatRoomList(BaseListFragment fragment) {
		super(fragment.getActivity());
		this.fragmentChatList = (FragmentChatList)fragment;
		sqlHelperChatMessage = SQLHelperChatMessage.getInstance(context);
	}


	@Override
	public int getItemViewType(int position) {
		if(mDatas.size() == 0) {
			return 0;
		}else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@SuppressLint("NewApi") @Override
	protected View getRealView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		if (convertView == null || convertView.getTag() == null) {
			if(type == 1) {
				convertView = LayoutInflater.from(context).inflate(R.layout.listitem_chat, null);
			}else {
				//缺省页
//				convertView = LayoutInflater.from(context).inflate(R.layout.default_empty_chat, null);
				convertView = new EmptyLayout(context);
				ListView listView = (ListView)parent;
				int width = listView.getWidth();
				int height = listView.getHeight();
				int headerViewsCount = listView.getHeaderViewsCount();
				for(int i=0; i<headerViewsCount; i++) {
					View child = listView.getChildAt(i);
					if(child != null && child.getBottom() > 0) {
						height -= listView.getChildAt(i).getBottom();
					}
				}

				if(height < 200)
					height = 200;
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
				convertView.setLayoutParams(params);
				//设置缺省聊天内容
				((EmptyLayout)convertView).setNoDataContent(context.getResources().getString(R.string.empty_message));
			}

			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(type == 1) {
			final ModelChatUserList user = getItem(position);
			convertView.setTag(R.id.tag_chat, user);
			// 单人聊天
			if (user.isSingle()) {
				String path = user.getFrom_uface_url();
				if(!TextUtils.isEmpty(path)) {
					UIImageLoader.getInstance(context).displayImage(path, holder.img_rounduser_header);
				}else {
					new MessageApi(context).getUserFace(user.getTo_uid(), new ChatFaceResponseHandler(position, holder));
				}

				holder.tv_chat_username.setText(user.getTo_name());

			}
			else {
				//获取群聊头像
				if(user.getGroupFace() == null && user.getLogoId() != 0) {
					getGroupFace(position, user.getLogoId(), holder.img_rounduser_header);
				}else if(user.getGroupFace() != null){
					UIImageLoader.getInstance(context).displayImage(user.getGroupFace(),
							holder.img_rounduser_header);
				}else {
					//设置默认群头像
					holder.img_rounduser_header.setImageResource(R.drawable.ic_chat_group);
				}

				String title = user.getTitle();
				if (!TextUtils.isEmpty(title)) {
					holder.tv_chat_username.setText(title);
				} else {
					holder.tv_chat_username.setText("群组会话");
				}

			}

			//最后一条消息
			ModelChatMessage lastMsg = user.getLastMessage();
			String content = lastMsg.getContent();
			if (!TextUtils.isEmpty(content)) {
				//如果消息正在发送
				if(lastMsg.getSendState() == ModelChatMessage.SEND_STATE.SENDING) {
					content = "[发送中]" + content;
				}

				Spannable spannable = UnitSociax.showContentFaceView(context, new SpannableString(content));
				holder.tv_chat_content.setText(spannable, TextView.BufferType.SPANNABLE);
				try {
					holder.tv_chat_ctime.setText(TimeHelper.friendlyTime(lastMsg.getMtime()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				//内容为空
				holder.tv_chat_content.setText("");
			}


			// 获取到新消息，发送广播给主界面，更新消息界面消息提醒的UI
			int newInfo = user.getIsNew() > 99 ? 99 : user.getIsNew();
			if (newInfo > 0) {
				// 有新消息
				holder.badgeUnread.setBadgeCount(newInfo);
				fragmentChatList.addUnreadMsg(user.getRoom_id(), newInfo);

			} else {
				// 没有新消息
				holder.badgeUnread.setBadgeCount(0);
			}
		}else {
			((EmptyLayout)convertView).setErrorType(EmptyLayout.NODATA);
		}

		return convertView;
	}


	//获取群头像
	private void getGroupFace(final int position, int logoId, final ImageView img_rounduser_header) {
		new MessageApi(img_rounduser_header.getContext()).getAttach(null, "url", String.valueOf(logoId),
				new RequestResponseHandler() {
			@Override
			public void onSuccess(Object result) {
				JSONObject json = (JSONObject)result;
				try {
					if (json.getInt("status") == 1) {
						String url = json.getString("url");
						if(!TextUtils.isEmpty(url)) {
							getItem(position).setGroupFace(url);
							//更新数据库
							SQLHelperChatMessage.updateRommUserFace(getItem(position));
							Glide.with(context)
									.load(url).transform(new GlideCircleTransform(context))
									.crossFade()
									.error(R.drawable.ic_chat_group)
									.placeholder(R.drawable.ic_chat_group)
									.into(img_rounduser_header);
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

//	private synchronized  void getAttach(final int position, String path, final ImageView imageView) {
////		if(path != null && new File(path).exists()) {
////			UIImageLoader.getInstance(imageView.getContext()).displayImage(path, imageView);
////		}
//		if(path != null) {
//			downloadImage(position, path, imageView);
//		}else {
//		}
//	}

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

						//显示图片
						imageView.setImageBitmap(bitmap);
//						saveImage(position, bitmap);

					}

					@Override
					public void onLoadingCancelled(String s, View view) {
					}
				});
	}

	private void saveImage(final int position, Bitmap bitmap) {
		String cache_path = Environment.getExternalStorageDirectory() + "/" + TSConfig.CACHE_PATH + "/user";
		File dir = new File(cache_path);
		if(!dir.exists()) {
			//创建目录
			dir.mkdirs();
		}

		cache_path = dir.getAbsolutePath() + "/IMAGE_" + getItem(position).getFrom_uid() + ".png";
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
				Log.e("AdapterChatDetail", "保存本地");
				getItem(position).setFrom_uface(cache_path);
				//保存至数据库
				sqlHelperChatMessage.updateRommUserFace(getItem(position));
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
	}

	//获取聊天头像回调类
	private class ChatFaceResponseHandler extends RequestResponseHandler {
		private int position;
		private ViewHolder holder;
		public ChatFaceResponseHandler(int position, ViewHolder holder) {
			this.position = position;
			this.holder = holder;
		}

		@Override
		public void onSuccess(Object result) {
			try {
				int status = ((JSONObject) result).getInt("status");
				if (status == 1) {
					String userFace = ((JSONObject) result).getString("url");
					if(!TextUtils.isEmpty(userFace)) {
						getItem(position).setFrom_uface_url(userFace);
						//保存用户头像并更新数据库
						SQLHelperChatMessage.addRoomToRoomList(getItem(position), getItem(position).getRoom_id());
						//显示用户头像
						//这里放弃Glide加载图片的方式
						UIImageLoader.getInstance(context).displayImage(userFace, holder.img_rounduser_header);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Object errorResult) {

		}
	}

	private class ViewHolder {
		//普通消息列表UI
//		RoundedImageView img_rounduser_header;
		LinearLayout ll_user_head;
		ImageView img_rounduser_header;
		TextView tv_chat_username;
		TextView tv_chat_content;
		TextView tv_chat_ctime;
		TextView tv_remind_new;
		//空白提示
		TextView tv_empty_content;
		BadgeView badgeUnread;

		ViewHolder(View convertView) {
			ll_user_head = (LinearLayout) convertView.findViewById(R.id.ll_user_head);
			img_rounduser_header = (ImageView) convertView
					.findViewById(R.id.img_chat_userheader);
			tv_chat_username = (TextView) convertView
					.findViewById(R.id.tv_chat_user_name);
			tv_chat_content = (TextView) convertView
					.findViewById(R.id.tv_chat_content);
			tv_chat_ctime = (TextView) convertView
					.findViewById(R.id.tv_chat_ctime);
//			tv_remind_new = (TextView) convertView
//					.findViewById(R.id.tv_remind_new);
			tv_empty_content = (TextView)convertView.findViewById(R.id.tv_empty_content);
			badgeUnread = new BadgeView(context);
			badgeUnread.setTargetView(ll_user_head);
			badgeUnread.setBackground(60, context.getResources().getColor(R.color.remind_color));
			badgeUnread.setBadgeGravity(Gravity.RIGHT);
			badgeUnread.setBadgeMargin(0, 0, 5, 0);
			badgeUnread.setTextSize(10);
		}
	}

	@Override
	public ModelChatUserList getItem(int position) {
		if(mDatas.size() == 0) {
			return null;
		}
		return mDatas.get(position);
	}


	/**
	 * 获取最后一条的id
	 * @return
	 */
	public int getMaxid() {
		return 0;
	}

	@Override
	public void addData(List<ModelChatUserList> data) {
		if (mDatas != null && data != null && !data.isEmpty()) {
			mDatas.addAll(data);
		}
		//排序
		Collections.sort(mDatas, new Comparator<ModelChatUserList>() {
			@Override
			public int compare(ModelChatUserList lhs, ModelChatUserList rhs) {
				//降序
				return rhs.getMtime() - lhs.getMtime();
			}
		});
		notifyDataSetChanged();
	}
}
