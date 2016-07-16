package com.thinksns.sociax.thinksnsbase.bean;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONObject;

import java.io.Serializable;

public abstract class SociaxItem implements Serializable,
		Comparable<SociaxItem> {
	/**
	 * 所有modle的基类
	 */
	private static final long serialVersionUID = 1L;
	protected static final String NULL = "";

	public SociaxItem(JSONObject jsonData) throws DataInvalidException {
		if (jsonData == null)
			throw new DataInvalidException();
	}

	public SociaxItem() {

	}

	/**
	 * 检测对象是否合法
	 * 
	 * @return
	 */
	public abstract boolean checkValid();

	public abstract String getUserface();

	protected boolean checkNull(String data) {
		return data == null || data.equals(NULL);
	}

	protected boolean checkNull(int data) {
		return data == 0;
	}

	@Override
	public int compareTo(SociaxItem another) {
		return 0;
	}
}
