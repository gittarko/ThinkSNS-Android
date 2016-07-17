package com.thinksns.sociax.t4.android.view;

import org.json.JSONObject;

/**
 * Created by hedong on 16/4/5.
 */
public interface IWeibaDetailView {
    //设置微吧详情页头部数据
    public void setWeibaHeaderContent(JSONObject jsonObject);
    //关注/取消关注微吧
    public void changeWeibaFollow(int status, String message);
}
