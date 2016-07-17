package com.thinksns.sociax.t4.model;


import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：   一行勋章
 * @author  wz    
 * @date    2015-1-26
 * @version 1.0
 */
public class ModelMedalRow extends SociaxItem {
	int size = 1;
	ListData<ModelUserMedal> childs;

	public ListData<ModelUserMedal> getChilds() {
		return childs;
	}

	public void setChilds(ListData<ModelUserMedal> childs) {
		this.childs = childs;
	}

	/**
	 * 每行礼物
	 * 
	 * @param childsize
	 *            每行个数
	 */
	public ModelMedalRow(int childsize) {
		this.size = childsize;
		childs = new ListData<ModelUserMedal>();
	}

	public ModelUserMedal getChildAt(int position) {
		if (position > size)
			return null;
		else
			return (ModelUserMedal) childs.get(position);
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
