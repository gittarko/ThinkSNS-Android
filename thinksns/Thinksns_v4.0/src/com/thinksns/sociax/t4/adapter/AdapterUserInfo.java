package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.task.ActivityMedalPavilion;
import com.thinksns.sociax.t4.android.user.ActivityChangeUserInfo;
import com.thinksns.sociax.t4.android.user.ActivityFollowUser;
import com.thinksns.sociax.t4.android.user.ActivityOtherUserBaseInfo;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 类说明：用户主页
 *
 */
public class AdapterUserInfo extends ListBaseAdapter<ModelUser> {
    private final String TAG = "AdapterUserInfoHome";
    private boolean isMe = false; // 标记是不是息的个人中心

    public AdapterUserInfo(Context context) {
        super(context);
    }

    @Override
    protected boolean hasFooterView() {
        return false;
    }

    @Override
    public View getRealView(int position, View convertView, ViewGroup parent) {
        HolderSociax holder = null;
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            LayoutInflater inflater = getLayoutInflater(mContext);
            holder = new HolderSociax();
            convertView = inflater.inflate(R.layout.fragment_userinfo_home, null);
            //认证信息
            holder.ll_oauth_info = (LinearLayout) convertView.findViewById(R.id.ll_oauth_info);
            holder.tv_oauth = (TextView) convertView.findViewById(R.id.tv_oau);
            //粉丝数
            holder.tv_user_info_follow = (TextView) convertView
                    .findViewById(R.id.tv_user_info_follow);
            //关注数
            holder.tv_user_info_following = (TextView) convertView
                    .findViewById(R.id.tv_user_info_following);
            //地区
            holder.tv_user_info_from = (TextView) convertView
                    .findViewById(R.id.tv_user_info_from);
            //简介
            holder.tv_user_info_intro = (TextView) convertView
                    .findViewById(R.id.tv_user_info_intro);
            //基本信息
            holder.rl_mycenter_home_userinfo = (RelativeLayout) convertView
                    .findViewById(R.id.rl_mycenter_home_userinfo);
            //关注
            holder.ll_mycenter_home_following = (LinearLayout) convertView
                    .findViewById(R.id.rl_mycenter_home_following);
            //粉丝
            holder.ll_mycenter_home_follow = (LinearLayout) convertView
                    .findViewById(R.id.rl_mycenter_home_follow);
            //没有关注任何人提示
            holder.tv_tips_nofollow = (TextView) convertView
                    .findViewById(R.id.tv_tips_nofollowed);
            //没有粉丝提示
            holder.tv_tips_nofollower = (TextView) convertView
                    .findViewById(R.id.tv_tips_nofollower);

            // 关注和粉丝用户名
//            holder.tv_follow_name1 = (TextView) convertView
//                    .findViewById(R.id.tv_follow_name1);
//            holder.tv_follow_name2 = (TextView) convertView
//                    .findViewById(R.id.tv_follow_name2);
//            holder.tv_follow_name3 = (TextView) convertView
//                    .findViewById(R.id.tv_follow_name3);
//            holder.tv_follow_name4 = (TextView) convertView
//                    .findViewById(R.id.tv_follow_name4);
//            holder.tv_follow_name5 = (TextView) convertView
//                    .findViewById(R.id.tv_follow_name5);

//            holder.tv_following_name1 = (TextView) convertView
//                    .findViewById(R.id.tv_followed_name1);
//            holder.tv_following_name2 = (TextView) convertView
//                    .findViewById(R.id.tv_followed_name2);
//            holder.tv_following_name3 = (TextView) convertView
//                    .findViewById(R.id.tv_followed_name3);
//            holder.tv_following_name4 = (TextView) convertView
//                    .findViewById(R.id.tv_followed_name4);
//            holder.tv_following_name5 = (TextView) convertView
//                    .findViewById(R.id.tv_followed_name5);
            //粉丝头像列表
//            holder.img_follow_one = (ImageView) convertView
//                    .findViewById(R.id.img_follow_one);
//            holder.img_follow_two = (ImageView) convertView
//                    .findViewById(R.id.img_follow_two);
//            holder.img_follow_three = (ImageView) convertView
//                    .findViewById(R.id.img_follow_three);
//            holder.img_follow_four = (ImageView) convertView
//                    .findViewById(R.id.img_follow_four);
//            holder.img_follow_five = (ImageView) convertView
//                    .findViewById(R.id.img_follow_five);
            //关注头像列表
//            holder.img_following_one = (ImageView) convertView
//                    .findViewById(R.id.img_followed_one);
//            holder.img_following_two = (ImageView) convertView
//                    .findViewById(R.id.img_followed_two);
//            holder.img_following_three = (ImageView) convertView
//                    .findViewById(R.id.img_followed_three);
//            holder.img_following_four = (ImageView) convertView
//                    .findViewById(R.id.img_followed_four);
//            holder.img_following_five = (ImageView) convertView
//                    .findViewById(R.id.img_followed_five);
            //勋章
            holder.ll_honner_info = (LinearLayout) convertView
                    .findViewById(R.id.ll_honner_info);
            holder.ll_honner = (LinearLayout) convertView.findViewById(R.id.ll_honner);
            holder.tv_change_user_info = (TextView) convertView
                    .findViewById(R.id.tv_change_user_info);
            convertView.setTag(R.id.tag_viewholder, holder);

        } else {
            holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        final ModelUser user = getItem(position);

        /**
         * 跳转到关注页面
         */
        holder.ll_mycenter_home_following
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(mContext,
                                ActivityFollowUser.class);
                        intent.putExtra("type", "following");
                        intent.putExtra("uid", user.getUid());
                        mContext.startActivity(intent);

                    }
                });

        /**
         * 跳转到关注页面
         */
        holder.ll_mycenter_home_follow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,
                        ActivityFollowUser.class);
                intent.putExtra("type", "follow");
                intent.putExtra("uid", user.getUid());

                mContext.startActivity(intent);

            }
        });

        //关注、粉丝数
        if (user.getFollowedCount() != 0) {
            holder.tv_user_info_follow.setText(user.getFollowedCount() + "");
        } else {
            holder.tv_tips_nofollower.setVisibility(View.VISIBLE);
        }
        if (user.getFollowersCount() != 0) {
            holder.tv_user_info_following.setText(user.getFollowersCount() + "");
        } else {
            holder.tv_tips_nofollow.setVisibility(View.VISIBLE);
        }

        //地区
        String location = user.getLocation();
        if(location == null || location.isEmpty() || location.equals("null"))
            location = "来自星星的你";
        holder.tv_user_info_from.setText(location);
        String intro = user.getIntro();
        if(intro == null || intro.isEmpty() || intro.equals("null"))
            intro = "这家伙很懒，什么也没留下";
        holder.tv_user_info_intro.setText(intro);

        if (user.getUid() == Thinksns.getMy().getUid()) {
            holder.tv_change_user_info.setText("编辑资料");
        }
        //进入用户基本信息页
        holder.rl_mycenter_home_userinfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user.getUid() == Thinksns.getMy().getUid()) {
                    Intent intent = new Intent(mContext,
                            ActivityChangeUserInfo.class);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, ActivityOtherUserBaseInfo.class);
                    intent.putExtra("uname", user.getUserName());
                    intent.putExtra("uface", user.getUserface());
                    intent.putExtra("city", user.getLocation());
                    intent.putExtra("intro", user.getIntro());
                    intent.putExtra("score", user.getUserCredit().getScore_value() + "");
                    intent.putExtra("level", user.getUserLevel()
                            .getLevel() + "");
                    mContext.startActivity(intent);

                }
            }
        });

        //认证信息
        String certInfo = user.getCertInfo();
        if (certInfo != null && !certInfo.equals("null") && !certInfo.equals("")) {
            holder.tv_oauth.setText(user.getCertInfo());
        } else {
            holder.tv_oauth.setText("无");
            holder.ll_oauth_info.setVisibility(View.GONE);
        }

        if (user != null) {
            // 勋章
            holder.ll_honner_info.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,
                            ActivityMedalPavilion.class);
                    intent.putExtra("uid", user.getUid());
                    mContext.startActivity(intent);
                }
            });
            try {
                if(user.getMedals() == null) {
                    holder.ll_honner_info.setVisibility(View.GONE);
                }else {
                    final JSONArray honner = new JSONArray(user.getMedals());
                    if (honner.length() == 0) {
                        holder.ll_honner_info.setVisibility(View.GONE);
                    } else {
                        holder.ll_honner_info.setVisibility(View.VISIBLE);
                        int size = UnitSociax.dip2px(mContext, 25);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                        lp.setMargins(0, 0, UnitSociax.dip2px(mContext, 10), 0);
                        for (int i = 0; i < honner.length(); i++) {
                            ImageView imageView = new ImageView(mContext);

                            imageView.setLayoutParams(lp);
                            holder.ll_honner.addView(imageView);
                            Glide.with(mContext).load(honner.getString(i))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(new GlideCircleTransform(mContext))
                                    .crossFade().into(imageView);
                        }


                    }
                }
            } catch (Exception e) {
                holder.ll_honner_info.setVisibility(View.GONE);
                e.printStackTrace();
            }

            final JSONArray follower = user.getFollower_t4();
            final JSONArray following = user.getFollowing_t4();
            if (follower.length() == 0) {
            } else {
                if (follower != null) {
                    try {
                        if (null != follower.getJSONObject(0)) {
                            holder.tv_follow_name1.setText(follower
                                    .getJSONObject(0).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(follower.getJSONObject(0).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade().into(holder.img_follow_one);

                            holder.img_follow_one
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra("uid", follower
                                                        .getJSONObject(0)
                                                        .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }
                        if (null != follower.getJSONObject(1)) {
                            holder.tv_follow_name2.setText(follower
                                    .getJSONObject(1).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(follower.getJSONObject(1).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade().into(holder.img_follow_two);

                            holder.img_follow_two
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra("uid", follower
                                                        .getJSONObject(1)
                                                        .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }
                        if (null != follower.getJSONObject(2)) {
                            holder.tv_follow_name3.setText(follower
                                    .getJSONObject(2).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(follower.getJSONObject(2).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade().into(holder.img_follow_three);

                            holder.img_follow_three
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra("uid", follower
                                                        .getJSONObject(2)
                                                        .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }
                        if (null != follower.getJSONObject(3)) {
                            holder.tv_follow_name4.setText(follower
                                    .getJSONObject(3).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(follower.getJSONObject(3).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade().into(holder.img_follow_four);

                            holder.img_follow_four
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra("uid", follower
                                                        .getJSONObject(3)
                                                        .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }
                        if (null != follower.getJSONObject(4)) {
                            holder.tv_follow_name5.setText(follower
                                    .getJSONObject(4).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(follower.getJSONObject(4).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade().into(holder.img_follow_five);

                            holder.img_follow_five
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra("uid", follower
                                                        .getJSONObject(4)
                                                        .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            mContext.startActivity(intent);
                                        }
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (following.length() == 0) {
            } else {
                if (following != null) {
                    try {
                        if (null != following.getJSONObject(0)) {
                            holder.tv_following_name1.setText(following
                                    .getJSONObject(0).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(following.getJSONObject(0).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade().into(holder.img_following_one);

                            holder.img_following_one
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra(
                                                        "uid",
                                                        following
                                                                .getJSONObject(
                                                                        0)
                                                                .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }

                        if (null != following.getJSONObject(1)) {
                            holder.tv_following_name2.setText(following
                                    .getJSONObject(1).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(following.getJSONObject(1).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade().into(holder.img_following_two);

                            holder.img_following_two
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra(
                                                        "uid",
                                                        following
                                                                .getJSONObject(
                                                                        1)
                                                                .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }

                        if (null != following.getJSONObject(2)) {
                            holder.tv_following_name3.setText(following
                                    .getJSONObject(2).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(following.getJSONObject(2).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade()
                                    .into(holder.img_following_three);

                            holder.img_following_three
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra(
                                                        "uid",
                                                        following
                                                                .getJSONObject(
                                                                        2)
                                                                .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }
                        if (null != following.getJSONObject(3)) {
                            holder.tv_following_name4.setText(following
                                    .getJSONObject(3).getString("uname") + "");

                            Glide.with(mContext).load(following.getJSONObject(3).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade()
                                    .into(holder.img_following_four);

                            holder.img_following_four
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra(
                                                        "uid",
                                                        following
                                                                .getJSONObject(
                                                                        3)
                                                                .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }
                        if (null != following.getJSONObject(4)) {
                            holder.tv_following_name5.setText(following
                                    .getJSONObject(4).getString("uname") + "");

                            Glide.with(mContext)
                                    .load(following.getJSONObject(4).getString(
                                            "avatar"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                            new GlideCircleTransform(mContext))
                                    .crossFade()
                                    .into(holder.img_following_five);

                            holder.img_following_five
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(mContext,
                                                    ActivityUserInfo_2.class);
                                            try {
                                                intent.putExtra(
                                                        "uid",
                                                        following
                                                                .getJSONObject(
                                                                        4)
                                                                .getInt("uid"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mContext.startActivity(intent);
                                        }
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return convertView;
    }

}
