package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionChangeFollow;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;

import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

/**
 * 类说明：
 * 
 * @author Administrator
 * @date 2014-11-4
 * @version 1.0
 */
public class AdapterFindPeople extends AdapterUserFollowingList {
	private int time = 0;

	public AdapterFindPeople(FragmentSociax fragment,
							 ListData<SociaxItem> list, int uid) {
		super(fragment, list, uid);
	}

	public AdapterFindPeople(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list) {
		super(context, list);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {

		return (ListData<SociaxItem>) getApiUser().searchUser(httpListener);
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return (ListData<SociaxItem>) getApiUser().searchUser(httpListener);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax viewHolder;
		if (convertView == null) {
			viewHolder = new HolderSociax();
			convertView = inflater.inflate(R.layout.listitem_user, null);
			viewHolder.tv_user_photo = (ImageView) convertView.findViewById(R.id.image_photo);
			viewHolder.tv_user_name = (TextView) convertView
					.findViewById(R.id.unnames);
			viewHolder.tv_user_content = (TextView) convertView
					.findViewById(R.id.uncontent);
			viewHolder.tv_user_add = (TextView) convertView
					.findViewById(R.id.image_add);
			convertView.setTag(R.id.tag_viewholder, viewHolder);
			convertView.setTag(R.id.tag_search_user, getItem(position));
		} else {
			viewHolder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}
		convertView.setTag(R.id.tag_search_user, getItem(position));
		Glide.with(context).load(getItem(position).getUserface())
		.diskCacheStrategy(DiskCacheStrategy.ALL)
		.transform(new GlideCircleTransform(context))
		.crossFade()
		.into(viewHolder.tv_user_photo);

		viewHolder.tv_user_name.setText(getItem(position).getUname());
		String intro = getItem(position).getIntro();
		if(intro == null || intro.equals("null") || intro.isEmpty())
			intro = "这家伙很懒，什么也没留下";
		viewHolder.tv_user_content.setText(intro);

		viewHolder.tv_user_add.setTag(R.id.tag_position, position);
		viewHolder.tv_user_add.setTag(R.id.tag_follow, getItem(position));

		if (getItem(position).getFollowing().equals("0")) {
			//加关注
			viewHolder.tv_user_add.setBackgroundResource(R.drawable.roundbackground_green_digg);
			viewHolder.tv_user_add.setText(R.string.fav_add_follow);
			viewHolder.tv_user_add.setTextColor(context.getResources().getColor(R.color.fav_border));
		} else {
			//取消关注
			viewHolder.tv_user_add.setBackgroundResource(R.drawable.roundbackground_fav_true);
			viewHolder.tv_user_add.setText(R.string.fav_followed);
			viewHolder.tv_user_add.setTextColor(context.getResources().getColor(R.color.fav_text_true));
		}

		viewHolder.tv_user_add.setVisibility(View.VISIBLE);
		viewHolder.tv_user_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setClickable(false);
				FunctionChangeFollow fcChangeFollow = new FunctionChangeFollow(
						context, AdapterFindPeople.this, v);
				fcChangeFollow.changeListFollow();
			}
		});

		return convertView;
	}

	/**
	 * 底部追加信息
	 * 
	 * @param list
	 */
	public void addFooter(ListData<SociaxItem> list) {
		// 如果追加内容不为空则，则在尾部追加信息
		if (null != list) {
			if (list.size() > 0) {
				hasRefreshFootData = true;
				this.list.clear();
				this.list.addAll(list);
				lastNum = this.list.size();
			}
		}
//		getListView().hideFooterView();

		// 如果list的数据为空，则表示没有更多数据，提示没有更多信息
		if (this.list.size() == 0 && !isHideFootToast) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
		// 数据修改好之后更新列表
		this.notifyDataSetChanged();
	}

	public void addHeader(ListData<SociaxItem> list) {
		if (null != list) {
			this.list.clear();
			this.list.addAll(list);
			// this.list.addAll(this.list.size(), tempList);
			// 修改适配器绑定的数组后
			this.notifyDataSetChanged();
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_success,
					Toast.LENGTH_SHORT).show();
		}
		getListView().hideFooterView();

	}

	@Override
	public int getMaxid() {
		if (getLast() == null)
			return 0;
		else
			return getLast().getId();
	}

	@Override
	public void loadInitData() {
		// 判断网路是否可用
		if (!UnitSociax.isNetWorkON(context)) {
			Toast.makeText(context, R.string.net_fail, Toast.LENGTH_SHORT)
					.show();
			return;
			}
		setLoadingView();
		if (loadingView != null)
			loadingView.show((View) getListView());
		if ((context.getOtherView() != null) && (time == 0)) {
			loadingView.show(context.getOtherView());
			time++;
		}
		refreshNewSociaxList();
	}
}
