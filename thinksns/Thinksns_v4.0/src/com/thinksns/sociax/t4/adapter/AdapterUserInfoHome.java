package com.thinksns.sociax.t4.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

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
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明：用户主页
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-19
 */
public class AdapterUserInfoHome extends AdapterSociaxList {
    private ModelUser user;

    private final String TAG = "AdapterUserInfoHome";
    private boolean isMe = false; // 标记是不是息的个人中心

    @Override
    public int getCount() {
        if (list == null || list.size() == 0) {
            if (adapterState == AdapterSociaxList.NO_MORE_DATA) {
                //正在加载或加载结束
                return 1;
            } else if (adapterState == AdapterSociaxList.STATE_LOADING) {
                return 1;
            }
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list == null || list.size() == 0) {
            if (adapterState == AdapterSociaxList.NO_MORE_DATA) {
                return 0;
            } else if (adapterState == AdapterSociaxList.STATE_LOADING) {
                return 2;
            }
        }
        return 1;

    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    /**
     * @param context
     * @param list
     */
    public AdapterUserInfoHome(ThinksnsAbscractActivity context,
                               ListData<SociaxItem> list, ModelUser user) {
        super(context, list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            holder = new HolderSociax();
            if (type == 1) {
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
                //粉丝列表
                holder.ll_following_list = (LinearLayout) convertView.findViewById(R.id.ll_following_list);
                //关注列表
                holder.ll_followed_list = (LinearLayout) convertView.findViewById(R.id.ll_followed_list);
                //没有礼物
//			holder.tv_tips_nogift = (TextView) convertView
//					.findViewById(R.id.tv_tips_nogift);
                //没有关注任何人提示
                holder.tv_tips_nofollow = (TextView) convertView
                        .findViewById(R.id.tv_tips_nofollowed);
                //没有粉丝提示
                holder.tv_tips_nofollower = (TextView) convertView
                        .findViewById(R.id.tv_tips_nofollower);
                //勋章
                holder.ll_honner_info = (LinearLayout) convertView
                        .findViewById(R.id.ll_honner_info);
                holder.ll_honner = (LinearLayout) convertView.findViewById(R.id.ll_honner);
                holder.tv_change_user_info = (TextView) convertView
                        .findViewById(R.id.tv_change_user_info);

                holder.iv_following_next=(ImageView)convertView.findViewById(R.id.iv_following_next);
                holder.iv_followed_next=(ImageView)convertView.findViewById(R.id.iv_followed_next);

                convertView.setTag(R.id.tag_viewholder, holder);
            } else if (type == 2) {
                //加载正在加载数据的界面
                convertView = new EmptyLayout(parent.getContext());
                ListView listView = (ListView) parent;
                int width = listView.getWidth();
                int height = listView.getHeight();
                int count = listView.getHeaderViewsCount();
                int headerH = 0;
                for (int i = 0; i < count; i++) {
                    int bottom = listView.getChildAt(i).getBottom();
                    if (bottom > 0)
                        headerH += bottom;
                }

                height -= headerH;
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
                convertView.setLayoutParams(params);
            }

        } else {
            holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        if (type == 2) {
            ((EmptyLayout) convertView).setErrorType(EmptyLayout.NETWORK_LOADING);
            return convertView;
        }

        final ModelUser user = (ModelUser) getItem(position);
        /**
         * 跳转到关注页面
         */
        holder.ll_mycenter_home_following
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context,
                                ActivityFollowUser.class);
                        intent.putExtra("type", "following");
                        intent.putExtra("uid", user.getUid());
                        context.startActivity(intent);

                    }
                });

        /**
         * 跳转到关注页面
         */
        holder.ll_mycenter_home_follow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,
                        ActivityFollowUser.class);
                intent.putExtra("type", "follow");
                intent.putExtra("uid", user.getUid());

                context.startActivity(intent);

            }
        });

        //关注、粉丝数
        if (user.getFollowedCount() != 0) {
            holder.tv_user_info_follow.setText(user.getFollowedCount() + "");
            holder.tv_user_info_follow.setEnabled(true);
        } else {
            holder.tv_tips_nofollower.setVisibility(View.VISIBLE);
            holder.tv_user_info_follow.setEnabled(false);
            holder.iv_followed_next.setVisibility(View.GONE);
            holder.ll_mycenter_home_follow.setClickable(false);
        }
        if (user.getFollowersCount() != 0) {
            holder.tv_user_info_following.setEnabled(true);
            holder.tv_user_info_following.setText(user.getFollowersCount() + "");
        } else {
            holder.tv_user_info_following.setEnabled(false);
            holder.tv_tips_nofollow.setVisibility(View.VISIBLE);
            holder.iv_following_next.setVisibility(View.GONE);
            holder.ll_mycenter_home_following.setClickable(false);
        }

        //地区
        String location = user.getLocation();
        if (location == null || location.isEmpty() || location.equals("null"))
            location = "来自星星的你";
        holder.tv_user_info_from.setText(location);
        String intro = user.getIntro();
        if (intro == null || intro.isEmpty() || intro.equals("null") || intro.equals("暂无简介")) {
            holder.tv_user_info_intro.setText("这家伙很懒，什么也没留下");
//            if (user.getUid() == Thinksns.getMy().getUid()) {
//                holder.tv_user_info_intro.setText("暂无简介");
//            } else {
                holder.tv_user_info_intro.setText("这家伙很懒，什么也没留下");
//            }
        } else {
            holder.tv_user_info_intro.setText(intro);
        }

        if (user.getUid() == Thinksns.getMy().getUid()) {
            holder.tv_change_user_info.setText("编辑资料");
        }
        //进入用户基本信息页
        holder.rl_mycenter_home_userinfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user.getUid() == Thinksns.getMy().getUid()) {
                    Intent intent = new Intent(context,
                            ActivityChangeUserInfo.class);
                    context.startActivity(intent);
                } else {
                    //普通用户基本信息页
                    Intent intent = new Intent(context, ActivityOtherUserBaseInfo.class);
                    intent.putExtra("uname", user.getUserName());
                    intent.putExtra("uface", user.getUserface());
                    intent.putExtra("city", user.getLocation());
                    intent.putExtra("intro", user.getIntro());
                    intent.putExtra("score", user.getUserCredit().getScore_value() + "");
                    intent.putExtra("level", user.getUserLevel()
                            .getLevel() + "");
                    // 直接传有问题，暂时使用上面方法替代
                     intent.putExtra("user", user);
                    context.startActivity(intent);

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
                    Intent intent = new Intent(context,
                            ActivityMedalPavilion.class);
                    intent.putExtra("uid", user.getUid());
                    context.startActivity(intent);
                }
            });
            try {
                if (holder.ll_honner!=null&&holder.ll_honner.getChildCount()!=0){
                    holder.ll_honner.removeAllViews();
                }

                if (user.getMedals() == null) {
                    holder.ll_honner_info.setVisibility(View.GONE);
                } else {
                    final JSONArray honner = new JSONArray(user.getMedals());
                    if (honner.length() == 0) {
                        holder.ll_honner_info.setVisibility(View.GONE);
                    } else {
                        holder.ll_honner_info.setVisibility(View.VISIBLE);
                        int size = UnitSociax.dip2px(context, 25);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                        lp.setMargins(0, 0, UnitSociax.dip2px(context, 10), 0);
                        for (int i = 0; i < honner.length(); i++) {
                            ImageView imageView = new ImageView(context);

                            imageView.setLayoutParams(lp);
                            holder.ll_honner.addView(imageView);
                            Glide.with(context).load(honner.getString(i))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(new GlideCircleTransform(context))
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
            if (holder.ll_following_list!=null&&holder.ll_following_list.getChildCount()!=0){
                holder.ll_following_list.removeAllViews();
            }

            if (follower.length() == 0) {
                holder.tv_tips_nofollower.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < follower.length(); i++) {
                    try {
                        int width = UnitSociax.getWindowWidth(context) - UnitSociax.dip2px(context, 20);
                        //一行排列5个
                        LinearLayout linearLayout = createUserList(follower.getJSONObject(i), width / 5);
                        holder.ll_following_list.addView(linearLayout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (holder.ll_followed_list!=null&&holder.ll_followed_list.getChildCount()!=0){
                holder.ll_followed_list.removeAllViews();
            }

            if (following.length() == 0) {
                holder.tv_tips_nofollow.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < following.length(); i++) {
                    try {
                        int width = UnitSociax.getWindowWidth(context) - UnitSociax.dip2px(context, 20);
                        //一行排列5个
                        LinearLayout linearLayout = createUserList(following.getJSONObject(i), width / 5);
                        holder.ll_followed_list.addView(linearLayout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return convertView;
    }

    private LinearLayout createUserList(final JSONObject jsonObject, int width) {
        //生成容器
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(params);
        //生成头像
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.default_user);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(UnitSociax.dip2px(context, 40),
                UnitSociax.dip2px(context, 40));
        imageParams.gravity = Gravity.CENTER;
        imageParams.bottomMargin = UnitSociax.dip2px(context, 3);
//        imageView.setLayoutParams(imageParams);
        linearLayout.addView(imageView, imageParams);
        //生成用户名
        TextView textName = new TextView(context);
        textName.setTextSize(10);
        textName.setTextColor(context.getResources().getColor(R.color.gray));
        textName.setGravity(Gravity.CENTER);
        textName.setSingleLine();
        textName.setEllipsize(TextUtils.TruncateAt.END);
        linearLayout.addView(textName);
        try {
            textName.setText(jsonObject.getString("uname") + "");
            Glide.with(context)
                    .load(jsonObject.getString("avatar"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(
                            new GlideCircleTransform(context))
                    .crossFade().into(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,
                            ActivityUserInfo_2.class);
                    try {
                        intent.putExtra("uid", jsonObject.getInt("uid"));
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return linearLayout;
    }

    @Override
    public int getMaxid() {
        return 0;
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        ListData<SociaxItem> listuser = new ListData<SociaxItem>();
        try {
            new Api.Users().show(user, httpListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listuser;
    }

    @Override
    public ListData<SociaxItem> refreshHeader(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        ListData<SociaxItem> listuser = new ListData<SociaxItem>();
        try {
            new Api.Users().show(user, httpListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listuser;
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return null;
    }

    @Override
    public void addHeader(ListData<SociaxItem> list) {
        adapterState = AdapterSociaxList.STATE_IDLE;
        if (list != null && list.size() != 0) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public void addFooter(ListData<SociaxItem> list) {
        if (list != null && list.size() != 0) {
            this.user = (ModelUser) list.get(0);
            notifyDataSetChanged();
        }
    }

    @Override
    public void changeListData(ListData<SociaxItem> list) {
        if (list != null && list.size() != 0) {
            this.user = (ModelUser) list.get(0);
            notifyDataSetChanged();
        }
    }
}
