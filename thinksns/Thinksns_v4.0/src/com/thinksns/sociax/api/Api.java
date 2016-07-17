package com.thinksns.sociax.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.db.WeiboSqlHelper;
import com.thinksns.sociax.modle.*;
import com.thinksns.sociax.modle.NotifyCount.Type;
import com.thinksns.sociax.net.Get;
import com.thinksns.sociax.net.Post;
import com.thinksns.sociax.net.Request;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.api.*;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.t4.android.img.Bimp;
import com.thinksns.sociax.t4.exception.*;
import com.thinksns.sociax.t4.model.*;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.exception.UserListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.utils.FormFile;
import com.thinksns.sociax.unit.Compress;
import com.thinksns.sociax.unit.FormPost;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;


@SuppressLint("UseValueOf")
public class Api {
    public static final String TAG = "ThinksnsApi";

    public static enum Status {
        REQUESTING, SUCCESS, ERROR, RESULT_ERROR, REQUEST_ENCRYP_KEY
    }

    private static String mHost;// 服务器地址
    private static String mPath;// 方法路径
    private static String url;
    public static Context mContext;
    private static Request post;
    private static Request get;
    private static Api instance;
    private static URI socketServer;// 聊天服务器的地址

    private static final String APP_NAME = "api";

    /**
     * 没有任何站点信息，新建一个以site_url内信息作为url的信息
     *
     * @param context
     */
    private Api(Context context) {
        Api.setContext(context);
        String[] configHost = context.getResources().getStringArray(
                R.array.site_url);
        Api.setHost(configHost[0]);
        Api.setPath(configHost[1]);
        Api.setSocketServer(configHost[2]);
        Api.post = new Post();
        Api.get = new Get();

        Log.v(TAG,
                "-------api1---------" + Api.getHost() + Api.getPath()
                        + Api.getSocketServer());
    }

    private Api(String host, String path, Context context) {
        Api.setContext(context);
        String[] configHost = context.getResources().getStringArray(
                R.array.site_url);
        Api.setHost(configHost[0]);
        Api.setPath(configHost[1]);
        Api.setSocketServer(configHost[2]);
        Api.post = new Post();
        Api.get = new Get();

        Log.v(TAG,
                "-------api2---------" + Api.getHost() + Api.getPath()
                        + Api.getSocketServer());
    }

    /**
     * @param context
     * @param type    标记是否已经有站点 true 则需要使用url[]，false则根据app_init_set重新生成
     * @param url     站点信息[host,past]
     * @return
     */
    public static Api getInstance(Context context, boolean type, String[] url) {
        if (!type) {
            Api.instance = new Api(context);
        } else {
            Api.instance = new Api(url[0], url[1], context);
        }
        return Api.instance;
    }


    /**
     * 简单数据请求地址拼接;采用get方式请求
     *
     * @param url
     * @return
     */
    public static String requestUrl(String url) {
        String request = "http://" + Api.mHost + "/" + Api.mPath + "?";
        request += url;
        request += Api.get.getTokenString();
        return request;
    }

    public static Uri.Builder createUrlBuild(String mod, String act) {
        return createUrlBuild(APP_NAME, mod, act);
    }

    private static Uri.Builder createUrlBuild(String app, String mod, String act) {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority(Api.getHost());
        uri.appendEncodedPath(Api.getPath());
        uri.appendQueryParameter("app", app);
        uri.appendQueryParameter("mod", mod);
        uri.appendQueryParameter("act", act);
        Log.d(TAG, " url " + uri.toString());
        return uri;
    }

    private static Uri.Builder createForCheck(String api, String mod, String act) {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority(Api.getHost());
        uri.appendEncodedPath(Api.getPath());
        uri.appendQueryParameter("app", api);
        uri.appendQueryParameter("mod", mod);
        uri.appendQueryParameter("act", act);
        return uri;
    }

    private static Uri.Builder createThinksnsUrlBuild(String api, String mod,
                                                      String act) {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("t.thinksns.com");
        uri.appendEncodedPath("");
        uri.appendQueryParameter("app", api);
        uri.appendQueryParameter("mod", mod);
        uri.appendQueryParameter("act", act);
        return uri;
    }

    public Object uploadRegisterFace(Bitmap bitmap, File file)
            throws ApiException {
        String temp = "0";
        try {
            Uri.Builder uri = Api.createUrlBuild(Oauth.MOD_NAME,
                    ApiOauth.UPLPAD_FACE);
            FormFile formFile = new FormFile(Compress.compressPic(bitmap),
                    file.getName(), "Filedata", "application/octet-stream");
            Api.post.setUri(uri);
            HashMap<String, String> param = new HashMap<String, String>();
            temp = FormPost.post(uri.toString(), param, formFile);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(AppConstant.APP_TAG,
                    "upload face pic error ..." + e.toString());
        }
        Log.d(AppConstant.APP_TAG, temp);
        return temp;
    }

