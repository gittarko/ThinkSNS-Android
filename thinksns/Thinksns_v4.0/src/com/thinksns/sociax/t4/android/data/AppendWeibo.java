package com.thinksns.sociax.t4.android.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;import com.thinksns.sociax.t4.android.channel.ActivityChannelWeibo;
import com.thinksns.sociax.t4.android.fragment.FragmentWeibo;

import com.thinksns.sociax.t4.android.function.FunctionChangeSociaxItemStatus;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.android.img.UIImageLoader;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.android.topic.ActivityTopicWeibo;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.android.widget.roundimageview.RoundedImageView;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelImageAttach;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.model.ModelVideo;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.DynamicInflateForWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.exception.TimeIsOutFriendly;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;
import com.thinksns.sociax.unit.TypeNameUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明： 微博内容映射
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-29
 */
public class AppendWeibo extends AppendSociax {
    private static final String TAG = "TSTAG_AppendWeibo";
    private WeiboListViewClickListener weiboListViewClickListener;

    private Thinksns application;
    /**
     * 判断是否已经成功初始化holder，如果未初始化则不无法调用appendData等方法映射数据
     */
    boolean isInitHolderSuccess = false;

    /**
     * 默认微博背景图六张，推荐微博里面当没有背景图的时候就需要使用这里的图片
     */
    private int[] bg_defaults = {R.drawable.bg_weibodefault_1,
            R.drawable.bg_weibodefault_2, R.drawable.bg_weibodefault_2,
            R.drawable.bg_weibodefault_4, R.drawable.bg_weibodefault_4,
            R.drawable.bg_weibodefault_6};

    /**
     * 微博内容映射组建
     */
    public AppendWeibo(Context context) {
        super(context);
        application = (Thinksns) context.getApplicationContext();
    }

    /**
     * 微博内容拼接，用于帖子列表适配
     */
    public AppendWeibo(Context context, AdapterSociaxList adapter) {
        super(context);
        this.context = context;
        this.adapter = adapter;
        application = (Thinksns) context.getApplicationContext();
    }

    public AppendWeibo(Context context, ListBaseAdapter adapter, WeiboListViewClickListener listener) {
        super(context);
        this.context = context;
        this.adapterList = adapter;
        application = (Thinksns)context.getApplicationContext();
        this.weiboListViewClickListener = listener;
    }

