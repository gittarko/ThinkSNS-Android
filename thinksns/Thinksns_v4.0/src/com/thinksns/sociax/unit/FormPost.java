package com.thinksns.sociax.unit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateWeibo;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.thinksnsbase.utils.FormFile;

import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FormPost {

	public FormPost() {
		super();

	}

	// 如果是文本的文件的话那么通过map类传递进来如果是文件的话通过FormFile传递进来
	public static String post(String actionUrl, Map<String, String> params,
			FormFile file) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(15 * 1000);
		conn.setDoInput(true);
		// 允许输入
		conn.setDoOutput(true);
		// 允许输出
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		// Post方式
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		String testUrlParams = "";// 用于log打印参数
		// 首先组拼文本类型的参数
		if (params != null) {
			StringBuilder sbParams = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sbParams.append(PREFIX);
				sbParams.append(BOUNDARY);
				sbParams.append(LINEND);
				sbParams.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sbParams.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sbParams.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sbParams.append(LINEND);
				sbParams.append(entry.getValue());
				sbParams.append(LINEND);
			}
			testUrlParams += sbParams.toString().getBytes();
			outStream.write(sbParams.toString().getBytes());
		}

		// 发送文件数据
		StringBuilder sbFile = new StringBuilder();
		sbFile.append(PREFIX);
		sbFile.append(BOUNDARY);
		sbFile.append(LINEND);
		sbFile.append("Content-Disposition: form-data; name=\""
				+ file.getFormnames() + "\"; filename=\"" + file.getFileName()
				+ "\"" + LINEND);
		sbFile.append("Content-Type: application/octet-stream; charset="
				+ CHARSET + LINEND);
		sbFile.append(LINEND);
		testUrlParams += sbFile.toString().getBytes();
		outStream.write(sbFile.toString().getBytes());

		InputStream is = file.getInStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		is.close();
		testUrlParams += LINEND.getBytes();
		outStream.write(LINEND.getBytes());

		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		testUrlParams += end_data;
		outStream.write(end_data);
		outStream.flush();

		// 得到响应码
		int res = conn.getResponseCode();
		InputStream in = conn.getInputStream();

		String result = null;
		if (res == 200) {
			int ch;
			StringBuilder sb2 = new StringBuilder();
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
			result = sb2.toString();
		}
		Log.v("FromPost", "wztest  " + result);
		outStream.close();
		conn.disconnect();
		return result;
	}

	// 上传视频
	public static String post(String actionUrl, Map<String, String> params,
			FormFile[] formFiles) throws IOException {
		{
			// 通知栏
			NotificationManager notiManager = (NotificationManager) Thinksns
					.getContext().getSystemService(
							Activity.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.icon = R.drawable.app_load;
			notification.tickerText = "正在上传视频";
			notification.contentView = new RemoteViews(Thinksns.getContext()
					.getPackageName(), R.layout.video_progress_item);
			notiManager.notify(0, notification);
			//
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			URL uri = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(15 * 1000);
			conn.setDoInput(true);
			// 允许输入
			conn.setDoOutput(true);
			// 允许输出
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			// Post方式
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}
			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			int totalSize = 0;
			for (FormFile file : formFiles) {
				InputStream is = file.getInStream();
				totalSize += is.available();
			}
			// 发送文件数据
			int i = 1;
			int perFlag = 0;
			for (FormFile file : formFiles) {
				if (file != null) {
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\""
							+ file.getFormnames() + "\"; filename=\""
							+ file.getFileName() + "\"" + LINEND);
					Log.v(file.getFormnames(), file.getFileName());
					sb1.append("Content-Type: application/octet-stream; charset="
							+ CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());

					InputStream is = file.getInStream();
					byte[] buffer = new byte[1024];
					int len = 0;

					while ((len = is.read(buffer)) != -1) {
						float percent = 1024 * i / (float) totalSize * 100;
						if (percent >= perFlag) {
							notification.contentView.setTextViewText(
									R.id.content_view_text1, 1024 * i
											/ totalSize * 100 + "%");
							notification.contentView.setProgressBar(
									R.id.content_view_progress, totalSize,
									1024 * i, false);
							notiManager.notify(0, notification);
							perFlag += 5;
						}
						i++;
						outStream.write(buffer, 0, len);
					}
					is.close();
					outStream.write(LINEND.getBytes());
				}
			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 得到响应码
			int res = conn.getResponseCode();
			InputStream in = conn.getInputStream();

			String result = null;
			if (res == 200) {
				int ch;
				StringBuilder sb2 = new StringBuilder();
				while ((ch = in.read()) != -1) {
					sb2.append((char) ch);
				}
				result = sb2.toString();
			}
			try {
				ModelBackMessage msg = new ModelBackMessage(result);
				if (msg.getStatus() == 1) {
					ActivityCreateWeibo.staticVideoPath = null;
					ActivityCreateWeibo.staticTime = null;
					Intent intent = new Intent(Thinksns.getContext(),
							ActivityHome.class);
					intent.putExtra("weiboId", msg.getWeiboId());
					PendingIntent pIntent = PendingIntent.getActivity(
							Thinksns.getContext(), 0, intent, 0);
					notification.contentIntent = pIntent;
					notification.contentView.setTextViewText(
							R.id.content_view_text1, "上传成功");
					notiManager.notify(0, notification);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			outStream.close();
			conn.disconnect();
			return result;
		}

	}

	/**
	 * 上传多图
	 * 
	 * @param actionUrl
	 * @param params
	 *            文件列表
	 * @return
	 */

	public static String postMultilPic(String actionUrl,
			HashMap<String, String> params, FormFile[] formFiles) {
		String result = null;
		try {
			// 通知栏
			NotificationManager notiManager = (NotificationManager) Thinksns
					.getContext().getSystemService(
							Activity.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.icon = R.drawable.app_load;
			notification.tickerText = "正在上传图片";
			notification.contentView = new RemoteViews(Thinksns.getContext()
					.getPackageName(), R.layout.video_progress_item);
			notiManager.notify(0, notification);
			//
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			URL uri = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(15 * 1000);
			conn.setDoInput(true);
			// 允许输入
			conn.setDoOutput(true);
			// 允许输出
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			// Post方式
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);
			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}

			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());

			int totalSize = 0;
			for (FormFile file : formFiles) {
				InputStream is = file.getInStream();
				totalSize += is.available();
			}
			// 发送文件数据
			int i = 1;
			int perFlag = 0;

			for (FormFile file : formFiles) {
				Log.v("formFiles size=", formFiles.length
						+ " file.getFormnames()=" + file.getFormnames()
						+ " file.getFileName()=" + file.getFileName());
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name[]=\""
						+ file.getFormnames() + "\"; filename=\""
						+ file.getFileName() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = file.getInStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					float percent = 1024 * i / (float) totalSize * 100;
					Log.v("FromPost--postMultilPic", "totalSize="+totalSize+" percent="+percent+" perfFlag=+"+perFlag);
					if (percent >= perFlag) {
						notification.contentView.setTextViewText(
								R.id.content_view_text1, 1024 * i / totalSize
										* 100 + "%");
						notification.contentView.setProgressBar(
								R.id.content_view_progress, totalSize,
								1024 * i, false);
						notiManager.notify(0, notification);
						perFlag += 5;
					}
					i++;
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();

			// 得到响应码
			int res = conn.getResponseCode();
			InputStream in = conn.getInputStream();

			if (res == 200) {
				int ch;
				StringBuilder sb2 = new StringBuilder();
				while ((ch = in.read()) != -1) {
					sb2.append((char) ch);
				}
				result = sb2.toString();
			}
			try {
				ModelBackMessage msg = new ModelBackMessage(result);
				if (msg.getStatus() == 1) {
					Intent intent = new Intent(Thinksns.getContext(),
							ActivityHome.class);
					intent.putExtra("weiboId", msg.getWeiboId());
					PendingIntent pIntent = PendingIntent.getActivity(
							Thinksns.getContext(), 0, intent, 0);
					notification.contentIntent = pIntent;
					notification.contentView.setTextViewText(
							R.id.content_view_text1, "上传成功");
					notiManager.notify(0, notification);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			outStream.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result!=null);
		Log.v("result", result);
		return result;
	}
	
	
	/**
	 * 上传多图，不带notifycation
	 * @param actionUrl
	 * @param params
	 *            文件列表
	 * @return
	 */
	public static String postPicOnly(String actionUrl,
			HashMap<String, String> params, FormFile[] formFiles) {
		String result = null;
		try {
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			URL uri = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(15 * 1000);
			conn.setDoInput(true);
			// 允许输入
			conn.setDoOutput(true);
			// 允许输出
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			// Post方式
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);
			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}

			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());

			int totalSize = 0;
			for (FormFile file : formFiles) {
				InputStream is = file.getInStream();
				totalSize += is.available();
			}
			// 发送文件数据
			int i = 1;
			int perFlag = 0;

			for (FormFile file : formFiles) {
				Log.v("formFiles size=", formFiles.length
						+ " file.getFormnames()=" + file.getFormnames()
						+ " file.getFileName()=" + file.getFileName());
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name[]=\""
						+ file.getFormnames() + "\"; filename=\""
						+ file.getFileName() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = file.getInStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					i++;
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();

			// 得到响应码
			int res = conn.getResponseCode();
			InputStream in = conn.getInputStream();

			if (res == 200) {
				int ch;
				StringBuilder sb2 = new StringBuilder();
				while ((ch = in.read()) != -1) {
					sb2.append((char) ch);
				}
				result = sb2.toString();
			}
			outStream.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.v("result", result);
		return result;
	}
}
