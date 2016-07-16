package com.thinksns.sociax.thinksnsbase.utils;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class WordCount implements TextWatcher {
	public static final int MAX_COUNT = 140;
	private static final String TAG = "WordCount";

	private  TextView overWordCount;		//输入文字个数提示器
	private  EditText editText;

	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;
	private String tran;
	private Context context;
	/**输入是否符合限制***/
	private boolean isValid = true;

	public WordCount(EditText text, TextView v) {
		this.overWordCount = v;
		this.editText = text;
	}

	public WordCount(EditText text, TextView v, String tran) {
		this.overWordCount = v;
		this.editText = text;
		this.tran = tran;
		limit(tran);
	}

	public int getMaxCount() {
		return MAX_COUNT;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		temp = s;
	}

	@Override
	public void afterTextChanged(Editable s) {
		int number = MAX_COUNT - s.length();
		selectionStart = editText.getSelectionStart();
		selectionEnd = editText.getSelectionEnd();
		if (number <= 10) {
			String over = "<font color='red'>" + number + "</font>";
			this.overWordCount.setText(Html.fromHtml(over));
			if(number < 0) {
				isValid = false;
				return;
			}
		}else {
			this.overWordCount.setText("" + number);
		}

		isValid = true;

	}

	public void limit(String tran) {
		int number = MAX_COUNT - tran.length();

		if (number <= 10) {
			String over = "<font color='red'>" + number + "</font>";
			this.overWordCount.setText(Html.fromHtml(over));
		} else {
			this.overWordCount.setText("" + number);
		}

		editText.setText(tran);
		editText.setSelection(tran.length());		// 设置光标
	}

	/**
	 * 输入是否有效
	 * @return
     */
	public boolean inputValid() {
		return this.isValid;
	}
}
