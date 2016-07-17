package com.thinksns.sociax.t4.android.api;

import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/** 
 * 类说明：   标签
 * 
 * @author  Zoey    
 * @date    2015年10月22日
 * @version 1.0
 */
public interface ApiTag {
	static final String MOD_NAME = "Tag";
	static final String DELETE_TAG = "deleteTag";//删除标签
	static final String ADD_TAG = "addTag";//添加标签
	static final String TAG_ALL = "tag_all";//所有标签
	static final String TAG_MY = "tag_my";//我的标签
	/**
	 * 删除标签
	 * @return
	 * @throws ApiException
	 */
	public String deleteTag(int tag_id) throws ApiException;
	/**
	 * 添加标签
	 * @return
	 * @throws ApiException
	 */
	public String addTag(String name) throws ApiException;
	/**
	 * 所有标签
	 * 
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getAllTag() throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException,
			ExceptionIllegalParameter;
	/**
	 * 我的标签
	 * 
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getMyTag() throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException,
			ExceptionIllegalParameter;
}
