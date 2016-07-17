package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;


public class ContactCategory extends Contact {

	private int cId;
	private String cName;
	private String cHead;
	private String cTpye;

	public ContactCategory() {
		// TODO Auto-generated constructor stub
	}

	public ContactCategory(JSONObject data) throws DataInvalidException,
			JSONException {
		// super(data);
		this.setcId(data.getInt("departId"));
		setUid(data.getInt("departId"));
		this.setcName(data.getString("departName"));
		setUname(data.getString("departName"));
		if (data.has("type")) {
			this.setcTpye(data.getString("type"));
			setType(data.getString("type"));
		}
	}

	public ContactCategory(int dId, String dName, String type) {
		this.setcId(dId);
		this.setcName(dName);
		setUname(dName);
		this.setType(type);
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public String getcHead() {
		return cHead;
	}

	public void setcHead(String cHead) {
		this.cHead = cHead;
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

	public String getcTpye() {
		return cTpye;
	}

	public void setcTpye(String cTpye) {
		this.cTpye = cTpye;
	}

}
