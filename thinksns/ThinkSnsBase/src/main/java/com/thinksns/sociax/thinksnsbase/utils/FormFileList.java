package com.thinksns.sociax.thinksnsbase.utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 类说明：
 * 
 * @author PC
 * @date 2014-9-18
 * @version 1.0
 */
public class FormFileList implements Serializable {
	private ArrayList<FormFile> list;

	public ArrayList<FormFile> getList() {
		return list;
	}

	public void setList(ArrayList<FormFile> list) {
		this.list = list;
	}

}
