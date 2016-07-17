package com.thinksns.sociax.t4.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/** 
 * 类说明： 我的礼物
 *   
 * @author  Zoey    
 * @date    2015年9月23日
 * @version 1.0
 */
public class ModelMyGifts extends SociaxItem implements Serializable{

	private String id;//礼物ID
	private String name;//礼物名称
	private String brief;//礼物简介
	private String info;//礼物详情
	private String image;//图片地址
	private String score;//所需积分
	private String stock;//库存量
	private String max;//限购数量
	private String inUserName;//被赠送人的用户名称
	private String outUserName;//赠送人用户名称
	private String date;//格式化后的时间
	private String cate;//分类
	private String say;//祝福语
	
	private String inUid;
	private String outUid;
	private String gid;
	private String type;
	private String phone;
	private String addres;
	private String content;
	private String num;
	private String status;
	private String logId;
	
	public ModelMyGifts() {
		super();
	}

	public ModelMyGifts(JSONObject data) throws DataInvalidException {
		super(data);
			try {
				if (data.has("id")) this.setId(data.getString("id"));
				if (data.has("name")) this.setName(data.getString("name"));
				if (data.has("brief")) this.setBrief(data.getString("brief"));
				if (data.has("info")) this.setInfo(data.getString("info"));
				if (data.has("image")) this.setImage(data.getString("image_src"));
				if (data.has("score")) this.setScore(data.getString("score"));
				if (data.has("stock")) this.setStock(data.getString("stock"));
				if (data.has("max")) this.setMax(data.getString("max"));
				if (data.has("inUserName")) this.setInUserName(data.getString("inUserName"));
				if (data.has("outUserName")) this.setOutUserName(data.getString("outUserName"));
				if (data.has("date")) this.setDate(data.getString("date"));
				if (data.has("cate")) this.setCate(data.getString("cate"));
				if (data.has("say")) this.setSay(data.getString("say"));
				
				if (data.has("inUid")) this.setInUid(data.getString("inUid"));
				if (data.has("outUid")) this.setOutUid(data.getString("outUid"));
				if (data.has("gid")) this.setGid(data.getString("gid"));
				if (data.has("type")) this.setType(data.getString("type"));
				if (data.has("phone")) this.setPhone(data.getString("phone"));
				if (data.has("addres")) this.setAddres(data.getString("addres"));
				if (data.has("content")) this.setContent(data.getString("content"));
				if (data.has("num")) this.setNum(data.getString("num"));
				if (data.has("status")) this.setStatus(data.getString("status"));
				if (data.has("logId")) this.setLogId(data.optString("logId"));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	
	public String getInUid() {
		return inUid;
	}

	public void setInUid(String inUid) {
		this.inUid = inUid;
	}

	public String getOutUid() {
		return outUid;
	}

	public void setOutUid(String outUid) {
		this.outUid = outUid;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddres() {
		return addres;
	}

	public void setAddres(String addres) {
		this.addres = addres;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInUserName() {
		return inUserName;
	}

	public void setInUserName(String inUserName) {
		this.inUserName = inUserName;
	}

	public String getOutUserName() {
		return outUserName;
	}

	public void setOutUserName(String outUserName) {
		this.outUserName = outUserName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSay() {
		return say;
	}

	public void setSay(String say) {
		this.say = say;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
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

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}
}
