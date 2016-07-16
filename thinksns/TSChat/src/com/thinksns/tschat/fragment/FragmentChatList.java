package com.thinksns.tschat.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;
import com.thinksns.tschat.R;
import com.thinksns.tschat.adapter.AdapterChatRoomList;
import com.thinksns.tschat.api.MessageApi;
import com.thinksns.tschat.base.BaseListFragment;
import com.thinksns.tschat.base.ListBaseAdapter;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ListEntity;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSChat;
import com.thinksns.tschat.eventbus.SocketLoginEvent;
import com.thinksns.tschat.inter.ChatCoreResponseHandler;
import com.thinksns.tschat.ui.ActivityChatDetail;
import com.thinksns.tschat.widget.SmallDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 类说明： 默认聊天列表fragmnet
 * 其他开发者以继承的方式集成聊天
 * @author dong.he
 *
 */
public abstract class FragmentChatList extends BaseListFragment<ModelChatUserList> {

	protected static final String TAG = "FragmentChatList";
	private ModelChatUserList chat;
	private AlertDialog del_dialog;
	private SmallDialog toastDlg;
	private View mErrorLayout;

	private static FragmentChatList instance;
	private Activity currentActivity;

	private Map<Integer, Integer> unraadsMsg;

	private BroadcastReceiver mReceiver;
	private boolean onLoadMore = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.currentActivity = activity;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		try {
			super.setUserVisibleHint(isVisibleToUser);
			if(getUserVisibleHint()) {
				//视图可见
			}else {

			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		EventBus.getDefault().register(this);
	}

	public static FragmentChatList getInstance() {
		return instance;
	}

	@Override
	public void initView(View view) {
		toastDlg = new SmallDialog(getActivity(), "加载中...");
		toastDlg.setCanceledOnTouchOutside(false);
		super.initView(view);
		//将fragment注入TSChat
		TSChatManager.initRoom(this);
	}

	@Override
	protected void initListViewAttrs() {
		super.initListViewAttrs();
		//列表点击样式
		mListView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));
		//网络错误提示视图
		View view = initErrorLayout();
		mListView.addHeaderView(view);
	}

	/**
	 * 初始化网络提示试图
	 * @return
	 */
	private View initErrorLayout() {
		View layout  = LayoutInflater.from(getActivity()).inflate(R.layout.network_error_layout, null);
		mErrorLayout = layout.findViewById(R.id.ll_error_layout);
		return layout;
	}

	@Override
	protected void initReceiver() {
	}

	@Override
	protected void destroyReceiver() {
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onSocketStatus(SocketLoginEvent event) {
		final SocketLoginEvent.LOGIN_STATUS status = event.getStatus();
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (status) {
					case LOGIN_ERROR:
						mErrorLayout.setVisibility(View.VISIBLE);
						break;
					case LOGIN_SUCCESS:
						mErrorLayout.setVisibility(View.GONE);
						break;
				}
			}
		});

	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void getRoomList(List<ModelChatUserList> list) {
		setLoadFinish(true);
		if(toastDlg.isShowing())
			toastDlg.dismiss();
		if(list.size() > 0)
			executeParserTask(list);
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void updateLastMessage(final ModelChatUserList room) {
		if(currentActivity == null) {
			Log.v(TAG, "activity is null");
			return;
		}

		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				int i = 0;
				for(; i<mAdapter.getDataSize(); i++) {
					if(mAdapter.getItem(i).getRoom_id() == room.getRoom_id()) {
						mAdapter.removeItem(room);
						break;
					}
				}
				//将最新消息置于顶部
				mAdapter.addItem(0, room);
			}
		});

	}

	@Override
	protected String getCacheKeyPrefix() {
		return "chat_room";
	}

	/**
	 * 子类实现该方法必须调用super以使得该方法被使用
	 * @param parent
	 * @param view
	 * @param position
     * @param id
     */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		chat = (ModelChatUserList) view.getTag(R.id.tag_chat);
		if (chat == null) {
			return;
		}

		if (chat.getRoom_id() == 0)
			return;
		if (chat.getIsNew() > 0) {
			//清空未读消息
			chat.setIsNew(0);
			mAdapter.notifyDataSetChanged();
			TSChatManager.sendClearUnreadMsg(chat.getRoom_id(), "unread",
					new ChatCoreResponseHandler() {
				@Override
				public void onSuccess(Object object) {
					//清理未读成功
					Log.v(TAG, "CLEAR MESSAGE,CLEAR TYPE:unread--->onSuccess");
//					TSChatManager.clearUnreadMessage(chat.getRoom_id(),"unread");
				}

				@Override
				public void onFailure(Object object) {
					Log.v(TAG, "CLEAR MESSAGE, CLEAR TYPE:unread--->onFailure");
				}
			});


			TSChatManager.clearUnreadMessage(chat.getRoom_id(),"unread");

		}

		ActivityChatDetail.initChatInfo(chat);
		ActivityStack.startActivity(currentActivity, getChatDetailActivity());

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final ModelChatUserList chatUser = (ModelChatUserList) view.getTag(R.id.tag_chat);
		if (chatUser != null) {
			//删除当前聊天
			del_dialog = new AlertDialog.Builder(getActivity()).create();
			del_dialog.show();

			Window window = del_dialog.getWindow();
			window.setContentView(R.layout.dialog_del_room_of_chat);
			TextView tv_del_chat = (TextView) window.findViewById(R.id.tv_del_room_of_chat);
			tv_del_chat.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					del_dialog.dismiss();
					TSChatManager.sendClearUnreadMsg(chatUser.getRoom_id(), "all",
							new ChatCoreResponseHandler() {
								@Override
								public void onSuccess(Object object) {
									//清理未读成功
									Log.v(TAG, "CLEAR MESSAGE,CLEAR TYPE:all--->onSuccess");
									TSChatManager.clearUnreadMessage(chatUser.getRoom_id(),"all");
									onDeleteChat(chatUser, true);
								}

								@Override
								public void onFailure(Object object) {
									Log.v(TAG, "CLEAR MESSAGE, CLEAR TYPE:all--->onFailure");
									onDeleteChat(chatUser, true);
								}
							});
				}
			});

			if (!del_dialog.isShowing())
				del_dialog.show();
		}

		return true;

	}


	//跳转至聊天详情
	public abstract Class<? extends Activity> getChatDetailActivity();

	/**
	 * 删除与某人的聊天消息
	 * @param chat
     */
	public void onDeleteChat(ModelChatUserList chat, boolean isSucess)
	{
		if(isSucess) {
			//删除消息记录
			mAdapter.removeItem(chat);
		}
	}

	//根据房间号删除列表
	public void onDeleteChat(int room_id) {
		for(int i=0; i<mAdapter.getDataSize(); i++) {
			if(mAdapter.getItem(i).getRoom_id() == room_id) {
				mAdapter.removeItem(mAdapter.getItem(i));
				break;
			}
		}
	}

	@Override
	public void initData() {
		//优先读取本地的内容
		getCacheData(0);
//		if(!TSChatManager.isLogin() || !UnitSociax.isNetWorkON(getActivity())) {
//			//socket未连接成功或网络未连接
//			mErrorLayout.setVisibility(View.VISIBLE);
//		}

	}

	//获取聊天服务器消息记录
	private void getSocketRooms(int mtime) {
		TSChatManager.getRoomList(0, mtime, 0, new ChatCoreResponseHandler() {
			@Override
			public void onStart(Object object) {
				Log.v(TAG, "GET ROOM LIST--->onStart:" + object.toString());
			}

			@Override
			public void onSuccess(Object object) {
				Log.v(TAG, "GET ROOM LSIT--->onSuccess!");
				List<ModelChatUserList> list = (List<ModelChatUserList>)object;
				pullToRefreshListView.onRefreshComplete();
				getRoomList(list);
			}

			@Override
			public void onFailure(Object object) {
				Log.v(TAG, "GET ROOM LIST--->onFailure");
				if(toastDlg.isShowing())
					toastDlg.dismiss();
			}
		});
	}

	private void getCacheData(int mtime) {
		//从本地加载房间记录
		ArrayList<ModelChatUserList> messageList = new MessageApi(getActivity()).getRoomList(mtime, getPageSize());
		if(mtime == 0) {
			//第一页消息记录插入列表头部
			if(messageList.size() > 0)
				mAdapter.addData(messageList, 0);
			else
				toastDlg.show();
		}else if(messageList.size() > 0){
			mAdapter.addData(messageList);
		}

		getSocketRooms(mtime);
	}

	@Override
	protected boolean requestDataIfViewCreated() {
		return false;
	}

	/**
	 * 查找adapter中是否存在entity
	 * @param data
	 * @param enity
     * @return 如果存在则更新列表内容
     */
	@Override
	protected boolean compareTo(final List<? extends Entity> data, Entity enity) {
		if (enity != null) {
			int s = data.size();
			for (int i = 0; i < s; i++) {
				ModelChatUserList item = (ModelChatUserList)enity;
				if (item.getRoom_id() == ((ModelChatUserList)data.get(i)).getRoom_id()) {
					//替换列表中的旧内容
					mAdapter.setItem(i, item);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(TSChatManager.isLogin()) {
			mErrorLayout.setVisibility(View.GONE);
		}else {
			mErrorLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void sendRequestData() {
		//获取聊天列表,默认拿20条
		int mtime = 0;
		if(mAdapter.getLastItem() != null) {
			mtime = mAdapter.getLastItem().getMtime();
		}
		loadFinish = false;
		new MessageApi(getActivity()).getRoomList(mtime, getPageSize());

	}

	@Override
	public void refreshCacheData() {
		//获取本地缓存的消息记录
		int count = 0;
		if(mAdapter.getCount() == 0) {
			count = getPageSize();
		}else {
			count = mAdapter.getCount();
		}
		new MessageApi(getActivity()).getRoomList(0, getPageSize());
	}

	//解析数据,该方法在线程中请勿直接操作主线程数据
	@Override
	protected ListEntity<ModelChatUserList> parseList(final Object responseData) throws Exception {
		return new ListEntity<ModelChatUserList>() {
			@Override
			public List<ModelChatUserList> getList() {
				List<ModelChatUserList> list = (ArrayList<ModelChatUserList>)responseData;
				if(list == null || list.size() == 0) {
					if(!loadFinish) {
						//开始从网络获取数据
						pullToRefreshListView.post(new Runnable() {
							@Override
							public void run() {
								toastDlg.show();
							}
						});
					}

					if(mAdapter.getLastItem() != null && onLoadMore) {
						//本地无更多记录，从服务器获取更多
						TSChatManager.getRoomList(0, mAdapter.getLastItem().getMtime(), getPageSize(), null);
						onLoadMore = false;
					}
				}
				return list;
			}
		};
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_chat_room_listview;
	}

	protected View getListFooterView() {
		return null;
	}

	protected View getListHeaderView() {
		return null;
	}

	@Override
	public ListBaseAdapter getListAdapter() {
		return new AdapterChatRoomList(this);
	}

	@Override
	protected void executeOnLoadDataSuccess(List<ModelChatUserList> data) {
		super.executeOnLoadDataSuccess(data);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

	}


	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		//检查服务器是否有新消息
		mCurrentPage = 0;
		onLoadMore = false;
		readCache = false;
		TSChatManager.getRoomList(0, 0, 20, new ChatCoreResponseHandler() {
			@Override
			public void onStart(Object object) {
				Log.v(TAG, "GET ROOM LIST--->onStart");
			}

			@Override
			public void onSuccess(Object object) {
				Log.v(TAG, "GET ROOM LSIT--->onSuccess!");
				if(object != null) {
					List<ModelChatUserList> list = (List<ModelChatUserList>) object;
					getRoomList(list);
				}
			}

			@Override
			public void onFailure(Object object) {
				Log.v(TAG, "GET ROOM LIST--->onFailure");
				pullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onFinish(Object object) {
				Log.v(TAG, "GET_ROOM_LIST--->onFinish");
				pullToRefreshListView.onRefreshComplete();
			}
		});
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		//获取更多记录
		ModelChatUserList room = mAdapter.getItem(mAdapter.getCount() - 1);
		TSChatManager.getRoomList(0, room.getMtime(), 20, null);
		onLoadMore = true;
		readCache = false;
	}

	//记录消息未读数
	public void addUnreadMsg(int room_id, int count) {
		TSChatManager.addUnreadMsgCount(room_id, count);
	}

}
