package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskCategory extends SociaxItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tId = -1;
	private String name;
	private String count;
	private boolean isShare;
	private boolean setShare;

	private String cataType;

	private String emailList;

	public String getEmailList() {
		return emailList;
	}

	public void setEmailList(String emailList) {
		this.emailList = emailList;
	}

	public TaskCategory() {
	}

	public TaskCategory(JSONObject data) throws JSONException {
		this.settId(Integer.valueOf(data.getInt("category_id")));
		this.setName(data.getString("title"));
		this.setCount(data.getString("task_count"));
		this.setShare(data.getBoolean("isShare"));
		this.setSetShare(data.getBoolean("setShare"));
	}

	public TaskCategory(String taskCateName) {
		this.setName(taskCateName);
	}

	public int gettId() {
		return tId;
	}

	public void settId(int tId) {
		this.tId = tId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public boolean isShare() {
		return isShare;
	}

	public void setShare(boolean isShare) {
		this.isShare = isShare;
	}

	public boolean isSetShare() {
		return setShare;
	}

	public void setSetShare(boolean setShare) {
		this.setShare = setShare;
	}

	public String getCataType() {
		return cataType;
	}

	public void setCataType(String cataType) {
		this.cataType = cataType;
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
