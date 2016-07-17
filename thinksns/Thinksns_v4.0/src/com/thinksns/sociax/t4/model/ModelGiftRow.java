package com.thinksns.sociax.t4.model;


import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：一行礼物
 * 
 * @author wz
 * @date 2014-11-13
 * @version 1.0
 */
public class ModelGiftRow extends SociaxItem {
	int size = 1;
	ListData<ModelGift> childs;

	public ListData<ModelGift> getChilds() {
		return childs;
	}

	public void setChilds(ListData<ModelGift> childs) {
		this.childs = childs;
	}

	/**
	 * 每行礼物
	 * 
	 * @param childsize
	 *            每行个数
	 */
	public ModelGiftRow(int childsize) {
		this.size = childsize;
		childs = new ListData<ModelGift>();
	}

	public ModelGift getChildAt(int position) {
		if (position > size)
			return null;
		else
			return (ModelGift) childs.get(position);
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
