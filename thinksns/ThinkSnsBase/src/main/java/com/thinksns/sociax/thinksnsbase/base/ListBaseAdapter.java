package com.thinksns.sociax.thinksnsbase.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thinksns.sociax.thinksnsbase.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

/** 
 * 类说明：  数据列表适配器基类
 * @author  dong.he    
 * @date    2015-9-1
 * @version 1.0
 */
public class ListBaseAdapter<T extends SociaxItem> extends BaseAdapter {
	public static final int STATE_EMPTY_ITEM = 0;
	//还有更多
	public static final int STATE_LOAD_MORE = 1;
	//没有更多
	public static final int STATE_NO_MORE = 2;
	//没有任何内容
	public static final int STATE_NO_DATA = 3;
	//少于一页内容
	public static final int STATE_LESS_ONE_PAGE = 4;
	//加载错误
	public static final int STATE_NETWORK_ERROR = 5;
	public static final int STATE_OTHER = 6;
	//没有最新
	public static final int STATE_NO_NEW = 7;
	//有最新内容
	public static final int STATE_NEW_MORE = 8;

	protected int state = STATE_LOAD_MORE;

	protected String _loadmoreText;
	protected String _loadFinishText;
	protected String _noDateText;
	protected int mScreenWidth;
	protected boolean hasFooter = true;

	private LayoutInflater mInflater;
	protected Context mContext;