    public void uploadRegisterUserFace(String path, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        try {
            Log.e("ActivityRegister", "upload file path is " + path);
            params.put("file", new File(path));
            ApiHttpClient.post(new String[]{Oauth.MOD_NAME, ApiOauth.UPLPAD_FACE}, handler);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Object changeFace(Bitmap bitmap, File file) throws ApiException {
        String temp = "0";
        try {
            Uri.Builder uri = Api.createUrlBuild("User", ApiOauth.CHANGE_FACE);
            FormFile formFile = new FormFile(Compress.compressPic(bitmap),
                    file.getName(), "Filedata", "application/octet-stream");
            Api.post.setUri(uri);
            HashMap<String, String> param = new HashMap<String, String>();
            temp = FormPost.post(uri.toString(), param, formFile);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(AppConstant.APP_TAG, "upload face pic error ..." + e.toString());
        }
        Log.d(AppConstant.APP_TAG, temp);

        return temp;
    }

    /**
     * 聊天上传文件
     *
     * @param filePath 文件地址
     * @param type     文件
     * @return
     * @throws ApiException
     */
    public Object uploadFile(String filePath, String type) throws ApiException {
        if (type.equals("voice")) {
            try {
                Api.post = new Post();
                Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                        ApiMessage.UPLPAD_VOICE);
                Api.post.setUri(uri);
                File file = new File(filePath);
                Log.v("Api-->uploadFile voice", file.getName());
                FormFile formFile = new FormFile(new FileInputStream(file),
                        file.getName(), "audio", "application/octet-stream");
                Map<String, String> param = new HashMap<String, String>();
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());
                param.put("from", ModelWeibo.From.ANDROID.ordinal() + "");
                return FormPost.post(uri.toString(), param, formFile);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(AppConstant.APP_TAG,
                        "upload face pic error ..." + e.toString());
            }
        } else if (type.equals("image")) {
            try {
                Api.post = new Post();
                String resultTemp = "";
                File file = new File(filePath);
                Log.v("Api.uploadFile image", "path=" + filePath);
                Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                        ApiMessage.UPLPAD_IMAGE);
                FormFile formFile = new FormFile(Compress.compressPic(file),
                        file.getName(), "Filedata", "application/octet-stream");
                Api.post.setUri(uri);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());
                resultTemp = FormPost.post(uri.toString(), param, formFile);
                return resultTemp;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(AppConstant.APP_TAG,
                        "upload file image error ..." + e.toString());
            }
        }

        return null;
    }

    private static Object run(Request req) throws ApiException {
        try {
            return req.run();
        } catch (ClientProtocolException e) {
            Log.e(AppConstant.APP_TAG, e.toString());
            throw new ApiException(e.getMessage());
        } catch (HostNotFindException e) {
            throw new ApiException("服务请求地址不正确，请联系开发者");
        } catch (IOException e) {
            Log.e(AppConstant.APP_TAG, e.toString());
            throw new ApiException("网络未连接,请检查网络设置");
        }
    }

    private static Status checkResult(Object result) {
        if (result != null && result.equals(Api.Status.ERROR)) {
            return Api.Status.ERROR;
        }
        return Api.Status.SUCCESS;
    }

    public static void checkHasVerifyError(JSONObject result)
            throws VerifyErrorException, ApiException {
        if (result.has("code") && result.has("message")) {
            try {
                throw new VerifyErrorException(result.getString("message"));
            } catch (JSONException e) {
                throw new ApiException("暂无更多数据");
            }
        }
    }

    /**
     * 账号认证Api类
     *
     * @author Povol
     */
    public static final class Oauth implements ApiOauth {
        @SuppressWarnings("unused")
        private String encryptKey;

        @Override
        public void authorize(final String uname, final String password, final HttpResponseListener listener) {
            RequestParams params = new RequestParams();
            params.put("login", uname);
            params.put("password", password);
            ApiHttpClient.post(new String[]{ApiOauth.MOD_NAME, ApiOauth.AUTHORIZE}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                              JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (listener != null) {
                                listener.onError("网络连接失败，请检查您的网络设置");
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.v("Api.authorize", response.toString());

                            Log.v("testRegister", "/authorize json/" +response.toString());

                            try {
                                if (response.getInt("status") == 1) {
                                    //成功获取登录返回信息
                                    String oauth_token = response.getString("oauth_token");
                                    String oauth_token_secret = response.getString("oauth_token_secret");
                                    int uid = response.getInt("uid");
                                    ApiHttpClient.TOKEN = oauth_token;
                                    ApiHttpClient.TOKEN_SECRET = oauth_token_secret;

                                    if (listener != null) {
                                        listener.onSuccess(new ModelUser(uid, uname, password, oauth_token,
                                                oauth_token_secret));
                                    }
                                } else {
                                    if (listener != null) {
                                        listener.onError(response.getString("msg"));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (listener != null)
                                    listener.onError(new String("数据解析错误"));
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                    });
        }

        public void setEmptyKey() {
            this.encryptKey = "";
        }

        @Override
        public Status requestEncrypKey() throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,
                    ApiOauth.REQUEST_ENCRYP);
            Api.post.setUri(uri);
            Object result = Api.run(Api.post);

            Api.checkResult(result);
            try {
                JSONArray encrypt = new JSONArray((String) result);
                this.encryptKey = encrypt.getString(0);
            } catch (JSONException e) {
                return Api.Status.RESULT_ERROR;
            }
            return Api.Status.REQUEST_ENCRYP_KEY;
        }

        @Override
        public Object register(Object data, String... types)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,
                    ApiOauth.REGISTER);
            Post post = new Post();
            post.setUri(uri);
            String[] dataArray = (String[]) data;
            post.append("uname", dataArray[0]);
            post.append("sex", dataArray[2]);
            post.append("password", dataArray[1]);
            Log.d(TAG, types + "," + uri.toString() + "dataArray[0]="
                    + dataArray[0] + "dataArray[1]=" + dataArray[1]
                    + "dataArray[2]=" + dataArray[2] + "dataArray[3]="
                    + dataArray[3] + "dataArray[4]=" + dataArray[4]);
            if (types.length > 0) {
                post.append("avatar_url", dataArray[5]);
                post.append("avatar_width", dataArray[6]);
                post.append("avatar_height", dataArray[7]);
            }
            post.append("phone", dataArray[3]);
            post.append("regCode", dataArray[4]);
            // Object o = Api.run(post); // 成功：1 失败：0 邮箱不合格 2 昵称重复 3
            // // int result = 0;
            // // result = Integer.valueOf(o.toString());
            // Log.d(TAG, "o=" +o.toString());
            Log.d(TAG, "post=" + post.toString());
            return Api.run(post);
        }

        @Override
        public Object signIn(ModelRegister data) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,
                    ApiOauth.SIGN_IN);

            Api.post.setUri(uri);


            Log.v("testRegister", "/url/" + uri.toString() + "&username=" + data.getUsername() + "&sex=" + data.getSex() + "&password=" + data.getPassword() +
                    "&phone=" + data.getPhone() + "&code=" + data.getCode() + "&location=" + data.getLocation() + "&intro=" + data.getIntro() + "&province=" +
                    data.getProvince() + "&city=" + data.getCity() + "&area=" + data.getArea() + "&avatarUrl=" + data.getAvatarUrl() + "&avatarW=" + data.getAvatarW() +
                    "&avatarH=" + data.getAvatarH());

            post.append("username", data.getUsername()); // 用户名
            post.append("sex", data.getSex());
            post.append("password", data.getPassword());
            post.append("phone", data.getPhone());
            post.append("code", data.getCode());
            post.append("location", data.getLocation());
            post.append("intro", data.getIntro() == null ? "" : data.getIntro());
            post.append("province", data.getProvince());
            post.append("city", data.getCity());
            post.append("area", data.getArea());
            post.append("avatarUrl", data.getAvatarUrl());
            post.append("avatarW", data.getAvatarW());
            post.append("avatarH", data.getAvatarH());

            Object result = Api.run(Api.post);
            Log.v("testRegister", "/json/" + result.toString());
            return result;
        }

        /**
         * type type_uid access_token refresh_token
         */
        @Override
        public int thirdRegister(Object data) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,
                    ApiOauth.REGISTER);
            Post post = new Post();
            post.setUri(uri);
            String[] dataArray = (String[]) data;
            post.append("uname", dataArray[0]);
            post.append("sex", dataArray[1]); // (1：男,2：女)
            post.append("email", dataArray[2]);
            post.append("type", dataArray[3]);
            post.append("type_uid", dataArray[4]);
            post.append("access_token", dataArray[5]);
            post.append("refresh_token", dataArray[6]);
            Object o = Api.run(post); // 成功：1 失败：0 邮箱不合格 2 昵称重复 3
            int result = 0;
            result = Integer.valueOf(o.toString());
            return result;
        }

        @Override
        public Object thridLogin(String type, String type_uid)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,
                    ApiOauth.THRID_LOGIN);
            // Post post = new Post();
            // post.setUri(uri);
            // post.append("type", type);
            // post.append("type_uid", type_uid);
            Get post = new Get();
            post.setUri(uri);
            post.append("type", type);
            post.append("type_uid", type_uid);
            return Api.run(post);
        }

        public Object thridT4Login(String type, String type_uid,
                                   String access_token) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,
                    ApiOauth.THRID_LOGIN);
            // Post post = new Post();
            // post.setUri(uri);
            // post.append("type", type);
            // post.append("type_uid", type_uid);
            Get post = new Get();
            post.setUri(uri);
            post.append("type", type);
            post.append("type_uid", type_uid);
            post.append("access_token", access_token);
            System.out.println("xxl  " + uri.toString() + ",type_uid="
                    + type_uid + ",access_token=" + access_token);
            return Api.run(post);
        }

        @Override
        public String getRegisterVerifyCode(String phoneNumber) {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.OAUTH,
                    ApiStatuses.REGISTER_VERIFY);
            Api.post = new Post();
            post.setUri(uri);
            post.append("phone", phoneNumber);

            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
                return e.getExceptionMessage();
            }
        }

        @Override
        public String getFindVerifyCode(String phoneNumber) {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.OAUTH,
                    ApiStatuses.FINBACK_VERIFY);
            Api.post = new Post();
            post.setUri(uri);
            post.append("login", phoneNumber);

            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return "[]";
        }

        public Object oauthRegisterVerifyCode(String phoneNumber,
                                              String oauthNum) {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.OAUTH,
                    ApiStatuses.CHECK_REGISTER_VERIFY);
            Api.post = new Post();
            post.setUri(uri);
            post.append("phone", phoneNumber);
            post.append("regCode", oauthNum);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return "[]";
        }

        public Object oauthFindbackVerifyCode(String phoneNumber,
                                              String oauthNum) {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.OAUTH,
                    ApiStatuses.CHECK_FINDBACK_VERIFY);
            Api.post = new Post();
            post.setUri(uri);
            post.append("login", phoneNumber);
            post.append("code", oauthNum);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return "[]";
        }

        public Object saveNewPwd(String phoneNumber, String pwd, String Code) {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.OAUTH,
                    ApiStatuses.SAVA_USER_PWD);
            Api.post = new Post();
            post.setUri(uri);
            post.append("login", phoneNumber);
            post.append("code", Code);
            post.append("password", pwd);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return "[]";
        }

        /**
         * 获取隐私设置
         *
         * @return
         */
        public Object getPrivacy() {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.PRIVACY,
                    ApiStatuses.GET_PRIVACY);
            Api.post = new Post();
            post.setUri(uri);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return "[]";
        }

        /**
         * 保存隐私设置 [space,comment,message]
         *
         * @return
         */
        public Object savePrivacy(String[] privacy) {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.PRIVACY,
                    ApiStatuses.SAVE_PRIVACY);
            Api.post = new Post();
            post.setUri(uri);
            post.append("space", privacy[0]);
            post.append("comment_weibo", privacy[1]);
            post.append("message", privacy[2]);
            Log.v("API savePrivacy", "wztest" + privacy[0] + privacy[1] + privacy[2]);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return "[]";
        }

        /*********************** wz t4 ********************/

        /**
         * 获取第三方注册情况
         *
         * @param pfinfo String[3] ={platform name,platform id,platform token}
         * @return 返回{pfinfo,returnInfo}把请求串放在0位置，返回串放在1位置，因为下一个handler还需要用到请求穿
         */
        public Object getThirdRegInfo(String[] pfinfo) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME,
                    ApiStatuses.THIDR_REG_INFO);
            Api.post = new Post();
            post.setUri(uri);
            post.append("type", pfinfo[0]);
            post.append("type_uid", pfinfo[1]);
            post.append("access_token", pfinfo[2]);
            try {
//                Log.v("thirdLogin","getThirdRegInfo json/"+Api.run(Api.post).toString());
                return Api.run(Api.post);
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 保存第三方登录之后的注册信息
         *
         * @param type
         * @param type_uid
         * @param access_token
         * @param icon         //用户头像
         * @param sex          //用户性别
         * @param uname
         * @param psw
         * @return
         * @throws ApiException
         */
        public Object setThirdRegInfo(String type, String type_uid,
                                      String access_token, String icon, String sex,
                                      String uname, String psw)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiOauth.MOD_NAME, ApiOauth.BIND_LOGIN);
            Get post = new Get();
            post.setUri(uri);
            post.append("type", type);
            post.append("type_uid", type_uid);
            post.append("access_token", access_token);
            if (icon != null) {
                post.append("other_avatar", icon);
            }
            if (sex != null) {
                post.append("other_sex", sex);
            }
            post.append("uname", uname);
            post.append("password", psw);

            return Api.run(post);
        }

    }

    public static final class StatusesApi implements ApiStatuses {
        @Override
        public ModelWeibo show(int id) throws ApiException,
                WeiboDataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.SHOW);
            Api.get.setUri(uri);
            Api.get.append("id", id);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            try {
                JSONObject data = new JSONObject((String) result);
                Api.checkHasVerifyError(data);
                return new ModelWeibo(new JSONObject((String) result));
            } catch (JSONException e) {
                throw new WeiboDataInvalidException("请求微博不存在");
            }
        }

        @Override
        public boolean destroyWeibo(ModelWeibo weibo) throws ApiException,
                VerifyErrorException, DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.DESTROY);
            Api.post.setUri(uri);
            Api.post.append("id", weibo.getWeiboId());
            Api.post.append("uid", weibo.getUid());
            Object result = Api.run(Api.post);
            Api.checkResult(result);
            if (result.equals("\"false\""))
                return false;
            return true;
        }

        @Override
        public boolean destroyComment(Comment comment) throws ApiException,
                VerifyErrorException, DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.COMMENT_DESTROY);
            Post post = new Post();
            post.setUri(uri);
            post.append("comment_id", comment.getComment_id());
            Object result = Api.run(post);
            Api.checkResult(result);
            System.err.println("comment" + result);
            try {
                Integer temp = new Integer((String) result);
                if (temp == 1) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> search(String key, int count)
                throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.SEARCH);
            Get get = new Get();
            get.setUri(uri);
            get.append("key", key);
            get.append("page", count);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> searchHeader(String key, ModelWeibo item,
                                                 int count) throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.SEARCH);
            Get get = new Get();
            get.append("since_id", item.getWeiboId());
            get.append("key", key);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> searchFooter(String key, ModelWeibo item,
                                                 int count) throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.SEARCH);
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.SEARCH);
            Get get = new Get();
            get.setUri(uri);
            // Api.get.append("max_id", item.getWeiboId());
            get.append("key", key);
            get.append("page", count);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> mentions(int count) throws ApiException,
                VerifyErrorException, ListAreEmptyException,
                DataInvalidException {
            this.beforeTimeline(ApiStatuses.MENTION);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> mentionsHeader(ModelWeibo item, int count)
                throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.MENTION);
            Api.get.append("since_id", item.getWeiboId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> mentionsFooter(ModelWeibo item, int count)
                throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.MENTION);
            Api.get.append("max_id", item.getWeiboId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        // 获取好友微博（第一页）
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> friendsTimeline(int count)
                throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.FRIENDS_TIMELINE);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> friendsHeaderTimeline(ModelWeibo item,
                                                          int count) throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {

            this.beforeTimeline(ApiStatuses.FRIENDS_TIMELINE);
            Api.get.append("since_id", item.getWeiboId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        // 获取好友微博（加载更多）
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> friendsFooterTimeline(ModelWeibo item,
                                                          int count) throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.FRIENDS_TIMELINE);
            Api.get.append("max_id", item.getWeiboId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<ModelWeibo> publicTimeline(int count)
                throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.PUBLIC_TIMELINE);
            return (ListData<ModelWeibo>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<ModelWeibo> publicHeaderTimeline(ModelWeibo item,
                                                         int count) throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.PUBLIC_TIMELINE);
            Api.get.append("since_id", item.getWeiboId());
            return (ListData<ModelWeibo>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<ModelWeibo> publicFooterTimeline(ModelWeibo item,
                                                         int count) throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.PUBLIC_TIMELINE);
            Api.get.append("max_id", item.getWeiboId());
            return (ListData<ModelWeibo>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> userTimeline(ModelUser user, int count)
                throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.USER_TIMELINE);
            Get get = new Get();
            get.setUri(uri);
            get.append("user_id", user.getUid());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> userHeaderTimeline(ModelUser user,
                                                       ModelWeibo item, int count) throws ApiException,
                VerifyErrorException, ListAreEmptyException,
                DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.USER_TIMELINE);
            Get get = new Get();
            get.setUri(uri);
            get.append("since_id", item.getWeiboId());
            get.append("user_id", user.getUid());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> userFooterTimeline(ModelUser user,
                                                       ModelWeibo item, int count) throws ApiException,
                VerifyErrorException, ListAreEmptyException,
                DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.USER_TIMELINE);
            Get get = new Get();
            get.setUri(uri);
            get.append("max_id", item.getWeiboId());
            get.append("user_id", user.getUid());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<Comment> commentTimeline(int count)
                throws ApiException, DataInvalidException,
                VerifyErrorException, ListAreEmptyException {
            this.beforeTimeline(ApiStatuses.COMMENT_TIMELINE);
            return (ListData<Comment>) this.afterTimeLine(count,
                    ListData.DataType.COMMENT);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<Comment> commentHeaderTimeline(Comment item, int count)
                throws ApiException, DataInvalidException,
                VerifyErrorException, ListAreEmptyException {
            this.beforeTimeline(ApiStatuses.COMMENT_TIMELINE);
            Api.get.append("since_id", item.getComment_id());
            return (ListData<Comment>) this.afterTimeLine(count,
                    ListData.DataType.COMMENT);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<Comment> commentFooterTimeline(Comment item, int count)
                throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.COMMENT_TIMELINE);
            Api.get.append("max_id", item.getComment_id());
            return (ListData<Comment>) this.afterTimeLine(count,
                    ListData.DataType.COMMENT);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentMyTimeline(int count)
                throws ApiException, DataInvalidException,
                VerifyErrorException, ListAreEmptyException {
            this.beforeTimeline(ApiStatuses.COMMENT_BY_ME);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.RECEIVE);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentMyHeaderTimeline(Comment item,
                                                            int count) throws ApiException, DataInvalidException,
                VerifyErrorException, ListAreEmptyException {
            this.beforeTimeline(ApiStatuses.COMMENT_BY_ME);
            Api.get.append("since_id", item.getComment_id());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.RECEIVE);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentMyFooterTimeline(Comment item,
                                                            int count) throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            this.beforeTimeline(ApiStatuses.COMMENT_BY_ME);
            Api.get.append("max_id", item.getComment_id());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.RECEIVE);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentForWeiboTimeline(ModelWeibo item,
                                                            int count) throws ApiException, DataInvalidException,
                VerifyErrorException, ListAreEmptyException {
            Get get = new Get();
            get.setUri(Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.COMMENTS));
            get.append("feed_id", item.getWeiboId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.COMMENT, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentForWeiboHeaderTimeline(
                ModelWeibo item, Comment comment, int count)
                throws ApiException, DataInvalidException,
                VerifyErrorException, ListAreEmptyException {
            Get get = new Get();
            get.setUri(Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.COMMENTS));
            get.append("since_id", comment.getComment_id());
            get.append("id", item.getWeiboId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.COMMENT, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentForWeiboFooterTimeline(
                ModelWeibo item, Comment comment, int count)
                throws ApiException, VerifyErrorException,
                ListAreEmptyException, DataInvalidException {
            Get get = new Get();
            get.setUri(Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.COMMENTS));
            get.append("max_id", comment.getComment_id());
            get.append("id", item.getWeiboId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.COMMENT, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentReceiveMyTimeline(int count)
                throws ApiException, DataInvalidException,
                VerifyErrorException, ListAreEmptyException {
            this.beforeTimeline(ApiStatuses.COMMENT_RECEIVE_ME);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.RECEIVE);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentReceiveMyHeaderTimeline(
                Comment item, int count) throws ApiException,
                DataInvalidException, VerifyErrorException,
                ListAreEmptyException {
            this.beforeTimeline(ApiStatuses.COMMENT_RECEIVE_ME);
            Api.get.append("since_id", item.getComment_id());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.RECEIVE);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentReceiveMyFooterTimeline(
                Comment item, int count) throws ApiException,
                VerifyErrorException, ListAreEmptyException,
                DataInvalidException {
            this.beforeTimeline(ApiStatuses.COMMENT_RECEIVE_ME);
            Api.get.append("max_id", item.getComment_id());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.RECEIVE);
        }

        /**
         * following 关注
         */
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> following(ModelUser user, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOOLOWING);
            Log.e("uri", "uri+" + uri.toString());
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> followingHeader(ModelUser user,
                                                    Follow firstUser, int count) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOOLOWING);
            Log.e("uri", "uri+" + uri.toString());
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            Api.get.append("since_id", firstUser.getFollowId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> followingFooter(ModelUser user,
                                                    Follow lastUser, int count) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOOLOWING);
            Log.e("uri", "uri+" + uri.toString());
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            Api.get.append("max_id", lastUser.getFollowId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        /**
         * follower粉丝
         */
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> followers(ModelUser user, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOLLOWERS);
            Log.e("uri", "uri+" + uri.toString());
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> followersHeader(ModelUser user,
                                                    Follow firstUser, int count) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOLLOWERS);
            Log.e("uri", "uri+" + uri.toString());
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            Api.get.append("since_id", firstUser.getFollowId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> followersFooter(ModelUser user,
                                                    Follow lastUser, int count) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOLLOWERS);
            Log.e("uri", "uri+" + uri.toString());
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            Api.get.append("max_id", lastUser.getFollowId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        /**
         * 互相关注
         */
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> followEach(ModelUser user, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOLLOWEACH);
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> followEachHeader(ModelUser user,
                                                     Follow firstUser, int count) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOLLOWEACH);
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            Api.get.append("since_id", firstUser.getFollowId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> followEachFooter(ModelUser user,
                                                     Follow lastUser, int count) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FOLLOWEACH);
            Log.e("uri", "uri+" + uri.toString());
            Api.get.setUri(uri);
            Api.get.append("user_id", user.getUid());
            Api.get.append("max_id", lastUser.getFollowId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.FOLLOW);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> searchUser(String key, int count, int page)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.SEARCH_USER);
            Get get = new Get();
            get.setUri(uri);
            get.append("key", key);
            get.append("page", page);
            return (ListData<SociaxItem>) afterTimeLine(count,
                    ListData.DataType.SEARCH_USER, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> searchHeaderUser(String user,
                                                     ModelUser firstUser, int count, int page) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.SEARCH_USER);
            Get get = new Get();
            get.setUri(uri);
            get.append("key", user); // 关键字
            get.append("page", page);
            // get.append("key", user);
            // get.append("since_id", firstUser.getUid());
            return (ListData<SociaxItem>) afterTimeLine(count,
                    ListData.DataType.SEARCH_USER, get);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> searchFooterUser(String user,
                                                     ModelUser lastUser, int count, int page) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            this.beforeTimeline(ApiStatuses.SEARCH_USER);
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.SEARCH_USER);
            Get get = new Get();
            get.setUri(uri);
            get.append("key", user); // 关键字
            get.append("page", page);
            // get.append("key", user);
            // get.append("max_id", lastUser.getUid());
            return (ListData<SociaxItem>) afterTimeLine(count,
                    ListData.DataType.SEARCH_USER, get);
        }

        @Override
        public int update(ModelWeibo weibo) throws ApiException,
                VerifyErrorException, UpdateException {
            if (weibo.isNullForContent())
                throw new UpdateContentEmptyException();
            if (!weibo.checkContent())
                throw new UpdateContentBigException();

            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.UPDATE);
            Api.post.setUri(uri);
            Api.post.append("content", weibo.getContent());
            Api.post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
            Object result = Api.run(Api.post);
            Api.checkResult(result);
            String data = (String) result;
            if (data.equals("false"))
                throw new UpdateException();
            if (data.indexOf("{") != -1 || data.indexOf("[") != -1) {
                try {
                    JSONObject tempData = new JSONObject(data);
                    Api.checkHasVerifyError(tempData);
                } catch (JSONException e) {
                    throw new ApiException();
                }
            }
            return Integer.parseInt(data);
        }

        @Override
        public ModelBackMessage createNewTextWeibo(ModelWeibo weibo)
                throws ApiException, VerifyErrorException, UpdateException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.CREATE_TEXT_WEIBO);
            Api.post.setUri(uri);
            Api.post.append("content", weibo.getContent());
            Api.post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
            if (weibo.getType() != null) {
                Api.post.append("channel_category_id", weibo.getType());
            }

            Object result = Api.run(Api.post);
            Api.checkResult(result);
            String data = (String) result;
            ModelBackMessage message;
            try {
                message = new ModelBackMessage(data);
                return message;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public ModelBackMessage createNewTextWeibo(ModelWeibo weibo, double longitude, double latitude, String address)
                throws ApiException, VerifyErrorException, UpdateException {
            if (weibo.isNullForContent())
                throw new UpdateContentEmptyException();
            if (!weibo.checkContent())
                throw new UpdateContentBigException();
            String tips = null;
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.CREATE_TEXT_WEIBO);
            Api.post.setUri(uri);
            Api.post.append("content", weibo.getContent());
            Api.post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
            Api.post.append("latitude", latitude);
            Api.post.append("longitude", longitude);
            Api.post.append("address", address);
            if (weibo.getType() != null) {
                Api.post.append("channel_category_id", weibo.getType());
            }
            Object result = Api.run(Api.post);
            Api.checkResult(result);
            String data = (String) result;
            ModelBackMessage message;
            try {
                message = new ModelBackMessage(data);
                if (message.getStatus() == 1
                        && !TextUtils.isEmpty(tips)) {
                    message.setMsg(tips);
                }
                return message;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        public int comment(Comment comment) throws ApiException,
                VerifyErrorException, UpdateException, DataInvalidException,
                JSONException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.COMMENT);
            comment.checkCommentCanAdd();
            Get post = new Get();
            post.setUri(uri);
            post.append("content", comment.getContent())
                    .append("feed_id", comment.getStatus().getWeiboId() + "")
                    .append("from", ModelWeibo.From.ANDROID.ordinal() + "");

            if (comment.getReplyCommentId() != -1) {
                post.append("to_comment_id", comment.getReplyCommentId());
            }
            if (comment.getAppName() != null)
                post.append("git", comment.getAppName());

            Object result = Api.run(post);
            Log.d(TAG, "result=" + result.toString());
            Api.checkResult(result);
            String data = (String) result;
            ModelBackMessage message = new ModelBackMessage(data);
            int resultConde = 0;
            try {
                resultConde = message.getStatus();
            } catch (Exception e) {
                Log.d(AppConstant.APP_TAG, "发送评论出错  wm " + e.toString());
                return 0;
            }
            return resultConde;
        }

        /**
         * 评论微博
         *
         * @param content
         * @param weibo_id
         * @param to_commentId
         * @return
         * @throws ApiException
         */
        public Object commentWeibo(String content, int weibo_id, int to_commentId) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.COMMENT);
            Get post = new Get();
            post.setUri(uri);
            post.append("content", content)
                    .append("feed_id", weibo_id + "")
                    .append("from", ModelWeibo.From.ANDROID.ordinal() + "");

            if (to_commentId != 0) {
                post.append("to_comment_id", to_commentId);
            }

            return Api.run(post);

        }

        @Override
        public ModelBackMessage transpond(Comment comment) throws ApiException,
                VerifyErrorException, UpdateException, DataInvalidException,
                JSONException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.REPOST);
            comment.checkCommentCanAdd();

            Get post = new Get();
            post.setUri(uri);

            post.append("content", comment.getContent())
                    .append("feed_id", comment.getStatus().getWeiboId() + "")
                    .append("from", ModelWeibo.From.ANDROID.ordinal() + "");

            if (comment.getAppName() != null)
                post.append("git", comment.getAppName());

            Object result = Api.run(post);
            Api.checkResult(result);
            String data = (String) result;
            ModelBackMessage message = new ModelBackMessage(data);
            return message;
            // return resultConde >= 1 ? true : false;
        }

        public ModelBackMessage transpond(int feed_id, String content) throws ApiException,
                VerifyErrorException, UpdateException, DataInvalidException,
                JSONException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.REPOST);
            Get post = new Get();
            post.setUri(uri);
            post.append("content", content)
                    .append("feed_id", feed_id + "")
                    .append("from", ModelWeibo.From.ANDROID.ordinal() + "");

            Object result = Api.run(post);
            Api.checkResult(result);
            String data = (String) result;
            ModelBackMessage message = new ModelBackMessage(data);
            return message;
        }

        /**
         * 转发帖子
         * @param feed_id
         * @param content
         * @return
         * @throws ApiException
         * @throws VerifyErrorException
         * @throws UpdateException
         * @throws DataInvalidException
         * @throws JSONException
         */
        public ModelBackMessage transpondPost(int feed_id, String content) throws ApiException,
                VerifyErrorException, UpdateException, DataInvalidException,
                JSONException {
            Uri.Builder uri = Api.createUrlBuild("Weiba",
                    ApiStatuses.REPOST_WEIBA);
            Get post = new Get();
            post.setUri(uri);
            post.append("content", content)
                    .append("post_id", feed_id)
                    .append("from", ModelWeibo.From.ANDROID.ordinal());
            post.append("ifShareFeed", 1);
            Object result = Api.run(post);
            Api.checkResult(result);
            String data = (String) result;
            ModelBackMessage message = new ModelBackMessage(data);
            return message;
        }

        @Override
        public boolean upload(ModelWeibo weibo, File file) throws ApiException,
                VerifyErrorException, UpdateException {
            String result = null;
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                        ApiStatuses.UPLOAD);

                // File file1 = new File("/sdcard","test.jpg");
                // byte [] content = readFileImage(file1);
                // ByteArrayInputStream byteIn = new
                // ByteArrayInputStream(content);
                // FormFile formFile = new FormFile(byteIn
                // ,file1.getName(),"pic","application/octet-stream") ;

                Post post = new Post();
                post.setUri(uri);
                FormFile formFile = new FormFile(Compress.compressPic(file),
                        file.getName(), "pic", "application/octet-stream");
                // Api.post.setUri(uri);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("content", weibo.getContent());
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());
                param.put("from", ModelWeibo.From.ANDROID.ordinal() + "");
                result = FormPost.post(uri.toString(), param, formFile);
            } catch (FileNotFoundException e) {
                throw new UpdateException("file not found!");
            } catch (IOException e) {
                Log.d(TAG, "upload weibo " + e.toString());
                throw new UpdateException("file upload faild");
            }
            try {
                Api.checkHasVerifyError(new JSONObject(result));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Integer.parseInt(result) > 0;
        }

        @Override
        public boolean repost(ModelWeibo weibo, boolean comment)
                throws ApiException, VerifyErrorException, UpdateException,
                DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.REPOST);
            Get post = new Get();
            post.setUri(uri);

            System.err.println(" transpond "
                    + weibo.getSourceWeibo().isNullForTranspond());
            if (weibo.getSourceWeibo() != null) {
                if (weibo.getSourceWeibo().getType().equals("weiba_post")) {
                    post.append("app_name", "weiba");
                    post.append("type", "weiba_post");
                    post.append("id", weibo.getSourceWeibo().getPosts()
                            .getPostId()
                            + "");
                } else if (weibo.getSourceWeibo().getType()
                        .equals("weiba_repost")) {
                    post.append("type", "weiba_post");
                    post.append("app_name", "weiba");
                    post.append("id", weibo.getSourceWeibo().getPosts()
                            .getPostId()
                            + "");
                } else if (weibo.getType().equals("weiba_repost")) {
                    post.append("type", "weiba_post");
                    post.append("app_name", "weiba");
                    post.append("id", weibo.getPosts().getPostId() + "");
                } else {
                    post.append("id", weibo.getSourceWeibo().getWeiboId() + "");
                }
            }

            // if (weibo.getTranspond().isNullForTranspond()) {
            // post.append("id", weibo.getTranspond().getWeiboId() + "");
            // } else {
            // post.append("id", weibo.getTranspond().getTranspondId() + "");
            // }

            post.append("content", weibo.getContent());
            post.append("curid", weibo.getSourceWeibo().getWeiboId() + ""); // 当前微博id

            if (comment) {
                post.append("comment", 1);
            } else {
                post.append("comment", 0);
            }
            post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
            Object result = Api.run(post);

            Log.d("api---> repost", result.toString());
            Api.checkResult(result);
            return Integer.valueOf((String) result) > 0 ? true : false;
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME, act);
            Log.e("uri", "uri+" + uri.toString());
            Api.get.setUri(uri);
        }

        private ListData<?> afterTimeLine(int count, ListData.DataType type)
                throws ApiException, ListAreEmptyException,
                VerifyErrorException, DataInvalidException {
            Api.get.append("count", count);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            if (type == ListData.DataType.COMMENT
                    || type == ListData.DataType.RECEIVE) {
                if (result.equals("null"))
                    throw new CommentListAreEmptyException();
            } else if (type == ListData.DataType.WEIBO
                    || type == ListData.DataType.MODEL_CHANNEL) {
                if (result.equals("null"))
                    throw new WeiBoListAreEmptyException();
            } else if (type == ListData.DataType.USER
                    || type == ListData.DataType.FOLLOW
                    || type == ListData.DataType.SEARCH_USER) {
                if (result.equals("null"))
                    throw new UserListAreEmptyException();
            }
            try {
                String str = ((String) result);
                JSONArray data = new JSONArray(str);
                int length = data.length();
                ListData<SociaxItem> list = new ListData<SociaxItem>();
                for (int i = 0; i < length; i++) {
                    JSONObject itemData = data.getJSONObject(i);
                    try {
                        SociaxItem weiboData = getSociaxItem(type, itemData);
                        list.add(weiboData);
                    } catch (DataInvalidException e) {
                        Log.e(TAG, "json error wm :" + e.toString());
                        Log.e(TAG,
                                "has one invalid item with string:"
                                        + data.getString(i));
                        continue;
                    }
                }
                return list;
            } catch (JSONException e) { // 检查返回值，如果是一个JSONObject,则进行一次验证看看是否是验证失败得提示信息
                try {
                    JSONObject data = new JSONObject((String) result);
                    Api.checkHasVerifyError(data);
                    throw new CommentListAreEmptyException();
                } catch (JSONException e1) {
                    Log.e(AppConstant.APP_TAG,
                            "comment json 解析 错误  wm " + e.toString());
                    throw new ApiException("无效的数据格式");
                }
            }
        }

        private ListData<?> afterTimeLine(int count, ListData.DataType type,
                                          Get get) throws ApiException, ListAreEmptyException,
                VerifyErrorException, DataInvalidException {
            // get.append("count", count);
            Object result = Api.run(get);
            Api.checkResult(result);
            if (type == ListData.DataType.COMMENT
                    || type == ListData.DataType.RECEIVE) {
                if (result.equals("null"))
                    throw new CommentListAreEmptyException();
            } else if (type == ListData.DataType.WEIBO) {
                if (result.equals("null"))
                    throw new WeiBoListAreEmptyException();
            } else if (type == ListData.DataType.USER
                    || type == ListData.DataType.FOLLOW
                    || type == ListData.DataType.SEARCH_USER) {
                if (result.equals("null"))
                    throw new UserListAreEmptyException();
            }
            try {
                JSONArray data = new JSONArray((String) result);
                int length = data.length();
                ListData<SociaxItem> list = new ListData<SociaxItem>();
                for (int i = 0; i < length; i++) {
                    JSONObject itemData = data.getJSONObject(i);
                    try {
                        SociaxItem weiboData = getSociaxItem(type, itemData);
                        // if(!weiboData.checkValid()) continue;
                        list.add(weiboData);
                    } catch (DataInvalidException e) {
                        Log.e(TAG, "json error wm :" + e.toString());
                        Log.e(TAG,
                                "has one invalid item with string:"
                                        + data.getString(i));
                        continue;
                    }
                }
                return list;
            } catch (JSONException e) { // 检查返回值，如果是一个JSONObject,则进行一次验证看看是否是验证失败得提示信息
                try {
                    JSONObject data = new JSONObject((String) result);
                    Api.checkHasVerifyError(data);
                    throw new CommentListAreEmptyException();
                } catch (JSONException e1) {
                    Log.e(AppConstant.APP_TAG,
                            "comment json 解析 错误  wm " + e.toString());
                    throw new ApiException("无效的数据格式");
                }
            }
        }

        @Override
        public int unRead() throws ApiException, VerifyErrorException,
                DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.UN_READ);
            Api.get.setUri(uri);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            String data = (String) result;
            // if(data.equals("false")) throw new UpdateException();
            if (data.indexOf("{") != -1 || data.indexOf("[") != -1) {
                try {
                    JSONObject tempData = new JSONObject(data);
                    Api.checkHasVerifyError(tempData);
                } catch (JSONException e) {
                    throw new ApiException();
                }
            }
            return Integer.parseInt(data);
        }

        @Override
        public ListData<SociaxItem> getWeiboPhoto(int uid, int count, int page)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.WEIBO_PHOTO);
            Get get = new Get();
            get.setUri(uri);
            get.append("uid", uid);
            get.append("count", count);
            get.append("page", page);
            Object o = Api.run(get);
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            try {
                JSONArray array = new JSONArray(o.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject temp = array.getJSONObject(i);
                    StringItem si = new StringItem();
                    si.setName(temp.getString("body"));
                    si.setUrl(temp.getString("savepath"));
                    list.add(si);
                }
            } catch (JSONException e) {
                Log.d(TAG, "get weibo photo error " + e.toString());
            }
            return list;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getTopicWeiboList(String key, int page)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.SEARCH);
            Get get = new Get();
            get.setUri(uri);
            get.append("key", key);
            get.append("page", page);
            try {
                return (ListData<SociaxItem>) this.afterTimeLine(0,
                        ListData.DataType.WEIBO, get);
            } catch (VerifyErrorException e) {
                return null;
            } catch (ListAreEmptyException e) {
                return null;
            } catch (DataInvalidException e) {
                return null;
            }
        }

        @Override
        public int addDig(int feedId) throws ApiException, JSONException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.ADD_DIGG);
            Get get = new Get();
            get.setUri(uri);
            get.append("feed_id", feedId);
            String result = (String) Api.run(get);
            Log.v("Api", "addDig-->" + result);
            JSONObject jo = new JSONObject(result);
            return jo.getInt("status");
        }

        @Override
        public int delDigg(int feedId) throws ApiException, JSONException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.DEL_DIG);
            Get get = new Get();
            get.setUri(uri);
            get.append("feed_id", feedId);
            String result = (String) Api.run(get);
            JSONObject jo = new JSONObject(result);
            return jo.getInt("status");
        }

        @Override
        public Object getDiggList(int feedId, int max_id) throws ApiException,
                JSONException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.DIG_LIST);
            Get get = new Get();
            get.setUri(uri);
            get.append("max_id", max_id);
            get.append("feed_id", feedId);
            String result = (String) Api.run(get);
            return result;
        }

        @Override
        public ModelBackMessage favWeibo(ModelWeibo weibo, final HttpResponseListener listener) throws ApiException,
                JSONException {
            RequestParams params = new RequestParams();
            params.put("feed_id", weibo.getWeiboId());
            String[] mod_act = null;
            if (!weibo.isFavorited()) {
                mod_act = new String[]{ApiStatuses.MOD_NAME, ApiStatuses.FAVORITE};
            } else {
                mod_act = new String[]{ApiStatuses.MOD_NAME, ApiStatuses.UNFAVORITE};
            }
            ApiHttpClient.get(mod_act, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (listener != null) {
                        if (throwable instanceof ConnectTimeoutException)
                            listener.onError("网络连接超时...");
                        else if (throwable instanceof UnknownHostException) {
                            listener.onError("服务器连接失败");
                        }
                    }
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (listener != null) {
                        listener.onSuccess(response);
                    }
                    super.onSuccess(statusCode, headers, response);
                }

            });
            return null;
        }

        @Override
        public ModelBackMessage unFavWeibo(ModelWeibo weibo, final HttpResponseListener listener)
                throws ApiException, JSONException {
            return favWeibo(weibo, listener);
        }

        @Override
        public ModelBackMessage deleteWeibo(ModelWeibo weibo)
                throws ApiException, JSONException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME, ApiStatuses.DELETE);
            Get get = new Get();
            get.setUri(uri);
            get.append("feed_id", weibo.getWeiboId());
            ModelBackMessage message = new ModelBackMessage((String) Api.run(get));

            return message;
        }

        @Override
        public ModelWeibo getWeiboById(int id) throws ApiException,
                JSONException, WeiboDataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.WEIBO_DETAIL);
            Get get = new Get();
            get.setUri(uri);
            get.append("feed_id", id);
            String jsonString = (String) Api.run(get);

            ModelWeibo weibo = new ModelWeibo(new JSONObject(jsonString));
            return weibo;
        }

        @Override
        public ModelBackMessage denounceWeibo(int id, String reason)
                throws ApiException, JSONException {
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                    ApiStatuses.DENOUNCE);
            Get get = new Get();
            get.setUri(uri);
            get.append("feed_id", id);
            get.append("reason", reason);
            ModelBackMessage backMsg = new ModelBackMessage(
                    (String) Api.run(get));
            return backMsg;
        }

        //举报帖子
        public ModelBackMessage denouncePost(int id, String reason) throws ApiException, JSONException{
            Uri.Builder uri = Api.createUrlBuild("Weiba", DENOUNCE_POST);
            Post post = new Post();
            post.setUri(uri);
            post.append("post_id", id);
            post.append("reason", reason);
            ModelBackMessage backMsg = new ModelBackMessage(
                    (String) Api.run(post));
            return backMsg;
        }

        // 获取频道第一页数据
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> channelTimeline(int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiStatuses.CHANNEL_TIMELINE);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        // 获取个人主页微博
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> userTimeline(int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiStatuses.USER_TIMELINE);
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> channelFooterTimeline(ModelWeibo item,
                                                          int count) throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiStatuses.CHANNEL_TIMELINE);
            Api.get.append("max_id", item.getWeiboId());
            return (ListData<SociaxItem>) this.afterTimeLine(count,
                    ListData.DataType.WEIBO);
        }

        @Override
        public ModelBackMessage createNewImageWeibo(ModelWeibo weibo, FormFile[] filelist)
                throws ApiException, VerifyErrorException,
                UpdateException {
            String result = null;
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                        ApiStatuses.CREATE_IMAGE_WEIBO);
                Post post = new Post();
                post.setUri(uri);

                HashMap<String, String> param = new HashMap<String, String>();
                param.put("content", weibo.getContent());
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());
                if(TextUtils.isEmpty(weibo.getFrom()))
                    param.put("from", ModelWeibo.From.ANDROID.ordinal() + "");
                else
                    param.put("from", weibo.getFrom());
                if (weibo.getAddress() != null && weibo.getLongitude() != null
                        && weibo.getLatitude() != null) {
                    param.put("latitude", weibo.getLatitude());
                    param.put("longitude", weibo.getLongitude());
                    param.put("address", weibo.getAddress());
                }
                if (weibo.getType() != null) {
                    param.put("channel_category_id", weibo.getType());
                }

                Log.v("ActivityCreateWeibo",
                        "channel_category_id 3=" + weibo.getType());
                result = FormPost.postMultilPic(uri.toString(), param, filelist);
                ModelBackMessage message = new ModelBackMessage(result);
                return message;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public ModelBackMessage changeWeiboDigg(int feedId, int prestatus)
                throws ApiException, JSONException {
            Uri.Builder uri;
            if (prestatus == 0) {
                uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                        ApiStatuses.ADD_DIGG);
            } else {
                uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                        ApiStatuses.DEL_DIG);
            }
            post = new Post();
            post.setUri(uri);
            post.append("feed_id", feedId);
            String result;
            try {
                result = (String) Api.run(post);
                ModelBackMessage message = new ModelBackMessage(result);
                return message;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public ModelBackMessage createNewVideoWeibo(ModelWeibo weibo,
                                                    Bitmap file1, File file2) throws ApiException,
                VerifyErrorException, UpdateException {
            String result = null;
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiStatuses.MOD_NAME,
                        ApiStatuses.CREATE_VIDEO_WEIBO);
                Get post = new Get();
                post.setUri(uri);
                FormFile formFile = null;
                if (file1 != null) {
                    formFile = new FormFile(Compress.compressPic(file1),
                            "avatar.png", "pic", "application/octet-stream");
                }
                FormFile videoFile = new FormFile(new FileInputStream(file2),
                        file2.getName(), "video", "application/octet-stream");
                Map<String, String> param = new HashMap<String, String>();
                param.put("content", weibo.getContent());
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());
                param.put("timeline", weibo.getTimeLine() + "");
                param.put("from", ModelWeibo.From.ANDROID.ordinal() + "");
                if (weibo.getAddress() != null && weibo.getLongitude() != null
                        && weibo.getLatitude() != null) {
                    param.put("latitude", weibo.getLatitude());
                    param.put("longitude", weibo.getLongitude());
                    param.put("address", weibo.getAddress());
                }
