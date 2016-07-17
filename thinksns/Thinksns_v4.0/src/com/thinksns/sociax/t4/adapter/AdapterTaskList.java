package com.thinksns.sociax.t4.adapter;

import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionTaskComplete;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelTask;
import com.thinksns.sociax.t4.model.ModelTaskType;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-25
 * @version 1.0
 */
public class AdapterTaskList extends AdapterSociaxList {

	private Thinksns application;
	
	public AdapterTaskList(FragmentSociax fragment, ListData<SociaxItem> list) {
		super(fragment, list);
		
		application = (Thinksns) context.getApplicationContext();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder;
		if (convertView == null) {
			holder = new HolderSociax();
			convertView = inflater.inflate(R.layout.listitem_task, null);
			holder.tv_task_name = (TextView) convertView
					.findViewById(R.id.tv_task_name);
			holder.tv_task_desc = (TextView) convertView
					.findViewById(R.id.tv_task_desc);
			holder.tv_task_type = (TextView) convertView
					.findViewById(R.id.tv_task_type);
			holder.tv_task_status = (TextView) convertView
					.findViewById(R.id.tv_task_status);
			holder.img_task_img = (ImageView) convertView
					.findViewById(R.id.img_taskimg);

			holder.ll_part = (LinearLayout) convertView
					.findViewById(R.id.ll_title);
			holder.img_divider_footer = (View) convertView
					.findViewById(R.id.img_footer_divider);
			holder.img_divider_header = (View) convertView
					.findViewById(R.id.img_divider_header);
			holder.tv_part_status = (TextView) convertView
					.findViewById(R.id.tv_part_status);
			holder.rl_task_content = (RelativeLayout) convertView
					.findViewById(R.id.rl_task_content);

			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}
		convertView.setTag(R.id.tag_task, getItem(position));
		if (getItem(position).isFirst()) {// 如果是分类的第一个任务，需要显示一行内容
			holder.ll_part.setVisibility(View.VISIBLE);
			if (position == 0) {// 第一个分类去掉顶部分隔线
				holder.img_divider_header.setVisibility(View.GONE);
			} else {
				holder.img_divider_header.setVisibility(View.VISIBLE);
			}
			holder.tv_task_type.setText(getItem(position).getTask_Title());

		} else {
			holder.ll_part.setVisibility(View.GONE);
		}
		if (getItem(position).isLast()) {// 如果分类最后一个任务，则需要隐藏底部分隔线
			holder.img_divider_footer.setVisibility(View.GONE);
			if (position == getCount() - 1) {// 最后一个任务，添加分隔线
				holder.img_divider_footer.setVisibility(View.VISIBLE);
			} else {
				holder.img_divider_footer.setVisibility(View.GONE);
			}
		} else {
			holder.img_divider_footer.setVisibility(View.VISIBLE);
		}
		if (getItem(position).getTaskTypeStaus().equals("2")&&getItem(position).getTask_type_receive().equals("1")) {// 已经完成，不需要显示详细任务内容
			holder.rl_task_content.setVisibility(View.GONE);
			holder.tv_part_status.setText("已完成");
		} else {
			holder.rl_task_content.setVisibility(View.VISIBLE);
			if (getItem(position).getTaskTypeStaus().equals("1")) {
				holder.tv_part_status.setText("进行中");
			} else {
				holder.tv_part_status.setText("");
			}
			if (getItem(position).getImg().equals("")) {// 图片为空则使用默认的图片
				holder.img_task_img.setImageResource(R.drawable.default_task);
			} else {
//				ImageLoader.getInstance().displayImage(getItem(position).getImg(),holder.img_task_img, Thinksns.getOptions());
				
				application.displayImage(getItem(position).getImg(),holder.img_task_img);
			}
			holder.tv_task_name.setText(getItem(position).getTask_name());
			holder.tv_task_desc.setText(getItem(position).getReward());
			holder.tv_task_status.setTag(R.id.tag_task, getItem(position));
			holder.tv_task_status.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ModelTask mdTask = (ModelTask) v.getTag(R.id.tag_task);
					FunctionTaskComplete fc = new FunctionTaskComplete(context,
							AdapterTaskList.this, mdTask);
					fc.doCompleteStep();
				}
			});
			if (getItem(position).getStatus().equals("1")) {
				holder.tv_task_status
						.setBackgroundResource(R.drawable.tv_get_score);
				holder.tv_task_status.setText("");

			} else if (getItem(position).getStatus().equals("2")) {
				holder.tv_task_status
						.setBackgroundResource(R.drawable.tc_cmplete_task);
				holder.tv_task_status.setText("");
			} else if (getItem(position).getStatus().equals("0")) {
				holder.tv_task_status.setBackgroundResource(0);
				holder.tv_task_status.setText(getItem(position)
						.getProgress_rate());
			} else {
				holder.tv_task_status.setBackgroundResource(0);
				holder.tv_task_status.setText("");
			}
		}

		return convertView;
	}

	@Override
	public int getMaxid() {
		return 0;
	}

	@Override
	public ModelTask getItem(int position) {
		return (ModelTask) super.getItem(position);
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
		return getApi().getTaskList();
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return getApi().getTaskList();
	}

	Api.Tasks getApi() {
		return thread.getApp().getTasksApi();
	}

	@Override
	public void addFooter(ListData<SociaxItem> list) {
		// log测试
		Log.v("SociaxListAdapter--addFooter", "wztest addlist.size="
				+ (list == null ? "0" : list.size()));
		// 如果追加内容不为空则，则在尾部追加信息
		this.list.clear();
		if (null != list) {
			if (list.size() > 0) {
				hasRefreshFootData = true;

				for (int i = 0; i < list.size(); i++) {
					if (((ModelTaskType) list.get(i)).getStatus().equals("2")&&((ModelTaskType) list.get(i)).getReceive().equals("1")) {// 类型为2表示已经完成，只显示一条标记就行
						ModelTask md = new ModelTask();
						md.setFirst(true);
						md.setLast(true);
						md.setTask_Title(((ModelTaskType) list.get(i))
								.getTitle());
						md.setTaskTypeStaus("2");
						md.setTask_type_receive(((ModelTaskType) list.get(i)).getReceive());
						this.list.add(md);
					} else {// 其他类型的都要显示
						List<ModelTask> listi = ((ModelTaskType) list.get(i))
								.getTask_List();
						for (int j = 0; j < listi.size(); j++) {
							if (j == 0) {
								listi.get(j).setFirst(true);
							} else {
								listi.get(j).setFirst(false);
							}
							if (j == listi.size() - 1) {
								listi.get(j).setLast(true);
							}
							this.list.add(listi.get(j));
						}
					}
				}
				lastNum = this.list.size();
			}
		}
		// 如果脚部追加信息少于数据条数，则隐藏更多，否则显示更多
		getListView().hideFooterView();
		setShowFooter(false);
		// 如果list的数据为空，则表示没有更多数据，提示没有更多信息
		if (this.list.size() == 0 && !isHideFootToast) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
		// 数据修改好之后更新列表
		this.notifyDataSetChanged();
	}

	@Override
	public void addHeader(ListData<SociaxItem> list) {
		if (null != list) {
			// 如果追加内容不为空则，则在尾部追加信息
			this.list.clear();
			if (list.size() > 0) {
				hasRefreshFootData = true;
				for (int i = 0; i < list.size(); i++) {
					if (((ModelTaskType) list.get(i)).getStatus().equals("2")&&((ModelTaskType) list.get(i)).getReceive().equals("1")) {// 类型为2表示已经完成，只显示一条标记就行
						ModelTask md = new ModelTask();
						md.setFirst(true);
						md.setLast(true);
						md.setTask_Title(((ModelTaskType) list.get(i))
								.getTitle());
						md.setTaskTypeStaus("2");
						md.setTask_type_receive(((ModelTaskType) list.get(i)).getReceive());
						this.list.add(md);
					} else {// 其他类型的都要显示
						List<ModelTask> listi = ((ModelTaskType) list.get(i))
								.getTask_List();
						for (int j = 0; j < listi.size(); j++) {
							if (j == 0) {
								listi.get(j).setFirst(true);
							} else {
								listi.get(j).setFirst(false);
							}

							if (j == listi.size() - 1) {
								listi.get(j).setLast(true);
							}
							this.list.add(listi.get(j));
						}
					}
				}
				lastNum = this.list.size();
			}
		}
		// 如果脚部追加信息少于数据条数，则隐藏更多，否则显示更多
		getListView().hideFooterView();
		setShowFooter(false);
		// 如果list的数据为空，则表示没有更多数据，提示没有更多信息
		if (this.list.size() == 0 && !isHideFootToast) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
		this.notifyDataSetChanged();
	}

	/**
	 * 修改列表数据
	 */
	public void changeListData(ListData<SociaxItem> list) {
		if (null != list) {
			// 如果追加内容不为空则，则在尾部追加信息
			this.list.clear();
			if (list.size() > 0) {
				hasRefreshFootData = true;
				for (int i = 0; i < list.size(); i++) {
					if (((ModelTaskType) list.get(i)).getStatus().equals("2")&&((ModelTaskType) list.get(i)).getReceive().equals("1")) {// 类型为2表示已经完成，只显示一条标记就行
						ModelTask md = new ModelTask();
						md.setFirst(true);
						md.setLast(true);
						md.setTask_Title(((ModelTaskType) list.get(i))
								.getTitle());
						md.setTaskTypeStaus("2");
						md.setTask_type_receive(((ModelTaskType) list.get(i)).getReceive());
						this.list.add(md);
					} else {// 其他类型的都要显示
						List<ModelTask> listi = ((ModelTaskType) list.get(i))
								.getTask_List();
						for (int j = 0; j < listi.size(); j++) {
							if (j == 0) {
								listi.get(j).setFirst(true);
							} else {
								listi.get(j).setFirst(false);
							}
							if (j == listi.size() - 1) {
								listi.get(j).setLast(true);
							}
							this.list.add(listi.get(j));
						}
					}
				}
				lastNum = this.list.size();
			}
		}
		// 如果脚部追加信息少于数据条数，则隐藏更多，否则显示更多
		getListView().hideFooterView();
		setShowFooter(false);
		// 如果list的数据为空，则表示没有更多数据，提示没有更多信息
		if (this.list.size() == 0 && !isHideFootToast) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
		this.notifyDataSetChanged();
	}
}
