package com.thinksns.sociax.api;

import com.thinksns.sociax.modle.Message;

import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelChatMessage;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * @author Administrator
 * 
 */
public interface ApiMessage {
	public static final String MOD_NAME = "Message";
	public static final String INBOX = "inbox";
	public static final String OUTBOX = "outbox";
	public static final String BOX = "box"; // 聊天列表
	public static final String SHOW = "show"; //
	public static final String CREATE = "create";
	public static final String REPLY = "reply";
	public static final String DESTROY = "destroy";
	public static final String UPLPAD_VOICE = "upload_voice";
	public static final String UPLPAD_IMAGE = "upload_image";
	public static final String UNREAD_COUNT = "unreadcount";
	public static final String CREATE_LIST = "createList";
	public static final String CREATE_CHAT_LIST = "get_listid_by_uid";
	public static final String CAN_SEND_MESSAGE = "can_send_message";
	public static final String GET_CHAT_INFO = "get_list_info";
	public static final String GET_CHAT_LIST = "get_message_list";
	public static final String SEND_MESSAGE = "send_message";
	public static final String GET_CHAT = "get_msg"; // qcj添加 獲取聊天內容
	public static final String CLEAN_MESSAGE = "clear_message"; // qcj添加 清除聊天记录
	public static final String CLEAN_UNREAD = "clear_unread"; // qcj添加 未读消息清零
	public static final String GET_THELASTMESSAGE = "get_chat"; // qcj添加
																// 获取最近的一条的聊天记录
	public static final String GET_UNREAD_MESSAGE = "get_unread"; // caoligai
																	// 获取未读消息
	public static final String SEND_IMG_MESSAGE = "uploadImage";//上传图片地址
	public static final String SEND_VOICE_MESSAGE = "uploadVoice";//上传语音地址
	public static final String GET_ATTACH_MESSAGE = "getAttach";//获取附件地址
	public static final String GET_USERFACE = "getUserFace";//获取用户头像地址
	public static final String GET_USERINFO = "getUserInfo";//获取名片用户详情

	/**
	 * 获取最新的一条聊天记录
	 * 
	 * @param listid
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	SociaxItem getLastMessage(int listid) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	/**
	 * 清除未读聊天内容 qcj添加 对应的是 CLEAN_UNREAD = "clear_unread"
	 * 
	 * @param listid
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	boolean clearUnreadMessage(int listid) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	/**
	 * 清除聊天内容 qcj添加 对应的是 CLEAN_MESSAGE = "clear_message";
	 * 
	 * @param listid
	 * @return
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	boolean clearMessage(int listid) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	// 获取聊天内容 qcj添加 对应的是 GET_CHAT = "get_msg";
	ListData<SociaxItem> getChat(int listid) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;
	
	// 获取聊天内容 qcj添加 对应的是 GET_CHAT = "get_msg";
		ListData<SociaxItem> getChat(int listid,int message_id) throws ApiException,
				ListAreEmptyException, DataInvalidException, VerifyErrorException;

	ListData<SociaxItem> inbox(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	ListData<SociaxItem> getChatList(int count, int maxId) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	ListData<SociaxItem> inboxHeader(Message message, int count, int page)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	ListData<SociaxItem> inboxFooter(Message message, int count, int page)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	ListData<SociaxItem> outbox(Message message, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	ListData<SociaxItem> outboxHeader(Message message, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	ListData<SociaxItem> outboxFooter(Message message, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	Message show(Message message) throws ApiException, DataInvalidException,
			VerifyErrorException;

	void show(Message message, ListData<SociaxItem> list) throws ApiException,
			DataInvalidException, VerifyErrorException;

	int[] create(Message message) throws ApiException, DataInvalidException,
			VerifyErrorException;

	boolean reply(Message message) throws ApiException, DataInvalidException,
			VerifyErrorException;

	boolean createNew(Message message) throws ApiException,
			DataInvalidException, VerifyErrorException;

	/**
	 * 获取未读消息
	 * 
	 * @return
	 * @throws ApiException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	SociaxItem getUnreadCount() throws ApiException, DataInvalidException,
			VerifyErrorException;

	/**
	 * 创建聊天
	 * 
	 * @param from_uid
	 *            聊天发起人，号分隔
	 * @param member
	 *            聊天成员uid ，号分隔
	 * @param title
	 *            标题
	 * @return
	 * @throws ApiException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	Object createGroupChat(int from_uid, String member, String title)
			throws ApiException, DataInvalidException, VerifyErrorException;

	/**
	 * 获取聊天信息
	 * 
	 * @param room_id
	 *            聊天room
	 * @return
	 * @throws ApiException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	Object getListInfo(int room_id) throws ApiException, DataInvalidException,
			VerifyErrorException;

	/**
	 * 获取聊天信息
	 *
	 * @param uid
	 *            聊天room
	 * @return
	 * @throws ApiException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	Object canSendMessage(int uid) throws ApiException, DataInvalidException,
			VerifyErrorException;

	/**
	 * caoligai 增加,发送一条信息
	 * 
	 * @param message
	 *            信息 Model
	 * @throws ApiException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	String sendMessage(ModelChatMessage message) throws ApiException,
			DataInvalidException, VerifyErrorException;
	
	/**
	 * 获取聊天房间的title
	 * @return
	 * @throws ApiException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	public String getChatInfo(String method,int to_uid) throws ApiException;
	/**
	 * 获取头像
	 * @return
	 * @throws ApiException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	public String getUserFace(String method, int to_uid, ApiHttpClient.HttpResponseListener listener) throws ApiException;
	/**
	 * 获取附件地址
	 * @return
	 * @throws ApiException
	 * @throws DataInvalidException
	 * @throws VerifyErrorException
	 */
	public String getAttachAddress(String attach_id,String method) throws ApiException;

	Object getUnreadMessage(String list_id) throws ApiException, DataInvalidException,
			VerifyErrorException;
}
