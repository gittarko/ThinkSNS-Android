package com.thinksns.sociax.t4.android.video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class HttpDownloader extends Thread {
	private static final String TAG = "TSTAG_HttpDownloader";
	private String mUrl = null;
	private String mTempPathString = null;
	private String mFilePathString = null;
	private boolean mCanceled = false;
	private Activity mActivity;
	private HttpDownloaderCallback mCallback = null;

	private void onError(final String errorMessage) {
		File file = new File(mTempPathString);
		if (file.exists())
			file.delete();

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mCallback.onDownloadFailed(errorMessage);
			}
		});
	}

	public interface HttpDownloaderCallback {
		public void onProgressUpdate(float progress);

		public void onDownloadSuccessed(String savedPath);

		public void onDownloadFailed(String errorReason);
	}

	public HttpDownloader(Context context, String fromUrl, String toFilePath,
			HttpDownloaderCallback httpDownloaderCallback) {
		Log.v(TAG, "fromUrl="+fromUrl+"  toFilePath="+toFilePath);
		this.mUrl = fromUrl;
		this.mFilePathString = toFilePath;
		this.mActivity = (Activity) context;
		this.mCallback = httpDownloaderCallback;
		this.mTempPathString = toFilePath + System.currentTimeMillis();
	}

	public void cancel(boolean canceled) {
		this.mCanceled = canceled;
	}

	@SuppressWarnings("resource")
	public void run() {
		URL url = null;
		HttpURLConnection connection = null;
		int fileLength;
		byte data[] = new byte[4096];
		long total = 0;
		int count;
		InputStream input = null;
		OutputStream output = null;

		try {
			url = new URL(mUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.e("nat",
						"Server returned HTTP " + connection.getResponseCode()
								+ " " + connection.getResponseMessage());
				onError("Server returned HTTP " + connection.getResponseCode()
						+ " " + connection.getResponseMessage());
				return;
			}

			fileLength = connection.getContentLength();
			input = connection.getInputStream();
			output = new FileOutputStream(mTempPathString);
			while ((count = input.read(data)) != -1) {
				if (mCanceled == true) {
					input.close();

					try {
						if (output != null)
							output.close();
						if (input != null)
							input.close();
					} catch (IOException ignored) {

					}

					if (connection != null)
						connection.disconnect();

					onError("Thread Canceled");

					return;
				}
				total += count;
				if (fileLength > 0) {
					final float progress = (float) ((float) total / (float) fileLength);
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mCallback.onProgressUpdate(progress);
						}
					});
				}
				output.write(data, 0, count);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			onError(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			onError(e.toString());

		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {

			}

			if (connection != null)
				connection.disconnect();

			File fromFile = new File(mTempPathString);
			File toFile = new File(mFilePathString);
			if (fromFile.exists()) {
				if (toFile.exists() == true)
					toFile.delete();

				fromFile.renameTo(toFile);
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					mCallback.onDownloadSuccessed(mFilePathString);
				}
			});

		}

	}
}
