package com.thinksns.sociax.t4.android.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterMyFriends;
import com.thinksns.sociax.t4.android.Thinksns;

import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.widget.ContactItemInterface;
import com.thinksns.sociax.t4.android.widget.ContactListViewImpl;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

/** 
 * 类说明：   选择聊天的好友
 * 
 * @author  Zoey    
 * @date    2015年11月11日
 * @version 1.0
 */
public class FragmentSelectChat extends FragmentSociax implements TextWatcher{

	private ContactListViewImpl listviewFriend;
	private EditText searchBox;
	private String searchString;

	private Object searchLock = new Object();
	boolean inSearchMode = false;
	
	List<ContactItemInterface> contactList;
	List<ContactItemInterface> filterList;
	private SearchListTask curSearchTask = null;
	private AdapterMyFriends adapter=null;
	
	private boolean isSingleSelect=false;
	public static Map<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_select_chat_user, null);
		
		filterList = new ArrayList<ContactItemInterface>();

		listviewFriend = (ContactListViewImpl)view.findViewById(R.id.lv_chat_user);
		listviewFriend.setFastScrollEnabled(true);

		searchBox = (EditText) view.findViewById(R.id.input_search_query);
		searchBox.addTextChangedListener(this);
		
		listviewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				Log.v("clickMsg", "------------clickMsg--------------");
				
				final ModelSearchUser searchUser = (ModelSearchUser) view.getTag(R.id.tag_search_user);
				if (searchUser == null) {
					return;
				}
				RelativeLayout rl_user=(RelativeLayout)view.findViewById(R.id.rl_user);
				final CheckBox cb_select=(CheckBox)view.findViewById(R.id.cb_select);
				
				cb_select.setTag(R.id.tag_follow, searchUser);
				view.setTag(R.id.tag_position, position);
				cb_select.setChecked(searchUser.isSelect());
				
				rl_user.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						boolean preSelect =searchUser.isSelect();
						searchUser.setSelect(!preSelect);
						cb_select.setChecked(!preSelect);
						
						//如果是单选，需要把其他选择去掉
						if(isSingleSelect){
							for (int i = 0; i < contactList.size()&&(i!=(Integer) v.getTag(R.id.tag_position)); i++) {
								isSelected.put(i,false);
								searchUser.setSelect(false);
							}
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		});
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getFriendsList();
	}
	
	public void getFriendsList(){
		try {
			app.getUsers().getUserFriendsList(Thinksns.getMy().getUid(), 0, mListener);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}

	private ApiHttpClient.HttpResponseListener mListener = new ApiHttpClient.HttpResponseListener() {
		@Override
		public void onSuccess(Object result) {
			Message msg = new Message();
			msg.what = StaticInApp.GET_FRIENDS_EACHOTHER;
			msg.obj = result;
			handler.sendMessage(msg);
		}

		@Override
		public void onError(Object result) {
			Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_SHORT).show();
		}
	};

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StaticInApp.GET_FRIENDS_EACHOTHER:
				try {
					contactList = (List<ContactItemInterface>) msg.obj;
					adapter = new AdapterMyFriends(getActivity(), R.layout.list_item_my_friends,contactList,StaticInApp.CONTACTS_LIST_CHAT,false);
					listviewFriend.setAdapter(adapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};
	
	/**
	 * 获取被选择的用户列表
	 * 
	 * @return
	 */
	public List<ContactItemInterface> getSelectUser() {
		if (contactList!=null&&contactList.size()!=0) 
			return contactList;
		return null;
	}
	
	@Override
	public int getLayoutId() {
		return 0;
	}


	@Override
	public void initView() {

	}

	@Override
	public void initIntentData() {

	}

	@Override
	public void initListener() {

	}

	@Override
	public void initData() {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		searchString = searchBox.getText().toString().trim().toUpperCase();

		if (curSearchTask != null
				&& curSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
			try {
				curSearchTask.cancel(true);
			} catch (Exception e) {
			}
		}
		curSearchTask = new SearchListTask();
		curSearchTask.execute(searchString);
	}
	
	private class SearchListTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			filterList.clear();
			String keyword = params[0];
			inSearchMode = (keyword.length() > 0);
			if (inSearchMode) {
				for (ContactItemInterface item : contactList) {
					ModelSearchUser contact = (ModelSearchUser) item;
					boolean isPinyin = contact.getUname().toUpperCase().indexOf(keyword) > -1;
					boolean isChinese = contact.getUname().indexOf(keyword) > -1;
					if (isPinyin || isChinese) {
						filterList.add(item);
					}
				}
			}
			return null;
		}

		protected void onPostExecute(String result) {

			synchronized (searchLock) {
				if (inSearchMode) {
					AdapterMyFriends adapter = new AdapterMyFriends(getActivity(),R.layout.list_item_my_friends, filterList,StaticInApp.CONTACTS_LIST_CHAT,false);
					adapter.setInSearchMode(true);
					listviewFriend.setInSearchMode(true);
					listviewFriend.setAdapter(adapter);
				} else {
					AdapterMyFriends adapter = new AdapterMyFriends(getActivity(),R.layout.list_item_my_friends, contactList,StaticInApp.CONTACTS_LIST_CHAT,false);
					adapter.setInSearchMode(false);
					listviewFriend.setInSearchMode(false);
					listviewFriend.setAdapter(adapter);
				}
			}
		}
	}
}
