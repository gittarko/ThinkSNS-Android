/*
 * Copyright (C) 2011 Patrik �kerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thinksns.sociax.t4.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.channel.ActivityChannelWeibo;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weiba.ActivityWeibaDetail;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.model.ModelAds;
import com.thinksns.sociax.android.R;

/**
 * 
 * 终端店首页的滚动图片的适配器
 * 
 * @author caoligai
 * 
 */
public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	ArrayList<ModelAds> datas;
	private Thinksns application;

	public ImageAdapter(Context context, LayoutInflater inflater,ArrayList<ModelAds> datas) {
		this.mContext = context;
		this.mInflater=inflater;
		this.datas = datas;

		if (context!=null) {
			application = (Thinksns) context.getApplicationContext();
		}
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;// 返回很大的�?使得getView中的position不断增大来实现循�?
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.viewflow_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.iv_viewflow_item);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final ModelAds ads=datas.get(position % datas.size());
		if (ads.getImageUrl()!=null) {
			application.displayImage(ads.getImageUrl(),holder.image);
		}
		
		holder.image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=null;
				String data=ads.getData();
				//跳转到相应的微吧
				if (ads.getType().equals("weiba")) {
					
					intent=new Intent(mContext,ActivityWeibaDetail.class);
					intent.putExtra("weiba_id",Integer.parseInt(data));
					
				}
				//跳转到相应的帖子
				else if(ads.getType().equals("post")){
					
					intent=new Intent(mContext,ActivityPostDetail.class);
					intent.putExtra("post_id",Integer.parseInt(data));
				}
				//展示图片即可
				else if(ads.getType().equals("false")){
					
					
					
				}
				//跳转到外部url
				else if(ads.getType().equals("url")){
					
					intent=new Intent(mContext,NetActivity.class);
					intent.putExtra("url", data);
					
				}
				//跳转到相应的微博详情页
				else if(ads.getType().equals("weibo")){
					
					intent=new Intent(mContext,ActivityWeiboDetail.class);
					intent.putExtra("weibo_id", data);
					
				}
				//跳转到某话题下
				else if(ads.getType().equals("topic")){
					
					intent=new Intent(mContext,ActivityChannelWeibo.class);
					intent.putExtra("topic_name", data);
					
				}
				//跳转到某频道页面
				else if(ads.getType().equals("channel")){
					
					intent=new Intent(mContext,ActivityChannelWeibo.class);
					intent.putExtra("channel_id", Integer.parseInt(data));
					
				}
				//跳转到相应的用户
				else if(ads.getType().equals("user")){
					
					intent=new Intent(mContext,ActivityUserInfo_2.class);
					intent.putExtra("uid", Integer.parseInt(data));
					
				}
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}

	public final class ViewHolder {
		public ImageView image;
		public TextView summary;
	}
}
