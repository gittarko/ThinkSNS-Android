package com.thinksns.sociax.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.thinksns.sociax.modle.Document;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

public class SingleDownload {
	private Context mContext;
	private Document mDocument;

	public Activity loadActivity;

	/* 文件存放装路径 */
	private static final String savePath = "/sdcard/SociaxDocment/";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;

	private static final int DOWN_UPDATE = 1;

	private static final int DOWN_OVER = 2;

	private int progress;

	private String fileAbsolutePath = null;

	private Thread downLoadThread;

	private boolean interceptFlag = false;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setVisibility(View.VISIBLE);
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				// Intent intent = new Intent(mContext,MuPDFActivity.class);
				// if (fileAbsolutePath != null){
				// Uri uri = Uri.parse(fileAbsolutePath);
				// intent.setAction(Intent.ACTION_VIEW);
				// intent.setData(uri);}
				// mContext.startActivity(intent);
				// if(loadActivity !=null )loadActivity.finish();
				break;
			default:
				break;
			}
		};
	};

	public SingleDownload(Context context) {
		this.mContext = context;
	}

	public SingleDownload(Context context, Document document, ProgressBar bar) {
		this.mContext = context;
		this.mDocument = document;
		this.mProgress = bar;
	}

	/**
	 * 下载
	 * 
	 * @param url
	 */
	public void startDownload() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	// 外部接口让主Activity调用
	public void checkUpdateInfo() {
	}

	// 外部接口让主Activity调用
	public void checkUpdateInfo(int versionCode) {

	}

	public void stopDownload() {
		interceptFlag = true; // 取消设置为
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {

				String path = "http://download.thinksns.com/attach/ThinkSNS%E5%BC%80%E5%8F%91%E5%A4%A7%E8%B5%9B%E5%85%AC%E5%91%8A.pdf";
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String docName = savePath + mDocument.getdName().trim() + "."
						+ mDocument.getdType();
				File docFile = new File(docName);
				fileAbsolutePath = docFile.getAbsolutePath();
				FileOutputStream fos = new FileOutputStream(docFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

}