//                if (weibo.getType() != null) {
//                    param.put("channel_category_id", weibo.getType());
//                }
                Log.v("ActivityCreateWeibo",
                        "channel_category_id 4=" + weibo.getType());
                result = FormPost.post(uri.toString(), param, new FormFile[]{
                        formFile, videoFile});
            } catch (FileNotFoundException e) {
                throw new ApiException("file not found!");
            } catch (IOException e) {
                Log.d(TAG, "upload weibo " + e.toString());
                throw new ApiException("file upload faild");
            }
            System.err.println("result" + result);
            ModelBackMessage message = null;
            try {
                message = new ModelBackMessage(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return message;
        }
    }

    /**
     * @author Administrator
     */
    public static final class Message implements ApiMessage {

        @Override
        public ListData<SociaxItem> inbox(int count) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Get get = new Get();
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.BOX);
            get.setUri(uri);
            get.append("count", count);
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            this.getMessageList(list, true, get);
            return list;
        }

        @Override
        public ListData<SociaxItem> inboxHeader(
                com.thinksns.sociax.modle.Message message, int count, int page)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Get get = new Get();
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.BOX);
            get.setUri(uri);
            get.append("count", count);
            get.append("page", page);
            // Api.get.append("since_id", message.getListId());
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            this.getMessageList(list, true, get);
            return list;
        }

        @Override
        public ListData<SociaxItem> inboxFooter(
                com.thinksns.sociax.modle.Message message, int count, int page)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Get get = new Get();
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.BOX);
            get.setUri(uri);
            get.append("count", count);
            get.append("page", page);
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            this.getMessageList(list, true, get);
            return list;
        }

        @Override
        public ListData<SociaxItem> outbox(
                com.thinksns.sociax.modle.Message message, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiMessage.SHOW);
            Api.get.append("id", message.getListId());
            Api.get.append("count", count);
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            this.getMessageList(list, false, Api.get);
            return list;
        }

        @Override
        public ListData<SociaxItem> outboxHeader(
                com.thinksns.sociax.modle.Message message, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiMessage.SHOW);
            Api.get.append("count", count);
            Api.get.append("id", message.getListId());
            Api.get.append("since_id", message.getMeesageId());
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            this.getMessageList(list, false, Api.get);
            return list;
        }

        @Override
        public ListData<SociaxItem> outboxFooter(
                com.thinksns.sociax.modle.Message message, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiMessage.SHOW);
            Api.get.append("count", count);
            Api.get.append("id", message.getListId());
            Api.get.append("max_id", message.getMeesageId());
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            this.getMessageList(list, false, "footer");
            return list;
        }

        @Override
        public com.thinksns.sociax.modle.Message show(
                com.thinksns.sociax.modle.Message message)
                throws ApiException, DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiMessage.SHOW);
            Api.get.append("id", message.getListId());
            Api.get.append("show_cascade", 0);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            try {
                JSONObject data = new JSONObject((String) result);
                Api.checkHasVerifyError(data);
                return new com.thinksns.sociax.modle.Message(data);
            } catch (JSONException e) {
                // throw new ApiException();
            }
            return null;
        }

        private void getMessageList(ListData<SociaxItem> list, boolean type,
                                    Request req) throws DataInvalidException, VerifyErrorException,
                ApiException {
            Object result = Api.run(req);
            Api.checkResult(result);
            try {
                JSONArray data = new JSONArray((String) result);
                int length = data.length();
                com.thinksns.sociax.modle.Message mainMessage = null;
                for (int i = 0; i < length; i++) {
                    if (type) {
                        mainMessage = new com.thinksns.sociax.modle.Message(
                                data.getJSONObject(i));
                    } else {
                        mainMessage = new com.thinksns.sociax.modle.Message(
                                data.getJSONObject(i), false);
                    }
                    // if(!tempData.checkValid()) continue;
                    list.add(mainMessage);
                }
            } catch (JSONException e) {
                JSONObject data;
                try {
                    data = new JSONObject((String) result);
                    Api.checkHasVerifyError(data);
                    throw new ApiException();
                } catch (JSONException e1) {
                    throw new ApiException();
                }
            }
        }

        private void getMessageList(ListData<SociaxItem> list, boolean type,
                                    String tag) throws DataInvalidException, VerifyErrorException,
                ApiException {
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            try {
                JSONArray data = new JSONArray((String) result);
                int length = data.length();
                com.thinksns.sociax.modle.Message mainMessage = null;
                for (int i = 0; i < length; i++) {
                    if (type) {
                        mainMessage = new com.thinksns.sociax.modle.Message(
                                data.getJSONObject(i));
                    } else {
                        mainMessage = new com.thinksns.sociax.modle.Message(
                                data.getJSONObject(i), false);
                    }
                    // if(!tempData.checkValid()) continue;
                    list.add(mainMessage);
                }
            } catch (JSONException e) {
                JSONObject data;
                try {
                    data = new JSONObject((String) result);
                    Api.checkHasVerifyError(data);

                } catch (JSONException e1) {
                    Log.e(AppConstant.APP_TAG,
                            "api =====> get message footer wm " + e1.toString());
                }
            }
        }

        @Override
        public boolean createNew(com.thinksns.sociax.modle.Message message)
                throws ApiException, DataInvalidException, VerifyErrorException {
            // message.checkMessageCanAdd();
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.CREATE);
            Api.post.setUri(uri);
            Api.post.append("to_uid", message.getTo_uid());
            Api.post.append("content", message.getContent());
            Object result = Api.run(Api.post);
            Api.checkResult(result);
            if (result.equals("\"false\"") || result.equals("\"0\""))
                return false;
            return true;
        }

        @SuppressWarnings("unused")
        @Override
        public void show(com.thinksns.sociax.modle.Message message,
                         ListData<SociaxItem> list) throws ApiException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiMessage.SHOW);
            Api.get.append("id", message.getListId());
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            try {
                JSONArray data = new JSONArray((String) result);
                int length = data.length();
                com.thinksns.sociax.modle.Message mainMessage = null;
                for (int i = 0; i < length; i++) {
                    com.thinksns.sociax.modle.Message tempData = new com.thinksns.sociax.modle.Message(
                            data.getJSONObject(i));
                    if (i == 0) {
                        mainMessage = tempData;
                    }

                    if (!tempData.checkValid())
                        continue;
                    list.add(tempData);
                }
            } catch (JSONException e) {
                JSONObject data;
                try {
                    data = new JSONObject((String) result);
                    Api.checkHasVerifyError(data);
                    throw new ApiException();
                } catch (JSONException e1) {
                    throw new ApiException();
                }
            }
        }

        @Override
        public int[] create(com.thinksns.sociax.modle.Message message)
                throws ApiException, DataInvalidException, VerifyErrorException {
            // message.checkMessageCanAdd();
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.CREATE);
            Api.post.setUri(uri);
            Api.post.append("content", message.getContent());
            Object result = Api.run(Api.post);
            Api.checkResult(result);
            try {
                JSONArray data = new JSONArray((String) result);
                int[] res = new int[data.length()];
                for (int i = 0; i < data.length(); i++) {
                    res[i] = data.getInt(i);
                }
                return res;
            } catch (JSONException e) {
                try {
                    JSONObject data = new JSONObject((String) result);
                    Api.checkHasVerifyError(data);
                    throw new ApiException();
                } catch (JSONException e2) {
                    throw new ApiException();
                }

            }

        }

        @Override
        public boolean reply(com.thinksns.sociax.modle.Message message)
                throws ApiException, DataInvalidException, VerifyErrorException {
            // message.checkMessageCanReply();
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.REPLY);
            Api.post.setUri(uri);
            // Api.post.append("id",
            // message.getSourceMessage().getMessageId()).append("content",
            // message.getContent());
            Api.post.append("id", message.getListId());
            Api.post.append("content", message.getContent());
            Object result = Api.run(Api.post);
            Api.checkResult(result);
            if (result.equals("\"false\"") || result.equals("\"0\""))
                return false;
            return true;
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME, act);
            Api.get.setUri(uri);
        }

        @Override
        public SociaxItem getUnreadCount() throws ApiException,
                DataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.UNREAD_COUNT);
            post = new Post();
            post.setUri(uri);
            Object result = Api.run(Api.post);
            if (result.equals("\"false\"") || result.equals("\"0\"")) {
                throw new DataInvalidException();
            }
            if (result.equals(null) || result.equals("null"))
                throw new ApiException();
            try {
                return new ModelNotification(new JSONObject(result.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
                return new ModelNotification();
            }
        }

        @Override
        public Object createGroupChat(int from_uid, String members, String title)
                throws ApiException, DataInvalidException, VerifyErrorException {
            // message.checkMessageCanReply();
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.CREATE_LIST);
            post = new Post();
            post.setUri(uri);
            post.append("from_uid", from_uid);
            post.append("members", members);
            post.append("title", title);
            Object result = Api.run(Api.post);
            Log.d(TAG, "result=" + result.toString());
            if (result.equals("\"false\"") || result.equals("\"0\"")) {
                throw new DataInvalidException();
            }
            if (result.equals(null) || result.equals("null")) {
                throw new ApiException();
            }
            return result.toString();
        }

        // 新版本方法
        // 获取与某人聊天list_id（没聊过则创建）
        public Object createChat(int to_uid) throws ApiException,
                DataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.CREATE_CHAT_LIST);
            post = new Post();
            post.setUri(uri);
            post.append("uid", to_uid);
            Object result = Api.run(Api.post);
            Log.d(TAG, "result=" + result.toString());
            if (result.equals("\"false\"") || result.equals("\"0\"")) {
                throw new DataInvalidException();
            }
            if (result.equals(null) || result.equals("null")) {
                throw new ApiException();
            }
            return result.toString();
        }

        @Override
        public Object getListInfo(int room_id) throws ApiException,
                DataInvalidException, VerifyErrorException {
            // message.checkMessageCanReply();
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.GET_CHAT_INFO);
            post = new Post();
            post.setUri(uri);
            post.append("list_id", room_id);
            Object result = Api.run(Api.post);
            if (result.equals("\"false\"") || result.equals("\"0\"")) {
                throw new DataInvalidException();
            }
            if (result.equals(null) || result.equals("null")) {
                throw new ApiException();
            }
            return result.toString();

        }

        /**
         * 创建房间
         *
         * @param uid
         * @return
         */
        @Override
        public Object canSendMessage(int uid) throws ApiException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME, ApiMessage.CAN_SEND_MESSAGE);
            post = new Post();
            post.setUri(uri);
            post.append("user_id", uid);

            Object result = Api.run(post);
            return result;
        }

        @Override
        public ListData<SociaxItem> getChatList(int count, int maxId)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Post post = new Post();
            post.setUri(Api.createUrlBuild(ApiMessage.MOD_NAME, ApiMessage.GET_CHAT_LIST));
            post.append("oauth_token_secret", Request.getSecretToken());
            post.append("oauth_token", Request.getToken());
            Object result = Api.run(post);
            ListData<SociaxItem> data = getChatUserList(result);
            return data;
        }

        @SuppressWarnings("null")
        public ListData<SociaxItem> getChatUserList(Object result) {
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            if (!result.equals("null") && result != null) {
                try {
                    JSONArray data = new JSONArray((String) result);
                    for (int i = 0; i < data.length(); i++) {
                        ModelChatUserList chatUser = new ModelChatUserList(data.getJSONObject(i));
                        list.add(chatUser);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }

        /*
         * //获取聊天 qcj 2015.7.9
		 *
		 * @see com.thinksns.sociax.api.ApiMessage#getChat(int)
		 */
        @Override
        public ListData<SociaxItem> getChat(int listid) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Post post = new Post();
            post.setUri(Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.GET_CHAT));
            post.append("token", Request.getToken());
            post.append("list_id", listid);
            Object result = Api.run(post);
            ListData<SociaxItem> data = getChatContentList(result);
            return data;
        }

        @Override
        public ListData<SociaxItem> getChat(int listid, int message_id)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Post post = new Post();
            post.setUri(Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.GET_CHAT));
            post.append("token", Request.getToken());
            post.append("from_msg_id", message_id);
            post.append("list_id", listid);
            Object result = Api.run(post);
            ListData<SociaxItem> data = getChatContentList(result);
            return data;
        }

        /**
         * result ==json 封裝為javabean
         *
         * @return
         */
        private ListData<SociaxItem> getChatContentList(Object result) {
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            if (!result.equals("null") && result != null) {
                try {
                    JSONArray data = new JSONArray((String) result);
                    for (int i = 0; i < data.length(); i++) {
                        ModelChatContent chatMessage = new ModelChatContent(
                                data.getJSONObject(i));
                        list.add(chatMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }

        /*
         * //// qcj添加 清除聊天记录
		 *
		 * @see com.thinksns.sociax.api.ApiMessage#clearMessage(int)
		 */
        @Override
        public boolean clearMessage(int listid) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Post post = new Post();
            post.setUri(Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.CLEAN_MESSAGE));
            post.append("token", Request.getToken());
            post.append("list_id", listid);
            Object result = Api.run(post);
            return judgeIsOk(result);
        }

        /*
         * 清除未读聊天内容 qcj添加 对应的是 CLEAN_UNREAD = "clear_unread"
		 *
		 * @see com.thinksns.sociax.api.ApiMessage#clearUnreadMessage(int)
		 */
        @Override
        public boolean clearUnreadMessage(int listid) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Post post = new Post();
            post.setUri(Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.CLEAN_UNREAD));
            post.append("token", Request.getToken());
            post.append("list_id", listid);
            Object result = Api.run(post);
            return judgeIsOk(result);
        }

        private boolean judgeIsOk(Object result) {
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                if (jsonObject.has("status")) {
                    int flag = jsonObject.getInt("status");
                    if (flag == 1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }

        /**
         * 发送消息
         */
        @Override
        public String sendMessage(ModelChatMessage message)
                throws ApiException, DataInvalidException, VerifyErrorException {

            Post post = new Post(Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.SEND_MESSAGE));

            post.append("list_id", message.getRoom_id());
            post.append("content", message.getContent());

            post.append("uid", message.getUid_loginUser());
            post.append("latitude", message.getLatitude());
            post.append("longitude", message.getLongitude());
            post.append("length", message.getLength());

            post.append("oauth_token_secret", Request.getSecretToken());
            post.append("oauth_token", Request.getToken());

            Object object = Api.run(post);
            Log.d(TAG, "成功发送一条信息 " + object.toString());

            return object.toString();
        }


        /**
         * 发送图片附件
         *
         * @param list_id
         * @param content
         * @param imageUrl
         */
        public Object sendImgMessage(int list_id, String content,
                                     String imageUrl) {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.SEND_IMG_MESSAGE);
            Post post = new Post();
            post.setUri(uri);
            try {
                if (content != null && content.length() > 0) {
                    post.append("list_id", list_id);
                    post.append("content", content);
                    Object result = Api.run(post);
                    return result.toString();
                } else if (imageUrl != null && imageUrl.length() > 0) {
                    File file = new File(imageUrl);
                    Log.v("Api.uploadFile image", "path=" + imageUrl);
                    FormFile formFile = new FormFile(
                            Compress.compressPic(file), file.getName(), "File",
                            "application/octet-stream");
                    Map<String, String> param = new HashMap<String, String>();
                    param.put("list_id", list_id + "");
                    String resultTemp = FormPost.post(uri.toString(), param, formFile);

                    Log.v("ChatSocketClient", "------resultJson-------------" + resultTemp);

                    return resultTemp;
                }
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 发送语音附件
         *
         * @param list_id
         * @param content
         * @return
         */
        public Object sendVoiceMessage(int list_id, String content,
                                       String voiceUrl, long voiceLength) {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.SEND_VOICE_MESSAGE);
            Post post = new Post();
            post.setUri(uri);
            try {
                if (content != null && content.length() > 0) {
                    post.append("list_id", list_id);
                    post.append("content", content);
                    Object result = Api.run(post);
                    return result.toString();
                } else if (voiceUrl != null && voiceUrl.length() > 0) {
                    File file = new File(voiceUrl);
                    Log.v("Api-->uploadFile voice", file.getName());
                    FormFile formFile = new FormFile(new FileInputStream(file),
                            file.getName(), "File", "application/octet-stream");
                    Map<String, String> param = new HashMap<String, String>();
                    param.put("list_id", list_id + "");
                    param.put("length", voiceLength + "");
                    String result = FormPost.post(uri.toString(), param,
                            formFile);
                    return result;
                }
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 发送语音附件
         */
        public Object getAttachMessage(String attach_id) {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.GET_ATTACH_MESSAGE);
            Post post = new Post();
            post.setUri(uri);

            try {
                post.append("method", "url");
                post.append("oauth_token_secret", Request.getSecretToken());
                post.append("oauth_token", Request.getToken());
                post.append("hash", attach_id);
                Object result = Api.run(post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
         * (non-Javadoc) 获取最新的一条信息
		 *
		 * @see com.thinksns.sociax.api.ApiMessage#getLastMessage(int)
		 */
        @Override
        public SociaxItem getLastMessage(int listid) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Post post = new Post();
            post.setUri(Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.GET_THELASTMESSAGE));
            post.append("token", Request.getToken());
            post.append("list_id", listid);
            Object result = Api.run(post);
            try {
                ModelChatContent chatContent = new ModelChatContent(new JSONObject(result.toString()));
                Log.d(TAG, "获取的最后一条信息为 :  " + chatContent.toString());
                return chatContent;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Object getUnreadMessage(String list_id) throws ApiException,
                DataInvalidException, VerifyErrorException {

            Post post = new Post(Api.createUrlBuild(ApiMessage.MOD_NAME,
                    ApiMessage.GET_UNREAD_MESSAGE));

            post.append("list_id", list_id);
            post.append("oauth_token_secret", Request.getSecretToken());
            post.append("oauth_token", Request.getToken());
            Object result = Api.run(post);

            try {
                ModelChatContent chatContent = new ModelChatContent(
                        new JSONObject(result.toString()));
                return chatContent;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        //获取房间的title
        @Override
        public String getChatInfo(String method, int to_uid) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME, ApiMessage.GET_USERINFO);
            Post post = new Post();
            post.setUri(uri);
            post.append("uid", to_uid);
            post.append("method", method);
            Object result = Api.run(post);
            return result.toString();
        }

        //获取用户头像

        /**
         * @param method   头像来源 room_list:聊天列表 chat_detail :聊天详情
         * @param uid
         * @param listener
         * @return
         * @throws ApiException
         */
        @Override
        public String getUserFace(String method, int uid, final HttpResponseListener listener)
                throws ApiException {
//            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME, ApiMessage.GET_USERFACE);
//            Post post = new Post();
//            post.setUri(uri);
//            post.append("uid", uid);
//            post.append("method", method);
//            Object result = Api.run(post);
            RequestParams params = new RequestParams();
            params.put("method", "url");
            params.put("uid", uid);
            ApiHttpClient.post(new String[]{ApiMessage.MOD_NAME, ApiMessage.GET_USERFACE}, params,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            if (listener != null) {
                                listener.onError(throwable.toString());
                            }
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if (listener != null) {
                                listener.onSuccess(response);
                            }

                            super.onSuccess(statusCode, headers, response);
                        }

                    }

            );
            return null;
        }

        @Override
        public String getAttachAddress(String attach_id, String method)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiMessage.MOD_NAME, ApiMessage.GET_ATTACH_MESSAGE);
            Post post = new Post();
            post.setUri(uri);
            post.append("attach_id", attach_id);
            post.append("method", method);
            Object result = Api.run(post);
            return result.toString();
        }
    }

    public static final class Friendships implements ApiFriendships {
        @Override
        public boolean show(ModelUser friends) throws ApiException,
                VerifyErrorException {
            this.beforeTimeline(ApiFriendships.SHOW);
            Api.get.append("user_id", friends.getUid());
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            try {
                JSONObject data = new JSONObject((String) result);
                Api.checkHasVerifyError(data);
            } catch (JSONException e) {
                throw new ApiException();
            }
            String resultString = (String) result;
            return resultString.equals("\"havefollow\"")
                    || resultString.equals("\"eachfollow\"");
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiFriendships.MOD_NAME, act);
            Api.get.setUri(uri);
        }

        @Override
        public boolean create(ModelUser user) throws ApiException,
                VerifyErrorException, DataInvalidException {
            return this.doApiRuning(user, Api.post, ApiUsers.FOLLOW_CREATE);
        }

        @Override
        public boolean destroy(ModelUser user) throws ApiException,
                VerifyErrorException, DataInvalidException {
            return this.doApiRuning(user, Api.post, ApiUsers.FOLLOW_DESTROY);
        }

        @Override
        public boolean addBlackList(ModelUser user) throws ApiException,
                VerifyErrorException, DataInvalidException {
            return this.doApiRuning(user, Api.post,
                    ApiFriendships.ADDTOBLACKLIST);
        }

        @Override
        public boolean delBlackList(ModelUser user) throws ApiException,
                VerifyErrorException, DataInvalidException {
            return this.doApiRuning(user, Api.post,
                    ApiFriendships.DELTOBLACKLIST);
        }

        private boolean doApiRuning(ModelUser user, Request res, String act)
                throws ApiException, VerifyErrorException, DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME, act);
            if (user.isNullForUid())
                throw new DataInvalidException();
            Api.post = new Post();
            Api.post.setUri(uri);
            Api.post.append("user_id", user.getUid());
            Object result = Api.run(Api.post);
            Api.checkResult(result);

            String data = (String) result;
            if (data.indexOf("{") != -1 || data.indexOf("[") != -1) {
                try {
                    JSONObject datas = new JSONObject((String) result);
                    Api.checkHasVerifyError(datas);
                    if (datas.has("following")) {
                        int stataCode = datas.getInt("following");
                        if (act.equals(ApiUsers.FOLLOW_CREATE)) {
                            return stataCode == 1 ? true : false;
                        } else if (act.equals(ApiUsers.FOLLOW_DESTROY)) {
                            return stataCode == 0 ? true : false;
                        }
                    }
                } catch (JSONException e) {
                    Log.d(AppConstant.APP_TAG, " doruning wm" + e.toString());
                    throw new ApiException("操作失败");
                }
            }
            return false;
        }

        @Override
        public boolean isFollowTopic(ModelUser user, String topic)
                throws ApiException, VerifyErrorException, DataInvalidException {
            return doApiRuning(ApiFriendships.ISFOLLOWTOPIC, topic);
        }

        @Override
        public boolean followTopic(ModelUser user, String topic)
                throws ApiException, VerifyErrorException, DataInvalidException {
            return doApiRuning(ApiFriendships.FOLLOWTOPIC, topic);
        }

        @Override
        public boolean unFollowTopic(ModelUser user, String topic)
                throws ApiException, VerifyErrorException, DataInvalidException {
            return doApiRuning(ApiFriendships.UNFOLLOWTOPIC, topic);
        }

        private boolean doApiRuning(String act, String topic)
                throws ApiException, VerifyErrorException, DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiFriendships.MOD_NAME, act);
            Api.get = new Get();
            Api.get.setUri(uri);
            Api.get.append("topic", topic);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            String data = (String) result;
            Log.d(AppConstant.APP_TAG, " doApiRuning result" + data);
            if (data.equals("ERROR")) {
                throw new ApiException("网络繁忙，请重试！");
            }
            if (data.indexOf("{") != -1 || data.indexOf("[") != -1) {
                try {
                    JSONObject datas = new JSONObject((String) result);
                    Api.checkHasVerifyError(datas);
                    if (datas.has("is_followed")) {
                        String tempString = datas.getString("is_followed");
                        if (act.equals(ApiFriendships.FOLLOWTOPIC)) {
                            return tempString.equals("havefollow") ? true
                                    : false;
                        } else if (act.equals(ApiFriendships.UNFOLLOWTOPIC)) {
                            return tempString.equals("unfollow") ? true : false;
                        }
                    }
                    throw new ApiException();
                } catch (JSONException e) {
                    throw new ApiException();
                }
            }
            if (data.equals("\"true\"") || data.equals("1")) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static final class Favorites implements ApiFavorites {

        @Override
        public ListData<SociaxItem> index(int count) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            this.beforeTimeline(ApiFavorites.INDEX);
            Api.get.append("count", count);
            return this.getList();
        }

        @Override
        public ListData<SociaxItem> indexHeader(ModelWeibo weibo, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiFavorites.INDEX);
            Api.get.append("count", count);
            Api.get.append("since_id", weibo.getWeiboId());
            return this.getList();
        }

        @Override
        public ListData<SociaxItem> indexFooter(ModelWeibo weibo, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiFavorites.INDEX);
            Api.get.append("count", count);
            Api.get.append("max_id", weibo.getWeiboId());
            return this.getList();
        }

        @Override
        public boolean create(ModelWeibo weibo) throws ApiException,
                DataInvalidException, VerifyErrorException {
            return this.doApiRuning(weibo, Api.post, ApiFavorites.CREATE);
        }

        @Override
        public boolean isFavorite(ModelWeibo weibo) throws ApiException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiFavorites.IS_FAVORITE);
            Api.get.append("id", weibo.getWeiboId());
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            String data = (String) result;
            if (data.indexOf("{") != -1 || data.indexOf("[") != -1) {
                try {
                    JSONObject datas = new JSONObject((String) result);
                    Api.checkHasVerifyError(datas);
                    throw new ApiException();
                } catch (JSONException e) {
                    throw new ApiException();
                }
            }

            return data.equals("true");
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiFavorites.MOD_NAME, act);
            Api.get = new Get();
            Api.get.setUri(uri);
        }

        @Override
        public boolean destroy(ModelWeibo weibo) throws ApiException,
                DataInvalidException, VerifyErrorException {
            return this.doApiRuning(weibo, Api.post, ApiFavorites.DESTROY);
        }

        private boolean doApiRuning(ModelWeibo weibo, Request res, String act)
                throws ApiException, VerifyErrorException, DataInvalidException {
            Uri.Builder uri = Api.createUrlBuild(ApiFavorites.MOD_NAME, act);
            if (weibo.isNullForWeiboId())
                throw new DataInvalidException();
            Api.post.setUri(uri);
            Api.post.append("source_table_name", "feed");
            Api.post.append("source_id", weibo.getWeiboId());
            Api.post.append("source_app", "public");
            Object result = Api.run(Api.post);
            System.err.println("create " + result.toString());
            Api.checkResult(result);
            String data = (String) result;
            if (data.indexOf("{") != -1 || data.indexOf("[") != -1) {
                try {
                    JSONObject datas = new JSONObject((String) result);
                    Api.checkHasVerifyError(datas);
                    // throw new ApiException();
                } catch (JSONException e) {

                    throw new ApiException("操作失败");
                }
            }
            return Integer.parseInt(data) > 0;
        }

        public ListData<SociaxItem> getList() throws VerifyErrorException,
                ApiException, ListAreEmptyException {
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            try {
                JSONArray data = new JSONArray((String) result);
                int length = data.length();
                ListData<SociaxItem> list = new ListData<SociaxItem>();

                for (int i = 0; i < length; i++) {
                    JSONObject itemData = data.getJSONObject(i);
                    try {
                        ModelWeibo weiboData = new ModelWeibo(itemData);
                        if (!weiboData.checkValid())
                            continue;
                        list.add(weiboData);
                    } catch (WeiboDataInvalidException e) {
                        Log.e(TAG, "has one invalid weibo item with string:"
                                + data.getString(i));
                    }
                }
                return list;
            } catch (JSONException e) { // 检查返回值，如果是一个JSONObject,则进行一次验证看看是否是验证失败得提示信息
                try {
                    JSONObject data = new JSONObject((String) result);
                    Api.checkHasVerifyError(data);
                    throw new ListAreEmptyException();
                } catch (JSONException e1) {
                    throw new ApiException("暂无更多数据");
                }
            }
        }
    }

    // 多站点网站
    public static final class Sites implements ApiSites {
        @Override
        public ListData<SociaxItem> getSisteList() throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            ListData<SociaxItem> list = null;
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            Log.d(TAG, "site list result + " + result);
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject itemData = data.getJSONObject(i);
                        try {
                            SociaxItem siteData = new ApproveSite(itemData);
                            list.add(siteData);
                        } catch (SiteDataInvalidException e) {
                            Log.e(TAG,
                                    "has one invalid weibo item with string:"
                                            + data.getString(i));
                        }
                    }
                }
                return list;
            } catch (JSONException e) {
                try {
                    JSONObject data = new JSONObject((String) result);
                    Api.checkHasVerifyError(data);
                    throw new ListAreEmptyException();
                } catch (JSONException e1) {
                    throw new ApiException("暂无更多数据");
                }
            }

        }

        @Override
        public boolean getSiteStatus(ApproveSite as) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            this.beforeTimeline(ApiSites.GET_SITE_STATUS);
            Api.get.append("id", as.getSite_id());
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            try {
                JSONObject object = new JSONObject((String) result);
                if (object.has("status") && object.has("alias")) {
                    if (object.getInt("status") == 1) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG, "get site status error  " + e.toString());
                e.printStackTrace();
            }
            return false;
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createThinksnsUrlBuild(Api.APP_NAME,
                    ApiSites.MOD_NAME, act);
            Api.get.setUri(uri);
        }

        @Override
        public ListData<SociaxItem> newSisteList(int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiSites.GET_SITE_LIST);
            Api.get.append("count", count);
            return this.getSisteList();
        }

        @Override
        public ListData<SociaxItem> getSisteListHeader(ApproveSite as, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiSites.GET_SITE_LIST);
            Api.get.append("count", count);
            Api.get.append("since_id", as.getSite_id());
            return this.getSisteList();
        }

        @Override
        public ListData<SociaxItem> getSisteListFooter(ApproveSite as, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Api.get.append("count", count);
            Api.get.append("max_id", as.getSite_id());
            return this.getSisteList();
        }

        // dev.thinksns.com/ts/2.0/index.php?app=home&mod=Widget&act=addonsRequest&addon=Login&hook=isSinaLoginAvailable
        @Override
        public boolean isSupport() throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createForCheck("home", "Widget",
                    "addonsRequest");
            Api.get.setUri(uri);
            Api.get.append("addon", "Login").append("hook",
                    "isSinaLoginAvailable");
            Object result = Api.run(Api.get);
            Api.checkResult(result);

            Integer object = null;
            try {
                object = new Integer((String) result);
                return object == 1 ? true : false;
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public boolean isSupportReg() throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            Uri.Builder uri = Api.createForCheck("home", "Public",
                    "isRegisterAvailable");
            Api.get.setUri(uri);

            Api.get.append("wap_to_normal", 1);

            Object result = Api.run(Api.get);
            Api.checkResult(result);

            Integer object = null;
            try {
                object = new Integer((String) result);
                return object.equals(1) ? true : false;
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public ListData<SociaxItem> searchSisteList(String key, int count)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiSites.GET_SITE_LIST);
            Api.get.append("count", count);
            Api.get.append("content", key);
            return this.getSisteList();
        }

    }

    public static final class Public implements ApiPublic {

        @Override
        public String showAboutUs() throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiPublic.MOD_NAME,
                    ApiPublic.SHOW_ABOUT_US);
            Post post = new Post();
            post.setUri(uri);
            Object result = Api.run(post);
            return result.toString();
        }

        @SuppressLint("NewApi")
        public ListData<ModelAds> getAds() throws ApiException {
            ListData<ModelAds> results = new ListData<ModelAds>();
            Post post = new Post();
            post.setUri(Api.createUrlBuild(ApiPublic.MOD_NAME, ApiPublic.GET_ADS));
            Object result = Api.run(post);
            if (!result.equals("")) {
                JSONArray data;
                try {
                    data = new JSONArray(result.toString());
                    for (int i = 0; i < data.length(); i++) {
                        results.add(new ModelAds((JSONObject) data.get(i)));
                    }
                    return results;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * 资讯类
     */
    public static final class Information implements ApiInformation {

        /**
         * 获取资讯分类
         *
         * @param listener
         * @return
         * @throws ApiException
         */
        @Override
        public void getCate(final HttpResponseListener listener) throws ApiException {
            ApiHttpClient.post(new String[]{ApiInformation.MOD_NAME, ApiInformation.GET_CATE}, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                    if (object != null) {
                        try {
                            String message = object.getString("msg");
                            int status = object.getInt("status");
                            JSONArray dataArray = object.getJSONArray("data");
                            if (status == 1) {
                                if (listener != null) {
                                    ListData<SociaxItem> listData = new ListData<SociaxItem>();
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        try {
                                            ModelInformationCate informationCate =
                                                    new ModelInformationCate(dataArray.getJSONObject(i));
                                            listData.add(informationCate);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (DataInvalidException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    listener.onSuccess(listData);
                                }
                            } else {
                                listener.onSuccess(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        /**
         * 获取资讯列表
         *
         * @param cid      分类id
         * @param max_id     最后一条数据的id
         * @return
         * @throws ApiException
         */
        @Override
        public void getCateList(int cid, int max_id, final AsyncHttpResponseHandler handler){
            RequestParams params = new RequestParams();
            params.put("cid", cid);
            if (max_id != 0) {
                params.put("max_id", max_id);
            }
            ApiHttpClient.post(new String[]{ApiInformation.MOD_NAME, ApiInformation.GET_CATE_LIST}, params, handler);
        }
    }


    /**
     * 找人
     *
     * @author Zoey
     */
    public static final class FindPeople implements ApiFindPeople {

        //上报位置信息
        @Override
        public String updateLocation(String latitude, String longitude) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiFindPeople.MOD_NAME,
                    ApiFindPeople.NEARBY_UPDATE_LOCATION);
            Post post = new Post();
            post.setUri(uri);
            post.append("latitude", latitude);
            post.append("longitude", longitude);
            Object result = Api.run(post);

            return result.toString();
        }
    }

    /**
     * 标签
     *
     * @author Zoey
     */
    public static final class Tags implements ApiTag {

        //删除标签
        @Override
        public String deleteTag(int tag_id) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiTag.MOD_NAME,
                    ApiTag.DELETE_TAG);
            Get get = new Get();
            get.setUri(uri);
            get.append("tag_id", tag_id);
            Object result = Api.run(get);

            Log.v("tagMsg", "----------tagMsg url-------------" + uri.toString() + "&tag_id=" + tag_id);
            Log.v("tagMsg", "----------tagMsg deljson-------------" + result.toString() + "/tag_id/" + tag_id);

            return result.toString();
        }

        //添加标签
        @Override
        public String addTag(String name) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiTag.MOD_NAME,
                    ApiTag.ADD_TAG);
            Post post = new Post();
            post.setUri(uri);
            post.append("name", name);
            Object result = Api.run(post);

            Log.v("tagMsg", "----------tagMsg addjson-----" + result.toString() + "/name/" + name);

            return result.toString();
        }

        /**
         * 所有标签
         */
        @Override
        public ListData<SociaxItem> getAllTag() throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException, ExceptionIllegalParameter {
            Uri.Builder uri = Api.createUrlBuild(ApiTag.MOD_NAME, ApiTag.TAG_ALL);
            Api.post = new Post();
            post.setUri(uri);
            Object result = Api.run(Api.post);

            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            try {
                JSONArray jsonArray = new JSONArray(result.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelAllTag allTag = new ModelAllTag(jsonArray.getJSONObject(i));
                    if (allTag.getId() != null)
                        listData.add(allTag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return listData;
        }

        /**
         * 我的标签
         */
        @Override
        public ListData<SociaxItem> getMyTag() throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException, ExceptionIllegalParameter {
            Uri.Builder uri = Api.createUrlBuild(ApiTag.MOD_NAME, ApiTag.TAG_MY);
            Api.post = new Post();
            post.setUri(uri);
            Object result = Api.run(Api.post);

            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            try {
                JSONArray jsonArray = new JSONArray(result.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelMyTag myTag = new ModelMyTag(jsonArray.getJSONObject(i));
                    listData.add(myTag);
                }
                return listData;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 勋章馆
     *
     * @author Zoey
     */
    public static final class Medal implements ApiMedal {

        /**
         * 获取全部勋章
         */
        @Override
        public String getAllMedals() throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiMedal.MOD_NAME,
                    ApiMedal.ALL_MEDALS);
            Api.get.setUri(uri);
            Object result = Api.run(Api.get);
            Api.checkResult(result);

            return result.toString();
        }

        /**
         * 获取用户勋章，uid!=0的情况下为获取指定用户的勋章，uid=0的话默认获取当前用户的勋章
         */

        @Override
        public String getMyMedal(int uid) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiMedal.MOD_NAME,
                    ApiMedal.MY_MEDAL);

            Api.get.setUri(uri);
            if (uid != 0) {
                Api.get.append("uid", uid);
            }
            Api.get.append("token", Request.getToken());
            Api.get.append("oauth_token_secret", Request.getSecretToken());

            Object result = Api.run(Api.get);

            return result.toString();
        }
    }

    // Users
    public static final class Users implements ApiUsers {

        @Override
        public ModelUser show(final ModelUser user, final HttpResponseListener listener) {
            RequestParams params = new RequestParams();
            if (user.getUid() != -1) {
                params.put("user_id", user.getUid());
            }
            if (user.getUserName() != null) {
                params.put("uname", user.getUserName());
            }

            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.SHOW}, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (listener != null) {
                        try {
                            if (!response.has("status")) {
                                //获取个人信息失败
                                ListData<ModelUser> list = new ListData<ModelUser>();
                                ModelUser newUser = new ModelUser(response);
                                newUser.setToken(user.getToken());
                                newUser.setSecretToken(user.getSecretToken());
                                list.add(newUser);
                                listener.onSuccess(list);
                            } else {
                                listener.onError(response.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (DataInvalidException e) {
                            Log.d(AppConstant.APP_TAG, "======》  解析个人信息出错 。。。" + e.toString());
                            e.printStackTrace();
                            listener.onError(e.toString());
                        }
                    }

                    super.onSuccess(statusCode, headers, response);
                }

            });
            return null;
        }

        public void show(int userId, String userName, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            if (userId != -1 && userId != 0) {
                params.put("user_id", userId);
            } else if (userName != null) {
                params.put("uname", userName);
            }

            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.SHOW}, params, listener);

        }

        /**
         * 更换背景
         */
        public Object changeBackGround(String imageUrl) {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.CHANGE_BACKGROUND);
            Post post = new Post();
            post.setUri(uri);
            try {
                if (imageUrl != null && imageUrl.length() > 0) {
                    File file = new File(imageUrl);
                    FormFile formFile = new FormFile(Bimp.getInputStreamFromLocal(imageUrl, false),
                            file.getName(), "pic",
                            "application/octet-stream");
                    String resultTemp = FormPost.post(uri.toString(), null, formFile);
                    return resultTemp;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public ModelUser showByUname(ModelUser user) throws ApiException,
                DataInvalidException, VerifyErrorException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.SHOW);
            Get get = new Get();
            get.setUri(uri);
            if (user.getUserName() != null) {
                get.append("uname", user.getUserName());
            } else {
                get.append("user_id", user.getUid());
            }
            Object result = Api.run(get);

            Log.v("tagMsg", "--------showByUname-----------" + result.toString());

            Api.checkResult(result);
            String data = (String) result;

            if (data.equals("\"false\""))
                throw new DataInvalidException("该用户不存在");
            try {
                JSONObject userData = new JSONObject(data);
                Api.checkHasVerifyError(userData);
                return new ModelUser(userData);
            } catch (JSONException e) {
                Log.d(AppConstant.APP_TAG,
                        "======》  解析个人信息出错 。。。" + e.toString());
                throw new DataInvalidException("获取个人信息失败");
            }
        }

        // 返回通知，@，私信
        @Override
        public NotifyCount notificationCount(int uid) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            this.beforeTimeline(ApiUsers.NOTIFICATION_COUNT);
            Api.get.append("user_id", uid);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            String data = (String) result;
            // String data
            // ="{\"message\":2,\"notify\":3,\"appmessage\":\"0\",\"comment\":1,\"atme\":1,\"total\":8,\"weibo_comment\":3,\"global_comment\":0}";
            if (data.equals("\"false\""))
                throw new ListAreEmptyException("请求的数据异常");

            try {
                JSONObject userData = new JSONObject(data);
                Api.checkHasVerifyError(userData);
                Log.d("apiData", "getNotifyCount" + userData.toString());

                NotifyCount notifyCount = new NotifyCount(userData);
                return notifyCount;
            } catch (JSONException e) {
                throw new DataInvalidException("数据格式错误");
            }
        }

        /**
         * 提醒数据list
         */
        @Override
        public ListData<SociaxItem> getNotificationList(int uid)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline(ApiUsers.NOTIFICATIONLIST);
            Api.get.append("user_id", uid);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            if (result.equals("\"false\""))
                throw new ListAreEmptyException("请求的数据异常");
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        NotifyItem notifyItem = new NotifyItem(jsonObject);
                        if (notifyItem.getCount() < 1) {
                            continue;
                        }
                        list.add(notifyItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public boolean unsetNotificationCount(Type type, int uid)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException {
            this.beforeTimeline("Notifytion", "set_notify_read");
            NotifyCount notifycount = new NotifyCount();
            for (NotifyCount.Type t : NotifyCount.Type.values()) {

                if (t.equals(type)) {
                    String name = t.name();
                    Api.get.append("type", name);
                }
            }
            Api.get.append("mid", uid);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            Log.d(TAG, "api unsetNotification......" + result);
            String data = (String) result;
            if (data.equals("\"false\""))
                return false;
            if (data.indexOf("{") != -1 || data.indexOf("[") != -1) {
                try {
                    JSONObject userData = new JSONObject(data);
                    Api.checkHasVerifyError(userData);
                    return false;
                } catch (JSONException e) {
                    throw new DataInvalidException("数据格式错误");
                }
            } else {
                if (NotifyCount.Type.atme.name().equals(type.name())) {
                    notifycount.setAtme(0);
                } else if (NotifyCount.Type.message.name().equals(type.name())) {
                    notifycount.setMessage(0);
                } else if (NotifyCount.Type.notify.name().equals(type.name())) {
                    notifycount.setNotify(0);
                } else if (NotifyCount.Type.comment.name().equals(type.name())) {
                    notifycount.setWeiboComment(0);
                }
                Log.d(TAG, "unsetNotificationCount" + type.name());
                return true;
            }

        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild("User", act);
            Api.get.setUri(uri);
        }

        private void beforeTimeline(String mod, String act) {
            Uri.Builder uri = Api.createUrlBuild(mod, act);
            Api.get.setUri(uri);
        }

        @Override
        public boolean uploadFace(File file) throws ApiException {
            String temp = "0";
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                        ApiUsers.UPLOAD_FACE);
                FormFile formFile = new FormFile(Compress.compressPic(file),
                        file.getName(), "Filedata", "application/octet-stream");
                Api.post.setUri(uri);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());

                temp = FormPost.post(uri.toString(), param, formFile);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(AppConstant.APP_TAG,
                        "upload face pic error ..." + e.toString());
            }
            Log.d(AppConstant.APP_TAG, temp);
            return temp.equals("\"1\"") ? true : false;
        }

        @Override
        public boolean uploadFace(Bitmap bitmap, File file) throws ApiException {
            String temp = "0";
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                        ApiUsers.UPLOAD_FACE);

                FormFile formFile = new FormFile(Compress.compressPic(bitmap),
                        file.getName(), "Filedata", "application/octet-stream");
                Api.post.setUri(uri);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());

                temp = FormPost.post(uri.toString(), param, formFile);

                JSONObject tempdata = new JSONObject(temp);
                temp = tempdata.getString("status");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(AppConstant.APP_TAG,
                        "upload face pic error ..." + e.toString());
            }
            Log.d(AppConstant.APP_TAG, temp);
            return temp.equals("1") ? true : false;
        }

        //上传用户自定义封面
        public boolean uploaUserCover(Bitmap bitmap, File file) throws ApiException {
            String temp = "0";
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                        "uploadUserCover");

                FormFile formFile = new FormFile(Compress.compressPic(bitmap),
                        file.getName(), "Filedata", "application/octet-stream");
                Api.post.setUri(uri);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());

                temp = FormPost.post(uri.toString(), param, formFile);

                JSONObject tempdata = new JSONObject(temp);
                temp = tempdata.getString("status");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(AppConstant.APP_TAG,
                        "upload face pic error ..." + e.toString());
            }
            Log.d(AppConstant.APP_TAG, temp);
            return temp.equals("1") ? true : false;
        }

        @Override
        public void getRecentTopic(int pageCount, int maxid, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("count", pageCount);
            params.put("max_id", maxid);
            ApiHttpClient.get(new String[]{ApiStatuses.MOD_NAME, ApiUsers.RECENT_TOPIC}, params, listener);
        }

        @Override
        public ListData<SociaxItem> getRecentAt() throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME,
                    ApiStatuses.MOD_NAME, ApiUsers.RECENT_USER);
            Api.get.setUri(uri);
            Object result = Api.run(Api.get);
            ListData<SociaxItem> listData = null;
            try {
                JSONArray jsonArray = new JSONArray((String) result);
                listData = new ListData<SociaxItem>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonUser = jsonArray.getJSONObject(i);
                    ModelUser tempUser = new ModelUser(jsonUser);
                    listData.add(tempUser);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Api get recent at data error " + e.toString());
            }
            return listData;
        }

        @Override
        public ListData<SociaxItem> getUserCategory(String type)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.GET_USER_CATEGORY);
            ListData<SociaxItem> listData = null;
            Get get = new Get();
            get.setUri(uri);
            get.append("type", type);
            Object result = Api.run(get);
            try {
                JSONObject jsonObject = new JSONObject((String) result);
                listData = packageData(jsonObject);
            } catch (Exception e) {
                try {
                    listData = packageData(new JSONArray((String) result));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                Log.d(TAG, e.toString());
            }
            return listData;
        }

        private ListData<SociaxItem> packageData(JSONArray ja) throws Exception {

            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            ListData<SociaxItem> listData2 = null;

            for (int i = 0; i < ja.length(); i++) {
                JSONObject joTemp = (JSONObject) ja.get(i);
                int id = joTemp.getInt("user_group_id");
                String title = joTemp.getString("user_group_name");
                StringItem si = new StringItem(id, title);
                if (!joTemp.isNull("child")) {
                    JSONArray jaChile = joTemp.getJSONArray("child");
                    listData2 = packageChildData(jaChile);
                } else {
                    listData2 = null;
                }
                si.setListData(listData2);
                listData.add(si);
            }
            if (listData != null)
                Collections.sort(listData);
            return listData;

        }

        private ListData<SociaxItem> packageChildData(JSONArray ja)
                throws Exception {
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject joTemp = (JSONObject) ja.get(i);
                int id = joTemp.getInt("user_verified_category_id");
                String title = joTemp.getString("title");
                StringItem si = new StringItem(id, title);
                listData.add(si);
            }
            return listData;
        }

        @SuppressWarnings("rawtypes")
        private ListData<SociaxItem> packageData(JSONObject jo)
                throws JSONException {

            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            ListData<SociaxItem> listData2 = null;
            for (Iterator iterator = jo.keys(); iterator.hasNext(); ) {
                int id = Integer.valueOf(iterator.next().toString());
                JSONObject joTemp = jo.getJSONObject(id + "");
                String title = joTemp.getString("title");
                if (!joTemp.isNull("child")) {
                    JSONObject joChile = joTemp.getJSONObject("child");
                    listData2 = packageData(joChile);
                } else {
                    listData2 = null;
                }
                StringItem si = new StringItem(id, title);
                si.setListData(listData2);
                listData.add(si);
            }
            if (listData != null)
                Collections.sort(listData);

            return listData;
        }

        @Override
        public ListData<SociaxItem> getUserFollower(int page)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.GET_USER_FOLLOWER);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            Get get = new Get();
            get.setUri(uri);
            get.append("page", page);
            try {
                JSONArray jsonArray = new JSONArray((String) Api.run(get));
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelUser user = new ModelUser(jsonArray.getJSONObject(i));
                    listData.add(user);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return listData;
        }

        @Override
        public boolean checkint(String la, String lo) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.CHECKIN);
            Post post = new Post();
            post.setUri(uri);
            post.append("latitude", la);
            post.append("longitude", lo);
            try {
                Object o = Api.run(post);
                System.out.println(o);
                if ((Integer.valueOf(o.toString().trim())) == 1) {
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return false;
        }

        @Override
        public ListData<SociaxItem> getNeighbor(String la, String lo, int page)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.NEIGHBOR);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            Get get = new Get();
            get.setUri(uri);
            get.append("latitude", la);
            get.append("longitude", lo);
            get.append("page", page);
            try {
                JSONObject jsonObject = new JSONObject((String) Api.run(get));
                JSONArray jsonArray = (jsonObject.getJSONArray("data"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelUser user = new ModelUser(jsonArray.getJSONObject(i));
                    listData.add(user);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return listData;
        }

        @Override
        public ListData<SociaxItem> searchByArea(String key, int page)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.SEARCH_BY_AREA);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            Get get = new Get();
            get.setUri(uri);
            get.append("areaid", key);
            get.append("page", page);
            try {
                JSONObject jsonObject = new JSONObject((String) Api.run(get));
                JSONArray jsonArray = (jsonObject.getJSONArray("data"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelUser user = new ModelUser(jsonArray.getJSONObject(i));
                    listData.add(user);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return listData;
        }

        @Override
        public ListData<SociaxItem> searchByTag(String key, int page)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.SEARCH_BY_TAG);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            Get get = new Get();
            get.setUri(uri);
            get.append("tagid", key);
            get.append("page", page);
            try {
                JSONObject jsonObject = new JSONObject((String) Api.run(get));
                JSONArray jsonArray = (jsonObject.getJSONArray("data"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelUser user = new ModelUser(jsonArray.getJSONObject(i));
                    listData.add(user);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return listData;
        }

        @Override
        public ListData<SociaxItem> searchByVerifyCategory(String key, int page)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.SEARCH_BY_VERIFY_CATEGORY);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            Get get = new Get();
            get.setUri(uri);
            get.append("verifyid", key);
            get.append("page", page);
            try {
                JSONObject jsonObject = new JSONObject((String) Api.run(get));
                JSONArray jsonArray = (jsonObject.getJSONArray("data"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelUser user = new ModelUser(jsonArray.getJSONObject(i));
                    listData.add(user);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return listData;
        }

        @Override
        public ListData<SociaxItem> searchByUesrCategory(String key, int page)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.SEARCH_BY_UESR_CATEGORY);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            Get get = new Get();
            get.setUri(uri);
            get.append("cateid", key);
            get.append("page", page);
            try {
                JSONObject jsonObject = new JSONObject((String) Api.run(get));
                JSONArray jsonArray = (jsonObject.getJSONArray("data"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelUser user = new ModelUser(jsonArray.getJSONObject(i));
                    listData.add(user);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return listData;
        }

        /***************
         * t4 userapi
         ***********************/

        @Override
        public ListData<SociaxItem> getUserFollowingList(int uid, String name,int max_id, final HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            if (name!=null&&!name.equals("null")&&!name.equals("")){
                params.put("key",name);
            }
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.FOOLOWING}, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ModelSearchUser follow;
                            try {
                                follow = new ModelSearchUser(jsonArray.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onSuccess(listData);
                    }
                    super.onSuccess(statusCode, headers, jsonArray);
                }

            });
            return null;
        }

        public void getUserFollowingList(int uid, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.FOOLOWING}, params, listener);
        }

        public void getUserFollowingList(int uid,String name,int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            if (name!=null&&!name.equals("null")&&!name.equals("")){
                params.put("key",name);
            }
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.FOOLOWING}, params, listener);
        }

        @Override
        public ListData<SociaxItem> getUserBlackList(int count, int max_id, final HttpResponseListener listener) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.BLACK_LIST}, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        try {
                            ListData<SociaxItem> listData = new ListData<SociaxItem>();
                            for (int i = 0; i < response.length(); i++) {
                                ModelSearchUser follow = new ModelSearchUser(
                                        response.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            }
                            listener.onSuccess(listData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });
            return null;
        }


        public void getUserBlackList(int count, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.BLACK_LIST}, params, listener);
        }

        @Override
        public ListData<SociaxItem> getUserFriendsList(int uid, int max_id, final ApiHttpClient.HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.FRIENDS}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                ModelSearchUser follow = new ModelSearchUser(response.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onSuccess(listData);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });

            return null;
        }

        @Override
        public ListData<SociaxItem> getUserFollowList(int uid,String name, int max_id, final HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            if (name!=null&&!name.equals("null")&&!name.equals("")){
                params.put("key",name);
            }
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.FOLLOWERS}, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onSuccess(listData);
                    }
                }

            });
            return null;
        }

        public ListData<SociaxItem> getUserFollowList(int uid, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.FOLLOWERS}, params, listener);
            return null;
        }

        public ListData<SociaxItem> getUserFollowList(int uid,String name, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            if (name!=null&&!name.equals("null")&&!name.equals("")){
                params.put("key",name);
            }
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.FOLLOWERS}, params, listener);
            return null;
        }

        @Override
        public Object changeFollowing(int uid, int type) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    type == 1 ? ApiUsers.FOLLOW : ApiUsers.UNFOLLOW);
            Api.post = new Post();
            post.setUri(uri);
            post.append("user_id", uid);
            try {
                JSONObject result = new JSONObject(Api.run(post).toString());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Object removeBlackList(int uid) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.MOD_NAME,
                    ApiUsers.REMOVE_BLACK);
            Api.post = new Post();
            ;
            post.setUri(uri);
            post.append("user_id", uid);
            try {
                JSONObject result = new JSONObject(Api.run(post).toString());

                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public ListData<SociaxItem> getNearByUser(String lat, String lng,
                                                  int page, final HttpResponseListener listener) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("latitude", lat);
            params.put("longitude", lng);
            params.put("page", page);
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.NEW_NEARBY}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            }
                            listener.onSuccess(listData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null)
                        listener.onError(throwable.toString());
                }
            });
            return null;
        }

        public ListData<SociaxItem> getNearByUser(double lat, double lng,
                                                  int page, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("latitude", lat);
            params.put("longitude", lng);
            params.put("page", page);

            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.NEW_NEARBY}, params, listener);
            return null;
        }

        @Override
        public ListData<SociaxItem> getDistrictUser(int city_id, int max_id, final ApiHttpClient.HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("city_id", city_id);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.DISTRICT}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            }
                            listener.onSuccess(listData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });

            return null;
        }

        @Override
        public ListData<SociaxItem> searchUserByKey(String key, int max_id,
                                                    int count, final HttpResponseListener listener) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("max_id", max_id);
            params.put("count", count);
            if (key != null)
                params.put("key", key);
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.SEARCH_BY_KEY}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {

                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        listener.onSuccess(listData);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });

            return null;
        }

        @Override
        public ListData<SociaxItem> searchAtUser(String key, int max_id,
                                                 int count, final HttpResponseListener listener) {
//            Uri.Builder uri = Api.createUrlBuild(MOD_NAME, ApiUsers.SEARCH_AT);
//            Api.post = new Post();
//            post.setUri(uri);
//            if (key != null)
//                post.append("key", key);
//            post.append("max_id", max_id);
//            post.append("count", count);
//            ListData<SociaxItem> listData = new ListData<SociaxItem>();
//            try {
//                JSONArray jsonArray = new JSONArray((String) Api.run(post));
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    ModelSearchUser follow = new ModelSearchUser(
//                            jsonArray.getJSONObject(i));
//                    if (follow.getUid() != 0)
//                        listData.add(follow);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return listData;
            RequestParams params = new RequestParams();
            if (key != null) {
                params.put("key", key);
            }
            params.put("max_id", max_id);
            params.put("count", count);
            ApiHttpClient.post(new String[]{MOD_NAME, ApiUsers.SEARCH_AT}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    ListData<SociaxItem> listData = new ListData<SociaxItem>();
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ModelSearchUser follow = new ModelSearchUser(jsonArray.getJSONObject(i));
                            if (follow.getUid() != 0)
                                listData.add(follow);
                        }
                        listener.onSuccess(listData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null)
                        listener.onError(throwable.toString());
                }
            });
            return null;
        }

        @Override
        public ListData<SociaxItem> searchUserByVerifyCode(String id, int max_id, final HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("verify_id", id);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.VERIFY}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            }
                            listener.onSuccess(listData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null)
                        listener.onError(throwable.toString());
                }
            });

            return null;
        }

        @Override
        public ListData<SociaxItem> searchUserByTag(int id, int max_id, final HttpResponseListener listener)
                throws ApiException {
//			Uri.Builder uri = Api.createUrlBuild(ApiUsers.FINDPEOPLE,
//					ApiUsers.TAG);
//			Api.post = new Post();
//			post.setUri(uri);
//			post.append("tag_id", id);
//			post.append("max_id", max_id);
//			ListData<SociaxItem> listData = new ListData<SociaxItem>();
//			try {
//				JSONArray jsonArray = new JSONArray((String) Api.run(post));
//				for (int i = 0; i < jsonArray.length(); i++) {
//					ModelSearchUser follow = new ModelSearchUser(
//							jsonArray.getJSONObject(i));
//					if (follow.getUid() != 0)
//						listData.add(follow);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
            RequestParams params = new RequestParams();
            params.put("tag_id", id);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.TAG}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                if (follow.getUid() != 0)
                                    listData.add(follow);
                            }
                            listener.onSuccess(listData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null)
                        listener.onError(throwable.toString());
                }
            });
            return null;
        }

        @Override
        public ListData<SociaxItem> searchUserByContract(String phone, final HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("tel", phone);
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.CONTRACT}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                listData.add(follow);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listener.onSuccess(listData);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });

            return null;
        }

        @Override
        public ListData<SociaxItem> searchUser(final HttpResponseListener listener) throws ApiException {
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.SEARCH}, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                listData.add(follow);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        listener.onSuccess(listData);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });

            return null;
        }

        //指定查询count条系统推荐用户
        public void searchUser(final AsyncHttpResponseHandler listener, int count) {
            RequestParams params = new RequestParams();
            params.put("rus", count);
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.SEARCH}, params, listener);
        }

        @Override
        public ListData<SociaxItem> searchUser(final HttpResponseListener listener, int count) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("rus", count);
            ApiHttpClient.post(new String[]{ApiUsers.FINDPEOPLE, ApiUsers.SEARCH}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                ModelSearchUser follow = new ModelSearchUser(
                                        jsonArray.getJSONObject(i));
                                listData.add(follow);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        listener.onSuccess(listData);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });

            return null;
        }

        @Override
        public ListData<SociaxItem> getTagList() {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.FINDPEOPLE,
                    ApiUsers.GET_USER_TAGS);
            Api.post = new Post();
            post.setUri(uri);
            ListData<SociaxItem> listData = null;
            try {
                JSONArray jsonArray = new JSONArray((String) Api.run(post));
                listData = new ListData<SociaxItem>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelUserTagandVerify follow = new ModelUserTagandVerify(jsonArray.getJSONObject(i));
                    listData.add(follow);
                }
                if (listData != null) {
                    return listData;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        public ListData<SociaxItem> getVerifyList() {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.FINDPEOPLE,
                    ApiUsers.GET_USER_VERIFYS);
            Api.post = new Post();
            post.setUri(uri);

            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            try {
                JSONArray jsonArray = new JSONArray((String) Api.run(post));
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelUserTagandVerify follow = new ModelUserTagandVerify(
                            jsonArray.getJSONObject(i));
                    listData.add(follow);
                }
                return listData;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public Map<String, List<ModelCityInfo>> getCityList() {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.FINDPEOPLE,
                    ApiUsers.GET_USER_CITY);
            Api.post = new Post();
            post.setUri(uri);
            Map<String, List<ModelCityInfo>> key_map = new HashMap<String, List<ModelCityInfo>>();
            try {
                Object result = Api.run(post);
                JSONObject response = new JSONObject(result.toString());
                Iterator<String> key = response.keys();
                while (key.hasNext()) {
                    String obj_k = key.next();
                    try {
                        JSONObject object = response.getJSONObject(obj_k);
                        Iterator<String> obj_key = object.keys();
                        List<ModelCityInfo> list = new ArrayList<ModelCityInfo>();
                        while (obj_key.hasNext()) {
                            String obj_k_k = obj_key.next();
                            JSONObject city = object.getJSONObject(obj_k_k);
                            ModelCityInfo cityInfo = new ModelCityInfo(city);
                            list.add(cityInfo);
                        }
                        key_map.put(obj_k, list);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return key_map;
        }

        @Override
        public Map<String, List<ModelCityInfo>> getAreaList() {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.GET_AREA_LIST);
            Api.post = new Post();
            post.setUri(uri);
            Map<String, List<ModelCityInfo>> key_map = new HashMap<String, List<ModelCityInfo>>();
            try {
                Object result = Api.run(post);
                JSONObject response = new JSONObject(result.toString());
                Iterator<String> key = response.keys();
                while (key.hasNext()) {
                    String obj_k = key.next();
                    try {
                        JSONObject object = response.getJSONObject(obj_k);
                        Iterator<String> obj_key = object.keys();
                        List<ModelCityInfo> list = new ArrayList<ModelCityInfo>();
                        while (obj_key.hasNext()) {
                            String obj_k_k = obj_key.next();
                            JSONObject city = object.getJSONObject(obj_k_k);
                            ModelCityInfo cityInfo = new ModelCityInfo(city);
                            list.add(cityInfo);
                        }
                        key_map.put(obj_k, list);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return key_map;
        }


        @Override
        public Map<String, List<ModelAreaInfo>> getArea() {
            return getArea("0");
        }

        @Override
        public Map<String, List<ModelAreaInfo>> getArea(String id) {
            Uri.Builder uri = Api.createUrlBuild(ApiPublic.MOD_NAME,
                    ApiUsers.GET_AREA);
            Api.post = new Post();
            post.setUri(uri);
            post.append("pid", id);
            Map<String, List<ModelAreaInfo>> key_map = new HashMap<String, List<ModelAreaInfo>>();
            try {
                Object result = Api.run(post);
                JSONObject response = new JSONObject(result.toString());
                Iterator<String> key = response.keys();
                while (key.hasNext()) {
                    String obj_k = key.next();
                    try {
                        JSONArray object = response.getJSONArray(obj_k);
                        List<ModelAreaInfo> list = new ArrayList<ModelAreaInfo>(object.length());
                        for (int i = 0; i < object.length(); i++) {
                            ModelAreaInfo cityInfo = new ModelAreaInfo((JSONObject) object.get(i));
                            list.add(cityInfo);
                        }
                        key_map.put(obj_k, list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return key_map;
        }

        /**
         * 根据pid获取地区列表,pid=0表示获取一级省份列表
         *
         * @param pid
         * @param listener 数据回掉接口
         */
        public void getAreaById(int pid, final HttpResponseListener listener) {
            RequestParams params = new RequestParams();
            params.put("pid", pid);
            params.put("notsort", "1");
            ApiHttpClient.post(new String[]{ApiPublic.MOD_NAME, ApiUsers.GET_AREA}, params,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            List<ModelAreaInfo> list = new ArrayList<ModelAreaInfo>(response.length());
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    ModelAreaInfo cityInfo = new ModelAreaInfo((JSONObject) response.get(i));
                                    list.add(cityInfo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            listener.onSuccess(list);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            listener.onError("网络连接失败,请稍后重试");
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            super.onSuccess(statusCode, headers, responseString);
                            if (responseString.equals("null")) {
                                //没有获取到地区数据
                                listener.onSuccess(null);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
        }

        /**
         * 修改用户信息
         *
         * @param changetype 修复类型
         * @param input      修改的内容，
         * @param oldpwd     修改密码的时候需要用到，其他时候放空即可
         * @return
         */
        public Object saveUserInfo(int changetype, String input, String oldpwd) {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.SAVE_USER_INFO);
            Api.post = new Post();
            post.setUri(uri);
            if (changetype == StaticInApp.CHANGE_USER_INTRO) {
                post.append("intro", input);
            } else if (changetype == StaticInApp.CHANGE_USER_NAME) {
                post.append("uname", input);
            } else if (changetype == StaticInApp.CHANGE_USER_CITY) {
                post.append("city_id", input);
            } else if (changetype == StaticInApp.CHANGE_USER_PWD) {
                post.append("password", input);
                post.append("old_password", oldpwd);
            } else if (changetype == StaticInApp.CHANGE_USER_SEX) {
                if (input.equals("男")) {
                    post.append("sex", 1);
                } else if (input.equals("女")) {
                    post.append("sex", 2);
                }
            }
            try {
                return Api.run(post);
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        public Object getMyCredit() {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.CREDIT,
                    ApiUsers.CREDIT_MY);
            Api.post = new Post();
            post.setUri(uri);
            try {
                return Api.run(post);
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        // 获取二维码文本
        @Override
        public String getEwmText(String uid) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild("public", "Mobile", "wap_app");
            Log.v("Api--getEWMText", "wztest url");
            Get get = new Get();
            get.setEwmUri(uri);

            Log.v("erweima", "----erweima-------" + uri.toString());

            get.append("uid", uid);
            return uri.toString().trim();
        }

        @Override
        public ListData<SociaxItem> getUserPhoto(int uid, int max_id, int count,
                                                 final HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            params.put("max_id", max_id);
            params.put("count", count);

            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.USER_PHOTO}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                ModelUserPhoto follow = new ModelUserPhoto(jsonArray.getJSONObject(i));
                                listData.add(follow);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onSuccess(listData);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });

            return null;
        }

        @Override
        public ListData<SociaxItem> getUserVedio(int uid, int max_id, int count, final HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            params.put("max_id", max_id);
            params.put("count", count);

            ApiHttpClient.post(new String[]{ApiUsers.MOD_NAME, ApiUsers.USER_VIDEO}, params,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                            super.onSuccess(statusCode, headers, jsonArray);
                            if (listener != null) {
                                ListData<SociaxItem> listData = new ListData<SociaxItem>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        ModelVideo follow = new ModelVideo(jsonArray.getJSONObject(i));
                                        listData.add(follow);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                listener.onSuccess(listData);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (listener != null) {
                                listener.onError(throwable.toString());
                            }
                        }
                    });

            return null;
        }

        @Override
        public ListData<SociaxItem> getUserBindInfo() throws ApiException {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.GET_USER_BIND_INFO);
            Api.post = new Post();
            post.setUri(uri);
            String result = Api.run(post).toString();
            if (result.equals("null") || result.equals("false")) {
                return null;
            } else {
                try {
                    ListData<SociaxItem> returnlist = new ListData<SociaxItem>();
                    JSONArray resut2json = new JSONArray(result);
                    for (int i = 0; i < resut2json.length(); i++) {
                        ModelBindItem mdi = new ModelBindItem(
                                resut2json.getJSONObject(i));
                        returnlist.add(mdi);
                    }
                    return returnlist;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        /**
         * 绑定手机号
         *
         * @param phonenum
         * @param code
         * @return
         */
        public Object bindPhone(String phonenum, String code) {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.BIND_PHONE);
            Api.post = new Post();
            post.setUri(uri);
            if (phonenum != null) {
                post.append("phone", phonenum);
            }
            if (code != null) {
                post.append("code", code);
            }
            try {
                return Api.run(post);
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Object bindOther(String type, String openId, String token) {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.BIND_OTHER);
            Api.post = new Post();
            post.setUri(uri);
            if (type != null)
                post.append("type", type);
            if (openId != null)
                post.append("type_uid", openId);
            if (token != null)
                post.append("access_token", token);
            try {
                return Api.run(post);
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Object unbindOther(String type) {
            // TODO Auto-generated method stub

            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.UN_BIND_OTHER);
            Api.post = new Post();
            post.setUri(uri);
            if (!type.equals("")) {
                post.append("type", type);
            }
            try {
                return Api.run(post);
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public ListData<SociaxItem> readAds() throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiUsers.FINDPEOPLE,
                    ApiUsers.TOP_AD);
            Api.post = new Post();
            post.setUri(uri);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            try {
                JSONObject json = new JSONObject((String) Api.run(post));
                if (json.has("is_active") && !json.get("is_active").equals("0")) {
                    if (json.has("content")) {
                        JSONArray jsonArray = json.getJSONArray("content");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ModelBanner follow = new ModelBanner(
                                    jsonArray.getJSONObject(i));
                            if (follow.isActive()) {// 只查看活动的广告
                                listData.add(follow);
                            }
                        }
                    }
                }
                if (listData.size() == 0) {
                    return null;
                } else {
                    return listData;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public ListData<SociaxItem> getUserTopList(int type)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(APP_NAME, ApiUsers.FINDPEOPLE,
                    type == 1 ? ApiUsers.TOP_SOCRE_LIST
                            : ApiUsers.TOP_MEDAL_LIST);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            Api.post = new Post();
            post.setUri(uri);
            try {
                JSONObject result = new JSONObject(Api.run(post).toString());
                JSONArray jsonArray = result.getJSONArray("lists");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelRankListItem follow = new ModelRankListItem(
                            jsonArray.getJSONObject(i));
                    follow.setRankMy(result.getString("rank"));
                    listData.add(follow);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return listData;
        }

        public Object getBindVerifyCode(String phoneNumber) {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild(MOD_NAME,
                    ApiStatuses.BIND_VERIFY);
            Api.post = new Post();
            post.setUri(uri);
            post.append("phone", phoneNumber);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return "[]";
        }

        public Object checkVerifyCode(String phoneNumber, String oauthNum) {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild(ApiStatuses.OAUTH,
                    ApiStatuses.CHECK_REGISTER_VERIFY);
            Api.post = new Post();
            post.setUri(uri);
            post.append("phone", phoneNumber);
            post.append("regCode", oauthNum);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return "[]";
        }

        /**
         * 我的礼物
         *
         * @return
         */
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getUserHonner(int uid, int max_id)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild("User", "get_user_medal");
            Log.e("uri", "uri+" + uri.toString());
            post = new Post();
            post.setUri(uri);
            post.append("max_id", max_id);
            post.append("uid", uid);
            Object result = Api.run(post);
            try {
                return (ListData<SociaxItem>) afterRequest(
                        ListData.DataType.MODEL_MEDAL, result);
            } catch (ExceptionIllegalParameter e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public String addFeedBack(String content) {
            Uri.Builder uri = Api.createUrlBuild("System", "sendFeeedback");
            post = new Post();
            post.setUri(uri);
            post.append("uid", Thinksns.getMy().getUid());
            post.append("content", content);
            try {
                return Api.run(post).toString();
            } catch (ApiException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getFeedbackType(int count, int max_id)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild("User", "get_feedback_type");
            Log.e("uri", "uri+" + uri.toString());
            post = new Post();
            post.setUri(uri);
            post.append("max_id", max_id);
            post.append("count", count);
            Object result = Api.run(post);
            try {
                return (ListData<SociaxItem>) afterRequest(
                        ListData.DataType.MODEL_FEEDBACK, result);
            } catch (ExceptionIllegalParameter e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        // 获取互相关注人列表，暂未写完
        @Override
        public ListData<SociaxItem> getUserFriendsListByLetter(int uid,
                                                               int max_id) throws ApiException {

            Uri.Builder uri = Api.createUrlBuild(ApiUsers.MOD_NAME,
                    ApiUsers.FRIENDS_LETTER);
            ListData<SociaxItem> listData = new ListData<SociaxItem>();
            Api.post = new Post();
            post.setUri(uri);
            post.append("uid", uid);
            post.append("oauth_token", Thinksns.getMy().getToken());
            post.append("oauth_token_secret", Thinksns.getMy().getSecretToken());
            post.append("max_id", max_id);
            Object result = Api.run(post);
            return listData;
        }
    }

    /**
     * 联系人 api 类
     */
    public static final class STContacts implements ApiContact {

        @Override
        public ListData<SociaxItem> getContactCategoryList(int departId)
                throws ApiException {
            beforeTimeline(ApiContact.GET_DEPARTMENT_LIST);
            if (departId > 0)
                Api.get.append("deptId", departId);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    if (departId <= 0) {
                        ContactCategory cc1 = new ContactCategory(-2, "我的联系人",
                                "department");
                        ContactCategory cc2 = new ContactCategory(-1, "所有同事",
                                "department");
                        list.add(cc1);
                        list.add(cc2);
                    }
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ContactCategory contactCategory = new ContactCategory(
                                jsonObject);
                        contactCategory.setType("department");
                        list.add(contactCategory);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        /**
         * 获取联系人列表
         *
         * @return
         * @throws ApiException
         */
        @Override
        public ListData<SociaxItem> getAllContactList() throws ApiException {
            beforeTimeline(ApiContact.GET_ALL_COLLEAGUE);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    list = new ListData<SociaxItem>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Contact contact = new Contact(jsonObject);
                        list.add(contact);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiContact.MOD_NAME, act);
            Api.get.setUri(uri);
        }

        @Override
        public ListData<SociaxItem> getContactListFooter(Contact contact,
                                                         int count) throws ApiException {
            beforeTimeline(ApiContact.GET_ALL_COLLEAGUE);
            Api.get.append("max_id", contact.getUid());
            Api.get.append("count", count);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    list = new ListData<SociaxItem>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Contact contact2 = new Contact(jsonObject);
                        list.add(contact2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public ListData<SociaxItem> getColleagueByDepartment(int departId)
                throws ApiException {
            beforeTimeline(ApiContact.GET_COLLEAGUE_BY_DEPARTMENT);
            Api.get.append("id", departId);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    list = new ListData<SociaxItem>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Contact contact2 = new Contact(jsonObject);
                        list.add(contact2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public ListData<SociaxItem> getColleagueByDepartmentFooter(
                int departId, ContactCategory category, int count)
                throws ApiException {
            return null;
        }

        @Override
        public ListData<SociaxItem> getMyContacter() throws ApiException {
            beforeTimeline(ApiContact.GET_MY_CONTACTER);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    list = new ListData<SociaxItem>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Contact contact = new Contact(jsonObject);
                        list.add(contact);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public boolean contacterCreate(ModelUser user) throws ApiException {
            return doApiRuning(user, Api.post, ApiContact.CONTACTER_CREATE);
        }

        @Override
        public boolean contacterDestroy(ModelUser user) throws ApiException {
            return doApiRuning(user, Api.post, ApiContact.CONTACTER_DESTROY);
        }

        private boolean doApiRuning(ModelUser user, Request res, String act)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiContact.MOD_NAME, act);
            Api.post.setUri(uri);
            Api.post.append("user_id", user.getUid());
            Object result = Api.run(Api.post);
            Api.checkResult(result);
            if (result != null) {
                try {
                    int stataCode = Integer.valueOf(((String) result));
                    return stataCode == 1 ? true : false;

                } catch (Exception e) {
                    Log.d(AppConstant.APP_TAG, " doruning wm" + e.toString());
                    throw new ApiException("操作失败");
                }
            }
            return false;
        }

        @Override
        public ListData<SociaxItem> getDataByDepartment(int departId,
                                                        int isDepart) throws ApiException {
            beforeTimeline(ApiContact.GET_DATA_BY_DEPARTMENT);
            Api.get.append("id", departId);
            Api.get.append("isDepart", 1);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    list = new ListData<SociaxItem>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);

                        Contact contact2 = null;
                        if (jsonObject.has("type")) {
                            if (jsonObject.getString("type").equals(
                                    "department")) {
                                contact2 = new ContactCategory(jsonObject);
                            } else if (jsonObject.getString("type").equals(
                                    "user")) {
                                contact2 = new Contact(jsonObject);
                            }
                        } else {
                            contact2 = new Contact(jsonObject);
                        }
                        list.add(contact2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public ListData<SociaxItem> getDataByDepartmentFooter(int departId,
                                                              int isDepart) throws ApiException {
            beforeTimeline(ApiContact.GET_DATA_BY_DEPARTMENT);
            Api.get.append("id", departId);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    list = new ListData<SociaxItem>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);

                        Contact contact2 = new Contact(jsonObject);
                        ;
                        list.add(contact2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public ListData<SociaxItem> searchColleague(String key)
                throws ApiException {
            beforeTimeline(ApiContact.SEARCH_COLLEAGUE);
            Api.get.append("key", key);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    list = new ListData<SociaxItem>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Contact contact = new Contact(jsonObject);
                        list.add(contact);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }
    }

    /**
     * 类说明：任务API实现类
     *
     * @author Povol
     */
    public static final class Tasks implements ApiTask {

        private Uri.Builder beforeTimeline(String act) {
            return Api.createUrlBuild(ApiTask.MOD_NAME, act);
        }

        @SuppressWarnings("finally")
        @Override
        public ListData<SociaxItem> getTaskCategoryList() throws ApiException {
            Api.get.setUri(beforeTimeline(ApiTask.GET_ALL_COLLEAGUE));
            Object result = Api.run(Api.get);

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        TaskCategory tCategory = new TaskCategory(jsonObject);
                        list.add(tCategory);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @Override
        public boolean createTaskCate(TaskCategory tCategory)
                throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.CREATE_TASK_CATEGORY));
            Api.post.append("category_name", tCategory.getName());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
                case 2:
                    Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                    throw new ApiException("分类名称重复");
            }
            return false;
        }

        @Override
        public boolean destroyTaskCate(TaskCategory tCategory)
                throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.DESTROY_TASK_CATEGORY));
            Api.post.append("category_id", tCategory.gettId());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "destroy task cate fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
                case 2:
                    Log.d(AppConstant.APP_TAG, "destroy task cate fail ....	");
                    throw new ApiException("分类中存在任务不能删除！");
            }
            return false;
        }

        @Override
        public boolean eidtTaskCate(TaskCategory tCategory) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.EDIT_TASK_CATEGORY));
            Api.post.append("category_id", tCategory.gettId());
            Api.post.append("category_name", tCategory.getName());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
                case 2:
                    Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                    throw new ApiException("分类名称重复");
            }
            return false;
        }

        @Override
        public boolean shareTaskCate(TaskCategory tCategory)
                throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.SHARE_TASK_CATEGORY));
            Api.post.append("category_id", tCategory.gettId());
            Api.post.append("user_emails", tCategory.getEmailList());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "share task cate fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
            }
            return false;
        }

        @Override
        public boolean delShareTaskCate(TaskCategory tCategory)
                throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.CANCEL_SHARE_TASK_CATEGORY));
            Api.post.append("category_id", tCategory.gettId());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "cancel share task cate fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
            }
            return false;
        }

        @SuppressWarnings("finally")
        @Override
        public ListData<SociaxItem> getTaskListByCategory(TaskCategory tCategory)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiTask.GET_TASK_BY_CATEGORY));
            Api.get.append("category_id", tCategory.gettId());
            Object result = Api.run(Api.get);

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Task task = new Task(jsonObject);
                        list.add(task);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @Override
        public boolean destroyTask(Task task) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.DESTROY_TASK));
            Api.post.append("task_id", task.getTaskId());
            Api.post.append("category_id", task.getCateId());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "destroy task fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
            }
            return false;
        }

        @Override
        public boolean createTask(Task task) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.CREATE_TASK));
            Api.post.append("task", task.getTaskTitle());
            Api.post.append("category_id", task.getCateId());
            if (task.getDeadline() != null && !task.getDeadline().equals(""))
                Api.post.append("date", task.getDeadline());
            if (task.getType() != null && !task.getType().equals(""))
                Api.post.append("type", task.getType());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                    throw new ApiException("操作失败");
                case 2:
                    Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                    throw new ApiException("分类名称重复");
                default:
                    return true;
            }
        }

        @Override
        public boolean editTask(Task task) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.EDIT_TASK));
            Api.post.append("task_id", task.getTaskId());
            Api.post.append("task", task.getTaskTitle());
            Api.post.append("user_id", task.getJoiner_uid());
            Api.post.append("date", task.getDeadline());
            Api.post.append("task_detail", task.getDesc());
            Object result = Api.run(Api.post);

            int code = -1;

            try {
                code = Integer.valueOf((String) result);
            } catch (Exception e) {
                Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                throw new ApiException("操作失败");
            }

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
                case 2:
                    Log.d(AppConstant.APP_TAG, "create task cate fail ....	");
                    throw new ApiException("分类名称重复");
            }
            return false;
        }

        @Override
        public boolean starTask(Task task) throws ApiException {

            Api.post.setUri(beforeTimeline(ApiTask.STARRED_TASK));
            Api.post.append("task_id", task.getTaskId());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "destroy task fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
            }
            return false;
        }

        @Override
        public boolean unStarTask(Task task) throws ApiException {

            Api.post.setUri(beforeTimeline(ApiTask.CANCEL_STARRED_TASK));
            Api.post.append("task_id", task.getTaskId());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "un star task fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
            }
            return false;
        }

        @Override
        public boolean doTask(Task task) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.FINISHED_TASK));
            Api.post.append("task_id", task.getTaskId());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "do task fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
            }
            return false;
        }

        @Override
        public boolean unDoTask(Task task) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiTask.CANCEL_FINISHED_TASK));
            Api.post.append("task_id", task.getTaskId());
            Object result = Api.run(Api.post);
            int code = Integer.valueOf((String) result);

            switch (code) {
                case 0:
                    Log.d(AppConstant.APP_TAG, "destroy task fail ....	");
                    throw new ApiException("操作失败");
                case 1:
                    return true;
            }
            return false;
        }

        @SuppressWarnings("finally")
        @Override
        public ListData<SociaxItem> getTaskByType(TaskCategory tCategory)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiTask.GET_TASK_BY_TYPE));
            Api.get.append("type", tCategory.getCataType());
            Object result = Api.run(Api.get);

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Task task = new Task(jsonObject);
                        list.add(task);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @Override
        public String getTaskNotify() throws ApiException {
            Api.get.setUri(beforeTimeline(ApiTask.GET_TASK_NOTIFY));
            Object result = Api.run(Api.get);
            return (String) result;
        }

        @SuppressWarnings("finally")
        @Override
        public ListData<SociaxItem> getShareUser(int ctaeId)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiTask.GET_SHARE_USERS));
            Api.get.append("category_id", ctaeId);
            Object result = Api.run(Api.get);

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelUser user = new ModelUser(jsonObject);
                        list.add(user);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @SuppressWarnings("finally")
        @Override
        public ListData<SociaxItem> getTaskBySearchKey(String key)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiTask.SEARCH_TASK));
            Api.get.append("keyword", key);
            Object result = Api.run(Api.get);

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Task task = new Task(jsonObject);
                        list.add(task);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        /**************************
         * t4
         ***********************/
        @SuppressWarnings("finally")
        @Override
        public ListData<SociaxItem> getTaskList() throws ApiException {
            Api.post = new Post();
            post.setUri(beforeTimeline(ApiTask.TASK_LIST));
            Object result = Api.run(Api.post);

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelTaskType task = new ModelTaskType(jsonObject);
                        list.add(task);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @Override
        public Object completeTask(String task_id, String task_type,
                                   String task_level) throws ApiException {
            Api.post = new Post();
            post.setUri(beforeTimeline(ApiTask.TASK_COMPLETE));
            post.append("task_id", task_id);
            post.append("task_type", task_type);
            post.append("task_level", task_level);
            return Api.run(Api.post);
        }

        /**
         * 每日任务
         */
        @Override
        public ListData<SociaxItem> getDailyTask() throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiTask.MOD_NAME, ApiTask.DAILY_TASK);
            Api.get.setUri(uri);
            Object result = Api.run(Api.get);
            Api.checkResult(result);

            Log.v("doneTask", "--------getDailyTask------json-----------" + result.toString());

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelDailyOrMainTask dailyTask = new ModelDailyOrMainTask(jsonObject);
                        list.add(dailyTask);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        /**
         * 主线任务
         */
        @Override
        public ListData<SociaxItem> getMainTask() throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiTask.MOD_NAME, ApiTask.MAIN_TASK);
            Api.get.setUri(uri);
