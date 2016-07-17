package com.thinksns.sociax.t4.android.gift;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;

/**
 * 类说明：牛币转账
 * 
 * @author Zoey
 * @date 2015年9月28日
 * @version 1.0
 */
public class ActivityScoreTransfer extends ThinksnsAbscractActivity {

	private TextView tv_receiver_nickname,tv_title_back;
	private EditText et_count_of_score,et_remark;
	private Button btn_transfer_now;
	private static int to_uid=0;
	private TransferHandler mHandler=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		initIntentData();
		initView();
		initListener();
		initData();
	}

	private void initIntentData(){
		
	}
	
	private void initView() {
		tv_receiver_nickname=(TextView)this.findViewById(R.id.tv_receiver_nickname);
		tv_title_back = (TextView) findViewById(R.id.tv_title_left);
		et_count_of_score=(EditText)this.findViewById(R.id.et_count_of_score);
		et_remark=(EditText)this.findViewById(R.id.et_remark);
		btn_transfer_now=(Button)this.findViewById(R.id.btn_transfer_now);
		this.setRegion(et_count_of_score);
		
		mHandler=new TransferHandler();
	}
	
	private void initListener(){
		tv_title_back.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		tv_receiver_nickname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(ActivityScoreTransfer.this,ActivityFindGiftReceiver2.class);
				startActivityForResult(intent, StaticInApp.REQUEST_CODE_SELET_GIFT_RECEIVER);
				
			}
		});
		btn_transfer_now.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int num=Integer.parseInt(et_count_of_score.getText().toString());
				String desc=et_remark.getText().toString();
				transferNow(to_uid,num,desc);
			}
		});
	}
	
	public void transferNow(final int to_uid,final int num,final String desc){
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				Message msg = new Message();
				msg.what = StaticInApp.TRANSFER_SCORE;
				try {
					msg.obj = ((Thinksns) (ActivityScoreTransfer.this.getApplicationContext())).getApiCredit().transferMyScore(to_uid, num, desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(msg);
				
			}
		}).start();
	}
	
	@SuppressLint("HandlerLeak")
	public class TransferHandler extends Handler {
		public TransferHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StaticInApp.TRANSFER_SCORE:
				try {
					if (msg.obj!=null) {
						JSONObject result = new JSONObject(msg.obj.toString());
						if (result!=null) {
							String status=result.getString("status");
							String message=result.getString("mesage");
							Toast.makeText(ActivityScoreTransfer.this, message, 1).show();
							if (status.equals("1")) {
								
								//发送广播至牛币详情页面，更新页面
								Intent intent = new Intent(StaticInApp.UPDATE_SCORE_DETAIL);
								ActivityScoreTransfer.this.sendBroadcast(intent);
								
								finish();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				break;
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == StaticInApp.RESULT_CODE_SELET_GIFT_RECEIVER
				&&requestCode==StaticInApp.REQUEST_CODE_SELET_GIFT_RECEIVER) {
			if (data != null){
				ModelUser giftUser=(ModelUser) data.getSerializableExtra("user");
				tv_receiver_nickname.setText(giftUser.getUserName());
				to_uid =giftUser.getUid();
			}
		}
	}
	
	private void initData(){
		
	}
	
	@Override
	public String getTitleCenter() {
		return null;
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_transfer_score;
	}
	
	//动态判断输入的值
	private int MIN_MARK = 1; 
    private int MAX_MARK = Integer.parseInt(Thinksns.getMy().getUserCredit().getScore_value());
    private void setRegion( final EditText et)  { 
        et.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void onTextChanged(CharSequence s, int start, int before, int count) { 
                if (start > 1) { 
                    if (MIN_MARK != -1 && MAX_MARK != -1) { 
                      int num = Integer.parseInt(s.toString()); 
                      if (num > MAX_MARK)  { 
                          s = String.valueOf(MAX_MARK); 
                          et.setText(s); 
                      } 
                      else if(num < MIN_MARK) 
                          s = String.valueOf(MIN_MARK);
                      return; 
                    } 
                } 
            } 

            @Override 
            public void beforeTextChanged(CharSequence s, int start, int count, 
                    int after) { 
            } 

            @Override 
            public void afterTextChanged(Editable s) { 
                if (s != null && !s.equals("")) { 
                    if (MIN_MARK != -1 && MAX_MARK != -1)  { 
                         int markVal = 0; 
                         try{ 
                             markVal = Integer.parseInt(s.toString()); 
                         } 
                         catch (NumberFormatException e)  { 
                             markVal = 0; 
                         } 
                         if (markVal > MAX_MARK) { 
                             Toast.makeText(getBaseContext(), "转账数额不能超过自己的总牛币", Toast.LENGTH_SHORT).show();
                             et.setText(String.valueOf(MAX_MARK)); 
                         } 
                         if (markVal<MIN_MARK) {
                        	  Toast.makeText(getBaseContext(), "最少转1分", Toast.LENGTH_SHORT).show(); 
                              et.setText(String.valueOf(MIN_MARK)); 
						}
                         et.setSelection(et.getText().toString().length());
                         return; 
                    } 
                 } 
            } 
        }); 
    }
	
}
