package com.thinksns.tschat.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.tschat.R;
import com.thinksns.tschat.adapter.AdapterSelectUser;
import com.thinksns.tschat.api.UserApi;
import com.thinksns.tschat.base.BaseListFragment;
import com.thinksns.tschat.base.ListBaseAdapter;
import com.thinksns.tschat.bean.ListEntity;
import com.thinksns.tschat.bean.ModelUser;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.widget.SmallDialog;

import java.util.List;

/**
 * 类说明： 选择好友,默认为多选，特殊情况如果activity的intent传入int StaticInApp.SELECT_GIFT_RESEND,表示礼物转赠，单选
 * @author wz
 * @date 2014-11-18
 * @version 1.0
 */
public class FragmentSelectUser extends BaseListFragment<ModelUser> {
	private static List<ModelUser> selectUserList;
	private boolean isSingle = false;

	private EmptyLayout emptyLayout;
	private SmallDialog dialog;

	@Override
	public void initView(View view) {
		dialog = new SmallDialog(view.getContext(), "加载中...");
		dialog.setCanceledOnTouchOutside(false);

		super.initView(view);
		emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
	}

	@Override
	protected String getCacheKeyPrefix() {
		return "select_user_";
	}

	@Override
	protected View getListHeaderView() {
		return null;
	}

	@Override
	protected View getListFooterView() {
		return null;
	}

	@Override
	public ListBaseAdapter getListAdapter() {
		return new AdapterSelectUser(this, isSingle);
	}

	@Override
	public void initIntentData() {
		int select_type = getActivity().getIntent().getIntExtra("select_type", TSConfig.SELECT_CHAT_USER);
		if(select_type == TSConfig.SELECT_CARD) {
			isSingle = true;
		}
	}


	@Override
	public void initListener() {

	}

	@Override
	public void initData() {

	}

	@Override
	public void sendRequestData() {
		mCurrentPage = 0;
		UserApi.getUserFriends(TSChatManager.getLoginUser().getUid(), 0, mHandler);
		dialog.show();
	}

	@Override
	protected ListEntity<ModelUser> parseList(final Object reponseData) throws Exception {
		return new ListEntity<ModelUser>() {
			@Override
			public List<ModelUser> getList() {
				return (List<ModelUser>)reponseData;
			}
		};
	}

	@Override
	protected void executeOnLoadDataSuccess(List<ModelUser> data) {
		if(mAdapter.getDataSize() == 0 && data.size() == 0) {
			pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
			pullToRefreshListView.setVisibility(View.GONE);
			//显示默认缺省图
			emptyLayout.setVisibility(View.VISIBLE);
			emptyLayout.setNoDataContent(getResources().getString(R.string.empty_friends));
		}

		dialog.dismiss();
		super.executeOnLoadDataSuccess(data);
	}

	/**
	 * 获取被选择的用户列表
	 * 
	 * @return
	 */
	public List<ModelUser> getSelectUser() {
		return ((AdapterSelectUser)mAdapter).getSelectUser();
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_chat_userlist;
	}

}
