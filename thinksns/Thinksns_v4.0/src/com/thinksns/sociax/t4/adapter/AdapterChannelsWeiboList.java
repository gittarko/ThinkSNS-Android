package com.thinksns.sociax.t4.adapter;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendWeibo;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

/**
 * 类说明： 频道微博list与其他weibolist的区别在需要设置背景图片，利用Append内方法区分
 * 
 * @author wz
 * @date 2014-10-16
 * @version 1.0
 */
public class AdapterChannelsWeiboList extends AdapterWeiboList {
	AppendWeibo append;
	private boolean mBusy = false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public AdapterChannelsWeiboList(ThinksnsAbscractActivity context,
									ListData<SociaxItem> list, int uid) {
		super(context, list, uid);
	}

	/**
	 * 从fragment生成的微博adapter
	 *
	 * @param fragment
	 * @param list
	 */
	public AdapterChannelsWeiboList(FragmentSociax fragment,
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
			return getApiWeibo().channelsTimeline(PAGE_COUNT, 0, httpListener);
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
				return DbHelperManager.getInstance(context, ListData.DataType.CHANNELS_WEIBO).getFooterData(10, getMaxid());
			}
			return getApiWeibo().channelsTimeline(PAGE_COUNT, getMaxid(), httpListener);
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

}
