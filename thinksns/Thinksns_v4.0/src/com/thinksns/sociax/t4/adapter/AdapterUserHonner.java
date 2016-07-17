package com.thinksns.sociax.t4.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinksns.sociax.api.Api.Users;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.image.SmartImageView;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelMedalRow;
import com.thinksns.sociax.t4.model.ModelUserMedal;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/** 
 * 类说明：   
 * @author  wz    
 * @date    2015-1-26
 * @version 1.0
 */
public class AdapterUserHonner extends AdapterSociaxList {
	int column = 1;// 每行个数
	int remainder = 0;// 余数
	int row = 0;//
	private int uid;
	ListData<ModelMedalRow> listGiftRow;
	LinearLayout ll_send;
	TextView tv_send_gift, tv_nogift_my;
	private Thinksns application;
	
	/**
	 * 
	 * @param fragment
	 * @param list
	 * @param column
	 */
	public AdapterUserHonner(FragmentSociax fragment,
							 ListData<SociaxItem> list, int column, int uid) {
		super(fragment, list);
		this.uid = uid;
		this.column = column;
		this.remainder = list.size() % column;
		listGiftRow = new ListData<ModelMedalRow>();
		changeRowItem();
		application = (Thinksns) context.getApplicationContext();
	}

	/**
	 * 
	 * @param activity
	 * @param list
	 * @param column
	 *            每行列数，目前固定为4
	 * @param uid
	 */
	public AdapterUserHonner(ThinksnsAbscractActivity activity,
			ListData<SociaxItem> list, int column, int uid) {
		super(activity, list);
		this.uid = uid;
		this.column = column;
		this.remainder = list.size() % column;
		listGiftRow = new ListData<ModelMedalRow>();
		changeRowItem();
		application = (Thinksns) context.getApplicationContext();
	}

