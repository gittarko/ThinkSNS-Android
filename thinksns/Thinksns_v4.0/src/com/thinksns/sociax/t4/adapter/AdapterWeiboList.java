package com.thinksns.sociax.t4.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendWeibo;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.weibo.ActivityCollectedWeibo;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明： 所有微博类的基类，微博类，默认使用的是好友的微博列表，其他列表请重写refreshNew/refreshFooter
 * 修改显示效果请重写getView（例如AdapterRecommendweibolist,AdapterChannelWeiboList）
 * 
 * @author wz
 * @date 2014-10-15
 * @version 1.0
 */
public class AdapterWeiboList extends AdapterSociaxList {
	protected AppendWeibo append;// 数据映射
	protected Api.WeiboApi apiwebo;
	protected boolean clickHead = true;		//是否允许点击头像进入个人主页
	protected int uid;
	protected int contentHeight;

	public FragmentSociax getFragment() {
		return fragment;
	}

	public void setFragment(FragmentSociax fragment) {
		this.fragment = fragment;
	}

	@Override
	public ModelWeibo getFirst() {
		return (ModelWeibo) super.getFirst();
	}

	@Override
	public ModelWeibo getLast() {
		return (ModelWeibo) super.getLast();
	}

	/**
	 * 获取最后一条的id
	 * 
	 * @return
	 */
	public int getMaxid() {
		if (getLast() == null)
			return 0;
		else
			return getLast().getWeiboId();
	}

	@Override
	public ModelWeibo getItem(int position) {
		return (ModelWeibo) super.getItem(position);
	}

	/**
	 * 从Activity生成的微博adapter
	 * 
	 * @param context
	 * @param list
	 */
	public AdapterWeiboList(ThinksnsAbscractActivity context,
							ListData<SociaxItem> list, int uid) {
		super(context, list);
		append = new AppendWeibo(context, this);
		this.uid = uid;
	}

	/**
	 * 从fragment生成的微博adapter
	 * 
	 * @param fragment
	 * @param list
	 */
	public AdapterWeiboList(FragmentSociax fragment, ListData<SociaxItem> list, int uid) {
		super(fragment, list);
		append = new AppendWeibo(context, this);
		this.setFragment(fragment);
		this.uid = uid;
	}

	@Override
	public int getItemViewType(int position) {
		if(list == null || list.size() == 0) {
			if (adapterState == AdapterSociaxList.NO_MORE_DATA) {
				return 0;
			}else if(adapterState == AdapterSociaxList.STATE_LOADING) {
				return 2;
			}
		}
		return 1;

	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getCount() {
		if(list == null || list.size() == 0) {
			if(adapterState == AdapterSociaxList.NO_MORE_DATA) {
				//正在加载或加载结束
				return 1;
			}else if(adapterState == AdapterSociaxList.STATE_LOADING) {
				return 1;
			}
			return 0;
		}else {
			return list.size();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		holder = null;
		if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
			if(type == 1) {
				convertView = inflater.inflate(R.layout.listitem_weibo_nobackground, null);
				holder = append.initHolder(convertView);
				convertView.setTag(R.id.tag_viewholder, holder);
			}else if(type == 0){
				if (context instanceof ActivityCollectedWeibo) {
					convertView = inflater.inflate(R.layout.default_collect_bg, null);
				} else {
					convertView = inflater.inflate(R.layout.default_personal_share_bg, null);
				}

				holder = new HolderSociax();
				holder.tv_empty_content = (TextView)convertView.findViewById(R.id.tv_empty_content);
				if(contentHeight == 0) {
					contentHeight = UnitSociax.getWindowHeight(parent.getContext())/2;
				}
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(UnitSociax.getWindowWidth(parent.getContext()),
						contentHeight);
				convertView.setLayoutParams(params);
			}else if(type == 2) {
				//加载正在加载数据的界面
				convertView = new EmptyLayout(parent.getContext());
				ListView listView = (ListView)parent;
				int width = listView.getWidth();
				int height = listView.getHeight();
				int count = listView.getHeaderViewsCount();
				int headerH = 0;
				for(int i= 0; i < count; i++) {
					headerH += listView.getChildAt(i).getBottom();
				}

				height -= headerH;
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
				convertView.setLayoutParams(params);

			}


		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		if(type == 1) {
			ModelWeibo modelWeibo = getItem(position);
			convertView.setTag(R.id.tag_weibo, modelWeibo);
			append.appendWeiboItemDataWithNoBackGround(position, holder, modelWeibo);
			if (!clickHead) {
				//可以设置点击自己头像不进入个人主页
				holder.iv_weibo_user_head.setEnabled(false);
			} else {
				holder.iv_weibo_user_head.setEnabled(true);
			}
		}else if(type == 0) {
			//错误或空页面
			if (!(context instanceof ActivityCollectedWeibo)) {
				if (uid == Thinksns.getMy().getUid()) {
					holder.tv_empty_content.setText("没有任何内容");
				} else if (uid != -1) {
					holder.tv_empty_content.setText("没有任何内容");
				} else {
					holder.tv_empty_content.setText("没有任何内容");
				}
			}
		}else {
			((EmptyLayout)convertView).setErrorType(EmptyLayout.NETWORK_LOADING);
		}

		return convertView;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			return getApiWeibo().friendsTimeline(PAGE_COUNT, 0, httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != completeListener) {
				completeListener.onRefreshComplete();
			}
		}
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		try {
			//如果网络连接错误，获取本地数据
			if (!UnitSociax.isNetWorkON(context)) {
				return DbHelperManager.getInstance(context, ListData.DataType.FRIENDS_WEIBO).getFooterData(10, getMaxid());
			}
			return getApiWeibo().friendsTimeline(PAGE_COUNT, getMaxid(), httpListener);
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != completeListener) {
				completeListener.onRefreshComplete();
			}
		}
	}

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return refreshNew(PAGE_COUNT);
	}
	
	/**
	 * 获取api
	 * 
	 * @return
	 */
	protected Api.WeiboApi getApiWeibo() {
		return thread.getApp().getWeiboApi();
	}

}