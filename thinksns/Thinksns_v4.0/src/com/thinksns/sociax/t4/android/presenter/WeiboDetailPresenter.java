package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.android.view.IWeiboDetailsView;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;

/**
 * Created by hedong on 16/2/23.
 */
public class WeiboDetailPresenter extends WeiboListListPresenter{
    private IWeiboDetailsView mWeiboDetailsView;
    private int weibo_id;

    public WeiboDetailPresenter(Context context, IBaseListView<ModelWeibo> baseListView,
                                WeiboListViewClickListener listViewClickListener) {
        super(context, baseListView, listViewClickListener);
    }

    public void loadWeiboDetails(int weibo_id, IWeiboDetailsView weiboDetailsView) {
        this.weibo_id = weibo_id;
        this.mWeiboDetailsView = weiboDetailsView;
        new getWeiboDetailTask().execute();
    }

    class getWeiboDetailTask extends AsyncTask<Void, Void, ModelWeibo> {
        protected ModelWeibo doInBackground(Void... params) {
            try {
                return new Api.StatusesApi().getWeiboById(weibo_id);
            } catch (Exception e) {
                return null;
            }
    }

        @Override
        protected void onPostExecute(ModelWeibo result) {
            if (result == null || result.equals("")) {
                mWeiboDetailsView.setErrorData("请求网络数据失败");
            } else {
                //设置微博正文内容
                mWeiboDetailsView.setWeiboContent(result);
                //设置微博点赞用户列表
                mWeiboDetailsView.setDiggUsers(result.getDiggUsers());
                //设置微博评论列表
                mWeiboDetailsView.setWeiboComments(result.getCommentList());
            }
        }
    }

}
