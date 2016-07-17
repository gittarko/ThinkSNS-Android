package com.thinksns.sociax.t4.android.data;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinksns.sociax.t4.adapter.AdapterCommentWeiboList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.function.FunctionChangeSociaxItemStatus;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.android.img.UIImageLoader;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.video.VideoWithPlayButtonView;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelImageAttach;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.model.ModelVideo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;
import com.thinksns.sociax.unit.TypeNameUtil;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 评论列表数据映射
 * 
 * @author wz
 * @date 2014-10-17
 * @version 1.0
 */
public class AppendCommentList extends AppendSociax {
	protected static final String TAG = "AppendCommentList";
	private int[] bg_defaults = { R.drawable.bg_weibodefault_1,
			R.drawable.bg_weibodefault_2, R.drawable.bg_weibodefault_2,
			R.drawable.bg_weibodefault_4, R.drawable.bg_weibodefault_6,
			R.drawable.bg_weibodefault_6 };// 默认微博背景图

	public AppendCommentList(Context context,
			AdapterCommentWeiboList adapterCommentList) {
		super(context);
		this.context = context;
		this.adapter = adapterCommentList;
	}

	/**
	 * @param convertView
	 * @param who
	 * @return
	 */
	public HolderSociax initHolder(View convertView, int who) {

		HolderSociax holder = new HolderSociax();
		// 头部用户信息
		holder.ll_user_group = (LinearLayout) convertView
				.findViewById(R.id.ll_uname_adn);
		holder.tv_comment_user_name = (TextView) convertView
				.findViewById(R.id.tv_weibo_user_name);
		holder.iv_comment_user_head = (ImageView) convertView
				.findViewById(R.id.iv_weibo_user_head);
		if (0 != who) {
			holder.tv_comment_ctime = (TextView) convertView
					.findViewById(R.id.tv_weibo_ctime);
			holder.tv_comment_from = (TextView) convertView
					.findViewById(R.id.tv_weibo_from);
		}
		holder.tv_comment_content = (TextView) convertView
				.findViewById(R.id.tv_weibo_content);

		//点赞图标，用于点赞类型的内容
		holder.iv_dig_icon = (ImageView)convertView.findViewById(R.id.iv_dig_icon);
		// 转发部分
		holder.img_source_weibo_bg = (ImageView) convertView
				.findViewById(R.id.img_weibobg);
		holder.tv_source_weibo_content = (TextView) convertView
				.findViewById(R.id.tv_weibocontent);

		// 微博背景图
		holder.iv_weibo_image = (ImageView) convertView
				.findViewById(R.id.iv_weibo_image);
		// 背景图的时候用到这个变量
		holder.rl_image = (FrameLayout) convertView.findViewById(R.id.rl_image);
		// 视频
		holder.ll_media = (LinearLayout) convertView
				.findViewById(R.id.ll_media);
		// 微博文件和图片
		holder.ll_other_files_image = (LinearLayout) convertView
				.findViewById(R.id.ll_image);

		// 赞的图标和赞的数目
		holder.iv_dig = (ImageView) convertView.findViewById(R.id.iv_dig);
		holder.tv_dig_num = (TextView) convertView
				.findViewById(R.id.tv_dig_num);
		// 评论数目和点击输入评论
		holder.tv_comment_num = (TextView) convertView
				.findViewById(R.id.tv_comment_num);
		holder.tv_add_comment = (TextView) convertView
				.findViewById(R.id.tv_add_comment);
		// 视频
		holder.ll_media = (LinearLayout) convertView
				.findViewById(R.id.ll_media);
		holder.rl_manage = (RelativeLayout) convertView
				.findViewById(R.id.rl_manage);
		// 评论信息
		holder.ll_comment_info = (LinearLayout) convertView
				.findViewById(R.id.ll_comment_info);
		// 赞信息
		holder.ll_digg_info = (LinearLayout) convertView
				.findViewById(R.id.ll_digg_info);
		// 转发那个的小图标
		holder.img_more = (ImageView) convertView.findViewById(R.id.img_more);
		// 微博隐藏的评论列表
		holder.ll_hide_comment = (LinearLayout) convertView
				.findViewById(R.id.ll_comment); // 评论列表布局
		holder.ll_hide_comment_list = (LinearLayout) convertView
				.findViewById(R.id.ll_comment_list);// 隐藏的列表
		holder.tv_hide_comment_list = (TextView) convertView
				.findViewById(R.id.tv_comment_list);

		holder.ll_transport = (LinearLayout) convertView
				.findViewById(R.id.ll_transport);
		// 指定
		// holder.img_top=(ImageView)convertView.findViewById(R.id.img_top);
		// 下面的内容在没有背景图的列表中用到于根据type来自区分显示内容
		holder.tv_post_is_delete = (TextView) convertView
				.findViewById(R.id.tv_post_is_delete);
		holder.ll_post_no_delete = (LinearLayout) convertView
				.findViewById(R.id.ll_post_no_delete);
		holder.ll_from_weibo_content = (LinearLayout) convertView
				.findViewById(R.id.ll_from_weibo_content);
		holder.ll_from_weiba_content = (LinearLayout) convertView
				.findViewById(R.id.ll_from_weiba_content);
		holder.tv_post_title = (TextView) convertView
				.findViewById(R.id.tv_post_title);
		holder.tv_post_content = (TextView) convertView
				.findViewById(R.id.tv_post_content);
		holder.tv_post_from = (TextView) convertView
				.findViewById(R.id.tv_post_from);
		// caoligai 添加，评论我的页面播放按钮
		holder.iv_comment_play = (ImageView) convertView
				.findViewById(R.id.iv_comment_play);

		return holder;
	}

