package com.thinksns.tschat.mp3;

public class JNIMp3Encode {

	public native void init(int channel, int sampleRate, int brate);

	public native void destroy();

	public native byte[] encode(short[] buffer, int len);

	static {
		System.loadLibrary("mp3lame");
	}
}
