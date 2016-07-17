package com.thinksns.sociax.t4.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/**
 * 类说明：频道抽象类
 * 
 * @author wz
 * @date Dec 10, 2014
 * @version 1.0
 */
public class ModelChannel extends SociaxItem {

	private int id;// 频道id
	private String cName;// 频道名称
	private Integer sortId = 0;// 分类，所有频道里面有分类
	private int is_follow;		// 是否已经关注，所有频道里面用到
	private JSONArray image;// 所有频道的频道列表是一个jsonArray数组，大小为4，代表一个大图和3个小图
	private String userChannelImageUrl,// 用户关注的频道是只有一个image ，url
			count;// 用户关注的频道内，表示产生多少条数据

	public String getUserChannelImageUrl() {
		return userChannelImageUrl;
	}

	public void setUserChannelImageUrl(String userChannelImageUrl) {
		this.userChannelImageUrl = userChannelImageUrl;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public ModelChannel() {
	}

	/**
	 * 所有频道时候使用本方法构造频道
	 * 
	 * @param data
	 * @throws DataInvalidException
	 * @throws JSONException
	 */
	public ModelChannel(JSONObject data) throws DataInvalidException,
			JSONException {
		super(data);
		if (data.has("channel_category_id"))
		setId(data.getInt("channel_category_id"));
		if (data.has("title"))
			setcName(data.getString("title"));
		if (data.has("sort"))
			setSortId(data.getInt("sort"));
		if (data.has("image"))
			setUserChannelImageUrl(data.getString("image"));
		if (data.has("is_follow"))
			setIs_follow(data.getInt("is_follow"));
		if (data.has("count")) {
			setCount(data.getInt("count") + "");
		}
	}

	/**
	 * 用户的频道使用本方法构造频道
	 * 
	 * @param data
	 * @param type
	 * @throws DataInvalidException
	 * @throws JSONException
	 */
	public ModelChannel(JSONObject data, String type)
			throws DataInvalidException, JSONException {
		super(data);
		if (data.has("channel_category_id"))
			setId(data.getInt("channel_category_id"));
		if (data.has("title"))
			setcName(data.getString("title"));
		if (data.has("image"))
			setUserChannelImageUrl(data.getString("image"));
		if (data.has("count")) {
			setCount(data.getInt("count") + "");
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public int getIs_follow() {
		return is_follow;
	}

	public void setIs_follow(int is_follow) {
		this.is_follow = is_follow;
	}

	public JSONArray getImage() {
		return image;
	}

	public void setImage(JSONArray image) {
		this.image = image;
	}

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}

	@Override
	public int compareTo(SociaxItem another) {
		return this.sortId.compareTo(((ModelChannel) another).getSortId());
	}
}
