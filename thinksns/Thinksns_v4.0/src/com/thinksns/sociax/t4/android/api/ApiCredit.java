package com.thinksns.sociax.t4.android.api;


import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

/** 
 * 类说明： 积分类接口
 *   
 * @author  Zoey    
 * @date    2015年9月25日
 * @version 1.0
 */
public interface ApiCredit {
	static final String MOD_NAME = "Credit";
	static final String SCORE_DETAIL = "detail";
	static final String SCORE_TRANSFER = "transfer";
	static final String SCORE_RULE = "rule";
	static final String CREATE_CHARGE = "createCharge";//创建订单
	static final String SAVE_CHARGE = "saveCharge";//设置充值状态
	
	/**
	 * 获取所有积分列表
	 * 
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> getScoreDetail(int max_id, int limit, final HttpResponseListener listeenr) throws ApiException;
	/**
	 * 积分转账
	 * 
	 * @return
	 * @throws ApiException
	 */
	public String transferMyScore(int to_uid,int num,String desc) throws ApiException;
	/**
	 * 获取积分规则
	 * 
	 * @return
	 * @throws ApiException
	 */
	public ListData<SociaxItem> getScoreRule(HttpResponseListener listener) throws ApiException;
	/**
	 * 创建订单
	 * 
	 * @return
	 * @throws ApiException
	 */
	public String createCharge(double money,int type) throws ApiException;
	/**
	 *设置充值状态
	 * 
	 * @return
	 * @throws ApiException
	 */
	public String saveCharge(String serial_number,int status,String sign) throws ApiException;
}
