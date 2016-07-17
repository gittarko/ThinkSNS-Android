package com.thinksns.sociax.t4.android.view;

import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * Created by hedong on 16/2/26.
 * 用户主页UI层处理
 */
public interface IUserHomeView {
    //设置用户头信息
    void setUserHeadInfo(ModelUser user);
    //设置用户关注
    void setUserFollow(int status, boolean isFollow);
    //设置用户黑名单效果
    void setUserBlackList(boolean status, boolean isBlack);
    //用户信息加载完毕
    void loadUserInfoComplete(ListData<SociaxItem> list);

    /**
     * 回调用户是否具有发私信权利
     * @param status 0:不允许发私信 1：允许发私信 2：获取请求API失败
     */
    void setUserMessagePower(int status);

    //加载用户信息失败
    void loadUserInfoError(String msg);
}
