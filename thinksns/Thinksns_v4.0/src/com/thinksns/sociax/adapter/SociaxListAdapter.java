package com.thinksns.sociax.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.thinksns.sociax.api.ApiStatuses;
import com.thinksns.sociax.api.ApiUsers;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.gimgutil.ImageFetcher;
import com.thinksns.sociax.gimgutil.ImageCache.ImageCacheParams;
import com.thinksns.sociax.modle.Posts;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

public abstract class SociaxListAdapter extends BaseAdapter {
	protected static final String TAG = "SociaxListAdapter";
	protected ListData<SociaxItem> list;// 当前的列表，后期要修改成SociaxData对应的列表
	protected ThinksnsAbscractActivity context;
	protected LayoutInflater inflater;

	public static final int LIST_FIRST_POSITION = 0;
	protected static View refresh;
	protected static Worker thread;
	protected ActivityHandler handler;
	protected ResultHandler resultHander;

	protected static String Type;
	public static final int REFRESH_HEADER = 0;// 头部刷新
	public static final int REFRESH_FOOTER = 1;// 脚部刷新
	public static final int REFRESH_NEW = 2;// 第一次刷新
	public static final int SEARCH_NEW = 3;// 第一次搜索
	public static final int UPDATA_LIST = 4;// 更新列表
	public static final int UPDATA_LIST_ID = 5;
	public static final int UPDATA_LIST_TYPE = 6;
	public static final int SEARCH_NEW_BY_ID = 7;
	public static final int PAGE_COUNT = 20;// 每次refresh更新的数目

	private static LoadingView loadingView;

	public static final int FAV_STATE = 8;

	protected boolean isSelectButtom;

	public boolean hasRefreshFootData;
	public boolean isHideFootToast = false;
	public boolean isShowToast = true; // 是否显示提示
	public boolean isCleanAllData = false; // 是否清楚list中所有数据

	public int lastNum;
	public String isRefreshActivity;
	public ImageView animView;

	public ImageFetcher mHeadImageFetcher, mContentImageFetcher;

	private static final String IMAGE_CACHE_DIR = "thumbs";
	private static final String CONTET_IMAGE_CACHE_DIR = "cthumbs";

	public SociaxListAdapter(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list) {
		this.list = list;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		try {
			refresh = this.context.getCustomTitle().getRight();
		} catch (Exception e) {
			Log.d(TAG,
					"sociaxlistadapter construct method get rigth res of custom title error "
							+ e.toString());
		}
		SociaxListAdapter.thread = new Worker(
				(Thinksns) context.getApplicationContext(), Type + " Refresh");
		handler = new ActivityHandler(thread.getLooper(), context);
		resultHander = new ResultHandler();
	}

	/**
	 * List列表头部刷新调用的接口
	 * 
	 * @param obj
	 * @return
	 * @throws VerifyErrorException
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 */
	public abstract ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException;

	/**
	 * List列表更多刷新调用的接口
	 * 
	 * @param obj
	 * @return
	 * @throws VerifyErrorException
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 */
	public abstract ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException;

	/**
	 * List列表刷新调用的接口
	 * 
	 * @param count
	 * @return
	 * @throws VerifyErrorException
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 */
	public abstract ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException;

	/**
	 * 执行头部刷新
	 */
	public void doRefreshHeader() {
		// 首先判断网络状态
		if (!UnitSociax.isNetWorkON(context)) {
			Toast.makeText(context, context.getResources().getText(R.string.net_fail), Toast.LENGTH_SHORT).show();
			context.getListView().headerHiden();
			return;
		}
		if (context.getListView() != null) {

			context.getListView().headerRefresh();
			context.getListView().headerShow();
		}
		Message msg = handler.obtainMessage();
		if (this.getFirst() == null) {
			// 如果第一条信息为空，则调用新刷新
			msg.what = REFRESH_NEW;
		} else {
			// 否则获取第一条微博，刷新头部的时候需要用到第一条微博的id作为分界线
			msg.obj = this.getFirst();
			msg.what = REFRESH_HEADER;
		}
		Log.d(TAG, "doRefreshHeader .....");
		handler.sendMessage(msg);
	}

