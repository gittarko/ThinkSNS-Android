package com.thinksns.sociax.api;

import com.thinksns.sociax.modle.Contact;
import com.thinksns.sociax.modle.ContactCategory;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

public interface ApiContact {

	public static final String MOD_NAME = "Contact";

	// 获取所有联系人列表
	public static final String GET_ALL_COLLEAGUE = "get_all_colleague";
	// 获取我的联系人列表
	public static final String GET_MY_CONTACTER = "get_my_contacter";

	// 返回部门列表
	public static final String GET_DEPARTMENT_LIST = "get_department_list";
	/**
	 * 部门下联系人
	 */
	public static final String GET_COLLEAGUE_BY_DEPARTMENT = "get_colleague_by_department";

	public static final String GET_DATA_BY_DEPARTMENT = "get_data_by_department";

	public static final String CONTACTER_CREATE = "contacter_create";

	public static final String CONTACTER_DESTROY = "contacter_destroy";

	public static final String SEARCH_COLLEAGUE = "search_colleague";

	/**
	 * 返回部门列表
	 * 
	 * @param departId
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> getContactCategoryList(int departId)
			throws ApiException;

	/**
	 * 返回我的联系人
	 * 
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> getMyContacter() throws ApiException;

	ListData<SociaxItem> getAllContactList() throws ApiException;

	ListData<SociaxItem> getContactListFooter(Contact contact, int count)
			throws ApiException;

	/**
	 * 根据部门id返回联系人
	 * 
	 * @param departId
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> getColleagueByDepartment(int departId)
			throws ApiException;

	ListData<SociaxItem> getColleagueByDepartmentFooter(int departId,
			ContactCategory category, int count) throws ApiException;

	ListData<SociaxItem> getDataByDepartment(int departId, int isDepart)
			throws ApiException;

	ListData<SociaxItem> getDataByDepartmentFooter(int departId, int isDepart)
			throws ApiException;

	/**
	 * 收藏联系人
	 * 
	 * @param user
	 * @throws ApiException
	 */
	boolean contacterCreate(ModelUser user) throws ApiException;

	/**
	 * 取消收藏联系人
	 * 
	 * @param user
	 * @throws ApiException
	 */
	boolean contacterDestroy(ModelUser user) throws ApiException;

	/**
	 * 搜索联系人
	 * 
	 * @param key
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> searchColleague(String key) throws ApiException;

}
