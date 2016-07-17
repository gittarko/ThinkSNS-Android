package com.thinksns.sociax.t4.adapter;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/** 
 * 类说明：   
 * @author  Administrator    
 * @date    2015-1-4
 * @version 1.0
 */
public class AdapterWeibaAll extends AdapterWeibaList{
	public AdapterWeibaAll(FragmentSociax fragment, ListData<SociaxItem> list) {
		super(fragment, list);
		isHideFootToast=true;
		isShowAll = false;
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
		return new Api.WeibaApi().getAllWeibaList(PAGE_COUNT, getMaxid(), httpListener);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return new Api.WeibaApi().getAllWeibaList(PAGE_COUNT, 0, httpListener);
	}
}
