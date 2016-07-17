package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;
import android.widget.Toast;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.function.FunctionChangeSociaxItemStatus;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowWeiboMore;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hedong on 16/2/19.
 * 微博基类Presenter， 其他类型实现自己的loadNetData,默认请求全部类型的微博数据
 */
public class WeiboListListPresenter extends BaseListPresenter<ModelWeibo> {

    protected String CACHE_PREFIX = "";
    protected WeiboListViewClickListener weiboListViewClickListener;
//    protected ListData<ModelWeibo> weiboList = new ListData<ModelWeibo>();

    public WeiboListListPresenter(Context context, IBaseListView<ModelWeibo> baseListView,
                                  WeiboListViewClickListener listViewClickListener) {
        super(context, baseListView);
        this.weiboListViewClickListener = listViewClickListener;
    }

    @Override
    public ListData<ModelWeibo> parseList(String result) {
        try {
            JSONArray data = new JSONArray(result);
            int length = data.length();
            ListData<ModelWeibo> weiboList = new ListData<ModelWeibo>();
            for (int i = 0; i < length; i++) {
                JSONObject itemData = data.getJSONObject(i);
                try {
                    ModelWeibo weiboData = new ModelWeibo(itemData);
                    weiboList.add(weiboData);
//                    DbHelperManager.getInstance(mContext, ListData.DataType.ALL_WEIBO).add(weiboData);
                } catch (DataInvalidException e) {
                    e.printStackTrace();
                }
            }
            return weiboList;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected ListData<ModelWeibo> readList(Serializable seri) {
        return (ListData<ModelWeibo>)seri;
    }

    /**
     * 过滤重复的数据,子类需要实现自己的相等方法
     * @param data
     * @param enity
     * @return
     */
    @Override
    protected boolean compareTo(List<? extends SociaxItem> data, SociaxItem enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                ModelWeibo weibo = (ModelWeibo)enity;
                if (weibo.getWeiboId() == ((ModelWeibo)data.get(i)).getWeiboId()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getCachePrefix() {
        return "weibolist";
    }

    @Override
    public void loadNetData() {
        //这里加1是因为API有时不稳定造成返回的条数比指定的少一个
        new Api.WeiboApi().publicTimeline(getPageSize(), maxId, mHandler);
    }

    /**
     * 以下是针对微博的业务操作
     */
    public void digWeio(final ModelWeibo weibo) {

    }

    /**
     * 评论微博
     * @param weibo
     */
    public void commentWeibo(final ModelWeibo weibo, final String content, final ModelComment comment) {
        if (weibo.getCommentList() == null) {
            Toast.makeText(mContext, "评论失败", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //创建评论至本地
                    ModelComment replyCom = new ModelComment();
                    replyCom.setUname(Thinksns.getMy().getUserName());
                    replyCom.setUid(Thinksns.getMy().getUid() + "");
                    String replyContent = "";
                    if(comment != null && comment.getToName() != null) {
                        replyCom.setReplyCommentId(comment.getComment_id());
                        replyContent = "回复@" + comment.getToName() + "：" + content;
                    }else {
                        replyContent = content;
                    }

                    replyCom.setContent(replyContent);
                    int toCommentId = 0;
                    int status = 0;
                    if(comment != null)
                        toCommentId = comment.getComment_id();
                    Object result = new Api.StatusesApi().commentWeibo(replyContent, weibo.getWeiboId(),
                            toCommentId);
                    JSONObject json = new JSONObject(result.toString());
                    status = json.getInt("status");
                    if(status == 1) {
                        //评论成功
                        int commentId = json.getInt("cid");
                        replyCom.setComment_id(commentId);
                        weibo.getCommentList().add(0, replyCom);
                        weibo.setCommentCount(weibo.getCommentCount() + 1);
                    }
                    weiboListViewClickListener.onCommentWeiboStatus(status);
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 关注微博
     * @param weibo
     */
    public void followWeibo(final ModelWeibo weibo) {
        FunctionChangeSociaxItemStatus fc = new FunctionChangeSociaxItemStatus(mContext);
        fc.setListenerSociax(new ListenerSociax() {
            @Override
            public void onTaskSuccess() {
                //更新UI
                weibo.setFollowing(1);
                weiboListViewClickListener.onFollowWeiboStatus(1);
            }

            @Override
            public void onTaskError() {
                weiboListViewClickListener.onFollowWeiboStatus(0);
            }

            @Override
            public void onTaskCancle() {
                weiboListViewClickListener.onFollowWeiboStatus(0);
            }
        });

        fc.changeUserInfoFollow(weibo.getUid(), false);
    }

    //微博更多操作
    public PopupWindowWeiboMore doWeiboMore(final ModelWeibo weibo) {
        PopupWindowWeiboMore popup = new PopupWindowWeiboMore(
                mContext, weibo, new PopupWindowWeiboMore.OnWeiboMoreClickListener() {
            @Override
            public void onDelete(int status) {
                weiboListViewClickListener.onDeleteWeiboStatus(status);
            }

            @Override
            public void onCollect(int status) {
                weiboListViewClickListener.onCollectWeiboStatus(status);
            }
        });

        return popup;
    }

    public void deleteWeiboComment(final ModelComment md) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    //删除评论
                    int status = 0;
                    try {
                        String result = (String) new Api.WeiboApi().deleteWeiboComment(md.getComment_id());
                        JSONObject json = new JSONObject(result);
                        status = json.getInt("status");
                    } catch (ApiException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    weiboListViewClickListener.onDeleteWeiboComment(status);
                }

            }).start();
    }
}
