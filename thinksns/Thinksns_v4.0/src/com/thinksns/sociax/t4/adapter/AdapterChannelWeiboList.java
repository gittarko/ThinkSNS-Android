package com.thinksns.sociax.t4.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendWeibo;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-10-15
 * @version 1.0
 */
public class AdapterChannelWeiboList extends AdapterWeiboList {
	protected int channel_id = 0;
	
	protected AppendWeibo append;// 数据映射
	protected Api.WeiboApi apiwebo;

	public FragmentSociax getFragment() {
		return fragment;
	}

	public void setFragment(FragmentSociax fragment) {
		this.fragment = fragment;
	}

	@Override
	public ModelWeibo getFirst() {
		return (ModelWeibo) super.getFirst();
	}

	@Override
	public ModelWeibo getLast() {
		return (ModelWeibo) super.getLast();
	}
	
	public AdapterChannelWeiboList(ThinksnsAbscractActivity context,
								   ListData<SociaxItem> list, int uid) {
		super(context, list, uid);
	}

	/**
	 * 获取最后一条的id
	 * 
	 * @return
	 */
	public int getMaxid() {
		if (getLast() == null)
			return 0;
		else
			Log.d(TAG, "刷新尾部的最大 id  " + getLast().getWeiboId());
		return getLast().getWeiboId();
	}

	@Override
	public ModelWeibo getItem(int position) {
		return (ModelWeibo) super.getItem(position);
	}
	
	/**
	 * 根据频道Activity微博列表
	 * 
	 * @param context
	 * @param list
	 * @param channel_id
	 */
	public AdapterChannelWeiboList(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list, int channel_id, int uid) {
		super(context, list, uid);
		this.channel_id = channel_id;
		append = new AppendWeibo(context, this);
	}

	/**
	 * 从fragment生成的微博adapter
	 * 
	 * @param fragment
	 * @param list
	 */
	public AdapterChannelWeiboList(FragmentSociax fragment,
			ListData<SociaxItem> list, int channel_id, int uid) {
		super(fragment, list, uid);
		this.channel_id = channel_id;
		append = new AppendWeibo(context, this);
	}

	
	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getChannelApi().getChannelWeibo(String.valueOf(channel_id), 0, count, 0, httpListener);
		} catch (ExceptionIllegalParameter exceptionIllegalParameter) {
			exceptionIllegalParameter.printStackTrace();
		}
		return null;
	}

	@Override
	public ListData<SociaxItem> refreshFooter(
			SociaxItem obj) throws VerifyErrorException, ApiException,
			ListAreEmptyException, DataInvalidException {
		try {
			return getChannelApi().getChannelWeibo(String.valueOf(channel_id), getMaxid(), PAGE_COUNT, 0, httpListener);
		} catch (ExceptionIllegalParameter exceptionIllegalParameter) {
			exceptionIllegalParameter.printStackTrace();
		}
		return null;
	}

	public ListData<SociaxItem> refreshHeader(
			SociaxItem obj) throws VerifyErrorException, ApiException,
			ListAreEmptyException, DataInvalidException {
		return refreshNew(PAGE_COUNT); 
	};

	Api.ChannelApi getChannelApi() {
		return thread.getApp().getChannelApi();
	}
}
