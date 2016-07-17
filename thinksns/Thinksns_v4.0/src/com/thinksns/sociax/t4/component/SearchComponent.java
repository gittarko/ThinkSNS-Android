package com.thinksns.sociax.t4.component;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.thinksns.sociax.component.EdtInterface;
import com.thinksns.sociax.android.R;

/** 
 * 类说明：   搜索组件
 * @author  wz    
 * @date    2015-1-23
 * @version 1.0
 */
public class SearchComponent extends LinearLayout implements EdtInterface {

	ImageButton ib;
	public EditText et;

	public SearchComponent(Context context) {
		super(context);

	}

	public SearchComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.input_and_button, this,
				true);
		init();
	}

	private void init() {
		ib = (ImageButton) findViewById(R.id.clear_edit);
		et = (EditText) findViewById(R.id.input_edit);
		et.addTextChangedListener(tw);// 为输入框绑定一个监听文字变化的监听器
		// 添加按钮点击事件
		ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideBtn();// 隐藏按钮
				et.setText("");// 设置输入框内容为空
			}
		});
	}
	@Override
	public void setOnKeyListener(OnKeyListener l) {
		// TODO Auto-generated method stub
		et.setOnKeyListener(l);
	}

	public String getText() {
		return et.getText().toString();
	}

	// 当输入框状态改变时，会调用相应的方法
	TextWatcher tw = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		// 在文字改变后调用
		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				hideBtn();// 隐藏按钮
			} else {
				showBtn();// 显示按钮
			}

		}
	};
	@Override
	public void hideBtn() {
		// 设置按钮不可见
		if (ib.isShown())
			ib.setVisibility(View.GONE);
	}
	@Override
	public void showBtn() {
		// 设置按钮可见
		if (!ib.isShown())
			ib.setVisibility(View.VISIBLE);
	}
}