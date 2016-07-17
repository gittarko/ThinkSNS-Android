package com.thinksns.sociax.t4.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.widget.ContactItemInterface;
import com.thinksns.sociax.t4.android.widget.ContactListAdapter;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelSearchUser;

/**
 * 类说明：互相关注的好友
 * 
 * @author Zoey
 * @date 2015年11月10日
 * @version 1.0
 */
public class AdapterMyFriends extends ContactListAdapter {

	private Context mContext;
	private static int  FROM=0;
	/**
	 * 是否单选
	 */
	private boolean isSingleSelect=false;
	private List<ContactItemInterface > list;
	
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
	
	public AdapterMyFriends(Context _context, int _resource,
			List<ContactItemInterface> _items,int from,boolean isSingleSelect) {
		super(_context, _resource, _items);
		this.mContext = _context;
		this.FROM = from;
		this.list = _items;
		this.isSingleSelect = isSingleSelect;

		initSelect();

	}

	private void initSelect() {
		if(list == null)
			return;
		for (int i = 0; i < list.size(); i++) {
			isSelected.put(i, ((ModelSearchUser)getItem(i)).isSelect());
		}
	}

	public void populateDataForRow(View parentView, ContactItemInterface item,int position) {
		
		View view = parentView.findViewById(R.id.rl_user);
		TextView username = (TextView) view.findViewById(R.id.tv_username);
		ImageView head = (ImageView) view.findViewById(R.id.img_chat_userheader);
		final CheckBox cb_select=(CheckBox)view.findViewById(R.id.cb_select);

		username.setText(item.getDisplayInfo());
		
		Glide.with(mContext).load(((ModelSearchUser)item).getUserface())
		.diskCacheStrategy(DiskCacheStrategy.ALL)
		.transform(new GlideCircleTransform(mContext)).crossFade()
		.into(head);
		
		if (FROM==StaticInApp.CONTACTS_LIST_CHAT) {
			cb_select.setVisibility(View.VISIBLE);
		}
		
		parentView.setTag(R.id.tag_search_user, item);
	}
}
