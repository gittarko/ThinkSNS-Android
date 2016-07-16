package com.thinksns.sociax.thinksnsbase.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.thinksns.sociax.thinksnsbase.R;
import com.thinksns.sociax.thinksnsbase.base.BaseApplication;

/**
 * Created by hedong on 16/4/7.
 */
public class NameClickableSpan extends ClickableSpan implements View.OnClickListener {
    private final ISpanClick mListener;
    private int mPosition;

    public NameClickableSpan(ISpanClick click, int position) {
        mListener = click;
        this.mPosition = position;
    }

    @Override
    public void onClick(View widget) {
        mListener.onClick(mPosition);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        int color = BaseApplication.getContext().getResources().getColor(R.color.clickspan_color);
        ds.setColor(color);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}
