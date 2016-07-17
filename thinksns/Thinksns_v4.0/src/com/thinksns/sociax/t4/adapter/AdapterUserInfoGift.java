package com.thinksns.sociax.t4.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.api.Api.GiftApi;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionGiftDialog;
import com.thinksns.sociax.t4.android.gift.ActivityScoreShop;
import com.thinksns.sociax.t4.android.image.SmartImageView;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelGift;
import com.thinksns.sociax.t4.model.ModelGiftRow;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-20
 * @version 1.0
 */
public class AdapterUserInfoGift extends AdapterSociaxList {
	int column = 1;// 每行个数
	int remainder = 0;// 余数
	int row = 0;//
	private int uid;
	ListData<ModelGiftRow> listGiftRow;

	LinearLayout ll_send;
	TextView tv_send_gift, tv_nogift_my;
	private Thinksns application;

	/**
	 * 
	 * @param fragment
	 * @param list
	 * @param column
	 */
	public AdapterUserInfoGift(FragmentSociax fragment,
							   ListData<SociaxItem> list, int column, int uid) {
		super(fragment, list);
		this.uid = uid;
		this.column = column;
		this.remainder = list.size() % column;
		listGiftRow = new ListData<ModelGiftRow>();
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
	public AdapterUserInfoGift(ThinksnsAbscractActivity activity,
			ListData<SociaxItem> list, int column, int uid) {
		super(activity, list);
		this.uid = uid;
		this.column = column;
		this.remainder = list.size() % column;
		listGiftRow = new ListData<ModelGiftRow>();
		changeRowItem();
		application = (Thinksns) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		if (row > 0)// 有礼物
			return row;
		else {// 没有礼物
				// 当没有礼物是时候就直接跳转到礼物商城里面去哈
			Intent intent = new Intent(getContext(), ActivityScoreShop.class);
			getContext().startActivity(intent);
			((ThinksnsAbscractActivity) getContext()).finish();
			return 1;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (row == 0) {// 没有礼物的情况下
			convertView = inflater.inflate(R.layout.tips_nogift, null);
			TextView tv_no_gift_my = (TextView) convertView
					.findViewById(R.id.tv_nogift_my);
			TextView tv_send_gift = (TextView) convertView
					.findViewById(R.id.tv_send_gift);
			TextView tv_no_gift = (TextView) convertView
					.findViewById(R.id.tv_no_gift);

			if (uid == Thinksns.getMy().getUid()) {
				tv_no_gift_my.setVisibility(View.VISIBLE);
				tv_send_gift.setVisibility(View.GONE);
				tv_no_gift.setVisibility(View.GONE);
			} else {
				tv_no_gift_my.setVisibility(View.GONE);
				tv_send_gift.setVisibility(View.VISIBLE);
				tv_no_gift.setVisibility(View.VISIBLE);
			}
			tv_send_gift.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ActivityScoreShop.class);
					intent.putExtra("uid", uid);
					context.startActivity(intent);
				}
			});
			convertView.setTag(R.id.tag_type, 1);
		} else {
			HolderSociax holder;
			if (convertView == null
					|| ((Integer) convertView.getTag(R.id.tag_type) == 1)) {
				holder = new HolderSociax();
				convertView = inflater.inflate(R.layout.gift_item, null);
				holder.ll_gift_1 = (LinearLayout) convertView
						.findViewById(R.id.ll_gift_1);
				holder.img_item_pic_1 = (SmartImageView) convertView
						.findViewById(R.id.img_gift_1_pic);
				holder.tv_gift_1_name = (TextView) convertView
						.findViewById(R.id.tv_gift_1_name);
				holder.tv_gift_1_price = (TextView) convertView
						.findViewById(R.id.tv_gift_1_price);

				holder.ll_gift_2 = (LinearLayout) convertView
						.findViewById(R.id.ll_gift_2);
				holder.img_item_pic_2 = (SmartImageView) convertView
						.findViewById(R.id.img_gift_2_pic);
				holder.tv_gift_2_name = (TextView) convertView
						.findViewById(R.id.tv_gift_2_name);
				holder.tv_gift_2_price = (TextView) convertView
						.findViewById(R.id.tv_gift_2_price);

				holder.ll_gift_3 = (LinearLayout) convertView
						.findViewById(R.id.ll_gift_3);
				holder.img_item_pic_3 = (SmartImageView) convertView
						.findViewById(R.id.img_gift_3_pic);
				holder.tv_gift_3_name = (TextView) convertView
						.findViewById(R.id.tv_gift_3_name);
				holder.tv_gift_3_price = (TextView) convertView
						.findViewById(R.id.tv_gift_3_price);

				holder.ll_gift_4 = (LinearLayout) convertView
						.findViewById(R.id.ll_gift_4);
				holder.img_item_pic_4 = (SmartImageView) convertView
						.findViewById(R.id.img_gift_4_pic);
				holder.tv_gift_4_name = (TextView) convertView
						.findViewById(R.id.tv_gift_4_name);
				holder.tv_gift_4_price = (TextView) convertView
						.findViewById(R.id.tv_gift_4_price);
				convertView.setTag(R.id.tag_viewholder, holder);
				convertView.setTag(R.id.tag_type, 2);
			} else {
				holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
			}
			ModelGiftRow mdGiftRow = (ModelGiftRow) listGiftRow.get(position);
			switch (mdGiftRow.getChilds().size()) {
			case 1:
//				holder.img_item_pic_1.setImageUrl(mdGiftRow.getChildAt(0).getGiftPicurl());
				
				application.displayImage(mdGiftRow.getChildAt(0).getGiftPicurl(),holder.img_item_pic_1);
				
				holder.tv_gift_1_name.setText(mdGiftRow.getChildAt(0)
						.getGiftName());
				holder.tv_gift_1_price.setText(mdGiftRow.getChildAt(0)
						.getGiftPrice());

				holder.img_item_pic_2.setImageBitmap(null);
				holder.tv_gift_2_name.setText("");
				holder.tv_gift_2_price.setText("");

				holder.img_item_pic_3.setImageBitmap(null);
				holder.tv_gift_3_name.setText("");
				holder.tv_gift_3_price.setText("");

				holder.img_item_pic_4.setImageBitmap(null);
				holder.tv_gift_4_name.setText("");
				holder.tv_gift_4_price.setText("");

				holder.ll_gift_1.setTag(R.id.tag_gift, mdGiftRow.getChildAt(0));
				holder.ll_gift_1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, StaticInApp.EXCHARGE_GIFT);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});
				holder.ll_gift_2.setOnClickListener(null);
				holder.ll_gift_3.setOnClickListener(null);
				holder.ll_gift_4.setOnClickListener(null);

				break;
			case 2:
