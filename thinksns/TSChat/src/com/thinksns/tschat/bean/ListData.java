package com.thinksns.tschat.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ListData<T extends Entity> extends ArrayList<Entity>
		implements Serializable {
	private static final long serialVersionUID = 12L;

	public static enum Position {
		BEGINING, END
	}

	public static enum DataType {
		COMMENT, WEIBO, MY_WEIBO, ALL_WEIBO, FRIENDS_WEIBO, RECOMMEND_WEIBO, 
		ATME_WEIBO, USER, RECEIVE, FOLLOW, SEARCH_USER,
		/************ t4 wz QQ37717239 **************/
		MODEL_CHANNEL, MODEL_COMMENT, MODEL_GIFT, MODEL_TOPIC,MODEL_SHOP_GIFT,
		MODEL_POST, MODEL_MEDAL, MODEL_FEEDBACK
	}

}
