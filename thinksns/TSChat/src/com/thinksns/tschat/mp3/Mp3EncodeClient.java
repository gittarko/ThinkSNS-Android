package com.thinksns.tschat.mp3;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Mp3EncodeClient {

	private BlockingQueue<short[]> recordQueue = new LinkedBlockingQueue<short[]>();
	private RecordingThread recordingThread;
	private Mp3EncodeThread mp3EncodeThread;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			}
		};

	};

	public void start() {
		recordQueue.clear();

		recordingThread = new RecordingThread(handler, recordQueue);
		recordingThread.start();

		mp3EncodeThread = new Mp3EncodeThread(handler, recordQueue);
		mp3EncodeThread.start();
	}

	public void stop() {
		recordingThread.stopRecording();
		mp3EncodeThread.stopMp3Encode();
	}

	public double getVolume() {
		return recordingThread.getVolume();
	}

	public int getCalculateVolume() {
		return recordingThread.getCalculateVolume();
	}

	public String getFilePath() {
		return mp3EncodeThread.getFilePath();
	}

}
