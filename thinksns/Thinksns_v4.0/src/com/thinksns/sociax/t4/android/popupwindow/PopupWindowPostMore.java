package com.thinksns.sociax.t4.android.popupwindow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.db.PostSqlHelper;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionThirdPlatForm;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateTransportWeibo;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 类说明： 帖子更多
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-5
 */
public class PopupWindowPostMore implements OnClickListener {
    private static final String TAG = "MorePopupWindow";
    private PopupWindow mPopupWindow;
    private LayoutInflater inflater;

    private TextView mTvComment, mTvTranspond, mTvCollection, mTvDenounce,
            mTvDelete, tv_share_to_sina, tv_share_to_weichat,
            tv_share_to_weichatfriends, tv_share_to_qq, tv_share_to_qqweibo,
            tv_share_to_qzone;

    private Context context;
    private Button btnCancel;
    private ModelPost post;
    private FragmentSociax fragment;
    private PopupWindowHandler handler;
    ListenerSociax listener;
    private Thinksns app;

    public ListenerSociax getListener() {
        return listener;
    }

    public void setListener(ListenerSociax listener) {
        this.listener = listener;
    }

    /**
     * 帖子更多
     *
     * @param post
     */
    public PopupWindowPostMore(Context context, ModelPost post) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        handler = new PopupWindowHandler();
        this.post = post;
        initSharePlatform();
        initPopuptWindow();
        app = Thinksns.getApplication();
    }

    /**
     * 初始化分享平台
     */
    private void initSharePlatform() {
        ShareSDK.initSDK(context);
    }

    /**
     * 获取PopupWindow实例
     */
    public PopupWindow getPopupWindowInstance() {
        if (null == mPopupWindow) {
            initPopuptWindow();
        }
        return mPopupWindow;

    }

    /**
     * 创建PopupWindow
     */
    private void initPopuptWindow() {
        View popupWindow = inflater.inflate(R.layout.more_popupwindow, null);
        btnCancel = (Button) popupWindow.findViewById(R.id.btn_pop_cancel);
        btnCancel.setOnClickListener(this);
//		mTvComment = (TextView) popupWindow.findViewById(R.id.tv_more_comment);
        //转发
        mTvTranspond = (TextView) popupWindow
                .findViewById(R.id.tv_more_transpond);
        //收藏
        mTvCollection = (TextView) popupWindow
                .findViewById(R.id.tv_more_collection);
        //举报
        mTvDenounce = (TextView) popupWindow
                .findViewById(R.id.tv_more_denounce);
        //删除
        mTvDelete = (TextView) popupWindow.findViewById(R.id.tv_more_delete);

        tv_share_to_sina = (TextView) popupWindow
                .findViewById(R.id.tv_share_to_sinaweibo);
        tv_share_to_weichat = (TextView) popupWindow
                .findViewById(R.id.tv_share_to_weichat);
        tv_share_to_weichatfriends = (TextView) popupWindow
                .findViewById(R.id.tv_share_to_weichatfav);
        tv_share_to_qq = (TextView) popupWindow
                .findViewById(R.id.tv_share_to_qq);
        tv_share_to_qqweibo = (TextView) popupWindow
                .findViewById(R.id.tv_share_to_qqweibo);
        tv_share_to_qzone = (TextView) popupWindow
                .findViewById(R.id.tv_share_to_qzone);

//		mTvComment.setOnClickListener(this);
        mTvTranspond.setOnClickListener(this);
        mTvCollection.setOnClickListener(this);
        showFavStatus(post.isIs_favourite());
        tv_share_to_weichat.setOnClickListener(this);
        tv_share_to_weichatfriends.setOnClickListener(this);
        tv_share_to_sina.setOnClickListener(this);
        tv_share_to_qq.setOnClickListener(this);
        tv_share_to_qzone.setOnClickListener(this);
        tv_share_to_qqweibo.setOnClickListener(this);

        mTvDenounce.setVisibility(View.GONE);
//		mTvDelete.setVisibility(View.GONE);

        //帖子是
        if (post.getPost_uid() == Thinksns.getMy().getUid() || post.getWeiba().getAdmin_uid() == Thinksns.getMy().getUid()
                || Thinksns.getMy().getIs_admin().equals("1")) {
            mTvDenounce.setVisibility(View.GONE);
            mTvDelete.setVisibility(View.VISIBLE);
            mTvDelete.setOnClickListener(this);
        } else {
            mTvDenounce.setVisibility(View.VISIBLE);
            mTvDelete.setVisibility(View.GONE);
            mTvDenounce.setOnClickListener(this);
        }

        // 创建一个PopupWindow
        // 参数1：contentView 指定PopupWindow的内容
        // 参数2：width 指定PopupWindow的width
        // 参数3：height 指定PopupWindow的height
        mPopupWindow = new PopupWindow(popupWindow, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_pop_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.tv_more_transpond:
                Bundle data1 = new Bundle();
                data1.putInt("feed_id", post.getPost_id());
                data1.putInt("type", AppConstant.CREATE_TRANSPORT_POST);
                ActivityStack.startActivityForResult((ThinksnsAbscractActivity) context,
                        ActivityCreateTransportWeibo.class, data1);
                mPopupWindow.dismiss();
                break;
            case R.id.tv_more_collection:
                try {
                    new Api.WeibaApi().favPost(post, new ApiHttpClient.HttpResponseListener() {

                        @Override
                        public void onSuccess(Object result) {
                            JSONObject json = (JSONObject) result;
                            try {
                                if (json.getInt("status") == 1) {
                                    post.setIs_favourite(!post.isIs_favourite());
                                    showFavStatus(post.isIs_favourite());
                                    Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "数据解析错误", Toast.LENGTH_SHORT).show();
                            }
                            mTvCollection.setEnabled(true);
                        }

                        @Override
                        public void onError(Object result) {
                            Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
                            mTvCollection.setEnabled(true);
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_more_denounce:
                //弹出举报对话框
                final PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(view.getContext());
                builder.setTitle("温馨提示", 18);
                builder.setMessage(view.getContext().getResources().getString(R.string.denounce_tips), 0)
                        .setEditText("");
                builder.setPositiveButton("举报", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String content = builder.getEditContent();
                        if(!TextUtils.isEmpty(content)) {
                            denouncePost(content);
                        }else {
                            Toast.makeText(view.getContext(), "请填写举报内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                builder.create();
                break;
            case R.id.tv_more_delete:
                //删除前先确认
                confirmDeletePost();
                break;
            case R.id.tv_share_to_sinaweibo:
                onSinaWeiboShare();
                break;
            case R.id.tv_share_to_weichat:
                onweichatShare();
                break;
            case R.id.tv_share_to_qq:
                onQQShare();
                break;
            case R.id.tv_share_to_weichatfav:
                onWeichatMovementShare();
                break;
            case R.id.tv_share_to_qzone:
                onQzoneShare();
                break;
            case R.id.tv_share_to_qqweibo:
                onQQWeiboShare();
                break;
            default:
                break;
        }
    }


    /**
     * 举报帖子
     */
    private void denouncePost(final String reason) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    ModelBackMessage backMessage = null;
                    message.what = AppConstant.DENOUNCE;
                    backMessage = new Api.StatusesApi().denouncePost(
                            post.getPost_id(), reason);
                    message.obj = backMessage;
                    message.arg1 = 1;
                    handler.sendMessage(message);
                } catch (ApiException e) {
                    message.arg1 = 2;
                    e.printStackTrace();
                } catch (JSONException e) {
                    message.arg1 = 2;
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //确认删除帖子
    private void confirmDeletePost() {
        PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(context);
        builder.setMessage("确认删除?", 16);
        builder.setTitle(null, 0);
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mTvDelete.setEnabled(false);

                try {
                    app.getWeibaApi().delPost(post.getPost_id(), new ApiHttpClient.HttpResponseListener() {
                        @Override
                        public void onSuccess(Object result) {
                            mTvDelete.setEnabled(true);
                            ModelBackMessage message = (ModelBackMessage) result;
                            if (message.getStatus() == 1) {
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                if (context instanceof ActivityPostDetail) {
                                    ((ActivityPostDetail) context).finish();
                                }
                                PostSqlHelper.getInstance(context).delPost(post);//更新数据库
                                sendBroad();//更新界面
                                mPopupWindow.dismiss();
                            } else {
                                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Object result) {
                            mTvDelete.setEnabled(false);
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create();
    }

    public void sendBroad(){
        Intent intent=new Intent(StaticInApp.UPDATA_WEIBA);
        context.sendBroadcast(intent);
    }

    /**
     * 分享到腾讯微博
     */
    private void onQQWeiboShare() {
//        FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
//                ShareSDK.getPlatform(TencentWeibo.NAME));
//        fc_share.doSharePost(post);
    }

    /**
     * 分享到QQ空间
     */
    private void onQzoneShare() {
        FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
                ShareSDK.getPlatform(QZone.NAME));
        fc_share.doSharePost(post);
    }

    /**
     * 分享到朋友圈
     */
    private void onWeichatMovementShare() {
        FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
                ShareSDK.getPlatform(WechatMoments.NAME));
        fc_share.doSharePost(post);
    }

    /**
     * 分享到微信
     */
    private void onweichatShare() {
        FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
                ShareSDK.getPlatform(Wechat.NAME));
        fc_share.doSharePost(post);
    }

    /**
     * 分享到QQ
     */
    private void onQQShare() {
        FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
                ShareSDK.getPlatform(QQ.NAME));
        fc_share.doSharePost(post);
    }

    /**
     * 分享到微博
     */
    private void onSinaWeiboShare() {
        FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
                ShareSDK.getPlatform(SinaWeibo.NAME));
        fc_share.doSharePost(post);
    }

    class PopupWindowHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConstant.FAVORITE:
                    // if (msg.arg1 == 1) {
                    // ModelBackMessage message = (ModelBackMessage) msg.obj;
                    // if (message.getStatus() == 1) {
                    // Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT)
                    // .show();
                    // post.setFavorited(true);
                    // mPopupWindow.dismiss();
                    // } else {
                    // Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT)
                    // .show();
                    // }
                    // }
                    break;
                case AppConstant.UNFAVORITE:
                    // if (msg.arg1 == 1) {
                    // ModelBackMessage message = (ModelBackMessage) msg.obj;
                    // if (message.getStatus() == 1) {
                    // Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT)
                    // .show();
                    // post.setFavorited(false);
                    // mPopupWindow.dismiss();
                    // } else {
                    // Toast.makeText(context, "取消收藏失败", Toast.LENGTH_SHORT)
                    // .show();
                    // }
                    // }
                    break;
                case AppConstant.DELETEWEIBO:
                    // if (msg.arg1 == 1) {
                    // ModelBackMessage message = (ModelBackMessage) msg.obj;
                    // if (message.getStatus() == 1) {
                    // Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT)
                    // .show();
                    // mPopupWindow.dismiss();
                    // } else {
                    // Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT)
                    // .show();
                    // }
                    // }
                    break;
                case AppConstant.DENOUNCE:
                    if (msg.arg1 == 1) {
                        ModelBackMessage message = (ModelBackMessage) msg.obj;
                        if (message.getStatus() == 1) {
                            Toast.makeText(context, "举报成功", Toast.LENGTH_SHORT)
                                    .show();
                            mPopupWindow.dismiss();
                        } else {
                            Toast.makeText(context, "举报失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                default:

                    break;
            }
        }
    }

    /**
     * 显示收藏状态
     *
     * @param isFav
     */
    private void showFavStatus(boolean isFav) {
        if (isFav) {
            mTvCollection.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.ic_more_collected, 0, 0);
            mTvCollection.setText("已收藏");
        } else {
            mTvCollection.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.ic_more_collect, 0, 0);
            mTvCollection.setText("收藏");
        }
    }

    /**
     * 自定义分享个数
     *
     * @return
     */
    public List<Map<String, Object>> getSharedByData() {
        List<Map<String, Object>> sharedList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("bgId", R.drawable.comment);
            map.put("text", "微信" + i);
            sharedList.add(map);
        }
        return sharedList;
    }
}