	/**
	 * 头部追加信息
	 * 
	 * @param list
	 */
	public void addHeader(ListData<SociaxItem> list) {
		if (null != list) {
			if (list.size() == 20) {
				this.list.clear();
				this.list.addAll(list);
				// 修改适配器绑定的数组后
				this.notifyDataSetChanged();
				Toast.makeText(context,
						com.thinksns.sociax.android.R.string.refresh_success,
						Toast.LENGTH_SHORT).show();
			} else if (list.size() == 0) {
				if (this.list.size() > 20) {
					ListData<SociaxItem> tempList = new ListData<SociaxItem>();
					for (int i = 0; i < 20 - list.size(); i++) {
						tempList.add(this.list.get(i));
					}
					this.list.clear();
					this.list.addAll(tempList);
				}
				Toast.makeText(context,
						com.thinksns.sociax.android.R.string.refresh_error,
						Toast.LENGTH_SHORT).show();
			} else {
				ListData<SociaxItem> tempList = new ListData<SociaxItem>();
				for (int i = 0; i < 20 - list.size() && i < this.list.size(); i++) {
					tempList.add(this.list.get(i));
				}
				this.list.clear();
				for (int i = 1; i <= list.size(); i++) {
					this.list.add(0, list.get(list.size() - i));
				}
				this.list.addAll(this.list.size(), tempList);
				// 修改适配器绑定的数组后
				this.notifyDataSetChanged();
				Toast.makeText(context,
						com.thinksns.sociax.android.R.string.refresh_success,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public ListData<SociaxItem> searchNew(String key) throws ApiException {
		return null;
	};

	public ListData<SociaxItem> searchNew(int key) throws ApiException {
		return null;
	};

	public ListData<SociaxItem> refreshNew(int count, String key)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return null;
	}

	public ListData<SociaxItem> refreshNew(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return null;
	}

	public Object refresState(int key) throws ApiException {
		return null;
	}

	public Context getContext() {
		return this.context;
	}

	public SociaxItem getFirst() {
		return this.list.size() == 0 ? null : this.list
				.get(LIST_FIRST_POSITION);
	}

	public SociaxItem getLast() {
		return this.list.get(this.list.size() - 1);
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public SociaxItem getItem(int position) {
		return this.list.get(position);
	}

	/**
	 * 删除Item
	 * 
	 * @param position
	 */
	public void deleteItem(int position) {
		if (list.size() > 0)
			this.list.remove(position - 1);
		this.notifyDataSetChanged();
	}

	/**
	 * 底部追加信息
	 * 
	 * @param list
	 */
	public void addFooter(ListData<SociaxItem> list) {
		if (null != list) {
			if (list.size() > 0) {
				hasRefreshFootData = true;
				this.list.addAll(list);
				lastNum = this.list.size();
				this.notifyDataSetChanged();
			}
		}
		if (list == null || list.size() == 0 || list.size() < 20) {
			context.getListView().hideFooterView();
		}
		if (this.list.size() == 0 && isShowToast) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 修改列表数据
	 * 
	 * @param list
	 */
	public void changeListData(ListData<SociaxItem> list) {
		if (null != list) {
			if (list.size() > 0) {
				hasRefreshFootData = true;
				this.list.clear();
				this.list.addAll(list);
				lastNum = this.list.size();
				this.notifyDataSetChanged();

			}
		}
		if (list == null || list.size() == 0 || list.size() < 20) {
			context.getListView().hideFooterView();
		}
		if (this.list.size() == 0 && isShowToast) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void changeListDataNew(ListData<SociaxItem> list) {
		if (null != list) {
			if (list.size() > 0) {
				hasRefreshFootData = true;
				this.list = list;
				lastNum = this.list.size();
				this.notifyDataSetChanged();

			} else {
				this.list.clear();
				this.notifyDataSetChanged();
			}
		}

		if (list == null || list.size() == 0 || list.size() < 20) {
			context.getListView().hideFooterView();
		}

		if (this.list.size() == 0) {
			Toast.makeText(context,
					com.thinksns.sociax.android.R.string.refresh_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void doRefreshFooter() {
		if (!UnitSociax.isNetWorkON(context)) {
			Toast.makeText(context, context.getResources().getText(R.string.net_fail), Toast.LENGTH_SHORT).show();
			return;
		}

		SociaxListAdapter.thread = new Worker(
				(Thinksns) context.getApplicationContext(), Type + " Refresh");
		handler = new ActivityHandler(thread.getLooper(), context);
		resultHander = new ResultHandler();
		context.getListView().footerShow();
		if (refresh != null) {
			// Anim.refreshMiddle(context, refresh);
			refresh.setClickable(false);
		}
		if (this.list.size() == 0) {
			return;
		}
		Log.d(TAG, "doRefreshFooter");
		Message msg = handler.obtainMessage();
		msg.obj = this.getLast();
		msg.what = REFRESH_FOOTER;
		handler.sendMessage(msg);
	}

	protected void cacheHeaderPageCount() {
		ListData<SociaxItem> cache = new ListData<SociaxItem>();
		for (int i = 0; i < PAGE_COUNT; i++) {
			cache.add(0, this.list.get(i));
		}
		Thinksns.setLastWeiboList(cache);
	}

	public void refreshNewWeiboList() {
		if (refresh != null) {
			// 设置加载适配器的时候头部右边的动画
			// Anim.refreshMiddle(context, refresh);
			if (null != isRefreshActivity
					&& isRefreshActivity.equals("ThinksnsMyWeibo"))
				Anim.refresh(
						context,
						refresh,
						com.thinksns.sociax.android.R.drawable.spinner_black);
			refresh.setClickable(false);
		}
		Message msg = handler.obtainMessage();
		msg.what = REFRESH_NEW;
		handler.sendMessage(msg);
	}

	public void doUpdataList() {
		Message msg = handler.obtainMessage();
		msg.what = UPDATA_LIST;
		handler.sendMessage(msg);
	}

	public void doUpdataList(String type) {
		if (type.equals("taskCate")) {

			loadingView = (LoadingView) context.findViewById(LoadingView.ID);
			if (loadingView != null)
				loadingView.show((View) context.getListView());
			if (context.getOtherView() != null) {
				loadingView.show(context.getOtherView());
			}
		}
		Message msg = handler.obtainMessage();
		msg.what = UPDATA_LIST;
		handler.sendMessage(msg);
	}

	public void doUpdataListById() {
		Message msg = handler.obtainMessage();
		msg.what = UPDATA_LIST_ID;
		handler.sendMessage(msg);
	}

	public void doUpdataListByType(SociaxItem sociaxItem) {

		loadingView = (LoadingView) context.findViewById(LoadingView.ID);
		if (loadingView != null)
			loadingView.show((View) context.getListView());
		if (context.getOtherView() != null) {
			loadingView.show(context.getOtherView());
		}

		Message msg = handler.obtainMessage();
		msg.obj = sociaxItem;
		msg.what = UPDATA_LIST_ID;
		handler.sendMessage(msg);
	}

	public void doSearchNew(String key) {
		Message msg = handler.obtainMessage();
		msg.what = SEARCH_NEW;
		msg.obj = key;
		handler.sendMessage(msg);
	}

	public void doSearchNewById(int key) {
		Message msg = handler.obtainMessage();
		msg.what = SEARCH_NEW_BY_ID;
		msg.arg1 = key;
		handler.sendMessage(msg);
	}

	public void updateState(int key) {
		Message msg = handler.obtainMessage();
		msg.what = FAV_STATE;
		msg.arg1 = key;
		handler.sendMessage(msg);
	}

	private class ActivityHandler extends Handler {
		private Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ListData<SociaxItem> newData = null;
			Message mainMsg = new Message();
			mainMsg.what = ResultHandler.ERROR;
			Log.d(TAG, "mainMsg.what=" + mainMsg.what);
			try {
				Log.v("SociaxListAdapter-->AdtivityHandler", "msg.what="
						+ msg.what);
				switch (msg.what) {
				case REFRESH_HEADER:
					newData = refreshHeader((SociaxItem) msg.obj);
					Log.v("SociaxListAdapter", newData.toString() + "XXXX");
					Log.d(TAG, "refresh header ....");
					break;
				case REFRESH_FOOTER:
					newData = refreshFooter((SociaxItem) msg.obj);
					Log.d(TAG, "refresh footer ....");
					break;
				case REFRESH_NEW:
					Log.d(TAG, "refresh new  ....");
					newData = refreshNew(PAGE_COUNT);
					break;
				case SEARCH_NEW:
					Log.d(TAG, "seache new  ....");
					newData = searchNew((String) msg.obj);
					break;
				case SEARCH_NEW_BY_ID:
					Log.d(TAG, "seache new  ....");
					newData = searchNew(msg.arg1);
					break;
				case UPDATA_LIST:
					Log.d(TAG, "updata list  ....");
					newData = refreshNew(PAGE_COUNT);
					break;
				case UPDATA_LIST_ID:
					Log.d(TAG, "updata list  ....");
					newData = refreshNew((SociaxItem) msg.obj);
					break;
				case UPDATA_LIST_TYPE:
					Log.d(TAG, "updata list  ....");
					newData = refreshNew((SociaxItem) msg.obj);
					break;
				case FAV_STATE:
					mainMsg.arg2 = ((Posts) (refresState(mId))).getFavorite();
					break;
				}
				mainMsg.what = ResultHandler.SUCCESS;
				mainMsg.obj = newData;
				mainMsg.arg1 = msg.what;
			} catch (VerifyErrorException e) {
				Log.d("SociaxListAdapter class ", e.toString());
				mainMsg.obj = e.getMessage();
			} catch (ApiException e) {
				Log.d("SociaxListAdapter class ", e.toString());
				mainMsg.what = 2;
				mainMsg.obj = e.getMessage();
			} catch (ListAreEmptyException e) {
				Log.d("SociaxListAdapter class ", e.toString());
				mainMsg.obj = e.getMessage();
			} catch (DataInvalidException e) {
				Log.d("SociaxListAdapter class ", e.toString());
				mainMsg.obj = e.getMessage();
			}
			Log.v("SociaxListAdapter-->msg.info", mainMsg.toString());
			resultHander.sendMessage(mainMsg);
		}
	}

	@SuppressLint("HandlerLeak")
	private class ResultHandler extends Handler {
		private static final int SUCCESS = 0;
		private static final int ERROR = 1;

		public ResultHandler() {
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (context.getListView() != null)
				context.getListView()
						.setLastRefresh(System.currentTimeMillis());
			if (msg.what == SUCCESS) {
				Log.v("SociaxListAdapter-->ResultHandler", "1");
				switch (msg.arg1) {
				case REFRESH_NEW:
					Log.v("SociaxListAdapter-->ResultHandler", "2");
					addFooter((ListData<SociaxItem>) msg.obj);
					Log.d(TAG, "refresh new load ....");
					break;
				case REFRESH_HEADER:
					Log.v("SociaxListAdapter-->ResultHandler", "3");
					addHeader((ListData<SociaxItem>) msg.obj);
					context.getListView().headerHiden();
					Log.d(TAG, "refresh header load ....");
					break;
				case REFRESH_FOOTER:
					Log.v("SociaxListAdapter-->ResultHandler", "4");
					addFooter((ListData<SociaxItem>) msg.obj);
					context.getListView().footerHiden();
					Log.d(TAG, "refresh heiden load ....");
					break;
				case SEARCH_NEW:
					changeListDataNew((ListData<SociaxItem>) msg.obj);
					context.getListView().footerHiden();
					Log.d(TAG, "refresh heiden load ....");
					break;
				case SEARCH_NEW_BY_ID:
					changeListDataNew((ListData<SociaxItem>) msg.obj);
					context.getListView().footerHiden();
					Log.d(TAG, "refresh heiden load ....");
					break;
				case UPDATA_LIST:
					changeListData((ListData<SociaxItem>) msg.obj);
					context.getListView().footerHiden();
					Log.d(TAG, "refresh heiden load ....");
					break;
				case UPDATA_LIST_ID:
					changeListDataNew((ListData<SociaxItem>) msg.obj);
					context.getListView().footerHiden();
					Log.d(TAG, "refresh heiden load ....");
					break;
				case UPDATA_LIST_TYPE:
					changeListDataNew((ListData<SociaxItem>) msg.obj);
					context.getListView().footerHiden();
					Log.d(TAG, "refresh heiden load ....");
					break;
				case FAV_STATE:
					context.updateView(mUpdateView, msg.arg2);
					break;
				}
			} else {
				if (!isHideFootToast)
					Toast.makeText(context, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
			}
			if (context.getListView() != null) {
				context.getListView().headerHiden();
				context.getListView().hideFooterView();

			}
			Anim.cleanAnim(animView);

			if (loadingView != null && context.getListView() != null) {
				loadingView.hide((View) context.getListView());
			}
			Log.v("SociaxListAdapter-->ResultHandler", "6");
			if (context.getOtherView() != null) {
				Log.v("SociaxListAdapter-->ResultHandler", "8");
				loadingView.hide(context.getOtherView());
			}
			Log.v("SociaxListAdapter-->ResultHandler", "9");
			if (refresh != null)
				cleanRightButtonAnim(refresh);
		}
	}

	/**
	 * 清除动画
	 * 
	 * @param v
	 */
	protected void cleanRightButtonAnim(View v) {
		v.setClickable(true);
		// v.setBackgroundResource(context.getCustomTitle().getRightResource());
		v.clearAnimation();
	}

	/**
	 * 加载数据
	 */
	public void loadInitData() {
		if (!UnitSociax.isNetWorkON(context)) {
			Toast.makeText(context, R.string.net_fail, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (this.getCount() == 0) {
			ListData<SociaxItem> cache = Thinksns.getLastWeiboList();
			if (cache != null) {
				this.addHeader(cache);
			} else {
				loadingView = (LoadingView) context
						.findViewById(LoadingView.ID);
				if (loadingView != null)
					loadingView.show((View) context.getListView());
				if (context.getOtherView() != null) {
					loadingView.show(context.getOtherView());
				}
				refreshNewWeiboList();
			}
		}
	}

	private int mId;
	private int mState;
	private View mUpdateView;

	public void loadInitData(View updateView, int id, int state) {
		if (!UnitSociax.isNetWorkON(context)) {
			Toast.makeText(context, R.string.net_fail, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mId = id;
		mState = state;
		mUpdateView = updateView;
		if (this.getCount() == 0) {
			ListData<SociaxItem> cache = Thinksns.getLastWeiboList();
			if (cache != null) {
				this.addHeader(cache);
			} else {
				loadingView = (LoadingView) context
						.findViewById(LoadingView.ID);
				if (loadingView != null)
					loadingView.show((View) context.getListView());
				if (context.getOtherView() != null) {
					loadingView.show(context.getOtherView());
				}
				refreshNewWeiboList();
				updateState(mId);
			}
		}
	}

	public int getMyUid() {
		Thinksns app = thread.getApp();
		return Thinksns.getMy().getUid();
	}

	public ApiUsers getApiUsers() {
		Thinksns app = thread.getApp();
		return app.getUsers();
	}

	public int getMySite() {
		Thinksns app = thread.getApp();
		if (Thinksns.getMySite() == null) {
			return 0;
		} else {
			return Thinksns.getMySite().getSite_id();
		}
	}

	public void initHeadImageFetcher() {
		int headImageSize = context.getResources().getDimensionPixelSize(
				R.dimen.header_width_hight);

		ImageCacheParams cacheParams = new ImageCacheParams(context,
				IMAGE_CACHE_DIR);
		// Set memory cache to 25% of mem class
		cacheParams.setMemCacheSizePercent(context, 0.25f);

		mHeadImageFetcher = new ImageFetcher(context, headImageSize);
		mHeadImageFetcher.setLoadingImage(R.drawable.default_user
		);
		mHeadImageFetcher.addImageCache(cacheParams);
		mHeadImageFetcher.setExitTasksEarly(false);
	}

	public void initContentImageFetcher() {
		int contentImageSize = 100;

		ImageCacheParams contentCacheParams = new ImageCacheParams(context,
				CONTET_IMAGE_CACHE_DIR);
		// Set memory cache to 25% of mem class
		contentCacheParams.setMemCacheSizePercent(context, 0.25f);

		mContentImageFetcher = new ImageFetcher(context, contentImageSize);
		mContentImageFetcher.setLoadingImage(R.drawable.bg_loading);
		mContentImageFetcher.addImageCache(contentCacheParams);
		mContentImageFetcher.setExitTasksEarly(false);
	}
}