	protected LayoutInflater getLayoutInflater(Context context) {
		if (mInflater == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		return mInflater;
	}

	public void setScreenWidth(int width) {
		mScreenWidth = width;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return this.state;
	}

	public int getMaxId() {
		return 0;
	}

	protected ListData<T> mDatas = new ListData<T>();


	public ListBaseAdapter(Context context) {
		//初始化数据加载完毕的文字提示
		_loadFinishText = "已加载完成";
		_loadmoreText = "";
//		_loadmoreText = "正在获取内容";
		_noDateText = "没有内容了";

		this.mContext = context;
	}

	@Override
	public int getCount() {
		switch (getState()) {
			case STATE_EMPTY_ITEM:
				return getDataSizePlus1();
			case STATE_NETWORK_ERROR:
			case STATE_LOAD_MORE:
				return getDataSizePlus1();
			case STATE_NO_DATA:
				return 1;
			case STATE_NO_MORE:
				return getDataSizePlus1();
			case STATE_LESS_ONE_PAGE:
				return getDataSizePlus1();
			default:
				break;
		}
		return getDataSize();
	}

	public int getDataSizePlus1(){
		if(hasFooterView()){
			return getDataSize() + 1;
		}
		return getDataSize();
	}

	public int getDataSize() {
		return mDatas.size();
	}

	@Override
	public T getItem(int arg0) {
		if (mDatas.size() > arg0) {
			return (T)mDatas.get(arg0);
		}
		return null;
	}

	protected T getLast() {
		if(mDatas.size() > 0)
			return (T)mDatas.get(mDatas.size() - 1);
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setData(ListData<T> data) {
		mDatas = data;
		notifyDataSetChanged();
	}

	public ListData<T> getData() {
		return mDatas == null ? (mDatas = new ListData<T>()) : mDatas;
	}

	public void addData(ListData<T> data) {
		if (mDatas != null && data != null && !data.isEmpty()) {
			mDatas.addAll(data);
		}
		notifyDataSetChanged();
	}

	public void addData(ListData<T> data, int i) {
		if (mDatas != null && data != null && !data.isEmpty()) {
			mDatas.addAll(i, data);
		}
		notifyDataSetChanged();
	}

	public void addItem(T obj) {
		if (mDatas != null) {
			mDatas.add(obj);
		}
		notifyDataSetChanged();
	}

	public void addItem(int pos, T obj) {
		if (mDatas != null) {
			mDatas.add(pos, obj);
		}
		notifyDataSetChanged();
	}

	public void removeItem(Object obj) {
		mDatas.remove(obj);
		notifyDataSetChanged();
	}

	public void setItem(int i, T obj) {
		if(mDatas != null)
			mDatas.set(i, obj);
	}


	public int getItemForPosition(T obj) {
		int index = -1;
		for(int i=0; i<mDatas.size(); i++) {
			if(mDatas.get(i).equals(obj))
				index = i;
		}

		return index;
	}

	public void clear() {
		mDatas.clear();
		notifyDataSetChanged();
	}

	public void setLoadmoreText(String loadmoreText) {
		_loadmoreText = loadmoreText;
	}

	public void setLoadFinishText(String loadFinishText) {
		_loadFinishText = loadFinishText;
	}

	public void setNoDataText(String noDataText) {
		_noDateText = noDataText;
	}

	protected boolean loadMoreHasBg() {
		return true;
	}

	//是否设置列表底部视图,默认是拥有的
	protected boolean hasFooterView(){
		return hasFooter;
	}

	public void hideFooterView() {
		hasFooter = false;
	}

	public void showFooterView() {
		hasFooter = true;
	}

	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		if((position == getCount() - 1) && hasFooterView()) {
			//列表最后
			this.mFooterView = (LinearLayout) LayoutInflater.from(
					parent.getContext()).inflate(R.layout.listitem_footer_view,
					null);
			if (!loadMoreHasBg()) {
				mFooterView.setBackgroundDrawable(null);
			}

			if(mDatas.size() == 0)
				this.state = STATE_NO_DATA;

			ProgressBar progress = (ProgressBar) mFooterView
						.findViewById(R.id.progressbar);
			TextView text = (TextView) mFooterView.findViewById(R.id.text);
			switch (getState()) {
				case STATE_LOAD_MORE:
					setFooterViewLoading();
					break;
				case STATE_NO_MORE:
					mFooterView.setVisibility(View.VISIBLE);
					progress.setVisibility(View.GONE);
					text.setVisibility(View.VISIBLE);
					text.setText("没有更多内容了");
					break;
				case STATE_EMPTY_ITEM:
					progress.setVisibility(View.GONE);
					mFooterView.setVisibility(View.VISIBLE);
					text.setText(_noDateText);
					break;
				case STATE_NETWORK_ERROR:
					mFooterView.setVisibility(View.VISIBLE);
					progress.setVisibility(View.GONE);
					text.setVisibility(View.VISIBLE);
					if (UnitSociax.isNetWorkON(mContext)) {
						text.setText("加载出错了");
					} else {
						text.setText("没有可用的网络");
					}
					break;
				default:
					progress.setVisibility(View.GONE);
					mFooterView.setVisibility(View.GONE);
					text.setVisibility(View.GONE);
					break;
			}

			return mFooterView;
		}

		if(position < 0)
			position = 0;

		return getRealView(position, converView, parent);

	}

	protected View getRealView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	private LinearLayout mFooterView;

	public View getFooterView() {
		return this.mFooterView;
	}

	public void setFooterViewLoading(String loadMsg) {
		ProgressBar progress = (ProgressBar) mFooterView
				.findViewById(R.id.progressbar);
		TextView text = (TextView) mFooterView.findViewById(R.id.text);
		mFooterView.setVisibility(View.VISIBLE);
		progress.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		if (loadMsg == null || loadMsg.isEmpty()) {
			text.setText(_loadmoreText);
		} else {
			text.setText(loadMsg);
		}
	}

	public void setFooterViewLoading() {
		setFooterViewLoading("");
	}

	public void setFooterViewText(String msg) {
		ProgressBar progress = (ProgressBar) mFooterView
				.findViewById(R.id.progressbar);
		TextView text = (TextView) mFooterView.findViewById(R.id.text);
		mFooterView.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
		text.setVisibility(View.VISIBLE);
		text.setText(msg);
	}

}
