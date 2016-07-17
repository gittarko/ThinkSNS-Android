package com.thinksns.sociax.api;

import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;

/**
 * 类说明： 频道接口
 * 
 * @author povol
 * @date Dec 5, 2012
 * @version 1.0
 */
public interface ApiChannel {

	public static String MOD_NAME = "Channel"; // MOD 名称
	// act 名称
	public static String GET_ALL_CHANNEL = "get_all_channel"; // 获取频道列表
	public static String GET_CHANNEL_FEED = "get_channel_feed"; // 获取频道微博
	public static String GET_CHANNEL_DETAIL = "channel_detail";
	public static String CHANNEL_FOLLOW = "channel_follow";// 频道关注
	public static String GET_USER_CHANNEL = "get_user_channel";

	/**
	 * 获取频道列表，第一次请求maxid=0，后面maid=频道id；count为每次请求数目
	 * 
	 * @return
	 *
	 */
	public ListData<SociaxItem> getAllChannel(int count, int maxid, HttpResponseListener listener)
			throws ApiException;

	public ListData<SociaxItem> getChannelFeed(int channelId, int page)
			throws ApiException;

	public ListData<SociaxItem> getChannelHeaderFeed(ModelWeibo weibo,
													 int channelId, int page) throws ApiException;

	public ListData<SociaxItem> getChannelFooterFeed(ModelWeibo weibo,
			int channelId, int page) throws ApiException;

	/**
	 * 获取某个频道的微博列表
	 * 
	 * @param channelId
	 *            ：channel_category_id 频道id
	 * @return
	 * @throws ApiException
	 * @throws ExceptionIllegalParameter
	 */
//	public ListData<SociaxItem> getChannelWeibo(String channelId, int max_id)
//			throws ApiException, ExceptionIllegalParameter;

	public ListData<SociaxItem> getChannelWeibo(String channelId, int max_id, int count, int type, final HttpResponseListener listener)
			throws ApiException, ExceptionIllegalParameter;
	/**
	 * 获取某个频道微博列表的更多数据
	 * 
	 * @param channel_id
	 *            频道id
	 * @param last
	 *            最后一条微博
	 * @return
	 * @throws ApiException
	 */
	public ListData<SociaxItem> getChannelFooter(int channel_id, ModelWeibo last)
			throws ApiException;
	/**
	 * 获取我的频道列表
	 * @param pageCount 读取条数
	 * @param maxid 最后一条id
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> getUserChannel(int pageCount, int maxid, HttpResponseListener listener)
			throws ApiException;

}
