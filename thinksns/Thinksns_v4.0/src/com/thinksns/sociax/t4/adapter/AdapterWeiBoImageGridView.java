package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.img.UIImageLoader;
import com.thinksns.sociax.t4.android.video.ConvertToUtils;
import com.thinksns.sociax.t4.model.ModelImageAttach;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.unit.UnitSociax;

import java.util.List;

public class AdapterWeiBoImageGridView extends BaseAdapter {

	Context mContext;
	List<ModelPhoto> mPhotoList;
	private int singleWidth = 0;
	private int singleHeight = 0;
	private int gridViewWidth = 0;

	public AdapterWeiBoImageGridView(Context context,List<ModelPhoto> photoList, int gridViewWidth) {
        mContext = context;
        mPhotoList = photoList;
		this.gridViewWidth = gridViewWidth;
	}

	public void setSingleImageWidthHeight(String width, String height) {
		this.singleWidth = Integer.parseInt(width);
		this.singleHeight = Integer.parseInt(height);
	}

	@Override
	public int getCount() {
		//最多显示9张图片
		if(mPhotoList.size() > 9)
			return 9;
		return mPhotoList.size();
	}

	@Override
	public int getItemViewType(int position) {
		if(getCount() == 1
				&& singleWidth != 0
				&& singleHeight != 0) {
			//如果设置了单张图片的宽高则加载单张图片布局
			return 1;
		}else {
			return 2;
		}
	}

	@Override
	public ModelPhoto getItem(int position) {
		return mPhotoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	@Override
	public View getView(int position, View covertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (covertView == null || covertView.getTag() == null) {
			holder = new ViewHolder();
			if(getItemViewType(position) == 2) {
				covertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview_home_pic, null);
				holder.ll_parent = (LinearLayout) covertView.findViewById(R.id.ll_parent);
				holder.iv_pic = (ImageView) covertView.findViewById(R.id.iv_pic);
			}else {
				covertView = new LinearLayout(parent.getContext());
				holder.iv_pic = (ImageView) getGridViewLayout(parent, singleWidth, singleHeight);
				((LinearLayout)covertView).addView(holder.iv_pic);
			}

			covertView.setTag(holder);
		} else {
			holder = (ViewHolder) covertView.getTag();
		}

		ModelPhoto modelPhoto = mPhotoList.get(position);
		String url = "";
		if(getCount() > 0) {
			//多图用中等缩略图展示
			if (modelPhoto.getMiddleUrl() != null) {
				url = modelPhoto.getMiddleUrl();
			}
		}else {
			if (modelPhoto.getUrl() != null) {
				url = modelPhoto.getUrl();
			}
		}

		Glide.with(mContext).load(url).crossFade()
		.placeholder(R.drawable.default_image_small).into(holder.iv_pic);

		return covertView;
	}

	private View getGridViewLayout(View view, int imgWidth, int imgHeight) {
		//设置单张图片的宽高
		int maxImgWidth = gridViewWidth;
		if (imgWidth != 0 && imgHeight != 0) {
			imgWidth = maxImgWidth;
			if (imgWidth == 0)
				imgWidth = 960;     //设置一个默认宽度
			imgHeight = maxImgWidth / 16 * 9;
			Log.e("getGridViewSize", "width = " + imgWidth + ", heihgt = " + imgHeight);
		}
		ImageView imageView = new ImageView(view.getContext());
		imageView.setLayoutParams(new ViewGroup.LayoutParams(imgWidth, imgHeight));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		return imageView;
	}

	class ViewHolder {
		LinearLayout ll_parent;
		ImageView iv_pic;
	}
}
