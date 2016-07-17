package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明：
 * 
 * @author povol
 * @date Nov 16, 2012
 * @version 1.0
 */
public class Weiba extends SociaxItem {

	private int weibaId;
	private int creatId;
	private String weibaName;
	private String weibaIcon;
	private String intro;
	private int followCount;
	private int threadCount;
	private int followstate;
	private int postPermission;

	private byte[] iconData;

	public Weiba() {
	}

	public Weiba(JSONObject data) throws DataInvalidException {
		super(data);
		try {
			setWeibaId(data.getInt("weiba_id"));
			setCreatId(data.getInt("uid"));
			setWeibaName(data.getString("weiba_name"));
			setWeibaIcon(data.getString("logo_url"));
			setIntro(data.getString("intro"));
			setFollowCount(data.getInt("follower_count"));
			setThreadCount(data.getInt("thread_count"));
			setFollowstate(data.getInt("followstate"));
			setPostPermission(data.getInt("who_can_post"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}

	public int getWeibaId() {
		return weibaId;
	}

	public void setWeibaId(int weibaId) {
		this.weibaId = weibaId;
	}

	public int getCreatId() {
		return creatId;
	}

	public void setCreatId(int creatId) {
		this.creatId = creatId;
	}

	public String getWeibaName() {
		return weibaName;
	}

	public void setWeibaName(String weibaName) {
		this.weibaName = weibaName;
	}

	public String getWeibaIcon() {
		return weibaIcon;
	}

	public void setWeibaIcon(String weibaIcon) {
		this.weibaIcon = weibaIcon;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public int getFollowCount() {
		return followCount;
	}

	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public int getFollowstate() {
		return followstate;
	}

	public void setFollowstate(int followstate) {
		this.followstate = followstate;
	}

	public int getPostPermission() {
		return postPermission;
	}

	public void setPostPermission(int postPermission) {
		this.postPermission = postPermission;
	}

	public byte[] getIconData() {
		return iconData;
	}

	public void setIconData(byte[] iconData) {
		this.iconData = iconData;
	}

}
