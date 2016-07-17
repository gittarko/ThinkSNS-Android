package com.thinksns.sociax.t4.android.data;

import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * 类说明： 所有映射列表的基类
 * 
 * @author wz
 * @date 2014-10-16
 * @version 1.0
 */
public class AppendSociax {
	protected AdapterSociaxList adapter;	//列表adapter
	protected ListBaseAdapter adapterList;
	protected Context context;//context
	protected UnitSociax uint;//工具类
	protected LayoutInflater inflater;
	public AppendSociax(Context context){
		this.context=context;
		inflater=LayoutInflater.from(context);
		this.uint=new UnitSociax(context);
	}
	/**
	 * 错误日志
	 * @param info
	 */
	protected void finishAppendByErr(String info){
		Log.e("append soicax err","stop append by : "+info);
		Log.e("append soicax err","stop append by : "+info);
		Log.e("append soicax err","stop append by : "+info);
	}
}