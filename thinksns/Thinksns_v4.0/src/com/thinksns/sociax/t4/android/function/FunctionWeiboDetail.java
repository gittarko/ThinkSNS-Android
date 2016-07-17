package com.thinksns.sociax.t4.android.function;

import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.android.R;

import android.app.Dialog;
import android.content.Context;

/** 
 * 类说明：   微博详情
 * @author  wz    
 * @date    2014-12-15
 * @version 1.0
 */
public class FunctionWeiboDetail extends FunctionSoicax{
	private ModelWeibo weibo;
	private Dialog view;
	/**
	 * 构造函数
	 * @param context
	 * @param weibo
	 */
	public FunctionWeiboDetail(Context context,ModelWeibo weibo) {
		super(context);
		// TODO Auto-generated constructor stub
		this.weibo=weibo;
//		view=new Dialog(context);
//		view.setContentView(R.layout.activity_weibo_detail);
//		view.show();
	}
	/**
	 * 修改操作
	 * @param content
	 */
	public void changeWeiboContent(String content){
		this.weibo.setContent(content);
	}
	
	
	@Override
	protected void initUiHandler() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initActivtyHandler() {
		// TODO Auto-generated method stub
		
	}
	
}
