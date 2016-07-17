package com.thinksns.sociax.t4.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.thinksns.sociax.android.R;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年9月23日
 * @version 1.0
 */
public class AdapterTopUp extends BaseAdapter {
	
	private Context mContext;
	private static int temp = -1;
	
	private int res[]={R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.bg_chat_card_get,R.drawable.bg_chat_msg_my};
	private String detail[]={"支付宝支付","财付通支付","微信支付","银行卡支付"};
	 
	public AdapterTopUp(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return res.length;
	}

	@Override
	public Object getItem(int position) {
		return res[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView=LayoutInflater.from(mContext).inflate(R.layout.list_item_top_up, null);
		
		CheckBox	cb_top_up=(CheckBox)convertView.findViewById(R.id.cb_top_up);
		ImageView iv_way_of_top_up=(ImageView)convertView.findViewById(R.id.iv_way_of_top_up);
		TextView tv_way_of_top_up=(TextView)convertView.findViewById(R.id.tv_way_of_top_up);
		
		iv_way_of_top_up.setImageResource(res[position]);
		tv_way_of_top_up.setText(detail[position]);
		
		final CheckBox tempButton = (CheckBox) convertView.findViewById(temp);
		
		cb_top_up.setId(position);
		cb_top_up.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				 if (isChecked){
					 // 实现CheckBox的单选功能
				      if (temp != -1){
				         if (tempButton != null){
				             	tempButton.setChecked(false);
				            }
				         }
				      //得到当前的position
				      temp = buttonView.getId();
			     } else {
			    	 temp = -1;
			     }
			}
		});
		
		
		return convertView;
	}
}
