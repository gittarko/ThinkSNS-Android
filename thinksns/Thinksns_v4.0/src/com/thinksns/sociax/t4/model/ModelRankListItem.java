package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明： 风云榜
 * 
 * @author wz
 * @date 2015-1-13
 * @version 1.0
 */
public class ModelRankListItem extends SociaxItem {
	int uid;
	String uname, uface,
	/**
	 * 积分
	 */
	experience = "0", feed_count = "0";
	String rank = "0";
	String rankMy = "0";
	/**
	 * 勋章数目
	 */
	String mcount;

	public String getMcount() {
		return mcount;
	}

	public void setMcount(String mcount) {
		this.mcount = mcount;
	}

	public String getRankMy() {
		return rankMy;
	}

	public void setRankMy(String rankMy) {
		this.rankMy = rankMy;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public ModelRankListItem(JSONObject data) {
		try {
			if (data.has("score")) {
				try {
					this.setExperience(data.getString("score"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (data.has("rank")) {
				try {
					this.setRank(data.getString("rank"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (data.has("uid")) {
				try {
					this.setUid(data.getInt("uid"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (data.has("uname")) {
				try {
					this.setUname(data.getString("uname"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (data.has("avatar")) {
				try {
					this.setUface(data.getString("avatar"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (data.has("weibo_count")) {
				try {
					this.setFeed_count(data.getString("weibo_count"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (data.has("mcount")) {
				try {
					this.setMcount(data.getString("mcount"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUface() {
		return uface;
	}

	public void setUface(String uface) {
		this.uface = uface;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getFeed_count() {
		return feed_count;
	}

	public void setFeed_count(String feed_count) {
		this.feed_count = feed_count;
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return null;
	}

}
