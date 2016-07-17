package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api.GiftApi;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.GlideRoundTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelShopGift;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明：   全部礼物
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年9月21日
 */
public class AdapterShopGift extends AdapterSociaxList {

    private int page = 1;
    private String mCate;

    @Override
    public ModelShopGift getItem(int position) {
        return (ModelShopGift) this.list.get(position);
    }

    public AdapterShopGift(ThinksnsAbscractActivity context, ListData<SociaxItem> list, String cate) {
        super(context, list);
        this.mCate = cate;
        isHideFootToast = true;
    }

    public AdapterShopGift(FragmentSociax fragment, ListData<SociaxItem> list, String cate) {
        super(fragment, list);
        this.mCate = cate;
        isHideFootToast = true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderSociax holder;
        ModelShopGift modelGift = getItem(position);

        if (convertView == null) {
            holder = new HolderSociax();
            convertView = inflater.inflate(R.layout.list_item_allgifts, null);

            holder.iv_all_gift = (ImageView) convertView.findViewById(R.id.iv_all_gift);
            holder.tv_gift_name = (TextView) convertView.findViewById(R.id.tv_gift_name);
            holder.tv_gift_score = (TextView) convertView.findViewById(R.id.tv_gift_score);
            holder.tv_gift_surplus = (TextView) convertView.findViewById(R.id.tv_gift_surplus);

            convertView.setTag(holder);
        } else {
            holder = (HolderSociax) convertView.getTag();
        }
        convertView.setTag(R.id.gift, modelGift);

        Glide.with(context).load(modelGift.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideRoundTransform(context))
                .crossFade()
                .into(holder.iv_all_gift);

        holder.tv_gift_name.setText(modelGift.getName());
        holder.tv_gift_score.setText(modelGift.getScore());
        holder.tv_gift_surplus.setText("已有" + modelGift.getCount() + "人兑换");

        return convertView;
    }

    @Override
    public int getMaxid() {
        return 0;
    }

    @Override
    public ListData<SociaxItem> refreshHeader(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return super.refreshHeader(obj);
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        page = 1;
        getApiGift().getShopGift(page, PAGE_COUNT, mCate, httpListener);
        return null;
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        page++;
        getApiGift().getShopGift(page, PAGE_COUNT, mCate, httpListener);
        return null;
    }

    GiftApi getApiGift() {
        return thread.getApp().getApiGift();
    }
}
