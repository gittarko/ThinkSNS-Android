package com.thinksns.sociax.t4.android.mp3;

import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

public class RecordingThread extends Thread {

	private static int FREQUENCY = 44100;
	private static int CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private volatile boolean setToStopped = false;
	private Handler handler;

	private long time;

	private double volume;

	private int calculateVolume;

	private static int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY,
			CHANNEL, ENCODING);
	private BlockingQueue<short[]> recordQueue;

	public RecordingThread(Handler handler, BlockingQueue<short[]> recordQueue) {
		this.handler = handler;
		this.recordQueue = recordQueue;
	}

	public void stopRecording() {
		this.setToStopped = true;
	}

	public void getTime() {

	}

	public double getVolume() {
		return this.volume;
	}

	public int getCalculateVolume() {
		return this.calculateVolume;
	}

	public double calculateVolume(short[] buffer) {
		double sumVolume = 0.0;
		double avgVolume = 0.0;
		double volume = 0.0;
		for (short b : buffer) {
			sumVolume += Math.abs(b);
		}
		avgVolume = sumVolume / buffer.length;
		volume = Math.log10(1 + avgVolume) * 10;
		return volume;
	}

	public int calculateVolume(short[] buffer, int len) {
		double sum = 0;
		for (int i = 0; i < len; i++) {
			sum += buffer[i] * buffer[i];
		}
		if (len > 0) {
			final double amplitude = sum / len;
			return ((int) (Math.sqrt(amplitude) / 4000 * 8));
		}
		return 0;
	}

	@Override
	public void run() {

		AudioRecord audioRecord = null;

		try {

			short[] buffer = new short[bufferSize];
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					FREQUENCY, CHANNEL, ENCODING, bufferSize);

			int state = audioRecord.getState();
			if (state == AudioRecord.STATE_INITIALIZED) {

				audioRecord.startRecording();
				handler.obtainMessage(Settings.MSG_RECORDING_START)
						.sendToTarget();
				boolean flag = true;

				while (!setToStopped) {

					int len = audioRecord.read(buffer, 0, buffer.length);

					// 去掉全0数据
					if (flag) {

						double sum = 0.0;
						for (int i = 0; i < len; i++) {
							sum += buffer[i];
						}
						if (sum == 0.0) {
							continue;

						} else {

							handler.obtainMessage(Settings.MSG_RECORDING_START)
									.sendToTarget();
							flag = false;

						}
					}

					this.calculateVolume = calculateVolume(buffer, len);

					short[] data = new short[len];
					System.arraycopy(buffer, 0, data, 0, len);

					// bolcking queue
					recordQueue.add(data);

				}

				handler.sendEmptyMessage(Settings.MSG_RECORDING_STOP);
				audioRecord.stop();

			} else {

				handler.sendEmptyMessage(Settings.MSG_RECORDING_STATE_ERROR);

			}

		} catch (Exception e) {

			handler.sendEmptyMessage(Settings.MSG_RECORDING_EXCEPTION);

		} finally {

			try {

				audioRecord.release();
				audioRecord = null;

				handler.sendEmptyMessage(Settings.MSG_RECORDING_RELEASE);

			} catch (Exception e) {

			}
		}

	}

}
