package com.thinksns.sociax.t4.android.popupwindow;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.unit.UnitSociax;

public class PopupWindowMyGIft extends PopupWindow implements RadioGroup.OnCheckedChangeListener {

    private Activity context;
    private View view;

    private RadioGroup rg;
    private RadioButton rb_send, rb_receive;

    public PopupWindowMyGIft(Activity context) {
        super(context);
        this.context = context;
        initPopupWindow();
    }

    private void initPopupWindow() {
        view = context.getLayoutInflater().inflate(R.layout.popup_my_gift, null);
        view.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_ins));
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        rg = (RadioGroup)view.findViewById(R.id.ll_popup);
        rg.setOnCheckedChangeListener(this);
        rb_send = (RadioButton)view.findViewById(R.id.tv_send_gift);
        rb_receive = (RadioButton)view.findViewById(R.id.tv_received_gift);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int checkedPaddingLeft = UnitSociax.dip2px(group.getContext(), 10);
        int uncheckedPaddingLeft = UnitSociax.dip2px(group.getContext(), 25);
        if(checkedId == R.id.tv_send_gift) {
            rb_send.setPadding(checkedPaddingLeft, rb_send.getPaddingTop(), rb_send.getPaddingRight(), rb_send.getPaddingBottom());
            rb_receive.setPadding(uncheckedPaddingLeft, rb_receive.getPaddingTop(),
                    rb_receive.getPaddingRight(), rb_receive.getPaddingBottom());
        }else if(checkedId == R.id.tv_received_gift) {
            rb_send.setPadding(uncheckedPaddingLeft, rb_send.getPaddingTop(),
                    rb_send.getPaddingRight(), rb_send.getPaddingBottom());
            rb_receive.setPadding(checkedPaddingLeft, rb_receive.getPaddingTop(), rb_receive.getPaddingRight(),
                    rb_receive.getPaddingBottom());

        }
    }

    public interface OnMyGiftItemClickListener {
        View.OnClickListener onSendGiftClick();
        View.OnClickListener onReceivedGiftClick();
    }

    public void setListener(OnMyGiftItemClickListener listener) {
        view.findViewById(R.id.tv_send_gift).setOnClickListener(listener.onSendGiftClick());
        view.findViewById(R.id.tv_received_gift).setOnClickListener(listener.onReceivedGiftClick());
    }
}
