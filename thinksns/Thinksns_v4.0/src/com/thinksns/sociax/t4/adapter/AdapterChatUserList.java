package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.findpeople.ActivitySelectUser;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-28
 * @version 1.0
 */
public class AdapterChatUserList extends BaseAdapter {
	private Context context;
	ListData<ModelSearchUser> list;
	LayoutInflater inflater;
	private int room_id;
	private String type = "show";// 当前adapter类型：show展示群成员列表，delete，删除群成员
	private Thinksns application;
	/**
	 * 当前adapter类型：show展示群成员列表，delete，删除群成员
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 当前adapter类型：show展示群成员列表，delete，删除群成员
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @param context
	 * @param list
	 * @param type
	 *            当前adapter类型：show展示群成员列表，delete，删除群成员
	 * @param room_id
	 *            房间id
	 */
	public AdapterChatUserList(Context context, ListData<ModelSearchUser> list,
			String type, int room_id) {
		this.context = context;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
		this.type = type;
		this.room_id = room_id;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ModelSearchUser getItem(int position) {
		return (ModelSearchUser) list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder;
		
		application = (Thinksns) context.getApplicationContext();
		
		if (convertView == null) {
			holder = new HolderSociax();
			convertView = inflater.inflate(R.layout.item_chat_info_user_old, null);
			holder.img_user_header = (ImageView) convertView
					.findViewById(R.id.img_header);			
			holder.tv_user_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			holder.img_delete = (ImageView) convertView
					.findViewById(R.id.img_delete);
			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}
		if (getItem(position).getId() == -1) {// 添加图标
			holder.img_delete.setVisibility(View.GONE);
			holder.img_user_header.setBackgroundResource(R.drawable.tv_add_chat_user);
			holder.tv_user_name.setText("");
			holder.img_user_header.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							ActivitySelectUser.class);
					intent.putExtra("select_type", StaticInApp.CHAT_ADD_USER);
//					((ActivityChatInfo) context).startActivityForResult(intent,
//							StaticInApp.CHAT_ADD_USER);
				}
			});
		} else if (getItem(position).getId() == -2) {// 删除图标
			holder.img_delete.setVisibility(View.GONE);
			holder.img_user_header.setBackgroundResource(R.drawable.tv_delete_chat_user);
			holder.tv_user_name.setText("");
			holder.img_user_header.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					changeAdapterType();
				}
			});
		} else {
			if (getItem(position).getUid() == Thinksns.getMy().getUid()) {//不能删除自己
				holder.img_delete.setVisibility(View.GONE);
				
				Glide.with(context).load(getItem(position).getUface())
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(context.getResources().getDrawable(R.drawable.default_user))
				.error(context.getResources().getDrawable(R.drawable.default_user))
				.transform(new GlideCircleTransform(context))
				.crossFade()
				.into(holder.img_user_header);
				
				holder.tv_user_name.setText(getItem(position).getUname());
				holder.img_user_header.setTag(R.id.tag_search_user,
						getItem(position));
				holder.img_user_header
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (type.equals("show")) {
									Intent intent = new Intent(context,
											ActivityUserInfo_2.class);
									intent.putExtra("uid", ((ModelSearchUser) v
											.getTag(R.id.tag_search_user))
											.getUid());
//									((ActivityChatInfo) context)
//											.startActivity(intent);
								}
							}
						});

			} else {
				if (type.equals("delete")) {
					holder.img_delete.setVisibility(View.VISIBLE);
				} else {
					holder.img_delete.setVisibility(View.GONE);
				}
				
				Glide.with(context).load(getItem(position).getUface())
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(context.getResources().getDrawable(R.drawable.default_user))
				.error(context.getResources().getDrawable(R.drawable.default_user))
				.transform(new GlideCircleTransform(context))
				.crossFade()
				.into(holder.img_user_header);
				
				holder.tv_user_name.setText(getItem(position).getUname());
				holder.img_user_header.setTag(R.id.tag_search_user,
						getItem(position));
				holder.img_user_header
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (type.equals("show")) {//展示成员，则点击头像直接进入用户主页
									Intent intent = new Intent(context,ActivityUserInfo_2.class);
									intent.putExtra("uid", ((ModelSearchUser) 
											v.getTag(R.id.tag_search_user))
											.getUid());
//									((ActivityChatInfo) context).startActivity(intent);
								} else {//删除成员，调用删除
									//注释
//									((Thinksns)(((ActivityChatInfo) context).getApplicationContext()))
//											.getChatSocketClient()
//											.deleteMember(room_id,((ModelSearchUser)v.getTag(R.id.tag_search_user)).getUid());
								}
							}
						});
			}
		}
		return convertView;
	}
	/**
	 * 修改adapter类型
	 */
	protected void changeAdapterType() {
		if (type.equals("show")) {
			type = "delete";
		} else {
			type = "show";
		}
		this.notifyDataSetChanged();
	}
}
