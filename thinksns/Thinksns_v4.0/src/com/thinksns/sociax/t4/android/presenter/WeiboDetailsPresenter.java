package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.android.view.IWeiboDetailsView;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.ButtonUtils;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.unit.SociaxUIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hedong on 16/2/15.
 */
public class WeiboDetailsPresenter {
    private IWeiboDetailsView mWeiboDetailsView;
    private int weibo_id;

    public WeiboDetailsPresenter(IWeiboDetailsView detailsView) {
        this.mWeiboDetailsView = detailsView;
    }

    public void loadWeiboDetails(int weibo_id) {
        this.weibo_id = weibo_id;
        new getWeiboDetailTask().execute();
    }

    //对微博点赞或取消赞
    public void postDigWeibo(final ModelWeibo weibo) {
        if(weibo == null) {
            return;
        }
        ListData<ModelUser> users = weibo.getDiggUsers();
        if(weibo.isDigg()) {
            weibo.setIsDigg(false);
            weibo.setDiggNum(weibo.getDiggNum() - 1);
            users.remove(Thinksns.getMy());
        }else {
            weibo.setIsDigg(true);
            weibo.setDiggNum(weibo.getDiggNum() + 1);
            users.add(0, Thinksns.getMy());
        }

        mWeiboDetailsView.setDiggUsers(users);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doDigWeibo(weibo);
            }
        }).start();

    }

    private void doDigWeibo(final ModelWeibo weibo) {
        int result = 0;
        try {
            result = executeDigWeibo(weibo);
        } catch (ApiException e) {
            System.err.println(e.toString());
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        if(result == 1) {

        }else {
            ListData<ModelUser> users = weibo.getDiggUsers();
            if(weibo.isDigg()) {
                users.remove(Thinksns.getMy());
                weibo.setIsDigg(false);
                weibo.setDiggNum(weibo.getDiggNum() - 1);
            }else {
                users.add(0, Thinksns.getMy());
                weibo.setIsDigg(true);
                weibo.setDiggNum(weibo.getDiggNum() + 1);
            }

            mWeiboDetailsView.setDiggUsers(users);
        }

        mWeiboDetailsView.digWeiboUI(result);
    }

    private int executeDigWeibo(final ModelWeibo weibo) throws ApiException, Exception{
        int result = 0;
        if (weibo.getIsDigg() == 1) {
            // 已经赞过
            result = new Api.StatusesApi().addDig(weibo.getWeiboId());
        } else {
            // 还没有赞过
            result = new Api.StatusesApi().delDigg(weibo.getWeiboId());
        }

        return result;
    }

    /**
     * 收藏微博
     * @param weibo
     */
    public void postCollectWeibo(final ModelWeibo weibo) {
        try {
            new Api.StatusesApi().favWeibo(weibo,
                    new ApiHttpClient.HttpResponseListener() {

                @Override
                public void onSuccess(Object result) {
                    JSONObject json = (JSONObject) result;
                    int status = 0;
                    try {
                        status = json.getInt("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mWeiboDetailsView.collectWeiboUI(status);
                }

                @Override
                public void onError(Object result) {
                    mWeiboDetailsView.collectWeiboUI(-1);
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //同步改变微博的显示状态
        mWeiboDetailsView.toggleCollectStatus();

    }

    public void postCommentWeibo(final ModelWeibo weibo, final String content,
                                 final String to_name, final int to_commentId) {
        if (ButtonUtils.isFastDoubleClick()) {
            return;
        }

        final long ctime = System.currentTimeMillis() / 1000;
        final ModelComment comemnt = createComment(content, to_name, to_commentId);
        comemnt.setCtime(ctime + "");
        mWeiboDetailsView.addCommentWeibo(comemnt);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int status = 0;
                try {
                    Object result = new Api.StatusesApi().commentWeibo(comemnt.getContent(), weibo.getWeiboId(), comemnt.getReplyCommentId());
                    JSONObject jsonObject = new JSONObject(result.toString());
                    status = jsonObject.getInt("cid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (ApiException e) {
                    e.printStackTrace();
                }

                mWeiboDetailsView.commentWeiboUI(status, ctime + "");

            }
        }).start();
    }

    private ModelComment createComment(String content, String to_name, int to_commentId) {
        ModelComment comment = new ModelComment();
        comment.setType(ModelComment.Type.SENDING);      //设置评论处于发表状态
        comment.setUname(Thinksns.getMy().getUserName());
        comment.setUface(Thinksns.getMy().getUserface());
        comment.setUid(Thinksns.getMy().getUid() + "");
        comment.setUserApprove(Thinksns.getMy().getUserApprove());

        if(to_commentId != 0 && to_name != null) {
            String newContent = "回复@" + to_name + "：" + content;
            comment.setContent(newContent);
            comment.setReplyCommentId(to_commentId);
        }else {
            comment.setContent(content);
        }

        return comment;
    }

    class getWeiboDetailTask extends AsyncTask<Void, Void, ModelWeibo> {

        @Override
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
            }else if(result.isWeiboIsDelete() == 1) {
                mWeiboDetailsView.setErrorData("分享已删除");
            }
            else {
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
