package com.thinksns.sociax.t4.unit;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.db.AttachSqlHelper;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.adapter.AdapterWeiBoImageGridView;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.function.FunctionChangeSociaxItemStatus;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.android.img.UIImageLoader;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.android.task.ActivityTaskCenter;
import com.thinksns.sociax.t4.android.video.ActivityVideoDetail;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.component.GridViewNoScroll;
import com.thinksns.sociax.t4.model.*;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.TimeIsOutFriendly;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;
import com.thinksns.sociax.unit.TypeNameUtil;
import com.thinksns.tschat.map.ActivityLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态加载布局
 */
public class DynamicInflateForWeibo {

    private static final int MAX_SHOW_DIGG_USER_SIZE = 5;
    private static final int MAX_SHOW_COMMENT_SIZE = 3;

    private DynamicInflateForWeibo() {
    }

    /**
     * 添加用户头像组
     *
     * @param context
     * @param groupStub
     * @param path
     */
    public static void addUserGroup(final Context context, ViewStub groupStub, String path) {
        ImageView img = null;
        try {
            //如果没有被inflate过
            img = (ImageView) groupStub.inflate();
            groupStub.setTag(R.id.image_group, img);
            ((Thinksns) context.getApplicationContext()).displayImage(path, img);
        } catch (Exception e) {
            groupStub.setVisibility(View.VISIBLE);
            img = (ImageView) groupStub.getTag(R.id.image_group);
        } finally {
            if (img != null && context != null) {
                Glide.with(Thinksns.getContext())
                        .load(path)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(img);
            }
        }

    }

    /**
     * 添加图片
     *
     * @param context
     * @param imageStub
     * @param imageList
     */
    public static void addImage(final Context context, final ViewStub imageStub, final ListData<ModelImageAttach> imageList) {
        /**
         * 该方法的核心思想是：
         * 使用try、catch捕获ViewStub.inflate()方法
         * 当ViewStub未被inflate，执行try块方法，并将inflate过的View放至ViewStub中
         * 当ViewStub已被inflate，执行catch块，从ViewStub获取inflate过的View
         */
        ImageView image = null;
        try {
            image = (ImageView) imageStub.inflate();
            imageStub.setTag(R.id.iv_weibo_image, image);
        } catch (Exception e) {
            image = (ImageView) imageStub.getTag(R.id.iv_weibo_image);
        } finally {
            if (image != null && context != null) {
                setAboutImage(context, (ModelImageAttach) imageList.get(0), image);
            }
        }
    }

    /**
     * 添加图片组
     *
     * @param context
     * @param imageStub
     * @param imageList
     */
    public static void addImageGroup(final Context context, ViewStub imageStub,
                                     ListData<ModelImageAttach> imageList, int gridWidth) {
        GridViewNoScroll imageGroup = null;
        try {
            imageGroup = (GridViewNoScroll) imageStub.inflate();
            imageStub.setTag(R.id.gv_weibo, imageGroup);
        } catch (Exception e) {
            imageStub.setVisibility(View.VISIBLE);
            imageGroup = (GridViewNoScroll) imageStub.getTag(R.id.gv_weibo);
        } finally {
            if (imageGroup != null && context != null) {
                imageGroup.setSelector(new ColorDrawable(0));
                setAboutImageGroup(context, imageList, imageGroup, gridWidth);
            }
        }
    }

    /**
     * 添加点赞列表
     *
     * @param viewStub
     * @param diggUsers
     */
    public static void addDigg(ViewStub viewStub, ListData<ModelUser> diggUsers) {
        TextView diggList = null;
        try {
            diggList = (TextView) viewStub.inflate();
            viewStub.setTag(R.id.ll_praise_list, diggList);
        } catch (Exception e) {
            viewStub.setVisibility(View.VISIBLE);
            diggList = (TextView) viewStub.getTag(R.id.ll_praise_list);
        } finally {
            if (diggList != null) {
                setAboutDigg(diggList, diggUsers);
            }
        }
    }

