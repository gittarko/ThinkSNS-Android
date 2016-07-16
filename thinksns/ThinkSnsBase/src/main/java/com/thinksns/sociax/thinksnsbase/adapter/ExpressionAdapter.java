/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thinksns.sociax.thinksnsbase.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.thinksns.sociax.thinksnsbase.R;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

import java.util.List;

public class ExpressionAdapter extends ArrayAdapter<String>{

	public ExpressionAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = View.inflate(getContext(), R.layout.row_expression, null);
		}
		
		ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
		
		String filename = getItem(position);
		int resId = getContext().getResources().getIdentifier(filename, "drawable",
				getContext().getPackageName());
		imageView.setImageResource(resId);
		//设置表情大小
		int width = UnitSociax.getWindowWidth(parent.getContext()) / 10;
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, width);
		return convertView;
	}
	
}
