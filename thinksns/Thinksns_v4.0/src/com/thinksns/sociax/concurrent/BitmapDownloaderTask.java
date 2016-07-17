package com.thinksns.sociax.concurrent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.client.ClientProtocolException;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.modle.ApproveSite;
import com.thinksns.sociax.net.Get;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.exception.HostNotFindException;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.unit.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	private String url;
	private final WeakReference<ImageView> imageViewReference;
	private Context mContext;

	public static enum Type {
		FACE, THUMB, MIDDLE_THUMB, LARGE_THUMB, LOGO, OTHER
	}

	private Type type;

	public BitmapDownloaderTask(ImageView imageView, Type type) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.type = type;
	}
	public BitmapDownloaderTask(ImageView imageView, Type type,Context context) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.type = type;
		this.mContext=context;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		try {
			Bitmap result = downloadBitmap(params[0]);
			return result;
		} catch (HostNotFindException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private Bitmap downloadBitmap(String url) throws HostNotFindException,
			ClientProtocolException, IOException {
		Get get = new Get(url);
		return get.download();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (this.isCancelled()) {
			result = null;
		}
		if (imageViewReference != null && result != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				switch (this.type) {
				case FACE:
					result = ImageUtil.toRoundCorner(result, 0);
					// SociaxItem temp = (SociaxItem)imageView.getTag();
					/*
					 * if(temp != null) temp.setHeader(result);
					 */
					break;
				case THUMB:
					ModelWeibo temp2 = (ModelWeibo) imageView.getTag();

					// temp2.setThumb(result);
					break;
				case MIDDLE_THUMB:
					ModelWeibo temp3 = (ModelWeibo) imageView.getTag();
					// temp3.setThumbMiddle(result);
					break;
				case LARGE_THUMB:
					ModelWeibo temp4 = (ModelWeibo) imageView.getTag();
					// temp4.setThumbLarge(result);
					break;
				case LOGO:
					ApproveSite site = (ApproveSite) imageView.getTag();
					/*
					 * if(result ==null){ break; }
					 */
					result = ImageUtil.toRoundCorner(result, 0);
					site.setLogoUrl(result);
					break;
				}

//				imageView.setImageBitmap(result);
				
				//将bitmap转换为glide能用的byte数组
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				result.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				
				Glide.with(mContext).load(byteArray)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(mContext))
				.crossFade()
				.into(imageView);
				
			}
		}
	}
}
