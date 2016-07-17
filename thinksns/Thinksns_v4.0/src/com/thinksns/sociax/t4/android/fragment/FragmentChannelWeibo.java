package com.thinksns.sociax.t4.android.fragment;

import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterChannelWeiboList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：某个频道的微博，需要传入intent int channel_id
 * @author wz
 * @date 2014-10-15
 * @version 1.0
 */
public class FragmentChannelWeibo extends FragmentWeibo {
	
	private int channel_id = 0;

	@Override
	public void initIntentData() {
		if (getActivity().getIntent().hasExtra("channel_id")) {
			channel_id = getActivity().getIntent()
					.getIntExtra("channel_id",0);
		}
		if (channel_id == 0) {
			Toast.makeText(getActivity(), "加载错误", Toast.LENGTH_SHORT).show();
			onDestroy();
		}
	}

	@Override
	public AdapterSociaxList createAdapter() {
        list = new ListData<SociaxItem>();
		//获取本地数据
		return new AdapterChannelWeiboList(this, list, channel_id, -1);
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.refreshNewSociaxList();
	}
	
	@Override
	public int getLayoutId() {
		return R.layout.fragment_home_all_weibo_list;
	}
}
