package com.thinksns.tschat.api;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.tschat.bean.ModelUser;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhiYiForMac on 15/12/8.
 */
public class UserApi {

    /**
     * 获取用户好友列表
     * @param uid
     * @param max_id
     * @param handler
     */
    public static void getUserFriends(int uid, int max_id, final RequestResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("max_id", max_id);
        ApiHttpClient.post(new String[]{"User", "user_friend"}, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                List<ModelUser> listData = new ArrayList<ModelUser>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        ModelUser follow = new ModelUser(response.getJSONObject(i));
                        if (follow.getUid() != 0)
                            listData.add(follow);
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.onSuccess(listData);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                handler.onFailure(errorResponse);
            }
        });
    }
}
