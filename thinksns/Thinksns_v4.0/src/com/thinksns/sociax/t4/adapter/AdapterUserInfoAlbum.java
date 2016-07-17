package com.thinksns.sociax.t4.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.function.FunctionPhotoFullScreen;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.android.user.ActivityUserPhoVedlist;
import com.thinksns.sociax.t4.android.video.ActivityVideoDetail;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 类说明：
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-20
 */
public class AdapterUserInfoAlbum extends AdapterSociaxList {
    ModelUser user;
    private boolean isMe = false;    // 标记是否是进入我的个人主页
    private Thinksns application;

    @Override
    public int getCount() {
        return 1;
    }

    /**
     * @param context
     * @param list    1：简介；2我的微博；3相册，4礼物 默认1
     */
    public AdapterUserInfoAlbum(ThinksnsAbscractActivity context,
                                ListData<SociaxItem> list, ModelUser user) {
        super(context, list);
        this.user = user;
        this.setShowFooter(false);
        if (user.getUid() == Thinksns.getMy().getUid()) {
            isMe = true;
        }
        application = (Thinksns) context.getApplicationContext();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new HolderSociax();
            convertView = inflater.inflate(R.layout.fragment_userinfo_album,
                    null);
            holder.ib_photo_next = (ImageButton) convertView
                    .findViewById(R.id.ib_photo_next);
            holder.ib_video_next = (ImageButton) convertView
                    .findViewById(R.id.ib_video_next);
            holder.tv_photo_count = (TextView) convertView
                    .findViewById(R.id.tv_photo_count);
            holder.tv_video_count = (TextView) convertView
                    .findViewById(R.id.tv_video_count);
            holder.rl_video_more = (RelativeLayout) convertView
                    .findViewById(R.id.rl_video_more);
            holder.rl_photo_more = (RelativeLayout) convertView
                    .findViewById(R.id.rl_photo_more);

            holder.img_photo_one = (ImageView) convertView
                    .findViewById(R.id.img_photo_one);
            holder.img_photo_two = (ImageView) convertView
                    .findViewById(R.id.img_photo_two);
            holder.img_photo_three = (ImageView) convertView
                    .findViewById(R.id.img_photo_three);
            holder.img_photo_four = (ImageView) convertView
                    .findViewById(R.id.img_photo_four);

            holder.img_video_one = (ImageView) convertView
                    .findViewById(R.id.img_vedio_one);
            holder.img_video_two = (ImageView) convertView
                    .findViewById(R.id.img_vedio_two);
            holder.img_video_three = (ImageView) convertView
                    .findViewById(R.id.img_vedio_three);
            holder.img_video_four = (ImageView) convertView
                    .findViewById(R.id.img_vedio_four);

            holder.img_vedio_one_bf = convertView
                    .findViewById(R.id.img_vedio_one_bf);
            holder.img_vedio_two_bf = convertView
                    .findViewById(R.id.img_vedio_two_bf);
            holder.img_vedio_three_bf = convertView
                    .findViewById(R.id.img_vedio_three_bf);
            holder.img_vedio_four_bf = convertView
                    .findViewById(R.id.img_vedio_four_bf);

            holder.ll_myPic = (LinearLayout) convertView
                    .findViewById(R.id.ll_myPic);
            holder.ll_myVideo = (LinearLayout) convertView
                    .findViewById(R.id.ll_myVideo);
            holder.tv_tips_nopic = (TextView) convertView
                    .findViewById(R.id.tv_tips_nopic);
            holder.tv_tips_novedio = (TextView) convertView
                    .findViewById(R.id.tv_tips_novideo);

            holder.view1 = (View) convertView.findViewById(R.id.view1);
            holder.view2 = (View) convertView.findViewById(R.id.view2);
            holder.view3 = (View) convertView.findViewById(R.id.view3);

            convertView.setTag(R.id.tag_viewholder, holder);
        } else {
            holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        if (Integer.parseInt(user.getPhotoCount()) > 0) {
            holder.tv_photo_count.setText(user.getPhotoCount());
        }
        if (Integer.parseInt(user.getVdeioCount()) > 0) {
            holder.tv_video_count.setText(user.getVdeioCount());
        }

        //查看更多视频
        holder.rl_video_more.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        ActivityUserPhoVedlist.class);
                intent.putExtra("type", "vedio");
                intent.putExtra("uid", user.getUid());
                context.startActivity(intent);
            }
        });

        //根据屏幕宽度来确定视频或照片显示大小
        int width = (UnitSociax.getWindowWidth(context) - 35) / 4;
        int width_bg = UnitSociax.dip2px(context, 40);

        android.widget.LinearLayout.LayoutParams lp_ll = new LinearLayout.LayoutParams(width, width);
        android.widget.FrameLayout.LayoutParams lp_fl_vedio = new FrameLayout.LayoutParams(width, width);
        android.widget.FrameLayout.LayoutParams lp_fl_bg = new FrameLayout.LayoutParams(width_bg, width_bg);
        int marginSize = (width - width_bg) / 2;
        lp_fl_bg.setMargins(marginSize, marginSize, 0, 0);
        /**
         * 设置预览的视频
         */
        if (user.getVedio() != null) {
            JSONArray vedioarray = user.getVedio();

            switch (vedioarray.length()) {
                case 1:
                    try {
                        holder.ll_myVideo.setVisibility(View.VISIBLE);
                        holder.view1.setVisibility(View.VISIBLE);
                        holder.view2.setVisibility(View.VISIBLE);

                        application.displayImage(vedioarray.getJSONObject(0).getString("flashimg"), holder.img_video_one);

                        holder.img_video_one.setLayoutParams(lp_fl_vedio);
                        holder.img_vedio_one_bf.setLayoutParams(lp_fl_bg);

                        holder.img_video_two.setImageBitmap(null);
                        holder.img_video_three.setImageBitmap(null);


                        holder.img_video_one.setTag(vedioarray.getJSONObject(0).getString("flashvar").trim());
                        holder.img_video_one
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);
                                    }
                                });
                        holder.img_video_two.setOnClickListener(null);
                        holder.img_video_three.setOnClickListener(null);

                        holder.img_vedio_one_bf.setVisibility(View.VISIBLE);
                        holder.img_vedio_two_bf.setVisibility(View.GONE);
                        holder.img_vedio_three_bf.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        holder.ll_myVideo.setVisibility(View.VISIBLE);

                        holder.view1.setVisibility(View.VISIBLE);
                        holder.view2.setVisibility(View.VISIBLE);

                        application.displayImage(vedioarray
                                .getJSONObject(0).getString("flashimg"), holder.img_video_one);
                        application.displayImage(vedioarray
                                .getJSONObject(1).getString("flashimg"), holder.img_video_two);

                        holder.img_video_one.setLayoutParams(lp_fl_vedio);
                        holder.img_video_two.setLayoutParams(lp_fl_vedio);

                        holder.img_vedio_one_bf.setLayoutParams(lp_fl_bg);
                        holder.img_vedio_two_bf.setLayoutParams(lp_fl_bg);

                        holder.img_video_three.setImageBitmap(null);

                        holder.img_vedio_one_bf.setVisibility(View.VISIBLE);
                        holder.img_vedio_two_bf.setVisibility(View.VISIBLE);
                        holder.img_vedio_three_bf.setVisibility(View.GONE);

                        holder.img_video_one.setTag(vedioarray.getJSONObject(0)
                                .getString("flashvar"));
                        holder.img_video_one
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);

                                    }
                                });

                        holder.img_video_two.setTag(vedioarray.getJSONObject(1)
                                .getString("flashvar"));
                        holder.img_video_two
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);

                                    }
                                });
                        holder.img_video_three.setOnClickListener(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case 3:
                    try {
                        holder.ll_myVideo.setVisibility(View.VISIBLE);

                        holder.view1.setVisibility(View.VISIBLE);
                        holder.view2.setVisibility(View.VISIBLE);

                        application.displayImage(vedioarray
                                .getJSONObject(0).getString("flashimg"), holder.img_video_one);
                        application.displayImage(vedioarray
                                .getJSONObject(1).getString("flashimg"), holder.img_video_two);
                        application.displayImage(vedioarray
                                .getJSONObject(2).getString("flashimg"), holder.img_video_three);

                        holder.img_video_one.setLayoutParams(lp_fl_vedio);
                        holder.img_video_two.setLayoutParams(lp_fl_vedio);
                        holder.img_video_three.setLayoutParams(lp_fl_vedio);

                        holder.img_vedio_one_bf.setLayoutParams(lp_fl_bg);
                        holder.img_vedio_two_bf.setLayoutParams(lp_fl_bg);
                        holder.img_vedio_three_bf.setLayoutParams(lp_fl_bg);

                        holder.img_vedio_one_bf.setVisibility(View.VISIBLE);
                        holder.img_vedio_two_bf.setVisibility(View.VISIBLE);
                        holder.img_vedio_three_bf.setVisibility(View.VISIBLE);

                        holder.img_video_one.setTag(vedioarray.getJSONObject(0)
                                .getString("flashvar"));
                        holder.img_video_one
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);

                                    }
                                });
                        holder.img_video_two.setTag(vedioarray.getJSONObject(1)
                                .getString("flashvar"));
                        holder.img_video_two
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);

                                    }
                                });
                        holder.img_video_three.setTag(vedioarray.getJSONObject(2)
                                .getString("flashvar"));
                        holder.img_video_three
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);

                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 4:
                    try {
                        holder.ll_myVideo.setVisibility(View.VISIBLE);

                        holder.view1.setVisibility(View.VISIBLE);
                        holder.view2.setVisibility(View.VISIBLE);

                        application.displayImage(vedioarray
                                .getJSONObject(0).getString("flashimg"), holder.img_video_one);
                        application.displayImage(vedioarray
                                .getJSONObject(1).getString("flashimg"), holder.img_video_two);
                        application.displayImage(vedioarray
                                .getJSONObject(2).getString("flashimg"), holder.img_video_three);
                        application.displayImage(vedioarray
                                .getJSONObject(3).getString("flashimg"), holder.img_video_four);

                        holder.img_vedio_one_bf.setVisibility(View.VISIBLE);
                        holder.img_vedio_two_bf.setVisibility(View.VISIBLE);
                        holder.img_vedio_three_bf.setVisibility(View.VISIBLE);
                        holder.img_vedio_four_bf.setVisibility(View.VISIBLE);

                        holder.img_vedio_one_bf.setLayoutParams(lp_fl_bg);
                        holder.img_vedio_two_bf.setLayoutParams(lp_fl_bg);
                        holder.img_vedio_three_bf.setLayoutParams(lp_fl_bg);
                        holder.img_vedio_four_bf.setLayoutParams(lp_fl_bg);

                        holder.img_video_one.setLayoutParams(lp_fl_vedio);
                        holder.img_video_two.setLayoutParams(lp_fl_vedio);
                        holder.img_video_three.setLayoutParams(lp_fl_vedio);
                        holder.img_video_four.setLayoutParams(lp_fl_vedio);

                        holder.img_video_one.setTag(vedioarray.getJSONObject(0)
                                .getString("flashvar"));
                        holder.img_video_one
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);

                                    }
                                });
                        holder.img_video_two.setTag(vedioarray.getJSONObject(1)
                                .getString("flashvar"));
                        holder.img_video_two
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);
                                    }
                                });
                        holder.img_video_three.setTag(vedioarray.getJSONObject(2)
                                .getString("flashvar"));
                        holder.img_video_three
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        Intent intent = new Intent(context, ActivityVideoDetail.class);
                                        intent.putExtra("url", (String) v.getTag());
                                        context.startActivity(intent);

                                    }
                                });
                        holder.img_video_four.setTag(vedioarray.getJSONObject(3).getString("flashvar"));
                        holder.img_video_four.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context, ActivityVideoDetail.class);
                                intent.putExtra("url", (String) v.getTag());
                                context.startActivity(intent);

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    // 提示消息
                    holder.tv_tips_novedio.setVisibility(View.VISIBLE);
                    holder.ib_video_next.setVisibility(View.GONE);
                    holder.rl_video_more.setClickable(false);
                    if (isMe) {
                        holder.tv_tips_novedio.setText("没有视频");
                    }

                    holder.img_video_one.setImageBitmap(null);
                    holder.img_video_two.setImageBitmap(null);
                    holder.img_video_three.setImageBitmap(null);
                    holder.img_video_four.setImageBitmap(null);

                    holder.img_vedio_one_bf.setVisibility(View.GONE);
                    holder.img_vedio_two_bf.setVisibility(View.GONE);
                    holder.img_vedio_three_bf.setVisibility(View.GONE);
                    holder.img_video_four.setVisibility(View.GONE);

                    holder.img_video_one.setOnClickListener(null);
                    holder.img_video_two.setOnClickListener(null);
                    holder.img_video_three.setOnClickListener(null);
                    holder.img_video_four.setOnClickListener(null);
                    break;
            }
        }
        /************************************************************************************/
        //查看相册更多
        holder.rl_photo_more.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityUserPhoVedlist.class);
                intent.putExtra("type", "photo");
                intent.putExtra("uid", user.getUid());
                context.startActivity(intent);
            }
        });
        /**
         * 设置预览的图片以及图片点开事件
         */
        if (user.getPhoto() != null) {
            JSONArray photoarray = user.getPhoto();
            final FunctionPhotoFullScreen fc_photoList = new FunctionPhotoFullScreen(
                    context);
            fc_photoList.setUserInfoPhotoList(photoarray);
            holder.img_photo_one.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ActivityViewPager.imageSize = new ImageSize(v.getMeasuredWidth(), v.getMeasuredHeight());
                    fc_photoList.clickAtPhoto(0);
                }
            });

            holder.img_photo_two.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    fc_photoList.clickAtPhoto(1);
                }
            });
            holder.img_photo_three.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    fc_photoList.clickAtPhoto(2);
                }
            });
            holder.img_photo_four.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    fc_photoList.clickAtPhoto(3);
                }
            });
            switch (photoarray.length()) {
                case 1:
                    try {
                        holder.ll_myPic.setVisibility(View.VISIBLE);

                        holder.view3.setVisibility(View.VISIBLE);

                        application.displayImage(photoarray
                                .getJSONObject(0).getString("image_url"), holder.img_photo_one);

                        holder.img_photo_one.setLayoutParams(lp_ll);

                        holder.img_photo_two.setImageBitmap(null);
                        holder.img_photo_three.setImageBitmap(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        holder.ll_myPic.setVisibility(View.VISIBLE);

                        holder.view3.setVisibility(View.VISIBLE);

                        application.displayImage(photoarray
                                .getJSONObject(0).getString("image_url"), holder.img_photo_one);
                        application.displayImage(photoarray
                                .getJSONObject(1).getString("image_url"), holder.img_photo_two);

                        holder.img_photo_one.setLayoutParams(lp_ll);
                        holder.img_photo_two.setLayoutParams(lp_ll);

                        holder.img_photo_three.setImageBitmap(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case 3:

                    try {
                        holder.ll_myPic.setVisibility(View.VISIBLE);

                        holder.view3.setVisibility(View.VISIBLE);

                        application.displayImage(photoarray
                                .getJSONObject(0).getString("image_url"), holder.img_photo_one);
                        application.displayImage(photoarray
                                .getJSONObject(1).getString("image_url"), holder.img_photo_two);
                        application.displayImage(photoarray
                                .getJSONObject(2).getString("image_url"), holder.img_photo_three);

                        holder.img_photo_one.setLayoutParams(lp_ll);
                        holder.img_photo_two.setLayoutParams(lp_ll);
                        holder.img_photo_three.setLayoutParams(lp_ll);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case 4:
                    try {
                        holder.ll_myPic.setVisibility(View.VISIBLE);

                        holder.view3.setVisibility(View.VISIBLE);

                        application.displayImage(photoarray
                                .getJSONObject(0).getString("image_url"), holder.img_photo_one);
                        application.displayImage(photoarray
                                .getJSONObject(1).getString("image_url"), holder.img_photo_two);
                        application.displayImage(photoarray
                                .getJSONObject(2).getString("image_url"), holder.img_photo_three);
                        application.displayImage(photoarray
                                .getJSONObject(3).getString("image_url"), holder.img_photo_four);

                        holder.img_photo_one.setLayoutParams(lp_ll);
                        holder.img_photo_two.setLayoutParams(lp_ll);
                        holder.img_photo_three.setLayoutParams(lp_ll);
                        holder.img_photo_four.setLayoutParams(lp_ll);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:

                    holder.tv_tips_nopic.setVisibility(View.VISIBLE);
                    holder.ib_photo_next.setVisibility(View.GONE);
                    holder.rl_photo_more.setClickable(false);
                    if (isMe) {
                        holder.tv_tips_nopic.setText("没有照片");
                    }
                    holder.img_photo_one.setImageBitmap(null);
                    holder.img_photo_two.setImageBitmap(null);
                    holder.img_photo_three.setImageBitmap(null);
                    holder.img_photo_four.setImageBitmap(null);
                    break;
            }
        }

        /************************************************************************************/
        return convertView;
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
//			listuser.add(new Api.Users().show(user));
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
//			listuser.add(new Api.Users().show(user));
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

    public void loadInitData() {
        if (!com.thinksns.sociax.thinksnsbase.utils.UnitSociax.isNetWorkON(context)) {
            Toast.makeText(context, R.string.net_fail, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        setLoadingView();
        if (loadingView != null)
            loadingView.show((View) getListView());
        if (context.getOtherView() != null) {
            loadingView.show(context.getOtherView());
        }
        refreshNewSociaxList();
    }

    @Override
    public void addHeader(ListData<SociaxItem> list) {
        this.user = (ModelUser) list.get(0);
        Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    @Override
    public void addFooter(ListData<SociaxItem> list) {
        this.user = (ModelUser) list.get(0);
        notifyDataSetChanged();
    }

    @Override
    public void changeListData(ListData<SociaxItem> list) {
        if (list != null && list.size() == 1) {
            this.user = (ModelUser) list.get(0);
            notifyDataSetChanged();
        }
    }

    public void skipToBrowser(View v) {
//		Uri uri = Uri.parse((String) v.getTag());
//		Intent it = new Intent(Intent.ACTION_VIEW, uri);
//		context.startActivity(it);

        Uri uri = Uri.parse((String) v.getTag());
        Intent intent = new Intent(context, NetActivity.class);
        intent.putExtra("url", uri);
        context.startActivity(intent);
    }
}
