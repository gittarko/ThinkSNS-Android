package com.thinksns.sociax.t4.android.view;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * Created by hedong on 16/2/15.
 */
public interface IWeiboDetailsView {
    //设置分享正文内容
    public void setWeiboContent(ModelWeibo weibo);

    //设置点赞用户列表
    public void setDiggUsers(ListData<ModelUser> users);

    //设置分享评论列表
    public void setWeiboComments(ListData<SociaxItem> comments);

    //设置数据加载错误提示
    void setErrorData(String error);

    //改变微博点赞/取消赞UI
    void digWeiboUI(int status);

    void collectWeiboUI(int status);

    //切换微博收藏状态
    void toggleCollectStatus();

    void commentWeiboUI(int status, String ctime);

    void addCommentWeibo(ModelComment comment);

}
