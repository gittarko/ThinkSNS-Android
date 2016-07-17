package com.thinksns.sociax.t4.android.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Listener.onWebViewLoadListener;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.function.FunctionChangeWeibaFollow;
import com.thinksns.sociax.t4.android.interfaces.OnPopupWindowClickListener;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowCommon;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.weiba.ActivityPostCommon;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weiba.ActivityPostList;
import com.thinksns.sociax.t4.android.weiba.ActivityWeibaDetail;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.GlideRoundTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelDiggUser;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelWeiba;
import com.thinksns.sociax.t4.unit.DynamicInflateForWeiba;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;
import com.thinksns.tschat.widget.UIImageLoader;

import org.apache.http.cookie.SM;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 类说明： 帖子列表头部内容拼接类
 *
 * @author wz
 * @version 1.0
 * @date 2014-12-23
 */
public class AppendPost extends AppendSociax {
    /**
     * H5字体样式
     *
     */
    public static final String WEB_STYLE = "<style>" +
            "p {font-size:15px;}" +
            "img { width:100%; height:auto}" +
            "img.emot {width:20px; height:20px}" +
            "</style>";
    private Thinksns application;

    /**
     * 帖子内容拼接，用于帖子详情
     *
     * @param context
     *
     */
    public AppendPost(Context context) {
        super(context);
        this.context = context;
        application = (Thinksns) context.getApplicationContext();
    }

    /**
     * 帖子内容拼接，用于帖子列表适配
     *
     * @param context
     * @param adapter
     */
    public AppendPost(Context context, AdapterSociaxList adapter) {
        super(context);
        this.context = context;
        this.adapter = adapter;
        application = (Thinksns) context.getApplicationContext();
    }


    /**
     * 初始化holder
     *
     * @return
     */
    public HolderSociax initHolder(View convertView) {
        // 下面的内容请不要随便删除
        HolderSociax holder = new HolderSociax();
        //帖子发表时间
        holder.tv_post_ctime = (TextView) convertView.findViewById(R.id.tv_post_time);
        //帖子内容
        holder.tv_post_info = (TextView) convertView.findViewById(R.id.tv_post_des);
        //帖子标题
        holder.tv_post_title = (TextView) convertView.findViewById(R.id.tv_post_title);
        //发帖人头像
        holder.img_posts_user = (ImageView) convertView
                .findViewById(R.id.img_post_user_header);
        //发帖人姓名
        holder.tv_post_uname = (TextView) convertView
                .findViewById(R.id.tv_post_user_name);
        holder.tv_post_comment = (TextView) convertView
                .findViewById(R.id.tv_post_comment);
        holder.tv_post_read = (TextView) convertView
                .findViewById(R.id.tv_post_read);
        holder.ll_user_group = (LinearLayout) convertView
                .findViewById(R.id.ll_user_group);

        //帖子分类
        holder.tv_part_name = (TextView) convertView
                .findViewById(R.id.tv_part_name);
        holder.tv_tag = (TextView) convertView
                .findViewById(R.id.tv_tag);

        //帖子详情，微吧信息
        holder.ll_weiba_info = (LinearLayout) convertView.findViewById(R.id.ll_weiba_info);
        holder.img_weiba_icon1 = (ImageView) convertView.findViewById(R.id.img_weiba_logo);
        holder.tv_weiba_des = (TextView) convertView.findViewById(R.id.tv_weiba_des);
        holder.tv_weiba_name = (TextView) convertView.findViewById(R.id.tv_weiba_name);
        holder.tv_weiba_follow = (TextView) convertView.findViewById(R.id.tv_weiba_follow);
        //帖子H5
        holder.wb_content = (WebView) convertView.findViewById(R.id.wv_content);
        holder.tl_imgs = (TableLayout) convertView.findViewById(R.id.tl_imgs);
        //点赞视图
        holder.ll_digg = (LinearLayout) convertView.findViewById(R.id.ll_digg);
        //点赞用户头像列表
        holder.ll_digg_list = (LinearLayout)convertView.findViewById(R.id.ll_digglist);
        //帖子列表预览图
        holder.stub_image_group = (ViewStub) convertView.findViewById(R.id.stub_image_group);
        holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);

