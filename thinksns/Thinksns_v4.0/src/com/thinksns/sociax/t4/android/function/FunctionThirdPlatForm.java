package com.thinksns.sociax.t4.android.function;

import java.util.HashMap;

import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.login.ActivityBindThirdLoginUser;
import com.thinksns.sociax.t4.model.ModelImageAttach;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelVideo;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.unit.PrefUtils;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 第三方登录/注册/绑定/分享相关
 *
 * @author wz
 * @version 1.0
 * @date 2014-12-5
 */
public class FunctionThirdPlatForm extends FunctionSoicax {
    private static final int MSG_ERR = 1;
    private static final int MSG_SUCCESS = 2;
    private static final int MSG_CANCEL = 3;
    private static final int MSG_REG = 4;
    private Platform pf;                // 操作平台
    /**
     * 获取当前操作的平台
     *
     * @return
     */
    public Platform getPlatform() {
        return pf;
    }

    /**
     * 需要注意的是必须先initShareSDK
     *
     * @param context
     * @param platform 平台 ，使用ShareSDK.getPlaform获取
     */
    public FunctionThirdPlatForm(Context context, Platform platform) {
        super(context);
        pf = platform;
        pf.SSOSetting(false);// 设置成true直接使用网页授权，否则先考虑使用目标客户端授权，没有客户端情况下使用网页授权
    }

    @Override
    protected void initUiHandler() {
        handlerUI = new UIHandler();
    }

    @Override
    protected void initActivtyHandler() {
        handlerActivity = new ActivityHandler(thread.getLooper(), context);
    }

