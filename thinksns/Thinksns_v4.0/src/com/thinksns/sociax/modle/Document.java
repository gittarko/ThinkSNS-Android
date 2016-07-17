package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Document extends SociaxItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dId;
	private String dName;
	private String dPath; // 首字母
	private String dType; // 部门
	private int dSize;
	private String dTime;

	public Document() {
	}

	public Document(JSONObject data) throws DataInvalidException, JSONException {
		super(data);
		if (data.has("doc_id"))
			this.dId = data.getInt("doc_id");
		if (data.has("doc_name"))
			this.dName = data.getString("doc_name");
		if (data.has("doc_path"))
			this.dPath = data.getString("doc_path");
		if (data.has("doc_type"))
			this.dType = data.getString("doc_type");
	}

	public int getdId() {
		return dId;
	}

	public void setdId(int dId) {
		this.dId = dId;
	}

	public String getdName() {
		return dName;
	}

	public void setdName(String dName) {
		this.dName = dName;
	}

	public String getdPath() {
		return dPath;
	}

	public void setdPath(String dPath) {
		this.dPath = dPath;
	}

	public String getdType() {
		return dType;
	}

	public void setdType(String dType) {
		this.dType = dType;
	}

	public int getdSize() {
		return dSize;
	}

	public void setdSize(int dSize) {
		this.dSize = dSize;
	}

	public String getdTime() {
		return dTime;
	}

	public void setdTime(String dTime) {
		this.dTime = dTime;
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
