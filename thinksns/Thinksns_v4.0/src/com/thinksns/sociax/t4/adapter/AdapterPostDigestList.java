package com.thinksns.sociax.t4.adapter;

import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.exception.*;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;


/** 
 * 类说明：精华帖   
 * @author  wz    
 * @date    2014-12-26
 * @version 1.0
 */
public class AdapterPostDigestList extends AdapterPostList{

	public AdapterPostDigestList(FragmentSociax fragment,
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
			return getApi().getPostDigest(weiba_id,PAGE_COUNT,getMaxid(), httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApi().getPostDigest(weiba_id,PAGE_COUNT,0, httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
		}
		return null;
	}

}