//            Api.get.append("oauth_token", Request.getToken());
//            Api.get.append("oauth_token_secret", Request.getSecretToken());
            Object result = Api.run(Api.get);
            Api.checkResult(result);

            Log.v("doneTask", "--------resultJson-----------------" + result.toString());

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelDailyOrMainTask dailyTask = new ModelDailyOrMainTask(jsonObject);
                        list.add(dailyTask);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        /**
         * 副本任务
         */
        @SuppressLint("NewApi")
        @Override
        public ListData<SociaxItem> getCopyTask() throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiTask.MOD_NAME, ApiTask.COPY_TASK);
            Api.get.setUri(uri);
            Api.get.append("oauth_token", Request.getToken());
            Api.get.append("oauth_token_secret", Request.getSecretToken());
            Object result = Api.run(Api.get);
            Api.checkResult(result);

            Log.v("doneTask", "--------resultJson-----------------" + result.toString());

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelCopyTask dailyTask = new ModelCopyTask(jsonObject);
                        list.add(dailyTask);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        /**
         * 获取资源文件下的json文件数据
         */
        public String getJsonFromRaw() {
            //将json文件读取到buffer数组中
            InputStream is = mContext.getResources().openRawResource(R.raw.taskjson);
            String json = null;
            try {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                //将字节数组转换为以utf-8编码的字符串
                json = new String(buffer, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    /**
     * 文档api实现类
     */
    public static final class Documents implements ApiDocument {

        @SuppressWarnings("finally")
        @Override
        public ListData<SociaxItem> getDocumentCategoryList()
                throws ApiException {
            beforeTimeline(ApiDocument.CATEGORY_LIST);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ContactCategory contactCategory = new ContactCategory(
                                jsonObject);
                        list.add(contactCategory);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        /**
         * 获取文档列表
         */
        @Override
        public ListData<SociaxItem> getDocumentList() throws ApiException {

            beforeTimeline(ApiDocument.ALL_DOCUMENTLIST);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONObject odata = new JSONObject((String) result);

                    JSONArray data = odata.getJSONArray("data");
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Document document = new Document(jsonObject);
                        list.add(document);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiDocument.MOD_NAME, act);
            Api.get.setUri(uri);
        }

    }

    public static final class MobileApps implements ApiMobileApps {

        @Override
        public ListData<SociaxItem> getMobileAppsList() throws ApiException {
            beforeTimeline(ApiMobileApps.GET_APPS_LIST);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        MobileApp mobileApp = new MobileApp(jsonObject);
                        list.add(mobileApp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public ListData<SociaxItem> getUserAppsList() throws ApiException {
            beforeTimeline(ApiMobileApps.GET_USER_APPS_LIST);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        MobileApp mobileApp = new MobileApp(jsonObject);
                        list.add(mobileApp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public ListData<SociaxItem> searchAppsList() throws ApiException {
            return null;
        }

        @Override
        public boolean installApp(int uid, int appid) throws ApiException {
            beforeTimeline(ApiMobileApps.INSTALL);

            Api.get.append("app_id", appid);
            Object result = Api.run(Api.get);
            if (result.equals("1")) {
                return true;
            } else {
                return true;
            }
        }

        @Override
        public boolean uninstallApp(int uid, int appid) throws ApiException {
            beforeTimeline(ApiMobileApps.UN_INSTALL);

            // Api.get.append("user_id", uid);
            Api.get.append("app_id", appid);
            Object result = Api.run(Api.get);
            if (result.equals("1")) {
                return true;
            } else {
                return true;
            }
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiMobileApps.MOD_NAME, act);
            Api.get.setUri(uri);
        }

    }

    /**
     * 微吧接口实现类
     */
    public static final class WeibaApi implements ApiWeiba {

        /**
         * 处理请求微吧返回的数据
         *
         * @param result
         * @return 微博list
         */
        @SuppressWarnings("finally")
        private ListData<SociaxItem> getWeibaList(Object result) {
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Weiba weiba = new Weiba(jsonObject);
                        list.add(weiba);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @Override
        public List<SociaxItem> getWeibas() throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.GET_WEIBAS));
            return getWeibaList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> getWeibasHeader(Weiba weiba, int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.GET_WEIBAS));
            Api.get.append("since_id", weiba.getWeibaId());
            return getWeibaList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> getWeibasFooter(Weiba weiba, int page, int count)
                throws ApiException {

            Get get = new Get();
            get.setUri(beforeTimeline(ApiWeiba.GET_WEIBAS));
            get.append("page", page);
            // Api.get.setUri(beforeTimeline(ApiWeiba.GET_WEIBAS));
            // Api.get.append("max_id", weiba.getWeibaId());
            return getWeibaList(Api.run(get));
        }

        @Override
        public boolean create(int weibaId) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.CREATE));
            Api.get.append("id", weibaId);
            return getBoolValue(Api.run(Api.get));
        }

        @Override
        public boolean destroy(int weibaId) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.DESTROY));
            Api.get.append("id", weibaId);
            return getBoolValue(Api.run(Api.get));
        }

        /**
         * 处理请求帖子列表返回的数据
         *
         * @param result
         * @return 帖子list
         */
        @SuppressWarnings("finally")
        private ListData<SociaxItem> getPostsList(Object result) {
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Posts post = new Posts(jsonObject);
                        list.add(post);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        private boolean getBoolValue(Object result) {
            return Integer.valueOf((String) result) == 1 ? true : false;
        }

        @Override
        public void favPost(ModelPost post, final HttpResponseListener listener) throws ApiException, JSONException {
            RequestParams params = new RequestParams();
            params.put("post_id", post.getPost_id());
            params.put("weiba_id", post.getWeiba_id());
            params.put("post_uid", post.getPost_uid());
            params.put("user_id", Thinksns.getMy().getUid());

            String[] mod_act;
            if (!post.isIs_favourite()) {
                mod_act = new String[]{WeibaApi.MOD_NAME, WeibaApi.FAVOURITE};
            } else {
                mod_act = new String[]{WeibaApi.MOD_NAME, WeibaApi.UN_FAVOURITE};
            }

            ApiHttpClient.get(mod_act, params, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (listener != null) {
                        if (throwable instanceof ConnectTimeoutException)
                            listener.onError("网络连接超时...");
                        else if (throwable instanceof UnknownHostException) {
                            listener.onError("服务器连接失败");
                        }
                    }
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (listener != null) {
                        listener.onSuccess(response);
                    }
                    super.onSuccess(statusCode, headers, response);
                }

            });
        }

        @Override
        public List<SociaxItem> getPosts(int weibaId) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.GET_POSTS));
            Api.get.append("id", weibaId);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> getPostsHeader(int weibaId, int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.GET_POSTS));
            Api.get.append("id", weibaId);
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> getPostsFooter(int weibaId, int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.GET_POSTS));
            Api.get.append("id", weibaId);
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public Posts postDetail(int postsId) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.POST_DETAIL));
            Api.get.append("id", postsId);
            String result = (String) Api.run(get);
            try {
                return (!result.equals("null")) ? new Posts(new JSONObject(
                        result)) : null;
            } catch (DataInvalidException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<SociaxItem> getCommentList(int postId) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.COMMENT_LIST));
            Api.get.append("id", postId);
            return getCommentList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> getCommentListHeader(int postId, int page,
                                                     int count) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.COMMENT_LIST));
            Api.get.append("id", postId);
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getCommentList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> getCommentListFooter(int postId, int page,
                                                     int count) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.COMMENT_LIST));
            Api.get.append("id", postId);
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getCommentList(Api.run(Api.get));
        }

        /**
         * 处理请求微吧返回的数据
         *
         * @param result
         * @return 微博list
         */
        @SuppressWarnings("finally")
        private ListData<SociaxItem> getCommentList(Object result) {
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        CommentPost cPost = new CommentPost(jsonObject);
                        list.add(cPost);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @Override
        public boolean commentPost(CommentPost cPost) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiWeiba.COMMENT_POST));
            Api.post.append("id", cPost.getPostId());
            Api.post.append("content", cPost.getContent());
            return getBoolValue(Api.run(Api.post));
        }

        @Override
        public boolean favoritePost(int postId) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiWeiba.POST_FAVORITE));
            Api.post.append("id", postId);
            return getBoolValue(Api.run(Api.post));
        }

        @Override
        public boolean unfavoritePost(int postId) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiWeiba.POST_UNFAVORITE));
            Api.post.append("id", postId);
            return getBoolValue(Api.run(Api.post));
        }

        @Override
        public boolean replyComment(CommentPost cPost) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiWeiba.REPLY_COMMENT));
            Api.post.append("id", cPost.getPostId());
            Api.post.append("content", cPost.getContent());
            return getBoolValue(Api.run(Api.post));
        }

        @Override
        public boolean deleteComment() throws ApiException {
            return false;
        }

        @Override
        public List<SociaxItem> followingPosts() throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.FOLLOWING_POSTS));
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> followingPostsHeader(int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.FOLLOWING_POSTS));
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> followingPostsFooter(int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.FOLLOWING_POSTS));
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> posteds(int uid) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.POSTEDS));
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> postedsHeader(int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.POSTEDS));
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> postedsFooter(int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.POSTEDS));
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> commenteds(int uid) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.COMMENTEDS));
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> commentedsHeader(int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.COMMENTEDS));
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> commentedsFooter(int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.COMMENTEDS));
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> favoritePostsList(int uid) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.FAVORITE_POSTS));
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> favoritePostsListHeader(int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.FAVORITE_POSTS));
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> favoritePostsListFooter(int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.FAVORITE_POSTS));
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> searchWeiba(String key) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.SEARCH_WEIBA));
            Api.get.append("keyword", key);
            return getWeibaList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> searchWeibaHeader(String key, int page,
                                                  int count) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.SEARCH_WEIBA));
            Api.get.append("keyword", key);
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getWeibaList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> searchWeibaFooter(String key, int page,
                                                  int count) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.SEARCH_WEIBA));
            Api.get.append("keyword", key);
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getWeibaList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> searchPost(String key) throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.SEARCH_POST));
            Api.get.append("keyword", key);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> searchPostHeader(String key, int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.SEARCH_POST));
            Api.get.append("keyword", key);
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        @Override
        public List<SociaxItem> searchPostFooter(String key, int page, int count)
                throws ApiException {
            Api.get.setUri(beforeTimeline(ApiWeiba.SEARCH_POST));
            Api.get.append("keyword", key);
            Api.get.append("page", page);
            Api.get.append("count", count);
            return getPostsList(Api.run(Api.get));
        }

        /*************************** T4 **********************************/
        /**
         * 组装url
         *
         * @param act
         * @return
         */
        private Uri.Builder beforeTimeline(String act) {
            return Api.createUrlBuild(ApiWeiba.MOD_NAME, act);
        }

        /**
         * 处理请求微吧返回的数据
         *
         * @param result
         * @param firstTitileName 第一个的title（例如推荐、全部）,传null则不使用title
         * @return 微博list
         */
        @SuppressWarnings("finally")
        private ListData<SociaxItem> getWeibaList(Object result, String firstTitileName) {
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelWeiba weiba = new ModelWeiba(jsonObject);
                        if (i == 0 && firstTitileName != null) {
                            weiba.setFirstInPart(true);
                            weiba.setStr_partName(firstTitileName + "("
                                    + length + ")");
                        }

                        if (firstTitileName.equals("我加入的"))
                            weiba.setFollow(true);
                        list.add(weiba);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        /**
         * 处理请求微吧返回的数据
         *
         * @param result
         * @param firstTitileName 第一个的title（例如推荐、全部）,传null则不使用title
         * @return 微博list
         */
        @SuppressWarnings("finally")
        private ListData<SociaxItem> getPostList(Object result,
                                                 String firstTitileName) {
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelPost post = new ModelPost(jsonObject);
                        if (i == 0 && firstTitileName != null) {
                            post.setFirstInPart(true);
                            post.setStr_part_name(firstTitileName);
                        }
                        if (i == length - 1 && firstTitileName != null
                                && firstTitileName.equals("热门推荐")) {
                            post.setLastInPart(true);
                        }
                        list.add(post);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @Override
        public ListData<SociaxItem> getMyWeibaList(int count, int max_id,
                                                   final HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.MY_WEIBA}, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(errorResponse);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (listener != null) {
                        ListData<SociaxItem> returnlist = new ListData<SociaxItem>();
                        try {
                            //提取我关注的微吧列表
                            if (response.has("my")) {
                                JSONArray joinArray = response.getJSONArray("my");
                                for (int i = 0; i < joinArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = joinArray.getJSONObject(i);
                                        ModelWeiba weiba = new ModelWeiba(jsonObject);
                                        if (i == 0) {
                                            weiba.setFirstInPart(true);
                                            weiba.setStr_partName("我加入的(" + joinArray.length() + ")");
                                        }

                                        weiba.setFollow(true);
                                        returnlist.add(weiba);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            //提取系统推荐的微吧列表
                            if (response.has("recommend")) {
                                JSONArray recommendArray = response.getJSONArray("recommend");
                                if(recommendArray.length() > 0) {
                                    for (int i = 0; i < recommendArray.length(); i++) {
                                        try {
                                            JSONObject jsonObject = recommendArray.getJSONObject(i);
                                            ModelWeiba weiba = new ModelWeiba(jsonObject);
                                            if (i == 0) {
                                                weiba.setFirstInPart(true);
                                                weiba.setStr_partName("推荐的(" + recommendArray.length() + ")");
                                            }
                                            returnlist.add(weiba);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }else {
                                    ModelWeiba weiba = new ModelWeiba();
                                    weiba.setFirstInPart(true);
                                    weiba.setStr_partName("推荐的(" + recommendArray.length() + ")");
                                    returnlist.add(weiba);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        listener.onSuccess(returnlist);
                    }

                    super.onSuccess(statusCode, headers, response);
                }

            });
            return null;
        }

        @SuppressWarnings("finally")
        @Override
        public ListData<SociaxItem> getRecommendWeibaList(int count, int max_id)
                throws ApiException {
            post = new Post();
            post.setUri(beforeTimeline(ApiWeiba.RECOMMED_WEIBA));
            post.append("limit", count);
            post.append("max_id", max_id);
            Object result = Api.run(post);
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelWeiba weiba = new ModelWeiba(jsonObject);
                        if (i == 0) {// 分栏第一个
                            weiba.setFirstInPart(true);
                            weiba.setStr_partName("推荐的");
                        }
                        list.add(weiba);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        @Override
        public Object getWeibaPostList(int weiba_id, int count, int max_id, final HttpResponseListener listener) {
            RequestParams params = new RequestParams();
            params.put("weiba_id", weiba_id);
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.GET_WEIBA_POST_LIST}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (listener != null) {
                                listener.onError(throwable.toString());
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.v("API", response.toString());
                            if (listener != null) {
                                listener.onSuccess(response);
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                    });
            return null;
        }

        @Override
        public Object changeWeibaFollow(int weiba_id, boolean isfollow)
                throws ApiException {
            post = new Post();
            post.setUri(beforeTimeline(isfollow ? ApiWeiba.UNFOLLOW
                    : ApiWeiba.DOFOLLOW));
            post.append("weiba_id", weiba_id);
            Object result = Api.run(post);
            return result.toString();
        }

        public void changeWeibaFollow(int weiba_id, boolean isfollow, AsyncHttpResponseHandler handler) {
            RequestParams params = new RequestParams();
            params.put("weiba_id", weiba_id);
            if (isfollow) {
                ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.UNFOLLOW}, params, handler);
            } else {
                ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.DOFOLLOW}, params, handler);
            }

        }

        @Override
        public Object getPostDetail(int post_id) throws ApiException {
            post = new Post();
            post.setUri(beforeTimeline(ApiWeiba.POST_DETAIL));
            post.append("id", post_id);
            Object result = Api.run(post);
            return result.toString();
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getPostCommentList(int pageCount,
                                                       int feed_id, int maxid, final HttpResponseListener listener) throws VerifyErrorException,
                ApiException, ListAreEmptyException, DataInvalidException,
                ExceptionIllegalParameter {
            post = new Post();
            post.setUri(beforeTimeline(ApiWeiba.WEIBA_COMMENTS));
            RequestParams params = new RequestParams();
            params.put("feed_id", feed_id);
            params.put("count", pageCount);
            params.put("max_id", maxid);
            ApiHttpClient.get(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.WEIBA_COMMENTS}, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                    Object result = new String(arg2);
                    if (listener != null) {
                        try {
                            listener.onSuccess((ListData<SociaxItem>) afterRequest(
                                    ListData.DataType.MODEL_COMMENT, result));
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError(e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                    if (listener != null)
                        listener.onError(arg3.toString());
                }
            });
//			return (ListData<SociaxItem>) afterRequest(
//					ListData.DataType.MODEL_COMMENT, Api.run(post));
            return null;
        }

        @Override
        public Object getChangePostFavourite(int post_id, int weiba_id,
                                             int post_uid, String preStatus) throws ApiException {
            post = new Post();
            post.setUri(beforeTimeline(preStatus.equals("1") ? ApiWeiba.UN_FAVOURITE
                    : ApiWeiba.FAVOURITE));
            post.append("post_id", post_id);
            post.append("weiba_id", weiba_id);
            post.append("post_uid", post_uid);
            Object result = Api.run(post);
            return result.toString();
        }

        @Override
        public Object getChangePostDigg(int post_id, int weiba_id,
                                        int post_uid, String preStatus) throws ApiException {
            post = new Post();
            post.setUri(beforeTimeline(preStatus.equals("1") ? ApiWeiba.UN_DIGG
                    : ApiWeiba.DIGG));
            post.append("post_id", post_id);
            post.append("weiba_id", weiba_id);
            post.append("post_uid", post_uid);
            Object result = Api.run(post);

            Log.v("postTest","getChangePostDigg/json/post_id/weiba_id/post_uid"
                    +result.toString()+"/"+post_id+"/"+weiba_id+"/"+post_uid);

            return result.toString();
        }

        @Override
        public ListData<SociaxItem> getPostDigest(int weiba_id, int pageCount,
                                                  int max_id, final HttpResponseListener listener) throws VerifyErrorException, ApiException,
                ListAreEmptyException, DataInvalidException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("weiba_id", weiba_id);
            params.put("max_id", max_id);
            params.put("count", pageCount);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.GET_DIGEST_ALL}, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                    String str = new String(arg2);
                    ListData<SociaxItem> returnlist = new ListData<SociaxItem>();
                    if (listener != null) {
                        try {
                            JSONArray postlist = new JSONArray(str);
                            for (int i = 0; i < postlist.length(); i++) {
                                ModelPost md = new ModelPost(postlist.getJSONObject(i));
                                returnlist.add(md);
                            }
                            listener.onSuccess(returnlist);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError("数据解析错误");
                        }
                    }
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                    if (listener != null)
                        listener.onError(arg3.toString());
                }
            });
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getPostAll(int weiba_id, int pageCount,
                                               int max_id) throws VerifyErrorException, ApiException,
                ListAreEmptyException, DataInvalidException,
                ExceptionIllegalParameter {
            post = new Post();
            post.setUri(beforeTimeline(ApiWeiba.ALL_POST));
            post.append("weiba_id", weiba_id);
            post.append("max_id", max_id);
            post.append("count", pageCount);
            return (ListData<SociaxItem>) afterRequest(
                    ListData.DataType.MODEL_POST, Api.run(post));
        }

        //获取所有帖子，适用于逛一逛微吧
        public void getPostAll(int weiba_id, int pageCount, int max_id, AsyncHttpResponseHandler handler) {
            RequestParams params = new RequestParams();
            params.put("weiba_id", weiba_id);
            params.put("max_id", max_id);
            params.put("count", pageCount);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.ALL_POST}, params, handler);
        }

        @Override
        public String getWeiba(int id, final HttpResponseListener listener) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("id", id);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.DETAIL}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (listener != null) {
                                listener.onError(throwable.toString());
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.v("API", response.toString());
                            if (listener != null) {
                                listener.onSuccess(response);
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                    });
            return null;
        }

        /**
         * 删除帖子
         * @param post_id 帖子id
         * @param listener
         * @return
         * @throws ApiException
         */
        @Override
        public ModelBackMessage delPost(int post_id,final HttpResponseListener listener) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("post_id", post_id);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.DEL_POST}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (listener != null) {
                                listener.onError(throwable.toString());
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.v("API", response.toString());
                            if (listener != null&&response!=null) {
                                ModelBackMessage message = null;
                                try {
                                    message = new ModelBackMessage(response.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                listener.onSuccess(message);
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                    });
            return null;
        }

        @Override
        public String getWeibaUrl(int id, final HttpResponseListener listener) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("id", id);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.GET_WEIBA_URL}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (listener != null) {
                                listener.onError(throwable.toString());
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.v("API", response.toString());
                            if (listener != null) {
                                listener.onSuccess(response);
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                    });
            return null;
        }

        @Override
        public ListData<SociaxItem> findWeiba(int pageCount, String categories, int maxid, int count, final HttpResponseListener listener) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("limit", pageCount);
            params.put("key", categories);
            params.put("count", count);
            params.put("max_id", maxid);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.FIND_WEIBA}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            if (listener != null) {
                                listener.onError(errorResponse);
                            }
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            Log.v("Api", "findWeiba-->" + response.toString());
                            JSONArray arr = null;
                            try {
                                arr = (JSONArray) response.get(1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (listener != null) {
                                int length = arr.length();
                                ListData<SociaxItem> list = new ListData<SociaxItem>();
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = arr.getJSONObject(i);
                                        ModelWeiba weiba = new ModelWeiba(jsonObject);
                                        list.add(weiba);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                listener.onSuccess(list);
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                    });
            return null;
        }

        /**
         * 搜索帖子
         *
         * @param key
         * @param listener
         * @return
         * @throws ApiException
         */
        @Override
        public ListData<SociaxItem> searchTopic(String key, final HttpResponseListener listener) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("key", key);
            params.put("weiba_id", 0);
            ApiHttpClient.get(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.SEARCH_TOPIC}, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Object result = new String(bytes);
                    if (listener != null) {
                        try {
                            listener.onSuccess(afterRequest(ListData.DataType.MODEL_POST, result));
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError(e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    throwable.printStackTrace();
                    if (listener != null) {
                        if (throwable instanceof ConnectTimeoutException)
                            listener.onError("连接超时，请检查您的网络设置");
                        else if (throwable instanceof UnknownHostException) {
                            listener.onError("服务器连接失败");
                        }
                    }
                }
            });
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getPostHot(int pageCount, int max_id)
                throws VerifyErrorException, ApiException,
                ListAreEmptyException, DataInvalidException,
                ExceptionIllegalParameter {
            post = new Post();
            post.setUri(beforeTimeline(ApiWeiba.HOT_POST));
            post.append("max_id", max_id);
            post.append("count", pageCount);
            return (ListData<SociaxItem>) afterRequest(
                    ListData.DataType.MODEL_POST, Api.run(post));
        }

        public ListData<SociaxItem> collectPost(final HttpResponseListener listener) throws Exception {
            ApiHttpClient.get(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.COLLECT_POST}, null, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(errorResponse);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (listener != null) {

                        ListData<SociaxItem> returnlist = new ListData<SociaxItem>();
                        try {
                            if (response.has("data")) {
                                returnlist = getPostList(response.getString("data"), null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listener.onSuccess(returnlist);
                    }
                    super.onSuccess(statusCode, headers, response);
                }
            });
            return null;
        }

        public void collectPost(final AsyncHttpResponseHandler listener) {
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.COLLECT_POST}, null, listener);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getRecommendTopic(int pageCount, final int max_id, final HttpResponseListener listener)
                throws VerifyErrorException, ApiException,
                ListAreEmptyException, DataInvalidException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("max_id", max_id);
            params.put("count", pageCount);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.RECOMMEND_TOPIC}, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(errorResponse);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (listener != null) {

                        ListData<SociaxItem> returnlist = new ListData<SociaxItem>();
                        try {
                            if (max_id == 0) {
                                ListData<SociaxItem> tempMy = getPostList(
                                        response.getString("my"), "我关注的微吧帖子");
                                ListData<SociaxItem> tempRecommend = getPostList(
                                        response.getString("commend"), "热门推荐");

                                if (tempRecommend != null && tempRecommend.size() > 0) {
                                    returnlist.addAll(tempRecommend);
                                } else {
                                    returnlist.add(new ModelPost("热门推荐"));
                                }
                                if (tempMy != null && tempMy.size() > 0) {
                                    returnlist.addAll(tempMy);
                                } else {
                                    returnlist.add(new ModelPost("我关注的微吧帖子"));
                                }
                                tempMy = null;
                                tempRecommend = null;
                            } else {
                                ListData<SociaxItem> tempMy = getPostList(
                                        response.getString("my"), null);
                                returnlist.addAll(tempMy);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onSuccess(returnlist);
                    }
                    super.onSuccess(statusCode, headers, response);
                }

            });
            return null;
        }

        @Override
        public ListData<SociaxItem> getAllWeibaList(int pageCount, int maxid, final HttpResponseListener listener)
                throws ApiException {
            RequestParams params = new RequestParams();
            params.put("count", pageCount);
            params.put("max_id", maxid);
            ApiHttpClient.post(new String[]{ApiWeiba.MOD_NAME, ApiWeiba.ALL_WEIBA}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            if (listener != null) {
                                listener.onError(errorResponse);
                            }
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            Log.v("Api", "getAllWeiba-->" + response.toString());
                            if (listener != null) {
                                int length = response.length();
                                ListData<SociaxItem> list = new ListData<SociaxItem>();
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = response.getJSONObject(i);
                                        ModelWeiba weiba = new ModelWeiba(jsonObject);
                                        list.add(weiba);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                listener.onSuccess(list);
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                    });
//			return getWeibaList(Api.run(Api.post), null);
            return null;
        }

        @Override
        public ListData<SociaxItem> searchWeibaList(int pageCount, int maxid,
                                                    String key) throws ApiException {
            post = new Post();
            post.setUri(beforeTimeline(ApiWeiba.ALL_WEIBA));
            post.append("count", pageCount);
            post.append("max_id", maxid);
            post.append("key", key);
            return getWeibaList(Api.run(Api.post), null);
        }

        @Override
        public boolean cretePost(ModelPost posts) throws ApiException {
            Api.post.setUri(beforeTimeline(ApiWeiba.ADD_POST));
            Api.post.append("weiba_id", posts.getWeiba_id());
            Api.post.append("title", posts.getTitle());
            Api.post.append("content", posts.getContent());

            Object result = Api.run(Api.post);
            try {
                JSONObject result2json = new JSONObject(result.toString());
                try {
                    return result2json.getString("status").equals("1");
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public ModelBackMessage replyPost(ModelComment comment)
                throws ApiException, VerifyErrorException, UpdateException {
            String result = null;
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiWeiba.MOD_NAME,
                        ApiWeiba.REPLY_POST);
                post = new Post();
                post.setUri(uri);
                post.append("content", comment.getContent());
                post.append("post_id", comment.getComment_id());
                post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
                if (comment.getIsShareFeed() != null)
                    post.append("ifShareFeed", comment.getIsShareFeed());
                result = Api.run(post).toString();
                ModelBackMessage message = new ModelBackMessage(result);
                return message;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public ModelBackMessage replyComment(ModelComment comment, String comment_user)
                throws ApiException, VerifyErrorException, UpdateException {
            String result = null;
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiWeiba.MOD_NAME,
                        ApiWeiba.REPLY_POST);
                post = new Post();
                post.setUri(uri);
                post.append("content", "回复 @" + comment_user + ": " + comment.getContent());
                post.append("post_id", comment.getComment_id());
                post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
                if (comment.getIsShareFeed() != null)
                    post.append("ifShareFeed", comment.getIsShareFeed());
                result = Api.run(post).toString();
                ModelBackMessage message = new ModelBackMessage(result);
                return message;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public ModelBackMessage createNewPostWithImage(ModelPost posts,
                                                       FormFile[] filelist) throws ApiException, VerifyErrorException,
                UpdateException {
            String result = null;
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiWeiba.MOD_NAME,
                        ApiWeiba.UPLOAD_POST);

                Post post = new Post();
                post.setUri(uri);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("weiba_id", posts.getWeiba_id() + "");
                param.put("title", posts.getTitle());
                param.put("content", posts.getContent());
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());
                param.put("from", ModelWeibo.From.ANDROID.ordinal() + "");
                result = FormPost.postPicOnly(uri.toString(), param, filelist);
                ModelBackMessage message = new ModelBackMessage(result);
                return message;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Object creteNewPost(ModelPost posts) throws ApiException {
            post = new Post();
            post.setUri(beforeTimeline(ApiWeiba.ADD_POST));
            post.append("weiba_id", posts.getWeiba_id());
            post.append("title", posts.getTitle());
            post.append("content", posts.getContent());
            post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
            Object result = Api.run(post);
            return result.toString();
        }

        //获取某个帖子的点赞用户
        public void getPostDiggUserList(int post_id, int max_id, AsyncHttpResponseHandler handler) {
            RequestParams params = new RequestParams();
            params.put("post_id", post_id);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{"Weiba", "digg_lists"}, params, handler);
        }
    }

    /**
     * 频道接口实现类
     */
    public static final class ChannelApi implements ApiChannel {
        /*************************
         * T4
         ***************************/
        @Override
        public ListData<SociaxItem> getAllChannel(int pageCount, int maxid, final HttpResponseListener listener)
                throws ApiException {
            ApiHttpClient.post(new String[]{ApiChannel.MOD_NAME, ApiChannel.GET_ALL_CHANNEL}, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null)
                        listener.onError(errorResponse.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (listener != null) {
                        int length = response.length();
                        ListData<SociaxItem> list = new ListData<SociaxItem>();
                        for (int i = 0; i < length; i++) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject = response.getJSONObject(i);
                                ModelChannel c;
                                try {
                                    c = new ModelChannel(jsonObject);
                                    list.add(c);
                                } catch (DataInvalidException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onSuccess(list);
                    }
                    super.onSuccess(statusCode, headers, response);
                }

            });

            return null;

        }

        public void getAllChannel(int pageCount, int maxid, final AsyncHttpResponseHandler listener) {
            ApiHttpClient.post(new String[]{ApiChannel.MOD_NAME, ApiChannel.GET_ALL_CHANNEL},
                    listener);
        }

        @Override
        public ListData<SociaxItem> getUserChannel(int pageCount, int maxid, final HttpResponseListener listener)
                throws ApiException {
            ApiHttpClient.post(new String[]{ApiChannel.MOD_NAME, ApiChannel.GET_USER_CHANNEL}, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    // TODO Auto-generated method stub
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(errorResponse.toString());
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (listener != null) {
                        ListData<SociaxItem> list = null;
                        int length = response.length();
                        list = new ListData<SociaxItem>();
                        for (int i = 0; i < length; i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                ModelChannel c = new ModelChannel(jsonObject, "");
                                list.add(c);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (DataInvalidException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onSuccess(list);
                    }
                    super.onSuccess(statusCode, headers, response);
                }

            });

            return null;
        }

        public void getUserChannel(int pageCount, int maxid, final AsyncHttpResponseHandler listener) {
            ApiHttpClient.post(new String[]{ApiChannel.MOD_NAME, ApiChannel.GET_USER_CHANNEL},
                    listener);
        }

        /*************************
         * T4
         ***************************/

        @Override
        public ListData<SociaxItem> getChannelWeibo(String channelId, int max_id, int count, int type, final HttpResponseListener listener)
                throws ApiException, ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("channel_category_id", channelId);
            params.put("max_id", max_id);
            params.put("count", count);
            params.put("type", type);
            ApiHttpClient.get(new String[]{ApiChannel.MOD_NAME, ApiChannel.GET_CHANNEL_DETAIL}, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int index, Header[] headers, byte[] bytes) {
                    try {
                        JSONObject json = new JSONObject(new String(bytes));
                        JSONArray data = json.optJSONArray("feed_list");
                        ListData<SociaxItem> returnlist = new ListData<SociaxItem>();
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                ModelWeibo weibo = new ModelWeibo(data.getJSONObject(i));
                                returnlist.add(weibo);
                            }
                            listener.onSuccess(returnlist);
                        }

                        listener.onSuccess(returnlist);

                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onError("数据解析错误");
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    throwable.printStackTrace();
                    if (listener != null) {
                        if (throwable instanceof ConnectTimeoutException)
                            listener.onError("连接超时，请检查您的网络设置");
                        else if (throwable instanceof UnknownHostException) {
                            listener.onError("服务器连接失败");
                        }
                    }
                }
            });
            return null;

        }

        public void getChannelWeibo(String channelId, int max_id, int count, int type, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("channel_category_id", channelId);
            params.put("max_id", max_id);
            params.put("count", count);
            params.put("type", type);
            ApiHttpClient.get(new String[]{ApiChannel.MOD_NAME, ApiChannel.GET_CHANNEL_DETAIL},
                    params, listener);

        }

        @Override
        public ListData<SociaxItem> getChannelFooter(int channel_id,
                                                     ModelWeibo last) throws ApiException {

            Uri.Builder uri = Api.createUrlBuild(ApiChannel.MOD_NAME,
                    ApiChannel.GET_CHANNEL_DETAIL);

            Get get = new Get();
            get.setUri(uri);
            get.append("channel_category_id", channel_id + "");
            if (last != null) {
                get.append("max_id", last.getChannel_category_id());
            } else {
                get.append("max_id", 0);
            }

            Log.v("channelUrl", "" + uri.toString() + "&oauth_token=" + Request.getToken()
                    + "&oauth_token_secret=" + Request.getSecretToken() + "&channel_category_id=" + channel_id + "&max_id="
                    + last.getChannel_category_id());
            return getChannelFeedList(Api.run(get));
        }

        @Override
        public ListData<SociaxItem> getChannelFeed(int channelId, int page)
                throws ApiException {
            Get get = new Get();
            get.setUri(Api.createUrlBuild(ApiChannel.MOD_NAME,
                    ApiChannel.GET_CHANNEL_FEED));
            get.append("category_id", channelId);
            get.append("page", page);
            // Api.get.append("category_id", channelId);
            return getChannelFeedList(Api.run(get));
        }

        @Override
        public ListData<SociaxItem> getChannelHeaderFeed(ModelWeibo weibo,
                                                         int channelId, int page) throws ApiException {
            Get get = new Get();
            get.setUri(Api.createUrlBuild(ApiChannel.MOD_NAME,
                    ApiChannel.GET_CHANNEL_FEED));
            get.append("category_id", channelId);
            get.append("page", page);
            // Api.get.append("max_id", weibo.getWeiboId());
            return getChannelFeedList(Api.run(get));
        }

        @Override
        public ListData<SociaxItem> getChannelFooterFeed(ModelWeibo weibo,
                                                         int channelId, int page) throws ApiException {
            Get get = new Get();
            get.setUri(Api.createUrlBuild(ApiChannel.MOD_NAME,
                    ApiChannel.GET_CHANNEL_FEED));
            get.append("category_id", channelId);
            get.append("page", page);
            return getChannelFeedList(Api.run(get));
        }

        @SuppressWarnings("finally")
        private ListData<SociaxItem> getChannelFeedList(Object result) {
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    Log.v("channel", "-------channel json---------" + result.toString());

                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        ModelWeibo w = new ModelWeibo(jsonObject);
                        list.add(w);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            } finally {
                return list;
            }
        }

        /**
         * 讲结果封装成ModelChannel list
         *
         * @param result
         * @return
         */
        @SuppressWarnings({"rawtypes"})
        private ListData<SociaxItem> getChannelList(Object result) {
            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {

                    JSONArray data = new JSONArray((String) result);
                    Log.i("channel", "getChannelList(Object result)=获取所有频道的数据"
                            + data.toString());
                    int length = data.length();
                    Log.i("channel",
                            "getChannelList(Object result)=获取数组长度length="
                                    + length);
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject = data.getJSONObject(i);
                        Log.i("channel",
                                "getChannelList(Object result)=获取所有频道的数据data.getJSONObject(i)="
                                        + jsonObject.toString());
                        ModelChannel c = new ModelChannel(jsonObject);
                        Log.i("channel", "getChannelList" + c.toString());
                        list.add(c);
                    }

                }
            } catch (Exception e) {
                try {
                    Log.i("channel", "getChannelList(Object result)---catch==");
                    list = new ListData<SociaxItem>();
                    JSONObject itemData = new JSONObject(result.toString());
                    for (Iterator iterator = itemData.keys(); iterator
                            .hasNext(); ) {
                        String key = (String) iterator.next();
                        JSONObject jsonObject = itemData.getJSONObject(key);
                        ModelChannel c = new ModelChannel(jsonObject);
                        Log.i("channel",
                                "getChannelList(Object result)---catch=="
                                        + c.toString());
                        list.add(c);
                    }
                } catch (Exception e2) {
                    Log.d(TAG, e2.toString());
                    Log.i("channel",
                            "getChannelList(Object result)---catch==get channel error ...");
                }
                Log.i("channel",
                        "getChannelList(Object result)---catch==get channel error ...");
                Log.d(TAG, "get channel error ... ");
            }
            return list;
        }

        /**
         * @param channel_category_id
         * @param type                取消关注type=0，添加=1 channel_category_id
         * @return
         */
        public Object changeFollow(String channel_category_id, String type) {
            // TODO Auto-generated method stub
            Get get = new Get();
            get.setUri(Api.createUrlBuild(ApiChannel.MOD_NAME,
                    ApiChannel.CHANNEL_FOLLOW));
            get.append("channel_category_id", channel_category_id);
            get.append("type", type);
            Object result = null;
            try {
                result = Api.run(get);
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 群组接口实现类
     */
    public static final class GroupApi implements ApiGroup {

        private Uri.Builder baseUrl(String act) {
            return Api.createUrlBuild(ApiGroup.MOD_NAME, act);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public ListData<SociaxItem> showStatuesType() throws ApiException {
            Get get = new Get();
            get.setUri(baseUrl(ApiGroup.SHOW_STATUSES_TYPE));
            Object result = Api.run(get);
            ListData<SociaxItem> list = null;
            try {
                list = new ListData<SociaxItem>();
                JSONObject itemData = new JSONObject(result.toString());

                for (Iterator iterator = itemData.keys(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    String temp = itemData.getString(key);
                    StringItem item = new StringItem(Integer.valueOf(key), temp);
                    list.add(item);
                }

            } catch (JSONException e) {
                Log.d(TAG,
                        "get group show statues type error wm " + e.toString());
            }
            return list;
        }

        private ListData<SociaxItem> getStatueList(Object result,
                                                   ListData.DataType type) {
            ListData<SociaxItem> list = null;
            try {
                JSONArray data = new JSONArray((String) result);
                int length = data.length();
                list = new ListData<SociaxItem>();
                for (int i = 0; i < length; i++) {
                    try {
                        JSONObject itemData = data.getJSONObject(i);
                        SociaxItem weiboData = Api
                                .getSociaxItem(type, itemData);
                        list.add(weiboData);
                    } catch (Exception e) {
                        Log.e(TAG, "json itme  error wm :" + e.toString());
                    }
                    continue;
                }
            } catch (Exception e) {
                Log.e(TAG, "json result error wm :" + e.toString());
            }
            return list;
        }

        @Override
        public ListData<SociaxItem> showStatuses(int count, int type)
                throws ApiException {
            Get get = new Get();
            get.setUri(baseUrl(ApiGroup.SHOW_STATUSES));
            get.append("count", count);
            get.append("gid", 106);
            get.append("type", type);

            return getStatueList(Api.run(get), ListData.DataType.WEIBO);
        }

        @Override
        public ListData<SociaxItem> showStatusesHeader(ModelWeibo item,
                                                       int count, int type) throws ApiException {
            Get get = new Get();
            get.setUri(baseUrl(ApiGroup.SHOW_STATUSES));
            get.append("count", count);
            get.append("gid", 106);
            get.append("type", type);
            get.append("since_id", item.getWeiboId());

            return getStatueList(Api.run(get), ListData.DataType.WEIBO);
        }

        @Override
        public ListData<SociaxItem> showStatusesFooter(ModelWeibo item,
                                                       int count, int type) throws ApiException {
            Get get = new Get();
            get.setUri(baseUrl(ApiGroup.SHOW_STATUSES));
            get.append("count", count);
            get.append("gid", 106);
            get.append("type", type);
            get.append("max_id", item.getWeiboId());

            return getStatueList(Api.run(get), ListData.DataType.WEIBO);
        }

        @Override
        public ListData<SociaxItem> showAtmeStatuses(int count)
                throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.SHOW_ATME_STATUSES));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            return getStatueList(Api.run(Api.get), ListData.DataType.WEIBO);
        }

        @Override
        public ListData<SociaxItem> showAtmeStatusesHeader(ModelWeibo item,
                                                           int count) throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.SHOW_ATME_STATUSES));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("since_id", item.getWeiboId());

            return getStatueList(Api.run(Api.get), ListData.DataType.WEIBO);
        }

        @Override
        public ListData<SociaxItem> showAtmeStatusesFooter(ModelWeibo item,
                                                           int count) throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.SHOW_ATME_STATUSES));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("max_id", item.getWeiboId());
            return getStatueList(Api.run(Api.get), ListData.DataType.WEIBO);
        }

        @Override
        public ListData<SociaxItem> showStatusComments(int count)
                throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.SHOW_STATUS_COMMENTS));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("type", "receive");
            return getStatueList(Api.run(Api.get), ListData.DataType.RECEIVE);
        }

        @Override
        public ListData<SociaxItem> showStatusCommentsHeader(
                ReceiveComment item, int count) throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.SHOW_STATUS_COMMENTS));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("type", "receive");
            Api.get.append("since_id", item.getComment_id());

            return getStatueList(Api.run(Api.get), ListData.DataType.RECEIVE);
        }

        @Override
        public ListData<SociaxItem> showStatusCommentsFooter(
                ReceiveComment item, int count) throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.SHOW_STATUS_COMMENTS));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("type", "receive");
            Api.get.append("max_id", item.getComment_id());

            return getStatueList(Api.run(Api.get), ListData.DataType.RECEIVE);
        }

        @Override
        public ListData<SociaxItem> groupMembers(int count) throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.GROUP_MEMBERS));
            Api.get.append("count", count);
            Api.get.append("gid", 106);

            return getStatueList(Api.run(Api.get), ListData.DataType.USER);
        }

        @Override
        public ListData<SociaxItem> groupMembersHeader(ModelUser user, int count)
                throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.GROUP_MEMBERS));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("since_id", user.getUid());

            return getStatueList(Api.run(Api.get), ListData.DataType.USER);
        }

        @Override
        public ListData<SociaxItem> groupMembersFooter(ModelUser user, int count)
                throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.GROUP_MEMBERS));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("max_id", user.getUid());

            return getStatueList(Api.run(Api.get), ListData.DataType.USER);
        }

        @Override
        public ListData<SociaxItem> weiboComments(ModelWeibo item,
                                                  Comment comment, int count) throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.WEIBO_COMMENTS));
            get.append("count", count);
            get.append("gid", 106);
            get.append("id", item.getWeiboId());
            return getStatueList(Api.run(get), ListData.DataType.COMMENT);
        }

        @Override
        public ListData<SociaxItem> weiboCommentsHeader(ModelWeibo item,
                                                        Comment comment, int count) throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.WEIBO_COMMENTS));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("id", item.getWeiboId());
            Api.get.append("since_id", comment.getComment_id());

            return getStatueList(Api.run(Api.get), ListData.DataType.COMMENT);
        }

        @Override
        public ListData<SociaxItem> weiboCommentsFooter(ModelWeibo item,
                                                        Comment comment, int count) throws ApiException {
            Api.get.setUri(baseUrl(ApiGroup.WEIBO_COMMENTS));
            Api.get.append("count", count);
            Api.get.append("gid", 106);
            Api.get.append("id", item.getWeiboId());
            Api.get.append("max_id", comment.getComment_id());

            return getStatueList(Api.run(Api.get), ListData.DataType.COMMENT);
        }

        @Override
        public boolean updateStatus(ModelWeibo weibo) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiGroup.MOD_NAME,
                    ApiGroup.UPDATE_STATUS);

            Api.post.setUri(uri);
            Api.post.append("content", weibo.getContent());
            Api.post.append("gid", "106");
            Api.post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
            Object result = Api.run(Api.post);
            Api.checkResult(result);
            String data = (String) result;

            Log.d("apiData", "updateResult" + data.toString());
            return Integer.parseInt(data) > 0;
        }

        @Override
        public boolean uploadStatus(ModelWeibo weibo, File file)
                throws ApiException {
            String result = null;
            try {
                Uri.Builder uri = Api.createUrlBuild(ApiGroup.MOD_NAME,
                        ApiGroup.UPLOAD_STATUS);

                FormFile formFile = new FormFile(Compress.compressPic(file),
                        file.getName(), "pic", "application/octet-stream");
                Api.post.setUri(uri);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("content", weibo.getContent());
                param.put("token", Request.getToken());
                param.put("secretToken", Request.getSecretToken());
                param.put("gid", "106");
                param.put("from", ModelWeibo.From.ANDROID.ordinal() + "");
                result = FormPost.post(uri.toString(), param, formFile);
            } catch (Exception e) {
                Log.e(TAG, "group send pic weibo error wm" + e.toString());
            }
            if (result != null)
                return Integer.parseInt(result) > 0;
            else
                return false;
        }

        @Override
        public boolean repostStatuses(ModelWeibo weibo, boolean isComment)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiGroup.MOD_NAME,
                    ApiGroup.REPOST_STATUSES);

            Api.post.setUri(uri);
            if (weibo.getSourceWeibo().isNullForTranspond()) {
                Api.post.append("id", weibo.getSourceWeibo().getWeiboId() + "");
            } else {
                Api.post.append("id", weibo.getSourceWeibo().getIsRepost() + "");
            }
            Api.post.append("content", weibo.getContent());
            if (isComment) {
                Api.post.append("comment", 1);
            } else {
                Api.post.append("comment", 0);
            }
            Api.post.append("gid", "106");
            Api.post.append("from", ModelWeibo.From.ANDROID.ordinal() + "");
            Object result = Api.run(Api.post);
            Api.checkResult(result);

            return Integer.valueOf((String) result) > 0 ? true : false;
        }

        @Override
        public boolean commentStatuses(Comment comment) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiGroup.MOD_NAME,
                    ApiGroup.COMMENT_STATUSES);
            Api.post.setUri(uri);

            Api.post.append("content", comment.getContent())
                    .append("row_id", comment.getStatus().getWeiboId() + "")
                    .append("ifShareFeed", comment.getType().ordinal() + "")
                    .append("gid", "106")
                    .append("from", ModelWeibo.From.ANDROID.ordinal() + "");

            if (!comment.isNullForReplyComment()) {
                int replyCommentId = comment.getReplyComment().getComment_id();
                Api.post.append("to_comment_id", replyCommentId + "");
            }

            Object result = Api.run(Api.post);
            Api.checkResult(result);
            String data = (String) result;

            int resultConde = 0;

            try {
                resultConde = Integer.valueOf(data);
            } catch (Exception e) {
                Log.d(AppConstant.APP_TAG, "发送评论出错  wm " + e.toString());
                throw new ApiException("服务端出错");
            }
            return resultConde >= 1 ? true : false;
        }
    }

    public static final class CheckinApi implements ApiCheckin {

        private Uri.Builder baseUrl(String act) {
            return Api.createUrlBuild(ApiCheckin.MOD_NAME, act);
        }

        @Override
        public Object checkIn() throws ApiException {
            Api.get.setUri(baseUrl(ApiCheckin.CHECKIN));
            Object result = Api.run(Api.get);
            return result;
        }

        @Override
        public Object getCheckInfo() throws ApiException {
            Api.get.setUri(baseUrl(ApiCheckin.GET_CHECK_INFO));
            Object result = Api.run(Api.get);
            return result;
        }

        @Override
        public Object getCheckRankList() throws ApiException {
            Api.get.setUri(baseUrl(ApiCheckin.RANK));
            Object result = Api.run(Api.get);
            return result;
        }

        @Override
        public void setLocationInfo(double latitude, double longitude)
                throws ApiException {
            Api.get.setUri(baseUrl("checkinlocation"));
            Api.get.append("longitude", longitude);
            Api.get.append("latitude", latitude);
            Api.run(Api.get);
        }
    }

    public static final class UpgradeApi implements ApiUpgrade {

        private Uri.Builder beforeTimeline(String act) {
            return Api.createUrlBuild(ApiUpgrade.MOD_NAME, act);
        }

        @Override
        public VersionInfo getVersion() throws ApiException {
            Get get = new Get();
            get.setUri(beforeTimeline(ApiUpgrade.GET_VERSION));
            Object result = Api.run(get);
            VersionInfo vInfo = null;
            try {
                vInfo = new VersionInfo(new JSONObject((String) result));
            } catch (JSONException e) {
                e.printStackTrace();
                throw new ApiException("数据解析错误");
            }
            return vInfo;
        }
    }

    /**
     * 系统通知
     */
    public static final class NotifytionApi implements ApiNotifytion {

        @Override
        public ListData<SociaxItem> getNotifyByCount(int uid)
                throws ApiException {
            beforeTimeline(ApiNotifytion.GET_NOTIFY_BY_COUNT);
            Object result = Api.run(Api.get);
            Api.checkResult(result);

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        NotifyItem notifyItem = new NotifyItem(jsonObject);
                        /*
                         * if(notifyItem.getCount() < 1){ continue; }
						 */
                        list.add(notifyItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public int getMessageCount() throws ApiException {
            int messageCount = 0;
            beforeTimeline(ApiNotifytion.GET_MESSAGE_COUNT);
            Object result = Api.run(Api.get);

            if (Api.Status.SUCCESS == Api.checkResult(result)) {
                if (!result.equals("null") && !result.equals("ERROR")) {
                    try {
                        messageCount = new Integer((String) result);
                    } catch (Exception e) {
                        Log.d(TAG, "getMessage Count error " + e.toString());
                    }
                }
            }
            return messageCount;
        }

        private void beforeTimeline(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiNotifytion.MOD_NAME, act);
            Api.get.setUri(uri);
        }

        @Override
        public ListData<SociaxItem> getSystemNotify(int uid)
                throws ApiException {
            beforeTimeline(ApiNotifytion.GET_SYSTEM_NOTIFY);
            Object result = Api.run(Api.get);
            Api.checkResult(result);

            ListData<SociaxItem> list = null;
            try {
                if (!result.equals("null")) {
                    JSONArray data = new JSONArray((String) result);
                    int length = data.length();
                    list = new ListData<SociaxItem>();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
//                        SystemNotify systemNotify = new SystemNotify(jsonObject);
//
//                        list.add(systemNotify);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        public void setMessageRead(String type) throws ApiException {
            beforeTimeline(ApiNotifytion.SET_MESSAGE_READ);
            Api.get.append("type", type);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
        }

        @Override
        public void setNotifyRead(String type) throws ApiException {
            beforeTimeline(ApiNotifytion.SET_NOTIFY_READ);
            Api.get.append("type", type);
            Object result = Api.run(Api.get);
            Api.checkResult(result);
        }
    }

    // //////////////////////////************************//////////////////////

    public static String getHost() {
        return mHost;
    }

    public static String getPath() {
        return mPath;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        Api.mContext = context;
    }

    private static void setHost(String host) {
        Api.mHost = host;
    }

    private static void setPath(String path) {
        if (path.contains("php/")) {
            Api.mPath = path.substring(0, path.lastIndexOf("/"));
        } else
            Api.mPath = path;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Api.url = url;
    }

    /****************** t4 ******************/

    /**
     * 将JSONObject返回生成对应的对象
     *
     * @param type       ，用于区别ListData的类型
     * @param jsonObject
     * @return
     * @throws DataInvalidException
     * @throws ApiException
     */
    private static SociaxItem getSociaxItem(ListData.DataType type,
                                            JSONObject jsonObject) throws DataInvalidException, ApiException {
        if (type == ListData.DataType.COMMENT) {
            return new Comment(jsonObject);
        } else if (type == ListData.DataType.MODEL_COMMENT) {
            return new ModelComment(jsonObject);
        } else if (type == ListData.DataType.WEIBO
                || type == ListData.DataType.ALL_WEIBO
                || type == ListData.DataType.FRIENDS_WEIBO
                || type == ListData.DataType.ATME_WEIBO
                || type == ListData.DataType.RECOMMEND_WEIBO
                || type == ListData.DataType.CHANNELS_WEIBO) {
            //处理微博
            return new ModelWeibo(jsonObject);
        } else if (type == ListData.DataType.USER) {
            return new ModelUser(jsonObject);
        } else if (type == ListData.DataType.RECEIVE) {
            return new ReceiveComment(jsonObject);
        } else if (type == ListData.DataType.FOLLOW) {
            return new Follow(jsonObject, "");
        } else if (type == ListData.DataType.SEARCH_USER) {
            return null;//new SearchUser(jsonObject);
        } else if (type == ListData.DataType.MODEL_GIFT) {
            return new ModelGift(jsonObject);
        } else if (type == ListData.DataType.MODEL_TOPIC) {
            return new ModelTopic(jsonObject);
        } else if (type == ListData.DataType.MODEL_POST) {
            return new ModelPost(jsonObject);
        } else if (type.equals(ListData.DataType.MODEL_MEDAL)) {
            return new ModelUserMedal(jsonObject);
        } else if (type.equals(ListData.DataType.MODEL_FEEDBACK)) {
            return new ModelFeedBack(jsonObject);
        }
        throw new ApiException("参数错误");
    }

    /**
     * 请求结束之后，把返回类型修改成对应的SociaxItem
     *
     * @param type   SociaxItem类型
     * @param result 返回字符串
     * @return
     * @throws ApiException
     * @throws ListAreEmptyException
     * @throws VerifyErrorException
     * @throws DataInvalidException
     * @throws ExceptionIllegalParameter
     */
    private static ListData<?> afterRequest(ListData.DataType type,
                                            Object result) throws ApiException, ListAreEmptyException,
            VerifyErrorException, DataInvalidException,
            ExceptionIllegalParameter {

        if (type == ListData.DataType.MODEL_COMMENT) {
            //处理评论列表
            if (result.equals("null"))
                throw new CommentListAreEmptyException();
        } else if (type == ListData.DataType.COMMENT
                || type == ListData.DataType.RECEIVE) {
            if (result.equals("null"))
                throw new CommentListAreEmptyException();
        } else if (type == ListData.DataType.WEIBO
                || type == ListData.DataType.MODEL_CHANNEL
                || type == ListData.DataType.FRIENDS_WEIBO) {
            //处理微博和频道列表
            if (result.equals("null"))
                throw new WeiBoListAreEmptyException();
        } else if (type == ListData.DataType.USER
                || type == ListData.DataType.FOLLOW
                || type == ListData.DataType.SEARCH_USER) {
            //处理用户列表
            if (result.equals("null"))
                throw new UserListAreEmptyException();
        } else if (type == ListData.DataType.MODEL_GIFT
                || type == ListData.DataType.MODEL_SHOP_GIFT
                || type == ListData.DataType.MODEL_TOPIC
                || type.equals(ListData.DataType.MODEL_POST)
                || type.equals(ListData.DataType.MODEL_MEDAL)
                || type.equals(ListData.DataType.MODEL_FEEDBACK)) {
            if (result.equals("null"))
                throw new ListAreEmptyException();
        }

        try {
            JSONArray data = new JSONArray(result.toString());
            int length = data.length();
            ListData<SociaxItem> list = new ListData<SociaxItem>();
            for (int i = 0; i < length; i++) {
                JSONObject itemData = data.getJSONObject(i);
                try {
                    SociaxItem weiboData = getSociaxItem(type, itemData);
                    list.add(weiboData);
                    if (weiboData instanceof ModelWeibo) {
                        WeiboSqlHelper.addWeibo((ModelWeibo) weiboData);
                    }
//                    DbHelperManager.getInstance(Api.mContext, type).add(weiboData);
                } catch (DataInvalidException e) {
                    e.printStackTrace();
                }
            }
            return list;
        } catch (JSONException e) {
            // 检查返回值，如果是一个JSONObject,则进行一次验证看看是否是验证失败得提示信息
            try {
                JSONObject data = new JSONObject((String) result);
                Api.checkHasVerifyError(data);
                throw new CommentListAreEmptyException();
            } catch (JSONException e1) {
                Log.e(AppConstant.APP_TAG, "comment json 解析 错误  wm " + e.toString());
                throw new ApiException(result.toString());
            }
        }
    }

    /**
     * 微博类接口
     *
     * @author wz
     */
    public static final class WeiboApi implements ApiWeibo {

        /***
         * 调用mod=Weibo，act之前
         *
         * @param act
         */
        private void beforeRequest(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiWeibo.MOD_NAME, act);
            Log.e("uri", "uri+" + uri.toString());
            Api.post = new Post();
            Api.post.setUri(uri);
        }

        @Override
        public ModelWeibo show(int id) throws ApiException,
                WeiboDataInvalidException, VerifyErrorException {
            beforeRequest(ApiWeibo.SHOW);
            post.append("id", id);
            Object result = Api.run(post);
            Api.checkResult(result);
            try {
                JSONObject data = new JSONObject((String) result);
                Api.checkHasVerifyError(data);
                return new ModelWeibo(new JSONObject((String) result));
            } catch (JSONException e) {
                e.printStackTrace();
                throw new WeiboDataInvalidException("请求微博不存在");
            }
        }

        /**
         * 全部微博
         */
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> publicTimeline(int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.PUBLIC_TIMELINE}, params,

                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                            Object result = new String(arg2);
                            if (listener != null) {
                                try {
                                    listener.onSuccess((ListData<SociaxItem>) afterRequest(ListData.DataType.ALL_WEIBO,
                                            result));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onError(e.toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                            listener.onError(arg3.getMessage());
                        }
                    });
            return null;
        }

        //新接口
        public void publicTimeline(int count, int max_id, AsyncHttpResponseHandler handler) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.PUBLIC_TIMELINE},
                    params, handler);
        }

        @Override
        public ListData<SociaxItem> channelsTimeline(int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.CHANNEL_TIMELINE}, params,

                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                            Object result = new String(arg2);
                            if (listener != null) {
                                try {
                                    listener.onSuccess((ListData<SociaxItem>) afterRequest(ListData.DataType.CHANNELS_WEIBO,
                                            result));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onError(e.toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                            listener.onError(arg3.getMessage());
                        }
                    });
            return null;
        }

        /**
         * 加载频道列表
         *
         * @param count
         * @param max_id
         * @param listener
         * @return
         */
        public void channelsTimeline(int count, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.CHANNEL_TIMELINE}, params,
                    listener);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> recommendTimeline(int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
//			beforeRequest(ApiWeibo.RECOMMEND_TIMELINE);
//			if (count == 0) {
//				throw new ExceptionIllegalParameter();
//			}
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.RECOMMEND_TIMELINE}, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                            Object result = new String(arg2);
                            if (listener != null) {
                                try {
                                    listener.onSuccess((ListData<SociaxItem>) afterRequest(ListData.DataType.RECOMMEND_WEIBO,
                                            result));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onError(e.toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                            if (listener != null)
                                listener.onError(arg3.toString());
                        }
                    });
            return null;
        }

        //获取推荐微博列表
        public void recommendTimeline(int count, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.RECOMMEND_TIMELINE}, params,
                    listener);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> friendsTimeline(int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            beforeRequest(ApiWeibo.FRIENDS_TIMELINE);
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.FRIENDS_TIMELINE}, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                            Object result = new String(arg2);
                            if (listener != null) {
                                try {
                                    if (result.toString().startsWith("[")) {
                                        listener.onSuccess((ListData<SociaxItem>) afterRequest(ListData.DataType.FRIENDS_WEIBO,
                                                result));
                                    } else if (result.toString().startsWith("{")) {
                                        //数据返回类型是jsonobject
                                        JSONObject json = new JSONObject(result.toString());
                                        if (json.has("status")) {
                                            listener.onError(json.getString("msg"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                            if (listener != null)
                                listener.onError(arg3.toString());
                        }
                    });
            return null;
        }

        public void friendsTimeline(int count, int max_id, final AsyncHttpResponseHandler listener) {
            beforeRequest(ApiWeibo.FRIENDS_TIMELINE);
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.FRIENDS_TIMELINE}, params,
                    listener);
        }

        //获取微博评论
        public ListData<SociaxItem> getWeioComments(int weibo_id, int max_id, int count, final HttpResponseListener listener) {
            RequestParams params = new RequestParams();
            params.put("feed_id", weibo_id);
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_COMMENTS}, params,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Object result = new String(responseBody);
                            if (listener != null) {
                                try {
                                    listener.onSuccess((ListData<SociaxItem>) afterRequest(
                                            ListData.DataType.MODEL_COMMENT, result));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onError(e.toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if (listener != null)
                                listener.onError(error.toString());
                        }
                    });

            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> diggMeWeibo(int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_DIGG_ME}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        ListData<SociaxItem> list = new ListData<SociaxItem>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject itemData = response.getJSONObject(i);
                                SociaxItem weiboData = getSociaxItem(ListData.DataType.MODEL_COMMENT, itemData);
                                list.add(weiboData);
                                DbHelperManager.getInstance(Api.mContext, ListData.DataType.MODEL_COMMENT).add(weiboData);
                            } catch (DataInvalidException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onSuccess(list);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });
            return null;
        }

        public void diggMeWeibo(int count, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_DIGG_ME}, params, listener);
        }


        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> atMeWeibo(int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            beforeRequest(ApiWeibo.WEIBO_AT_ME);
            if (count == 0) {
                throw new ExceptionIllegalParameter();
            }
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_AT_ME}, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                            Object result = new String(arg2);
                            if (listener != null) {
                                try {
                                    listener.onSuccess((ListData<SociaxItem>) afterRequest(ListData.DataType.ATME_WEIBO,
                                            result));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onError(e.toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                            if (listener != null)
                                listener.onError(arg3.toString());
                        }
                    });
            return null;
        }

        public void atMeWeibo(int count, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_AT_ME}, params, listener);
        }


        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> commentMeWeibo(int count, int max_id, String type,
                                                   final HttpResponseListener listener) throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            if (type != null) {
                params.put("type", type);
            }
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_COMMENTT_ME},
                    params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                              JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (listener != null) {
                                listener.onError(errorResponse);
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            if (listener != null) {
                                ListData<SociaxItem> list = new ListData<SociaxItem>();
                                int length = response.length();
                                for (int i = 0; i < length; i++) {
                                    try {
                                        JSONObject itemData = response.getJSONObject(i);
                                        SociaxItem weiboData = getSociaxItem(ListData.DataType.MODEL_COMMENT,
                                                itemData);
                                        list.add(weiboData);
                                        DbHelperManager.getInstance(Api.mContext, ListData.DataType.MODEL_COMMENT).add(weiboData);
                                    } catch (DataInvalidException e) {
                                        Log.e(TAG, "has one invalid item with string");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (ApiException e) {
                                        e.printStackTrace();
                                    }
                                }

                                listener.onSuccess(list);
                            }

                            super.onSuccess(statusCode, headers, response);
                        }

                    });
            return null;
        }

        public void commentMeWeibo(int count, int max_id, String type, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            if (type != null) {
                params.put("type", type);
            }
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_COMMENTT_ME},
                    params, listener);
        }

        public void myWeibo(int uid, int count, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_MY}, params,
                    listener);
        }

        public ListData<SociaxItem> myWeibo(int uid, int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("user_id", uid);
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.get(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_MY}, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                            Object result = new String(arg2);
                            if (listener != null) {
                                try {
                                    listener.onSuccess((ListData<SociaxItem>) afterRequest(ListData.DataType.WEIBO,
                                            result));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onError(e.toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                            if (listener != null) {
                                listener.onError("网络连接失败...");
                            }
                        }
                    });
//			Object result = Api.run(post);
//			try {
//				return (ListData<SociaxItem>) afterRequest(DataType.WEIBO,
//						result);
//			} catch (ExceptionIllegalParameter e) {
//				e.printStackTrace();
//			}
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> collectWeibo(int uid, int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_COLLECT}, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    if (listener != null)
                        listener.onError(throwable.toString());
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (listener != null) {
                        ListData<SociaxItem> list = new ListData<SociaxItem>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject itemData = response.getJSONObject(i);
                                try {
                                    SociaxItem weiboData;
                                    try {
                                        weiboData = getSociaxItem(ListData.DataType.WEIBO, itemData);
                                        list.add(weiboData);
                                        DbHelperManager.getInstance(Api.mContext, ListData.DataType.WEIBO).add(weiboData);
                                    } catch (ApiException e) {
                                        e.printStackTrace();
                                    }
                                } catch (DataInvalidException e) {
                                    Log.e(TAG, "json error wm :" + e.toString());
                                }
                            }
                            listener.onSuccess(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError("数据解析错误");
                        }
                    }
                    super.onSuccess(statusCode, headers, response);
                }

            });
            return null;
        }

        public void collectWeibo(int uid, int count, int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.WEIBO_COLLECT}, params, listener);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getAllTopic(int count, final int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.ALL_TOPIC}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                              JSONObject errorResponse) {
                            if (listener != null) {
                                listener.onError(errorResponse);
                            }
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if (listener != null) {
                                ListData<SociaxItem> returnlist = new ListData<SociaxItem>();
                                JSONArray commends = null, lists = null;// 推荐话题、普通话题
                                if (response.has("commends")) {
                                    try {
                                        commends = response.getJSONArray("commends");
                                        for (int i = 0; i < commends.length(); i++) {
                                            ModelTopic mdi = new ModelTopic(
                                                    commends.getJSONObject(i));
                                            if (i == 0 && max_id == 0) {// 只有第一页才显示标题
                                                mdi.setFirst(true);
                                            }
                                            returnlist.add(mdi);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (response.has("lists")) {
                                    try {
                                        lists = response.getJSONArray("lists");
                                        for (int i = 0; i < lists.length(); i++) {
                                            ModelTopic mdi = new ModelTopic(lists.getJSONObject(i));
                                            if (i == 0 && max_id == 0) {
                                                mdi.setFirst(true);
                                            }
                                            returnlist.add(mdi);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                listener.onSuccess(returnlist);
                            }
                            super.onSuccess(statusCode, headers, response);
                        }
                    });
            return null;
        }

        public void getAllTopic(int count, final int max_id, final AsyncHttpResponseHandler listener) {
            RequestParams params = new RequestParams();
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.ALL_TOPIC}, params, listener);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object getTopicWeibo(String topic_name, int count, int max_id, final HttpResponseListener listener)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {

            RequestParams params = new RequestParams();
            params.put("topic_name", topic_name);
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.TOPIC_WEIBO}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null)
                        listener.onSuccess(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });
            return null;
        }

        public void getTopicWeibo(String topic_name, int count, int max_id, final AsyncHttpResponseHandler listener) {

            RequestParams params = new RequestParams();
            params.put("topic_name", topic_name);
            params.put("count", count);
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiWeibo.MOD_NAME, ApiWeibo.TOPIC_WEIBO}, params, listener);
        }

        /**
         * 删除微博评论
         */
        @Override
        public Object deleteWeiboComment(int commentId) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(MOD_NAME, DELETE_COMMENT);
            Api.get = new Get();
            Api.get.setUri(uri);
            Api.get.append("commentid", commentId);

            return Api.run(Api.get);
        }

    }

    /**
     * 礼物相关接口
     *
     * @author wz
     */
    public static final class GiftApi implements ApiGift {
        @SuppressWarnings("unused")
        private String encryptKey;

        /***
         * 调用mod=Weibo，act之前
         *
         * @param act
         */
        private void beforeRequest(String act) {
            Uri.Builder uri = Api.createUrlBuild(ApiGift.MOD_NAME, act);
            Log.e("uri", "uri+" + uri.toString());
            Api.post = new Post();
            Api.post.setUri(uri);
        }

        @SuppressWarnings("unchecked")
        @Override
        /**
         * 获取所有礼物
         * max_id 最后一个礼物的id
         * @return
         */
        public ListData<SociaxItem> getAllGift(int max_id) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException, ExceptionIllegalParameter {
            // TODO Auto-generated method stub
            beforeRequest(ApiGift.GIFT_ALL);
            post.append("max_id", max_id);
            Object result = Api.run(post);
            try {
                return (ListData<SociaxItem>) afterRequest(ListData.DataType.MODEL_GIFT,
                        result);
            } catch (ExceptionIllegalParameter e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 我的礼物
         *
         * @return
         */
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getMyGift(int max_id, final HttpResponseListener listener) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException, ExceptionIllegalParameter {

            RequestParams params = new RequestParams();
            params.put("max_id", max_id);
            ApiHttpClient.post(new String[]{ApiGift.MOD_NAME, ApiGift.GIFT_MY}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        int length = response.length();
                        ListData<SociaxItem> list = new ListData<SociaxItem>();
                        for (int i = 0; i < length; i++) {
                            try {
                                JSONObject itemData = response.getJSONObject(i);
                                ModelGift gift = new ModelGift(itemData);
                                list.add(gift);
                                DbHelperManager.getInstance(Api.mContext, ListData.DataType.MODEL_GIFT).add(gift);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onSuccess(list);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null)
                        listener.onError(throwable.toString());
                }
            });

            return null;
        }

        /**
         * 对换礼物
         *
         * @param giftid
         * @return
         */
        public Object buyGift(String giftid) {
            // TODO Auto-generated method stub
            beforeRequest(ApiGift.BUY_GIFT);
            post.append("gift_id", giftid);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "[]";
        }

        /**
         * 我的礼物
         *
         * @return
         */
        @SuppressWarnings("unchecked")
        @Override
        public ListData<SociaxItem> getUerGift(int uid, int max_id)
                throws ApiException, ListAreEmptyException,
                DataInvalidException, VerifyErrorException,
                ExceptionIllegalParameter {
            // TODO Auto-generated method stub
            Uri.Builder uri = Api.createUrlBuild("User", "user_gift");
            Log.e("uri", "uri+" + uri.toString());
            post = new Post();
            post.setUri(uri);
            post.append("max_id", max_id);
            post.append("user_id", uid);

            Object result = Api.run(post);
            try {
                return (ListData<SociaxItem>) afterRequest(ListData.DataType.MODEL_GIFT,
                        result);
            } catch (ExceptionIllegalParameter e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 对换礼物
         *
         * @param
         * @return
         */
        @Override
        public Object sentGift(String gift_id, String uids, String sendinfo,
                               String sendWay) {
            // TODO Auto-generated method stub
            beforeRequest(ApiGift.SEND_GIFT);
            post.append("uids", uids);
            post.append("gift_id", gift_id);
            if (sendinfo != null)
                post.append("sendinfo", sendinfo);
            if (sendWay != null)
                post.append("sendWay", sendWay);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "[]";
        }

        /**
         * 转赠礼物
         *
         * @return
         */
        public Object resentGift(String gift_id, String id, String uids,
                                 String sendinfo, String sendWay) {
            // TODO Auto-generated method stub
            beforeRequest(ApiGift.RESEND_GIFT);
            post.append("uid", uids);
            post.append("id", id);
            post.append("giftId", gift_id);
            post.append("giftNum", "1");
            if (sendinfo != null)
                post.append("sendinfo", sendinfo);
            if (sendWay != null)
                post.append("sendWay", sendWay);
            try {
                Object result = Api.run(Api.post);
                return result.toString();
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "[]";
        }
        /*******************t4*********************/
        /**
         * 获取全部礼物列表
         *
         * @param page 页码
         * @param cate 分类，值只有1和2，1代表虚拟，2代表实体礼物，不传代表全部
         * @param num  每页返回的数据条数 默认20条
         * @return
         */
        @Override
        public void getShopGift(int page, int num, final HttpResponseListener listener) {
            getShopGift(page, num, null, listener);
        }

        @Override
        public void getShopGift(int page, int num, final String cate, final HttpResponseListener listener) {
            RequestParams params = new RequestParams();
            params.put("page", page);
            params.put("num", num);
            if (cate != null && !cate.equals("")) {
                params.put("cate", cate);
            }
            ApiHttpClient.post(new String[]{ApiGift.MOD_NAME, ApiGift.GET_SHOP_GIFT}, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable
                        throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(errorResponse);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.v("giftJsonRefresh", "--------json------------" + response.toString());
                    JSONArray jsonArray = null;
                    ListData<SociaxItem> listData = new ListData<SociaxItem>();
                    try {
                        jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ModelShopGift gift = new ModelShopGift(jsonArray.getJSONObject(i));
                            if (gift.getId() != null)
                                listData.add(gift);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listener.onSuccess(listData);
                }
            });
        }

        /**
         * 获取资源文件下的json文件数据
         */

        public String getJsonFromRaw() {
            //将json文件读取到buffer数组中
            InputStream is = mContext.getResources().openRawResource(R.raw.shopgiftjson);
            String json = null;
            try {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                //将字节数组转换为以utf-8编码的字符串
                json = new String(buffer, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        /**
         * 礼物详情
         *
         * @param id 礼物id
         * @return
         */
        @Override
        public String getGiftDetail(String id) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiGift.MOD_NAME, ApiGift.GET_GIFT_DETAIL);
            Api.post = new Post();
            post.setUri(uri);
            post.append("id", id);
            Object result = Api.run(post);

            Log.v("giftJson", "-------giftJson---getGiftDetail-------" + result.toString());

            return result.toString();
        }

        /**
         * 兑换礼物
         *
         * @param id     礼物id
         * @param uid    赠送的人的UID
         * @param num    兑换的数量
         * @param addres 邮寄地址
         * @param say    祝福语
         * @param type   类型
         * @return
         */
        @Override
        public String exchangeGift(String id, int uid, int num, String addres,
                                   String say, String type, String phone, String name) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiGift.MOD_NAME, ApiGift.EXCHANGE_GIFT);
            Api.post = new Post();
            post.setUri(uri);
            post.append("id", id);
            post.append("uid", uid);
            post.append("num", num);
            post.append("addres", addres);
            post.append("say", say);
            post.append("type", type);
            post.append("phone", phone);
            post.append("name", name);
            Object result = Api.run(post);

            return result.toString();
        }

        /**
         * 获取我的礼物列表
         *
         * @param type 请求类型 0：获得的礼物 1：赠送的礼物
         * @return
         */
        @Override
        public ListData<SociaxItem> getMyGifts(int page, String type, final HttpResponseListener listener) throws ApiException,
                ListAreEmptyException, DataInvalidException,
                VerifyErrorException {
            RequestParams params = new RequestParams();
            params.put("type", type);
            params.put("page", page);
            ApiHttpClient.post(new String[]{ApiGift.MOD_NAME, ApiGift.GET_MY_GIFTS}, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            ListData<SociaxItem> listData = new ListData<SociaxItem>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    ModelMyGifts gift = new ModelMyGifts(jsonArray.getJSONObject(i));
                                    if (gift.getId() != null)
                                        listData.add(gift);
                                } catch (DataInvalidException e) {
                                    e.printStackTrace();
                                }
                            }
                            listener.onSuccess(listData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError("数据解析错误");
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }
            });

            return null;
        }

        /**
         * 礼物转赠
         */
        @Override
        public String transferMyGift(String logId, int uid, String say, int num, String type)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiGift.MOD_NAME, ApiGift.TRANSFER_MY_GIFT);
            Api.post = new Post();
            post.setUri(uri);
            post.append("id", logId);
            post.append("uid", uid);
            post.append("say", say);
            post.append("num", num);
            post.append("type", type);
            Object result = Api.run(post);

            Log.v("transfermyGift", "----------transfermyGift---json------------" + result.toString());

            return result.toString();
        }
    }

    /**
     * 积分类
     *
     * @author Zoey
     */
    public static final class Credit implements ApiCredit {

        /**
         * 获取积分详情
         */
        @Override
        public ListData<SociaxItem> getScoreDetail(int max_id, int limit, final HttpResponseListener listener) throws ApiException {
            RequestParams params = new RequestParams();
            params.put("max_id", max_id);
            params.put("limit", limit);
            ApiHttpClient.post(new String[]{ApiCredit.MOD_NAME, ApiCredit.SCORE_DETAIL}, params,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (listener != null)
                                listener.onError(throwable.toString());
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                            super.onSuccess(statusCode, headers, jsonArray);
                            if (listener != null) {
                                ListData<SociaxItem> listData = new ListData<SociaxItem>();
                                try {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ModelMyScoreDetail detail = new ModelMyScoreDetail(jsonArray.getJSONObject(i));
                                        listData.add(detail);
                                    }
                                    listener.onSuccess(listData);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    });
            return null;
        }

        /**
         * 积分转账
         */
        @Override
        public String transferMyScore(int to_uid, int num,
                                      String desc) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiCredit.MOD_NAME,
                    ApiCredit.SCORE_TRANSFER);
            Post post = new Post();
            post.setUri(uri);
            post.append("to_uid", to_uid + "");
            post.append("num", num + "");
            post.append("desc", desc);
            Object result = Api.run(post);

            return result.toString();
        }

        /**
         * 获取积分规则
         */
        @Override
        public ListData<SociaxItem> getScoreRule(final HttpResponseListener listener) throws ApiException {
            ApiHttpClient.post(new String[]{ApiCredit.MOD_NAME, ApiCredit.SCORE_RULE}, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (listener != null) {
                        listener.onError(throwable.toString());
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        ListData<SociaxItem> listData = new ListData<SociaxItem>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                ModelScoreRule detail = new ModelScoreRule(response.getJSONObject(i));
                                listData.add(detail);
                            }
                            listener.onSuccess(listData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
            return null;
        }

        /**
         * 创建订单
         */
        @Override
        public String createCharge(double money, int type) throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiCredit.MOD_NAME,
                    ApiCredit.CREATE_CHARGE);
            Post post = new Post();
            post.setUri(uri);
            post.append("money", money + "");
            post.append("type", type + "");
            Object result = Api.run(post);

            Log.v("charge", "/money/" + money + "/type/" + type);
            Log.v("charge", "---------createCharge json---------------" + result.toString());

            return result.toString();
        }

        /**
         * 设置充值状态
         */
        @Override
        public String saveCharge(String serial_number, int status, String sign)
                throws ApiException {
            Uri.Builder uri = Api.createUrlBuild(ApiCredit.MOD_NAME,
                    ApiCredit.SAVE_CHARGE);
            Post post = new Post();
            post.setUri(uri);
            post.append("serial_number", serial_number);
            post.append("status", 1 + "");
            post.append("sign", sign);
            Object result = Api.run(post);

            Log.v("charge", "---------saveCharge json-------serial_number--------" + serial_number + "-status--" + 1 + "-sign--" + sign);
            Log.v("charge", "---------saveCharge json---------------" + result.toString());

            return result.toString();
        }
    }

    /**
     * 获取聊天服务器地址
     *
     * @return
     */
    public static URI getSocketServer() {
        return socketServer;
    }

    /**
     * 设置聊天服务器地址
     *
     * @param server
     */
    public static void setSocketServer(String server) {
        try {
            socketServer = new URI(server);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
