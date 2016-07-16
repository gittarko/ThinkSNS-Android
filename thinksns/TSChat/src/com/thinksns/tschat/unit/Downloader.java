package com.thinksns.tschat.unit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 类说明：
 * 
 * @author Zoey
 * @date 2015-7-24
 * @version 1.0
 */
public class Downloader {
	private URL url = null;

	/**
	 * 根据URL下载文件,前提是这个文件当中的内容是文本,函数的返回值就是文本当中的内容 1.创建一个URL对象
	 * 2.通过URL对象,创建一个HttpURLConnection对象 3.得到InputStream 4.从InputStream当中读取数据
	 * 
	 * @param urlStr
	 * @return
	 */
	public String download(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlStr
	 * @return
	 */
	public InputStream getInputStreamFromURL(String urlStr) {
		HttpURLConnection urlConn = null;
		InputStream inputStream = null;
		try {
			url = new URL(urlStr);
			urlConn = (HttpURLConnection) url.openConnection();
			inputStream = urlConn.getInputStream();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return inputStream;
	}

	private InputStream getInputStream(String urlStr)
			throws MalformedURLException, IOException {

		url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream inputStream = null;

		if (conn.getResponseCode() == 200) {
			inputStream = conn.getInputStream();
		}

		return inputStream;
	}

	/**
	 * 聊天附件下载
	 */
	public File downLoadFile(String url, String path, String fileName) {

		URL uri = null;
		File file = null;
		OutputStream out = null;

		try {
			uri = new URL(url);
			InputStream in = null;
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

			if (conn.getResponseCode() == 200) {

				in = conn.getInputStream();

				File file_dir = new File(path);
				if (!file_dir.exists()) {
					file_dir.mkdirs();
				}

				file = new File(file_dir, fileName);
				if (!file.exists()) {
					file_dir.mkdir();
				}

				out = new FileOutputStream(file);
				
				byte[] buff = new byte[4 * 1024];

				int len = 0;
				while ((len = (in.read(buff))) > 0) {
					out.write(buff, 0, len);
				}
				out.flush();
				out.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
}
