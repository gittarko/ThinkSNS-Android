package com.thinksns.sociax.t4.android.img;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.thinksns.sociax.t4.unit.UnitSociax;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.util.Pair;

public class Bimp {
	public static int max = 0;
	public static boolean act_bool = true;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();
	public static boolean isOriginal = false;		//是否发送原图s

	// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
	public static ArrayList<String> address = new ArrayList<String>();

	public static void clear() {
		Bimp.bmp.clear();
		Bimp.address.clear();
		Bimp.max = 0;
		Bimp.isOriginal = false;
	}

	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	public static int[] getLocalImageSize(String path) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path,newOpts);  //此时返回bm为空
		int [] imageSize = new int[2];
		imageSize[0] = newOpts.outWidth;
		imageSize[1] = newOpts.outHeight;
		return imageSize;
	}

	public static Bitmap compressLocalImage(String path, int reqWidth, int reqHeight) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path,newOpts);//此时返回bm为空
		newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight);
		newOpts.inJustDecodeBounds = false;		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		return BitmapFactory.decodeFile(path, newOpts);
	}

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {

			//谷歌官方例子
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
			//网友例子
//
//			final int heightRatio = Math.round((float) height
//						/ (float) reqHeight);
//			final int widthRatio = Math.round((float) width / (float) reqWidth);
//			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//		if (width > height && width > reqWidth) {
//			//如果宽度大的话根据宽度固定大小缩放
//			inSampleSize = (int) (width / reqWidth);
//		} else if (width < height && height > reqHeight) {
//			//如果高度高的话根据宽度固定大小缩放
//			inSampleSize = (int) (height / reqHeight);
//		}
//
		if (inSampleSize <= 0)
			inSampleSize = 1;

		Log.e("Bimp->calculateInSampleSize", "图片压缩比例：" + inSampleSize);

		return inSampleSize;
	}

	/**
	 * 压缩本地图片
	 * @param path 本地图片路径
	 * @return bitmap
	 */
	public final static Bitmap getBitMapFromLocal(String path) {
		Bitmap bitmap = compressLocalImage(path, 1080, 1920);
		//旋转图片
		int degree = readPicDegree(path);
		if(degree > 0) {
			bitmap = rotateBitmap(degree, bitmap);
		}
        return compressImageToBitmap(bitmap);	//压缩好比例大小后再进行质量压缩
	}

	public final static InputStream getInputStreamFromLocal(String path, boolean isOriginal) {
		//如果图片大小低于500K或发送原图则不压缩
		Bitmap image = BitmapFactory.decodeFile(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if(isOriginal) {
			return new ByteArrayInputStream(baos.toByteArray());
		}
		if(baos.toByteArray().length < 500 || Bimp.isOriginal) {
			return new ByteArrayInputStream(baos.toByteArray());
		}

		Bitmap bitmap = compressLocalImage(path, 1080, 1920);
		//旋转图片
		int degree = readPicDegree(path);
		if(degree > 0) {
			bitmap = rotateBitmap(degree, bitmap);
		}
		return compressImageToStream(bitmap);
	}

	/**
	 * 压缩一张bitmap到500k以下
	 * @param image
	 * @return
     */
	private static Bitmap compressImageToBitmap(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024 > 500) {
			//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();		//重置baos即清空baos
            options -= 5;		//每次都减少5
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩option5%，把压缩后的数据存放到baos中
        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        return  BitmapFactory.decodeStream(isBm, null, null);		//把ByteArrayInputStream数据生成图片
    }


	/**
	 * 通过ExifInterface类读取图片文件的被旋转角度
	 * @param path ： 图片文件的路径
	 * @return 图片文件的被旋转角度
	 */
	public static int readPicDegree(String path) {
		int degree = 0;

		// 读取图片文件信息的类ExifInterface
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (exif != null) {
			int orientation = exif.getAttributeInt(
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
		}

		return degree;
	}

	/**
	 * 将图片纠正到正确方向
	 *
	 * @param degree ： 图片被系统旋转的角度
	 * @param bitmap ： 需纠正方向的图片
	 * @return 纠向后的图片
	 */
	public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);

		Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return bm;
	}

	private static InputStream compressImageToStream(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while ( baos.toByteArray().length / 1024 > 500) {
			//循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();		//重置baos即清空baos
			options -= 5;		//每次都减少5
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
		}
		//把压缩后的数据baos存放到ByteArrayInputStream中
		return new ByteArrayInputStream(baos.toByteArray());
	}

	// 把网络图片转换为bitmap
	/**
	 * return a bitmap from service
	 * 
	 * @param url
	 * @return bitmap type
	 */
	public final static Bitmap getBitMapFromService(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;

		try {
			myFileUrl = new URL(url);
			HttpURLConnection conn;
			conn = (HttpURLConnection) myFileUrl.openConnection();

			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	//通过路径获取到bitmap对象
	public static Bitmap getBitmap(String path){
		    Bitmap bitmap = null;  
	        InputStream in = null;  
	        BufferedOutputStream out = null;  
	        try {  
	            in = new BufferedInputStream(new URL(path).openStream(), 1024);  
	            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();  
	            out = new BufferedOutputStream(dataStream,1024);  
	            copy(in, out);  
	            out.flush();  
	            byte[] data = dataStream.toByteArray();  
	            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
	            data = null;  
	            return bitmap;  
	        }catch (IOException e){  
	            e.printStackTrace();  
	            return null;  
	        }  
	}
	
	//按比例显示图片，聊天
	public static Pair<Float, Float> getScaleImgWH(float picWidth,float picheight ,int maxWidth,int maxHeight,float scale){
		if (picWidth!=0&&picheight!=0) {
			if (picWidth>=maxWidth&&scale>=1) {
				picWidth=maxWidth;
				picheight=(int) (picWidth/scale);
			}else if (picheight>=maxHeight&&scale<1) {
				picheight=maxHeight;
				picWidth=(int) (picheight*scale);
			}
		}
		return Pair.create(picWidth,picheight);
	}
	
	//动态获取本地图片的宽高
	public static Pair<Float, Float> getLocalPicWH(final String path,Context context){
		//获取屏幕宽度
		int windowWidth=UnitSociax.getWindowWidth(context);
		final int maxWidth=(windowWidth*2)/5;//设置图片的最大宽度为屏幕的2/5
		int maxHeight=maxWidth;//设置图片的最大高度为屏幕的2/5
		
		//拿到图片的宽度的高度
		float picWidth=(Bimp.getBitMapFromLocal(path)).getWidth();
		float picheight=(Bimp.getBitMapFromLocal(path)).getHeight();
		float scale=picWidth/picheight;
			
		picWidth=(Bimp.getScaleImgWH(picWidth,picheight,maxWidth,maxHeight,scale)).first;
		picheight=(Bimp.getScaleImgWH(picWidth,picheight,maxWidth,maxHeight,scale)).second;
		
		return Pair.create(picWidth,picheight);
	}
	
	private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
	
	public static String getImageListToString() {
		String str = "";
		if (address != null) {
			for (int i = 0; i < address.size(); i++) {
				str += address.get(i) + ",";
			}
			if (str.contains(","))
				str = str.substring(0, str.lastIndexOf(","));
		}
		return str;
	}
}
