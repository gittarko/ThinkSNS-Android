package com.thinksns.sociax.t4.android.gift;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.alipay.AlipayConfig;
import com.thinksns.sociax.t4.android.alipay.PayResult;
import com.thinksns.sociax.t4.android.alipay.SignUtils;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.component.StringToMD5;
import com.thinksns.sociax.t4.model.ModelCharge;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/**
 * 类说明：积分充值
 * 
 * @author Zoey
 * @date 2015年9月21日
 * @version 1.0
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN) @SuppressLint("NewApi") 
public class ActivityScoreTopUp extends ThinksnsAbscractActivity {

	private CheckBox cb_top_up_zfb,cb_top_up_cft,cb_top_up_wechat,cb_top_up_card;
	private RelativeLayout  rl_top_up_zfb,rl_top_up_cft,rl_top_up_wechat,rl_top_up_card;
	private Button btn_top_up_10,btn_top_up_60,btn_top_up_now;
	private EditText et_top_up_other;
	private ImageView tv_title_left;
	private ModelCharge modelCharge=null;
	private InputMethodManager imm = null;
	private static boolean isOpen;
	private static String FLAG=null;
	
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
		
		imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		isOpen=imm.isActive();
		
		cb_top_up_zfb=(CheckBox)this.findViewById(R.id.cb_top_up_zfb);
		cb_top_up_cft=(CheckBox)this.findViewById(R.id.cb_top_up_cft);
		cb_top_up_wechat=(CheckBox)this.findViewById(R.id.cb_top_up_wechat);
		cb_top_up_card=(CheckBox)this.findViewById(R.id.cb_top_up_card);
		
		rl_top_up_zfb=(RelativeLayout)this.findViewById(R.id.rl_top_up_zfb);
		rl_top_up_cft=(RelativeLayout)this.findViewById(R.id.rl_top_up_ctf);
		rl_top_up_wechat=(RelativeLayout)this.findViewById(R.id.rl_top_up_wechat);
		rl_top_up_card=(RelativeLayout)this.findViewById(R.id.rl_top_up_card);
		
		btn_top_up_10=(Button)this.findViewById(R.id.btn_top_up_10);
		btn_top_up_60=(Button)this.findViewById(R.id.btn_top_up_60);
		btn_top_up_now=(Button)this.findViewById(R.id.btn_top_up_now);
		
		tv_title_left=(ImageView) this.findViewById(R.id.tv_title_left);
		
		et_top_up_other=(EditText)this.findViewById(R.id.et_top_up_other);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) @SuppressLint("NewApi") 
	private void initListener(){
		
		tv_title_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rl_top_up_zfb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb_top_up_zfb.setChecked(true);
				cb_top_up_cft.setChecked(false);
				cb_top_up_wechat.setChecked(false);
				cb_top_up_card.setChecked(false);
				
				if (isOpen==true) {
					UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
				}
			}
		});
		rl_top_up_cft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb_top_up_zfb.setChecked(false);
				cb_top_up_cft.setChecked(true);
				cb_top_up_wechat.setChecked(false);
				cb_top_up_card.setChecked(false);
				
				if (isOpen==true) {
					UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
				}
			}
		});
		rl_top_up_wechat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb_top_up_zfb.setChecked(false);
				cb_top_up_cft.setChecked(false);
				cb_top_up_wechat.setChecked(true);
				cb_top_up_card.setChecked(false);
				
				if (isOpen==true) {
					UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
				}
			}
		});
		rl_top_up_card.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb_top_up_zfb.setChecked(false);
				cb_top_up_cft.setChecked(false);
				cb_top_up_wechat.setChecked(false);
				cb_top_up_card.setChecked(true);
				
				if (isOpen==true) {
					UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
				}
			}
		});
		cb_top_up_zfb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_top_up_cft.setChecked(false);
					cb_top_up_wechat.setChecked(false);
					cb_top_up_card.setChecked(false);
					
					if (isOpen==true) {
						UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
					}
				}
			}
		});
		cb_top_up_cft.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_top_up_zfb.setChecked(false);
					cb_top_up_wechat.setChecked(false);
					cb_top_up_card.setChecked(false);
					
					if (isOpen==true) {
						UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
					}
				}
			}
		});
		cb_top_up_wechat.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_top_up_zfb.setChecked(false);
					cb_top_up_cft.setChecked(false);
					cb_top_up_card.setChecked(false);
					
					if (isOpen==true) {
						UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
					}
				}
			}
		});
		cb_top_up_card.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_top_up_zfb.setChecked(false);
					cb_top_up_cft.setChecked(false);
					cb_top_up_wechat.setChecked(false);
					
					if (isOpen==true) {
						UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
					}
				}
			}
		});
		
		btn_top_up_10.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_top_up_10.setBackground(ActivityScoreTopUp.this.getResources().getDrawable(R.drawable.rec_bg_1_blue));
				btn_top_up_60.setBackground(ActivityScoreTopUp.this.getResources().getDrawable(R.drawable.rec_bg_1_white));
				btn_top_up_10.setTextColor(ActivityScoreTopUp.this.getResources().getColor(R.color.white));
				btn_top_up_60.setTextColor(ActivityScoreTopUp.this.getResources().getColor(R.color.bg_gift_exchange_rule));
				et_top_up_other.setCursorVisible(false);
				
				FLAG="10";
				
				if (isOpen==true) {
					UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
				}
			}
		});
		btn_top_up_60.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_top_up_10.setBackground(ActivityScoreTopUp.this.getResources().getDrawable(R.drawable.rec_bg_1_white));
				btn_top_up_60.setBackground(ActivityScoreTopUp.this.getResources().getDrawable(R.drawable.rec_bg_1_blue));
				btn_top_up_10.setTextColor(ActivityScoreTopUp.this.getResources().getColor(R.color.bg_gift_exchange_rule));
				btn_top_up_60.setTextColor(ActivityScoreTopUp.this.getResources().getColor(R.color.white));
				et_top_up_other.setCursorVisible(false);
				
				FLAG="60";
				
				if (isOpen==true) {
					UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
				}
			}
		});
		et_top_up_other.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_top_up_10.setBackground(ActivityScoreTopUp.this.getResources().getDrawable(R.drawable.rec_bg_1_white));
				btn_top_up_60.setBackground(ActivityScoreTopUp.this.getResources().getDrawable(R.drawable.rec_bg_1_white));
				btn_top_up_10.setTextColor(ActivityScoreTopUp.this.getResources().getColor(R.color.bg_gift_exchange_rule));
				btn_top_up_60.setTextColor(ActivityScoreTopUp.this.getResources().getColor(R.color.bg_gift_exchange_rule));
				et_top_up_other.setCursorVisible(true);
				
				if (isOpen==false) {
					UnitSociax.showSoftKeyborad(ActivityScoreTopUp.this, et_top_up_other);
				}
			}
		});
		et_top_up_other.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			 @Override
			 public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				 if (actionId == EditorInfo.IME_ACTION_SEND){     
					 if (isOpen==true) {
						UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
					}
			      }  
				 return false;
			 }
		});
		
		//动态监听充值金额
		setRegion(et_top_up_other);
		
		btn_top_up_now.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (isOpen==true) {
					UnitSociax.hideSoftKeyboard(ActivityScoreTopUp.this, et_top_up_other);
				}

				double money=0;
				if (TextUtils.isEmpty(FLAG)) {
					Toast.makeText(ActivityScoreTopUp.this, "请选择充值金额", 1).show();
					return;
				}
				else if(Double.parseDouble(FLAG)>100.0){
					Toast.makeText(ActivityScoreTopUp.this, "单次最高充值100元", 1).show();
					return;
				}else if(Double.parseDouble(FLAG)==0.0){
					Toast.makeText(ActivityScoreTopUp.this, "单次最低充值1元", 1).show();
					return;
				}else {
					money = Double.parseDouble(FLAG);
				}
				
				int type=-1;
				if (cb_top_up_zfb.isChecked()) {
					type=0;
				}
				else if (cb_top_up_wechat.isChecked()) {
					type=1;
				}
				else if (cb_top_up_cft.isChecked()) {
					
				}
				else if (cb_top_up_card.isChecked()) {
					
				}
				createCharge(money,type);
			}
		});
	}
	
	
	//动态判断输入的值
		private int MIN_MARK = 1; 
	    private int MAX_MARK =100;
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
	                             FLAG = markVal+"";
	                         } 
	                         catch (NumberFormatException e)  { 
	                             markVal = 0; 
	                         } 
	                         if (markVal > MAX_MARK) { 
	                             Toast.makeText(getBaseContext(), "单次最多充值100人民币", Toast.LENGTH_SHORT).show(); 
	                             et.setText(String.valueOf(MAX_MARK)); 
	                         } 
	                         if (markVal<MIN_MARK) {
	                        	  Toast.makeText(getBaseContext(), "单次最少充值1元", Toast.LENGTH_SHORT).show(); 
	                              et.setText(String.valueOf(MIN_MARK)); 
							}
	                         et.setSelection(et.getText().toString().length());
	                         return; 
	                    } 
	                 } 
	            } 
	        }); 
	    }
	
	/**
	 * 创建订单
	 * @param money
	 * @param type
	 */
	public void createCharge(final double money,final int type){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.CREATE_CHARGE;
				try {
					msg.obj = ((Thinksns) (ActivityScoreTopUp.this.getApplicationContext())).getApiCredit().createCharge(money, type);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(msg);
			}
		}).start();
	}
	/**
	 * 设置充值状态
	 * @param serial_number
	 * @param status
	 * @param sign
	 */
	public void saveCharge(final String serial_number,final int status,final String sign){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.SAVE_CHARGE;
				try {
					msg.obj = ((Thinksns) (ActivityScoreTopUp.this.getApplicationContext())).getApiCredit().saveCharge(serial_number, status, sign);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(msg);
			}
		}).start();
	}
	
	private void initData(){
		
	}
	
	@Override
	public String getTitleCenter() {
		return null;
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_score_top_up;
	}
	
	
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				Log.v("charge", "---------PayResult ---------------"+(String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(ActivityScoreTopUp.this, "支付成功",Toast.LENGTH_SHORT).show();
					if(modelCharge!=null){
						String sign=StringToMD5.MD5(modelCharge.getSerial_number()+"&"+1+"&"+AlipayConfig.KEY);
						saveCharge(modelCharge.getSerial_number(), modelCharge.getStatus(), sign);
					}
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(ActivityScoreTopUp.this, "支付结果确认中",Toast.LENGTH_SHORT).show();
						Log.v("charge", "------topUp---payResult----"+resultInfo);
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(ActivityScoreTopUp.this, "支付失败",Toast.LENGTH_SHORT).show();
						Log.v("charge", "------topUp---payResult----"+resultInfo);
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(ActivityScoreTopUp.this, "检查结果为：" + msg.obj,Toast.LENGTH_SHORT).show();
				break;
			}
			case StaticInApp.CREATE_CHARGE: {
				
				try {
					if (msg.obj!=null) {
						JSONObject result = new JSONObject(msg.obj.toString());
						if (result!=null) {
							int status=result.getInt("status");
							String mesage=result.getString("mesage");
							
							if (mesage!=null&&!mesage.equals("")&&!mesage.equals("null")) {
								Toast.makeText(ActivityScoreTopUp.this, mesage, 1).show();
							}
							if (status==1) {
								modelCharge = new ModelCharge(result.getJSONObject("data"));
								pay("积分充值", "1元=100积分", modelCharge.getCharge_value()+"");
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (DataInvalidException e) {
					e.printStackTrace();
				}
				
				break;
			}
			case StaticInApp.SAVE_CHARGE: {
				
				try {
					if (msg.obj!=null) {
						JSONObject result = new JSONObject(msg.obj.toString());
						if (result!=null) {
							int status=result.getInt("status");
							String mesage=result.getString("mesage");
							Toast.makeText(ActivityScoreTopUp.this, mesage, 1).show();
							if (status==1) {
								
								//发送广播至积分详情页面，更新页面
								Intent intent = new Intent(StaticInApp.UPDATE_SCORE_DETAIL);
								ActivityScoreTopUp.this.sendBroadcast(intent);
								
								ActivityScoreTopUp.this.finish();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				break;
			}
			
			default:
				break;
			}
		};
	};
	
	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String shopName,String shopInfo,String price) {
		if (TextUtils.isEmpty(AlipayConfig.PARTNER) || TextUtils.isEmpty(AlipayConfig.RSA_PRIVATE)
				|| TextUtils.isEmpty(AlipayConfig.SELLER)) {
			new AlertDialog.Builder(this)
					.setTitle("警告")
					.setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									//
									finish();
								}
							}).show();
			return;
		}
		// 订单
		String orderInfo = getOrderInfo(shopName,shopInfo, "0.01");

		Log.v("charge", "-------orderInfo------------"+orderInfo);
		
		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		
		Log.v("charge", "---sign----orderInfo------------"+orderInfo);
		
		try {
			// 仅需对sign 做URL编码
			if (sign!=null) {
				sign = URLEncoder.encode(sign, "UTF-8");
			}else {
				Log.v("charge", "-----sign==null----------");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.v("charge", "-----UnsupportedEncodingException----------"+e.getMessage());
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"+ getSignType();
		
		Log.v("charge", "-----payInfo----------"+payInfo);
		
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(ActivityScoreTopUp.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + AlipayConfig.PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + AlipayConfig.SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, AlipayConfig.RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}