package com.thinksns.sociax.t4.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年9月10日
 * @version 1.0
 */
public class ModelCopyTask extends SociaxItem {
	
	String id;
	String name;
	String desc;
	int exp;
	int score;
	ArrayList<ModelCopyTaskCons> consList;
	boolean iscomplete;
	String surplus;
	
	public boolean isIscomplete() {
		return iscomplete;
	}

	public void setIscomplete(boolean iscomplete) {
		this.iscomplete = iscomplete;
	}

	public String getSurplus() {
		return surplus;
	}

	public void setSurplus(String surplus) {
		this.surplus = surplus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public ArrayList<ModelCopyTaskCons> getConsList() {
		return consList;
	}

	public void setConsList(ArrayList<ModelCopyTaskCons> consList) {
		this.consList = consList;
	}

	public ModelCopyTask() {
		super();
	}

	public ModelCopyTask(JSONObject data) throws DataInvalidException {
		super(data);
			try {
				if(data.has("name"))	this.setName(data.getString("name"));
				if(data.has("desc"))	this.setDesc(data.getString("desc"));
				if(data.has("exp"))	this.setExp(data.getInt("exp"));
				if(data.has("score"))	this.setScore(data.getInt("score"));
				if (data.has("cons")) {
					String consStr=data.getString("cons");
					JSONArray consArray=new JSONArray(consStr);
					consList=new ArrayList<ModelCopyTaskCons>();
					for (int i = 0; i < consArray.length(); i++) {
						ModelCopyTaskCons con = new ModelCopyTaskCons(consArray.getJSONObject(i));
						consList.add(con);
					}
					this.setConsList(consList);
				}
				if(data.has("iscomplete"))	this.setIscomplete(data.getBoolean("iscomplete"));
				if(data.has("surplus"))	this.setSurplus(data.getString("surplus"));
			} catch (JSONException e) {
				e.printStackTrace();
				Log.v("copyTask","-----解析-----Exception---------"+e.getMessage());
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
