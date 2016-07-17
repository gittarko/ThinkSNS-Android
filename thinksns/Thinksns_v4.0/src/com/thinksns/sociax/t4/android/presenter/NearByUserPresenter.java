package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hedong on 16/3/1.
 */
public class NearByUserPresenter extends BaseListPresenter<ModelSearchUser> {
    private double latitude, longitude;
    private int page = 1;

    public NearByUserPresenter(Context context, IBaseListView<ModelSearchUser> baseListView) {
        super(context, baseListView);
    }

    public void setLatitudeLngitude(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public ListData<ModelSearchUser> parseList(String result) {
        ListData<ModelSearchUser> listData = new ListData<ModelSearchUser>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                ModelSearchUser follow = new ModelSearchUser(
                        jsonArray.getJSONObject(i));
                if (follow.getUid() != 0)
                    listData.add(follow);
            }
            return listData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected ListData<ModelSearchUser> readList(Serializable seri) {
        return (ListData<ModelSearchUser>)seri;
    }

    @Override
    protected boolean compareTo(List<? extends SociaxItem> data, SociaxItem enity) {
        return data.contains(enity);
    }

    @Override
    public String getCachePrefix() {
        return "user_list";
    }

    @Override
    public void loadNetData() {
        new Api.Users().getNearByUser(latitude, longitude, page, mHandler);
    }

    @Override
    public void setMaxId(int maxId) {
        if(maxId > 0) {
            page++;
        }else {
            page = 1;
        }
        super.setMaxId(maxId);
    }
}