//				holder.img_item_pic_1.setImageUrl(mdGiftRow.getChildAt(0).getGiftPicurl());
//				holder.img_item_pic_2.setImageUrl(mdGiftRow.getChildAt(1).getGiftPicurl());
				
				application.displayImage(mdGiftRow.getChildAt(0).getGiftPicurl(),holder.img_item_pic_1);
				application.displayImage(mdGiftRow.getChildAt(1).getGiftPicurl(),holder.img_item_pic_2);
				
				holder.tv_gift_1_name.setText(mdGiftRow.getChildAt(0)
						.getGiftName());
				holder.tv_gift_1_price.setText(mdGiftRow.getChildAt(0)
						.getGiftPrice());
				holder.tv_gift_2_name.setText(mdGiftRow.getChildAt(1)
						.getGiftName());
				holder.tv_gift_2_price.setText(mdGiftRow.getChildAt(1)
						.getGiftPrice());

				holder.img_item_pic_3.setImageBitmap(null);
				holder.tv_gift_3_name.setText("");
				holder.tv_gift_3_price.setText("");

				holder.img_item_pic_4.setImageBitmap(null);
				holder.tv_gift_4_name.setText("");
				holder.tv_gift_4_price.setText("");
				holder.ll_gift_1.setTag(R.id.tag_gift, mdGiftRow.getChildAt(0));
				holder.ll_gift_1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, 2);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});
				holder.ll_gift_2.setTag(R.id.tag_gift, mdGiftRow.getChildAt(1));
				holder.ll_gift_2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, 2);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});

				holder.ll_gift_3.setOnClickListener(null);
				holder.ll_gift_4.setOnClickListener(null);
				break;
			case 3:
//				holder.img_item_pic_1.setImageUrl(mdGiftRow.getChildAt(0).getGiftPicurl());
//				holder.img_item_pic_2.setImageUrl(mdGiftRow.getChildAt(1).getGiftPicurl());
//				holder.img_item_pic_3.setImageUrl(mdGiftRow.getChildAt(2).getGiftPicurl());
				
				application.displayImage(mdGiftRow.getChildAt(0).getGiftPicurl(),holder.img_item_pic_1);
				application.displayImage(mdGiftRow.getChildAt(1).getGiftPicurl(),holder.img_item_pic_2);
				application.displayImage(mdGiftRow.getChildAt(2).getGiftPicurl(),holder.img_item_pic_3);
				
				holder.tv_gift_1_name.setText(mdGiftRow.getChildAt(0)
						.getGiftName());
				holder.tv_gift_1_price.setText(mdGiftRow.getChildAt(0)
						.getGiftPrice());
				
				holder.tv_gift_2_name.setText(mdGiftRow.getChildAt(1)
						.getGiftName());
				holder.tv_gift_2_price.setText(mdGiftRow.getChildAt(1)
						.getGiftPrice());

				holder.tv_gift_3_name.setText(mdGiftRow.getChildAt(2)
						.getGiftName());
				holder.tv_gift_3_price.setText(mdGiftRow.getChildAt(2)
						.getGiftPrice());

				holder.img_item_pic_4.setImageBitmap(null);
				holder.tv_gift_4_name.setText("");
				holder.tv_gift_4_price.setText("");

				holder.ll_gift_1.setTag(R.id.tag_gift, mdGiftRow.getChildAt(0));
				holder.ll_gift_1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, StaticInApp.EXCHARGE_GIFT);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});

				holder.ll_gift_2.setTag(R.id.tag_gift, mdGiftRow.getChildAt(1));
				holder.ll_gift_2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, StaticInApp.EXCHARGE_GIFT);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});

				holder.ll_gift_3.setTag(R.id.tag_gift, mdGiftRow.getChildAt(2));
				holder.ll_gift_3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, StaticInApp.EXCHARGE_GIFT);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});

				holder.ll_gift_4.setOnClickListener(null);

				break;
			case 4:
