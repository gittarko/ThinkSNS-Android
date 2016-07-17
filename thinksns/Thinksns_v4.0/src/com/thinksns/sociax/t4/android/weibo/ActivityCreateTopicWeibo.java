package com.thinksns.sociax.t4.android.weibo;

/**
 * Created by hedong on 16/4/17.
 * 创建话题微博
 */
public class ActivityCreateTopicWeibo extends ActivityCreateBase{

    @Override
    protected void initIntent() {
        super.initIntent();
        content = "#" + getIntent().getStringExtra("topic") + "#";
    }

}
