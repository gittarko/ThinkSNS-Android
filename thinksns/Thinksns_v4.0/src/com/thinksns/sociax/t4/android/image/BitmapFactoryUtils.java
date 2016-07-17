package com.thinksns.sociax.t4.android.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 类说明：
 * 
 * @author ZhiShi
 * @date 2014-9-22
 * @version 1.0
 */
public class BitmapFactoryUtils {
	private static LruCache<String, Bitmap> mMemoryCache;

	public static LruCache<String, Bitmap> getLruCache() {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// 使用最大可用内存值的1/8作为缓存的大小。
		int cacheSize = maxMemory / 8;
		if (mMemoryCache == null) {
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// 重写此方法来衡量每张图片的大小，默认返回图片数量。
					return bitmap.getByteCount() / 1024;
				}
			};
		}
		return mMemoryCache;

	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getLruCache() != null) {
			getLruCache().put(key, bitmap);
		}
	}

	public static Bitmap getBitmapToMemoryCache(String key) {
		if (getLruCache() != null) {
			return getLruCache().get(key);
		}
		return null;
	}

	public static Bitmap getCompressBitmap(String url) {
		Bitmap bitmap = BitmapFactory.decodeFile(url);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
		int op = 100;
		if (outputStream.toByteArray().length / 1024 > 300) {
			outputStream.reset();
			op -= 10;
			bitmap.compress(Bitmap.CompressFormat.JPEG, op, outputStream);
		}
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				outputStream.toByteArray());
		Bitmap bitmap2 = BitmapFactory.decodeStream(inputStream);
		inputStream = null;
		outputStream = null;
		return bitmap2;

	}

	public static int getSimpleSize(BitmapFactory.Options options) {
		int width = options.outWidth;
		int hight = options.outHeight;
		float expectW = 400.0f;
		float expectH = 400.0f;
		int scale = 1;
		if (width > hight && width > expectW) {
			scale = (int) (width / expectW);
		} else if (hight > width && hight > expectH) {
			scale = (int) (hight / expectH);
		}
		return scale;

	}

	public static Bitmap getBitmap(String srcFile) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcFile, newOpts);
		newOpts.inSampleSize = getSimpleSize(newOpts);
		newOpts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(srcFile, newOpts);
		return bitmap;
	}

}