    /**
     * 添加评论列表
     *
     * @param context
     * @param viewStub
     * @param weibo
     * @param position
     */
    public static void addComment(final Context context, ViewStub viewStub, final ModelWeibo weibo,
                                  int position, WeiboListViewClickListener clickListener) {
        LinearLayout commentList = null;
        try {
            commentList = (LinearLayout) viewStub.inflate();
            viewStub.setTag(R.id.ll_comments, commentList);
        } catch (Exception e) {
            viewStub.setVisibility(View.VISIBLE);
            commentList = (LinearLayout) viewStub.getTag(R.id.ll_comments);
        } finally {
            if (commentList != null && context != null) {
                setAboutComment(context, commentList, weibo, position, clickListener);
            }
        }
    }

    /**
     * 添加微吧
     *
     * @param context
     * @param viewStub
     */
    public static void addWeiba(final Context context, ViewStub viewStub, final ModelWeibo weibo) {
        LinearLayout weiba = null;
        try {
            weiba = (LinearLayout) viewStub.inflate();
            viewStub.setTag(R.id.ll_from_weiba_content, weiba);
        } catch (Exception e) {
            viewStub.setVisibility(View.VISIBLE);
            weiba = (LinearLayout) viewStub.getTag(R.id.ll_from_weiba_content);
        } finally {
            if (weiba != null && context != null) {
                setAboutWeiba(context, weiba, weibo);
            }
        }
    }

    /**
     * 添加位置信息
     */
    public static void addAddress(@NonNull Context context, @NonNull ViewStub viewStub, @NonNull ModelWeibo weibo) {
        TextView address = null;
        try {
            address = (TextView) viewStub.inflate();
            viewStub.setTag(R.id.tv_get_my_location);
        } catch (Exception e) {
            viewStub.setVisibility(View.VISIBLE);
            address = (TextView) viewStub.getTag(R.id.tv_get_my_location);
        } finally {
            if (address != null) {
                setAboutAddress(context, address, weibo);
            }
        }
    }


    /**
     * 添加视频
     *
     * @param context
     * @param viewStub
     * @param video
     * @param adapter
     * @param position
     */
    public static void addMedia(final Context context, ViewStub viewStub, ModelVideo video, AdapterSociaxList adapter, int position) {
        FrameLayout media = null;
        try {
            media = (FrameLayout) viewStub.inflate();
            viewStub.setTag(R.id.ll_media, media);
        } catch (Exception e) {
            viewStub.setVisibility(View.VISIBLE);
            media = (FrameLayout) viewStub.getTag(R.id.ll_media);
        } finally {
            if (media != null && context != null) {
                setAboutMedia(context, media, video);
            }
        }
    }

    /**
     * 添加转发类微博
     * @param viewStub
     * @param weibo
     */
    public static void addTransportWeibo(ViewStub viewStub, ModelWeibo weibo) {
        RelativeLayout rl_transport = null;
        if(viewStub.getParent() != null) {
            rl_transport = (RelativeLayout) viewStub.inflate();
            viewStub.setTag(R.id.stub_transport, rl_transport);
        }else {
            rl_transport = (RelativeLayout)viewStub.getTag(R.id.stub_transport);
        }

        viewStub.setVisibility(View.VISIBLE);
        appendTranspond(rl_transport, weibo);
    }

