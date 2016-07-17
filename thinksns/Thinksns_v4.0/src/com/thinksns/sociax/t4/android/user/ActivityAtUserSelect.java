package com.thinksns.sociax.t4.android.user;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.adapter.AdapterSearchAt;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.component.SearchComponent;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：@好友专用,返回intent int uid ,String at_name ,ModelUser user
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-23
 */
public class ActivityAtUserSelect extends ThinksnsAbscractActivity implements
        OnKeyListener, OnCheckedChangeListener {
    private AdapterSearchAt usListAdapter;
    private ListView listView;
    private PullToRefreshListView pullToRefreshListView;

    private SearchComponent edit;
    private Button goForSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loadNewData(null);

    }

    private void init() {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setDivider(new ColorDrawable(0xffdddddd));
        listView.setDividerHeight(1);
        edit = (SearchComponent) findViewById(R.id.editCancel1);
        goForSearch = (Button) findViewById(R.id.go_for_search);

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
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelSearchUser user = usListAdapter.getItem((int) id);
                setResult(RESULT_OK, getIntent().putExtra("at_name", user.getUname()));
                finish();
            }
        });
    }

    private void doSearch() {

        String key = edit.getText().trim();
        loadNewData(key);
    }

    private void loadNewData(String key) {
        ListData<SociaxItem> data = new ListData<SociaxItem>();
        usListAdapter = new AdapterSearchAt(ActivityAtUserSelect.this, data,
                key);
        listView.setAdapter(usListAdapter);
        usListAdapter.loadInitData();
    }

    @Override
    public String getTitleCenter() {
        return getString(R.string.recent);
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_at_user_select;
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

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullToRefreshListView;
    }
}
