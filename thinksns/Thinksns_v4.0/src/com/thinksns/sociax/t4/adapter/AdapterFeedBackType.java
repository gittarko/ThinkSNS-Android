package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thinksns.sociax.api.Api.Users;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelFeedBack;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/** 
 * 类说明：   意见反馈类型
 * @author  wz    
 * @date    2015-1-26
 * @version 1.0
 */
public class AdapterFeedBackType extends AdapterSociaxList{

	public AdapterFeedBackType(FragmentSociax fragment,
			ListData<SociaxItem> list) {
		super(fragment, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder;
		if(convertView==null){
			holder=new HolderSociax();
			convertView=inflater.inflate(R.layout.list_item_feedback, null);
			holder.tv_comment_content=(TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(R.id.tag_viewholder,holder);
		}else{
			holder=(HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}
		holder.tv_comment_content.setText(getItem(position).getType_name());
		convertView.setTag(R.id.tag_object,getItem(position));
		return convertView;
	}
	@Override
	public ModelFeedBack getItem(int position) {
		return (ModelFeedBack) super.getItem(position);
	}
	@Override
	public int getMaxid() {
		return getLast()==null?0:getLast().getType_id();
	}

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApi().getFeedbackType(PAGE_COUNT,0);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApi().getFeedbackType(PAGE_COUNT,getMaxid());
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public ModelFeedBack getLast() {
		return (ModelFeedBack) super.getLast();
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApi().getFeedbackType(PAGE_COUNT,0);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
		}

		return null;
	}
	Users getApi(){
		return thread.getApp().getUsers();
	}
}
