package com.thinksns.sociax.t4.android.api;

import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;

/**
 * 类说明： 礼物类接口
 * 
 * @author wz
 * @date 2014-11-13
 * @version 1.0
 */
public interface ApiGift {
	static final String MOD_NAME = "Gift";
	static final String GIFT_ALL = "gift_all";
	static final String BUY_GIFT = "exchange";
	static final String GIFT_MY = "gift_my";
	static final String GIFT_USER = "user_gift";
	static final String SEND_GIFT = "send_gift";
	static final String RESEND_GIFT = "resend_gift";
	
	/******************t4*********************/
	static final String GET_SHOP_GIFT = "getList";//获取积分商城礼物列表
	static final String GET_GIFT_DETAIL = "getInfo";//获取礼物详情
	static final String EXCHANGE_GIFT = "buy";//兑换礼物
	static final String GET_MY_GIFTS = "getLog";//我的礼物
	static final String TRANSFER_MY_GIFT = "transfer";//礼物转赠
	/**
	 * 获取所有礼物列表
	 * 
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getAllGift(int max_id) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException,
			ExceptionIllegalParameter;

	/**
	 * 获取我的礼物列表
	 * 
	 * @param max_id
	 *            最后一个礼物id
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getMyGift(int max_id, HttpResponseListener listener) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException,
			ExceptionIllegalParameter;

	/**
	 * 获取某个用户的礼物列表
	 * 
	 * @param uid
	 * @param max_id
	 *            最后一个礼物id
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getUerGift(int uid, int max_id) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException,
			ExceptionIllegalParameter;

	/**
	 * 赠送礼物
	 * 
	 * @param uids
	 *            用户uids,(逗号隔开)
	 * @param sendinfo
	 *            附加信息
	 * @param sendWay
	 *            发送方式 1-所有人都能看见你的名字、赠送的礼物和附加消息 2-只有接收礼物的人能看见你的名字和附加消息
	 *            3-接收礼物的人只能看见你的附加消息，不显示你的名字
	 * @return
	 */

	Object sentGift(String gift_id, String uids, String sendinfo, String sendWay);
	
	/****************t4*******************/
	
	/**
	 * 获取实体礼物/虚拟礼物列表
	 * 
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 * @throws ExceptionIllegalParameter
	 */
	void getShopGift(int page, int num, String cate, final ApiHttpClient.HttpResponseListener listener);
	void getShopGift(int page, int num, final ApiHttpClient.HttpResponseListener listener);

	public String getGiftDetail(String id) throws ApiException;
	/**
	 * 兑换礼物
	 * 
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 * @throws ExceptionIllegalParameter
	 */
	public String exchangeGift(String id,int uid,int num,String addres,String say,String type,String phone,String name) throws ApiException;
	
	/**
	 * 获取我的礼物列表
	 * 
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getMyGifts(int page,String type, ApiHttpClient.HttpResponseListener listener) throws ApiException,
	ListAreEmptyException, DataInvalidException, VerifyErrorException;
	/**
	 * 转赠礼物
	 * 
	 * @return
	 * @throws ApiException
	 */
	public String transferMyGift(String logId,int uid,String say,int num,String type) throws ApiException;
}