//				holder.img_item_pic_1.setImageUrl(mdGiftRow.getChildAt(0).getGiftPicurl());
//				holder.img_item_pic_2.setImageUrl(mdGiftRow.getChildAt(1).getGiftPicurl());
//				holder.img_item_pic_3.setImageUrl(mdGiftRow.getChildAt(2).getGiftPicurl());
//				holder.img_item_pic_4.setImageUrl(mdGiftRow.getChildAt(3).getGiftPicurl());
				
				application.displayImage(mdGiftRow.getChildAt(0).getGiftPicurl(),holder.img_item_pic_1);
				application.displayImage(mdGiftRow.getChildAt(1).getGiftPicurl(),holder.img_item_pic_2);
				application.displayImage(mdGiftRow.getChildAt(2).getGiftPicurl(),holder.img_item_pic_3);
				application.displayImage(mdGiftRow.getChildAt(3).getGiftPicurl(),holder.img_item_pic_4);
				
				holder.tv_gift_1_name.setText(mdGiftRow.getChildAt(0)
						.getGiftName());
				holder.tv_gift_1_price.setText(mdGiftRow.getChildAt(0)
						.getGiftPrice());

				holder.tv_gift_2_name.setText(mdGiftRow.getChildAt(1)
						.getGiftName());
				holder.tv_gift_2_price.setText(mdGiftRow.getChildAt(1)
						.getGiftPrice());
				
				holder.tv_gift_3_name.setText(mdGiftRow.getChildAt(2)
						.getGiftName());
				holder.tv_gift_3_price.setText(mdGiftRow.getChildAt(2)
						.getGiftPrice());

				holder.tv_gift_4_name.setText(mdGiftRow.getChildAt(3)
						.getGiftName());
				holder.tv_gift_4_price.setText(mdGiftRow.getChildAt(3)
						.getGiftPrice());

				holder.ll_gift_4.setTag(R.id.tag_gift, mdGiftRow.getChildAt(3));
				holder.ll_gift_4.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, StaticInApp.EXCHARGE_GIFT);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});
				holder.ll_gift_1.setTag(R.id.tag_gift, mdGiftRow.getChildAt(0));
				holder.ll_gift_1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, StaticInApp.EXCHARGE_GIFT);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});
				holder.ll_gift_2.setTag(R.id.tag_gift, mdGiftRow.getChildAt(1));
				holder.ll_gift_2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, StaticInApp.EXCHARGE_GIFT);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});
				holder.ll_gift_3.setTag(R.id.tag_gift, mdGiftRow.getChildAt(2));
				holder.ll_gift_3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FunctionGiftDialog dialog = new FunctionGiftDialog(uid,
								context, StaticInApp.EXCHARGE_GIFT);
						dialog.setGift((ModelGift) v.getTag(R.id.tag_gift));
						// dialog.show();
					}
				});
				break;
			default:
				break;

			}
		}
		return convertView;
	}

	@Override
	public ModelGift getItem(int position) {
		return (ModelGift) this.list.get(position);
	}

	@Override
	public int getMaxid() {
		return getLast() == null ? 0 : Integer.parseInt(getLast().getId());
	}

	@Override
	public ModelGift getLast() {
		if (list.size() > 0) {
			return (ModelGift) this.list.get(this.list.size() - 1);
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
			return getApi().getUerGift(uid, getMaxid());
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
			return getApi().getUerGift(uid, 0);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		}
	}

	GiftApi getApi() {
		return thread.getApp().getApiGift();
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
					ModelGiftRow mdGiftRow = new ModelGiftRow(
							remainder == 0 ? column : remainder);
					for (int j = 0; j < (remainder == 0 ? column : remainder); j++) {
						mdGiftRow.getChilds().add(list.get(i * column + j));
					}
					listGiftRow.add(mdGiftRow);
				} else {
					ModelGiftRow mdGiftRow = new ModelGiftRow(column);
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
			Toast.makeText(context, R.string.refresh_error, Toast.LENGTH_SHORT)
					.show();
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
			Toast.makeText(context, R.string.refresh_error, Toast.LENGTH_SHORT)
					.show();
		}
	}
}
