package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;
import android.util.Log;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hedong on 16/2/25.
 */
public class UserInfoPresenter extends BaseListPresenter<ModelUser> {
    int uid;
    String userName;

    public UserInfoPresenter(Context context, IBaseListView<ModelUser> baseListView,
                             int uid, String userName) {
        super(context, baseListView);
        this.uid = uid;
        this.userName = userName;
    }


    @Override
    public ListData<ModelUser> parseList(String result) {
        try {
            ListData<ModelUser> list = new ListData<ModelUser>();
            ModelUser newUser = new ModelUser(new JSONObject(result));
            newUser.setToken(Thinksns.getMy().getToken());
            newUser.setSecretToken(Thinksns.getMy().getSecretToken());
            list.add(newUser);
            return list;
        } catch (JSONException e) {
            Log.d(AppConstant.APP_TAG, "解析个人信息出错 。。。" + e.toString());
            e.printStackTrace();
        } catch (DataInvalidException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected ListData<ModelUser> readList(Serializable seri) {
        return null;
    }

    @Override
    public String getCachePrefix() {
        return "user_info";
    }

    @Override
    public void loadNetData() {
        new Api.Users().show(uid, userName, mHandler);
    }

}
