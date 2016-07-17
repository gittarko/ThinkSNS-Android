package com.thinksns.sociax.t4.adapter;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.api.Api.Users;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明：选择用户列表
 * @author wz
 * @date 2014-11-18
 * @version 1.0
 */
public class AdapterSelectUser extends AdapterSociaxList {

	protected int uid;
	/**
	 * 是否单选
	 */
	private boolean isSingleSelect=false;
	/**
	 * 
	 * 从fragment中生成
	 * 
	 * @param fragment
	 * @param list
	 * @param uid
	 *            获取该uid的信息
	 */
	public static Map<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();
	/**
	 * 
     * 类说明：多选，选择用户列表
	 * @param fragment
	 * @param list
	 * @param uid
	 */
	public AdapterSelectUser(FragmentSociax fragment,
							 ListData<SociaxItem> list, int uid, boolean isSingleSelect) {
		super(fragment, list);
		this.fragment = fragment;
		this.uid = uid;
		this.isSingleSelect=isSingleSelect;
		for (int i = 0; i < list.size(); i++) {
			isSelected.put(i, getItem(i).isSelect());
		}
	}

	public AdapterSelectUser(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final HolderSociax viewHolder;
		if (convertView == null) {
			viewHolder = new HolderSociax();
			convertView = inflater.inflate(R.layout.listitem_chat_selectuser, null);
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
			convertView.setTag(R.id.tag_viewholder, viewHolder);
		} else {
			viewHolder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}
		convertView.setTag(R.id.tag_search_user, getItem(position));
		
		Glide.with(context).load(getItem(position).getUserface())
		.diskCacheStrategy(DiskCacheStrategy.ALL)
		.transform(new GlideCircleTransform(context))
		.placeholder(context.getResources().getDrawable(R.drawable.default_user))
		.error(context.getResources().getDrawable(R.drawable.default_user))		
		.crossFade()
		.into(viewHolder.tv_user_photo);
		
		viewHolder.tv_user_name.setText(getItem(position).getUname());
		viewHolder.tv_user_content.setText(getItem(position).getIntro());
		
//		viewHolder.cb_select.setTag(R.id.tag_position, position);
		viewHolder.cb_select.setTag(R.id.tag_follow, getItem(position));
		
		viewHolder.rl_select_chat_user.setTag(R.id.tag_position, position);
		
		viewHolder.cb_select.setChecked(getItem(position).isSelect());
		viewHolder.rl_select_chat_user.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean preSelect = getItem(position).isSelect();
				getItem(position).setSelect(!preSelect);
				viewHolder.cb_select.setChecked(!preSelect);
				//如果是单选，需要把其他选择去掉
				if(isSingleSelect){
					for (int i = 0; i < list.size()&&(i!=position); i++) {
						isSelected.put(i,false);
						getItem(i).setSelect(false);
					}
					notifyDataSetChanged();
				}
			}
		});

		return convertView;
	}

	@Override
	public int getMaxid() {
		if (getLast() == null)
			return 0;
		else
			return getLast().getFollow_id();
	}

	@Override
	public ModelSearchUser getLast() {
		if (list.size() > 0) {
			return (ModelSearchUser) this.list.get(this.list.size() - 1);
		} else
			return null;
	}

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return refreshNew(PAGE_COUNT);
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return getApiUser().getUserFriendsList(uid, getMaxid(), httpListener);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return getApiUser().getUserFriendsList(uid, 0, httpListener);
	}

	@Override
	public ModelSearchUser getItem(int position) {
		return (ModelSearchUser) this.list.get(position);
	}

	Users getApiUser() {
		return thread.getApp().getUsers();
	}

	@Override
	public void addHeader(ListData<SociaxItem> list) {
		super.addHeader(list);
		if (null != this.list) {
			for (int i = 0; i < this.list.size(); i++) {
				isSelected.put(i, getItem(i).isSelect());
			}
		}
	}

	@Override
	public void addFooter(ListData<SociaxItem> list) {
		super.addFooter(list);
		if (null != this.list) {
			for (int i = 0; i < this.list.size(); i++) {
				isSelected.put(i, getItem(i).isSelect());
			}
		}
	}
}
