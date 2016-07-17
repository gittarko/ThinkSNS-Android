package com.thinksns.sociax.t4.android.checkin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.ApiCheckin;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.tschat.widget.SmallDialog;

/**
 * 类说明：签到专用
 * 
 */
public class ActivityCheckIn extends ThinksnsAbscractActivity {

	private MyHandler myHandler;

	private TextView tv_time,tv_shadow;
	private TextView ivCheck;		//每日签到
	private FrameLayout fm_content;	//整个layout
	private TextView tvCheckInfo;	//签到信息
	private ImageView img_face;		//太阳图片
	private TextView tv_tips;		//底部提醒
	private ImageView img_back;
	private LinearLayout ll_rank;	//排行榜

	private SmallDialog smallDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		smallDialog = new SmallDialog(this, "请稍后");
		smallDialog.setCanceledOnTouchOutside(false);
		initView();
		initListener();
		initData();
	}

	private void initListener() {
		img_face.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doCheckin();
			}
		});

		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}

	private void initData() {
		Object info = Thinksns.getCheckInfo();
		if(info != null) {
			setCheckData(info);
		}
	}

	private void initView() {
		tv_time = (TextView) findViewById(R.id.tv_check_week);
		tv_shadow=(TextView)findViewById(R.id.tv_shadow);
		ivCheck = (TextView) findViewById(R.id.iv_check);
		tvCheckInfo = (TextView) findViewById(R.id.tv_check_info);
		myHandler = new MyHandler();
		fm_content=(FrameLayout) findViewById(R.id.fl_content);
		img_face=(ImageView) findViewById(R.id.img_face);
		
		tv_tips=(TextView)findViewById(R.id.tv_tips);
		img_back=(ImageView) findViewById(R.id.img_rightback);
		ll_rank=(LinearLayout) findViewById(R.id.ll_rank);
	}

	//填充签到信息
	private void setCheckData(Object o) {
		try {
			JSONObject jsonData = new JSONObject(o.toString());
			if (jsonData.getBoolean("ischeck")) {
				img_face.setClickable(false);
				setCheckInState(true);
				tv_tips.setText("签到排行榜");
				ivCheck.setText("已签到");
				ivCheck.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tv_check_in_leftpadding, 0, 0, 0);
				getRankTask();
			} else {
				img_face.setClickable(true);
			}
			
			String mad = jsonData.getString("day");
			String month = mad.substring(0, 2);
			String day = mad.substring(3, 5);
			Calendar cal = Calendar.getInstance();
			Date date = new Date(cal.get(Calendar.YEAR) - 1900,
					Integer.valueOf(month) - 1, Integer.valueOf(day));
			SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
			
			tv_shadow.setText(month+"月"+day+"日  "+dateFm.format(date));
			tv_time.setText(month+"月"+day+"日  "+dateFm.format(date));

			tvCheckInfo.setText("已签到" + jsonData.getString("total_num")
					+ "天，连续签到" + jsonData.getString("con_num") + "天");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取排行榜
	 */
	private void getRankTask() {
		Object rankInfo = Thinksns.getRankInfo();
		if(rankInfo != null) {
			//已经获取到签到排行榜信息
			setCheckRankList(rankInfo);
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				ApiCheckin apiCheckin = new Api.CheckinApi();
				Message msg = myHandler.obtainMessage();
				msg.what = 3;
				try {
					Object object = apiCheckin.getCheckRankList();
					Thinksns.setRankInfo(object);
					if (object != null) {
						msg.arg1 = 1;
						msg.obj = object;
					} else {
						msg.arg1 = 2;
					}
				} catch (ApiException e) {
					msg.arg1 = 2;
					e.printStackTrace();
				}
				msg.sendToTarget();
			}
		}).start();
		
	}

	/**
	 * 执行签到
	 */
	private void doCheckin() {
		smallDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				ApiCheckin apiCheckin = new Api.CheckinApi();
				Message msg = myHandler.obtainMessage();
				msg.what = 2;
				try {
					Object object = apiCheckin.checkIn();
					Thinksns.setCheckIn(object);
					if (object != null) {
						msg.arg1 = 1;
						msg.obj = object;
					} else {
						msg.arg1 = 2;
					}
				} catch (ApiException e) {
					msg.arg1 = 2;
					e.printStackTrace();
				}
				msg.sendToTarget();
			}
		}).start();

		img_face.setClickable(false);
		setCheckInState(true);

	}

	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Context context) {

		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1://获取签到信息
				if (msg.arg1 == 1) {
					setCheckData(msg.obj);
				} else if (msg.arg1 == 2) {

				}
				break;
			case 2://执行签到
				if (msg.arg1 == 1) {
					setCheckData(msg.obj);
					Toast.makeText(ActivityCheckIn.this,
							R.string.check_success, Toast.LENGTH_SHORT).show();
				} else if (msg.arg1 == 2) {
					Toast.makeText(ActivityCheckIn.this,
							R.string.check_fail, Toast.LENGTH_SHORT).show();
					//还原未签到状态
					setCheckInState(false);
					img_face.setClickable(true);
				}

				smallDialog.dismiss();
				break;
			case 3://
				if (msg.arg1 == 1) {
					setCheckRankList(msg.obj);
				} else if (msg.arg1 == 2) {
					
				}
			break;
			}
		}
	}

	//设置签到状态
	private void setCheckInState(boolean b) {
		if(!b) {
			img_face.setBackgroundResource(R.drawable.img_nocheckin_face);
			fm_content.setBackgroundResource(R.drawable.bg_no_check_in);
		}else {
			img_face.setBackgroundResource(R.drawable.img_checkin_face);
			fm_content.setBackgroundResource(R.drawable.bg_has_checkin);
		}
	}

	@Override
	public String getTitleCenter() {
		return getString(R.string.checkin);
	}

	/**
	 * 签到排行列表
	 * @param obj
	 */
	public void setCheckRankList(Object obj) {
		try{
			JSONArray result=new JSONArray(obj.toString());
			for(int i = 0; i< result.length(); i++){
				ModelSearchUser user=new ModelSearchUser(result.getJSONObject(i));
				View view=getLayoutInflater().inflate(R.layout.item_checkin_rank, null);
				ImageView imgi=(ImageView) view.findViewById(R.id.imageView1);
				Glide.with(ActivityCheckIn.this).load(user.getUface())
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(ActivityCheckIn.this))
				.crossFade()
				.into(imgi);
				imgi.setTag(R.id.tag_position,user.getUid());
				imgi.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(ActivityCheckIn.this,ActivityUserInfo_2.class);
						intent.putExtra("uid", (Integer)v.getTag(R.id.tag_position));
						startActivity(intent);
					}
				});

				TextView tv_rank=(TextView) view.findViewById(R.id.textView1);
				tv_rank.setText((i+1)+"");
				ll_rank.addView(view);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.checkin_main;
	}
	@Override
	public OnClickListener getLeftListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "left onclick ....");
			}
		};
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return true;
	}
	@Override
	public void finish() {
		super.finish();
		Anim.startActivityFromTop(ActivityCheckIn.this);
	}
}
