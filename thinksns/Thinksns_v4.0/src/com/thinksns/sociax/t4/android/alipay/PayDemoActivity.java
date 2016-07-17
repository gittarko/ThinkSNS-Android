package com.thinksns.sociax.t4.android.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.thinksns.sociax.android.R;

public class PayDemoActivity extends FragmentActivity {

	// 商户PID
//	public static final String PARTNER = "2088811680570035";
////	public static final String PARTNER = "2088021476306753";
//	// 商户收款账号
//	public static final String SELLER = "webmaster@jdly.me";
////	public static final String SELLER = "kehaitian@zhishisoft.com";
//	// 商户私钥，pkcs8格式
//	public static final String RSA_PRIVATE = "MIICXQIBAAKBgQCtJ2e6jnXBjbSONCiysmaXEML/ds5fCNBVvShWr2oQte3m0lU/"
//												+ "q9RDKEDAQe9roUuweNAjFMhOdc8zmy/8C+4y7cbcLSMvkw1Uv7e03pNlcFByY1rX"
//												+ "8Ap8zzQCuZlpmNCfkXPGn5u08Fqx6Lj/jrfd6y0zSqFS1E/OYYLNZf5H4QIDAQAB"
//												+ "AoGALXcDTYDKep/yoMQhS4p8VX4ZNWrElT+DNIAOb3RvrSOX69uedrFvcpLHLbQi"
//												+ "YWhgr12JiSyTy8YzOaGt38hiKp5ghGYb9se6FyTEYmn54TAf7oiQKYpk7KRHnczy"
//												+ "Is3AhrjuNnSIeRkjtTcNG3NBjcqyAGz0DtR4YB1szbA2UGECQQDhkyMtl9iaYoGV"
//												+ "YCcfTejfifto/yYo1JjYLUMaF5GylA6Yt6Q8szPdGd/FpH9ewlBQNi9VDTjW/JxW"
//												+ "K0juV4bzAkEAxII7GfXI7qg5LYC+r7xVvKzHETp1R+NPD1xob8iKqSH1otoilWLi"
//												+ "4Y19mINEFGR1gE8KKeDPPZrGIYY+aYxS2wJACuBlWdVwhEErrmAsgr3m9KBalv12"
//												+ "gZF+rS3BhoiMZeftrD9uk96wKYEN4SHFYnRMGxIBxkAU9YjIoP8FTTi7MQJBAIBW"
//												+ "ESUuKPD8eoMmLuL0nAXhzQyKYif14x9EGYzPqwc0f4jA748iiswFrP3a1K6AwWBv"
//												+ "vlKFWLOCDl28n6wtEbsCQQCvNieIey0ew2jcIDLAuTjB5urJEZ7P9YEgApSJdY9F"
//												+ "tbzD0PmMAIOu65Cxxj4SUf38lHialrcqpJ9o2HXh0XMW";
////	public static final String RSA_PRIVATE = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAOT2x7XhLqWhi/yiWt4j+AvQzs7JMDeebGHLtESm4j+QRf4uy8JW+OZyyYUOfFUdyyE8/3QOih8q/8Afsic71fDrwhcPGrs0EMyvzBx3S4NDDoCZXm2zJBueWHrrezTdhfd0MzKBdAULnPb0wuYw+9LBAG4jDcB7dgXxjkVQvrOZAgMBAAECgYEAmFLValr0/ZwL1XbtnPuw+atg6pFOUrJCyI0M0N91hXQmHIRlalUSIb4b8zk/3iCEEwjRqhRCL5obq2Vq0E8udGc9XsBx9DT8xaxo6NAl1ZEDmi3tckWxHOvvHya3cKjLoXUm6YoFBuCKaVSBmyNlRbxz94+W1jlkRjzLUV9igEECQQD0cW3pj8iHZ41iNChXBBefeODzz/hJYnAmdUKbsGU9QdAM+IInAxP8MbYby59XySEK1S6ghqQ342HRUPUHNJMvAkEA78oAn0qRfp9SCcYMx8pwq/xVa9RaPp2sbNNHNvO6TJVDHMaUolFG5KqbstqgKSSMf8/Q6nC3pccQiDo5ZVMTtwJBAN8ADBGjhoFgyu7oPlUD+rkVgcNr20q3bzDmkNmP1wxHWhI7NCF7AWR1xXkeDkP7zRUg3uS7cF08JyTaAm+MWykCQQCxdfgb613eLh7S6OXQfrwALJEzakcXjC1tpLdRrfM0dkri4vxhXHFxDeqI5VAe4tOwS6a7uovPfpF3oE+V83PlAkEA3X/S49wLZcu30hfDiaB+gnYS+Uq9lTZis71yquTCcKmrRh9UoCF7a11+K3g/I6a8BMQxqBpE62PM0R/e3amQgA==";
//	// 支付宝公钥
//	public static final String RSA_PUBLIC = "";
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				Log.v("topUp", "------topUp--msg-----"+(String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(PayDemoActivity.this, "支付成功",Toast.LENGTH_SHORT).show();
					Log.v("topUp", "------topUp---payResult----"+resultInfo);
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayDemoActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();
						Log.v("topUp", "------topUp---payResult----"+resultInfo);
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayDemoActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();
						Log.v("topUp", "------topUp---payResult----"+resultInfo);
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(View v) {
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
		String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");

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
				PayTask alipay = new PayTask(PayDemoActivity.this);
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
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(PayDemoActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

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