    /**
     * 拼接转发微博
     *
     * @param layout，用于放转发内容
     * @param currentWeibo 当前微博，本方法会调用当前微博所包含的转发微博并且映射到内容上
     */
    public static void appendTranspond(RelativeLayout layout, ModelWeibo currentWeibo) {
        ModelWeibo transWeibo = currentWeibo.getSourceWeibo();
        layout.setTag(R.id.tag_weibo, transWeibo);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityWeiboDetail.class);
                Bundle data = new Bundle();
                ModelWeibo md = (ModelWeibo) view.getTag(R.id.tag_weibo);
                data.putSerializable("weibo", md);
                if (md == null) {
                    return;
                }
                intent.putExtras(data);
                view.getContext().startActivity(intent);
            }
        });

        TextView content = (TextView)layout.findViewById(R.id.tv_weibo_content);
        if (transWeibo.isWeiboIsDelete() == 1) {
            content.setText("内容已被删除");
            layout.findViewById(R.id.ll_name).setVisibility(View.GONE);
            layout.findViewById(R.id.fl_image).setVisibility(View.GONE);
        } else {
            layout.findViewById(R.id.ll_name).setVisibility(View.VISIBLE);
            TextView userName = (TextView)layout.findViewById(R.id.tv_user_name);
            userName.setText(transWeibo.getUsername());

            TextView time = (TextView)layout.findViewById(R.id.tv_weibo_time);
            try {
                time.setText(TimeHelper.friendlyTime(transWeibo.getTimestamp()));
            } catch (TimeIsOutFriendly e) {
                e.printStackTrace();
            }

            UnitSociax.showContentLinkViewAndLinkMovement(transWeibo.getContent(), content);
            content.setTag(R.id.tag_weibo, transWeibo);
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ActivityWeiboDetail.class);
                    Bundle data = new Bundle();
                    ModelWeibo md = (ModelWeibo) view.getTag(R.id.tag_weibo);
                    data.putSerializable("weibo", md);
                    intent.putExtras(data);
                    view.getContext().startActivity(intent);
                }
            });

            // 转发微博内带有的图片
            FrameLayout frameLayout = (FrameLayout)layout.findViewById(R.id.fl_image);
            ImageView iv_weibo_image = (ImageView)layout.findViewById(R.id.iv_weibo_preivew);
            if (transWeibo.hasImage() && transWeibo.getAttachImage() != null) {
                frameLayout.setVisibility(View.VISIBLE);
                //隐藏播放图标
                layout.findViewById(R.id.iv_play).setVisibility(View.GONE);
                //设置转发内容最多显示2行
                content.setMaxLines(2);
                //获取转发缩略图
                String url = ((ModelImageAttach) currentWeibo.getSourceWeibo().getAttachImage().get(0)).getSmall();
                UIImageLoader.getInstance(layout.getContext()).displayImage(url, iv_weibo_image);
                //存储预览图片
                final List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
                for (int i = 0; i < transWeibo.getAttachImage().size(); i++) {
                    ModelPhoto p = new ModelPhoto();
                    p.setId(i);
                    ModelImageAttach atach = (ModelImageAttach) transWeibo.getAttachImage().get(i);
                    p.setUrl(atach.getSmall());
                    p.setOriUrl(atach.getOrigin());
                    photoList.add(p);
                }
                //点击缩略图查看转发微博的图片
                iv_weibo_image.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), ActivityViewPager.class);
                        i.putExtra("index", 0);
                        i.putParcelableArrayListExtra("photolist",
                                (ArrayList<? extends Parcelable>) photoList);
                        v.getContext().startActivity(i);
                    }
                });
            }else if (transWeibo.hasVideo()) {
                frameLayout.setVisibility(View.VISIBLE);
                ImageView videoPlay = (ImageView) layout.findViewById(R.id.iv_play);
                videoPlay.setVisibility(View.VISIBLE);
                //设置转发内容最多显示2行
                content.setMaxLines(2);
                ModelVideo myVideo = transWeibo.getAttachVideo();
                try {
                    if (myVideo.getVideoImgUrl() != null) {
                        UIImageLoader.getInstance(videoPlay.getContext()).displayImage(myVideo.getVideoImgUrl(),iv_weibo_image);
                    }
                    videoPlay.setTag(R.id.tag_weibo, currentWeibo);
                } catch (Exception e) {
                    System.err.println("add voide image errro " + e.toString());
                }
            }else {
                frameLayout.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 添加转发微吧帖子
     * @param viewStub
     * @param currentWeibo
     */
    public static void addTransportWeiba(ViewStub viewStub, ModelWeibo currentWeibo) {
        RelativeLayout rl_transport = null;
        if(viewStub.getParent() != null) {
            rl_transport = (RelativeLayout) viewStub.inflate();
            viewStub.setTag(R.id.stub_transport, rl_transport);
        }else {
            rl_transport = (RelativeLayout)viewStub.getTag(R.id.stub_transport);
        }

        viewStub.setVisibility(View.VISIBLE);
        appendTranspondPost(rl_transport, currentWeibo);
    }

    public static void appendTranspondPost(RelativeLayout layout, ModelWeibo currentWeibo) {
        ModelWeibo transWeibo = currentWeibo.getSourceWeibo();
        if (transWeibo == null) {
            return;
        }

        layout.setTag(R.id.tag_weibo, currentWeibo.getSid());
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), ActivityPostDetail.class);
                Bundle data = new Bundle();
                data.putInt("post_id", (Integer)view.getTag(R.id.tag_weibo));
                intent.putExtras(data);
                view.getContext().startActivity(intent);
            }
        });

        // 内容部分
        TextView content = (TextView)layout.findViewById(R.id.tv_weiba_content);
        if (transWeibo.isWeiboIsDelete() == 1) {
            content.setText("该分享已删除");
            //隐藏微吧标题
            layout.findViewById(R.id.tv_weiba_title);
            layout.findViewById(R.id.tv_post_from);
            return;
        } else {
            //title
            TextView title = (TextView)layout.findViewById(R.id.tv_weiba_title);
            title.setText(transWeibo.getTitle());
            UnitSociax.showContentLinkViewAndLinkMovement(transWeibo.getContent(), content);
            //来源
            TextView source = (TextView)layout.findViewById(R.id.tv_post_from);
            source.setText("来自 " + transWeibo.getSource_name());
//            SpannableString styledText = new SpannableString(source.getText());
//            styledText.setSpan(new TextAppearanceSpan(source.getContext(), R.style.postfrom), 3,
//                    (source.getText().toString()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            source.setText(styledText, TextView.BufferType.SPANNABLE);
            content.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            source.setVisibility(View.VISIBLE);
        }

        content.setTag(R.id.tag_position, currentWeibo.getSid());
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityPostDetail.class);
                Bundle data = new Bundle();
                int post_id = (Integer) view.getTag(R.id.tag_position);
                data.putInt("post_id", post_id);
                intent.putExtras(data);
                view.getContext().startActivity(intent);
            }
        });

    }
    /**
     * 添加文件
     * @param context
     * @param viewStub
     * @param attachImage
     * @param position
     */
    public static void addFile(final Context context, ViewStub viewStub, ListData<ModelImageAttach> attachImage, int position) {
        LinearLayout ll_file = null;
        try {
            ll_file = (LinearLayout) viewStub.inflate();
            viewStub.setTag(R.id.ll_file, ll_file);
        } catch (Exception e) {
            viewStub.setVisibility(View.VISIBLE);
            ll_file = (LinearLayout) viewStub.getTag(R.id.ll_file);
        } finally {
            if (ll_file != null && context != null) {
                setAboutFile(context, ll_file, attachImage, position);
            }
        }
    }

    /**
     * 添加关注
     * @param viewStub
     */
    public static void addFollow(final Context context, final ViewStub viewStub, View.OnClickListener listener) {
        TextView tv_add_follow = null;
        try {
            tv_add_follow = (TextView) viewStub.inflate();
            viewStub.setTag(R.id.tv_add_follow, tv_add_follow);
        } catch (Exception e) {
            viewStub.setVisibility(View.VISIBLE);
            tv_add_follow = (TextView) viewStub.getTag(R.id.tv_add_follow);
        } finally {
            if (tv_add_follow != null && context != null) {
                tv_add_follow.setOnClickListener(listener);
            }
        }
    }

    /**
     * 设置单张图片相关
     *
     * @param image
     * @param imageView
     */
    private static void setAboutImage(final Context context, final ModelImageAttach image, ImageView imageView) {

        Glide.with(Thinksns.getContext())
                .load(image.getSmall())
                .centerCrop()
                .placeholder(R.color.bg_ios)
                .crossFade()
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,
                        ActivityViewPager.class);
                i.putExtra("index", "0");
                final List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
                ModelPhoto p = new ModelPhoto();
                p.setId(0);
                p.setUrl(image.getOrigin());
                photoList.add(p);
                i.putParcelableArrayListExtra("photolist", (ArrayList<? extends Parcelable>) photoList);
                context.startActivity(i);
            }
        });

        imageView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置图片组
     *
     * @param context
     * @param imageList
     * @param view
     */
    private static void setAboutImageGroup(final Context context, final ListData<ModelImageAttach> imageList,
                                           final GridViewNoScroll view, int gridWidth) {
        int imgNum = imageList.size();
        // 设置列数
        view.setNumColumns((imgNum == 1) ? 1 : (imgNum == 2 || imgNum == 4 ? 2 : 3));
        final List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
        for (int i = 0; i < imgNum; i++) {
            ModelPhoto p = new ModelPhoto();
            p.setId(i);
            p.setOriUrl(((ModelImageAttach) imageList.get(i)).getOrigin());
            p.setMiddleUrl(((ModelImageAttach) imageList.get(i)).getMiddle());
            p.setUrl(((ModelImageAttach)imageList.get(i)).getSmall());
            photoList.add(p);
        }

        AdapterWeiBoImageGridView imageAdapter = new AdapterWeiBoImageGridView(context, photoList, gridWidth);
        if(imgNum == 1) {
            ModelImageAttach attach = (ModelImageAttach)imageList.get(0);
            imageAdapter.setSingleImageWidthHeight(attach.getAttach_origin_width(), attach.getAttach_origin_height());
        }
        view.setAdapter(imageAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        Intent i = new Intent(context,
                                ActivityViewPager.class);
                        i.putExtra("index", position);
                        i.putParcelableArrayListExtra(
                                "photolist",
                                (ArrayList<? extends Parcelable>) photoList);
                        Log.e("DynamicInflateForWeibo", "width:" + view.getMeasuredWidth() + ", height:" + view.getMeasuredHeight());
                        ActivityViewPager.imageSize = new ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                        context.startActivity(i);
                    }
                });

        //设置表格快速滚动不加载图片
        view.setOnScrollListener(new PauseOnScrollListener(UIImageLoader.getInstance(context).getImageLoader(),
                true, true));
    }

    /**
     * 设置点赞列表
     *
     * @param diggList
     * @param diggUsers
     */
    private static void setAboutDigg(TextView diggList, ListData<ModelUser> diggUsers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < diggUsers.size() && i < MAX_SHOW_DIGG_USER_SIZE; i++) {
            ModelUser user = (ModelUser) diggUsers.get(i);
            sb.append(user.getUserName()).append(i < diggUsers.size() - 1 && i < MAX_SHOW_DIGG_USER_SIZE ? ", " : " ");
            if (i == MAX_SHOW_DIGG_USER_SIZE - 1) {
                sb.append("...");
            }
        }

        // 显示点赞列表
        diggList.setText(sb.toString());
    }

    /**
     * 设置评论列表
     *
     * @param context
     * @param comment
     * @param weibo
     * @param position
     */
    private static void setAboutComment(final Context context, LinearLayout comment, final ModelWeibo weibo,
                                        final int position, final WeiboListViewClickListener clickListener) {
        // init something
        ListData<SociaxItem> comments = weibo.getCommentList();
        LinearLayout commentList = (LinearLayout) comment.findViewById(R.id.ll_comment_list);
        final TextView more = (TextView) comment.findViewById(R.id.tv_comment_list);
        final UnitSociax uint = new UnitSociax(context);
        // 清空评论
        commentList.removeAllViews();
        int size = comments.size() < MAX_SHOW_COMMENT_SIZE ? comments.size() : MAX_SHOW_COMMENT_SIZE;
        // do something
        for (int j = 0; j < size; j++) {
            final ModelComment com = (ModelComment)comments.get(j);
            String comStr = com.getContent();
            TextView textView = new TextView(context);
            textView.setTextColor(context.getResources().getColor(R.color.gray));
            textView.setBackgroundResource(R.drawable.btn_press_selector);
            textView.setGravity(Gravity.LEFT | Gravity.TOP);
            String uname = com.getUname();
            String toname = null;
            int pos = comStr.indexOf("回复@");
            if (pos != -1) {
                //回复某人
                int end = comStr.indexOf("：", pos + 3);
                if(end != -1) {
                    toname = comStr.substring(pos + 3, end);
                }
            } else {
                comStr = " ：" + comStr;
                toname = uname;
            }

            com.setToName(toname);
            uint.showContentLinkViewAndLinkMovementchat(uname, comStr, textView, 11);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //评论微博
                    if(clickListener != null) {
                        clickListener.onCommentWeibo(weibo, com);
                    }
                }
            });

