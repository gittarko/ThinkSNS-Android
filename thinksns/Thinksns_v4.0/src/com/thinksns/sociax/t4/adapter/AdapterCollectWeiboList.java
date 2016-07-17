package com.thinksns.sociax.t4.adapter;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明：
 * 
 * @author Administrator
 * @date 2014-11-10
 * @version 1.0
 */
public class AdapterCollectWeiboList extends AdapterWeiboList {
	int uid = Thinksns.getMy().getUid();

	/**
	 * Fragment生成
	 * 
	 * @param fragment
	 * @param list
	 */
	public AdapterCollectWeiboList(FragmentSociax fragment,
								   ListData<SociaxItem> list, int uid) {
		super(fragment, list, uid);
		this.uid = uid;
	}

	public AdapterCollectWeiboList(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list, int uid) {
		super(context, list, uid);
		this.uid = uid;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApiWeibo().collectWeibo(uid, PAGE_COUNT, 0, httpListener);
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
			return getApiWeibo().collectWeibo(uid, PAGE_COUNT, getMaxid(), httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		}
	}
}
