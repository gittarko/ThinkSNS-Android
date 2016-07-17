package com.thinksns.sociax.t4.android.gift;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.adapter.AdapterMyFriends;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.widget.ContactItemInterface;
import com.thinksns.sociax.t4.android.widget.ContactListViewImpl;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wz
 * @version 1.0
 * @date 2015-1-23
 */
public class ActivityFindGiftReceiver2 extends ThinksnsAbscractActivity implements TextWatcher {

    private ContactListViewImpl listviewFriend;
    private EditText searchBox;
    private String searchString;

    private Object searchLock = new Object();
    boolean inSearchMode = false;

    List<ContactItemInterface> contactList;
    List<ContactItemInterface> filterList;
    private SearchListTask curSearchTask = null;
    private AdapterMyFriends adapter = null;
    private LinearLayout ll_default;

    @Override
    public String getTitleCenter() {
        return "赠送礼物";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        filterList = new ArrayList<ContactItemInterface>();
        ll_default = (LinearLayout) findViewById(R.id.ll_default);

        loadingView = (LoadingView) findViewById(LoadingView.ID);

        listviewFriend = (ContactListViewImpl) findViewById(R.id.lv_my_friends);
        listviewFriend.setFastScrollEnabled(true);

        searchBox = (EditText) findViewById(R.id.input_search_query);
        searchBox.addTextChangedListener(this);

        listviewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelSearchUser user = (ModelSearchUser) view.getTag(R.id.tag_search_user);

                ModelUser returnuser = new ModelUser();
                returnuser.setUserName(user.getUname());
                returnuser.setUid(user.getUid());
                returnuser.setFace(user.getUface());
                returnuser.setIntro(user.getIntro());

                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putString("name", returnuser.getUserName());
                b.putInt("uid", returnuser.getUid());
                b.putSerializable("user", returnuser);

                i.putExtras(b);
                setResult(StaticInApp.RESULT_CODE_SELET_GIFT_RECEIVER, i);
                finish();
            }
        });

        getFriendsList();
    }

    public void getFriendsList() {
        Thinksns app = (Thinksns) getApplicationContext();
        try {
            app.getUsers().getUserFriendsList(Thinksns.getMy().getUid(), 0, new ApiHttpClient.HttpResponseListener() {
                @Override
                public void onSuccess(Object result) {
                    Message msg = new Message();
                    msg.what = StaticInApp.GET_FRIENDS_EACHOTHER;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }

                @Override
                public void onError(Object result) {
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_friends;
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
            Toast.makeText(ActivityFindGiftReceiver2.this, result.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticInApp.GET_FRIENDS_EACHOTHER:
                    try {
                        contactList = (List<ContactItemInterface>) msg.obj;
                        if (contactList == null) {
                            return;
                        }
                        if (contactList.size() == 0) {
                            ll_default.setVisibility(View.VISIBLE);
                        } else {
                            ll_default.setVisibility(View.GONE);
                            adapter = new AdapterMyFriends(ActivityFindGiftReceiver2.this, R.layout.list_item_my_friends,
                                    contactList, StaticInApp.CONTACTS_LIST_FRIENDS, false);
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
                    AdapterMyFriends adapter = new AdapterMyFriends(ActivityFindGiftReceiver2.this,
                            R.layout.list_item_my_friends, filterList, StaticInApp.CONTACTS_LIST_FRIENDS, false);
                    adapter.setInSearchMode(true);
                    listviewFriend.setInSearchMode(true);
                    listviewFriend.setAdapter(adapter);
                } else {
                    if (contactList != null) {
                        AdapterMyFriends adapter = new AdapterMyFriends(ActivityFindGiftReceiver2.this,
                                R.layout.list_item_my_friends, contactList,
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
}
