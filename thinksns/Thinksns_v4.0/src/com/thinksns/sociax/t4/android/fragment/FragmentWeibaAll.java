package com.thinksns.sociax.t4.android.fragment;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.adapter.AdapterWeibaAll;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/** 
 * 类说明：   全部微吧
 * @author  Administrator    
 * @date    2015-1-4
 * @version 1.0
 */
public class FragmentWeibaAll extends FragmentWeibaList {

	public static FragmentWeibaAll newInstance(Bundle args) {
		FragmentWeibaAll fragmentWeibaAll = new FragmentWeibaAll();
		fragmentWeibaAll.setArguments(args);
		return fragmentWeibaAll;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			downToRefresh = getArguments().getBoolean("down_to_refresh", true);
		}
	}

	@Override
	public void initView() {
		super.initView();
		listView.setDivider(new ColorDrawable(0xffdddddd));
		listView.setDividerHeight(1);
	}
	
	@Override
	public AdapterSociaxList createAdapter() {
		list = new ListData<SociaxItem>();
		return new AdapterWeibaAll(this, list);
	}

	@Override
	public void executeDataSuccess(ListData<SociaxItem> list) {
		if(list == null ||
				list.size() < AdapterSociaxList.PAGE_COUNT) {
			if(adapter.getLast() != null) {
				if(list.size() == 0)
					Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
				pullRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
			}
			if(!downToRefresh) {
				pullRefresh.setMode(PullToRefreshBase.Mode.DISABLED);
			}
		}else{

		}

		super.executeDataSuccess(list);

	}
}
