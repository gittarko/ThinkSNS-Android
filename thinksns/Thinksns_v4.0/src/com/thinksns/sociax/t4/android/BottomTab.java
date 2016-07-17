package com.thinksns.sociax.t4.android;

import com.thinksns.sociax.t4.android.fragment.FragmentAllWeibos;
import com.thinksns.sociax.t4.android.fragment.FragmentFind;
import com.thinksns.sociax.t4.android.fragment.FragmentMy;
import com.thinksns.sociax.android.R;


/** 
 * 类说明：  底部的导航栏tab选项卡
 * @author  dong.he    
 * @date    2015-8-31
 * @version 1.0
 */
public enum BottomTab {
	HOME(0, R.drawable.tab_selector_home, FragmentAllWeibos.class),
	EXPLORE(1, R.drawable.tab_selector_find, FragmentFind.class),
	NEW(2, R.drawable.tab_selector_home, FragmentAllWeibos.class),
	//注释
//	CHAT(3, R.drawable.tab_selector_message, FragmentChatList.class),
	ME(4, R.drawable.tab_selector_my, FragmentMy.class);
	
	private int index;
	private int resIcon;
	private Class<?> clz;
	
	BottomTab(int index, int resIcon, Class<?> clz) {
		this.index = index;
		this.resIcon = resIcon;
		this.clz = clz;
	}
}
