package com.thinksns.sociax.t4.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;

import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.weiba.ActivityWeibaDetail;
import com.thinksns.sociax.t4.model.ModelFeedBack;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/** 
 * 类说明：   
 * @author  wz    
 * @date    2015-1-26
 * @version 1.0
 */
public class ListFeedBack extends ListSociax {
	private static final String TAG = "WeibaList";

	public ListFeedBack(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListFeedBack(Context context) {
		super(context);
	}

	@Override
	protected void onClick(View view, int position, long id) {
		if (view.getId() == R.id.footer_content) {
			ImageView iv = (ImageView) view.findViewById(R.id.anim_view);
			iv.setVisibility(View.VISIBLE);
			Anim.refresh(getContext(), iv);
			HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) this
					.getAdapter();
			AdapterSociaxList adapter = (AdapterSociaxList) headerAdapter
					.getWrappedAdapter();
			adapter.animView = iv;
			adapter.doRefreshFooter();
		} else if(view.getId()==R.id.tv_part_name){
			//频闭part分隔线的点击事件
		}else{
			ModelFeedBack md = (ModelFeedBack) view.getTag(R.id.tag_object);
			if(md==null){
				return;
			}
			Intent intent = new Intent(getContext(), ActivityWeibaDetail.class);
			intent.putExtra("type", md);
			((Activity)getContext()).setResult(Activity.RESULT_OK, intent);
			((Activity)getContext()).finish();
		}
	}
}
