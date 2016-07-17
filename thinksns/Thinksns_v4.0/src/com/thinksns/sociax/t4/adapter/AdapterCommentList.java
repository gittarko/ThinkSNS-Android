package com.thinksns.sociax.t4.adapter;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendComment;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明： 某条微博的评论列表，
 * 区分评论的微博列表AdapterCommentWeiboList
 * 
 * @author wz
 * @date 2014-11-24
 * @version 1.0
 */
public class AdapterCommentList extends AdapterSociaxList {
	protected  int feed_id;
	protected AppendComment append;
	private String type = "post";		//评论请求默认是帖子

	public AdapterCommentList(ThinksnsAbscractActivity context,
							  ListData<SociaxItem> list, int feed_id) {
		super(context, list);
		this.feed_id = feed_id;
		this.append = new AppendComment(context);
		isHideFootToast = true;
	}

	public AdapterCommentList(FragmentSociax fragment, ListData<SociaxItem> list, int feed_id) {
		super(fragment, list);
		this.feed_id = feed_id;
		this.append = new AppendComment(context);
		isHideFootToast=true;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setFeedId(int feed_id) {
		this.feed_id = feed_id;
	}

	public void setData(ListData<SociaxItem> list) {
		this.clear();
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}

	public void addData(ListData<SociaxItem> list) {
		if(list != null && list.size() > 0) {
			this.list.addAll(list);
			notifyDataSetChanged();
		}
	}

	public void addItem(int index, ModelComment comment) {
		if(list != null) {
			this.list.add(index, comment);
			notifyDataSetChanged();
		}
	}

	public void setItem(int index, ModelComment comment) {
		if(list != null) {
			this.list.set(index, comment);
			notifyDataSetChanged();
		}
	}

	public void removeItem(ModelComment comment) {
		if(list != null) {
			this.list.remove(comment);
			notifyDataSetChanged();
		}
	}

	public void removeItem(int i) {
		if(list != null) {
			this.list.remove(i);
			notifyDataSetChanged();
		}
	}

	public ListData<SociaxItem> getData() {
		return this.list;
	}

	public void clear() {
		if(this.list != null)
			this.list.clear();
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
		if(list == null
				|| list.size() == 0) {
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
	public View getView(final int position, View convertView, final ViewGroup parent) {
		HolderSociax holder = null;
		int type = getItemViewType(position);
		if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
			if (type == 1) {
				convertView = inflater.inflate(R.layout.listitem_comment_list, null);
				holder = append.initHolder(convertView);
			} else if (type == 0 || type == 2) {
				convertView = new EmptyLayout(parent.getContext());
				ListView listView = (ListView)parent;
				int width = listView.getWidth();
				int height = listView.getHeight();
				int count = listView.getHeaderViewsCount();
				int headerH = 0;
				for(int i= 0; i < count; i++) {
					if(listView.getChildAt(i) != null)
						headerH += listView.getChildAt(i).getBottom();
				}

				height -= headerH;
				int minHeight = UnitSociax.dip2px(parent.getContext(), 100);
				if(height < minHeight) {
					//设置最小高度
					height = minHeight;
				}

				AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
				convertView.setLayoutParams(params);
				((EmptyLayout)convertView).setNoDataContent(parent.getContext().getResources().getString(R.string.empty_user_comment));
			}

			convertView.setTag(R.id.tag_viewholder, holder);

		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		if (type == 1) {
			ModelComment comment = (ModelComment)getItem(position);
			if(comment.getComment_id() > 0) {
				holder.ll_empty.setVisibility(View.GONE);
				holder.ll_content.setVisibility(View.VISIBLE);
				convertView.setTag(R.id.tag_object, comment);
				holder.tv_comment_content.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((ListView) parent).performItemClick(v, position, position);
					}
				});
				append.appendCommentData(holder, comment);
			}else {
				holder.ll_content.setVisibility(View.GONE);
				holder.ll_empty.setVisibility(View.VISIBLE);
			}
		}else if(type == 2) {
			//加载中内容
			((EmptyLayout)convertView).setErrorType(EmptyLayout.NETWORK_LOADING);
		}else {
			((EmptyLayout)convertView).setErrorType(EmptyLayout.NODATA);
		}

		return convertView;
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
			if(type.equals("post")) {
				return getApi().getPostCommentList(PAGE_COUNT, feed_id, getMaxid(), httpListener);
			}else {
				return new Api.WeiboApi().getWeioComments(feed_id, getMaxid(), PAGE_COUNT, httpListener);
			}
		} catch (ExceptionIllegalParameter e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException{
			try {
				if(type.equals("post")) {
					return getApi().getPostCommentList(PAGE_COUNT, feed_id, 0, httpListener);
				}else {
					return new Api.WeiboApi().getWeioComments(feed_id, 0, PAGE_COUNT, httpListener);
				}
			} catch (ExceptionIllegalParameter e) {
				e.printStackTrace();
			}

			return null;
	}

	protected Api.WeibaApi getApi() {
		Thinksns app = thread.getApp();
		return app.getWeibaApi();
	}

	@Override
	public int getMaxid() {
		return getLast() ==null ? 0:((ModelComment)getLast()).getComment_id();
	}
}
