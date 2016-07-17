package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;
import android.util.Log;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.android.function.FunctionChangeSociaxItemStatus;
import com.thinksns.sociax.t4.android.view.IUserHomeView;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;

/**
 * Created by hedong on 16/2/25.
 * 用户主页Presenter,包含请求用户基本信息、拉黑/取消拉黑、关注/取消关注、聊天等操作
 */
public class UserHomePresenter {
    private IUserHomeView userHomeView;
    private Context mContext;

    public UserHomePresenter(Context context, IUserHomeView homeView) {
        this.userHomeView = homeView;
        this.mContext = context;
    }

    //加载用户基础信息
    public void loadUserInfo(ModelUser user) {
        new Api.Users().show(user, new ApiHttpClient.HttpResponseListener() {
            @Override
            public void onSuccess(Object result) {
                ListData<SociaxItem> list = (ListData<SociaxItem>) result;
                if (list != null && list.size() > 0) {
                    //更新用户信息
                    ModelUser userInfo = (ModelUser)list.get(0);
                    UserSqlHelper.updateUser(userInfo);
                    userHomeView.setUserHeadInfo(userInfo);
                    userHomeView.loadUserInfoComplete(list);
                }
            }

            @Override
            public void onError(Object result) {
                userHomeView.loadUserInfoError(result.toString());
            }
        });
    }

    //加关注、取消关注
    public void postUserFollow(final ModelUser user) {
        final boolean isFollow = user.isFollowed();
        userHomeView.setUserFollow(-1, !isFollow);

        FunctionChangeSociaxItemStatus fc = new FunctionChangeSociaxItemStatus(mContext);
        fc.setListenerSociax(new ListenerSociax() {

            @Override
            public void onTaskSuccess() {
                userHomeView.setUserFollow(1, !isFollow);
            }

            @Override
            public void onTaskError() {
                userHomeView.setUserFollow(0, isFollow);
            }

            @Override
            public void onTaskCancle() {
                userHomeView.setUserFollow(0, isFollow);
            }
        });

        fc.changeUserInfoFollow(user.getUid(), isFollow);

    }

    //拉黑
    public void postUserBlackList(final ModelUser user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ok = false;
                try {
                    if(!user.getIsInBlackList()) {
                        //没有拉黑则加入黑名单
                        ok = new Api.Friendships().addBlackList(user);
                    }else {
                        //取消拉黑
                        ok = new Api.Friendships().delBlackList(user);
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (VerifyErrorException e) {
                    e.printStackTrace();
                } catch (DataInvalidException e) {
                    e.printStackTrace();
                }
                if(ok) {
                    userHomeView.setUserBlackList(ok, user.getIsInBlackList());
                }else {
                    userHomeView.setUserBlackList(ok, !user.getIsInBlackList());
                }
            }
        }).start();
    }

    //发起私信
    public void postUserMessage(final int uid) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Api.Message messageApi = new Api.Message();
                String msgObj = null;
                int status = 2;
                try {
                    msgObj = (String) messageApi.canSendMessage(uid);
                    ModelBackMessage backMessage = new ModelBackMessage(msgObj);
                    if(backMessage.getStatus() ==1){
                        //允许发私信
                        status = 1;
                        //跳转私信
                    }else if(backMessage.getStatus()==0){
                        //没有发私信权利
                        status = 0;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (DataInvalidException e) {
                    e.printStackTrace();
                } catch (VerifyErrorException e) {
                    e.printStackTrace();
                }

                userHomeView.setUserMessagePower(status);
            }
        }).start();
    }
}
