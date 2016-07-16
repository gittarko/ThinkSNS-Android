package com.thinksns.tschat.unit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bimp {
	public static int max = 0;
	public static boolean act_bool = true;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();

	// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
	public static List<String> address = new ArrayList<String>();

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
//				in = new BufferedInputStream(
//						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeFile(path, options);
				break;
			}
			i += 1;
		}

		return bitmap;
	}

	// 把本地图片转换为bitmap
	/**
	 * use to lessen pic 50%
	 * 
	 * @param path  sd card path
	 * @return bitmap
	 */
	public final static Bitmap getBitMapFromLocal(String path) {
		Bitmap bitmap = compressFile2Bitmap(path);
        return compressImage(bitmap); //压缩好比例大小后再进行质量压缩
	}

	/**
	 * 缩小本地图片
	 * @param path 本地图片路径
	 * @return
     */
	private static Bitmap compressFile2Bitmap(String path) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path,newOpts);	//此时返回bm为空
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;

		float hh = 1920;
		float ww = 1080;

		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;		//be=1	表示不缩放
		if (w > h && w > ww) {
			//如果宽度大的话根据宽度固定大小缩放
			be = (int) (w / ww);
		} else if (w < h && h > hh) {
			//如果高度高的话根据宽度固定大小缩放
			be = (int) (h / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be; //设置缩放比例
		newOpts.inJustDecodeBounds = false;
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		return BitmapFactory.decodeFile(path, newOpts);
	}

	//压缩一张bitmap
	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream outputStream = compressBitmap2Stream(image);
		ByteArrayInputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
        return BitmapFactory.decodeStream(stream, null, null);	//把ByteArrayInputStream数据生成图片
    }

	//将一张bitma压缩并解析成inputstream
	private static ByteArrayOutputStream compressBitmap2Stream(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);	//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 500) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();		//重置baos即清空baos
			options -= 5;		//每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
		}

		return baos;
	}

	//压缩一张本地图片

	/**
	 *
	 * @param src  源图片
	 * @param dest 目标图片地址
     * @return
     */
	public static void compressUploadFile(String src, String dest) throws FileNotFoundException {
		Bitmap bitmap = getBitMapFromLocal(src);
		ByteArrayOutputStream outputStream = compressBitmap2Stream(bitmap);
		OutputStream f2 = new FileOutputStream(dest);
		try {
			outputStream.writeTo(f2);
			f2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ;
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
		if (picWidth!=0 && picheight!=0) {
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
		float windowWidth = TDevice.getScreenWidth(context);
		final int maxWidth = (int)windowWidth/3;
		int maxHeight = maxWidth;
		
		//拿到图片的宽度的高度
		float picWidth = (Bimp.getBitMapFromLocal(path)).getWidth();
		float picheight = (Bimp.getBitMapFromLocal(path)).getHeight();
		float scale = picWidth / picheight;
			
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
