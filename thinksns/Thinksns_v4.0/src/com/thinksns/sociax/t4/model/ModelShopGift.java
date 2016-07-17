package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * 类说明： 积分商城
 *   
 * @author  Zoey    
 * @date    2015年9月23日
 * @version 1.0
 */
public class ModelShopGift extends SociaxItem implements Serializable{

	private String id;//礼物ID
	private String name;//礼物名称
	private String brief;//礼物简介
	private String info;//礼物详情
	private String image;//图片地址
	private String score;//所需积分
	private String stock;//库存量
	private String max;//限购数量
	private String time;//时间戳
	private String cate;//分类
	private String count;
	
	public ModelShopGift() {
		super();
	}

	public ModelShopGift(JSONObject data) throws DataInvalidException {
		super(data);
			try {
				if (data.has("id")) this.setId(data.getString("id"));
				if (data.has("name")) this.setName(data.getString("name"));
				if (data.has("brief")) this.setBrief(data.getString("brief"));
				if (data.has("image")) this.setImage(data.getString("image"));
				if (data.has("score")) this.setScore(data.getString("score"));
				if (data.has("stock")) this.setStock(data.getString("stock"));
				if (data.has("max")) this.setMax(data.getString("max"));
				if (data.has("time")) this.setTime(data.getString("time"));
				if (data.has("cate")) this.setCate(data.getString("cate"));
				if (data.has("info")) this.setInfo(data.getString("info"));
				if (data.has("count")) this.setCount(data.getString("count"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
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

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
}
