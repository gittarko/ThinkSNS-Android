package com.thinksns.sociax.api;

import com.thinksns.sociax.t4.model.ModelAds;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年8月28日
 * @version 1.0
 */
public interface ApiPublic {
	
	static final String MOD_NAME = "Public";
	static final String SHOW_ABOUT_US = "showAbout";//关于我们
	static final String GET_ADS = "getSlideShow";//发现页轮播图
	
	public String showAboutUs() throws ApiException;
	public ListData<ModelAds> getAds() throws ApiException;
}