    /**
     * 执行登录
     * 新浪登陆失败请检查回调页面，回调页面仅使用网站地址就可以
     * 微信登陆失败请检查md5签名是否当前md5签名
     */
    public void doLogin() {
        // 设置授权的监听事件
        if (pf.isValid()) {
            // 取消上一个用户的授权，否则会直接跳过授权，使用上一个用户进入主页面
            pf.removeAccount();
        }

        ShareSDK.removeCookieOnAuthorize(true);
        pf.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_LOGIN;
                msg.obj = arg2.getMessage();
                msg.arg1 = MSG_ERR;
                handlerActivity.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1,HashMap<String, Object> arg2) {

                String[] platInfo = new String[3];
                platInfo[0] = getTypeName(arg0);
                if (platInfo[0] == null)
                    return;
                platInfo[1] = arg0.getDb().getUserId();
                platInfo[2] = arg0.getDb().getToken();

                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_LOGIN;
                FunctionThirdPlatForm.this.pf = arg0;
                msg.arg1 = MSG_SUCCESS;
                handlerActivity.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_LOGIN;
                msg.obj = arg0;
                msg.arg1 = MSG_CANCEL;
                handlerActivity.sendMessage(msg);

                Log.v("thirdLogin","onCancel/");
            }
        });
        // 执行授权动作
        pf.showUser(null);
    }

    /**
     * 获取平台对应到T4服务器的名称
     *
     * @param pf2
     * @return
     */
    private static String getTypeName(Platform pf2) {
        String typename = "";// 对应到T4服务器接口的名称
        if (pf2.getName().equals("QZone")) {
            typename = "qzone";
        } else if (pf2.getName().equals("SinaWeibo")) {
            typename = "sina";
        } else if (pf2.getName().equals(Wechat.NAME)) {
            typename = "weixin";
        }
        if (typename.equals("")) {
            return null;
        } else {
            return typename;
        }
    }

    /**
     * 取消授权
     */
    public void doRemoveOauth() {
        if (pf.isValid()) {
            // 如果已经完成授权
            pf.removeAccount();
        }
    }

    /**
     * 绑定授权
     */
    public void doBindOauth() {
        // 设置授权的监听事件
        if (pf.isValid()) {
            // 取消上一个用户的授权，否则会直接跳过授权，使用上一个用户进入主页面
            pf.removeAccount();
        }
        ShareSDK.removeCookieOnAuthorize(true);
        pf.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_BIND;
                msg.obj = arg2.getMessage();
                msg.arg1 = MSG_ERR;
                handlerUI.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1,HashMap<String, Object> arg2) {
                Platform platform=arg0;
                int index=arg1;
                HashMap<String,Object> map=arg2;

                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_BIND;
                msg.obj = arg0;
                msg.arg1 = MSG_SUCCESS;
                handlerUI.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_BIND;
                msg.obj = arg0;
                msg.arg1 = MSG_CANCEL;
                handlerUI.sendMessage(msg);
            }
        });
        // 执行授权动作
        pf.authorize();
    }

    public class UIHandler extends Handler {
        public UIHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Thinksns app = (Thinksns) context.getApplicationContext();
            if (msg.what == StaticInApp.DO_THIRD_LOGIN) {// 执行第三方登陆
                if (listener != null)
                    listener.onTaskCancle();
                switch (msg.arg1) {
                    case MSG_SUCCESS:// 登录成功
                        Intent intent = new Intent(context, ActivityHome.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        //关闭首页
                        ThinksnsActivity.getInstance().finish();
                        break;
                    case MSG_ERR:// 登录失败

                        break;
                    case MSG_REG:
                        // 登录成功但是需要注册
                        Intent intent1 = new Intent(context, ActivityBindThirdLoginUser.class);
                        intent1.putExtra("type", getTypeName(pf));
                        intent1.putExtra("type_uid", pf.getDb().getUserId());
                        intent1.putExtra("access_token", pf.getDb().getToken());
                        intent1.putExtra("icon", pf.getDb().getUserIcon());
                        intent1.putExtra("name", pf.getDb().getUserName());
                        intent1.putExtra("gender", pf.getDb().getUserGender());
                        context.startActivity(intent1);
                        break;
                }
            } else if (msg.what == StaticInApp.DO_THIRD_SHARE) {
                switch (msg.arg1) {
                    case MSG_SUCCESS:// 成功
                        Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_ERR:// 失败
                        Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_REG:// 取消
                        Toast.makeText(context, "取消发送", Toast.LENGTH_SHORT).show();
                        break;
                }

            } else if (msg.what == StaticInApp.DO_THIRD_BIND) {
                //绑定账号
                switch (msg.arg1) {
                    case MSG_SUCCESS:// 绑定成功
                        if (listener != null) {
                            listener.onTaskSuccess();
                        }
                        break;
                    case MSG_ERR:// 绑定失败
                        if (listener != null) {
                            listener.onTaskError();
                        }
                        break;
                    case MSG_REG:// 取消绑定
                        if (listener != null) {
                            listener.onTaskCancle();
                        }
                        break;
                }
            }
        }
    }

    /**
     * 用于授权的Handler回调
     */
    private class ActivityHandler extends Handler {
        private Context context = null;

        public ActivityHandler(Looper looper, Context context) {
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == StaticInApp.DO_THIRD_LOGIN) {
                // 执行第三方授权，处理结果
                switch (msg.arg1) {
                    case MSG_CANCEL:
                        //取消授权
                        listener.onTaskCancle();
                        break;
                    case MSG_SUCCESS:
                        listener.onTaskSuccess();
                        // 授权成功，取出授权用户信息，调用接口判断是否已经完成注册
                        try {
                            Message oauthMessage = new Message();
                            oauthMessage.what = StaticInApp.GET_THIRD_REG_INFO;
                            String[] platInfo = new String[3];
                            platInfo[0] = getTypeName(pf);
                            if (platInfo[0] == null)
                                return;
                            platInfo[1] = pf.getDb().getUserId();
                            platInfo[2] = pf.getDb().getToken();
                            Object result = app.getOauth().getThirdRegInfo(platInfo);
                            oauthMessage.obj = result;
                            this.sendMessage(oauthMessage);
                        } catch (Exception e) {
                            listener.onTaskCancle();
                            e.printStackTrace();
                        }
                        break;
                    case MSG_ERR:
                        if (msg.obj != null) {
                            Toast.makeText(context, msg.obj.toString(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "登陆失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            } else if (msg.what == StaticInApp.GET_THIRD_REG_INFO) {
                // 调用接口获取第三方注册信息
                Message regMessage = new Message();
                regMessage.what = StaticInApp.DO_THIRD_LOGIN;
                regMessage.arg1 = MSG_ERR;
                Object result = msg.obj;
                if (result == null) {
                    Toast.makeText(context, "登录失败，请检查网络", Toast.LENGTH_SHORT).show();
                    listener.onTaskCancle();
                    return;
                }
                try {
                    JSONObject regInfo = new JSONObject(result.toString());
                    if (regInfo.has("status")) {
                        if (regInfo.getString("status").equals("0")) {
                            // 账号未注册调到注册页面
                            regMessage.arg1 = MSG_REG;
                        }
                    } else if (regInfo.has("uid")) {
                        //已经绑定过第三方账号
                        String token = regInfo.getString("oauth_token");
                        String tokenSecret = regInfo.getString("oauth_token_secret");
                        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(tokenSecret)) {
                            ModelUser authorizeResult = new ModelUser(regInfo.getInt("uid"),
                                    "", "", token, tokenSecret);
                            ApiHttpClient.TOKEN_SECRET = tokenSecret;
                            ApiHttpClient.TOKEN = token;
                            //这里可以保存用户基础信息，跳转到首页之后再获取用户更详细资料
                            app.getUsers().show(authorizeResult, userListener);
                        } else {
                            Toast.makeText(context, "登录失败，请使用其他方式或联系开发人员",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "登录失败，请使用其他方式或联系开发人员",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "登录失败，请使用其他方式", Toast.LENGTH_SHORT)
                            .show();
                    Log.v("thirdLogin","StaticInApp.GET_THIRD_REG_INFO/"+e.getMessage());
                }

                handlerUI.sendMessage(regMessage);
            }

        }
    }

    HttpResponseListener userListener = new HttpResponseListener() {

        @Override
        public void onSuccess(Object result) {
            ListData<SociaxItem> list = (ListData<SociaxItem>) result;
            if (list != null && list.size() == 1) {
                PrefUtils.setPrefIsLocalRegister(Thinksns.getContext(), true); // 显示推荐向导
                ModelUser loginedUser = (ModelUser) list.get(0);
                Thinksns.setMy(loginedUser);
                UserSqlHelper db = UserSqlHelper.getInstance(context);
                db.addUser(loginedUser, true);
                Message regMessage = new Message();
                regMessage.what = StaticInApp.DO_THIRD_LOGIN;
                regMessage.arg1 = MSG_SUCCESS;
                handlerUI.sendMessage(regMessage);
            }
        }

        @Override
        public void onError(Object result) {
            Message regMessage = new Message();
            regMessage.what = StaticInApp.DO_THIRD_LOGIN;
            regMessage.arg1 = MSG_SUCCESS;
            handlerUI.sendMessage(regMessage);
        }
    };

    /**
     * 执行分享微博
     *
     * @param weibo 被分享的微博
     */
    public void doShareWeibo(ModelWeibo weibo) {

        String[] configHost = context.getResources().getStringArray(
                R.array.site_url);
        String str_shareVedioUrl = null;// 分享的视频地址
        String img_shareImgUrl = null;// 分享内容的图片地址
        String str_shareTitle = null;// 分享的标题，一般 “来自uname的分享”
        String str_shareContent = null;// 分享的内容
        String str_shareUrl = "http://" + configHost[0] + context.getResources().getString(R.string.w3g_address_of_shareSDK_weibo) + weibo.getWeiboId();

        // 标题
        str_shareTitle = "来自" + weibo.getUsername() + "的分享";
        // 内容
        str_shareContent = weibo.getContent() == null ? "来自" + context.getResources().getString(R.string.app_name) + "的分享" : weibo
                .getContent();
        // 展示的图片，如果有视频的话获取视频地址,以及预览的图片地址
        if (weibo.hasVideo()) {
            ModelVideo myVideo = weibo.getAttachVideo();
//			if (myVideo.getVideoPart() != null) { // 如果有预览视频则获取预览视频地址，否则获取详情
//				str_shareVedioUrl = myVideo.getVideoPart();
//			} else {
            if (myVideo.getVideoDetail() != null) {
                str_shareVedioUrl = myVideo.getVideoDetail();
            }
//			}
            img_shareImgUrl = myVideo.getVideoImgUrl(); // 预览图片
        } else if (weibo.hasImage()) {
            // 没有视频如果有图片的话使用图片地址
            img_shareImgUrl = ((ModelImageAttach) weibo.getAttachImage().get(0)).getMiddle();
        }
		else {
			// 既没有视频也没有图片的话使用ts的图标，可能需要进一步细化，考虑使用微博的转发微博的图片
			img_shareImgUrl = weibo.getUserface();
		}
        // 封装到ShareSdk进行分享
        ShareParams sp = new ShareParams();
        sp.setTitle(str_shareTitle);// 标题
        sp.setText(str_shareContent);// 内容

        if (pf.getName().equals(QQ.NAME) || pf.getName().equals(QZone.NAME)) {
            // QQ分享
            sp.setTitleUrl(str_shareUrl); // 标题的超链接,点击之后直接跳转到指定的页面
            if (img_shareImgUrl != null) {
                sp.setImageUrl(img_shareImgUrl.toString());
            }
            sp.setSite(context.getResources().getString(R.string.app_name));
            sp.setSiteUrl(configHost[0]);

        } else if (pf.getName().equals(Wechat.NAME)
                || pf.getName().equals(WechatMoments.NAME)) {
            // 分享到微信
            sp.setShareType(Wechat.SHARE_WEBPAGE);// 微信必须设置一个SHARE类型，否则返回err,这里使用3G网站
            if (img_shareImgUrl != null) {
                sp.setImageUrl(img_shareImgUrl.toString());
            }
            sp.setUrl(str_shareUrl);// 超链接，点击之后进入3G网站
//			if (str_shareVedioUrl != null) {// 分享视频
//				sp.setShareType(Wechat.SHARE_VIDEO);
//				sp.setUrl(str_shareVedioUrl);
//			} else {
//				// 在這里填充其他扩充类型，例如音乐等
//
//			}
        } else if (pf.getName().equals(SinaWeibo.NAME)) {
            // 分享到新浪微博
            if (img_shareImgUrl != null) {
                sp.setImageUrl(img_shareImgUrl.toString());
            }
        }

        // 设置分享事件回调
        pf.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.v("FunctionThirdPlatForm--shareWeibo  err", "/arg2/" + arg2.getMessage() + "/arg1/" + arg1);
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_ERR;
                handlerUI.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_SUCCESS;
                handlerUI.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_CANCEL;
                handlerUI.sendMessage(msg);
            }
        });
        // 执行图文分享
        pf.share(sp);
    }

    /**
     * 执行分享链接
     */
    public void doShareStr() {
        ModelUser user = Thinksns.getMy();

        if (user == null) {
            return;
        }
        String[] configHost = context.getResources().getStringArray(
                R.array.site_url);
        String str_shareTitle = null;// 分享的标题，一般 “来自uname的分享”
        String str_shareContent = null;// 分享的内容
        String str_shareUrl = "http://" + configHost[0] + context.getResources().getString(R.string.w3g_address_of_share_erweima) + user.getUid();
        String img_shareImgUrl = user.getUserface();// 分享内容的图片地址
        // 标题
        str_shareTitle = user.getUserName();
        // 内容
        str_shareContent = "点击查看个人主页";
        // 封装到ShareSdk进行分享
        ShareParams sp = new ShareParams();
        sp.setTitle(str_shareTitle);// 标题
        sp.setText(str_shareContent);// 内容

        if (pf.getName().equals(QQ.NAME) || pf.getName().equals(QZone.NAME)) {
            // QQ分享
            sp.setTitleUrl(str_shareUrl); // 标题的超链接,点击之后直接跳转到指定的页面
            sp.setSite(context.getResources().getString(R.string.app_name));
            sp.setSiteUrl(configHost[0]);
            if (img_shareImgUrl != null) {
                sp.setImageUrl(img_shareImgUrl.toString());
            }
        } else if (pf.getName().equals(Wechat.NAME)
                || pf.getName().equals(WechatMoments.NAME)) {
            // 分享到微信
            if (img_shareImgUrl != null) {
                sp.setImageUrl(img_shareImgUrl.toString());
            }
            sp.setShareType(Wechat.SHARE_WEBPAGE);// 微信必须设置一个SHARE类型，否则返回err,这里使用3G网站
            sp.setUrl(str_shareUrl);// 超链接，点击之后进入3G网站
        } else if (pf.getName().equals(SinaWeibo.NAME)) {
            if (img_shareImgUrl != null) {
                sp.setImageUrl(img_shareImgUrl.toString());
            }
            sp.setUrl(str_shareUrl);// 超链接，点击之后进入3G网站
        }

        // 设置分享事件回调
        pf.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_ERR;
                handlerUI.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_SUCCESS;
                handlerUI.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_CANCEL;
                handlerUI.sendMessage(msg);
            }
        });
        pf.share(sp);
    }

    /**
     * 执行分享帖子
     *
     * @param weibo 被分享的帖子
     */
    public void doSharePost(ModelPost weibo) {
        String[] configHost = context.getResources().getStringArray(
                R.array.site_url);

        String str_shareVedioUrl = null;// 分享的视频地址
        String img_shareImgUrl = null;// 分享内容的图片地址
        String str_shareTitle = null;// 分享的标题，一般 “来自uname的分享”
        String str_shareContent = null;// 分享的内容
        String str_shareUrl = "http://" + configHost[0] + context.getResources().getString(R.string.w3g_address_of_shareSDK_post)
                + weibo.getPost_id();

        // 标题
        str_shareTitle = "来自" + weibo.getUser().getUserName() + "的分享";
        // 内容
        str_shareContent = weibo.getContent() == null ? "来自" + context.getResources().getString(R.string.app_name) + "的分享" : weibo
                .getContent();
        // 展示的图片，如果有视频的话获取视频地址,以及预览的图片地址
//		if (weibo.hasVideo()) {
//			ModelVideo myVideo = weibo.getVideo();
//			if (myVideo.getVideoPart() != null) { // 如果有预览视频则获取预览视频地址，否则获取详情
//				str_shareVedioUrl = myVideo.getVideoPart();
//			} else {
//				if (myVideo.getVideoDetail() != null) {
//					str_shareVedioUrl = myVideo.getVideoDetail();
//				}
//			}
//			img_shareImgUrl = myVideo.getVideoImgUrl(); // 预览图片
//		} else if (weibo.hasImage()) {
//			// 没有视频如果有图片的话使用图片地址
//			img_shareImgUrl = weibo.getAttachs().get(0).getSmall();
//		} else {
//			// 既没有视频也没有图片的话使用ts的图标，可能需要进一步细化，考虑使用微博的转发微博的图片
//			img_shareImgUrl = "http://dev.thinksns.com/t4/addons/theme/stv1/_static/image/mobile_default.png";
//		}
//		Log.v("FunctionThirdPlatForm--doShare", "wztest title:"
//				+ str_shareTitle + " img：" + img_shareImgUrl + " platfrom:"
//				+ pf.getName() + "isValid" + pf.isValid());
        // 封装到ShareSdk进行分享
        ShareParams sp = new ShareParams();
        sp.setTitle(str_shareTitle);// 标题
        sp.setText(str_shareContent);// 内容

        if (pf.getName().equals(QQ.NAME) || pf.getName().equals(QZone.NAME)) {
            // QQ分享
            sp.setTitleUrl(str_shareUrl); // 标题的超链接,点击之后直接跳转到指定的页面
            if (img_shareImgUrl != null) {
                sp.setImageUrl(img_shareImgUrl.toString());
            }
            sp.setSite(context.getResources().getString(R.string.app_name));
            sp.setSiteUrl(configHost[0]);

        } else if (pf.getName().equals(Wechat.NAME)
                || pf.getName().equals(WechatMoments.NAME)) {
            // 分享到微信
            sp.setShareType(Wechat.SHARE_WEBPAGE);// 微信必须设置一个SHARE类型，否则返回err,这里使用3G网站
            sp.setImageUrl(img_shareImgUrl);
            sp.setUrl(str_shareUrl);// 超链接，点击之后进入3G网站
            if (str_shareVedioUrl != null) {// 分享视频
                sp.setShareType(Wechat.SHARE_VIDEO);
                sp.setUrl(str_shareVedioUrl);
            } else {
                // 在這里填充其他扩充类型，例如音乐等

            }
        } else if (pf.getName().equals(SinaWeibo.NAME)) {
            // 分享到新浪微博
            //建议使用图文分享，目前不能使用，正在审核高级功能，随后将取消注释
            sp.setImageUrl(img_shareImgUrl);

            sp.setSite(context.getResources().getString(R.string.app_name));
            sp.setSiteUrl(configHost[0]);
        }

        // 设置分享事件回调
        pf.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.v("FunctionThirdPlatForm--shareWeibo  err",
                        arg2.getMessage() + "");
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_ERR;
                handlerUI.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_SUCCESS;
                handlerUI.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Message msg = new Message();
                msg.what = StaticInApp.DO_THIRD_SHARE;
                msg.arg1 = MSG_CANCEL;
                handlerUI.sendMessage(msg);
            }
        });
        // 执行图文分享
        pf.share(sp);
    }
}