        return holder;

    }

    /**
     * 拼接帖子列表内容
     *
     * @param holder
     * @param post
     */
    public void appendPostListData(HolderSociax holder, final ModelPost post) {

        if(holder.img_posts_user != null) {
            UIImageLoader.getInstance(context).displayImage(post.getUface(), holder.img_posts_user);
            holder.img_posts_user.setTag(R.id.tag_user, post.getUser());
            holder.img_posts_user.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityUserInfo_2.class);
                    intent.putExtra("uid", ((ModelUser) v.getTag(R.id.tag_user)).getUid());
                    context.startActivity(intent);
                }
            });
        }

        if (post.getUser() != null)
            holder.tv_post_uname.setText(post.getUser().getUserName());
        //帖子时间
        holder.tv_date.setText(TimeHelper.friendlyTime(post.getPost_time()));

        /***************** 帖子内容 *******************/
        holder.tv_post_title.setText(post.getTitle());
        // 文字内容
        if (TextUtils.isEmpty(post.getContent())) {
            holder.tv_post_info.setVisibility(View.GONE);
        } else {
            com.thinksns.sociax.t4.unit.UnitSociax.showContentLinkViewAndLinkMovement(post.getContent(), holder.tv_post_info);
            holder.tv_post_info.setVisibility(View.VISIBLE);
        }

        //显示帖子图片,最多显示9张
        if (post.getImg() != null && post.getImg().length() > 0) {
            int length = post.getImg().length();
            try {
                ArrayList<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
                JSONArray imgArray = post.getImg();
                //只选取其中的9张图片
                for (int i = 0, j = length; i < j; i++) {
                    JSONObject json = imgArray.getJSONObject(i);
                    //获取小图
                    String small = "", big = "";
                    if(json.has("small") || json.has("big")) {
                        ModelPhoto p = new ModelPhoto();
                        if(json.has("small"))
                            small = json.getString("small");
                        if(json.has("big"))
                            big = json.getString("big");
                        if(!TextUtils.isEmpty(small)
                                && small.startsWith("http://")) {
                            if (small.contains("emotion/images/location/")) {
                                small = null;
                            }
                        }
                        if(!TextUtils.isEmpty(big)
                                && big.startsWith("http://")) {
                            if(big.contains("emotion/images/location")) {
                                big = null;
                            }
                        }

                        if(!TextUtils.isEmpty(small) && !TextUtils.isEmpty(big)) {
                            p.setOriUrl(big);
                            p.setUrl(small);
                            p.setMiddleUrl(small);
                        }else if(!TextUtils.isEmpty(small)) {
                            p.setUrl(small);
                            p.setOriUrl(small);
                            p.setMiddleUrl(small);
                        }else if(!TextUtils.isEmpty(big)) {
                            p.setOriUrl(big);
                            p.setOriUrl(big);
                            p.setMiddleUrl(big);
                        }
                        if(p.getUrl() != null)
                            photoList.add(p);
                    }

                }

                if(photoList.size() > 0) {
                    DynamicInflateForWeiba.addImageTable(context, holder.stub_image_group, photoList, 1080);
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            holder.stub_image_group.setVisibility(View.GONE);
        }

        // 赞
        if (holder.ll_digg != null) {// 有些拼接不需要用到赞列表，findviewbyid的时候会返回null,
            if (post.getDiggInfoList() != null
                    && post.getDiggInfoList().size() > 0) {
                ListData<ModelDiggUser> diggList = post.getDiggInfoList();
                appendDiggUser(holder.ll_digg, diggList);
            }
        }
        if (holder.tv_post_comment != null) {
            holder.tv_post_comment.setText(post.getReply_count() + "");
        }
        if (holder.tv_post_read != null) {
            holder.tv_post_read.setText(post.getRead_count() + "");
        }
    }



    /**
     * 赞的显示
     *
     * @param ll_digg
     *            赞layout
     * @param diggList
     *            赞列表
     */
	public static void appendDiggUser(LinearLayout ll_digg, ListData<ModelDiggUser> diggList) {
		// 头像区
		LinearLayout ll_digglist = (LinearLayout) ll_digg.findViewById(R.id.ll_digglist);
		ll_digglist.removeAllViews();
        //设置最多显示头像个数
        int num = (UnitSociax.getWindowWidth(ll_digg.getContext()) - UnitSociax.dip2px(ll_digg.getContext(), 40)) / UnitSociax.dip2px(ll_digg.getContext(), 40);
		if (diggList != null) {
			for (int i = 0; i < diggList.size() && i < num; i++) {
				ImageView imgi = new ImageView(ll_digg.getContext());
				Glide.with(ll_digg.getContext())
						.load(((ModelDiggUser) diggList.get(i)).getAvatar())
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.transform(new GlideCircleTransform(ll_digg.getContext()))
						.crossFade().into(imgi);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        UnitSociax.dip2px(ll_digg.getContext(), 30), UnitSociax.dip2px(ll_digg.getContext(), 30));
				lp.setMargins(0, 0, UnitSociax.dip2px(ll_digg.getContext(), 10), 0);
				imgi.setTag(R.id.tag_user,
						((ModelDiggUser) diggList.get(i)).getUid());
				imgi.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(),
								ActivityUserInfo_2.class);
						intent.putExtra("uid", Integer.parseInt(v.getTag(
								R.id.tag_user).toString()));
                       v.getContext().startActivity(intent);
					}
				});
				imgi.setLayoutParams(lp);
				ll_digglist.addView(imgi);
			}
		}
	}

    /**
     * 微吧详情,帖子列表头部拼接
     *
     * @param header
     * @param weibaDetil
     */