	/**
	 * 第三种微博内容拼接，一般用于评论我的
	 * 
	 * @param position
	 * @param holder
	 * @param currentComment
	 */
	public void appendCommentWeiboData(int position, HolderSociax holder,
			ModelComment currentComment) {
		if (currentComment.getWeibo() != null
				&& currentComment.getWeibo().getAttachVideo() != null) {
			holder.iv_comment_play.setVisibility(View.VISIBLE);
		} else {
			holder.iv_comment_play.setVisibility(View.GONE);
		}

		//添加用户组图标
		if (holder.ll_user_group != null && currentComment.getUserApprove() != null
				&& currentComment.getUserApprove().getApprove() != null) {
			holder.ll_user_group.setVisibility(View.VISIBLE);
			uint.addUserGroup(currentComment.getUserApprove().getApprove(),holder.ll_user_group);
		} else {
			holder.ll_user_group.removeAllViews();
			holder.ll_user_group.setVisibility(View.GONE);
		}

		//评论人姓名
		holder.tv_comment_user_name.setText(currentComment.getUname());
		//加载用户头像
		UIImageLoader.getInstance(context).displayImage(currentComment.getUface(),
				holder.iv_comment_user_head);

		holder.iv_comment_user_head.setTag(R.id.tag_weibo, currentComment);
		//点击头像进入个人中心
		holder.iv_comment_user_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ModelComment currentComment = (ModelComment) v.getTag(R.id.tag_weibo);
				Intent intent = new Intent(context, ActivityUserInfo_2.class);
				intent.putExtra("uid", Integer.parseInt(currentComment.getUid()));
				context.startActivity(intent);
			}
		});

		//去掉回复@
		String content = currentComment.getContent();
		if(content.startsWith("回复@"+ Thinksns.getMy().getUserName() + "：")) {
			//如果回复的是自己则去掉回复@我的名字
			content = content.replace("回复@"+ Thinksns.getMy().getUserName() + "：", "");
		}else if(content.startsWith("回复@")) {
			//如果回复的是别人，则去掉回复@
			content = content.replace("回复@", "回复 ");
		}

		// 内容
		uint.showContentLinkViewAndLinkMovement(content,
				holder.tv_comment_content);

		if (currentComment.getWeibo_bg() == null) {
			holder.img_source_weibo_bg.setVisibility(View.GONE);
		} else {
			holder.img_source_weibo_bg.setVisibility(View.VISIBLE);
			//显示原分享内容图片
			UIImageLoader.getInstance(context).displayImage(currentComment.getWeibo_bg(),
					holder.img_source_weibo_bg);
		}

		if (currentComment.getWeibo() == null
				|| currentComment.getWeibo().isNullForContent()
				|| currentComment.getWeibo().isWeiboIsDelete() == 1) {
			holder.tv_source_weibo_content.setTextColor(context.getResources()
					.getColor(R.color.red));
		} else {
			holder.tv_source_weibo_content.setTextColor(context.getResources()
					.getColor(R.color.gray));
			String sourceContent = UnitSociax.htmlRemoveTag(currentComment.getWeibo()
					.getContent());
			uint.showContentLinkViewAndLinkMovement(sourceContent,
					holder.tv_source_weibo_content);
		}

		// 判断是否需要文字
		isShowtext(holder.img_source_weibo_bg, holder.tv_source_weibo_content);
	}

	/**
	 * 第四种微博内容拼接，一般用于@我的
	 * 
	 * @param position
	 * @param holder
	 * @param currentComment
	 */
	public void appendAtMeWeiboData(int position, HolderSociax holder,
			ModelComment currentComment) {
		if (holder.ll_user_group != null
				&& currentComment.getUser() != null
				&& currentComment.getUser().getUsApprove() != null
				&& currentComment.getUser().getUsApprove().getApprove().size() > 0) {
			uint.addUserGroup(currentComment.getUser().getUsApprove()
					.getApprove(), holder.ll_user_group);
		} else {
			holder.ll_user_group.removeAllViews();
		}

		holder.tv_comment_user_name.setText(currentComment.getUname());
		
		Glide.with(context).load(currentComment.getUface())
		.diskCacheStrategy(DiskCacheStrategy.ALL)
		.transform(new GlideCircleTransform(context))
		.crossFade()
		.into(holder.iv_comment_user_head);
		
		holder.iv_comment_user_head.setTag(R.id.tag_weibo, currentComment);
		holder.iv_comment_user_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ModelComment currentComment = (ModelComment) v
						.getTag(R.id.tag_weibo);
				Intent intent = new Intent(context, ActivityUserInfo_2.class);
				intent.putExtra("uid", currentComment.getUid());
				context.startActivity(intent);
			}
		});

		// 时间
		holder.tv_comment_ctime.setText(TimeHelper.friendlyTime(currentComment
				.getCtime()));
		// from
		holder.tv_comment_from.setText(currentComment.getFrom().toString());
		// 内容
		uint.showContentLinkViewAndLinkMovement(currentComment.getContent(),
				holder.tv_comment_content);
		Log.v(TAG, "commentType=1" + currentComment.getComment_type()
				+ currentComment.getContent());
		//
		holder.tv_comment_content.setMaxWidth(UnitSociax
				.getWindowWidth(context) / 8);
		holder.tv_comment_content.setMaxHeight(UnitSociax
				.getWindowWidth(context) / 8);
		Log.d(TAG,
				"currentComment.getCommentType/getComment_type =  "
						+ currentComment.getCommentType() + "  "
						+ currentComment.getComment_type());

		if (currentComment.getCommentType().equals("post")
				|| currentComment.getCommentType().equals("postimage")
				|| currentComment.getCommentType().equals("postvideo")
				|| currentComment.getCommentType().equals("postfile")
				|| currentComment.getCommentType().equals("weiba_post")
				|| currentComment.getCommentType().equals("blog_post")) {

			holder.ll_source_content_layout.setVisibility(View.GONE);
			holder.ll_weibo_content.setVisibility(View.VISIBLE);

			if (currentComment.getComment_type().equals("post")
					|| currentComment.getAttach_info().equals("")) {
				holder.ll_weibo_content.setVisibility(View.GONE);
			} else {
				holder.ll_weibo_content.setVisibility(View.VISIBLE);
				// 图片
				if (currentComment.getComment_type().equals("postimage")) {
					holder.rl_image.setVisibility(View.VISIBLE);
					try {
						ListData<ModelImageAttach> attachs = new ListData<ModelImageAttach>();
						JSONArray ja = new JSONArray(
								currentComment.getAttach_info());
						for (int i = 0; i < ja.length(); i++) {
							ModelImageAttach attach = new ModelImageAttach();
							JSONObject temp = (JSONObject) ja.get(i);
							attach.setWeiboId(currentComment.getFeed_id());
							attach.setId(Integer.parseInt(temp
									.getString("attach_id")));
							attach.setName(temp.getString("attach_name"));
							if (temp.has("attach_small"))
								attach.setSmall(temp.getString("attach_small"));
							if (temp.has("attach_origin"))
								attach.setOrigin(temp
										.getString("attach_origin"));
							attachs.add(attach);
						}

//						holder.iv_weibo_image
//								.setImageUrl(((ModelImageAttach) attachs.get(0))
//										.getOrigin());
						
//						ImageLoader.getInstance().displayImage(((ModelImageAttach) attachs.get(0))
//								.getOrigin(), holder.iv_weibo_image, Thinksns.getOptions());
						
						Glide.with(context).load(((ModelImageAttach) attachs.get(0))
								.getOrigin())
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.crossFade()
						.into(holder.iv_weibo_image);
						
						holder.iv_weibo_image
								.setLayoutParams(new RelativeLayout.LayoutParams(
										UnitSociax.getWindowWidth(context),
										UnitSociax.getWindowWidth(context)));
						// holder.tv_weibo_imgNum.setText(attachs.size() + "张");
						final List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
						for (int i = 0; i < attachs.size(); i++) {
							ModelPhoto p = new ModelPhoto();
							p.setId(i);
							p.setUrl(((ModelImageAttach) (attachs.get(i)))
									.getOrigin());
							photoList.add(p);
						}
						holder.iv_weibo_image
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										Intent i = new Intent(context,
												ActivityViewPager.class);
										i.putExtra("index", "0");
										i.putParcelableArrayListExtra(
												"photolist",
												(ArrayList<? extends Parcelable>) photoList);
										context.startActivity(i);
									}
								});
						// holder.tv_weibo_imgNum.setVisibility(View.VISIBLE);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					holder.rl_image.setVisibility(View.GONE);
				}

				// 视频
				if (currentComment.getComment_type().equals("postvideo")) {
					try {
						ModelVideo myVideo = new ModelVideo(
								new JSONArray(currentComment.getAttach_info())
										.getJSONObject(0));
						holder.ll_media.setVisibility(View.VISIBLE);
						boolean is_autoPlay = ThinksnsActivity.preferences
								.getBoolean("auto_play", false);
						VideoWithPlayButtonView player = null;
						Uri videoUri = null;
						if (myVideo.getVideoPart() != null) { // 如果有预览视频则播放预览视频，否则播放详细视频
							videoUri = Uri.parse(myVideo.getVideoPart());
						} else {
							if (myVideo.getVideoDetail() != null) {
								videoUri = Uri.parse(myVideo.getVideoDetail());
							}
						}
						Uri videoImg = Uri.parse(myVideo.getVideoImgUrl()); // 预览图片
						// if (!videoMap.containsKey(position + "")) {
						player = new VideoWithPlayButtonView(context, videoUri,
								videoImg, myVideo.getHost() != null);
						player.setImageViewScaleType(ScaleType.FIT_CENTER);
						adapter.getVideoMap().put(position + "", player);
						// } else {
						// player = videoMap.get(position + "");
						// }

						adapter.getVideoList().add(player);
						player.setLayoutParams(new LinearLayout.LayoutParams(
								UnitSociax.getWindowWidth(context), UnitSociax
										.getWindowWidth(context)));
						holder.ll_media
								.setLayoutParams(new LinearLayout.LayoutParams(
										UnitSociax.getWindowWidth(context),
										UnitSociax.getWindowWidth(context)));
						holder.ll_media.removeAllViews();// 清除所有视频
						holder.ll_media.addView(player);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					holder.ll_media.setVisibility(View.GONE);
				}
				// 文件
				if (currentComment.getComment_type().equals("postfile")) {
					holder.ll_other_files_image.setVisibility(View.VISIBLE);
					LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					lpImage.gravity = Gravity.CENTER_VERTICAL;
					ListData<ModelImageAttach> attachs = new ListData<ModelImageAttach>();
					try {
						JSONArray ja = new JSONArray(
								currentComment.getAttach_info());
						for (int i = 0; i < ja.length(); i++) {
							ModelImageAttach attach = new ModelImageAttach();
							JSONObject temp = (JSONObject) ja.get(i);
							attach.setWeiboId(currentComment.getFeed_id());
							attach.setId(Integer.parseInt(temp
									.getString("attach_id")));
							attach.setName(temp.getString("attach_name"));
							if (temp.has("attach_middle"))
								attach.setMiddle(temp
										.getString("attach_middle"));
							if (temp.has("attach_origin"))
								attach.setOrigin(temp
										.getString("attach_origin"));
							attachs.add(attach);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (attachs.size() > 0) {
						holder.ll_other_files_image.removeAllViews();
						for (int i = 0; i < attachs.size(); i++) {
							TextView tx = new TextView(context);
							tx.setPadding(8, 8, 0, 8);
							tx.setGravity(Gravity.CENTER_VERTICAL);
							tx.setTextColor(context.getResources().getColor(
									R.color.main_link_color));
							tx.setCompoundDrawablesWithIntrinsicBounds(
									TypeNameUtil
											.getDomLoadImg(((ModelImageAttach) attachs
													.get(i)).getName()), 0, 0,
									0);
							tx.setCompoundDrawablePadding(10);
							tx.setBackgroundResource(R.drawable.reviewboxbg);
							tx.setText(((ModelImageAttach) attachs.get(i))
									.getName());
							holder.ll_other_files_image.addView(tx, lpImage);
							holder.ll_other_files_image.setTag(position);
						}
					} else {
						// holder.ll_other_files_image.setVisibility(View.GONE);
					}
				} else {
					holder.ll_other_files_image.setVisibility(View.GONE);
				}
			}
			if (currentComment.getCommentType().equals("postimage")) {

				holder.ll_source_content_layout.setVisibility(View.VISIBLE);
			}

			holder.ll_weibo_content.setVisibility(View.GONE);
			Log.v(TAG, "commentType=3" + currentComment.getComment_type()
					+ currentComment.getContent());
			if (currentComment.getWeibo() == null
					|| currentComment.getWeibo().isNullForContent()
					|| currentComment.getWeibo().isWeiboIsDelete() == 1) {

				// 修改为不可见 qcj修改 2015-7-3
				holder.img_source_weibo_bg.setVisibility(View.GONE);
				holder.tv_source_weibo_content.setTextColor(context
						.getResources().getColor(R.color.gray));
				uint.showContentLinkViewAndLinkMovement("内容已经被删除",
						holder.tv_source_weibo_content);
				holder.tv_source_weibo_content.setOnClickListener(null);

			} else {
				// 是否已赞
				if (currentComment.getWeibo().isDigg()) {
					holder.iv_dig.setImageResource(R.drawable.ic_favor_press);
					holder.tv_dig_num.setTextColor(context.getResources()
							.getColor(R.color.gray));
				} else {
					holder.iv_dig.setImageResource(R.drawable.ic_favor_normal);
					holder.tv_dig_num.setTextColor(context.getResources()
							.getColor(R.color.gray));
				}
				// 赞数
				holder.tv_dig_num.setText(currentComment.getWeibo()
						.getDiggNum() + "");
				// 赞监听
				holder.ll_digg_info.setTag(R.id.tag_position, position);
				holder.ll_digg_info
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								FunctionChangeSociaxItemStatus fc = new FunctionChangeSociaxItemStatus(
										context);
								ModelComment clickitem = (ModelComment) adapter
										.getItem((Integer) arg0
												.getTag(R.id.tag_position));
								fc.setListenerSociax(new ListenerSociax() {
									@Override
									public void onTaskSuccess() {
										if (adapter != null) {
											adapter.doUpdataList();
										}
									}

									@Override
									public void onTaskError() {
									}

									@Override
									public void onTaskCancle() {
									}
								});
								fc.changeWeiboDigg(clickitem.getWeibo()
										.getWeiboId(), clickitem.getWeibo()
										.getIsDigg());
							}
						});

				holder.tv_source_weibo_content.setTextColor(context
						.getResources().getColor(R.color.gray));
				uint.showContentLinkViewAndLinkMovement("@"
						+ currentComment.getWeibo().getUsername() + ":"
						+ currentComment.getWeibo().getContent(),
						holder.tv_source_weibo_content);
				holder.tv_source_weibo_content.setTag(R.id.tag_weibo,
						currentComment);
				holder.tv_source_weibo_content
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Bundle data = new Bundle();
								ModelComment md = (ModelComment) view
										.getTag(R.id.tag_weibo);
								if (md.getWeibo() != null
										&& md.getWeibo().getType()
												.equals("weiba_post")) {
									Intent intent = new Intent(context,
											ActivityPostDetail.class);
									data.putInt("post_id", md.getWeibo()
											.getSid());
									intent.putExtras(data);
									context.startActivity(intent);
									return;
								}
								Intent intent1 = new Intent(context,
										ActivityWeiboDetail.class);
								data.putInt("weibo_id",
										md.getWeibo() == null ? md.getFeed_id()
												: md.getWeibo().getWeiboId());
								intent1.putExtras(data);
								context.startActivity(intent1);
							}
						});

				if (currentComment.getWeibo_bg() == null
						&& holder.img_source_weibo_bg != null) {
					holder.img_source_weibo_bg.setVisibility(View.GONE);
				} else {
					holder.img_source_weibo_bg.setVisibility(View.VISIBLE);
//					holder.img_source_weibo_bg.setImageUrl(currentComment
//							.getWeibo_bg());
					
//					ImageLoader.getInstance().displayImage(currentComment
//							.getWeibo_bg(), holder.img_source_weibo_bg, Thinksns.getOptions());
					
					Glide.with(context).load(currentComment
							.getWeibo_bg())
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.crossFade()
					.into(holder.img_source_weibo_bg);
				}
				holder.img_source_weibo_bg
						.setLayoutParams(new LinearLayout.LayoutParams(
								UnitSociax.getWindowWidth(context) / 4,
								UnitSociax.getWindowWidth(context) / 4));
			}
		}
	}

	// 是否显示文字，当有图片时就不显示文字，或者就显示文字
	private void isShowtext(ImageView imageView, TextView textview) {
		if (imageView.getVisibility() == View.VISIBLE) {
			textview.setVisibility(View.GONE);
		} else {
			textview.setVisibility(View.VISIBLE);
		}
	}
}
