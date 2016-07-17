package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.GLDebugHelper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.model.ModelUserPhoto;
import com.thinksns.sociax.t4.unit.UnitSociax;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明：
 *
 * @author Zoey
 * @version 1.0
 * @date 2015-6-18
 */
public class AdapterUserImageGridView extends BaseAdapter {

    Context mContext;
    List<ModelUserPhoto> mPhotoList;
    List<ModelPhoto> photos;
    private int imgWidth;

    public AdapterUserImageGridView(Context context, List<ModelUserPhoto> photoList) {
        this(context, photoList, UnitSociax.getWindowWidth(context));
    }

    public AdapterUserImageGridView(Context context, List<ModelUserPhoto> photoList, int width) {
        mContext = context;
        mPhotoList = photoList;
        int totalImgWidth = width - UnitSociax.dip2px(mContext, 2) * 3;
        this.imgWidth = mPhotoList.size() == 2 ? totalImgWidth / 2 : totalImgWidth / 4;
        photos = new ArrayList<ModelPhoto>(photoList.size());
    }

    public int getMaxId() {
        if(mPhotoList == null || mPhotoList.size() == 0)
            return 0;
        return Integer.parseInt(mPhotoList.get(mPhotoList.size()-1).getImageId());
    }

    public void setData(List<ModelUserPhoto> datas) {
        if(mPhotoList == null)
            mPhotoList = new ArrayList<ModelUserPhoto>();
        else
            this.mPhotoList.clear();
        this.mPhotoList.addAll(datas);
        initPhotoList();
        notifyDataSetChanged();
    }

    public void addData(List<ModelUserPhoto> datas) {
        if(mPhotoList == null)
            mPhotoList = new ArrayList<ModelUserPhoto>();
        this.mPhotoList.addAll(datas);
        initPhotoList();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    public void initPhotoList() {
        photos.clear();
        for (int i = 0; i < mPhotoList.size(); i++) {
            ModelPhoto p = new ModelPhoto();
            p.setId(i);
            String url = mPhotoList.get(i).getImgUrl();
            p.setUrl(url);
            p.setMiddleUrl(url);
            p.setOriUrl(url);
            photos.add(p);
        }
    }

    @Override
    public ModelUserPhoto getItem(int position) {
        return mPhotoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View covertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (covertView == null) {
            holder = new ViewHolder();
            covertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview_home_pic, null);
            holder.iv_pic = (ImageView) covertView.findViewById(R.id.iv_pic);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.iv_pic.getLayoutParams();
            params.width = imgWidth;
            params.height = imgWidth;
            holder.iv_pic.setLayoutParams(params);

            covertView.setTag(holder);
        } else {
            holder = (ViewHolder) covertView.getTag();
        }

        holder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,
                        ActivityViewPager.class);
                i.putExtra("index", position);
                i.putParcelableArrayListExtra(
                        "photolist",
                        (ArrayList<? extends Parcelable>) photos);
                ActivityViewPager.imageSize = new ImageSize(imgWidth, imgWidth);
                mContext.startActivity(i);
            }
        });

        ModelUserPhoto modelPhoto = mPhotoList.get(position);
        if (modelPhoto.getImgUrl() != null) {
            Glide.with(mContext).load(modelPhoto.getImgUrl())
                    .crossFade().into(holder.iv_pic);
        }

        return covertView;
    }

    class ViewHolder {
        ImageView iv_pic;
    }
}
