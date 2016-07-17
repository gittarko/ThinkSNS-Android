package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.function.FunctionAdvertise;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelAds;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.util.ArrayList;

/**
 * 类说明：广告轮播adapter
 * @date 2014-12-11
 */
public class AdapterGalleryAds extends PagerAdapter {
    private Context mContext;
    private ListData<SociaxItem> mAds;
    private ArrayList<View> mImageDatas;
    private FunctionAdvertise.OnAdvertiseClickListener listener;

    public void setOnAdvertiseClistener(FunctionAdvertise.OnAdvertiseClickListener listener) {
        this.listener = listener;
    }

    public AdapterGalleryAds(Context context) {
        this.mContext = context;
        this.mAds = new ListData<SociaxItem>();
        mImageDatas = new ArrayList<View>();
    }

    public AdapterGalleryAds(Context context, ListData<SociaxItem> list) {
        this(context);
        this.mAds.addAll(list);
    }

    public void removeDatas() {
        this.mAds.clear();
    }

    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public int getRealCount() {
        return mAds.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if(getRealCount() == 0)
            return;
//        container.removeView((View)object);
    }

    public ModelAds getItem(int position) {
        return (ModelAds) this.mAds.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if(getRealCount() == 0)
            return null;
        int virtualPosition = position % getRealCount();
        ImageView adsView = null;
        if(virtualPosition >= mImageDatas.size()) {
            adsView = new ImageView(container.getContext());
            adsView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            adsView.setLayoutParams(params);
            mImageDatas.add(adsView);
        }else {
            adsView = (ImageView)mImageDatas.get(virtualPosition);
        }

        if(adsView.getParent() != null) {
            container.removeView(adsView);
        }

        final ModelAds ads = getItem(virtualPosition);
        Glide.with(Thinksns.getContext()).load(ads.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .placeholder(R.drawable.default_image_small)
                .error(R.drawable.default_image_small)
                .into(adsView);
        adsView.setTag(R.id.tag, ads);
        //设置图片点击事件
        adsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onClick(v);
            }
        });

        container.addView(adsView);

        return adsView;
    }

    public void addAds(ListData<SociaxItem> ads) {
        mAds.addAll(ads);
        this.notifyDataSetChanged();
    }
}
