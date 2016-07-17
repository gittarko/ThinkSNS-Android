package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：
 * 
 * @author Povol
 * @date 2013-2-3
 * @version 1.0
 */
public class StringItem extends SociaxItem {

	private int id;
	private String name;
	private String url;

	private ListData<SociaxItem> listData;

	public StringItem() {
	}

	public StringItem(int id, String name) {
		setId(id);
		setName(name);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ListData<SociaxItem> getListData() {
		return listData;
	}

	public void setListData(ListData<SociaxItem> listData) {
		this.listData = listData;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	@Override
	public int compareTo(SociaxItem another) {
		return ((Integer) id).compareTo(((StringItem) another).getId());
	}

	@Override
	public String toString() {
		return "StringItem [id=" + id + ", name=" + name + ", listData="
				+ listData + "]";
	}

}
