package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/** 
 * 类说明：   推荐帖子
 * @author  wz    
 * @date    2015-1-3
 * @version 1.0
 */
public class AdapterPostRecommendList extends AdapterPostList{
	
	public AdapterPostRecommendList(FragmentSociax fragment,
			ListData<SociaxItem> list) {
		super(fragment, list, null);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		HolderSociax holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_post, null);
			holder = append.initHolder(convertView);
            convertView.setTag(R.id.tag_viewholder, holder);
        } else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ListView)parent).performItemClick(v, position, position);
			}
		});

		//点击帖子内容进入帖子详情
		holder.tv_post_info.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ListView)parent).performItemClick(v, position, position);
			}
		});

		append.appendPostListData(holder, getItem(position));

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
			return getApi().getRecommendTopic(PAGE_COUNT,getMaxid(), httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException{
		try {
			return getApi().getRecommendTopic(PAGE_COUNT,0, httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
		} catch (DataInvalidException e) {
			e.printStackTrace();
		}
		return null;
	}
}

