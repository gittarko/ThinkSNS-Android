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
import com.thinksns.sociax.t4.model.ModelScoreRule;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/** 
 * 类说明：   积分规则
 * 
 * @author  Zoey    
 * @date    2015年9月25日
 * @version 1.0
 */
public class AdapterScoreRule extends AdapterSociaxList {

	Context mContext;
	
	@Override
	public ModelScoreRule getItem(int position) {
		return (ModelScoreRule) this.list.get(position);
	}
	
	public AdapterScoreRule(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list) {
		super(context, list);
		this.mContext=context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HolderSociax holder;
		ModelScoreRule modelDetail=getItem(position);
		if (modelDetail!=null) {
			if (convertView == null) {
				holder = new HolderSociax();
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_score_rule, null);
	
				holder.tv_score_rule_name = (TextView) convertView.findViewById(R.id.tv_score_rule_name);
				holder.tv_score_rule_exp = (TextView) convertView.findViewById(R.id.tv_score_rule_exp);
				holder.tv_score_rule_score = (TextView) convertView.findViewById(R.id.tv_score_rule_score);
	
				convertView.setTag(R.id.tag_viewholder, holder);
			} else {
				holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
			}
			
			convertView.setTag(R.id.my_score_rule, getItem(position));
			
			holder.tv_score_rule_name.setText(modelDetail.getAlias());
			holder.tv_score_rule_exp.setText(modelDetail.getExperience_alias()+modelDetail.getExperience());
			holder.tv_score_rule_score.setText(modelDetail.getScore_alias()+modelDetail.getScore());
		}
		return convertView;
	}
	
	@Override
	public int getMaxid() {
		return 0;
	}
	
	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws ApiException, ListAreEmptyException,
			DataInvalidException{
		ListData<SociaxItem> listData=(ListData<SociaxItem>) getApiCredit().getScoreRule(httpListener);
		return listData;
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem> listData=(ListData<SociaxItem>) getApiCredit().getScoreRule(httpListener);
		return listData;
	}
	
	Credit getApiCredit() {
		return thread.getApp().getApiCredit();
	}
	
	@Override
	public void addFooter(ListData<SociaxItem> list) {
		super.addFooter(list);
		getListView().hideFooterView();
		setShowFooter(false);
	}
}
