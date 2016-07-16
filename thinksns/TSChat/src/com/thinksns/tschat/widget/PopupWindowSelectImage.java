package com.thinksns.tschat.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import com.thinksns.tschat.R;
import com.thinksns.tschat.constant.TSConfig;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 类说明：选择拍照
 *
 * @author PC
 * @version 1.0
 * @date 2014-9-11
 */
public class PopupWindowSelectImage extends PopupWindow {
    SelectImageListener listener;
    private String path = "";// 图片地址

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private Context context;

    /**
     * 在页面下方弹出选择拍照
     *
     * @param mContext
     * @param parent
     */
    public PopupWindowSelectImage(Activity mContext, View parent,
                                  SelectImageListener listener) {
        this.listener = listener;
        View view = View.inflate(mContext, R.layout.popupwindows_selectimg,
                null);
        this.context = mContext;
        view.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_ins));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_bottom_in_2));
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        update();
        Button bt_fromCamera = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt_fromLocal = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt_cancle = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        bt_fromCamera.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                takePhoto((Activity)context);
                dismiss();
            }
        });
        bt_fromLocal.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent getImage = new Intent(context, MultiImageSelectorActivity.class);
                getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
                ((Activity)context).startActivityForResult(getImage, TSConfig.LOCAL_IMAGE);
                dismiss();
            }
        });

        bt_cancle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 在页面下方弹出选择拍照
     *
     * @param mContext
     * @param parent
     */
    public PopupWindowSelectImage(Activity mContext, View parent) {
        View view = View.inflate(mContext, R.layout.popupwindows_selectimg,
                null);
        this.context = mContext;
        view.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_ins));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_bottom_in_2));
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        update();
        Button bt_fromCamera = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt_fromLocal = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt_cancle = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        bt_fromCamera.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                takePhoto((Activity)context);
                dismiss();
            }
        });
//        bt_fromLocal.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(context, MultilPicActivity.class);
//                context.startActivityForResult(intent, StaticInApp.LOCAL_IMAGE);
//                dismiss();
//            }
//        });
        bt_cancle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 启动拍照
     *
     * @param context
     */
    public void takePhoto(Activity context) {
        listener.cameraImage();
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        listener.startPhotoZoom(uri);
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        String result = contentUri.toString();
        String[] proj = {MediaColumns.DATA};
        cursor = ((Activity)context).managedQuery(contentUri, proj, null, null, null);
        if (cursor == null)
            throw new NullPointerException("reader file field");
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            // 最后根据索引值获取图片路径
            result = cursor.getString(column_index);
            try {
                // 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
                if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e("getFralPathFromUrlErr", "error:" + e);
            }
        }
        return result;
    }
}
