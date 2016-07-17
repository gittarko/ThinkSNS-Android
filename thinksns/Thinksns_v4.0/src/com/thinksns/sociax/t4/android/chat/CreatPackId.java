package com.thinksns.sociax.t4.android.chat;

import java.util.ArrayList;
import java.util.Collections;

/** 
 * 
 * 类说明： 生成packid
 * 
 *  A-Z之间随机选取4个字母
 *  0-10之间随机选取4个字母
 *  字母和数字连在一起打乱输出
 *   
 * @author  Zoey    
 * @date    2015年9月22日
 * @version 1.0
 */
public class CreatPackId {
	
	private static String abc="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String string="";
	
	public static String createPackId(){
		
		ArrayList<String> list=new ArrayList<String>();
		for (int i = 0; i < 8; i++) {
			//随机生成1-26的随机数
			int random=(int)(Math.random()*abc.length());
			//随机生成0-10的随机数
			int random2=(int)(1+Math.random()*(10-1+1));
			list.add(abc.charAt(random)+"");
			list.add(random2+"");
		}
		
		Collections.shuffle(list);
		for (String str: list) {
			string += str;
		}
		
		return string;
	}
}