	@Override
	public int getCount() {
			return row;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			HolderSociax holder;
			if (convertView == null) {
				holder = new HolderSociax();
				convertView = inflater.inflate(R.layout.listitem_honner, null);
				holder.ll_gift_1 = (LinearLayout) convertView
						.findViewById(R.id.ll_gift_1);
				holder.img_item_pic_1 = (SmartImageView) convertView
						.findViewById(R.id.img_gift_1_pic);
				holder.tv_gift_1_name = (TextView) convertView
						.findViewById(R.id.tv_gift_1_name);

				holder.ll_gift_2 = (LinearLayout) convertView
						.findViewById(R.id.ll_gift_2);
				holder.img_item_pic_2 = (SmartImageView) convertView
						.findViewById(R.id.img_gift_2_pic);
				holder.tv_gift_2_name = (TextView) convertView
						.findViewById(R.id.tv_gift_2_name);

				holder.ll_gift_3 = (LinearLayout) convertView
						.findViewById(R.id.ll_gift_3);
				holder.img_item_pic_3 = (SmartImageView) convertView
						.findViewById(R.id.img_gift_3_pic);
				holder.tv_gift_3_name = (TextView) convertView
						.findViewById(R.id.tv_gift_3_name);

				holder.ll_gift_4 = (LinearLayout) convertView
						.findViewById(R.id.ll_gift_4);
				holder.img_item_pic_4 = (SmartImageView) convertView
						.findViewById(R.id.img_gift_4_pic);
				holder.tv_gift_4_name = (TextView) convertView
						.findViewById(R.id.tv_gift_4_name);
				convertView.setTag(R.id.tag_viewholder, holder);
			} else {
				holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
			}
			ModelMedalRow mdGiftRow = (ModelMedalRow) listGiftRow.get(position);
			switch (mdGiftRow.getChilds().size()) {
			case 1:
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(0)
//						.getSrc(),holder.img_item_pic_1, Thinksns.getOptions());
				
				application.displayImage(mdGiftRow.getChildAt(0)
						.getSrc(),holder.img_item_pic_1);
				
				holder.tv_gift_1_name.setText(mdGiftRow.getChildAt(0)
						.getName());

				holder.img_item_pic_2.setImageBitmap(null);
				holder.tv_gift_2_name.setText("");

				holder.img_item_pic_3.setImageBitmap(null);
				holder.tv_gift_3_name.setText("");

				holder.img_item_pic_4.setImageBitmap(null);
				holder.tv_gift_4_name.setText("");
				break;
			case 2:
				
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(0)
//						.getSrc(),holder.img_item_pic_1, Thinksns.getOptions());
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(1)
//						.getSrc(),holder.img_item_pic_2, Thinksns.getOptions());
				
				application.displayImage(mdGiftRow.getChildAt(0)
						.getSrc(),holder.img_item_pic_1);
				application.displayImage(mdGiftRow.getChildAt(1)
						.getSrc(),holder.img_item_pic_2);
				
				holder.tv_gift_1_name.setText(mdGiftRow.getChildAt(0)
						.getName());
				
				holder.tv_gift_2_name.setText(mdGiftRow.getChildAt(1)
						.getName());

				holder.img_item_pic_3.setImageBitmap(null);
				holder.tv_gift_3_name.setText("");

				holder.img_item_pic_4.setImageBitmap(null);
				holder.tv_gift_4_name.setText("");
				
				break;
			case 3:
				
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(0)
//						.getSrc(),holder.img_item_pic_1, Thinksns.getOptions());
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(1)
//						.getSrc(),holder.img_item_pic_2, Thinksns.getOptions());
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(2)
//						.getSrc(),holder.img_item_pic_3, Thinksns.getOptions());
				
				application.displayImage(mdGiftRow.getChildAt(0)
						.getSrc(),holder.img_item_pic_1);
				application.displayImage(mdGiftRow.getChildAt(1)
						.getSrc(),holder.img_item_pic_2);
				application.displayImage(mdGiftRow.getChildAt(2)
						.getSrc(),holder.img_item_pic_3);
				
				holder.tv_gift_1_name.setText(mdGiftRow.getChildAt(0)
						.getName());
				holder.tv_gift_2_name.setText(mdGiftRow.getChildAt(1)
						.getName());
				holder.tv_gift_3_name.setText(mdGiftRow.getChildAt(2)
						.getName());

				holder.img_item_pic_4.setImageBitmap(null);
				holder.tv_gift_4_name.setText("");
				break;
			case 4:
				
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(0)
//						.getSrc(),holder.img_item_pic_1, Thinksns.getOptions());
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(1)
//						.getSrc(),holder.img_item_pic_2, Thinksns.getOptions());
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(2)
//						.getSrc(),holder.img_item_pic_3, Thinksns.getOptions());
//				ImageLoader.getInstance().displayImage(mdGiftRow.getChildAt(3)
//						.getSrc(),holder.img_item_pic_4, Thinksns.getOptions());
				
				application.displayImage(mdGiftRow.getChildAt(0)
						.getSrc(),holder.img_item_pic_1);
				application.displayImage(mdGiftRow.getChildAt(1)
						.getSrc(),holder.img_item_pic_2);
				application.displayImage(mdGiftRow.getChildAt(2)
						.getSrc(),holder.img_item_pic_3);
				application.displayImage(mdGiftRow.getChildAt(3)
						.getSrc(),holder.img_item_pic_4);
				
				holder.tv_gift_1_name.setText(mdGiftRow.getChildAt(0)
						.getName());
				holder.tv_gift_2_name.setText(mdGiftRow.getChildAt(1)
						.getName());
				holder.tv_gift_3_name.setText(mdGiftRow.getChildAt(2)
						.getName());
				holder.tv_gift_4_name.setText(mdGiftRow.getChildAt(3)
						.getName());
				break;
		}
		return convertView;
	}

	@Override
	public ModelUserMedal getItem(int position) {
		return (ModelUserMedal) this.list.get(position);
	}