//            textView.setOnClickListener(jumpToDetaild(context, weibo, position));
            commentList.addView(textView);
        }
        if (comments.size() <= MAX_SHOW_COMMENT_SIZE) {
            more.setVisibility(View.GONE);
        } else {
            more.setVisibility(View.VISIBLE);
            more.setText("查看更多评论...");
        }

        comment.setOnClickListener(jumpToDetaild(context, weibo, position));
        if (more.getVisibility() == View.VISIBLE) {
            more.setOnClickListener(jumpToDetaild(context, weibo, position));
        }
    }

    /**
     * 匿名类,跳转到微博详情
     * @param context
     * @param weibo
     * @param position
     * @return
     */
    private static View.OnClickListener jumpToDetaild(final Context context, final ModelWeibo weibo, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context == null)
                    return;
                Intent intent = new Intent(context,
                        ActivityWeiboDetail.class);
                Bundle data = new Bundle();
                data.putSerializable("weibo", weibo);
                data.putInt("position", position);
                intent.putExtras(data);
                context.startActivity(intent);
            }
        };
    }

    /**
     * 设置微吧
     *
     * @param context
     * @param weiba
     * @param weibo
     */
    private static void setAboutWeiba(final Context context, LinearLayout weiba, final ModelWeibo weibo) {
        // init something
        final UnitSociax uint = new UnitSociax(context);
        LinearLayout ll_post_no_delete = (LinearLayout) weiba.findViewById(R.id.ll_post_no_delete);
        TextView tv_post_is_delete = (TextView) weiba.findViewById(R.id.tv_post_is_delete);
        TextView tv_post_title = (TextView) weiba.findViewById(R.id.tv_post_title);
        TextView tv_post_content = (TextView) weiba.findViewById(R.id.tv_post_content);
        TextView tv_post_from = (TextView) weiba.findViewById(R.id.tv_post_from);

        // do something
        if (weibo.isWeiboIsDelete() > 0) {
            ll_post_no_delete.setVisibility(View.GONE);
            tv_post_is_delete.setVisibility(View.VISIBLE);
        } else {
            ll_post_no_delete.setVisibility(View.VISIBLE);
            tv_post_is_delete.setVisibility(View.GONE);
            weiba.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,
                            ActivityPostDetail.class);
                    int post_id = weibo.getSid();
                    intent.putExtra("post_id", post_id);
                    context.startActivity(intent);
                }
            });
            tv_post_title.setText(weibo.getTitle());
            tv_post_content.setText(weibo.getContent()
                    .equals("") ? "图片帖子" : weibo.getContent());
            tv_post_content.setLineSpacing(5, 1.2f);
