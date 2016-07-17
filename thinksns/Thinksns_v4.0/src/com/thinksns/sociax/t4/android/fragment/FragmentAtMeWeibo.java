package com.thinksns.sociax.t4.android.fragment;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.presenter.WeiboListListPresenter;

/**
 * 类说明：提到我的微博
 * @version 1.0
 */
public class FragmentAtMeWeibo extends FragmentWeiboListViewAll {

	@Override
	protected void initPresenter() {
		mPresenter = new WeiboListListPresenter(getActivity(), this, this) {
			@Override
			public void loadNetData() {
				new Api.WeiboApi().atMeWeibo(getPageSize(), getMaxId(), mHandler);
			}
		};
		mPresenter.setCacheKey(getCacheKey());
	}

	@Override
	protected String getCacheKey() {
		return "atme_weibo";
	}
}
