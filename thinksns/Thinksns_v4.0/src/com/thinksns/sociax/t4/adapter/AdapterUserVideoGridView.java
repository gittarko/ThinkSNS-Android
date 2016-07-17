package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.img.UIImageLoader;
import com.thinksns.sociax.t4.android.video.ActivityVideoDetail;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.model.ModelUserPhoto;
import com.thinksns.sociax.t4.model.ModelVideo;

import java.util.ArrayList;
import java.util.List;

public class AdapterUserVideoGridView extends BaseAdapter {

    Context mContext;
    List<ModelVideo> mVideoList;

    public AdapterUserVideoGridView(Context context, List<ModelVideo> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    public int getMaxId() {
        if(mVideoList == null || mVideoList.size() == 0)
            return 0;
        return mVideoList.get(mVideoList.size()-1).getId();
    }

    public void setData(List<ModelVideo> datas) {
        if(mVideoList == null)
            mVideoList = new ArrayList<ModelVideo>();
        else
            this.mVideoList.clear();
        this.mVideoList.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(List<ModelVideo> datas) {
        if(mVideoList == null)
            mVideoList = new ArrayList<ModelVideo>();
        this.mVideoList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public ModelVideo getItem(int position) {
        return mVideoList.get(position);
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
            covertView = LayoutInflater.from(mContext).inflate(R.layout.item_video, null);
            holder.iv_pic = (ImageView) covertView.findViewById(R.id.img_vedio);
            covertView.setTag(holder);
        } else {
            holder = (ViewHolder) covertView.getTag();
        }

        covertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, NetActivity.class);
//                intent.putExtra("url", getItem(position).getVideoDetail());
//                mContext.startActivity(intent);

                ModelVideo video=getItem(position);

                Intent intent;
                String videoUrl, videoImgUrl = "";
                int video_id=video.getVideo_id();
                if (video_id!=0) {
                    intent = new Intent(mContext, ActivityVideoDetail.class);
                    videoUrl = video.getVideoDetail();
                    videoImgUrl = video.getVideoImgUrl();
                } else {
                    intent = new Intent(mContext, NetActivity.class);
                    videoUrl =video.getVideoPart();
                }
                intent.putExtra("url", videoUrl);
                intent.putExtra("preview_url", videoImgUrl);
                mContext.startActivity(intent);
            }
        });

        ModelVideo modelPhoto = mVideoList.get(position);
        if (modelPhoto.getVideoImgUrl() != null)
            UIImageLoader.getInstance(parent.getContext()).displayImage(modelPhoto.getVideoImgUrl(), holder.iv_pic);

        return covertView;
    }

    class ViewHolder {
        ImageView iv_pic;
    }
}
