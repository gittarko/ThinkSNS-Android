package com.thinksns.sociax.t4.android.fragment;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterMyFriends;
import com.thinksns.sociax.t4.android.Thinksns;

import com.thinksns.sociax.t4.component.ListSociax;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.widget.ContactItemInterface;
import com.thinksns.sociax.t4.android.widget.ContactListViewImpl;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

/** 
 * 类说明：   互相关注的好友
 * 
 * @author  Zoey    
 * @date    2015年11月10日
 * @version 1.0
 */
public class FragmentMyFriends extends FragmentSociax implements TextWatcher{

	private ContactListViewImpl listviewFriend;
	private EditText searchBox;
	private String searchString;

	private Object searchLock = new Object();
	boolean inSearchMode = false;
	
	List<ContactItemInterface> contactList;
	List<ContactItemInterface> filterList;
	private SearchListTask curSearchTask = null;
	private AdapterMyFriends adapter=null;
	private LinearLayout ll_default;
	
	private LoadingView loadingView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_friends, null);
		filterList = new ArrayList<ContactItemInterface>();
		ll_default=(LinearLayout)view.findViewById(R.id.ll_default);
		
		loadingView = (LoadingView) view.findViewById(LoadingView.ID);

		listviewFriend = (ContactListViewImpl)view.findViewById(R.id.lv_my_friends);
		listviewFriend.setFastScrollEnabled(true);

		searchBox = (EditText) view.findViewById(R.id.input_search_query);
		searchBox.addTextChangedListener(this);
		
		listviewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Bundle data = new Bundle();
				ModelSearchUser searchUser = (ModelSearchUser) view.getTag(R.id.tag_search_user);
				if (searchUser == null) {
					Log.v("ListUser--onClick", "wztest tag null");
					return;
				}
				Log.v("ListUser--onClick",searchUser.getUname() + searchUser.getUid());
				data.putInt("uid", searchUser.getUid());
				data.putString("name", searchUser.getUname());
				ActivityStack.startActivity(getActivity(), ActivityUserInfo_2.class, data);
			}
		});
		
		getFriendsList();
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (adapter !=null) {
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public int getLayoutId() {
		return 0;
	}

	@Override
	public void initView() {
		
	}

	public void getFriendsList(){
		loadingView.show(listviewFriend);
		Thinksns app = (Thinksns)getActivity().getApplicationContext();
		try {
			app.getUsers().getUserFriendsList(Thinksns.getMy().getUid(), 0, mListener);
		} catch (ApiException e) {
			e.printStackTrace();
			loadingView.hide(listviewFriend);
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
					if (contactList==null) {
						return;
					}
					if (contactList.size()==0) {
						ll_default.setVisibility(View.VISIBLE);
					}else {
						ll_default.setVisibility(View.GONE);
						adapter = new AdapterMyFriends(getActivity(), R.layout.list_item_my_friends,
								contactList,StaticInApp.CONTACTS_LIST_FRIENDS,false);
						listviewFriend.setAdapter(adapter);
					}
					loadingView.hide(listviewFriend);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};
	
	@Override
	public void initIntentData() {
		
	}

	@Override
	public void initListener() {
		
	}

	@Override
	public void initData() {
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
					AdapterMyFriends adapter = new AdapterMyFriends(getActivity(),R.layout.list_item_my_friends, filterList,StaticInApp.CONTACTS_LIST_FRIENDS,false);
					adapter.setInSearchMode(true);
					listviewFriend.setInSearchMode(true);
					listviewFriend.setAdapter(adapter);
				} else {
					if(contactList != null) {
						AdapterMyFriends adapter = new AdapterMyFriends(getActivity(), R.layout.list_item_my_friends, contactList,
								StaticInApp.CONTACTS_LIST_FRIENDS, false);
						adapter.setInSearchMode(false);
						listviewFriend.setInSearchMode(false);
						listviewFriend.setAdapter(adapter);
					}
				}
			}
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {
		
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
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
}
