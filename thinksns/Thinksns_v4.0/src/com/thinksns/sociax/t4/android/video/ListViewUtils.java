package com.thinksns.sociax.t4.android.video;


import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 类说明： 用于在scrollview嵌套listview时候自适应listview的宽度
 * 
 * @author wz
 * @date 2013-12-26
 * @version 1.0
 */
public class ListViewUtils {
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
					MeasureSpec.AT_MOST);
			listItem.measure(desiredWidth, 0); // 计算子项View 的宽高
			Log.v("params.totalHeight=", String.valueOf(totalHeight));
			totalHeight += (listItem.getMeasuredHeight()); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		Log.v("params.height2=", String.valueOf(params.height));
		listView.setLayoutParams(params);
	}
}