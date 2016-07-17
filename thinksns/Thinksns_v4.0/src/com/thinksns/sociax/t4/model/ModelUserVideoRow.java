package com.thinksns.sociax.t4.model;


import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 视频列表行
 * 
 * @author wz
 * @date 2014-11-24
 * @version 1.0
 */
public class ModelUserVideoRow extends SociaxItem {
	int size = 1;
	ListData<ModelVideo> childs;

	public ListData<ModelVideo> getChilds() {
		return childs;
	}

	public void setChilds(ListData<ModelVideo> childs) {
		this.childs = childs;
	}

	/**
	 * 每行礼物
	 * 
	 * @param childsize
	 *            每行个数
	 */
	public ModelUserVideoRow(int childsize) {
		this.size = childsize;
		childs = new ListData<ModelVideo>();
	}

	public ModelVideo getChildAt(int position) {
		if (position > size)
			return null;
		else
			return (ModelVideo) childs.get(position);
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

}
