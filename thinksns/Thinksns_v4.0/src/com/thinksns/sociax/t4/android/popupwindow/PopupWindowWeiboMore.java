package com.thinksns.sociax.t4.android.popupwindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.adapter.AdapterWeiboList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;

import com.thinksns.sociax.t4.android.weibo.ActivityCreateChannelWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateTransportWeibo;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.t4.android.function.FunctionThirdPlatForm;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 微博点击更多，生成的分享/操作popupwibdow
 * 
 * @author wz
 * @date 2014-10-21
 * @version 1.0
 */
public class PopupWindowWeiboMore implements OnClickListener {
	private static final String TAG = "MorePopupWindow";
	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;

	private Button mTvTranspond, mTvCollection,
			mTvDenounce, mTvDelete;
	private Button tv_share_to_sina, tv_share_to_weichat,
			tv_share_to_weichatfriends,
			tv_share_to_qq, tv_share_to_qqweibo,
			tv_share_to_qzone;
	private Context context;
	private Button btnCancel;
	private ModelWeibo weibo;
	private int position;

	private Fragment fragment;
	private PopupWindowHandler handler;
	private AdapterSociaxList adapter;

	public interface OnWeiboMoreClickListener {
		public void onDelete(int status);
		public void onCollect(int status);
	}

	private OnWeiboMoreClickListener weiboMoreClickListener;

	public void setOnWeiboMoreClickListener(OnWeiboMoreClickListener weiboMoreClickListener) {
		this.weiboMoreClickListener = weiboMoreClickListener;
	}
	/**
	 * fragment内微博列表点击更多之后生成的popupwindow
	 * 
	 * @param fragment
	 *            当前列表所在fragment
	 * @param weibo
	 *            被点击的微博
	 * @param position
	 *            被点击的item的位置
	 * @param adapter2
	 *            操作列表的adapter
	 */
	@SuppressLint("NewApi")
	public PopupWindowWeiboMore(Fragment fragment, ModelWeibo weibo,
								int position, AdapterWeiboList adapter2) {
		super();
		this.fragment = fragment;
		this.context = fragment.getActivity();
		this.inflater = LayoutInflater.from(context);
		this.weibo = weibo;
		this.position = position;
		this.adapter = adapter2;
		handler = new PopupWindowHandler();
		initSharePlatform();
	}

	public PopupWindowWeiboMore(Context context, final ModelWeibo weibo,
								OnWeiboMoreClickListener listener) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.weibo = weibo;
		setOnWeiboMoreClickListener(listener);

		handler = new PopupWindowHandler();
		initSharePlatform();
	}




		/**
         * 初始化分享平台
         *
         */
	private void initSharePlatform() {
		ShareSDK.initSDK(context);
	}

	/**
	 * Activity内微博列表的更多
	 * 
	 * @param context
	 * @param weibo
	 *            微博
	 * @param position
	 *            微博所在position，用于修改list
	 * @param adapter
	 *            微博list的adapter
	 */
	public PopupWindowWeiboMore(Context context, ModelWeibo weibo,
			int position, AdapterWeiboList adapter) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.weibo = weibo;
		this.position = position;
		this.adapter = adapter;
		handler = new PopupWindowHandler();
		initSharePlatform();
	}

	/**
	 * 从微博详情页面生成的更多
	 * 
	 * @param activity
	 * @param weibo
	 */
	public PopupWindowWeiboMore(ThinksnsAbscractActivity activity,
			ModelWeibo weibo) {
		super();
		this.context = activity;
		this.inflater = LayoutInflater.from(context);
		this.weibo = weibo;
		handler = new PopupWindowHandler();
		initSharePlatform();
		initPopuptWindow();
	}

	/**
	 * 获取PopupWindow实例
	 */
	public PopupWindow getPopupWindowInstance() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
		} else {
			initPopuptWindow();
		}
		return mPopupWindow;
	}

	/*
	 * 创建PopupWindow
	 */
	private void initPopuptWindow() {
		View popupWindow = inflater.inflate(R.layout.more_popupwindow, null);
		btnCancel = (Button) popupWindow.findViewById(R.id.btn_pop_cancel);
		btnCancel.setOnClickListener(this);
		//转发
		mTvTranspond = (Button) popupWindow
				.findViewById(R.id.tv_more_transpond);
		//收藏
		mTvCollection = (Button) popupWindow
				.findViewById(R.id.tv_more_collection);
		//举报
		mTvDenounce = (Button) popupWindow
				.findViewById(R.id.tv_more_denounce);
		//删除
		mTvDelete = (Button) popupWindow.findViewById(R.id.tv_more_delete);

		tv_share_to_sina = (Button) popupWindow
				.findViewById(R.id.tv_share_to_sinaweibo);
		tv_share_to_weichat = (Button) popupWindow
				.findViewById(R.id.tv_share_to_weichat);
		tv_share_to_weichatfriends = (Button) popupWindow
				.findViewById(R.id.tv_share_to_weichatfav);
		tv_share_to_qq = (Button) popupWindow
				.findViewById(R.id.tv_share_to_qq);
		tv_share_to_qqweibo = (Button) popupWindow
				.findViewById(R.id.tv_share_to_qqweibo);
		tv_share_to_qzone = (Button) popupWindow
				.findViewById(R.id.tv_share_to_qzone);

		if (weibo != null && weibo.isFavorited()) {
			mTvCollection.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ic_more_collected, 0, 0);
            mTvCollection.setText("取消收藏");
		} else {
			mTvCollection.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ic_more_collect, 0, 0);
            mTvCollection.setText("收藏");
		}
