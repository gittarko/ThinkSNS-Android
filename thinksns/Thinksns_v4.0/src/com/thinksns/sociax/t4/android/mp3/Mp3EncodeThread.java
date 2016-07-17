package com.thinksns.sociax.t4.android.mp3;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class Mp3EncodeThread extends Thread {

	private BlockingQueue<short[]> recordQueue;
	private Handler handler;
	private static final long TIME_WAIT_RECORDING = 100;
	private volatile boolean setToStopped = false;
	private JNIMp3Encode mp3Encode = new JNIMp3Encode();
	private int channel = 2;
	private int sampleRate = 44100;
	private int brate = 16;

	public Mp3EncodeThread(Handler handler, BlockingQueue<short[]> recordQueue) {
		this.recordQueue = recordQueue;
		this.handler = handler;
	}

	public void stopMp3Encode() {
		setToStopped = true;
	}

	public String getFilePath() {
		return Settings.recordingPath + "recording.mp3";
	}

	@Override
	public void run() {

		mp3Encode.init(channel, sampleRate, brate);
		FileOutputStream out = null;

		try {

			// 检查sdcard状态
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				File yzsPath = new File(Settings.recordingPath);
				if (!yzsPath.isDirectory()) {
					yzsPath.mkdir();
				}
			}
			out = new FileOutputStream(Settings.recordingPath + "recording.mp3");

			short[] queueHeadBuffer = null;
			while (true) {

				queueHeadBuffer = recordQueue.poll(TIME_WAIT_RECORDING,
						TimeUnit.MILLISECONDS);
				if (queueHeadBuffer != null) {
					byte[] mp3Datas = mp3Encode.encode(queueHeadBuffer,
							queueHeadBuffer.length);
					out.write(mp3Datas);
				}
				if (setToStopped && recordQueue.size() == 0) {
					break;
				}
			}

			out.close();
			mp3Encode.destroy();

		} catch (Exception e) {
			Log.e("", "Mp3EncodeThread" + e.toString());
		}
	}

}
