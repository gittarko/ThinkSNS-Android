package com.thinksns.sociax.api;


import com.thinksns.sociax.thinksnsbase.exception.ApiException;

/**
 * 类说明：
 * 
 * @author povol
 * @date 2013-2-6
 * @version 1.0
 */
public interface ApiCheckin {

	public static final String MOD_NAME = "Checkin";

	public static final String CHECKIN = "checkin";

	public static final String GET_CHECK_INFO = "get_check_info";

	public static final String RANK = "rank";

	public Object checkIn() throws ApiException;

	public Object getCheckInfo() throws ApiException;
	
	/**
	 * 获取签到排行榜
	 * @return
	 * @throws ApiException
	 */
	Object getCheckRankList() throws ApiException;

	public void setLocationInfo(double latitude, double longitude) throws ApiException;

}
