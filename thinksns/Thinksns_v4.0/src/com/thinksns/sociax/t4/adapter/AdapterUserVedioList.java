package com.thinksns.sociax.t4.adapter;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.thinksns.sociax.api.Api.Users;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelUserPhoto;
import com.thinksns.sociax.t4.model.ModelUserVideoRow;
import com.thinksns.sociax.t4.model.ModelVideo;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-24
 * @version 1.0
 */
public class AdapterUserVedioList extends AdapterSociaxList {
	private int uid;

	int column = 1;// 每行个数
	int remainder = 0;// 余数
	int row = 0;//
	ListData<ModelUserVideoRow> listGiftRow;
	private Thinksns application;

	@Override
	public int getCount() {
		return row;
	}

	@Override
	public ModelUserVideoRow getItem(int position) {
		return (ModelUserVideoRow) listGiftRow.get(position);
	}

	public AdapterUserVedioList(FragmentSociax fragment,
								ListData<SociaxItem> list, int column, int uid) {
		super(fragment, list);
		this.uid = uid;
		this.column = column;
		this.remainder = list.size() % column;
		listGiftRow = new ListData<ModelUserVideoRow>();
		changeRowItem();
		application = (Thinksns) context.getApplicationContext();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder;
		if (convertView == null) {
			holder = new HolderSociax();
			convertView = inflater.inflate(R.layout.listitem_user_pv, null);
			holder.img_item_pic_1 = (ImageView) convertView
					.findViewById(R.id.img_gift_1_pic);
			holder.img_item_pic_2 = (ImageView) convertView
					.findViewById(R.id.img_gift_2_pic);
			holder.img_item_pic_3 = (ImageView) convertView
					.findViewById(R.id.img_gift_3_pic);
			
			holder.img_vedio_one_bf=convertView.findViewById(R.id.img_vedio_one_bf);
			holder.img_vedio_two_bf=convertView.findViewById(R.id.img_vedio_two_bf);
			holder.img_vedio_three_bf=convertView.findViewById(R.id.img_vedio_three_bf);
			convertView.setTag(holder);
		} else {
			holder = (HolderSociax) convertView.getTag();
		}
		holder.img_item_pic_1.setTag(R.id.tag_position, position * column);
		holder.img_item_pic_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ModelVideo md=(ModelVideo) list.get((Integer) v.getTag(R.id.tag_position));
				
				Intent intent=new Intent(context,NetActivity.class);
				 intent.putExtra("url",md.getVideoDetail());
				 context.startActivity(intent);
			}
		});
		holder.img_item_pic_2.setTag(R.id.tag_position, position * column + 1);
		holder.img_item_pic_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ModelVideo md=(ModelVideo) list.get((Integer) v.getTag(R.id.tag_position));

				Intent intent=new Intent(context,NetActivity.class);
				 intent.putExtra("url",md.getVideoDetail());
				 context.startActivity(intent);
			}
		});
		holder.img_item_pic_3.setTag(R.id.tag_position, position * column + 2);
		holder.img_item_pic_3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ModelVideo md=(ModelVideo) list.get((Integer) v.getTag(R.id.tag_position));

				Intent intent=new Intent(context,NetActivity.class);
				 intent.putExtra("url",md.getVideoDetail());
				 context.startActivity(intent);
			}
		});
		ModelUserVideoRow mdRow = getItem(position);
		switch (mdRow.getChilds().size()) {
		case 1:
			
			application.displayImage(mdRow.getChildAt(0)
					.getVideoImgUrl(), holder.img_item_pic_1);
			
			holder.img_item_pic_2.setImageBitmap(null);
			holder.img_item_pic_3.setImageBitmap(null);
			holder.img_item_pic_1.setClickable(true);
			holder.img_item_pic_2.setClickable(false);
			holder.img_item_pic_3.setClickable(false);
			holder.img_vedio_one_bf.setVisibility(View.VISIBLE);
			holder.img_vedio_two_bf.setVisibility(View.GONE);
			holder.img_vedio_three_bf.setVisibility(View.GONE);
			break;
		case 2:
			
			application.displayImage(mdRow.getChildAt(0)
					.getVideoImgUrl(), holder.img_item_pic_1);
			application.displayImage(mdRow.getChildAt(1)
					.getVideoImgUrl(), holder.img_item_pic_2);
			
			holder.img_item_pic_3.setImageBitmap(null);
			holder.img_item_pic_1.setClickable(true);
			holder.img_item_pic_2.setClickable(true);
			holder.img_item_pic_3.setClickable(false);
			holder.img_vedio_one_bf.setVisibility(View.VISIBLE);
			holder.img_vedio_two_bf.setVisibility(View.VISIBLE);
			holder.img_vedio_three_bf.setVisibility(View.GONE);
			break;
		case 3:
			
			application.displayImage(mdRow.getChildAt(0)
					.getVideoImgUrl(), holder.img_item_pic_1);
			application.displayImage(mdRow.getChildAt(1)
					.getVideoImgUrl(), holder.img_item_pic_2);
			application.displayImage(mdRow.getChildAt(2)
					.getVideoImgUrl(), holder.img_item_pic_3);
			
			holder.img_item_pic_1.setClickable(true);
			holder.img_item_pic_2.setClickable(true);
			holder.img_item_pic_3.setClickable(true);
			holder.img_vedio_one_bf.setVisibility(View.VISIBLE);
			holder.img_vedio_two_bf.setVisibility(View.VISIBLE);
			holder.img_vedio_three_bf.setVisibility(View.VISIBLE);
			break;
		}

		return convertView;
	}

	@Override
	public int getMaxid() {
		if (getLast() == null)
			return 0;
		else
			return Integer.parseInt(((ModelUserPhoto) getLast()).getImageId());
	}

	@Override
	public SociaxItem getLast() {
		if (list.size() > 0) {
			return this.list.get(this.list.size() - 1);
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
		return getApi().getUserVedio(uid, getMaxid(), 50, httpListener);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return getApi().getUserVedio(uid, 0, 50, httpListener);
	}

	private void changeRowItem() {
		this.remainder = list.size() % column;
		if (remainder == 0) {
			row = this.list.size() / column;
		} else {
			row = this.list.size() / column + 1;
		}
		if (row > 0)// 有数据
		{
			listGiftRow.clear();
			for (int i = 0; i < row; i++) {// 每一行添加数据
				if (i == row - 1) {// 最后一行添加余数个礼物
					ModelUserVideoRow mdRow = new ModelUserVideoRow(
							remainder == 0 ? column : remainder);
					for (int j = 0; j < (remainder == 0 ? column : remainder); j++) {
						mdRow.getChilds().add(list.get(i * column + j));
					}
					listGiftRow.add(mdRow);
				} else {
					ModelUserVideoRow mdGiftRow = new ModelUserVideoRow(column);
					for (int j = 0; j < column; j++) {
						mdGiftRow.getChilds().add(list.get(i * column + j));
					}
					listGiftRow.add(mdGiftRow);
				}
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
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_success,
					Toast.LENGTH_SHORT).show();
		}
		if (list.size() == PAGE_COUNT) {
			getListView().showFooterView();
			setShowFooter(true);
		} else {
			getListView().hideFooterView();
			setShowFooter(false);
		}
	}
	
	public void skipToBrowser(ModelVideo md){
		Uri uri = Uri.parse(md.getVideoDetail());    
		Intent it = new Intent(Intent.ACTION_VIEW, uri);    
		context.startActivity(it); 
	}
}
