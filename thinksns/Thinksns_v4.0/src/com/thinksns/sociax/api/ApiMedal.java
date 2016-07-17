package com.thinksns.sociax.api;

import com.thinksns.sociax.thinksnsbase.exception.ApiException;

/**
 * 类说明：
 * 
 * @author Zoey
 * @date 2015年9月9日
 * @version 1.0
 */
public interface ApiMedal {
	
	static final String MOD_NAME = "Medal";
	static final String ALL_MEDALS = "getAll";// 获得全部勋章
	static final String MY_MEDAL = "getUser";// 获得我的勋章
	
	public String getAllMedals() throws ApiException;
	public String getMyMedal(int uid) throws ApiException;
}