	@Override
	public int getMaxid() {
		return getLast() == null ? 0 :getLast().getId();
	}

	@Override
	public ModelUserMedal getLast() {
		if (list.size() > 0) {
			return (ModelUserMedal) this.list.get(this.list.size() - 1);
		} else
			return null;
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
			return getApi().getUserHonner(uid, getMaxid());
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApi().getUserHonner(uid, 0);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		}
	}

	Users getApi() {
		return thread.getApp().getUsers();
	}

	/**
	 * 头部追加信息，有两种形式，一种是直接穿第一条微博id，获取比当前列表中没有的新微博数据，t3在用，此处修改成第二种；
	 * 第二种是调用跟微博第一次刷新一样的接口，然后把列表清除，保留前pagecount条；
	 * 
	 * @param list
	 */
	public void addHeader(ListData<SociaxItem> list) {
		if (null != list) {
			this.list.clear();
			this.list.addAll(list);
			// this.list.addAll(this.list.size(), tempList);
			// 修改适配器绑定的数组后
			changeRowItem();
			this.notifyDataSetChanged();
		}
		if (list.size() == PAGE_COUNT) {
			getListView().showFooterView();
			setShowFooter(true);
		} else {
			getListView().hideFooterView();
			setShowFooter(false);
		}
	}

	private void changeRowItem() {
		this.remainder = list.size() % column;
		if (remainder == 0) {
			row = this.list.size() / column;
		} else {
			row = this.list.size() / column + 1;
		}
		listGiftRow.clear();
		if (row > 0)// 有数据
			for (int i = 0; i < row; i++) {// 每一行添加数据
				if (i == row - 1) {// 最后一行添加余数个礼物
					ModelMedalRow mdGiftRow = new ModelMedalRow(
							remainder == 0 ? column : remainder);
					for (int j = 0; j < (remainder == 0 ? column : remainder); j++) {
						mdGiftRow.getChilds().add(list.get(i * column + j));
					}
					listGiftRow.add(mdGiftRow);
				} else {
					ModelMedalRow mdGiftRow = new ModelMedalRow(column);
					for (int j = 0; j < column; j++) {
						mdGiftRow.getChilds().add(list.get(i * column + j));
					}
					listGiftRow.add(mdGiftRow);
				}
			}
	}

	@Override
	public void addFooter(ListData<SociaxItem> list) {
		// log测试
		// 如果追加内容不为空则，则在尾部追加信息
		if (null != list) {
			if (list.size() > 0) {
				hasRefreshFootData = true;
				this.list.addAll(list);
				lastNum = this.list.size();
			}
		}
		// 如果脚部追加信息少于数据条数，则隐藏更多，否则显示更多
		if (list == null || list.size() == 0 || list.size() < PAGE_COUNT) {
			getListView().hideFooterView();
			setShowFooter(false);
		} else {
			getListView().showFooterView();
			setShowFooter(true);
		}
		// 如果list的数据为空，则表示没有更多数据，提示没有更多信息
		if (this.list.size() == 0 && !isHideFootToast) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
		// 数据修改好之后更新列表
		changeRowItem();
		this.notifyDataSetChanged();
	}
	@Override
	public void changeListData(ListData<SociaxItem> list) {
		if (null != list) {
			if (list.size() > 0) {
				hasRefreshFootData = true;
				this.list.clear();
				this.list.addAll(list);
				lastNum = this.list.size();
				changeRowItem();
				this.notifyDataSetChanged();
			}
		}
		// 如果脚部追加信息少于数据条数，则隐藏更多，否则显示更多
		if (list == null || list.size() == 0 || list.size() < PAGE_COUNT) {
			getListView().hideFooterView();
			setShowFooter(false);
		} else {
			getListView().showFooterView();
			setShowFooter(true);
		}
		if (this.list.size() == 0 && !isHideFootToast) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
	}
}
