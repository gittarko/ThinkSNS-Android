package com.thinksns.sociax.t4.android.db;

import android.content.Context;

import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：   数据库管理类
 * @author  dong.he
 * @date    2015-8-24
 * @version 1.0
 */
public class DbHelperManager {
	private Context mContext;
	private DbHelper helper = null;
	private ListData.DataType type;

	private DbHelperManager(Context context, ListData.DataType type) {
		this.mContext = context;
		this.type = type;
		init();
	}

	private void init() {
		switch(type) {
		case WEIBO:
		case ALL_WEIBO:
		case FRIENDS_WEIBO:
		case RECOMMEND_WEIBO:
		case ATME_WEIBO:
        case CHANNELS_WEIBO:
			helper = new WeiboDbHelper(mContext, type);
			break;
		default:
			break;
		}
	}

	public static DbHelperManager getInstance(Context context, ListData.DataType type) {
		return new DbHelperManager(context, type);
	}

	/**
	 * 保存一条数据
	 * @param item
	 * @return
	 */
	public long add(SociaxItem item) {
		if(helper != null)
			return helper.saveData(item);
		return 0;
	}
	/**
	 * 删除一条数据
	 * @param item
	 * @return
	 */
	public boolean delete(SociaxItem item){
		if (helper!=null)
			return helper.deleteData(item);
		return false;
	}

	/**
	 * 删除朋友圈中某人的微博
	 * @param item
	 * @return
	 */
	public boolean deleteSomeOne(int uid,int loginUid){
		if (helper!=null)
			return helper.deleteSomeBodyWeibo(uid,loginUid);
		return false;
	}

	/**
	 * 获取列表头部数据
	 * @param count
	 * @return
	 */
	public ListData<SociaxItem> getHeaderData(int count) {
		if(helper != null) {
			return helper.getHeaderList(count);
		}
		return new ListData<SociaxItem>();
	}

	public ListData<SociaxItem> getHeaderDataByUser(int count, int userId) {
		if(helper != null) {
			return helper.getHeaderByUser(count, userId);
		}
		return new ListData<SociaxItem>();
	}

	public ListData<SociaxItem> getFooterDataByUser(int count, int userId) {
		if(helper != null) {
			return helper.getFooterByUser(count, userId);
		}
		return new ListData<SociaxItem>();
	}

	/**
	 * 底部加载更多数据
	 * @param count
	 * @return
	 */
	public ListData<SociaxItem>	 getFooterData(int count, int lastId) {
		if(helper != null) {
			return helper.getFooterList(count, lastId);
		}
		return new ListData<SociaxItem>();
	}
}
