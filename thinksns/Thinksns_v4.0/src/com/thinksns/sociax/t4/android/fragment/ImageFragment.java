package com.thinksns.sociax.t4.android.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;

import org.greenrobot.eventbus.EventBus;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by hedong on 16/3/2.
 */
public class ImageFragment extends Fragment{
    private static final String IMAGE_SMALL_URL = "image_small";
    private static final String IMAGE_ORI_URL = "image_ori";

    private ImageView image;
    private ImageView oriImage;
    private ProgressBar progress;

    private String imageSmallUrl;
    private String imageMiddleUrl;

    private PhotoViewAttacher mAttacher;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_SMALL_URL, param1);
        args.putString(IMAGE_ORI_URL, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //读取缩略图和原图地址
            this.imageSmallUrl = getArguments().getString(IMAGE_SMALL_URL);
            this.imageMiddleUrl = getArguments().getString(IMAGE_ORI_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_layout, container, false);
        image = (ImageView) view.findViewById(R.id.image);
        oriImage = (ImageView)view.findViewById(R.id.ori_image);
        progress = (ProgressBar)view.findViewById(R.id.progressBar);

        mAttacher = new PhotoViewAttacher(oriImage);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });

        ImageLoader.getInstance().displayImage(imageMiddleUrl, oriImage, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(imageSmallUrl != null && ActivityViewPager.imageSize != null) {
                    //获取内存中的缩略图
                    String memoryCacheKey = MemoryCacheUtils.generateKey(imageSmallUrl, ActivityViewPager.imageSize);
                    Bitmap bmp = ImageLoader.getInstance().getMemoryCache().get(memoryCacheKey);
                    if (bmp != null && !bmp.isRecycled()) {
                        image.setVisibility(View.VISIBLE);
                        image.setImageBitmap(bmp);
                    }
                }
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(loadedImage != null)
                    image.setVisibility(View.GONE);
                mAttacher.update();
                progress.setVisibility(View.GONE);
                image.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
                progress.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStop() {
        super.onStop();
    }
}
