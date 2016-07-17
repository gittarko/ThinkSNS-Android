package com.thinksns.sociax.t4.android.data;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;

/**
 * 类说明： 评论内容拼接
 * 
 * @author wz
 * @date 2014-12-25
 * @version 1.0
 */
public class AppendComment extends AppendSociax {

	private Thinksns application;
	
	public AppendComment(Context context) {
		super(context);
		application = (Thinksns) context.getApplicationContext();
	}

	/**
	 * 初始化holder
	 * 
	 * @param convertView
	 * @return
	 */
	public HolderSociax initHolder(View convertView) {
		HolderSociax holder = new HolderSociax();

		holder.ll_content = (LinearLayout)convertView.findViewById(R.id.ll_content);
		holder.ll_empty = (LinearLayout)convertView.findViewById(R.id.ll_empty);

		holder.img_comment_userface = (ImageView) convertView.findViewById(R.id.img_comment_userface);
		holder.tv_comment_content = (TextView) convertView
				.findViewById(R.id.tv_comment_content);
		holder.tv_comment_user_name = (TextView) convertView
				.findViewById(R.id.tv_comment_uname);
		holder.tv_comment_ctime = (TextView) convertView
				.findViewById(R.id.tv_comment_ctime);
		holder.ll_user_group = (LinearLayout) convertView
				.findViewById(R.id.ll_user_group);
		holder.img_comment_replay=(ImageView)convertView.findViewById(R.id.img_comment_replay);
		return holder;
	}

	/**
	 * 拼接数
	 * @param holder
	 *
	 */
	public void appendCommentData(HolderSociax holder, final ModelComment comment) {
		if (holder != null || comment == null) {
			// 头像
			Glide.with(context).load(comment.getUface())
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.transform(new GlideCircleTransform(context))
			.crossFade()
			.into(holder.img_comment_userface);

			holder.img_comment_userface.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(context,ActivityUserInfo_2.class);
					intent.putExtra("uid", Integer.parseInt(comment.getUid()));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			});
			
			// 用户认证标签
			if (comment.getUserApprove() != null &&
					comment.getUserApprove().getApprove() != null) {
				List<String> ugroup = comment.getUserApprove().getApprove();
				holder.ll_user_group.removeAllViews();
				//认证图标只显示一个
				for (int i = 0; i < ugroup.size() && i < 1; i++) {
					ImageView smView = new ImageView(context);
					application.displayImage(ugroup.get(i), smView);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								UnitSociax.dip2px(context, context.getResources().getDimension(R.dimen.comment_usergroup)), 
								UnitSociax.dip2px(context, context.getResources().getDimension(R.dimen.comment_usergroup)));
					lp.setMargins(0, 0,0,0);
					smView.setLayoutParams(lp);
					holder.ll_user_group.addView(smView);

				}
				holder.ll_user_group.setVisibility(View.VISIBLE);
			} else {
				holder.ll_user_group.setVisibility(View.GONE);
			}

			String comStr = comment.getContent();
			uint.showContentLinkViewAndLinkMovementchat(null, comStr, holder.tv_comment_content, 14);
			holder.tv_comment_user_name.setText(comment.getUname());
			try {
				holder.tv_comment_ctime.setText(TimeHelper.friendlyTime(comment
						.getCtime()));
			} catch (Exception e) {
				e.printStackTrace();
				holder.tv_comment_ctime.setText(comment.getCtime());
			}
		} else {
			finishAppendByErr("holder is null or comment==null");
		}
	}

}
