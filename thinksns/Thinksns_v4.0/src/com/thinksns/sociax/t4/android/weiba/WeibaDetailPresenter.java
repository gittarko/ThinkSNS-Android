package com.thinksns.sociax.t4.android.weiba;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.view.IWeibaDetailView;
import com.thinksns.sociax.t4.model.ModelWeiba;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by hedong on 16/4/5.
 * 微吧详情业务执行类
 */
public class WeibaDetailPresenter {
    private ModelWeiba weiba;
    private IWeibaDetailView weibaDetailView;

    public WeibaDetailPresenter(ModelWeiba weiba, IWeibaDetailView view) {
        this.weiba = weiba;
        this.weibaDetailView = view;
    }

    //获取微吧详情
    public void getWeibaDetails() {

    }

    //关注、取消关注微吧
    public void changeWeibaFollow(final boolean isfollow) {
        new Api.WeibaApi().changeWeibaFollow(weiba.getWeiba_id(), isfollow, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getInt("status") == 1) {
                        //操作成功
                        if (weiba.isFollow()) {
                            weiba.setFollow(false);
                        } else {
                            weiba.setFollow(true);
                        }
                        weibaDetailView.changeWeibaFollow(1, "");
                    } else {
                        //操作失败
                        weibaDetailView.changeWeibaFollow(0, response.getString("msg"));
                    }
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                weibaDetailView.changeWeibaFollow(0, "网络连接失败, 请稍后重试");
            }
        });
    }
}
