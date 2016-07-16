///**
// * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *     http://www.apache.org/licenses/LICENSE-2.0
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.thinksns.tschat.map;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.BDNotifyListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.BMapManager;
//import com.baidu.mapapi.MKGeneralListener;
//import com.baidu.mapapi.map.ItemizedOverlay;
//import com.baidu.mapapi.map.MKEvent;
//import com.baidu.mapapi.map.MKMapViewListener;
//import com.baidu.mapapi.map.MapController;
//import com.baidu.mapapi.map.MapPoi;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.OverlayItem;
//import com.baidu.mapapi.utils.CoordinateConvert;
//import com.baidu.platform.comapi.basestruct.GeoPoint;
//import com.thinksns.tschat.R;
//import com.thinksns.tschat.api.MessageApi;
//import com.thinksns.tschat.api.RequestResponseHandler;
//import com.thinksns.tschat.unit.ImageUtil;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//
//public class ActivityBaiduMap extends FragmentActivity implements OnClickListener{
//
//	private final static String TAG = "map";
//
//	private ImageView iv_back;
//	private TextView tv_title;
//	private TextView tv_send;
//
//	static MapView mMapView = null;
//	private MapController mMapController = null;
//	public MKMapViewListener mMapListener = null;
//	private FrameLayout mMapViewContainer = null;
//
//	// 定位相关
//	LocationClient mLocClient;
//	public MyLocationListenner myListener = new MyLocationListenner();
//	public NotifyLister mNotifyer = null;
//	static BDLocation lastLocation = null;
//
//	public static ActivityBaiduMap instance = null;
//
//	ItemizedOverlay<OverlayItem> mAddrOverlay = null;
//	private static int room_id;
//
//	// for baidu map
//	public static BMapManager mBMapManager = null;
//	public static final String strKey = "zkRyGeZEw6awNfAuiKfbzd60";
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		if (mBMapManager == null) {
//			initEngineManager(this.getApplicationContext());
//		}
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(getLayoutId());
//
//		instance = this;
//		// Gl app = (Gl)this.getApplication();
//		initView();
//
//		initMapView();
//
//		initIntent();
//
//	}
//
//	private void initIntent() {
//		Intent intent = getIntent();
//		double latitude = Double.parseDouble(intent.getStringExtra("latitude"));
//		if (latitude == 0) {
//			showMapWithLocationClient();
//		} else {
//			double longtitude =intent.getDoubleExtra("longitude",0.1);
//			String address = intent.getStringExtra("address");
//			if (!address.equals("")&&!address.equals("null")
//					&& address!=null) {
//				showMap(latitude,longtitude, address);
//			}else {
//				address = "未获取到位置的名称";
//				showMap(latitude,longtitude, address);
//			}
//			tv_title.setText("查看位置");
//		}
//		room_id = intent.getIntExtra("room_id", -1);
//	}
//
//	private void initView() {
//		iv_back = (ImageView) findViewById(R.id.iv_back);
//		tv_title = (TextView) findViewById(R.id.tv_title_center);
//		tv_send = (TextView) findViewById(R.id.tv_send);
//		//设置发送按钮处于不可编辑状态
//		tv_send.setAlpha(0.5f);
//		tv_send.setEnabled(false);
//
//		iv_back.setOnClickListener(this);
//		tv_send.setOnClickListener(this);
//	}
//
//	/**
//	 * 已经知道位置，则直接定位
//	 *
//	 * @param latitude
//	 * @param longtitude
//	 * @param address
//	 */
//	private void showMap(double latitude, double longtitude, String address) {
//		tv_send.setVisibility(View.GONE);
//		GeoPoint point1 = new GeoPoint((int) (latitude * 1e6),(int)(longtitude * 1e6));
//		point1 = CoordinateConvert.fromGcjToBaidu(point1);
//		mMapController.setCenter(point1);
//		Drawable marker = this.getResources()
//				.getDrawable(R.drawable.icon_marka);
//		// 为maker定义位置和边界
//		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
//				marker.getIntrinsicHeight());
//		mAddrOverlay = new ItemizedOverlay<OverlayItem>(marker, mMapView);
//		GeoPoint point = new GeoPoint((int)(latitude* 1e6),
//				(int)(longtitude * 1e6));
//		point = CoordinateConvert.fromGcjToBaidu(point);
//		OverlayItem addrItem = new OverlayItem(point, "", address);
//		mAddrOverlay.addItem(addrItem);
//		mMapView.getOverlays().add(mAddrOverlay);
//		mMapView.refresh();
//	}
//
//	/**
//	 * 重新定位
//	 */
//	private void showMapWithLocationClient() {
////		progressDialog = (LoadingView) findViewById(LoadingView.ID);
////		progressDialog.show(mMapView);
//
//		mLocClient = new LocationClient(this);
//		mLocClient.registerLocationListener(myListener);
//
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		// option.setCoorType("bd09ll"); //设置坐标类型
//		// Johnson change to use gcj02 coordination. chinese national standard
//		// so need to conver to bd09 everytime when draw on baidu map
//		option.setCoorType("gcj02");
//		option.setScanSpan(30000);
//		option.setAddrType("all");
//		mLocClient.setLocOption(option);
//
//		Drawable marker = this.getResources()
//				.getDrawable(R.drawable.icon_marka);
//		// 为maker定义位置和边界
//		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
//				marker.getIntrinsicHeight());
//		mAddrOverlay = new ItemizedOverlay<OverlayItem>(marker, mMapView);
//		mMapView.getOverlays().add(mAddrOverlay);
//
//		mMapListener = new MKMapViewListener() {
//
//			@Override
//			public void onMapMoveFinish() {
//			}
//
//			@Override
//			public void onClickMapPoi(MapPoi mapPoiInfo) {
//				String title = "";
//				if (mapPoiInfo != null) {
//					title = mapPoiInfo.strText;
//					Toast.makeText(ActivityBaiduMap.this, title,
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//
//			@Override
//			public void onGetCurrentMap(Bitmap b) {
//				ImageUtil iu = new ImageUtil();
//				String urlName = null;
//				String result = "";
//				urlName ="thinksns4_"+ System.currentTimeMillis() + ".jpg";
//				try {
//					result = iu.saveFile(urlName, b);
//					if (!result.equals("")) {
//						if (ImageUtil.getSDPath() == null) {
//							Toast.makeText(getApplicationContext(),
//									"保存失败,没有获取到SD卡", Toast.LENGTH_SHORT).show();
//							return;
//						}
//						upLoadMapThread(result);
//					} else {
//						Toast.makeText(getApplicationContext(), "保存失败",Toast.LENGTH_SHORT).show();
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//
//				}
//			}
//
//			@Override
//			public void onMapAnimationFinish() {
//			}
//		};
//
//		mMapView.regMapViewListener(mBMapManager, mMapListener);
//
//		if (lastLocation != null) {
//			GeoPoint point1 = new GeoPoint(
//					(int) (lastLocation.getLatitude() * 1e6),
//					(int) (lastLocation.getLongitude() * 1e6));
//			point1 = CoordinateConvert.fromGcjToBaidu(point1);
//			mMapController.setCenter(point1);
//		}
//		mMapView.refresh();
//		mMapView.invalidate();
//	}
//
//	/**
//	 * 上传地图
//	 *
//	 * @param path
//	 *   地图保存路径
//	 */
//	protected void upLoadMapThread(final String path) {
//		MessageApi.uploadImageMessage(room_id, path, new RequestResponseHandler() {
//			@Override
//			public void onSuccess(Object result) {
//						String attach_id = result.toString();
//						if (lastLocation != null) {
//							Intent intent = new Intent();
//							intent.putExtra("latitude", lastLocation.getLatitude());
//							intent.putExtra("longitude", lastLocation.getLongitude());
//							intent.putExtra("location", lastLocation.getAddrStr());
//							intent.putExtra("attach_id", attach_id);
//							setResult(RESULT_OK, intent);
//							finish();
//						}else {
//							Log.e("ActivityBaiduMap", "地图位置信息 is null");
//						}
//				}
//
//			@Override
//			public void onFailure(Object errorResult) {
//				if(errorResult != null)
//					Log.e("ActivityBaiduMap", "上传地图失败：" + errorResult.toString());
//			}
//		});
////		new Thread(new Runnable() {
////			@Override
////			public void run() {
////				progressDialog.show(mMapView);
////				Thinksns app = (Thinksns) ActivityBaiduMap.this.getApplication();
////				Message msg = uiHandler.obtainMessage();
////				msg.arg1 = StaticInApp.UPLOAD_FILE;
//				// result = app.getApi().uploadFile(path, "image");
////				if (room_id != 0 && room_id != -1) {
////					msg.obj = app.getMessages().sendImgMessage(room_id, null,path);
////				}
////				uiHandler.sendMessage(msg);
////			}
////		}).start();
//	}
//
//	@Override
//	protected void onPause() {
//		mMapView.onPause();
//		if (mLocClient != null) {
//			mLocClient.stop();
//		}
//		super.onPause();
//		lastLocation = null;
//	}
//
//	@Override
//	protected void onResume() {
//		mMapView.onResume();
//		if (mLocClient != null) {
//			mLocClient.start();
//		}
//		super.onResume();
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (mLocClient != null)
//			mLocClient.stop();
//		mMapView.destroy();
//
//		// Gl app = (Gl)this.getApplication();
//		if (mBMapManager != null) {
//			mBMapManager.destroy();
//			mBMapManager = null;
//		}
//		super.onDestroy();
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		mMapView.onSaveInstanceState(outState);
//	}
//
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		mMapView.onRestoreInstanceState(savedInstanceState);
//	}
//
//	private void initMapView() {
//		mMapView = (MapView) findViewById(R.id.bmapView);
//		mMapController = mMapView.getController();
//		mMapView.setLongClickable(true);
//		mMapView.getController().setZoom(17);
//		mMapView.getController().enableClick(true);
//		mMapView.setBuiltInZoomControls(true);
//	}
//
//	@Override
//	public void onClick(View view) {
//		int id = view.getId();
//		if(id == R.id.iv_back) {
//			finish();
//		}else if(id == R.id.tv_send) {
//			//发送位置
//			sendLocation();
//		}
//
//	}
//
//	/**
//	 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
//	 */
//	public class MyLocationListenner implements BDLocationListener {
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			if (location == null) {
//				return;
//			}
//			Log.d("map", "On location change received:" + location);
//			Log.d("map", "addr:" + location.getAddrStr());
//			tv_send.setEnabled(true);
//			tv_send.setAlpha(1.0f);
//			//注释
////			if (progressDialog != null) {
////				progressDialog.hide(mMapView);
////			}
//
//			if (lastLocation != null) {
//				if (lastLocation.getLatitude() == location.getLatitude()
//						&& lastLocation.getLongitude() == location.getLongitude()) {
//					Log.d("map", "same location, skip refresh");
//					// mMapView.refresh(); //need this refresh?
//					return;
//				}
//			}
//
//			lastLocation = location;
//
//			GeoPoint gcj02Point = new GeoPoint(
//					(int) (location.getLatitude() * 1e6),
//					(int) (location.getLongitude() * 1e6));
//			GeoPoint point = CoordinateConvert.fromGcjToBaidu(gcj02Point);
//
//			// GeoPoint p1 = gcjToBaidu(location.getLatitude(),
//			// location.getLongitude());
//			// System.err.println("johnson change to baidu:" + p1);
//			// GeoPoint p2 = baiduToGcj(location.getLatitude(),
//			// location.getLongitude());
//			// System.err.println("johnson change to gcj:" + p2);
//
//			OverlayItem addrItem = new OverlayItem(point, "title",location.getAddrStr());
//			mAddrOverlay.removeAll();
//			mAddrOverlay.addItem(addrItem);
//			mMapView.getController().setZoom(17);
//			mMapView.refresh();
//			mMapController.animateTo(point);
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//			if (poiLocation == null) {
//				return;
//			}
//		}
//	}
//
//	public class NotifyLister extends BDNotifyListener {
//		public void onNotify(BDLocation mlocation, float distance) {
//		}
//	}
//
//	public void back(View v) {
//		finish();
//	}
//
//	/**
//	 * 发送地图
//	 */
//	public void sendLocation() {
//		mMapView.getCurrentMap();// 保存当前地图
//	}
//
//	/**
//	 * 初始化百度引擎，必须在setContentView之前调用
//	 *
//	 * @param context
//	 */
//	public void initEngineManager(Context context) {
//		if (mBMapManager == null) {
//			mBMapManager = new BMapManager(context);
//		}
//
//		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
//			Toast.makeText(this.getApplicationContext(), "BMapManager  初始化错误!",
//					Toast.LENGTH_LONG).show();
//		}
//	}
//
//	class MyGeneralListener implements MKGeneralListener {
//
//		@Override
//		public void onGetNetworkState(int iError) {
//			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
//				Toast.makeText(ActivityBaiduMap.this, "您的网络出错啦！",
//						Toast.LENGTH_LONG).show();
//			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
//				Toast.makeText(ActivityBaiduMap.this, "输入正确的检索条件！",
//						Toast.LENGTH_LONG).show();
//			}
//		}
//
//		@Override
//		public void onGetPermissionState(int iError) {
//			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
//				// 授权Key错误：
//				Log.e("map", "permissio denied. check your app key");
//			}
//		}
//	}
//
//	static final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
//
//	public static GeoPoint gcjToBaidu(double lat, double lng) {
//		double x = lng, y = lat;
//		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
//		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
//		double bdLng = z * Math.cos(theta) + 0.0065;
//		double bdLat = z * Math.sin(theta) + 0.006;
//		return new GeoPoint((int) (bdLat * 1e6), (int) (bdLng * 1e6));
//	}
//
//	public static GeoPoint baiduToGcj(double lat, double lng) {
//		double x = lng - 0.0065, y = lat - 0.006;
//		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
//		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
//		double gcjLng = z * Math.cos(theta);
//		double gcjLat = z * Math.sin(theta);
//		return new GeoPoint((int) (gcjLat * 1e6), (int) (gcjLng * 1e6));
//	}
//
////	@Override
////	protected CustomTitle setCustomTitle() {
////		// return new LeftAndRightTitle(this,
////		// R.drawable.img_back,R.drawable.img_send_position);
////		return new LeftAndRightTitle(this, R.drawable.img_back, "发送");
////	}
////
////	@Override
////	public OnClickListener getLeftListener() {
////		return super.getLeftListener();
////	}
////
////	@Override
////	public OnClickListener getRightListener() {
////		return new OnClickListener() {
////
////			@Override
////			public void onClick(View v) {
////				sendLocation();
////			}
////		};
////	}
//
//	protected int getLayoutId() {
//		return R.layout.activity_chat_baidumap;
//	}
//
////	class UIHandler extends Handler {
////		@Override
////		public void handleMessage(Message msg) {
////			super.handleMessage(msg);
////			if (msg.arg1 == StaticInApp.UPLOAD_FILE) {
//////				progressDialog.hide(mMapView);
////				try {
////					if (msg.obj == null) {
////						Toast.makeText(ActivityBaiduMap.this,R.string.upload_false, Toast.LENGTH_LONG).show();
////					} else {
////						ModelAttach attach = null;
////						JSONObject object=new JSONObject(msg.obj.toString());
////						ArrayList<String> attachId_list =null;
////
////						if(object.has("status")&&object.getString("status").equals("1")){
////							if (object.has("list")) {
////								JSONArray array = new JSONArray(object.getJSONArray("list").toString());
////								attachId_list= new ArrayList<String>();
////								for (int i = 0; i < array.length(); i++) {
////									attachId_list.add(array.getString(i));
////								}
////							}
////						}
////
////						attach = new ModelAttach();
////						attach.setAttach_id(attachId_list.get(0));
////						Bundle mBundle = new Bundle();
////						mBundle.putSerializable("attach", attach);
////
////						if (lastLocation!=null) {
////							Intent intent = ActivityBaiduMap.this.getIntent();
////							intent.putExtra("latitude", lastLocation.getLatitude());
////							intent.putExtra("longitude",lastLocation.getLongitude());
////							intent.putExtra("location", lastLocation.getAddrStr());
////							intent.putExtras(mBundle);
////							ActivityBaiduMap.this.setResult(RESULT_OK, intent);
////						}
////					}
////				} catch (Exception e) {
////					e.printStackTrace();
////					Toast.makeText(getApplicationContext(), "上传图片失败",
////							Toast.LENGTH_SHORT).show();
////				}
////				finish();
////			}
////		}
////	}
//}
