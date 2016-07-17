package com.thinksns.sociax.t4.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelCopyTask;
import com.thinksns.sociax.t4.model.ModelCopyTaskCons;
import com.thinksns.sociax.android.R;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年9月10日
 * @version 1.0
 */
public class AdapterCopyTask extends BaseAdapter {

	Context context;
	ArrayList<ModelCopyTask> coList=new ArrayList<ModelCopyTask>();
	
	public AdapterCopyTask(Context context, ArrayList<ModelCopyTask> coList) {
		super();
		this.context = context;
		this.coList = coList;
	}

	@Override
	public int getCount() {
		return coList.size();
	}

	@Override
	public ModelCopyTask getItem(int position) {
		return coList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		HolderSociax holder;
		if (convertView == null) {
			holder = new HolderSociax();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_copy_task, null);

			holder.tv_copy_task_name=(TextView)convertView.findViewById(R.id.tv_copy_task_name);
			holder.tv_copy_task_complete_state=(TextView)convertView.findViewById(R.id.tv_copy_task_complete_state);
			holder.tv_copy_task_detail_content=(TextView)convertView.findViewById(R.id.tv_copy_task_detail_content);
			holder.tv_copy_task_detail_goal=(TextView)convertView.findViewById(R.id.tv_copy_task_detail_goal);
			holder.tv_copy_task_last_count=(TextView)convertView.findViewById(R.id.tv_copy_task_last_count);
			holder.iv_copy_task_complete=(ImageView)convertView.findViewById(R.id.iv_copy_task_complete);
			holder.ll_copy_task_condition=(LinearLayout)convertView.findViewById(R.id.ll_copy_task_condition);

			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		ModelCopyTask modelCopyTask = (ModelCopyTask) getItem(position);
		boolean isComplete=modelCopyTask.isIscomplete();
		if (isComplete) {
			holder.iv_copy_task_complete.setVisibility(View.VISIBLE);
			holder.tv_copy_task_complete_state.setText("已完成");
		}else {
			holder.iv_copy_task_complete.setVisibility(View.GONE);
			holder.tv_copy_task_complete_state.setText("(未完成)");
			holder.tv_copy_task_complete_state.setTextColor(context.getResources().getColor(R.color.bg_task_not_complete));
		}
		
		holder.tv_copy_task_name.setText(modelCopyTask.getName());
		holder.tv_copy_task_detail_content.setText(modelCopyTask.getDesc());
		
		holder.tv_copy_task_detail_goal.setText("奖励：经验值+"
				+ modelCopyTask.getExp() + "点  积分值+"
				+ modelCopyTask.getScore() + "点");
		// 修改分值的颜色
		SpannableString styledText = new SpannableString(holder.tv_copy_task_detail_goal.getText());
		styledText.setSpan(new TextAppearanceSpan(context, R.style.taskExpBlue), 6, 8,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(context, R.style.taskExpBlue), 14, 16,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.tv_copy_task_detail_goal.setText(styledText,TextView.BufferType.SPANNABLE);
		
		String surplus = modelCopyTask.getSurplus();
		if (TextUtils.isEmpty(surplus)) {
			holder.tv_copy_task_last_count.setText("无限领取");
		}else {
			holder.tv_copy_task_last_count.setText(surplus);
		}
		
		ArrayList<ModelCopyTaskCons> cons=modelCopyTask.getConsList();
		if (cons!=null) {
			for (int i = 0; i < cons.size(); i++) {
				ModelCopyTaskCons modelCondition=cons.get(i);
				TextView tv_condition=new TextView(context);
				tv_condition.setText(modelCondition.getDesc()+" ");
				tv_condition.setTextColor(context.getResources().getColor(R.color.bg_task_detail));
				tv_condition.setSingleLine();
				tv_condition.setEllipsize(TruncateAt.END);
				tv_condition.setTextSize(12);
				
				boolean status=modelCondition.isStatus();
				if (status) {
					Drawable drawable = context.getResources().getDrawable(R.drawable.ic_task_condition_ok);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
					tv_condition.setCompoundDrawables(null,null,drawable,null);
				}
				
				holder.ll_copy_task_condition.addView(tv_condition);
			}
		}
		
		return convertView;
	}
}