//            uint.showContentLinkViewAndLinkMovement(tv_post_content.getText().toString(), tv_post_content);
            tv_post_from.setText("来自 " + weibo.getSource_name());
            //设置微吧名称的样式
//            SpannableString styledText = new SpannableString(tv_post_from.getText());
//            styledText.setSpan(new TextAppearanceSpan(context, R.style.postfrom), 3, (tv_post_from.getText().toString()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            tv_post_from.setText(styledText, TextView.BufferType.SPANNABLE);
        }
    }

    /**
     * 设置视频
     *
     * @param context
     * @param ll_media
     * @param myVideo
     */
    private static void setAboutMedia(final Context context, FrameLayout ll_media,
                                      final ModelVideo myVideo) {
        ImageView ivVideo = (ImageView) ll_media.findViewById(R.id.img_vedio);
        View overlay = ll_media.findViewById(R.id.view_video_overlay);
        ivVideo.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Glide.with(Thinksns.getContext())
                .load(myVideo.getVideoImgUrl())
                .placeholder(R.color.bg_ios)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivVideo);
        ll_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                String videoUrl, videoImgUrl = "";
                if ("1".equals(myVideo.getHost())) {
                    intent = new Intent(context, ActivityVideoDetail.class);
                    videoUrl = myVideo.getVideoDetail();
                    videoImgUrl = myVideo.getVideoImgUrl();
                } else {
                    intent = new Intent(context, NetActivity.class);
                    videoUrl = myVideo.getVideoPart();
                }
                intent.putExtra("url", videoUrl);
                intent.putExtra("preview_url", videoImgUrl);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 设置文件
     * @param context
     * @param ll_file
     * @param attachImage
     * @param position
     */
    private static void setAboutFile(final Context context, LinearLayout ll_file, ListData<ModelImageAttach> attachImage, int position) {
        LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpImage.gravity = Gravity.CENTER_VERTICAL;
        if (attachImage != null) {
            ll_file.removeAllViews();
            for (int i = 0; i < attachImage.size(); i++) {
                TextView tx = new TextView(context);
                tx.setPadding(8, 8, 0, 8);
                tx.setGravity(Gravity.CENTER_VERTICAL);
                tx.setTextColor(context.getResources().getColor(
                        R.color.main_link_color));
                tx.setCompoundDrawablesWithIntrinsicBounds(TypeNameUtil
                                .getDomLoadImg(((ModelImageAttach) attachImage.get(i)).getName()),
                        0, 0, 0);
                tx.setCompoundDrawablePadding(10);
                tx.setBackgroundResource(R.drawable.reviewboxbg);
                tx.setText(((ModelImageAttach) attachImage.get(i)).getName());
                ll_file.addView(tx, lpImage);
                ll_file.setTag(position);
            }
        }
    }

    /**
     * 设置关注
     * @param context
     * @param view
     * @param uid
     */
    private static void setAboutFollow(final AdapterSociaxList adapter, final Context context, final View view, int uid) {
        FunctionChangeSociaxItemStatus fc = new FunctionChangeSociaxItemStatus(context);
        fc.setListenerSociax(new ListenerSociax() {

            @Override
            public void onTaskSuccess() {
                adapter.refreshNewSociaxList();
            }

            @Override
            public void onTaskError() {
                view.setClickable(true);
            }

            @Override
            public void onTaskCancle() {
            }
        });
        fc.changeUserInfoFollow(uid, false);
    }

    /**
     * 设置位置信息
     * @param context
     * @param address
     * @param weibo
     */
    private static void setAboutAddress(final Context context, TextView address, final ModelWeibo weibo) {
        address.setText(weibo.getAddress());
        if(weibo.getLatitude() != null && weibo.getLongitude() != null) {
            address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent intent = new Intent(context, ActivityLocation.class);
                    intent.putExtra("latitude", weibo.getLatitude());
                    intent.putExtra("longitude", weibo.getLongitude());
                    intent.putExtra("address", weibo.getAddress());
                    context.startActivity(intent);
                }
            });
        }
    }
}
