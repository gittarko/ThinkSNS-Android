package com.thinksns.sociax.t4.android.function;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.findpeople.ActivitySelectUser;
import com.thinksns.sociax.t4.model.ModelGift;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-18
 * @version 1.0
 */
public class FunctionGiftDialog{
	private ImageView pic;
	private TextView tv_gift_name, tv_gift_price, tv_describ;
	public Button bt_cancle, bt_next;
	private Context context1;
	private int type = StaticInApp.EXCHARGE_GIFT;// 赠送 、兑换
	private int uid;
	LayoutInflater inflater;
	View view;
	Dialog dialog = null;
	private Thinksns application;
	
	public Button getBt_next() {
		return bt_next;
	}

	public void setBt_next(Button bt_next) {
		this.bt_next = bt_next;
	}

	public Button getBt_cancle() {
		return bt_cancle;
	}

	public void setBt_cancle(Button bt_cancle) {
		this.bt_cancle = bt_cancle;
	}

	private ImageView img_delete;
	private ModelGift gift;
	private Handler handler;

	/**
	 * @param uid
	 *            赠送给的用户id，如果有，并且不是当前用户id，则点击确定直接赠送，否则点击确定进入选人页面
	 * @param context
	 * @param layout
	 * @param style
	 * @param type
	 */
	public FunctionGiftDialog(int uid, Context context, int type) {
		this(context,type);
		application = (Thinksns) context.getApplicationContext();
		this.uid = uid;
		this.handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case StaticInApp.EXCHARGE_GIFT:
					try {
						JSONObject result2json = new JSONObject(
								msg.obj.toString());
						String status = result2json.getString("status");
						if (status.equals("0")) {
							Toast.makeText(context1,
									result2json.getString("msg"), 1000).show();
						} else {
							Toast.makeText(context1,
									result2json.getString("msg"), 1000).show();
							((ThinksnsAbscractActivity) context1).refreshList();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(context1, "未知错误", 1000).show();
					}
					dialog.dismiss();

					break;

				case StaticInApp.SEND_GIFT:
					try {
						JSONObject result = new JSONObject(msg.obj.toString());
						Toast.makeText(context1, result.getString("msg"),
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(context1, "操作失败", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				}
			}
		};

	}

	public FunctionGiftDialog(Context context,int type2) {
		this.context1 = context;
		application = (Thinksns) context.getApplicationContext();
		this.type = type2;
		this.inflater=LayoutInflater.from(context);
		view=inflater.inflate(R.layout.dialog_gift, null);
		pic = (ImageView) view.findViewById(R.id.picimage);
		tv_gift_name = (TextView) view.findViewById(R.id.tv_gift_name);
		tv_gift_price = (TextView) view.findViewById(R.id.tv_gift_price);
		tv_describ = (TextView) view.findViewById(R.id.tv_describ);
		bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
		bt_next = (Button) view.findViewById(R.id.bt_next);
		img_delete = (ImageView) view.findViewById(R.id.img_delete);
		if(type==StaticInApp.EXCHARGE_GIFT){
			bt_cancle.setText("转赠");
			bt_next.setText("兑换");
		}
		img_delete.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog.dismiss();
				return false;
			}
		});
		bt_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type==StaticInApp.EXCHARGE_GIFT){
					Intent intent = new Intent(context1,
							ActivitySelectUser.class);
					Bundle bundle = new Bundle();
					bundle.putInt("select_type", StaticInApp.SELECT_GIFT_RESEND);
					bundle.putSerializable("gift", gift);
					intent.putExtras(bundle);
					context1.startActivity(intent);
				}
				dialog.dismiss();
			}
		});
		bt_next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (type == StaticInApp.SEND_GIFT) {// 赠送礼物
					if (uid == 0 || uid == Thinksns.getMy().getUid()) {// 如果当前没有被赠送的uid
						Intent intent = new Intent(context1,
								ActivitySelectUser.class);
						Bundle bundle = new Bundle();
						bundle.putInt("select_type", StaticInApp.SELECT_GIFT_RECEIVER);
						bundle.putSerializable("gift", gift);
						intent.putExtras(bundle);
						context1.startActivity(intent);
					} else {// 如果当前有被赠送的uid，则直接赠送
						sendGiftTask();
					}
				} else {// 兑换礼物
					sendExchargeTask();
				}
				dialog.dismiss();
			}
		});
		dialog = new Dialog(context,R.style.dialog);
        dialog.setContentView(view);
        dialog.show();
	}
	/**
	 * 图片
	 * @param giftPicurl
	 */
	public void setImage(String giftPicurl) {
//		pic.setImageUrl(giftPicurl);
		application.displayImage(giftPicurl,pic);
	}
	/**
	 * 设置礼物基本信息
	 * @param gift
	 */
	public void setGift(ModelGift gift) {
		this.gift = gift;
//		pic.setImageUrl(gift.getGiftPicurl());
		
		application.displayImage(gift.getGiftPicurl(),pic);
		
		tv_gift_name.setText(gift.getGiftName());
		tv_gift_price.setText(gift.getGiftPrice());
		tv_describ.setText((type == StaticInApp.SEND_GIFT )?("是否确认要购买，该礼物将从您的账户中扣除"+ gift.getGiftPrice()):( "把礼物转赠给好友 或把礼物兑换成")
				+ gift.getGiftPrice() + "的80%");
	}

	/**
	 * 兑换礼物积分线程
	 */
	public void sendExchargeTask() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				msg.what = StaticInApp.EXCHARGE_GIFT;
				msg.obj = new Api.GiftApi().buyGift(gift.getId());
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 赠送礼物
	 */
	public void sendGiftTask() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.SEND_GIFT;
				Thinksns app = (Thinksns) ((Activity) context1)
						.getApplication().getApplicationContext();
				try {
					msg.obj = app.getApiGift().sentGift(gift.getId(), uid + "",
							null, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);

			}
		}).start();
	}

	public ModelGift getGift() {
		return gift;
	}
}