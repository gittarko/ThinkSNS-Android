package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：   
 * @author  Zoey    
 * @date    2015年9月10日
 * @version 1.0
 */
public class ModelMainTask extends SociaxItem {

	String id;
	String name;
	String desc;
	int exp;
	int score;
	boolean iscomplete;
	boolean receive;
	String surplus;
	
	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}
}