    /**
     * 初始化holder
     */
    public HolderSociax initHolder(View convertView) {
        // 下面的内容请不要随便删除
        HolderSociax holder = new HolderSociax();
        try {
            // 头部用户信息
            holder.stub_uname_adn = (ViewStub) convertView
                    .findViewById(R.id.stub_uname_adn);
            holder.ll_user_info = (LinearLayout) convertView
                    .findViewById(R.id.ll_user_info);
            holder.tv_weibo_user_name = (TextView) convertView
                    .findViewById(R.id.tv_weibo_user_name);
            holder.iv_weibo_user_head = (RoundedImageView) convertView
                    .findViewById(R.id.iv_weibo_user_head);

            holder.tv_weibo_ctime = (TextView) convertView
                    .findViewById(R.id.tv_weibo_ctime);
            holder.tv_weibo_from = (TextView) convertView
                    .findViewById(R.id.tv_weibo_from);
            //关注按钮
            holder.stub_add_follow = (ViewStub) convertView
                    .findViewById(R.id.stub_add_follow);
            // 微博内容textview
            holder.tv_weibo_content = (TextView) convertView
                    .findViewById(R.id.tv_weibo_content);
            holder.iv_weibo_image = (ImageView) convertView
                    .findViewById(R.id.iv_weibo_image);
            holder.gv_weibo = (GridView) convertView
                    .findViewById(R.id.gv_weibo);
            // 另外一种微博没有背景图的时候用到这个变量
            holder.stub_image = (ViewStub) convertView
                    .findViewById(R.id.stub_image);
            holder.stub_image_group = (ViewStub) convertView
                    .findViewById(R.id.stub_image_group);
            // 微博文件和图片
            holder.ll_other_files_image = (LinearLayout) convertView
                    .findViewById(R.id.ll_image);
            holder.stub_file = (ViewStub) convertView
                    .findViewById(R.id.stub_file);
            // 赞的图标和赞的数目
            holder.iv_dig = (ImageView) convertView.findViewById(R.id.iv_dig);
            holder.tv_dig_num = (TextView) convertView
                    .findViewById(R.id.tv_dig_num);
            // 评论数目和点击输入评论
            holder.tv_comment_num = (TextView) convertView
                    .findViewById(R.id.tv_comment_num);
            // 视频
            holder.ll_media = (LinearLayout) convertView
                    .findViewById(R.id.ll_media);
            holder.stub_media = (ViewStub) convertView
                    .findViewById(R.id.stub_media);
            // 整个中间内容部分
//			holder.rl_content_layout = (LinearLayout) convertView
//					.findViewById(R.id.rl_content_layout);

            holder.rl_manage = (RelativeLayout) convertView
                    .findViewById(R.id.rl_manage);
            // 评论信息
            holder.ll_comment_info = (LinearLayout) convertView
                    .findViewById(R.id.ll_comment_info);
            holder.stub_digg = (ViewStub) convertView.
                    findViewById(R.id.stub_digg);
            holder.stub_comment = (ViewStub) convertView.
                    findViewById(R.id.stub_comment);
            // 赞信息
            holder.ll_digg_info = (LinearLayout) convertView
                    .findViewById(R.id.ll_digg_info);
            // 转发那个的小图标
            holder.img_more = (ImageView) convertView
                    .findViewById(R.id.img_more);
            // caoligai 修改
            holder.rl_more = (RelativeLayout) convertView
                    .findViewById(R.id.rl_more);
            // 微博隐藏的评论列表
            holder.ll_hide_comment = (LinearLayout) convertView
                    .findViewById(R.id.ll_comment); // 评论列表布局
            // ----------------------------评论列表--------------------------------------------
            holder.ll_hide_comment_list = (LinearLayout) convertView
                    .findViewById(R.id.ll_comment_list);// 隐藏的列表
            holder.ll_praise_list = (TextView) convertView
                    .findViewById(R.id.ll_praise_list); // 点赞的列表 qcj添加

            holder.tv_hide_comment_list = (TextView) convertView
                    .findViewById(R.id.tv_comment_list);

            holder.stub_transport_weibo = (ViewStub)convertView.findViewById(R.id.stub_transport);
            holder.stub_transport_weiba = (ViewStub)convertView.findViewById(R.id.stub_weiba_transport);

            // 下面的内容在没有背景图的列表中用到于根据type来自区分显示内容
            holder.tv_post_is_delete = (TextView) convertView
                    .findViewById(R.id.tv_post_is_delete);
            holder.ll_post_no_delete = (LinearLayout) convertView
                    .findViewById(R.id.ll_post_no_delete);
            holder.ll_from_weibo_content = (LinearLayout) convertView
                    .findViewById(R.id.ll_from_weibo_content);
            holder.ll_from_weiba_content = (LinearLayout) convertView
                    .findViewById(R.id.ll_from_weiba_content);
            holder.stub_weiba = (ViewStub) convertView
                    .findViewById(R.id.stub_weiba);
            holder.tv_post_title = (TextView) convertView
                    .findViewById(R.id.tv_post_title);
            holder.tv_post_content = (TextView) convertView
                    .findViewById(R.id.tv_post_content);
            holder.tv_post_from = (TextView) convertView
                    .findViewById(R.id.tv_post_from);
            holder.view_weibo_divide = convertView.findViewById(R.id.view_weibo_divide);
            holder.iv_weibo_comment_bg = (ImageView) convertView.findViewById(R.id.iv_weibo_comment_bg);
            holder.ll_weibo_main = (LinearLayout) convertView.findViewById(R.id.ll_weibo_main);
            holder.stub_address = (ViewStub) convertView.findViewById(R.id.stub_address);
            setInitHolderSuccess(true);
        } catch (Exception e) {
            Log.v(TAG, "initHolder Error" + e.getMessage());
        }
        return holder;

    }

    //点击微博正文进入微博详情
    private OnClickListener weiboOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_weibo_main:
                case R.id.tv_weibo_content:
                    jumpToWeiboDetail((Integer) v.getTag(R.id.tag_position));
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 跳转到微博详情
     * @param position
     */
    private void jumpToWeiboDetail(int position) {
        Intent intent = new Intent(context,
                ActivityWeiboDetail.class);
        Bundle data = new Bundle();
        if(adapter!= null)
            data.putSerializable("weibo", adapter.getItem(position));
        else if(adapterList != null) {
            data.putSerializable("weibo", adapterList.getItem(position));
        }

        data.putInt("position", position);
        intent.putExtras(data);
        ((Activity) context).startActivityForResult(intent, 100);
    }

