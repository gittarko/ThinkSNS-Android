package com.thinksns.sociax.t4.model;

import org.json.JSONException;
import org.json.JSONObject;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/** 
 * 类说明：   
 * 
 * @author  Zoey    
 * @date    2015年9月25日
 * @version 1.0
 */
public class ModelMyScoreDetail extends SociaxItem {

	private String rid;
	private String uid;
	private String action;
	private String ctime;
	private String score;
	
	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
	
	public ModelMyScoreDetail() {
		super();
	}

	public ModelMyScoreDetail(JSONObject data) throws DataInvalidException {
		super(data);
			try {
				if (data.has("rid")) this.setRid(data.getString("rid"));
				if (data.has("uid")) this.setUid(data.getString("uid"));
				if (data.has("action")) this.setAction(data.getString("action"));
				if (data.has("ctime")) this.setCtime(data.getString("ctime"));
				if (data.has("score")) this.setScore(data.getString("score"));
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
}
