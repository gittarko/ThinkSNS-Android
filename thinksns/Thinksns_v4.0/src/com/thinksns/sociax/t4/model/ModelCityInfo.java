package com.thinksns.sociax.t4.model;

import java.io.Serializable;

import org.json.JSONObject;

import com.thinksns.sociax.t4.android.function.FunctionPingYing;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 城市的信息
 * 
 * @author ZhiShi
 * @date 2014-9-28
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ModelCityInfo extends SociaxItem implements Serializable {
	private String name;
	private String name_pinyin;
	private int id;
	private String sortLetters; // 显示数据拼音的首字母

	public ModelCityInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}

	public ModelCityInfo(String name, String name_pinyin, int id) {
		super();
		this.name = name;
		this.name_pinyin = name_pinyin;
		this.id = id;
	}

	public ModelCityInfo(JSONObject data) {
		try {
			if (data.has("city_id"))
				this.setId(data.getInt("city_id"));
			String city_name = data.getString("city_name");
			this.setName(city_name);
			this.setSortLetters(FunctionPingYing.sortFirstLetters(city_name.replaceAll("　", "")));
			this.setName_pinyin(FunctionPingYing.getPingYingString(city_name.replaceAll("　", "")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_pinyin() {
		return name_pinyin;
	}

	public void setName_pinyin(String name_pinyin) {
		this.name_pinyin = name_pinyin;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CityInfo [name=" + name + ", name_pinyin=" + name_pinyin
				+ ", id=" + id + ", sortLetters=" + sortLetters + "]";
	}

}