    /**
     * 显示微博列表的数据 ,没有大的默认背景图，但是携带图片的会正常显示图片 显示转发微博
     */
    public void appendWeiboItemDataWithNoBackGround(final int position,
                                                    final HolderSociax holder, final ModelWeibo currentWeibo) {
        if(currentWeibo == null)
            return;
        final int weibo_uid = currentWeibo.getUid();
        if (currentWeibo.getUsApprove() != null
                && currentWeibo.getUsApprove().getApprove().size() > 0) {
            //只显示认证的第一个图标
            DynamicInflateForWeibo.addUserGroup(context, holder.stub_uname_adn, currentWeibo.getUsApprove().getApprove().get(0));
        } else {
            holder.stub_uname_adn.setVisibility(View.GONE);
        }

        UIImageLoader.getInstance(context).displayImage(currentWeibo.getUserface(),
                holder.iv_weibo_user_head);
        holder.iv_weibo_user_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context,
                        ActivityUserInfo_2.class);
                intent.putExtra("uid", weibo_uid);
                intent.putExtra("is_follow", currentWeibo.getFollowing() == 1);
                context.startActivity(intent);
            }
        });

        // 用户名
        holder.tv_weibo_user_name.setText(currentWeibo.getUsername());
        try {
            holder.tv_weibo_ctime.setText(TimeHelper.friendlyTime(currentWeibo
                    .getTimestamp()));
        } catch (TimeIsOutFriendly e) {
            holder.tv_weibo_ctime.setText(currentWeibo.getCtime());
        }

        // 来自哪里
        holder.tv_weibo_from.setText(currentWeibo.getFrom() == null ? "" : currentWeibo.getFrom());
        holder.tv_weibo_from.setTextColor(context.getResources().getColor(R.color.gray));

        //是否关注
        if (currentWeibo.getUid() != Thinksns.getMy().getUid()
                && currentWeibo.getFollowing() == 0) {
            // 不是本人且未关注
            DynamicInflateForWeibo.addFollow(context, holder.stub_add_follow,
                    new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            v.setClickable(false);
                            if(weiboListViewClickListener != null)
                                weiboListViewClickListener.onFollowWeibo(currentWeibo);
                        }
                    });
        } else {
            holder.stub_add_follow.setVisibility(View.GONE);
        }

        // 微吧
        if (currentWeibo.getType().equals("weiba_post")) {
            holder.ll_from_weibo_content.setVisibility(View.GONE);
            DynamicInflateForWeibo.addWeiba(context, holder.stub_weiba, currentWeibo);
        } else {
            // 微博
            holder.ll_from_weibo_content.setVisibility(View.VISIBLE);
            holder.stub_weiba.setVisibility(View.GONE);
            // 图片
            if (currentWeibo.hasImage()
                    && currentWeibo.getAttachImage() != null) {
                //计算图片列表的宽度
                int gridWidth = UnitSociax.getWindowWidth(context) - UnitSociax.dip2px(context, 62);
                DynamicInflateForWeibo.addImageGroup(context, holder.stub_image_group,
                        currentWeibo.getAttachImage(), gridWidth);
            } else {
                holder.stub_image.setVisibility(View.GONE);
                holder.stub_image_group.setVisibility(View.GONE);
            }

            // 内容
            if(!TextUtils.isEmpty(currentWeibo.getContent())) {
                uint.showContentLinkViewAndLinkMovement(currentWeibo.getContent(),
                        holder.tv_weibo_content);
                holder.tv_weibo_content.setVisibility(View.VISIBLE);
            }else {
                holder.tv_weibo_content.setVisibility(View.GONE);
            }

            // 视频
            ModelVideo myVideo = currentWeibo.getAttachVideo();
            if (currentWeibo.hasVideo() && myVideo != null) {
                int gridWidth = UnitSociax.getWindowWidth(context) - UnitSociax.dip2px(context, 62);
                int width = gridWidth / 16 * 9;
                holder.stub_media.getLayoutParams().height = width;
                DynamicInflateForWeibo.addMedia(context, holder.stub_media, myVideo, adapter, position);
            } else {
                holder.stub_media.setVisibility(View.GONE);
            }
            // 文件
            if (currentWeibo.hasFile()) {
                DynamicInflateForWeibo.addFile(context, holder.stub_file, currentWeibo.getAttachImage(), position);
            } else {
                holder.stub_file.setVisibility(View.GONE);
            }

            // 转发原微博
            if (currentWeibo.isNullForTranspond() || currentWeibo.getIsRepost() > 0) {
                if (currentWeibo.getType().equals("weiba_repost")) {
                    DynamicInflateForWeibo.addTransportWeiba(holder.stub_transport_weiba, currentWeibo);
                    holder.stub_transport_weibo.setVisibility(View.GONE);
                } else {
                    DynamicInflateForWeibo.addTransportWeibo(holder.stub_transport_weibo, currentWeibo);
                    holder.stub_transport_weiba.setVisibility(View.GONE);
                }
            } else {
                holder.stub_transport_weibo.setVisibility(View.GONE);
                holder.stub_transport_weiba.setVisibility(View.GONE);
            }
        }

        //设置微博地址
        if (currentWeibo.getAddress() != null && currentWeibo.getLongitude() != null
                && currentWeibo.getLatitude() != null) {
            DynamicInflateForWeibo.addAddress(context, holder.stub_address, currentWeibo);
        } else {
            holder.stub_address.setVisibility(View.GONE);
        }

        /***点赞、评论、更多****/
        initWeiboOperating(position, holder);

        // 是否已赞
        if (currentWeibo.isDigg()) {
            holder.iv_dig.setImageResource(R.drawable.ic_favor_press);
        } else {
            holder.iv_dig.setImageResource(R.drawable.ic_favor_normal);
        }

        // 赞数
        int digNum = currentWeibo.getDiggNum();
        holder.tv_dig_num.setText(digNum + "");

        // 评论数
        int commentcount = currentWeibo.getCommentCount();
        holder.tv_comment_num.setText(String.valueOf(commentcount));
        if (context instanceof ActivityHome
                || context instanceof ActivityChannelWeibo
                || context instanceof ActivityTopicWeibo) {

            // 点赞、评论列表
            boolean isShowMore = false;
            if (currentWeibo.getCommentList().size() > 0) {
                DynamicInflateForWeibo.addComment(context, holder.stub_comment, currentWeibo,
                        position, weiboListViewClickListener);
                isShowMore = true;
            } else {
                holder.stub_comment.setVisibility(View.GONE);
            }

            // 点赞人数和评论列表分隔线
            if (currentWeibo.getDiggNum() > 0 && currentWeibo.getCommentCount() > 0) {
                holder.view_weibo_divide.setVisibility(View.VISIBLE);
            } else {
                holder.view_weibo_divide.setVisibility(View.GONE);
            }

            if (digNum > 0) {
                DynamicInflateForWeibo.addDigg(holder.stub_digg, currentWeibo.getDiggUsers());
                isShowMore = true;
            } else {
                holder.stub_digg.setVisibility(View.GONE);
            }

            if (isShowMore) {
                holder.ll_hide_comment.setVisibility(View.VISIBLE);
                holder.iv_weibo_comment_bg.setVisibility(View.VISIBLE);
            } else {
                holder.ll_hide_comment.setVisibility(View.GONE);
                holder.iv_weibo_comment_bg.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 初始化微博相关操作
     */
    private void initWeiboOperating(final int position, final HolderSociax holder) {
        // 点赞
        holder.ll_digg_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                FunctionChangeSociaxItemStatus fc = new FunctionChangeSociaxItemStatus(
                        context);
                ModelWeibo clickitem = null;
                if(adapter != null) {
                    clickitem = (ModelWeibo)adapter.getItem(position);
                }else {
                    clickitem = (ModelWeibo)adapterList.getItem(position);
                }
                fc.setListenerSociax(new ListenerSociax() {
                    @Override
                    public void onTaskSuccess() {
                        ModelWeibo clickitem = null;
                        if(adapter != null) {
                            clickitem = (ModelWeibo)adapter.getItem(position);
                        }else {
                            clickitem = (ModelWeibo)adapterList.getItem(position);
                        }
                        boolean isDig = !clickitem.isDigg();
                        int curDigNum = clickitem.getDiggNum();
                        if (isDig) {
                            curDigNum += 1;
                            clickitem.getDiggUsers().add(0, Thinksns.getMy());
                        } else {
                            curDigNum -= 1;
                            clickitem.getDiggUsers().remove(Thinksns.getMy());
                        }
                        clickitem.setIsDigg(isDig);
                        clickitem.setDiggNum(curDigNum);
                        if(adapter != null)
                            adapter.notifyDataSetChanged();
                        else
                            adapterList.notifyDataSetChanged();

                        view.setEnabled(true);
                    }

                    @Override
                    public void onTaskError() {
                        view.setEnabled(true);
                    }

                    @Override
                    public void onTaskCancle() {
                        view.setEnabled(true);

                    }
                });

                fc.changeWeiboDigg(clickitem.getWeiboId(), clickitem.getIsDigg());
            }
        });

        // 评论框
        holder.ll_comment_info.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (context instanceof ActivityHome
                        || context instanceof ActivityChannelWeibo
                        || context instanceof ActivityTopicWeibo) {
                    if (adapter != null && adapter.getFragment() != null) {
                        ((FragmentWeibo) adapter.getFragment())
                                .clickComment(position);
                    }else if(adapterList != null) {
                        if(weiboListViewClickListener != null) {
                            //单独评论微博不需要传评论参数
                            weiboListViewClickListener.onCommentWeibo((ModelWeibo) adapterList.getItem(position), null);
                        }
                    }
                }  else {
                    jumpToWeiboDetail(position);
                }
            }
        });
        // 更多
        holder.img_more.setTag(R.id.tag_position, position);
        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (adapter != null && adapter.getFragment() != null) {
                    ((FragmentWeibo) adapter.getFragment()).clickMore(Integer
                            .parseInt(arg0.getTag(R.id.tag_position).toString()));
                }else if(adapterList != null) {
                    if(weiboListViewClickListener != null) {
                        weiboListViewClickListener.onWeiboMoreClick(position);
                    }
                }
                else {
                    if (context instanceof ActivityUserInfo_2) {
                        ((ActivityUserInfo_2) context).clickMore(position);
                    }
                }
            }
        });
    }

    /**
     * 拼接微吧转发微博
     *
     * @param linearLayout，用于放转发内容
     * @param currentWeibo 当前微博，本方法会调用当前微博所包含的转发微博并且映射到内容上
     */
    public void appendTranspondPost(LinearLayout layout, ModelWeibo currentWeibo, final int postion) {

        ModelWeibo transWeibo = currentWeibo.getSourceWeibo();
        layout.setTag(R.id.tag_weibo, currentWeibo.getSid());
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ActivityPostDetail.class);
                Bundle data = new Bundle();
                data.putInt("post_id", postion);
                intent.putExtras(data);
                context.startActivity(intent);
            }
        });
        if (transWeibo == null) {
            return;
        }

        layout.setBackgroundColor(context.getResources().getColor(R.color.transWeiboBg));
        layout.setVisibility(View.VISIBLE);
        layout.setPadding(0, 0, 0, 0);
        // 水平布局
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpText.gravity = Gravity.CENTER_VERTICAL;
        lpText.leftMargin = 15;
        // 执行一次清理
        layout.removeAllViews();
        // 内容部分
        LinearLayout contentll = new LinearLayout(context);
        contentll.setOrientation(LinearLayout.VERTICAL);
        TextView content = null;
        if (transWeibo.isWeiboIsDelete() == 1) {
            content = new TextView(context);
            content.setText("该分享已删除");
            content.setTextSize(13);
            content.setGravity(Gravity.CENTER_VERTICAL);
            content.setPadding(20, 25, 25, 25);
            content.setTextColor(context.getResources().getColor(R.color.bar));

            layout.setFocusable(false);
            layout.setFocusableInTouchMode(false);
            layout.setEnabled(false);
            layout.setClickable(false);

            layout.addView(content, lpText);
            return;
        } else {
            //title
            TextView title = new TextView(context);
            title.setTextSize(13);
            title.setSingleLine();
            title.setEllipsize(TextUtils.TruncateAt.END);
            title.setText(transWeibo.getTitle());
            title.setPadding(20, 25, 30, 5);
            title.setTextColor(context.getResources().getColor(R.color.black));
            contentll.addView(title);

            //content
            content = new TextView(context);
            content.setTextSize(12);
            content.setMaxLines(3);
            // 设置行距
            content.setLineSpacing(0, 1.4f);
            content.setGravity(Gravity.CENTER_VERTICAL);
            content.setEllipsize(TextUtils.TruncateAt.END);
            content.setPadding(20, 7, 25, 5);
            content.setTextColor(context.getResources().getColor(R.color.bar));
            uint.showContentLinkViewAndLinkMovement(transWeibo.getContent(), content);
            contentll.addView(content);

            //来源
            TextView source = new TextView(context);
            source.setTextSize(11);
            source.setSingleLine();
            source.setEllipsize(TextUtils.TruncateAt.END);
            source.setPadding(20, 5, 25, 25);
            source.setText("来自 " + transWeibo.getSource_name());

            SpannableString styledText = new SpannableString(source.getText());
            styledText.setSpan(new TextAppearanceSpan(context, R.style.postfrom), 3, (source.getText().toString()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setText(styledText, TextView.BufferType.SPANNABLE);

            source.setTextColor(context.getResources().getColor(R.color.bar));
            contentll.addView(source);
        }
        content.setTag(R.id.tag_position, currentWeibo.getSid());
        content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityPostDetail.class);
                Bundle data = new Bundle();
                int post_id = (Integer) view.getTag(R.id.tag_position);
                data.putInt("post_id", post_id);
                intent.putExtras(data);
                context.startActivity(intent);
            }
        });

        if (transWeibo.isWeiboIsDelete() != 1) {
            LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
                    UnitSociax.getWindowWidth(context) / 5,
                    UnitSociax.getWindowWidth(context) / 5);
            lpImage.setMargins(0, 0, 0, 0);
            lpImage.gravity = Gravity.CENTER_VERTICAL;
            // 转发微博内带有的图片
            if (transWeibo.hasImage() && transWeibo.getAttachImage() != null) {
                content.setLines(2);
                RelativeLayout rl_image = (RelativeLayout) inflater.inflate(
                        R.layout.component_weibo_image, null);
                layout.addView(rl_image, lpImage);
                ImageView iv_weibo_image = (ImageView) rl_image
                        .findViewById(R.id.iv_weibo_image);

                Glide.with(context)
                        .load(((ModelImageAttach) currentWeibo.getSourceWeibo()
                                .getAttachImage().get(0)).getOrigin())
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_weibo_image);

                iv_weibo_image.setLayoutParams(new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                final List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
                for (int i = 0; i < transWeibo.getAttachImage().size(); i++) {
                    ModelPhoto p = new ModelPhoto();
                    p.setId(i);
                    p.setUrl(((ModelImageAttach) (transWeibo.getAttachImage())
                            .get(i)).getOrigin());
                    photoList.add(p);
                }
                iv_weibo_image.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ActivityViewPager.class);
                        i.putExtra("index", "0");
                        i.putParcelableArrayListExtra("photolist",
                                (ArrayList<? extends Parcelable>) photoList);
                        context.startActivity(i);
                    }
                });
            }
            // 转发微博内带有的文件
            if (transWeibo.hasFile()) {
                if (transWeibo.getAttachImage() != null) {
                    for (int i = 0; i < transWeibo.getAttachImage().size(); i++) {
                        TextView tx = new TextView(context);
                        tx.setPadding(0, 0, 0, 0);
                        tx.setGravity(Gravity.CENTER_VERTICAL);
                        tx.setTextColor(context.getResources().getColor(
                                R.color.main_link_color));
                        tx.setCompoundDrawablesWithIntrinsicBounds(TypeNameUtil
                                .getDomLoadImg(((ModelImageAttach) currentWeibo
                                        .getSourceWeibo().getAttachImage()
                                        .get(i)).getName()), 0, 0, 0);
                        // tx.setCompoundDrawablePadding(10);
                        tx.setBackgroundResource(R.drawable.reviewboxbg);
                        tx.setText(((ModelImageAttach) currentWeibo
                                .getSourceWeibo().getAttachImage().get(i))
                                .getName());
                        layout.addView(tx, lpImage);
                    }
                }
            }
        }

        // 转发微博内带有的视频
        if (transWeibo.hasVideo()) {
            LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
                    UnitSociax.getWindowWidth(context) / 5,
                    UnitSociax.getWindowWidth(context) / 5);
            lpImage.gravity = Gravity.CENTER_VERTICAL;
            lpImage.setMargins(0, 0, 0, 0);
            FrameLayout videoLayout = (FrameLayout) View.inflate(context,
                    R.layout.weibo_video_item, null);
            ImageView videoImage = (ImageView) videoLayout
                    .findViewById(R.id.iv_video);
            ImageView videoPlay = (ImageView) videoLayout
                    .findViewById(R.id.iv_play);
            videoImage.setLayoutParams(new FrameLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            videoPlay.setLayoutParams(new FrameLayout.LayoutParams(UnitSociax
                    .getWindowWidth(context) / 10, UnitSociax
                    .getWindowWidth(context) / 10, Gravity.CENTER));
            final ModelVideo myVideo = transWeibo.getAttachVideo();
            try {
                if (myVideo.getVideoImgUrl() != null) {
                    // ImageLoader.getInstance().displayImage(myVideo.getVideoImgUrl(),
                    // videoImage, Thinksns.getOptions());
                    application.displayImage(myVideo.getVideoImgUrl(),
                            videoImage);
                }

                videoPlay.setTag(R.id.tag_weibo, currentWeibo);
            } catch (Exception e) {
                System.err.println("add voide image errro " + e.toString());
            }
            layout.addView(videoLayout, lpImage);
        }

        layout.addView(contentll, lpText);
        layout.setId(AppConstant.TRANSPOND_LAYOUT);
    }

    /**
     * 拼接转发微博
     *
     * @param layout，用于放转发内容
     * @param currentWeibo 当前微博，本方法会调用当前微博所包含的转发微博并且映射到内容上
     */
    public void appendTranspond(LinearLayout layout, ModelWeibo currentWeibo) {
        ModelWeibo transWeibo = currentWeibo.getSourceWeibo();
        layout.setTag(R.id.tag_weibo, transWeibo);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityWeiboDetail.class);
                Bundle data = new Bundle();
                ModelWeibo md = (ModelWeibo) view.getTag(R.id.tag_weibo);
                data.putSerializable("weibo", md);
                if (md == null) {
                    Log.e(TAG, "appendTranspond error weibo is null");
                    return;
                }
                intent.putExtras(data);
                context.startActivity(intent);
            }
        });
        if (transWeibo == null) {
            return;
        }

