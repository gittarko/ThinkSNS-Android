package com.thinksns.tschat.unit;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.thinksns.tschat.constant.TSConfig;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtil {
	private static int compress = 100;

	public static boolean isHasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString() + "/";
	}

	public static String saveFilePaht(String picName)
			throws FileNotFoundException {

		File dir = new File(getSDPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String tmpFilePath = getSDPath() + picName;

		/*
		 * File tmpFile = new File(tmpFilePath); FileOutputStream fileOut = new
		 * FileOutputStream(tmpFile); btp.compress(CompressFormat.JPEG,
		 * compress, fileOut);
		 */
		return tmpFilePath;
	}

	/**
	 * 获取图片信息
	 *
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;

	}

	/**
	 * 图片旋转
	 *
	 * @param angle
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	// 将图片压缩到指定质量，该方法并不会压缩图片的实际像素
	// 当加载到内存依然是原图的大小
	public String saveFile(String picName, int options, Bitmap btp) {
		File dir = new File(getSDPath());

		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			String tmpFilePath = getSDPath() + "/" + TSConfig.CACHE_PATH + "/"
					+ picName;
			File tmpFile = new File(tmpFilePath);
			File parent = tmpFile.getParentFile();

			if (!parent.exists()) {
				parent.mkdirs();
			}

			FileOutputStream fileOut = new FileOutputStream(tmpFile);
			btp.compress(CompressFormat.JPEG, options, fileOut);

			return tmpFilePath;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 保存图片文件
	 *
	 * @param picName
	 * @param btp
	 * @return 路径
	 * @throws FileNotFoundException
	 */
	public String saveFile(String picName, Bitmap btp)
			throws FileNotFoundException {

		File dir = new File(getSDPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			String tmpFilePath = getSDPath() + "/" + TSConfig.CACHE_PATH + "/"
					+ picName;
			File tmpFile = new File(tmpFilePath);
			File parent = tmpFile.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			FileOutputStream fileOut = new FileOutputStream(tmpFile);
			btp.compress(CompressFormat.JPEG, compress, fileOut);
			return tmpFilePath;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 方法说明：保存文件到本地
	 *
	 * @param urlPath
	 * @param fileName
	 * @param savePath
	 * @return 图片路径
	 */
	public boolean saveUrlImg(String urlPath, String fileName, String savePath) {
		File saveFile = new File(savePath);
		if (!saveFile.exists()) {
			saveFile.mkdirs();
		}
		try {
			URL url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn.getResponseCode() == 200) {
				InputStream in = conn.getInputStream();
				OutputStream bos = new FileOutputStream(savePath + "/"
						+ fileName);

				byte[] buff = new byte[1024];
				int len = 0;
				while ((len = in.read(buff)) != -1) {
					bos.write(buff, 0, len);
				}
				bos.flush();
				bos.close();
				in.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean saveImage(String picName, Bitmap bit)
			throws FileNotFoundException {
		File dir = new File(getSDPath() + "/" + TSConfig.CACHE_PATH + "/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		boolean result = false;
		String tmpFilePath = getSDPath() + "/" +TSConfig.CACHE_PATH + "/"
				+ picName;
		File tmpFile = new File(tmpFilePath);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(tmpFile));
		result = bit.compress(CompressFormat.JPEG, compress, bos);
		try {
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
								: Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 把图片变成圆角
	 * 
	 * @param bitmap
	 *            需要修改的图片
	 * @param pixels
	 *            圆角的弧度
	 * @return 圆角图片
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 使圆角功能支持BitampDrawable
	 * 
	 * @param bitmapDrawable
	 * @param pixels
	 * @return
	 */
	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
			int pixels) {
		Bitmap bitmap = bitmapDrawable.getBitmap();
		bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
		return bitmapDrawable;
	}

	/**
	 * gif图片透明
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap makeGifTransparent(Context context, int resId) {
		if (context == null) {
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
		return makeGifTransparent(bitmap);
	}

	/**
	 * 处理gif图片像素
	 * @param src
	 * @param color
	 * @return
	 */
	private static Bitmap eraseBG(Bitmap src, int color) {
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
		b.setHasAlpha(true);

		int[] pixels = new int[width * height];
		src.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int i = 0; i < width * height; i++) {
			if (pixels[i] == color) {
				pixels[i] = 0;
			}
		}

		b.setPixels(pixels, 0, width, 0, 0, width, height);

		return b;
	}

	/**
	 * gif图片透明
	 * @param bitmap
	 * @return
	 */
	public static Bitmap makeGifTransparent(Bitmap bitmap) {
		bitmap = eraseBG(bitmap, -1);
		bitmap = eraseBG(bitmap, -16777216);
		return bitmap;
	}


	public static Uri pathToUri(Context context, String path) {
		if (path != null) {
			path = Uri.decode(path);
			ContentResolver cr = context.getContentResolver();
			StringBuffer buff = new StringBuffer();
			buff.append("(")
					.append(MediaStore.Images.ImageColumns.DATA)
					.append("=")
					.append("'" + path + "'")
					.append(")");
			Cursor cur = cr.query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new String[] { MediaStore.Images.ImageColumns._ID },
					buff.toString(), null, null);
			int index = 0;
			for (cur.moveToFirst(); !cur.isAfterLast(); cur
					.moveToNext()) {
				index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
				// set _id value
				index = cur.getInt(index);
			}
			if (index == 0) {
				//do nothing
			} else {
				Uri uri_temp = Uri
						.parse("content://media/external/images/media/"
								+ index);
				return uri_temp;
			}
		}
		return null;
	}

	public static String uriToPath(Activity context, Uri contentUri) {
		Cursor cursor = null;
		String result = contentUri.toString();
		String[] proj = {MediaStore.MediaColumns.DATA};
		cursor = context.managedQuery(contentUri, proj, null, null, null);
		if (cursor == null)
			throw new NullPointerException("reader file field");
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
			cursor.moveToFirst();
			// 最后根据索引值获取图片路径
			result = cursor.getString(column_index);
			try {
				// 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
				if (Integer.parseInt(Build.VERSION.SDK) < 14) {
					cursor.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}


}
