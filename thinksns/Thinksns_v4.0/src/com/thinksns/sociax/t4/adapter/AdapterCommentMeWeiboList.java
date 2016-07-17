package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.widget.ListView;

import com.thinksns.sociax.t4.model.ModelComment;

/**
 * 类说明：评论我的
 * 
 * @author wz
 * @date 2014-10-17
 * @version 1.0
 */
public class AdapterCommentMeWeiboList extends AdapterDiggMeWeiboList {

	public AdapterCommentMeWeiboList(Context context, String type, ListView listView) {
		super(context, type, listView);
	}

	@Override
	public int getMaxId() {
		if(mDatas == null || mDatas.size() == 0)
			return 0;
		return ((ModelComment)mDatas.get(mDatas.size()-1)).getComment_id();
	}
}
