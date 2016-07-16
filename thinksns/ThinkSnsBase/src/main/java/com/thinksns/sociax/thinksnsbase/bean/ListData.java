package com.thinksns.sociax.thinksnsbase.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ListData<T extends SociaxItem> extends ArrayList<SociaxItem>
		implements Serializable {
	private static final long serialVersionUID = 12L;

	public static enum Position {
		BEGINING, END
	}

	public static enum DataType {
		COMMENT, WEIBO, MY_WEIBO, ALL_WEIBO, FRIENDS_WEIBO, RECOMMEND_WEIBO, CHANNELS_WEIBO,
		ATME_WEIBO, USER, RECEIVE, FOLLOW, SEARCH_USER,
		MODEL_CHANNEL, MODEL_COMMENT, MODEL_GIFT, MODEL_TOPIC,MODEL_SHOP_GIFT,
		MODEL_POST, MODEL_MEDAL, MODEL_FEEDBACK
	}

	public ListData(ListData<T> objs) {
		super(objs);
	}

	public ListData() {
		super();
	}
}
