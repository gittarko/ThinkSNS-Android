package com.thinksns.sociax.api;

import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

public interface ApiNotifytion {

	static final String GET_NOTIFY_BY_COUNT = "get_notify_by_count"; // 获取各类未读消息数量
	static final String GET_SYSTEM_NOTIFY = "get_system_notify"; // 获取未读系统消息
	static final String GET_MESSAGE_COUNT = "get_message_count"; // 获取未读消息数
	static final String SET_MESSAGE_READ = "set_message_read"; // 设置消息已读
	static final String SET_NOTIFY_READ = "set_notify_read"; // 清空未读消息数

	static final String MOD_NAME = "Notifytion";

	/**
	 * 获取各类未读消息数量
	 * 
	 * @param uid
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> getNotifyByCount(int uid) throws ApiException;

	/**
	 * 获取提醒的系统消息
	 * 
	 * @param uid
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> getSystemNotify(int uid) throws ApiException;

	/**
	 * 获取提醒的消息总数
	 * 
	 * @return
	 * @throws ApiException
	 */
	public int getMessageCount() throws ApiException;

	/**
	 * 清空未读消息数
	 * 
	 * @param type
	 *            消息的类型
	 * @throws ApiException
	 */
	public void setNotifyRead(String type) throws ApiException;

	/**
	 * 设置消息已读
	 * 
	 * @param type
	 *            消息类型
	 * @throws ApiException
	 */
	public void setMessageRead(String type) throws ApiException;
}
