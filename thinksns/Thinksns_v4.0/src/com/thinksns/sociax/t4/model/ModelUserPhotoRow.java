package com.thinksns.sociax.t4.model;


import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 用户图片行
 * 
 * @author wz
 * @date 2014-11-24
 * @version 1.0
 */
public class ModelUserPhotoRow extends SociaxItem {
	int size = 1;
	ListData<ModelUserPhoto> childs;

	public ListData<ModelUserPhoto> getChilds() {
		return childs;
	}

	public void setChilds(ListData<ModelUserPhoto> childs) {
		this.childs = childs;
	}

	/**
	 * 每行礼物
	 * 
	 * @param childsize
	 *            每行个数
	 */
	public ModelUserPhotoRow(int childsize) {
		this.size = childsize;
		childs = new ListData<ModelUserPhoto>();
	}

	public ModelUserPhoto getChildAt(int position) {
		if (position > size)
			return null;
		else
			return (ModelUserPhoto) childs.get(position);
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
