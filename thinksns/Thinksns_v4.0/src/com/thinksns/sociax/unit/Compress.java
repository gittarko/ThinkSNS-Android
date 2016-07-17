package com.thinksns.sociax.unit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

/**
 * 图片压缩
 */
public class Compress {

	private static final String TAG = "Compress";

	public static InputStream compressPic(File file) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; 	// 返回bm为空
		// 获取这个图片的宽和高
		BitmapFactory.decodeFile(file.getPath(), options);// 此时返回bm为空
		Bitmap localBitmap = null;
		for (int i = 0; ; i++) {
			if ((options.outWidth >> i > 1024)
					|| (options.outHeight >> i > 1024))
				continue;
			int j = i;
			options.inSampleSize = (int) Math.pow(2.0D, j);
			options.inJustDecodeBounds = false;
			localBitmap = BitmapFactory.decodeFile(file.getPath(), options);

			if (localBitmap != null)
				break;
			Log.e(TAG, "Bitmap decode error!");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		localBitmap.recycle();
		return new ByteArrayInputStream(baos.toByteArray());
	}

	public static InputStream compressPic(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}

	private static final int DEFAULT_REQUIRED_SIZE = 70;
	private static final int size = 100;

	public static Bitmap compressPicToBitmap(File file) throws Exception {
		try {
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(file);
			BitmapFactory.decodeStream(stream1, null, option);
			stream1.close();
			final int REQUIRED_SIZE = size > 0 ? size : DEFAULT_REQUIRED_SIZE;
			int width_tmp = option.outWidth, height_tmp = option.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			if (scale >= 2) {
				scale /= 2;
			}
			BitmapFactory.Options option2 = new BitmapFactory.Options();
			option2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(file);
			Bitmap bm = BitmapFactory.decodeStream(stream2, null, option2);
			stream2.close();

			//获取图片旋转的角度，然后给它旋转回来
			int degree = ImageUtil.readPictureDegree(file.getAbsolutePath());
			//根据指定旋转度数进行图片旋转
			Bitmap bitmap = ImageUtil.rotaingImageView(degree, bm);

			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 *
	 * @param image  图片源
	 * @param size	 压缩后大小,单位kb
     */
	public static Bitmap compressBitmap(Bitmap image, int size) {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100,out);
		long length = out.toByteArray().length / 1024;	//读出图片的kb大小
		if(length <= size) {
			return null;
		}
		Log.e("Compress", "压缩前大小:" + length + "kb");
		int options = 90;
		while(out.toByteArray().length > (size * 1024))
		{
			Log.e("Compress", "压缩中:" + out.toByteArray().length);
			out.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, out);
			options -= 10;
		}
		Log.e("Compress", "压缩后:" + out.toByteArray().length);
		//把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(out.toByteArray());
		Bitmap result = BitmapFactory.decodeStream(isBm, null, null);  //把ByteArrayInputStream数据生成图片

		return result;
	}

	//压缩本地图片
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
														 int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 压缩bitmap，使用采样率压缩
	 * @param src
	 * @param regWidth
	 * @param reqHeight
     * @return
     */
	public static Bitmap decodeBitmap(Bitmap src, int regWidth, int reqHeight, int size) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		src.compress(Bitmap.CompressFormat.JPEG, 100, out);
		if(out.toByteArray().length / 1024 > 1024) {
			//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			out.reset();//重置baos即清空baos
			src.compress(Bitmap.CompressFormat.JPEG, 50, out);

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(out.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap dest = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		//计算图片缩放比例
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;

		newOpts.inSampleSize = calculateInSampleSize(w, h, regWidth, reqHeight);
		//重新读入图片
		isBm = new ByteArrayInputStream(out.toByteArray());
		dest = BitmapFactory.decodeStream(isBm, null, newOpts);

		Bitmap result = compressBitmap(dest, size);
		if(result == null)
			return dest;
		else
			return result;

	}

	/**
	 * 谷歌官方图片处理
	 * @param oriWidth
	 * @param oriHeight
	 * @param reqWidth
	 * @param reqHeight
     * @return
     */
	//计算图片缩放比
	public static int calculateInSampleSize(int oriWidth, int oriHeight, int reqWidth, int reqHeight) {
		int inSampleSize = 1;

		if (oriHeight > reqHeight || oriWidth > reqWidth) {

			final int halfHeight = oriHeight / 2;
			final int halfWidth = oriWidth / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

}