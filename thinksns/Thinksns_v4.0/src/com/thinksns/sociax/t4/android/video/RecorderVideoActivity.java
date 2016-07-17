package com.thinksns.sociax.t4.android.video;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.widget.MovieRecorderView;
import com.thinksns.sociax.t4.android.widget.MovieRecorderView.OnRecordFinishListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/** 
	 * 类说明：   
	 * @author  ZhiYiForMac    
	 * @date    2015年11月12日
	 * @version 1.0
	 */
public class RecorderVideoActivity extends ThinksnsAbscractActivity implements OnClickListener {
	private MovieRecorderView mRecorderView;
    private Button mShootBtn;
    private TextView tv_cancel;
    
    private boolean isFinish = true;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(this);
		mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (Button) findViewById(R.id.shoot_button);
        mShootBtn.setOnTouchListener(new OnTouchListener() {
             
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                	tv_cancel.setEnabled(false);
                    mRecorderView.record(new OnRecordFinishListener() {
 
                        @Override
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(1);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mRecorderView.getTimeCount() > 1)
                        handler.sendEmptyMessage(1);
                    else {
                        if (mRecorderView.getVecordFile() != null)
                            mRecorderView.getVecordFile().delete();
                        mRecorderView.stop();
                        Toast.makeText(v.getContext(), "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    }
                    tv_cancel.setEnabled(true);
                }
                return true;
            }
        });
	}
	
	@Override
    public void onResume() {
        super.onResume();
        isFinish = true;
    }
 
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        mRecorderView.stop();
    }
 
    @Override
    public void onPause() {
        super.onPause();
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
 
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finishActivity();
        }
    };
 
    private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra("path", mRecorderView.getVecordFile().toString());
            startActivity(intent);
            finish();
//            VideoPlayerActivity.startActivity(this, mRecorderView.getVecordFile().toString());
        }
    }
 
    /**
     * 录制完成回调
     *
     * @author liuyinjun
     * 
     * @date 2015-2-9
     */
    public interface OnShootCompletionListener {
        public void OnShootSuccess(String path, int second);
        public void OnShootFailure();
    }
    
	@Override
	public String getTitleCenter() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_recorder;
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		switch(id) {
		case R.id.tv_cancel:
			finish();
			break;
		}
	}

}
