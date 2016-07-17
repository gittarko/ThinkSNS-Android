package com.thinksns.sociax.t4.model;


import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class ModelImageAttach extends SociaxItem {
	/**
	 * 附件id
	 */
	private int id;
	/**
	 * 附件所在微博id
	 */
	private int weiboId;
	/**
	 * 附件名称
	 */
	private String name;
	/**
	 * 附件小图片
	 */
	private String small;
	/**
	 * 附件中图片
	 */
	private String middle;
	/**
	 * 附件原始图片
	 */
	private String origin;
	
	private String attach_origin_height;
	private String attach_origin_width;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(int weiboId) {
		this.weiboId = weiboId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSmall() {
		return small;
	}

	public void setSmall(String small) {
		this.small = small;
	}
	public String getMiddle() {
		return middle;
	}

	public void setMiddle(String middle) {
		this.middle = middle;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}

	public String getAttach_origin_height() {
		return attach_origin_height;
	}

	public void setAttach_origin_height(String attach_origin_height) {
		this.attach_origin_height = attach_origin_height;
	}

	public String getAttach_origin_width() {
		return attach_origin_width;
	}

	public void setAttach_origin_width(String attach_origin_width) {
		this.attach_origin_width = attach_origin_width;
	}
}
