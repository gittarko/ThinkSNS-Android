package com.thinksns.sociax.t4.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.ApiWeiba;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;

import com.thinksns.sociax.t4.android.weiba.ActivityPostCommon;
import com.thinksns.sociax.t4.android.weiba.ActivityWeiba;
import com.thinksns.sociax.t4.android.weiba.ActivityWeibaCommon;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelWeiba;
import com.thinksns.sociax.t4.unit.DynamicInflateForWeiba;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明：微吧列表，默认使用首页的微吧列表，其他微吧列表请重写RefreshNew和Footer的接口
 * 
 * @author wz
 * @date 2014-12-20
 * @version 1.0
 */
public class AdapterWeibaList extends AdapterSociaxList {
	private View headerView;
	private ListView listView;
	protected int count_my = 0,// 我的圈子条数
			count_limit = 0;// 推荐圈子的条数
	protected boolean isShowAll = true;	//是否显示查看全部微吧的条目

	public AdapterWeibaList(FragmentSociax fragment, ListData<SociaxItem> list) {
		super(fragment, list);
		isHideFootToast = true;
	}

	public AdapterWeibaList(FragmentSociax fragment, ListData<SociaxItem> list, View headerView, ListView listView) {
		super(fragment, list);
		isHideFootToast = true;
		this.headerView = headerView;
		this.listView = listView;
	}

	public AdapterWeibaList(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list) {
		super(context, list);
	}

	@Override
	public ModelWeiba getItem(int position) {
		return (ModelWeiba) super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder;
		if (convertView == null) {
			holder = new HolderSociax();
			convertView = inflater.inflate(R.layout.listitem_weiba, null);
			holder.img_weiba_icon1 = (ImageView) convertView
					.findViewById(R.id.img_weiba_icon);
			holder.tv_weiba_name = (TextView) convertView
					.findViewById(R.id.tv_weiba_name);
			holder.tv_weiba_des = (TextView) convertView
					.findViewById(R.id.tv_weiba_des);
			holder.tv_part_name = (TextView) convertView
					.findViewById(R.id.tv_part_name);
			holder.ll_weiba_info = (LinearLayout) convertView
					.findViewById(R.id.ll_weiba_info);
            holder.stub_part_name = (ViewStub) convertView.findViewById(R.id.stub_part_name);
            holder.stub_weiba_info = (ViewStub) convertView.findViewById(R.id.stub_weiba_info);
			holder.tv_weiba_title1 = (TextView)convertView.findViewById(R.id.tv_all_weiba);
			holder.ll_weiba_top = (LinearLayout)convertView.findViewById(R.id.ll_weiba_show);

            convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		convertView.setTag(R.id.tag_weiba, getItem(position));

		ModelWeiba weiba = getItem(position);
        //设置标题栏
		if (weiba.isFirstInPart()) {
            DynamicInflateForWeiba.addPartName(holder.stub_part_name, weiba.getStr_partName());
		} else {
            holder.stub_part_name.setVisibility(View.GONE);
		}


		//设置微吧信息
        if (weiba.getWeiba_name() != null) {
            DynamicInflateForWeiba.addWeibaInfo(context, holder.stub_weiba_info, weiba, this);
        } else {
            holder.stub_weiba_info.setVisibility(View.GONE);
        }


		if(isShowAll && holder.ll_weiba_top != null
				&& weiba.isFollow()
				&& position < getCount() - 1
				&& !getItem(position + 1).isFollow()) {
			//此句是为了显示查看全部微吧
			holder.ll_weiba_top.setVisibility(View.VISIBLE);
			holder.tv_weiba_title1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//跳转至全部微吧页面
					Intent intent = new Intent(v.getContext(), ActivityWeibaCommon.class);
					intent.putExtra("name", "全部微吧");
					intent.putExtra("type", ActivityWeibaCommon.FRAGMENT_WEIBA_ALL);
					v.getContext().startActivity(intent);
				}
			});
		}else {
			holder.ll_weiba_top.setVisibility(View.GONE);
		}

		return convertView;
	}

	@Override
	public int getMaxid() {
		return getLast().getWeiba_id();
	}

	@Override
	public ModelWeiba getLast() {
		return (ModelWeiba) super.getLast();
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
		return new Api.WeibaApi().getMyWeibaList(PAGE_COUNT, getMaxid(), mListener);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return new Api.WeibaApi().getMyWeibaList(PAGE_COUNT, 0, mListener);
	}

	ApiHttpClient.HttpResponseListener mListener = new ApiHttpClient.HttpResponseListener() {

		@Override
		public void onSuccess(final Object result) {
			if (list != null && list.size() > 0) {
				list.clear();
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ListData<SociaxItem> data = (ListData<SociaxItem>) result;
					if (headerView != null
							&& listView != null
							&& data.size() >= 1) {
						ModelWeiba weiba = (ModelWeiba) data.get(0);
						if (!weiba.isFollow()) {
							// 我的关注为空
							headerView.findViewById(R.id.ll_empty_follow).setVisibility(View.VISIBLE);
						}else {
							headerView.findViewById(R.id.ll_empty_follow).setVisibility(View.GONE);
						}
					}

					addFooter(data);
				}
			});
		}

		@Override
		public void onError(Object result) {
            Toast.makeText(context, "连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
		}
	};

}
