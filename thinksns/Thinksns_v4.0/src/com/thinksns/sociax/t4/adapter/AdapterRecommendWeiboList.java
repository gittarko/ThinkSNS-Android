package com.thinksns.sociax.t4.adapter;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendWeibo;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

/**
 * 类说明： 推荐微博list与其他weibolist的区别在需要设置背景图片，利用Append内方法区分
 * 
 * @author wz
 * @date 2014-10-16
 * @version 1.0
 */
public class AdapterRecommendWeiboList extends AdapterWeiboList {
	AppendWeibo append;

	public AdapterRecommendWeiboList(ThinksnsAbscractActivity context,
									 ListData<SociaxItem> list, int uid) {
		super(context, list, uid);
	}

	/**
	 * 从fragment生成的微博adapter
	 * 
	 * @param fragment
	 * @param list
	 */
	public AdapterRecommendWeiboList(FragmentSociax fragment,
			ListData<SociaxItem> list, int uid) {
		super(fragment, list, uid);
		append = new AppendWeibo(context, this);
		isHideFootToast = true;
	}


	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApiWeibo().recommendTimeline(PAGE_COUNT, 0, httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != completeListener) {
				completeListener.onRefreshComplete();
			}
		}
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			//如果网络连接错误，获取本地数据
			//后期考虑设置缓存时间，如果缓存失效从网络获取
			if (!UnitSociax.isNetWorkON(context)) {
				return DbHelperManager.getInstance(context, ListData.DataType.RECOMMEND_WEIBO).getFooterData(10, getMaxid());
			}
			return getApiWeibo().recommendTimeline(PAGE_COUNT, getMaxid(), httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != completeListener) {
				completeListener.onRefreshComplete();
			}
		}
	}

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return refreshNew(PAGE_COUNT);
	}

//	@Override
//	public void addFooter(ListData<SociaxItem> list) {
//		super.addFooter(list);
//		if (this.list.size() == 0) {
//			if (context.getListView() != null)
//				((ListSociax) context.getListView())
//						.setBackgroundResource(R.drawable.bg_no_following_weiba);
//		} else {
//			if (context.getListView() != null)
//				((ListSociax) context.getListView()).setBackgroundResource(0);
//		}
//	}
}
