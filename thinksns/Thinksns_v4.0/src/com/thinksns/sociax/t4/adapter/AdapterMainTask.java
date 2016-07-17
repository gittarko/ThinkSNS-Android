package com.thinksns.sociax.t4.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelDailyOrMainTask;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年9月10日
 * @version 1.0
 */
public class AdapterMainTask extends BaseAdapter {

	Context context;
	ArrayList<ModelDailyOrMainTask> dailyTasks = new ArrayList<ModelDailyOrMainTask>();
	private Thinksns application;

	public AdapterMainTask(Context context,ArrayList<ModelDailyOrMainTask> dailyTasks) {
		super();
		this.context = context;
		this.dailyTasks = dailyTasks;
		application = (Thinksns) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return dailyTasks.size();
	}

	@Override
	public ModelDailyOrMainTask getItem(int position) {
		return dailyTasks.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_main_task, null);

			holder.tv_main_task_name = (TextView) convertView
					.findViewById(R.id.tv_main_task_name);
			holder.tv_main_task_complete_state = (TextView) convertView
					.findViewById(R.id.tv_main_task_complete_state);
			holder.tv_main_task_detail_content = (TextView) convertView
					.findViewById(R.id.tv_main_task_detail_content);
			holder.tv_main_task_detail_goal = (TextView) convertView
					.findViewById(R.id.tv_main_task_detail_goal);
			holder.iv_task_complete = (ImageView) convertView
					.findViewById(R.id.iv_task_complete);
			holder.iv_task_medal = (ImageView) convertView
					.findViewById(R.id.iv_task_medal);
			holder.view_progress = (View) convertView
					.findViewById(R.id.view_progress);

			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		ModelDailyOrMainTask modelDailyTask = (ModelDailyOrMainTask) getItem(position);

		String progress_rate = modelDailyTask.getProgress_rate();
		String status = modelDailyTask.getStatus();

		if (progress_rate!=null&&progress_rate.contains("/")) {
			String[] progress=progress_rate.split("/");
			int doneTask=Integer.parseInt(progress[0].trim());
			int totalTask=Integer.parseInt(progress[1].trim());

			if (status.equals("0")||doneTask == 0) {
				holder.tv_main_task_complete_state.setText("(未完成)");
				holder.tv_main_task_complete_state.setTextColor(context
						.getResources().getColor(R.color.bg_task_not_complete));
			}else if (status.equals("1")) {
				holder.iv_task_complete.setVisibility(View.VISIBLE);
				holder.tv_main_task_complete_state.setText("已完成");
				holder.tv_main_task_complete_state.setTextColor(context
						.getResources().getColor(R.color.bg_task_complete_state_blue_txt));
			}else if(status.equals("0")&&doneTask!=0){
				holder.tv_main_task_complete_state.setText("(" + progress_rate+ ")");
				holder.tv_main_task_complete_state.setTextColor(context
						.getResources().getColor(R.color.bg_task_complete_state_blue_txt));
			}

			/**
			 *  如果任务总数为1，那么隐藏进度条
			 *  否则，进度用屏幕宽度与获取的值按比例显示
			 */
			if (!progress_rate.equals("null")&&totalTask != -1) {
				if (totalTask == 1) {
					holder.view_progress.setVisibility(View.GONE);
				} else if(doneTask!=0){
					holder.view_progress.setVisibility(View.VISIBLE);
					int windowWidth = UnitSociax.getWindowWidth(context);
					android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							windowWidth * doneTask / totalTask, UnitSociax.dip2px(context, 3));
					holder.view_progress.setLayoutParams(lp);
				}
			}
		}else {
			if (status.equals("1")){
				holder.iv_task_complete.setVisibility(View.VISIBLE);
				holder.tv_main_task_complete_state.setText("已完成");
				holder.tv_main_task_complete_state.setTextColor(context
						.getResources().getColor(R.color.bg_task_complete_state_blue_txt));
			}else {
				holder.tv_main_task_complete_state.setText("(未完成)");
				holder.tv_main_task_complete_state.setTextColor(context
						.getResources().getColor(R.color.bg_task_not_complete));
			}
		}

		holder.tv_main_task_name.setText(modelDailyTask.getName());
		holder.tv_main_task_detail_content.setText(modelDailyTask.getDesc());
		holder.tv_main_task_detail_goal.setText("奖励：经验值+"
				+ modelDailyTask.getExp() + "点  财富值+"
				+ modelDailyTask.getScore() + "点");
		// 修改分值的颜色
		SpannableString styledText = new SpannableString(
				holder.tv_main_task_detail_goal.getText());
		styledText.setSpan(
				new TextAppearanceSpan(context, R.style.taskExpBlue), 6, 8,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(
				new TextAppearanceSpan(context, R.style.taskExpBlue), 14, 16,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		holder.tv_main_task_detail_goal.setText(styledText,
				TextView.BufferType.SPANNABLE);

		String icon = modelDailyTask.getIcon();
		if (icon != null) {
//			ImageLoader.getInstance().displayImage(icon, holder.iv_task_medal,
//					Thinksns.getOptions());

			application.displayImage(icon,holder.iv_task_medal);
		}

		return convertView;
	}
}
