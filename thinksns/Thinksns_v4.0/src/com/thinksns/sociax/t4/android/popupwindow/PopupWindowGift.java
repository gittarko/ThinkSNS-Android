package com.thinksns.sociax.t4.android.popupwindow;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.unit.UnitSociax;

public class PopupWindowGift extends PopupWindow implements RadioGroup.OnCheckedChangeListener {

    private Activity context;
    private View view;
    private RadioGroup rg;
    private RadioButton rb_all, rb_entity, rb_virtual;

    public PopupWindowGift(Activity context) {
        super(context);
        this.context = context;
        initPopupWindow();
    }

    private void initPopupWindow() {
        view = context.getLayoutInflater().inflate(R.layout.popup_gift, null);
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
        rb_all = (RadioButton)view.findViewById(R.id.tv_all_gift);
        rb_entity = (RadioButton)view.findViewById(R.id.tv_entity_gift);
        rb_virtual = (RadioButton)view.findViewById(R.id.tv_virtual_gift);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int checkedPaddingLeft = UnitSociax.dip2px(group.getContext(), 10);
        int uncheckedPaddingLeft = UnitSociax.dip2px(group.getContext(), 25);
        if(checkedId == R.id.tv_all_gift) {
            rb_all.setPadding(checkedPaddingLeft, rb_all.getPaddingTop(), rb_all.getPaddingRight(), rb_all.getPaddingBottom());
            rb_entity.setPadding(uncheckedPaddingLeft, rb_entity.getPaddingTop(), rb_entity.getPaddingRight(), rb_entity.getPaddingBottom());
            rb_virtual.setPadding(uncheckedPaddingLeft, rb_virtual.getPaddingTop(), rb_virtual.getPaddingRight(), rb_virtual.getPaddingBottom());
        }else if(checkedId == R.id.tv_entity_gift) {
            rb_all.setPadding(uncheckedPaddingLeft, rb_all.getPaddingTop(), rb_all.getPaddingRight(), rb_all.getPaddingBottom());
            rb_entity.setPadding(checkedPaddingLeft, rb_entity.getPaddingTop(), rb_entity.getPaddingRight(), rb_entity.getPaddingBottom());
            rb_virtual.setPadding(uncheckedPaddingLeft, rb_virtual.getPaddingTop(), rb_virtual.getPaddingRight(), rb_virtual.getPaddingBottom());
        }else if(checkedId == R.id.tv_virtual_gift) {
            rb_all.setPadding(uncheckedPaddingLeft, rb_all.getPaddingTop(), rb_all.getPaddingRight(), rb_all.getPaddingBottom());
            rb_entity.setPadding(uncheckedPaddingLeft, rb_entity.getPaddingTop(), rb_entity.getPaddingRight(), rb_entity.getPaddingBottom());
            rb_virtual.setPadding(checkedPaddingLeft, rb_virtual.getPaddingTop(), rb_virtual.getPaddingRight(), rb_virtual.getPaddingBottom());
        }
    }

    public interface OnGiftItemClickListener {
        View.OnClickListener onAllGiftClick();
        View.OnClickListener onEntityGiftClick();
        View.OnClickListener onVirtualGiftClick();
    }

    public void setListener(OnGiftItemClickListener listener) {
        view.findViewById(R.id.tv_all_gift).setOnClickListener(listener.onAllGiftClick());
        view.findViewById(R.id.tv_entity_gift).setOnClickListener(listener.onEntityGiftClick());
        view.findViewById(R.id.tv_virtual_gift).setOnClickListener(listener.onVirtualGiftClick());
    }
}
