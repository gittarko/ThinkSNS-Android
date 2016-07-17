package com.thinksns.sociax.t4.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/** 
 * 类说明： 充值  
 * @author  Zoey    
 * @date    2015年10月15日
 * @version 1.0
 */
public class ModelCharge extends SociaxItem {

	private String serial_number;
	private int charge_type;
	private double charge_value;
	private int uid;
	private int ctime;
	private int status;
	private double charge_sroce;
	private String charge_order;
	private int charge_id;
	
	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public int getCharge_type() {
		return charge_type;
	}

	public void setCharge_type(int charge_type) {
		this.charge_type = charge_type;
	}

	public double getCharge_value() {
		return charge_value;
	}

	public void setCharge_value(double charge_value) {
		this.charge_value = charge_value;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getCtime() {
		return ctime;
	}

	public void setCtime(int ctime) {
		this.ctime = ctime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getCharge_sroce() {
		return charge_sroce;
	}

	public void setCharge_sroce(double charge_sroce) {
		this.charge_sroce = charge_sroce;
	}

	public String getCharge_order() {
		return charge_order;
	}

	public void setCharge_order(String charge_order) {
		this.charge_order = charge_order;
	}

	public int getCharge_id() {
		return charge_id;
	}

	public void setCharge_id(int charge_id) {
		this.charge_id = charge_id;
	}

	public ModelCharge() {
		super();
	}

	public ModelCharge(JSONObject data) throws DataInvalidException {
		super(data);
		
			try {
				if(data.has("serial_number")) this.setSerial_number(data.getString("serial_number"));
				if(data.has("charge_type")) this.setCharge_type(data.getInt("charge_type"));
				if(data.has("charge_value")) this.setCharge_value(data.getDouble("charge_value"));
				if(data.has("uid")) this.setUid(data.getInt("uid"));
				if(data.has("ctime")) this.setCtime(data.getInt("ctime"));
				if(data.has("status")) this.setStatus(data.getInt("status"));
				if(data.has("charge_sroce")) this.setCharge_sroce(data.getDouble("charge_sroce"));
				if(data.has("charge_order")) this.setCharge_order(data.getString("charge_order"));
				if(data.has("charge_id")) this.setCharge_id(data.getInt("charge_id"));
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
