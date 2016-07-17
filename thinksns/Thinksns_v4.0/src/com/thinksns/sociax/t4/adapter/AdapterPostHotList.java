package com.thinksns.sociax.t4.adapter;

import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/** 
 * 类说明：   热门帖子
 * @author  Administrator    
 * @date    2015-1-4
 * @version 1.0
 */
public class AdapterPostHotList extends AdapterPostList{
	
	public AdapterPostHotList(FragmentSociax fragment,
			ListData<SociaxItem> list) {
		super(fragment, list, null);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		// TODO Auto-generated method stub
		return refreshNew(PAGE_COUNT);
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApi().getPostHot(PAGE_COUNT,getMaxid());
		} catch (ExceptionIllegalParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		// TODO Auto-generated method stub
		
		try {
			return getApi().getPostHot(PAGE_COUNT,0);
		} catch (ExceptionIllegalParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

