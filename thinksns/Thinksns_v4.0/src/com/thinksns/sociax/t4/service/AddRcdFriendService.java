package com.thinksns.sociax.t4.service;

import android.app.IntentService;
import android.content.Intent;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelChannel;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddRcdFriendService extends IntentService {

    private List<ModelSearchUser> users;
    private List<ModelChannel> channels;
    private final ExecutorService single = Executors.newSingleThreadExecutor();

    public AddRcdFriendService() {
        super("AddRcdFriendService");
    }

    public AddRcdFriendService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Thinksns app = (Thinksns) getApplication();
        users = (List<ModelSearchUser>) intent.getSerializableExtra("users");
        if (users != null) {
            for (final ModelSearchUser user : users) {
                if (user.getFollowing().equals("1")) {  // 如果为1表示要关注
                    single.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                app.getUsers().changeFollowing(user.getUid(), Integer.parseInt(user.getFollowing()));
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

        channels = (List<ModelChannel>) intent.getSerializableExtra("channel");

        if (channels != null) {
            for (final ModelChannel channel : channels) {
                if (channel.getIs_follow() == 1) {
                    single.execute(new Runnable() {
                        @Override
                        public void run() {
                            app.getChannelApi().changeFollow(channel.getId() + "", channel.getIs_follow() + "");
                        }
                    });
                }
            }
        }
    }
}
