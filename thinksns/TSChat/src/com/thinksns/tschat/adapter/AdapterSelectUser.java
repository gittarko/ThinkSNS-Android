package com.thinksns.tschat.adapter;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinksns.tschat.R;
import com.thinksns.tschat.base.ListBaseAdapter;
import com.thinksns.tschat.bean.ModelUser;
import com.thinksns.tschat.ui.ActivitySelectUser;
import com.thinksns.tschat.widget.UIImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类说明：选择用户列表
 * @author wz
 * @date 2014-11-18
 * @version 1.0
 */
public class AdapterSelectUser extends ListBaseAdapter<ModelUser> {

	protected int uid;
	protected Fragment fragment;

	/**
	 * 是否单选
	 */
	private boolean isSingleSelect = false;
	public static List<ModelUser> users;
	/**
	 * 
     * 类说明：多选，选择用户列表
	 * @param fragment
	 */
	public AdapterSelectUser(Fragment fragment, boolean select_type) {
		super(fragment.getActivity());
		this.fragment = fragment;
		users = new ArrayList<ModelUser>();
		this.isSingleSelect = select_type;
	}

	public static List<ModelUser> getSelectUser() {
		return users;
	}

	@Override
	public int getCount() {
		ActivitySelectUser selectUser = (ActivitySelectUser)fragment.getActivity();
		if(users.size() >0) {
			selectUser.changeButtonState(true);
		}else {
			selectUser.changeButtonState(false);
		}
		return super.getCount();
	}

	private class ViewHolder {
		ImageView tv_user_photo;
		RelativeLayout rl_select_chat_user;
		TextView tv_user_name;
		TextView tv_user_content;
		CheckBox cb_select;

	}

	@SuppressLint("NewApi") @Override
	public View getRealView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_chat_selectuser, null);
				viewHolder.tv_user_photo = (ImageView) convertView
						.findViewById(R.id.image_photo);
				viewHolder.rl_select_chat_user = (RelativeLayout) convertView
						.findViewById(R.id.rl_select_chat_user);
				viewHolder.tv_user_name = (TextView) convertView
						.findViewById(R.id.unnames);
				viewHolder.tv_user_content = (TextView) convertView
						.findViewById(R.id.uncontent);
				viewHolder.cb_select = (CheckBox) convertView
						.findViewById(R.id.cb_select);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		convertView.setTag(R.id.tag_search_user, getItem(position));
		final ModelUser user = getItem(position);
		//用户头像
		UIImageLoader.getInstance(context).displayImage(user.getUserface(), viewHolder.tv_user_photo);
		//用户名
		viewHolder.tv_user_name.setText(user.getUserName());
		//用户简介
		viewHolder.tv_user_content.setText(user.getIntro());
		viewHolder.rl_select_chat_user.setTag(R.id.tag_position, position);
		if (isSelect(getItem(position).getUid())) {
			viewHolder.cb_select.setChecked(true);
			viewHolder.cb_select.setBackgroundResource(R.drawable.checked);
		}
		else {
			viewHolder.cb_select.setChecked(false);
			viewHolder.cb_select.setBackgroundResource(R.drawable.unchecked);
		}

		viewHolder.rl_select_chat_user.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v.findViewById(R.id.cb_select);
					//如果是单选，需要把其他选择去掉
					if (isSingleSelect) {
						for (int i = 0; i < mDatas.size(); i++) {
							if (i == position)
								continue;
							//删除其余选中用户
							removeSelectUser(mDatas.get(i).getUid());
						}
						cb.setChecked(true);
						users.add(mDatas.get(position));
					} else {
						cb.toggle();
						if (cb.isChecked()) {
							users.add(getItem(position));
						} else {
							removeSelectUser(getItem(position).getUid());
						}
					}

					notifyDataSetChanged();

				}
		});
		return convertView;
	}

	private boolean isSelect(int uid) {
		if(users.size() == 0)
			return false;
		for(int i= 0,j=users.size(); i<j; i++) {
			if(users.get(i).getUid() == uid)
				return true;
		}
		return false;
	}

	private void removeSelectUser(int uid) {
		for(int i=0; i<users.size(); i++) {
			if(users.get(i).getUid() == uid) {
				users.remove(i);
				i--;
			}
		}
	}

	public ModelUser getLast() {
		if (mDatas.size() > 0) {
			return (ModelUser) this.mDatas.get(this.mDatas.size() - 1);
		} else
			return null;
	}

//	@Override
//	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
//			throws VerifyErrorException, ApiException, ListAreEmptyException,
//			DataInvalidException {
//		return refreshNew(PAGE_COUNT);
//	}

//	@Override
//	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
//			throws VerifyErrorException, ApiException, ListAreEmptyException,
//			DataInvalidException {
//		return getApiUser().getUserFriendsList(uid, getMaxid(), httpListener);
//	}
//
//	@Override
//	public ListData<SociaxItem> refreshNew(int count)
//			throws VerifyErrorException, ApiException, ListAreEmptyException,
//			DataInvalidException {
//		return getApiUser().getUserFriendsList(uid, 0, httpListener);
//	}

	@Override
	public ModelUser getItem(int position) {
		return this.mDatas.get(position);
	}
}
