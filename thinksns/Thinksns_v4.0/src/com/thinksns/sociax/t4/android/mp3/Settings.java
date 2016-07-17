package com.thinksns.sociax.t4.android.mp3;

import com.thinksns.sociax.t4.android.Thinksns;

public class Settings {
	public static String recordingPath = Thinksns.getCache_path() + "/chat_recording/";
	public static final int MSG_RECORDING_START = 1;
	public static final int MSG_RECORDING_STOP = 2;
	public static final int MSG_RECORDING_STATE_ERROR = 3;
	public static final int MSG_RECORDING_EXCEPTION = 4;
	public static final int MSG_RECORDING_RELEASE = 5;
	public static final int MSG_DRAW_ENDING = 6;
	public static final int MSG_FILE_EXCEPTION = 7;
}
