package com.thinksns.sociax.t4.adapter;

import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/** 
 * 类说明：   逛一逛
 * @author  wz    
 * @date    2015-2-5
 * @version 1.0
 */
public class AdapterPostAllList extends AdapterPostList{

	public AdapterPostAllList(FragmentSociax fragment,
							  ListData<SociaxItem> list, int weiba_id) {
		super(fragment, list, weiba_id, null);
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
			return getApi().getPostAll(weiba_id,PAGE_COUNT,getMaxid());
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
		try {
			return getApi().getPostAll(weiba_id,PAGE_COUNT,0);
		} catch (ExceptionIllegalParameter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
