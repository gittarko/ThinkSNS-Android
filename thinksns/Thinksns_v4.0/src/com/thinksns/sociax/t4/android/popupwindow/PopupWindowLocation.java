package com.thinksns.sociax.t4.android.popupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.thinksns.sociax.android.R;

public class PopupWindowLocation extends PopupWindow {

    private Context context;
    private View parentView;
    private TextView item_relocation, item_del_location, item_cancel;
    private OnLocationClickListener listener;

    public PopupWindowLocation(Context context, View parentView) {
        super(context);
        this.context = context;
        this.parentView = parentView;

        initPopupWindow();
    }

    private void initPopupWindow() {
        View view = View.inflate(context, R.layout.pupupwindow_location, null);
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_ins));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.getBackground().setAlpha(160);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);

        initAboutOption(view);
    }

    private void initAboutOption(View view) {
        item_relocation = (TextView) view.findViewById(R.id.item_relocation);
        item_del_location = (TextView) view.findViewById(R.id.item_del_location);
        item_cancel = (TextView) view.findViewById(R.id.item_cancel);

        item_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        item_relocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onReLocationClick();
                }
                if (isShowing()) {
                    dismiss();
                }
            }
        });

        item_del_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelLocationClick();
                }
                if (isShowing()) {
                    dismiss();
                }
            }
        });
    }

    public void setListener(OnLocationClickListener listener) {
        this.listener = listener;
    }

    public void show() {
        if (!isShowing()) {
            showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        }
    }

    public interface OnLocationClickListener {
        void onReLocationClick();

        void onDelLocationClick();
    }

}
