package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendCommentList;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * 类说明：点赞列表适配器
 * @author wz
 * @date 2014-10-17
 * @version 1.0
 */
public class AdapterDiggMeWeiboList extends ListBaseAdapter<ModelComment> {
	private AppendCommentList append;
	private String type;
	protected UnitSociax uint;		// 工具类
	protected ListView mListView;

	public AdapterDiggMeWeiboList(Context context, String type, ListView listView) {
		super(context);
		append = new AppendCommentList(context, null);
		this.uint = new UnitSociax(context);
		this.type = type;
		this.mListView = listView;
	}

	@Override
	public int getMaxId() {
		return (mDatas == null || mDatas.size() ==0) ? 0 :
				((ModelComment)mDatas.get(mDatas.size() -1)).getDigg_id();
	}

	@Override
	protected View getRealView(final int position, View convertView, final ViewGroup parent) {
		HolderSociax holder = null;
		if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
			LayoutInflater inflater = getLayoutInflater(mContext);
			convertView = inflater.inflate(R.layout.listitem_commentme_weibo,
					null);
			holder = append.initHolder(convertView, 0);
			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		convertView.setTag(R.id.tag_weibo, getItem(position));

		try {
			if(type != null && type.equals("digger")) {
				holder.iv_dig_icon.setVisibility(View.VISIBLE);
				holder.tv_comment_content.setVisibility(View.GONE);
			}else {
				holder.iv_dig_icon.setVisibility(View.GONE);
				holder.tv_comment_content.setVisibility(View.VISIBLE);
				//设置评论内容点击事件
				holder.tv_comment_content.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ListView listView = (ListView)parent;
						listView.performItemClick(v, position, position);
					}
				});
			}

			append.appendCommentWeiboData(position, holder, getItem(position));

		} catch (OutOfMemoryError e) {
			// 如果内存溢出，则先清理本应用的缓存，再重新加载
			((Thinksns) (mContext.getApplicationContext())).clearCache();
			UnitSociax uint = new UnitSociax(mContext);
			uint.clearAppCache();
			try {
				append.appendCommentWeiboData(position, holder,getItem(position));
			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();
			}
		}

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				ModelComment md = (ModelComment)v.getTag(R.id.tag_weibo);
//				if (md == null || md.getWeibo() == null) {
//					return;
//				}
//
//				Bundle data = new Bundle();
//				Intent intent = null;
//				if (md.getWeibo().getType().equals("weiba_post")) {
//					intent = new Intent(mContext,ActivityPostDetail.class);
//					data.putInt("post_id", md.getWeibo().getSid());
//				}else {
//					intent = new Intent(mContext, ActivityWeiboDetail.class);
//					data.putInt("weibo_id", md.getWeibo() == null ? md.getFeed_id() : md
//							.getWeibo().getWeiboId());
//				}
//
//				data.putSerializable("comment", md);
//				intent.putExtras(data);
//				mContext.startActivity(intent);
				mListView.performItemClick(v, position, position);
			}
		});

		//点击源内容进入源详情
		holder.tv_source_weibo_content.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListView.performItemClick(v, position, position);
			}
		});

		return convertView;
	}
}
