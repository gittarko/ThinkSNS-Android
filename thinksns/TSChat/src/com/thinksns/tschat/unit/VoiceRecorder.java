package com.thinksns.tschat.unit;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;

import com.thinksns.tschat.constant.TSConfig;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by hedong on 15/12/14.
 * 语音录制工具类
 *
 */
public class VoiceRecorder {
    private MediaRecorder recorder;
    private static final String PREFIX = "voice";
    private static final String EXTENSION = ".mp3";
    private boolean isRecording = false;
    private long startTime;
    private String voiceFilePath = null;
    private String voiceFileName = null;
    private File file;
    private Handler handler;

    public VoiceRecorder(Handler handler) {
        this.handler = handler;
    }

    /**
     * 开始录制语音
     * @param toUserName
     * @return
     */
    public String startRecording(String toUserName) {
        this.file = null;

        try {
            if(this.recorder != null) {
                //如果已经存在实例则释放内存对象
                this.recorder.release();
                this.recorder = null;
            }

            this.recorder = new MediaRecorder();
            this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);    // 设置麦克风
            /**
             * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
             * THREE_GPP(3gp格式，H263视频/ARM音频编码)
             * MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            this.recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
            this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            this.recorder.setAudioChannels(1);
            this.recorder.setAudioSamplingRate(8000);
            this.recorder.setAudioEncodingBitRate(64);
            this.voiceFileName = this.getVoiceFileName(toUserName);
            this.voiceFilePath = this.initVoiceFilePath();
            this.recorder.setOutputFile(voiceFilePath);
            this.recorder.prepare();
            this.isRecording = true;
            this.recorder.start();
        } catch (IOException var5) {
            Log.e("voice", "prepare() failed:" + var5.toString());
        }

        /**
         * 这里加入声音大小的动画显示
         */
        (new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        if(VoiceRecorder.this.isRecording) {
                            Message var1 = new Message();
                            var1.what = VoiceRecorder.this.recorder.getMaxAmplitude() * 5 / 32767;
                            VoiceRecorder.this.handler.sendMessage(var1);
                            SystemClock.sleep(100L);
                            continue;
                        }
                    } catch (Exception var2) {
                        Log.e("voice", var2.toString());
                    }

                    return;
                }
            }
        })).start();

        this.startTime = (new Date()).getTime();
        Log.d("voice", "start voice recording to file:" + this.file.getAbsolutePath());

        return this.file == null ? null : this.file.getAbsolutePath();
    }

    /**
     * 取消录音
     */
    public void discardRecording() {
        if(this.recorder != null && isRecording) {
            try {
                this.recorder.stop();
                this.recorder.reset();
                this.recorder.release();
                this.recorder = null;
                if(this.file != null && this.file.exists() && !this.file.isDirectory()) {
                    this.file.delete();
                }
            } catch (IllegalStateException var2) {
                ;
            } catch (RuntimeException var3) {
                ;
            }

            this.isRecording = false;
        }

    }

    /**
     * 停止录音
     * @return
     */
    public int stopRecoding() {
        if(this.recorder != null && isRecording) {
            this.isRecording = false;
            this.recorder.stop();
            this.recorder.release();
            this.recorder = null;
            if(this.file != null && this.file.exists() && this.file.isFile()) {
                if(this.file.length() == 0L) {
                    this.file.delete();
                    return -1011;
                } else {
                    int var1 = (int)((new Date()).getTime() - this.startTime) / 1000;
                    Log.d("voice", "voice recording finished. seconds:" + var1 + " file length:" + this.file.length());
                    return var1;
                }
            } else {
                return -1011;
            }
        } else {
            return 0;
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if(this.recorder != null) {
            this.recorder.release();
        }

    }

    public String getVoiceFileName(String var1) {
        Time var2 = new Time();
        var2.setToNow();
        return var1 + var2.toString().substring(0, 15) + EXTENSION;
    }

    public boolean isRecording() {
        return this.isRecording;
    }

    /**
     * 设置语音存放路径
     * 开发者可以自行配置属于自己应用的路径
     * @return
     */
    public String initVoiceFilePath() {
        String path = Environment.getExternalStorageDirectory() + "/" +
                TSConfig.VOICE_PATH;
        File tmpFile = new File(path);
        if(!tmpFile.exists()) tmpFile.mkdirs();

        this.file = new File(tmpFile.getAbsolutePath(), voiceFileName);
        return file.getAbsolutePath();
    }

    public String getVoiceFilePath() {
        return this.voiceFilePath;
    }
}
