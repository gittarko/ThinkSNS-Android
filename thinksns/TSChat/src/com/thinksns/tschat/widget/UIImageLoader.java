package com.thinksns.tschat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.thinksns.tschat.R;

/** 
	 * 类说明：   
	 * @author  dong.he    
	 * @date    2015年11月16日
	 * @version 1.0
	 */
public class UIImageLoader {
	static ImageLoader mImageLoader = ImageLoader.getInstance();
	static UIImageLoader instance;
	
    @SuppressWarnings("deprecation")
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .considerExifParams(true)
//            .showImageForEmptyUri(R.drawable.default_image)
//            .showImageOnFail(R.drawable.default_image)
//            .showImageOnLoading(R.drawable.default_image)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    
    public static UIImageLoader getInstance(Context context) {
    	if(instance == null) {
    		instance = new UIImageLoader(context);
    	}
    	return instance;
    }
    
    public static ImageLoader getImageLoader() {
    	return mImageLoader;
    }
    
	UIImageLoader(Context context) {
		init(context);
	}
	
	private void init(Context context) {

        @SuppressWarnings("deprecation")
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(10 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        mImageLoader.init(config);
    }

    public void displayImage(final String urlOrPath, final ImageView imageView) {
        if (urlOrPath!=null){
            if (urlOrPath.indexOf("storage")!=-1 && urlOrPath.indexOf("emulated")!=-1) {
                mImageLoader.displayImage("file://"+ urlOrPath, imageView, options);
            }else {
                mImageLoader.displayImage(urlOrPath, imageView, options);
            }
        }
    }

    public void displayImage(final String urlOrPath, final ImageView imageView, ImageLoadingListener listener) {
        mImageLoader.displayImage(urlOrPath, imageView, options, listener);
    }


    public void loadImage(final String url, final ImageSize size, final SimpleImageLoadingListener listener) {
        mImageLoader.loadImage(url, size, options, listener);
    }

    public void resume() {
        mImageLoader.resume();
    }

    public void pause() {
        mImageLoader.pause();
    }
    
}
