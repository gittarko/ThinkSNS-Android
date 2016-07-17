package com.thinksns.sociax.t4.android.fragment;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.adapter.AdapterFindPeopleByContract;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 手机通讯录找人
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-4
 */
public class FragmentFindPeopleByContract extends FragmentSociax {
    protected int selectpostion;

    private PullToRefreshListView pullToRefreshListView;
    private EmptyLayout emptyLayout;
    private LinearLayout mDefaultBg;
    private LinearLayout title_layout;
    private contractLoader mMyLoader;
    /**
     * 获取库Phon表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};
    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;
    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;
    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        //初始化下拉刷新组件
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_listview_divider)));
        listView.setDividerHeight(1);
        listView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));
        //初始化空置页面
        emptyLayout = (EmptyLayout)findViewById(R.id.empty_layout);
        emptyLayout.setNoDataContent("你还没有联系人");

        mMyLoader = new contractLoader();
        getActivity().getLoaderManager().initLoader(10, null, mMyLoader);

        title_layout = (LinearLayout) findViewById(R.id.title_layout);
        title_layout.setVisibility(View.GONE);
    }

    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelSearchUser user = (ModelSearchUser)adapter.getItem((int)id);
                if(user != null) {
                    if(user.getUid() != 0) {
                        //进入个人中心
                        Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
                        intent.putExtra("uid", user.getUid());
                        getActivity().startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        if (adapter != null) {
            adapter.loadInitData();
        }
    }

    @Override
    public EmptyLayout getEmptyLayout() {
        return emptyLayout;
    }

    /**
     * 获取/载入本地联系人
     *
     * @author wz
     */
    class contractLoader implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String order = Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            CursorLoader cursor = new CursorLoader(getActivity(),
                    Phone.CONTENT_URI, PHONES_PROJECTION, null, null, order);
            return cursor;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor phoneCursor) {
            StringBuilder builder = new StringBuilder();
            list = new ListData<SociaxItem>();
            if (phoneCursor != null) {
                phoneCursor.moveToFirst();
                while (phoneCursor.moveToNext()) {
                    String phoneNumber = phoneCursor.getString(
                            PHONES_NUMBER_INDEX).replaceAll(" ", "");
                    String contactName = phoneCursor
                            .getString(PHONES_DISPLAY_NAME_INDEX);
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    ModelSearchUser user = new ModelSearchUser();
                    builder.append("," + phoneNumber);
                    user.setPhone(phoneNumber);
                    user.setUname(contactName);
                    list.add(user);
                }
            }

            if(list.size() > 0) {
                //加载联系人
                adapter = new AdapterFindPeopleByContract(
                        FragmentFindPeopleByContract.this, list, getActivity()
                        .getIntent().getIntExtra("uid",Thinksns.getMy().getUid()));
                listView.setAdapter(adapter);
                initData();
            }else {
                emptyLayout.setErrorType(EmptyLayout.NODATA);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_userlist;
    }
}