//		mTvComment.setOnClickListener(this);
		mTvTranspond.setOnClickListener(this);
		mTvCollection.setOnClickListener(this);

		tv_share_to_weichat.setOnClickListener(this);
		tv_share_to_weichatfriends.setOnClickListener(this);
		tv_share_to_sina.setOnClickListener(this);
		tv_share_to_qq.setOnClickListener(this);
		tv_share_to_qzone.setOnClickListener(this);
		tv_share_to_qqweibo.setOnClickListener(this);

		if (weibo.getUid() == Thinksns.getMy().getUid()||Thinksns.getMy().getIs_admin().equals("1")) {
			mTvDenounce.setVisibility(View.GONE);
			mTvDelete.setVisibility(View.VISIBLE);
			mTvDelete.setOnClickListener(this);
		} else {
			mTvDenounce.setVisibility(View.VISIBLE);
			mTvDelete.setVisibility(View.GONE);
			mTvDenounce.setOnClickListener(this);
		}
		
		mPopupWindow = new PopupWindow(popupWindow, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		mPopupWindow.setFocusable(true);
	    // 实例化一个ColorDrawable颜色为半透明
	    ColorDrawable dw = new ColorDrawable(0xb0000000);
	    mPopupWindow.setBackgroundDrawable(dw);

	    // 设置popWindow的显示和消失动画
	    mPopupWindow.setAnimationStyle(R.style.popUpwindow_anim);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				backgroundAlpha(1f);
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_pop_cancel:
			mPopupWindow.dismiss();
			break;
		case R.id.tv_more_transpond:
			Intent transIntent = new Intent(context, ActivityCreateTransportWeibo.class);
			transIntent.putExtra("type", AppConstant.CREATE_TRANSPORT_WEIBO);
//			transIntent.putExtra("weibo", weibo);
			if(weibo.getChannel_category_id() > 0) {
				transIntent.putExtra("feed_name", weibo.getChannel_name());
			}else {
				transIntent.putExtra("position", position);
			}
			if(weibo.getIsRepost() > 0) {
				//如果微博是转发类型
				transIntent.putExtra("content", "//@" + weibo.getUsername() + " ：" + weibo.getContent());
				transIntent.putExtra("feed_id", weibo.getSid());
			}else
				transIntent.putExtra("feed_id", weibo.getWeiboId());

			((Activity) context).startActivityForResult(transIntent,
					AppConstant.TRANSPOND);
			mPopupWindow.dismiss();
			Anim.in((Activity) context);
			break;
		case R.id.tv_more_collection:
			try {
				new Api.StatusesApi().favWeibo(weibo, new HttpResponseListener() {

					@Override
					public void onSuccess(Object result) {
						JSONObject json = (JSONObject) result;
						try {
							if (json.getInt("status") == 1) {
								weibo.setFavorited(!weibo.isFavorited());
								int drawableRes = weibo.isFavorited() ? R.drawable.ic_more_collected : R.drawable.ic_more_collect;
								mTvCollection.setCompoundDrawablesWithIntrinsicBounds(0, drawableRes, 0, 0);
								mTvCollection.setText(weibo.isFavorited() ? "取消收藏" : "收藏");
							} else {
								Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(context, "数据解析错误", Toast.LENGTH_SHORT).show();
						}
						mTvCollection.setEnabled(true);
					}

					@Override
					public void onError(Object result) {
						Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
						mTvCollection.setEnabled(true);
					}
				});
			} catch (ApiException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case R.id.tv_more_denounce:
			PopupWindowDialog1 pup = new PopupWindowDialog1(context, "温馨提示",
					"不良信息是指含有色情、暴力、广告或者其他骚扰你正常工作生活的内容。您是否举报？", "举报", "再想想");
			pup.setListenerSociax(new ListenerSociax() {

				@Override
				public void onTaskSuccess() {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Message message = handler.obtainMessage();
							try {
								ModelBackMessage backMessage = null;
								message.what = AppConstant.DENOUNCE;
								backMessage = new Api.StatusesApi()
										.denounceWeibo(weibo.getWeiboId(), null);
								message.obj = backMessage;
								message.arg1 = 1;
								handler.sendMessage(message);
							} catch (ApiException e) {
								message.arg1 = 2;
								e.printStackTrace();
							} catch (JSONException e) {
								message.arg1 = 2;
								e.printStackTrace();
							}
						}
					}).start();
				}

				@Override
				public void onTaskError() {
				}

				@Override
				public void onTaskCancle() {
				}
			});
			pup.show();
			mPopupWindow.dismiss();
			break;
		case R.id.tv_more_delete:
			//删除微博前确认
			confirmDeleteWeibo();
			break;
		case R.id.tv_share_to_sinaweibo:
			onSinaWeiboShare();
			break;
		case R.id.tv_share_to_weichat:
			onweichatShare();
			break;
		case R.id.tv_share_to_qq:
			onQQShare();
			break;
		case R.id.tv_share_to_weichatfav:
			onWeichatMovementShare();
			break;
		case R.id.tv_share_to_qzone:
			onQzoneShare();
			break;
		case R.id.tv_share_to_qqweibo:
			onQQWeiboShare();
			break;
		default:
			break;
		}
	}

	//确认删除微博
	private void confirmDeleteWeibo() {
		PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(context);
		builder.setMessage("确认删除?", 16);
		builder.setTitle(null, 0);
		builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mTvDelete.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						Message message = handler.obtainMessage();
						try {
							ModelBackMessage backMessage = null;
							message.what = AppConstant.DELETEWEIBO;
							backMessage = new Api.StatusesApi().deleteWeibo(weibo);
							message.obj = backMessage;
							message.arg1 = 1;
							handler.sendMessage(message);
						} catch (ApiException e) {
							message.arg1 = 2;
							e.printStackTrace();
						} catch (JSONException e) {
							message.arg1 = 2;
							e.printStackTrace();
						}
					}
				}).start();
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create();
	}

	public void collect() {
		mTvCollection.performClick();
	}
	
	public void transport() {
		mTvTranspond.performClick();
	}
	
	/**
	 * 分享到腾讯微博
	 */
	private void onQQWeiboShare() {
//		FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
//				ShareSDK.getPlatform(TencentWeibo.NAME));
//		fc_share.doShareWeibo(weibo);
	}

	/**
	 * 分享到QQ空间
	 */
	private void onQzoneShare() {
		FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
				ShareSDK.getPlatform(QZone.NAME));
		fc_share.doShareWeibo(weibo);
	}

	/**
	 * 分享到朋友圈
	 */
	private void onWeichatMovementShare() {
		FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
				ShareSDK.getPlatform(WechatMoments.NAME));
		fc_share.doShareWeibo(weibo);
	}

	/**
	 * 分享到微信
	 */
	private void onweichatShare() {
		FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
				ShareSDK.getPlatform(Wechat.NAME));
		fc_share.doShareWeibo(weibo);
	}

	/**
	 * 分享到QQ
	 */
	private void onQQShare() {
		FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
				ShareSDK.getPlatform(QQ.NAME));
		fc_share.doShareWeibo(weibo);
	}

	/**
	 * 分享到微博
	 */
	private void onSinaWeiboShare() {
		FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(context,
				ShareSDK.getPlatform(SinaWeibo.NAME));
		fc_share.doShareWeibo(weibo);
	}

	class PopupWindowHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case AppConstant.FAVORITE:
				if (msg.arg1 == 1) {
					ModelBackMessage message = (ModelBackMessage) msg.obj;
					if (message.getStatus() == 1) {
						Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT)
								.show();
						weibo.setFavorited(true);
						if (adapter != null) {
							((ModelWeibo) adapter.getList().get(position)).setFavorited(true);
							adapter.notifyDataSetChanged();
						}else {
							if(weiboMoreClickListener != null) {
								weiboMoreClickListener.onCollect(1);
							}
						}
						mPopupWindow.dismiss();
					} else {
						Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT)
								.show();
						if(weiboMoreClickListener != null) {
							weiboMoreClickListener.onCollect(0);
						}
					}
				}
				break;
			case AppConstant.UNFAVORITE:
				if (msg.arg1 == 1) {
					ModelBackMessage message = (ModelBackMessage) msg.obj;
					if (message.getStatus() == 1) {
						Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
						//发送广播至朋友圈，更新页面
						Intent intent = new Intent();  
						intent.setAction(StaticInApp.NOTIFY_WEIBO);  
						context.sendBroadcast(intent);
						weibo.setFavorited(false);
						if (adapter != null) {
							//取消收藏，暂时屏蔽
//							((ModelWeibo) adapter.getList().get(position)).setFavorited(false);
//							if (adapter.getFragment() instanceof FragmentCollectWeibo) {
//								adapter.getList().remove(position);
//							}
//							adapter.notifyDataSetChanged();
						}else {
							if(weiboMoreClickListener != null) {
								weiboMoreClickListener.onCollect(1);
							}
						}
						mPopupWindow.dismiss();
					} else {
						Toast.makeText(context, "取消收藏失败", Toast.LENGTH_SHORT).show();
						if(weiboMoreClickListener != null) {
							weiboMoreClickListener.onCollect(0);
						}
					}
				}
				break;
			case AppConstant.DELETEWEIBO:
				mTvDelete.setEnabled(true);
				if (msg.arg1 == 1) {
					ModelBackMessage message = (ModelBackMessage) msg.obj;
					if (message.getStatus() == 1) {
						Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
						//删除数据库中的这条数据
						DbHelperManager.getInstance(Api.mContext, ListData.DataType.ALL_WEIBO).delete(weibo);
						if (adapter != null) {
							((AdapterSociaxList) adapter).deleteItem(position);
						}
						if (context instanceof ActivityWeiboDetail) {
							((ActivityWeiboDetail) context).finish();
						}
						if(weiboMoreClickListener != null) {
							weiboMoreClickListener.onDelete(1);
						}
						mPopupWindow.dismiss();
					} else {
						Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
						if(weiboMoreClickListener != null) {
							weiboMoreClickListener.onDelete(0);
						}
					}
				}
				break;
			case AppConstant.DENOUNCE:
				if (msg.arg1 == 1) {
					ModelBackMessage message = (ModelBackMessage) msg.obj;
					if (message.getStatus() == 1) {
						mPopupWindow.dismiss();
					}
					Toast.makeText(context, message.getMsg(),
							Toast.LENGTH_SHORT).show();
					mPopupWindow.dismiss();
				}
			default:

				break;
			}
		}
	}

	/**
	 * 自定义分享个数
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getSharedByData() {
		List<Map<String, Object>> sharedList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 6; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bgId", R.drawable.comment);
			map.put("text", "微信" + i);
			sharedList.add(map);
		}
		return sharedList;
	}
	
	public void showBottom(View parent) {
		if(mPopupWindow == null) {
			initPopuptWindow();
		}
		
		mPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		//设置屏幕透明度
		backgroundAlpha(0.4f);
	}
	
	public void dismiss() {
		if(mPopupWindow == null)
			return;
		mPopupWindow.dismiss();
	}
	
	public void hideCollect() {
		if(mTvCollection != null) {
			mTvCollection.setVisibility(View.GONE);
		}
	}
	
	public void hideTransport() {
		if(mTvTranspond != null) {
			mTvTranspond.setVisibility(View.GONE);
		}
	}
	
	/** 
     * 设置添加屏幕的背景透明度 
     * @param bgAlpha 
     */  
    public void backgroundAlpha(float bgAlpha){  
    	if(context instanceof Activity) {
			Window window = ((Activity)context).getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();  
			lp.alpha = bgAlpha; //0.0-1.0  
			window.setAttributes(lp);  
    	}
    }  
}
