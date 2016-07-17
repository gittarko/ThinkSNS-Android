package com.thinksns.sociax.t4.android.gift;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterFindGiftReceiver;
import com.thinksns.sociax.t4.adapter.AdapterMyFriends;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.widget.ContactItemInterface;
import com.thinksns.sociax.t4.component.ListGiftUser;
import com.thinksns.sociax.t4.component.SearchComponent;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;

import java.util.List;

/**
 * @author wz
 * @version 1.0
 * @date 2015-1-23
 */
public class ActivityFindGiftReceiver extends ThinksnsAbscractActivity implements
        OnKeyListener, OnCheckedChangeListener {
    private ListGiftUser seUserList;
    private AdapterFindGiftReceiver usListAdapter;
    private SearchComponent edit;
    private LinearLayout layout;
    private Button goForSearch;
    private String mTitle;
    private TextView tv_title_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        mTitle = getIntent().getStringExtra("press_button");
        init();
    }

    private void init() {
        seUserList = (ListGiftUser) findViewById(R.id.find_userList);
        edit = (SearchComponent) findViewById(R.id.editCancel1);
        layout = (LinearLayout) findViewById(R.id.search_layout);
        goForSearch = (Button) findViewById(R.id.go_for_search);
        tv_title_back = (TextView) findViewById(R.id.tv_title_left);

        edit.setOnKeyListener(this);


        goForSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                doSearch();
            }
        });
        tv_title_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void doSearch() {
        String key = edit.getText().toString().trim();
        loadNewData(key);
    }

    private void loadNewData(String key) {
        ListData<SociaxItem> data = new ListData<SociaxItem>();
        usListAdapter = new AdapterFindGiftReceiver(ActivityFindGiftReceiver.this, data, key);
        seUserList.setAdapter(usListAdapter, System.currentTimeMillis(), ActivityFindGiftReceiver.this);
        usListAdapter.loadInitData();
    }

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(0, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_gift_receiver;
    }

    @Override
    public OnTouchListListener getListView() {
        return seUserList;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event != null
                && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_ENVELOPE)
                && event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_UP) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
            doSearch();
            return true;
        }
        return false;
    }
}
