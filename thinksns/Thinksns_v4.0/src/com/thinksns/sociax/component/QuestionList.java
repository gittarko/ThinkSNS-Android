package com.thinksns.sociax.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 类说明： 显示问题列表的ListView
 * 
 * @author Povol
 * @date 2012-7-29
 * @version 1.0
 */
public class QuestionList extends SociaxList {

	public QuestionList(Context context) {
		super(context);
	}

	public QuestionList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onClick(View view, int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addHeaderView() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void addFooterView() {
		// TODO Auto-generated method stub
	}
}
