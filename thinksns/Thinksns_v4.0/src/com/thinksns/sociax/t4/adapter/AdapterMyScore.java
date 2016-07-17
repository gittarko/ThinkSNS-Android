package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thinksns.sociax.api.Api.Credit;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelMyScoreDetail;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;

/** 
 * 类说明：   
 * 
 * @author  Zoey    
 * @date    2015年9月25日
 * @version 1.0
 */
public class AdapterMyScore extends AdapterSociaxList {

	int mLimit;
	Context mContext;
	
	@Override
	public ModelMyScoreDetail getItem(int position) {
		return (ModelMyScoreDetail) this.list.get(position);
	}
	
	public AdapterMyScore(ThinksnsAbscractActivity context,
						  ListData<SociaxItem> list, int limit) {
		super(context, list);
		this.mLimit=limit;
		this.mContext=context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HolderSociax holder;
		ModelMyScoreDetail modelDetail=getItem(position);
		if (modelDetail!=null) {
			if (convertView == null) {
				holder = new HolderSociax();
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_my_score_detail, null);
	
				holder.tv_my_score_detail_name = (TextView) convertView.findViewById(R.id.tv_my_score_detail_name);
				holder.tv_my_score_detail_time = (TextView) convertView.findViewById(R.id.tv_my_score_detail_time);
				holder.tv_my_score_detail_result = (TextView) convertView.findViewById(R.id.tv_my_score_detail_result);
	
				convertView.setTag(R.id.tag_viewholder, holder);
			} else {
				holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
			}
			
			convertView.setTag(R.id.my_credit_detail, getItem(position));
			
			String action=getItem(position).getAction();
			if (action!=null&&!action.equals("null")&&!action.equals("")) {
				holder.tv_my_score_detail_name.setText(action);
			}else {
				holder.tv_my_score_detail_name.setText("系统增加");
			}
			
			holder.tv_my_score_detail_time.setText(TimeHelper.friendlyTime(getItem(position).getCtime()));
			
			String score=getItem(position).getScore();
			if (score!=null&&!score.equals("null")&&!score.equals("")) {
				String fuction=score.substring(0,1);
				if (fuction.equals("+")) {
					holder.tv_my_score_detail_result.setTextColor(mContext.getResources().getColor(R.color.bg_my_score_source_score));
				}else if (fuction.equals("-")) {
					holder.tv_my_score_detail_result.setTextColor(mContext.getResources().getColor(R.color.bg_gift_score));
				}
				holder.tv_my_score_detail_result.setText(getItem(position).getScore());
			}else {
				holder.tv_my_score_detail_result.setText("+1");
			}
		}
		return convertView;
	}
	
	@Override
	public int getMaxid() {
		int maxid=Integer.parseInt((getLast()==null?"0":getLast().getRid()));
		return maxid;
	}
	
	@Override
	public ModelMyScoreDetail getLast() {
		return (ModelMyScoreDetail) super.getLast();
	}
	
	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return super.refreshHeader(obj);
	}
	
	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws ApiException, ListAreEmptyException,
			DataInvalidException{
		ListData<SociaxItem> listData=(ListData<SociaxItem>) getApiCredit().getScoreDetail(0, mLimit, httpListener);
		return listData;
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem> listData=(ListData<SociaxItem>) getApiCredit().getScoreDetail(getMaxid(), mLimit, httpListener);
		return listData;
	}
	
	Credit getApiCredit() {
		return thread.getApp().getApiCredit();
	}
	
	@Override
	public void addFooter(ListData<SociaxItem> list) {
		super.addFooter(list);
		if (list == null || list.size() == 0||list.size()<PAGE_COUNT) {
			getListView().hideFooterView();
			setShowFooter(false);
		} else {
			getListView().showFooterView();
			setShowFooter(true);
		}
	}
}