//        layout.setBackgroundColor(context.getResources().getColor(R.color.transWeiboBg));
        layout.setVisibility(View.VISIBLE);
        // 水平布局
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpText.gravity = Gravity.CENTER_VERTICAL;
        lpText.leftMargin = 10;
        // 执行一次清理
        layout.removeAllViews();
        // 内容部分
        LinearLayout contentll = new LinearLayout(context);
        contentll.setOrientation(LinearLayout.VERTICAL);
        TextView content = null;
        if (transWeibo.isWeiboIsDelete() == 1) {
            content = new TextView(context);
            content.setText("该分享已删除");
            content.setTextSize(13);
            content.setGravity(Gravity.CENTER_VERTICAL);
            content.setPadding(20, 25, 25, 25);
            content.setTextColor(context.getResources().getColor(R.color.bar));

            layout.setFocusable(false);
            layout.setFocusableInTouchMode(false);
            layout.setEnabled(false);
            layout.setClickable(false);

            layout.addView(content, lpText);
            return;
        } else {
            LinearLayout ll_user = new LinearLayout(context);
            ll_user.setOrientation(LinearLayout.HORIZONTAL);
            TextView userName = new TextView(context);
            userName.setTextSize(12);
            userName.setLines(1);
            userName.setEllipsize(TextUtils.TruncateAt.END);
            userName.setText(transWeibo.getUsername());
            userName.setTextColor(context.getResources()
                    .getColor(R.color.black));

            // 新添加用户行
            TextView time = new TextView(context);
            time.setTextSize(12);
            time.setLines(1);
            time.setEllipsize(TextUtils.TruncateAt.END);
            time.setGravity(Gravity.CENTER_VERTICAL);
            time.setPadding(10, 0, 0, 0);
            time.setTextColor(context.getResources().getColor(R.color.gray));
            try {
                time.append(" "
                        + TimeHelper.friendlyTime(transWeibo.getTimestamp())
                        + "");
            } catch (TimeIsOutFriendly e) {
                e.printStackTrace();
            }

            ll_user.addView(userName);
            ll_user.addView(time);
            ll_user.setPadding(20, 25, 30, 5);

            contentll.addView(ll_user);
            content = new TextView(context);
            content.setTextSize(11);
            content.setMaxLines(3);
            // 设置行距
            content.setLineSpacing(0, 1.4f);
            content.setGravity(Gravity.CENTER_VERTICAL);
            content.setEllipsize(TextUtils.TruncateAt.END);
            content.setPadding(15, 5, 15, 15);
            content.setSingleLine();
            content.setTextColor(context.getResources().getColor(R.color.bar));
            uint.showContentLinkViewAndLinkMovement(transWeibo.getContent(), content);
            contentll.addView(content);
            content.setTag(R.id.tag_weibo, transWeibo);
            content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ActivityWeiboDetail.class);
                    Bundle data = new Bundle();
                    ModelWeibo md = (ModelWeibo) view.getTag(R.id.tag_weibo);
                    data.putSerializable("weibo", md);
                    intent.putExtras(data);
                    context.startActivity(intent);
                }
            });

            LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
                    UnitSociax.getWindowWidth(context) / 7,
                    UnitSociax.getWindowWidth(context) / 7);
            int marigin = UnitSociax.dip2px(context, 7);
            lpImage.setMargins(marigin, marigin, 0, marigin);
            lpImage.gravity = Gravity.CENTER_VERTICAL;
            // 转发微博内带有的图片
            if (transWeibo.hasImage() && transWeibo.getAttachImage() != null) {
                ImageView iv_weibo_image = new ImageView(context);
                iv_weibo_image.setScaleType(ScaleType.CENTER_CROP);
                layout.addView(iv_weibo_image, lpImage);
                String url = ((ModelImageAttach) currentWeibo.getSourceWeibo().getAttachImage().get(0)).getOrigin();
                UIImageLoader.getInstance(context).displayImage(url, iv_weibo_image);
                final List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
                for (int i = 0; i < transWeibo.getAttachImage().size(); i++) {
                    ModelPhoto p = new ModelPhoto();
                    ModelImageAttach attach = (ModelImageAttach)transWeibo.getAttachImage().get(i);
                    p.setId(i);
                    p.setUrl(attach.getOrigin());
                    p.setMiddleUrl(attach.getOrigin());
                    p.setOriUrl(attach.getOrigin());
                    photoList.add(p);
                }

                iv_weibo_image.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ActivityViewPager.class);
                        i.putExtra("index", 0);
                        i.putParcelableArrayListExtra("photolist",
                                (ArrayList<? extends Parcelable>) photoList);
                        context.startActivity(i);
                    }
                });
            }
            // 转发微博内带有的文件
            if (transWeibo.hasFile()) {
                if (transWeibo.getAttachImage() != null) {
                    for (int i = 0; i < transWeibo.getAttachImage().size(); i++) {
                        TextView tx = new TextView(context);
                        tx.setPadding(0, 0, 0, 0);
                        tx.setGravity(Gravity.CENTER_VERTICAL);
                        tx.setTextColor(context.getResources().getColor(
                                R.color.main_link_color));
                        tx.setCompoundDrawablesWithIntrinsicBounds(TypeNameUtil
                                .getDomLoadImg(((ModelImageAttach) currentWeibo
                                        .getSourceWeibo().getAttachImage()
                                        .get(i)).getName()), 0, 0, 0);
                        // tx.setCompoundDrawablePadding(10);
                        tx.setBackgroundResource(R.drawable.reviewboxbg);
                        tx.setText(((ModelImageAttach) currentWeibo
                                .getSourceWeibo().getAttachImage().get(i))
                                .getName());
                        layout.addView(tx, lpImage);
                    }
                }
            }

            // 转发微博内带有的视频
            if (transWeibo.hasVideo()) {
                FrameLayout videoLayout = (FrameLayout) View.inflate(context,
                        R.layout.weibo_video_item, null);
                ImageView videoImage = (ImageView) videoLayout
                        .findViewById(R.id.iv_video);
                ImageView videoPlay = (ImageView) videoLayout
                        .findViewById(R.id.iv_play);
                videoImage.setLayoutParams(new FrameLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                videoPlay.setLayoutParams(new FrameLayout.LayoutParams(UnitSociax
                        .getWindowWidth(context) / 20, UnitSociax
                        .getWindowWidth(context) / 20, Gravity.CENTER));
                final ModelVideo myVideo = transWeibo.getAttachVideo();
                try {
                    if (myVideo.getVideoImgUrl() != null) {
                        // ImageLoader.getInstance().displayImage(myVideo.getVideoImgUrl(),
                        // videoImage, Thinksns.getOptions());
                        application.displayImage(myVideo.getVideoImgUrl(),
                                videoImage);
                    }

                    videoPlay.setTag(R.id.tag_weibo, currentWeibo);
                } catch (Exception e) {
                    System.err.println("add voide image errro " + e.toString());
                }
                layout.addView(videoLayout, lpImage);
            }
        }

        layout.addView(contentll, lpText);
        layout.setId(AppConstant.TRANSPOND_LAYOUT);
    }

    /**
     * 是否已经初始化过holder
     */
    public boolean isInitHolderSuccess() {
        return isInitHolderSuccess;
    }

    /**
     * 设置是否已经初始化holder
     */
    public void setInitHolderSuccess(boolean isInitHolderSuccess) {
        this.isInitHolderSuccess = isInitHolderSuccess;
    }
}
