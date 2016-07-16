package com.thinksns.tschat.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thinksns.sociax.thinksnsbase.base.BaseApplication;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.db.SQLHelperChatMessage;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * Created by hedong on 15/12/3.
 * 聊天消息API
 */
public class MessageApi {
    private static SQLHelperChatMessage sqlChatHelper;
    private static ApiHttpClient client;

    public MessageApi(Context context) {
        if(sqlChatHelper == null)
            sqlChatHelper = SQLHelperChatMessage.getInstance(context);
    }

    /**
     * 获取聊天房间列表
     * @param mtime
     * @param count
     */
    public ArrayList<ModelChatUserList> getRoomList(int mtime, int count) {
        //从本地获取聊天对象数据
        return sqlChatHelper.getRoomList(mtime, count);
    }

    /**
     * 获取用户头像
     * @param uid
     * @param handler
     */
    public static void getUserFace(final int uid, final RequestResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("method", "url");
        params.put("uid", uid);
        ApiHttpClient.post(new String[]{"Message", "getUserface"}, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        handler.onFailure(throwable.toString());
                        //重新请求
                        getUserFace(uid, handler);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        handler.onSuccess(response);
                    }
                }

        );
    }


    /**
     * 获取附件地址
     * @param hash
     * @param method
     * @param attach_id
     * @param handler
     */
    public void getAttach(String hash, String method, String attach_id,
                          final RequestResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("hash", hash);
        if(method != null) {
            params.put("method", method);
        }
        if(attach_id != null)
            params.put("logo", attach_id);

        ApiHttpClient.post(new String[]{"Message", "getAttach"}, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                handler.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                handler.onFailure(errorResponse);
            }
        });

    }

    //获取某个房间的详细聊天消息列表
    public void getMeessageByRoom(int room_id, int message_id, final RequestResponseHandler handler) {
        ListData<Entity> list = new ListData<Entity>();
        list = sqlChatHelper.getChatMessageListById(room_id,message_id);
        handler.onSuccess(list);
    }

    /**
     * 获取附件：图片，语音，位置图片地址
     * @param attach_id
     * @param handler
     */
    public void getAttachUrl(String attach_id, final RequestResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("method", "url");
        params.put("hash", attach_id);
        ApiHttpClient.post(new String[]{"Message", "getAttach"}, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        handler.onFailure(throwable.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        handler.onSuccess(response);
                    }
                }

        );
    }

    /**
     * 上传图片消息
     * @param room_id
     * @param imageUrl
     * @param handler
     */
    public static void uploadImageMessage(int room_id, String imageUrl, final RequestResponseHandler handler) {
        RequestParams params = new RequestParams();
        Log.v("Api.uploadFile image", "path=" + imageUrl);
        params.put("list_id", room_id);
        try {
            params.put("file", new File(imageUrl));
            ApiHttpClient.post(new String[]{"Message", "uploadImage"}, params,
                    new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    handler.onFailure(errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.e("MessageApi", "upload image result:" + response.toString());
                    try {
                        if (response.has("status") &&
                                response.getString("status").equals("1")) {
                            // 成功
                            if (response.has("list")) {
                                JSONArray array = response.getJSONArray("list");
                                String attach_id = array.get(0).toString();
                                handler.onSuccess(attach_id);
                                return;
                            }
                        }
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                    handler.onFailure("上传错误");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    handler.onFailure(throwable.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.onFailure("文件不存在");
        }

    }

    /**
     * 上传语音文件
     * @param path          语音地址
     * @param voiceLength  语音时间
     * @param handler
     */
    public static void uploadVoiceMessage(int room_id, String path, int voiceLength,
                                   final RequestResponseHandler handler) {
        RequestParams params = new RequestParams();
        Log.v("Api.uploadFile voice", "path=" + path);
        params.put("list_id", room_id);
        params.put("length", voiceLength);
        try {
            params.put("file", new File(path));
            ApiHttpClient.post(new String[]{"Message", "uploadVoice"}, params, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    handler.onFailure(errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.e("MessageApi", "result:" + response.toString());
                    try {
                        if (response.has("status") &&
                                response.getString("status").equals("1")) {
                            // 成功
                            if (response.has("list")) {
                                JSONArray array = response.getJSONArray("list");
                                String attach_id = array.get(0).toString();
                                handler.onSuccess(attach_id);
                                return;
                            }
                        }
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                    handler.onFailure("上传错误");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    handler.onFailure(throwable.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.onFailure("文件不存在");
        }

    }

    /**
     * 上传群组头像
     * @param path
     * @param room_id
     * @param handle
     */
    public void uploadGroupFace(String path, int room_id, AsyncHttpResponseHandler handle) {
        RequestParams params = new RequestParams();
        try {
            params.put("Filedata", new File(path));
            params.put("room_id", room_id);
            ApiHttpClient.post(new String[]{"Message", "uploadGroupLogo"}, params, handle);
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取用户名片信息
     * @param uid
     * @param handler
     */
    public static void getUserCard(int uid, final RequestResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("method", "url");
        params.put("uid", uid);
        ApiHttpClient.post(new String[]{"Message","getUserInfo"}, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                handler.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(errorResponse != null) {
                    handler.onFailure(errorResponse.toString());
                }else {
                    handler.onFailure("未获取到数据");
                }
            }
        });
    }

    /**
     * 获取聊天成员列表
     * @param room_id
     * @param handler
     */
    public static void getMembers(int room_id, final RequestResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("list_id", room_id);
        ApiHttpClient.post(new String[]{"Message", "get_list_info"}, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                handler.onFailure(errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                handler.onSuccess(response);
            }
        });
    }
}
