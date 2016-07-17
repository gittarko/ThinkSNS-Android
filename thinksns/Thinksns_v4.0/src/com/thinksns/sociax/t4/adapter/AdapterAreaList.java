package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.model.ModelAreaInfo;

import java.util.List;

public class AdapterAreaList extends BaseAdapter implements SectionIndexer {
	private static final String TAG = "AreaGroupMemberAdapter";
	public static int LSAT_ID = 0;
	private List<ModelAreaInfo> list = null;
	private Context mContext;
	int type = 0;// 城市列表0 地区列表1

	/**
	 * 
	 * @param mContext
	 * @param list
	 * @param type
	 *            找人时候0城市列表 修改资料时候使用的1地区列表
	 */
	public AdapterAreaList(Context mContext, List<ModelAreaInfo> list, int type) {
		this.mContext = mContext;
		this.list = list;
		this.type = type;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<ModelAreaInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final ModelAreaInfo mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.group_member_item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title_area);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog_area);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters().toUpperCase().subSequence(0, 1));
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		return view;

	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toLowerCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}