//    public void appendWeibaInfoHeader(View header, JSONObject weibaDetil) {
//        if (header != null && weibaDetil != null) {
//            HolderSociax holder = new HolderSociax();
//            try {
//                holder.ll_weiba_isfollow_content = (LinearLayout) header
//                        .findViewById(R.id.ll_isfollow_content);
//                holder.ll_weiba_notfollow_content = (LinearLayout) header
//                        .findViewById(R.id.ll_not_follow_content);
//                if (weibaDetil.getInt("follow") == 1) {
//                    // 已经关注
//                    holder.img_weiba_bg = (ImageView) header
//                            .findViewById(R.id.img_weiba_bg);
//                    holder.tv_weiba_title = (TextView) header
//                            .findViewById(R.id.tv_weiba_title);
//                    holder.tv_weiba_des = (TextView) header
//                            .findViewById(R.id.tv_weiba_des);
//                    holder.tv_weiba_top1 = (TextView) header
//                            .findViewById(R.id.tv_weiba_top1);
//                    holder.tv_weiba_top2 = (TextView) header
//                            .findViewById(R.id.tv_weiba_top2);
//                    holder.tv_weiba_digest = (TextView) header
//                            .findViewById(R.id.tv_weiba_digest);
//                    holder.tv_weiba_isfollow = (TextView) header
//                            .findViewById(R.id.tv_weiba_isfollow);
//                    holder.ll_weiba_top = (LinearLayout) header
//                            .findViewById(R.id.ll_weiba_top);
//                    holder.ll_weiba_digest = (LinearLayout) header
//                            .findViewById(R.id.ll_weiba_digest);
//                    // 背景和头像
//                    if (weibaDetil.has("weiba_info")) {
//                        final ModelWeiba md = new ModelWeiba(
//                                weibaDetil.getJSONObject("weiba_info"));
//                        holder.tv_weiba_title.setText(md.getWeiba_name());
//                        holder.tv_weiba_des.setText("成员 "
//                                + md.getFollower_count() + "  帖子 "
//                                + md.getThread_count());
//                        holder.tv_weiba_isfollow
//                                .setOnClickListener(new OnClickListener() {
//
//                                    @Override
//                                    public void onClick(View v) {
//                                        PopupWindowCommon pup = new PopupWindowCommon(
//                                                context, adapter.getPullRefreshView().getRefreshableView(),
//                                                "确定要取消关注该圈子吗");
//                                        pup.setOnPopupWindowClickListener(new OnPopupWindowClickListener() {
//
//                                            @Override
//                                            public void secondButtonClick() {
//                                            }
//
//                                            @Override
//                                            public void firstButtonClick() {
//                                                FunctionChangeWeibaFollow fc = new FunctionChangeWeibaFollow(
//                                                        context, true, md
//                                                        .getWeiba_id(),
//                                                        adapter);
//                                                fc.changeFollow();
//                                            }
//                                        });
//                                    }
//                                });
//                    }
//
//                    // 精华帖
//                    if (weibaDetil.has("weiba_digest")
//                            && weibaDetil.getJSONArray("weiba_digest")
//                            .getInt(0) > 0) {
//                        holder.ll_weiba_digest.setVisibility(View.VISIBLE);
//                        holder.tv_weiba_digest.setText("精华区 ("
//                                + weibaDetil.getJSONArray("weiba_digest")
//                                .getInt(0) + ") ");
//                        try {
//                            holder.tv_weiba_digest.setTag(weibaDetil
//                                    .getJSONObject("weiba_info").getInt(
//                                            "weiba_id"));
//                            holder.tv_weiba_digest
//                                    .setOnClickListener(new OnClickListener() {
//
//                                        @Override
//                                        public void onClick(View v) {
//                                            int weiba_id = (Integer) v.getTag();
//                                            Intent intent = new Intent(context,
//                                                    ActivityPostList.class);
//                                            intent.putExtra("weiba_id",
//                                                    weiba_id);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            intent.putExtra("type",
//                                                    StaticInApp.POST_DIGEST);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        holder.ll_weiba_digest.setVisibility(View.GONE);
//                    }
//
//                    // 置顶帖
//                    if (weibaDetil.has("weiba_top")) {
//                        int topsize = weibaDetil.getJSONArray("weiba_top")
//                                .length();
//                        if (topsize > 0) {
//                            holder.ll_weiba_top.setVisibility(View.VISIBLE);
//                            if (topsize == 1) {
//                                holder.tv_weiba_top1
//                                        .setVisibility(View.VISIBLE);
//                                holder.tv_weiba_top2.setVisibility(View.GONE);
//                                ModelPost md1 = new ModelPost(weibaDetil
//                                        .getJSONArray("weiba_top")
//                                        .getJSONObject(0));
//                                holder.tv_weiba_top1.setText(md1.getTitle());
//
//                                holder.tv_weiba_top1.setTag(R.id.tag_post, md1);
//                                holder.tv_weiba_top1
//                                        .setOnClickListener(new OnClickListener() {
//
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(
//                                                        context,
//                                                        ActivityPostDetail.class);
//                                                Bundle data = new Bundle();
//                                                ModelPost md = (ModelPost) v
//                                                        .getTag(R.id.tag_post);
//
//                                                data.putInt("post_id",
//                                                        md.getPost_id());
//                                                intent.putExtras(data);
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                context.startActivity(intent);
//                                            }
//                                        });
//
//                            } else {
//                                holder.tv_weiba_top1
//                                        .setVisibility(View.VISIBLE);
//                                holder.tv_weiba_top2
//                                        .setVisibility(View.VISIBLE);
//                                ModelPost md1 = new ModelPost(weibaDetil
//                                        .getJSONArray("weiba_top")
//                                        .getJSONObject(0));
//                                ModelPost md2 = new ModelPost(weibaDetil
//                                        .getJSONArray("weiba_top")
//                                        .getJSONObject(1));
//
//                                holder.tv_weiba_top1.setText(md1.getTitle());
//                                holder.tv_weiba_top2.setText(md2.getTitle());
//
//                                holder.tv_weiba_top1.setTag(R.id.tag_post, md1);
//                                holder.tv_weiba_top1
//                                        .setOnClickListener(new OnClickListener() {
//
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(
//                                                        context,
//                                                        ActivityPostDetail.class);
//                                                Bundle data = new Bundle();
//                                                ModelPost md = (ModelPost) v
//                                                        .getTag(R.id.tag_post);
//                                                data.putInt("post_id",
//                                                        md.getPost_id());
//                                                intent.putExtras(data);
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                context.startActivity(intent);
//                                            }
//                                        });
//
//                                holder.tv_weiba_top2.setTag(R.id.tag_post, md2);
//                                holder.tv_weiba_top2
//                                        .setOnClickListener(new OnClickListener() {
//
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(
//                                                        context,
//                                                        ActivityPostDetail.class);
//                                                Bundle data = new Bundle();
//                                                ModelPost md = (ModelPost) v
//                                                        .getTag(R.id.tag_post);
//
//                                                data.putInt("post_id",
//                                                        md.getPost_id());
//                                                intent.putExtras(data);
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                context.startActivity(intent);
//                                            }
//                                        });
//                            }
//                        } else {
//                            holder.ll_weiba_top.setVisibility(View.GONE);
//                        }
//                        holder.ll_weiba_isfollow_content
//                                .setVisibility(View.VISIBLE);
//                        holder.ll_weiba_notfollow_content
//                                .setVisibility(View.GONE);
//                    }
//                } else {// 还没有关注
//                    holder.tv_weiba_title = (TextView) header
//                            .findViewById(R.id.tv_weiba_title_2);
//                    holder.tv_weiba_des = (TextView) header
//                            .findViewById(R.id.tv_weiba_des_2);
//                    holder.img_weiba_bg = (ImageView) header
//                            .findViewById(R.id.img_weiba_bg_2);
//                    holder.tv_weiba_intro = (TextView) header
//                            .findViewById(R.id.tv_weiba_intro);
//
//                    // 内容
//                    if (weibaDetil.has("weiba_info")) {
//                        ModelWeiba md = new ModelWeiba(
//                                weibaDetil.getJSONObject("weiba_info"));
//                        holder.tv_weiba_title.setText(md.getWeiba_name());
//                        holder.tv_weiba_des.setText("关注 "
//                                + md.getFollower_count() + "  帖子 "
//                                + md.getThread_count());
////						holder.img_weiba_bg.setImageUrl(md.getLogo());
//                        // caoligai 修改，Model 变化，头像字段也变化
////						holder.img_weiba_bg.setImageUrl(md.getAvatar_middle());
//
//                        ImageLoader.getInstance().displayImage(md.getAvatar_middle(), holder.img_weiba_bg, Thinksns.getOptions());
//
//                        holder.tv_weiba_intro.setText(md.getIntro());
//                    }
//
//                    holder.ll_weiba_isfollow_content.setVisibility(View.GONE);
//                    holder.ll_weiba_notfollow_content
//                            .setVisibility(View.VISIBLE);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    /**
     * 拼接帖子详情页面列表头部内容
     *
     * @param holder
     * @param post
     */
    public void appendPostHeaderData(HolderSociax holder, final ModelPost post, onWebViewLoadListener listener) {
        if (post == null || holder == null) {
            return;
        }

        /************** 头部内容 *****************/
        if (holder.img_posts_user != null
                && post.getUser() != null) {
            Glide.with(holder.img_posts_user.getContext()).load(post.getUface())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideCircleTransform(holder.img_posts_user.getContext()))
                    .crossFade()
                    .into(holder.img_posts_user);

            holder.img_posts_user.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),
                            ActivityUserInfo_2.class);
                    intent.putExtra("uid", post.getUser().getUid());
                    v.getContext().startActivity(intent);
                }
            });
        }

        final ModelWeiba md = post.getWeiba();
        if (md != null) {
            holder.tv_tag.setText(md.getWeiba_name());
            holder.tv_weiba_name.setText(md.getWeiba_name());
            holder.tv_weiba_des.setText("成员 " + md.getFollower_count() + "  " + "帖子 " + md.getThread_count());
            holder.tv_weiba_follow.setText(md.isFollow() ? "已关注" : "未关注");
            //设置微吧LOGO
            Glide.with(holder.img_weiba_icon1.getContext()).load(md.getAvatar_middle())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(holder.img_weiba_icon1.getContext()))
                    .crossFade()
                    .into(holder.img_weiba_icon1);

            holder.ll_weiba_info.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(!md.isFollow()) {
                        //没有关注进入微吧详情
                        Intent intent = new Intent(v.getContext(), ActivityWeibaDetail.class);
                        intent.putExtra("weiba", (Serializable)md);
                        v.getContext().startActivity(intent);
                    }
                }
            });

        } else {
            holder.tv_tag.setText("来自微吧");
        }

        if (holder.tv_post_ctime != null) {
            holder.tv_post_ctime.setText(TimeHelper.friendlyTime(post
                    .getPost_time()));
        }

        holder.tv_post_uname.setText(post.getUser().getUserName());

        // 用户认证标签
        if (post.getUser().getUserApprove().getApprove() != null) {
            List<String> ugroup = post.getUser().getUserApprove().getApprove();
            holder.ll_user_group.removeAllViews();
            for (int i = 0; i < ugroup.size(); i++) {
                try {
                    ImageView smView = new ImageView(holder.ll_user_group.getContext());
                    UIImageLoader.getInstance(holder.ll_user_group.getContext()).displayImage(ugroup.get(i), smView);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            UnitSociax.dip2px(holder.ll_user_group.getContext(), context.getResources().getDimension(R.dimen.weiba_detail_usergroup)),
                            UnitSociax.dip2px(holder.ll_user_group.getContext(), context.getResources().getDimension(R.dimen.weiba_detail_usergroup)));
                    //设置右间距5dp
                    lp.setMargins(0, 0, UnitSociax.dip2px(holder.ll_user_group.getContext(), 5), 0);
                    smView.setLayoutParams(lp);
                    holder.ll_user_group.addView(smView);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            holder.ll_user_group.setVisibility(View.VISIBLE);
        } else {
            holder.ll_user_group.setVisibility(View.GONE);
        }

        /***************** 帖子内容 *******************/
        holder.tv_post_title.setText(post.getTitle());
        String postContent = post.getContent();
        if(!TextUtils.isEmpty(postContent)
                && !post.isFromWeiba()) {
            String h5Content = WEB_STYLE + post.getContent();
            uint.appendWebViewContent(holder.wb_content, h5Content, listener);
            holder.wb_content.setVisibility(View.VISIBLE);
        }else {
            holder.wb_content.setVisibility(View.GONE);
            if(listener != null) {
                //不需要加载网页的时候直接返回结束
                listener.onPageFinished();
            }
        }

        if (holder.ll_digg != null){
            ListData<ModelDiggUser> diggList = post.getDiggInfoList();
            if(diggList != null
                    && diggList.size() > 0) {
                appendDiggUser(holder.ll_digg, diggList);
                holder.ll_digg.setVisibility(View.VISIBLE);
            }else {
                //没有点赞则不显示
                holder.ll_digg.setVisibility(View.GONE);
            }
            // 数目
            TextView tv_digg_num = (TextView) holder.ll_digg.findViewById(R.id.tv_dig_num);
            tv_digg_num.setText(String.valueOf(post.getPraise()));
            //点击点赞数目进入点赞列表
            tv_digg_num.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivityPostCommon.class);
                    intent.putExtra("post_id", post.getPost_id());
                    v.getContext().startActivity(intent);
                }
            });
        }

        /******************* 尾部内容 *****************/
        if (holder.tv_post_comment != null) {
            holder.tv_post_comment.setText(post.getReply_count() + "");
        }

        if (holder.tv_post_read != null) {
            holder.tv_post_read.setText(post.getRead_count() + "");
        }
    }
}
