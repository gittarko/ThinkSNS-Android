package com.thinksns.sociax.t4.android.popupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.interfaces.OnPopupWindowClickListener;

/**
 * 类说明： 公共的弹出窗口
 *
 * @author wz
 * @version 1.0
 * @date 2014-12-1
 */
public class PopupWindowChangeBg extends PopupWindow {
    public OnPopupWindowClickListener listner;
    private Context context;
    private TextView bt_first, bt_second, tv_tips;
    private String tips;
    private View parent;

    public PopupWindowChangeBg(Context mContext, View parent, String tips) {
        this(mContext, parent, tips, "确定", "再想想");
    }


    /**
     * 各种页面弹出窗口
     *
     * @param mContext
     * @param parent
     * @param tips
     * @param bt1_str
     * @param bt2_str
     */
    public PopupWindowChangeBg(Context mContext, View parent, String tips,
                               String bt1_str, String bt2_str) {
        this.context = mContext;
        this.context = mContext;
        this.parent = parent;
        this.tips = tips;

        initPopupWindow(bt1_str, bt2_str);
    }

    private void initPopupWindow(String bt1_str, String bt2_str) {
        View view = View.inflate(context, R.layout.pupupwindow_change_bg, null);
        view.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_ins));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.getBackground().setAlpha(160);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        update();
        tv_tips = (TextView) view.findViewById(R.id.item_popupwindows_camera);
        bt_first = (TextView) view.findViewById(R.id.item_popupwindows_Photo);
        bt_second = (TextView) view.findViewById(R.id.item_popupwindows_cancel);

        tv_tips.setText(tips);
        bt_first.setText(bt1_str);
        bt_second.setText(bt2_str);

        bt_first.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (listner != null)
                    listner.firstButtonClick();
                dismiss();
            }
        });
        bt_second.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (listner != null)
                    listner.secondButtonClick();
                dismiss();
            }
        });
        ((View) ll_popup.getParent()).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOnPopupWindowClickListener(OnPopupWindowClickListener listner) {
        this.listner = listner;
    }

    public void show() {
        if (!isShowing()) {
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }
}