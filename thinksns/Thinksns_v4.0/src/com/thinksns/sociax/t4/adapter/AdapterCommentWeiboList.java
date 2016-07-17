package com.thinksns.sociax.t4.adapter;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendCommentList;
import com.thinksns.sociax.t4.android.fragment.FragmentCommentMeWeibo;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.fragment.FragmentWeibo;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明： 评论列表基类 默认获取自己的微博列表，他人列表重写refreshNew/Footer/Header
 * 
 * @author wz
 * @date 2014-10-17
 * @version 1.0
 */
public class AdapterCommentWeiboList extends AdapterSociaxList {
	AppendCommentList append;
	String type;
	protected UnitSociax uint;// 工具类

	/**
	 * 从Fragment生成
	 * 
	 * @param fragment
	 * @param list
	 */
	public AdapterCommentWeiboList(FragmentSociax fragment,
			ListData<SociaxItem> list) {
		super(fragment, list);
		append = new AppendCommentList(context, this);
		this.uint = new UnitSociax(context);
	}

	/**
	 * 从Activity生成
	 * 
	 * @param activity
	 * @param list
	 */
	public AdapterCommentWeiboList(ThinksnsAbscractActivity activity,
			ListData<SociaxItem> list) {
		super(activity, list);
		append = new AppendCommentList(context, this);
	}

	/**
	 * 根据类型获取评论我的微博
	 * 
	 * @param type
	 *            标记评论的类型，执行网络请求时候会使用type获取user_commends_to_me
	 * @param fragment
	 * @param list
	 */
	public AdapterCommentWeiboList(FragmentSociax fragment,
			ListData<SociaxItem> list, String type) {
		super(fragment, list);
		isHideFootToast = true;
		append = new AppendCommentList(context, this);
		this.type = type;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
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
//						if(fragment instanceof FragmentCommentMeWeibo) {
//							((FragmentCommentMeWeibo)fragment).clickComment(position);
//						}
					}
				});
			}

			append.appendCommentWeiboData(position, holder, getItem(position));
		} catch (OutOfMemoryError e) {
			// 如果内存溢出，则先清理本应用的缓存，再重新加载
			((Thinksns) (context.getApplicationContext())).clearCache();
			UnitSociax uint = new UnitSociax(context);
			uint.clearAppCache();
			try {
				append.appendCommentWeiboData(position, holder,getItem(position));
			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();
			}
		}
		return convertView;
	}

	@Override
	public ModelComment getFirst() {
		return (ModelComment) super.getFirst();
	}

	@Override
	public ModelComment getLast() {
		return (ModelComment) super.getLast();
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
			return getLast().getComment_id();
	}

	@Override
	public ModelComment getItem(int position) {
		return (ModelComment) super.getItem(position);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApiWeibo().commentMeWeibo(PAGE_COUNT, 0, type, httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		}
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
		try {
			return getApiWeibo().commentMeWeibo(PAGE_COUNT, getMaxid(), type, httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取api
	 * 
	 * @return
	 */
	protected Api.WeiboApi getApiWeibo() {
		return thread.getApp().getWeiboApi();
	}

}
