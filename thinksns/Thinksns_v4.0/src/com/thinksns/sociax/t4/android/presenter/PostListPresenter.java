package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.fragment.BaseFragmentPostList;

import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hedong on 16/3/1.
 */
public class PostListPresenter extends BaseListPresenter<ModelPost> {

    //请求帖子列表类型
    public static final int WEIBA_DETAILS = 0x10;      //微吧详情,需要微吧ID
    public static final int MY_COLLECT_POST = 0x11;     //我收藏的帖子
    public static final int RECOMMEND_POST = 0x12;       //推荐帖子
    public static final int WEIBA_WALK = 0x13;          //逛一逛微吧

    protected int mType;
    protected int weiba_id;

    public PostListPresenter(Context context, IBaseListView<ModelPost> baseListView) {
        super(context, baseListView);
        mType = ((BaseFragmentPostList)baseListView).getRequestPostType();
        weiba_id = ((BaseFragmentPostList)baseListView).getWeibaId();
    }

    @Override
    public ListData<ModelPost> parseList(String result) {
        try {
            JSONArray data = null;
            if(mType != WEIBA_WALK) {
                JSONObject jsonObject = new JSONObject(result);
                data = jsonObject.getJSONArray("data");
            }else {
                data = new JSONArray(result);
            }

            int length = data.length();
            ListData<ModelPost> list = new ListData<ModelPost>();
            for (int i = 0; i < length; i++) {
                JSONObject item = data.getJSONObject(i);
                ModelPost post = new ModelPost(item);
                list.add(post);
            }
            return list;
        }catch(JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected ListData<ModelPost> readList(Serializable seri) {
        return (ListData<ModelPost>)seri;
    }

    @Override
    public String getCachePrefix() {
        String prefix = "";
        switch (mType) {
            case WEIBA_DETAILS:
                prefix = "weiba_details";
                break;
            case RECOMMEND_POST:
                prefix = "recommend_post";
                break;
            case MY_COLLECT_POST:
                prefix = "my_collect_post";
                break;
            case WEIBA_WALK:
                prefix = "weiba_walk";
                break;
            default:
                prefix = "weiba_details";
                break;
        }

        return prefix;
    }

    @Override
    public void loadNetData() {
        if(mType == WEIBA_DETAILS) {
            //微吧详情需要微吧ID
            if(((BaseFragmentPostList)baseListView).getWeibaId() <= 0) {
                return;
            }

        }else if(mType == RECOMMEND_POST) {

        }else if(mType == MY_COLLECT_POST) {
            new Api.WeibaApi().collectPost(mHandler);
        }else if(mType == WEIBA_WALK) {
            new Api.WeibaApi().getPostAll(weiba_id, getPageSize(), getMaxId(), mHandler);
        }
    }


}
