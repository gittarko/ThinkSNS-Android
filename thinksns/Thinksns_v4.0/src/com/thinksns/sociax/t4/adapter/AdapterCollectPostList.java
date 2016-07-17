package com.thinksns.sociax.t4.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/** 
 * 类说明：   推荐帖子
 * @author  wz    
 * @date    2015-1-3
 * @version 1.0
 */
public class AdapterCollectPostList extends AdapterPostList{

	public AdapterCollectPostList(FragmentSociax fragment,
								  ListData<SociaxItem> list) {
		super(fragment, list, null);
	}

	@Override
	public int getItemViewType(int position) {
		if(list == null || list.size() == 0) {
			if (adapterState == AdapterSociaxList.NO_MORE_DATA) {
				return 0;
			}else if(adapterState == AdapterSociaxList.STATE_LOADING) {
				return 2;
			}
		}
		return 1;

	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getCount() {
		if(list.size() == 0) {

			if(adapterState == AdapterSociaxList.NO_MORE_DATA) {
				//正在加载或加载结束
				return 1;
			}else if(adapterState == AdapterSociaxList.STATE_LOADING) {
				return 1;
			}
			return 0;
		}else {
			return list.size();
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		HolderSociax holder = null;
		if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
			if (type == 1) {
				convertView = inflater.inflate(R.layout.listitem_post, null);
				holder = append.initHolder(convertView);
			} else if (type == 0) {
				convertView = inflater.inflate(R.layout.default_collect_bg, null);
				holder = new HolderSociax();
			} else if (type == 2) {
				//加载正在加载数据的界面
				convertView = inflater.inflate(R.layout.loading, null);
				PullToRefreshListView listView = getPullRefreshView();
				int width = listView.getWidth();
				int height = listView.getHeight() - 100;
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
				convertView.setLayoutParams(params);
			}
			convertView.setTag(R.id.tag_viewholder, holder);
        } else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		if (type == 1) {
			append.appendPostListData(holder, getItem(position));
			// 这个tag将在ListView.onClick中用到
			convertView.setTag(R.id.tag_post, getItem(position));
		}
		return convertView;
	}

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return refreshNew(PAGE_COUNT);
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApi().collectPost(httpListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApi().collectPost(httpListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

