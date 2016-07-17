package com.thinksns.sociax.t4.android.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.concurrent.BitmapDownloaderTask;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentUserInfoAlbum;
import com.thinksns.sociax.t4.android.fragment.FragmentUserInfoWeibo;
import com.thinksns.sociax.t4.android.fragment.FragmentUserinfoHome;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.ScrollViewSociax;
import com.thinksns.sociax.t4.component.ViewPagerUnits;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.unit.Compress;
import com.thinksns.sociax.unit.ImageUtil;
import com.thinksns.sociax.android.R;

/**
 * 类说明：用户个人主页 ta人主页需要传入int uid或者String uname
 * 
 * @author wz
 * @date 2014-9-16
 * @version 1.0
 */
public class ActivityUserInfo extends ThinksnsAbscractActivity {

	int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
	int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
	private static final int ADD_FOLLOWED = 20;
	private static final int DEL_FOLLOWED = 21;
	private static final int LOAD_USER_INFO = 22;
	private static final int ADD_BLACKLIST = 23;
	private static final int DEL_BLACKLIST = 24;
	private static final int ADD_CONTACT = 25;
	private static final int DEL_CONTACT = 26;
	private static final int SELETE = 27;
	private static final int UPLOAD_FACE = 11;

	private boolean refreshing = false, isInitData = false;

	private final int SELETE_HOME = 0;
	private final int SELETE_WEIBO = 1;
	private final int SELETE_ALBUM = 2;
	private final int SELETE_GIFT = 3;
	private int selected = SELETE_HOME;

	private ActivityHandler handler;
	private ResultHandler resultHandler;
	private ImageView header;
	private Bitmap newHeader;
	private ImageView followButton, img_back, img_change_info, img_right;

	ImageView sendMessage;
	private TextView tvName;
	private ImageView imSex;

	private ProgressDialog prDialog;
	private LoadingView mLoadingView;
	private LinearLayout mLyUserInfoView, ll_change_info;
	private LinearLayout infoUtilLayout;

	private ModelUser user;
	private ScrollViewSociax svSociax;

	// 首页用到的变量
	private ViewPagerUnits viewPager;
	private List<Fragment> fragList;
	private AdapterViewPager adapterFragment;
	private RadioButton rb_weibo, rb_home, rb_album, rb_gift;
	private RelativeLayout rl_weibo, rl_home, rl_album, rl_gift;

	private LoadingView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		initView();
		initIntentData();
		initOnClickListener();
		initData();
	}

	private void initIntentData() {
		Thinksns app = (Thinksns) this.getApplicationContext();
		Worker thread = new Worker(app, "Loading UserInfo");
		handler = new ActivityHandler(thread.getLooper(), this);
		resultHandler = new ResultHandler();
	}

	private void loadWeiboData(int uid) {
	}

	void setRadioButtonBackGround(int selete) {
		switch (selete) {
		case SELETE_HOME:
			rb_album.setChecked(false);
			rb_gift.setChecked(false);
			rb_home.setChecked(true);
			rb_weibo.setChecked(false);
			rb_home.setTextColor(getResources().getColor(
					R.color.title_blue));
			rb_album.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_gift.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_weibo.setTextColor(getResources().getColor(R.color.title_graybg));

			break;
		case SELETE_ALBUM:

			rb_album.setChecked(true);
			rb_gift.setChecked(false);
			rb_home.setChecked(false);
			rb_weibo.setChecked(false);

			rb_home.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_album.setTextColor(getResources().getColor(
					R.color.title_blue));
			rb_gift.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_weibo.setTextColor(getResources().getColor(R.color.title_graybg));
			break;
		case SELETE_GIFT:

			rb_album.setChecked(false);
			rb_gift.setChecked(true);
			rb_home.setChecked(false);
			rb_weibo.setChecked(false);
			rb_home.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_album.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_gift.setTextColor(getResources().getColor(
					R.color.title_blue));
			rb_weibo.setTextColor(getResources().getColor(R.color.title_graybg));

			break;
		case SELETE_WEIBO:

			rb_album.setChecked(false);
			rb_gift.setChecked(false);
			rb_home.setChecked(false);
			rb_weibo.setChecked(true);

			rb_home.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_album.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_gift.setTextColor(getResources().getColor(R.color.title_graybg));
			rb_weibo.setTextColor(getResources().getColor(
					R.color.title_blue));
			break;
		}
	}

	private void initView() {

		rb_album = (RadioButton) findViewById(R.id.rb_album);
		rb_gift = (RadioButton) findViewById(R.id.rb_gift);
		rb_home = (RadioButton) findViewById(R.id.rb_home);
		rb_weibo = (RadioButton) findViewById(R.id.rb_weibo);

		rl_album = (RelativeLayout) findViewById(R.id.rl_album);
		rl_gift = (RelativeLayout) findViewById(R.id.rl_gift);
		rl_home = (RelativeLayout) findViewById(R.id.rl_home);
		rl_weibo = (RelativeLayout) findViewById(R.id.rl_weibo);
		svSociax = (ScrollViewSociax) findViewById(R.id.svSociax);

		img_back = (ImageView) findViewById(R.id.img_back);
		sendMessage = (ImageView) findViewById(R.id.send_chat);

		loadingView = (LoadingView) findViewById(LoadingView.ID);
		mLyUserInfoView = (LinearLayout) findViewById(R.id.ll_follow_and_message);
		ll_change_info = (LinearLayout) findViewById(R.id.ll_change_info);
		img_change_info = (ImageView) findViewById(R.id.img_change_info);
		infoUtilLayout = (LinearLayout) findViewById(R.id.info_util_layout);
		header = (ImageView) findViewById(R.id.iv_user_header);
		followButton = (ImageView) findViewById(R.id.button_follow);

		tvName = (TextView) findViewById(R.id.tv_user_name);

		imSex = (ImageView) findViewById(R.id.im_sex);
		// 如果有传入uid或者uname，并且不是当前用户的uid或者uname，则隐藏我的个人信息
		if ((getIntentData().containsKey("uid")
				&& getIntentData().getInt("uid") != Thinksns.getMy().getUid() && getIntentData()
				.getInt("uid") != 0)
				|| (getIntentData().containsKey("uname")
						&& !getIntentData().getString("uname").equals(
								Thinksns.getMy().getUserName()) && getIntentData()
						.getString("uname") != null)) {
			mLyUserInfoView.setVisibility(View.VISIBLE);
			ll_change_info.setVisibility(View.GONE);
		} else {
			ll_change_info.setVisibility(View.VISIBLE);
			mLyUserInfoView.setVisibility(View.GONE);
		}

		// fragment
		viewPager = (ViewPagerUnits) findViewById(R.id.vp_home);
	}

	private void setUerInfoData(ModelUser user) {
		// ThinksnsUserInfo.this.setUserInfoButton();
		tvName.setText(user.getUserName());

		if (user.getSex().equals("1") || user.getSex().equals("男")) {
			imSex.setImageResource(R.drawable.tv_user_info_man);
		} else {
			imSex.setImageResource(R.drawable.tv_user_info_woman);
		}

		loadHeader(user);
	}

	private void initOnClickListener() {

		img_change_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivityUserInfo.this,
						ActivityChangeUserInfo.class);
				startActivity(intent);
			}
		});
		img_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		rl_album.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSelected(SELETE_ALBUM);
			}
		});
		rl_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSelected(SELETE_HOME);
			}
		});
		rl_weibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSelected(SELETE_WEIBO);
			}
		});
		rl_gift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSelected(SELETE_GIFT);
			}
		});

		followButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setClickable(false);
				Message msg = handler.obtainMessage();
//				if ((ThinksnsUserInfo.FollowedStatus) v.getTag() == ThinksnsUserInfo.FollowedStatus.YES) {
//					msg.what = DEL_FOLLOWED;
//				} else {
//					msg.what = ADD_FOLLOWED;
//				}
				msg.obj = user;
				handler.sendMessage(msg);
			}
		});

		sendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getIntentData().putInt("to_uid", user.getUid());
				getIntentData().putString("to_name", user.getUserName());
				Thinksns app = (Thinksns) ActivityUserInfo.this
						.getApplicationContext();
//				app.startActivity(ActivityUserInfo.this,ActivityChatDetail.class, getIntentData());
				Anim.in(ActivityUserInfo.this);
			}

		});
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = resultHandler.obtainMessage();
				msg.arg1 = getSelected();
				msg.what = SELETE;
				msg.sendToTarget();
			}
		}).start();
	}

	private void initData() {
		System.out.println("threadLodaingData");
		if (refreshing) {
			Toast.makeText(this, R.string.re_load, Toast.LENGTH_LONG).show();
			return;
		}

		ModelUser user = new ModelUser();
		user.setUid(getIntentData().containsKey("uid") ? getIntentData()
				.getInt("uid") : Thinksns.getMy().getUid());
		if (getIntentData().getString("uname") != null) {
			user.setUserName(getIntentData().containsKey("uname") ? getIntentData()
					.getString("uname") : Thinksns.getMy().getUserName());
		}
		Message msg = handler.obtainMessage();
		msg.what = LOAD_USER_INFO;
		msg.obj = user;
		handler.sendMessage(msg);
	}

	/* ///////////////////////// Handler */// /////////////////////////////

	private class ActivityHandler extends Handler {
		private Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			boolean newData = false;
			Message mainMsg = new Message();
			mainMsg.what = ResultHandler.ERROR;
			Thinksns app = (Thinksns) this.context.getApplicationContext();
			Api.Friendships friendships = app.getFriendships();
			Api.STContacts stContacts = app.getContact();
			Api.Users userApi = app.getUsers();
			try {
				switch (msg.what) {
				case ADD_FOLLOWED:
					newData = friendships.create((ModelUser) msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj = newData;
					mainMsg.arg1 = msg.what;
					break;
				case DEL_FOLLOWED:
					newData = friendships.destroy((ModelUser) msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj = newData;
					mainMsg.arg1 = msg.what;
					break;
				case ADD_CONTACT:
					newData = stContacts.contacterCreate((ModelUser) msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj = newData;
					mainMsg.arg1 = msg.what;
					break;
				case DEL_CONTACT:
					newData = stContacts.contacterDestroy((ModelUser) msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj = newData;
					mainMsg.arg1 = msg.what;
					break;
				case LOAD_USER_INFO:
					ModelUser user = null;//userApi.show((ModelUser) msg.obj);
					if (user.getUid() == Thinksns.getMy().getUid()) {
						// 添加认证信息
						user.setToken(Thinksns.getMy().getToken());
						user.setSecretToken(Thinksns.getMy().getSecretToken());
						app.getUserSql().updateUser(user);
					}
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj = user;
					mainMsg.arg1 = msg.what;
					break;

				case UPLOAD_FACE:
					boolean result = userApi.uploadFace((Bitmap) msg.obj,
							new File(changeListener.getImagePath()));

					ModelUser iduser = new ModelUser();
					iduser.setUid(getIntentData().containsKey("uid") ? getIntentData()
							.getInt("uid") : Thinksns.getMy().getUid());
					if (getIntentData().getString("uname") != null) {
						iduser.setUserName(getIntentData().containsKey("uname") ? getIntentData()
								.getString("uname") : Thinksns.getMy()
								.getUserName());
					}
					// msg.obj = user;
					ModelUser user2 = null;//userApi.show(iduser);
					int i = app.getUserSql().updateUserFace(user2);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj = result;
					resultHandler.resultUser = user2;
					mainMsg.arg1 = msg.what;
					mainMsg.arg2 = i;

					break;

				case ADD_BLACKLIST:
					newData = friendships.addBlackList((ModelUser) msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj = newData;
					mainMsg.arg1 = msg.what;
					break;
				case DEL_BLACKLIST:
					newData = friendships.delBlackList((ModelUser) msg.obj);
					mainMsg.what = ResultHandler.SUCCESS;
					mainMsg.obj = newData;
					mainMsg.arg1 = msg.what;
					break;

				}
			} catch (VerifyErrorException e) {
				mainMsg.obj = e.getMessage();
				mainMsg.what = ResultHandler.ERROR;
				refreshing = false;
				Log.e(TAG, e.getMessage());
			} catch (ApiException e) {
				Log.d(AppConstant.APP_TAG, " wm " + e.toString());
				mainMsg.what = ResultHandler.ERROR;
				mainMsg.obj = e.getMessage();
				refreshing = false;
				Log.e(TAG, e.getMessage());
			} catch (DataInvalidException e) {
				mainMsg.what = ResultHandler.ERROR;
				mainMsg.obj = e.getMessage();
				refreshing = false;
				Log.e(TAG, e.getMessage());
			}
			resultHandler.sendMessage(mainMsg);
		}
	}

	private class ResultHandler extends Handler {
		private static final int SUCCESS = 0;
		private static final int ERROR = 1;

		private ModelUser resultUser = null;

		@SuppressLint("NewApi")
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			String info = "";
			if (msg.what == SELETE) {
				setRadioButtonBackGround(selected);
				if (!isInitData) {
					adapterFragment = new AdapterViewPager(
							getSupportFragmentManager());
					fragList = new ArrayList<Fragment>();
					Intent intent = getIntent();
					intent.putExtra("user", user);
					// 先将当前获取的详细用户设置给当前activity
					ActivityUserInfo.this.setIntent(intent);
					fragList.add(new FragmentUserinfoHome());
					fragList.add(new FragmentUserInfoWeibo());
					fragList.add(new FragmentUserInfoAlbum());
					adapterFragment.bindData(fragList);
					viewPager.setOffscreenPageLimit(fragList.size());
					initHomeViewPagerListener();
					isInitData = true;
				}
				viewPager.setCurrentItem(selected);
				if (selected == 1 || selected == 3) {
					LayoutParams params = new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					params.height = 500;
					viewPager.setLayoutParams(params);
				} else {
					View view = viewPager
							.getChildAt(viewPager.getCurrentItem());
					if (view == null) {
						return;
					}
					view.measure(w, h);
					Log.v("ActivityUserInfo--initHomeViewPagerListener",
							"wztest view!=null" + view.getMeasuredHeight());
					LayoutParams params = new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					params.height = view.getMeasuredHeight();
					viewPager.setLayoutParams(params);
				}
				return;
			} else if (msg.what == SUCCESS) {
				switch (msg.arg1) {
				case ADD_FOLLOWED:
//					followButton.setTag(ThinksnsUserInfo.FollowedStatus.YES);
					followButton
							.setBackgroundResource(R.drawable.tv_user_info_followded);
					info = "关注成功";
					Toast.makeText(ActivityUserInfo.this, info,
							Toast.LENGTH_SHORT).show();
					followButton.setClickable(true);
					break;
				case DEL_FOLLOWED:
//					followButton.setTag(ThinksnsUserInfo.FollowedStatus.NO);
					followButton
							.setBackgroundResource(R.drawable.tv_user_info_follow);
					info = "取消关注成功";
					Toast.makeText(ActivityUserInfo.this, info,
							Toast.LENGTH_SHORT).show();
					followButton.setClickable(true);
					break;
				case LOAD_USER_INFO:
					ModelUser user = (ModelUser) msg.obj;
					ActivityUserInfo.this.user = user;
					setSelected(SELETE_HOME);
					setUerInfoData(user);
					if (user.isFollowed()) {
//						followButton.setTag(ThinksnsUserInfo.FollowedStatus.YES);
						followButton
								.setBackgroundResource(R.drawable.tv_user_info_followded);
					} else {
//						followButton.setTag(ThinksnsUserInfo.FollowedStatus.NO);
						followButton
								.setBackgroundResource(R.drawable.tv_user_info_follow);
					}

					if (user.getIsInBlackList()) {
						followButton.setVisibility(View.GONE);
					}
					loadWeiboData(user.getUid());
					break;
				case UPLOAD_FACE:
					loadingView.setVisibility(View.GONE);
					boolean result = (Boolean) msg.obj;
					if (result && msg.arg2 > 0) {
						if (resultUser != null) {
							loadHeader(resultUser);
							info = "上传成功,稍后更新";
						}
					} else {
						info = "上传失败";
					}
					Toast.makeText(ActivityUserInfo.this, info,
							Toast.LENGTH_SHORT).show();
					prDialog.dismiss();
					break;
				}
			} else {
				info = (String) msg.obj;
				Toast.makeText(ActivityUserInfo.this, info, Toast.LENGTH_SHORT)
						.show();
				followButton.setClickable(false);
			}

		}
	}

	public void loadHeader(ModelUser user) {
		header.setTag(user);

	}

	public void initHomeViewPagerListener() {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				// TODO Auto-generated method stub

				switch (index) {
				case 0:
					setRadioButtonBackGround(SELETE_HOME);
					rb_home.setTextColor(getResources().getColor(
							R.color.title_blue));
					rb_album.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_gift.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_weibo.setTextColor(getResources().getColor(
							R.color.title_graybg));
					break;
				case 1:
					setRadioButtonBackGround(SELETE_WEIBO);
					rb_home.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_album.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_gift.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_weibo.setTextColor(getResources().getColor(
							R.color.title_blue));
					break;
				case 2:
					setRadioButtonBackGround(SELETE_ALBUM);
					rb_home.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_album.setTextColor(getResources().getColor(
							R.color.title_blue));
					rb_gift.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_weibo.setTextColor(getResources().getColor(
							R.color.title_graybg));
					break;
				case 3:
					setRadioButtonBackGround(SELETE_GIFT);
					rb_home.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_album.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_gift.setTextColor(getResources().getColor(
							R.color.title_blue));
					rb_weibo.setTextColor(getResources().getColor(
							R.color.title_graybg));
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		viewPager.setAdapter(adapterFragment);

	}

	final protected void dowloaderTask(String url, ImageView image,
			BitmapDownloaderTask.Type type) {
		BitmapDownloaderTask task = new BitmapDownloaderTask(image, type);
		task.execute(url);
	}

	// //************************************
	private void startProgressDialog() {
		if (prDialog == null) {
			prDialog = new ProgressDialog(this);
			prDialog.setMessage("正在上传...");
		}
		prDialog.show();
	}

	// ///////////////////********** 相片处理 **************************///////////

	private static final int LOCATION = 1;
	private static final int CAMERA = 0;

	private headImageChangeListener changeListener;
	private static final int IO_BUFFER_SIZE = 4 * 1024;
	private boolean hasImage;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			Bitmap btp = null;
			switch (requestCode) {
			case CAMERA:
				try {
					startPhotoZoom(Uri.fromFile(new File(changeListener
							.getImagePath())));
				} catch (Exception e) {
					Log.e(TAG, "file saving..." + e.toString());
				}
				break;
			case LOCATION:
				btp = checkImage(data);
				startPhotoZoom(data.getData());
				break;
			case 3:
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						btp = extras.getParcelable("data");
						Log.d(AppConstant.APP_TAG, "sava cut ....");
						Message msg = handler.obtainMessage();
						msg.what = UPLOAD_FACE;
						msg.arg1 = UPLOAD_FACE;
						msg.obj = btp;
						loadingView = (LoadingView) findViewById(LoadingView.ID);
						startProgressDialog();
						handler.sendMessage(msg);
					}
				} else {
					Log.d(AppConstant.APP_TAG, "data is null  .... ");
				}
				break;
			}
			if (btp != null) {
				this.hasImage = true;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		// TODO 裁剪图片
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	private Bitmap checkImage(Intent data) {
		if (changeListener == null)
			changeListener = new headImageChangeListener();
		Bitmap bitmap = null;
		try {
			Uri originalUri = data.getData();
			String path = getRealPathFromURI(originalUri);
			// path = path.substring(path.indexOf("/sdcard"), path.length());
			Log.d(TAG, "imagePath" + path);
			bitmap = Compress.compressPicToBitmap(new File(path));
			if (bitmap != null) {
				changeListener.setImagePath(path);
			}

		} catch (Exception e) {
			Log.e("checkImage", e.getMessage());
		} finally {
			newHeader = bitmap;
			return bitmap;
		}
	}

	private String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		String result = contentUri.toString();
		String[] proj = { MediaColumns.DATA };
		cursor = managedQuery(contentUri, proj, null, null, null);
		if (cursor == null)
			throw new NullPointerException("reader file field");
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			// 最后根据索引值获取图片路径
			result = cursor.getString(column_index);
			try {
				// 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
				if (Integer.parseInt(Build.VERSION.SDK) < 14) {
					cursor.close();
				}
			} catch (Exception e) {
				Log.e(TAG, "error:" + e);
			}
		}
		return result;
	}

	/**
	 * 照片来源
	 */
	class headImageChangeListener implements DialogInterface.OnClickListener {
		private String imagePath = "";

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				cameraImage();
				break;
			case 1:
				locationImage();
				break;
			default:
				dialog.dismiss();
			}
		}

		private void locationImage() {
			Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
			getImage.addCategory(Intent.CATEGORY_OPENABLE);
			getImage.setType("image/*");
			startActivityForResult(Intent.createChooser(getImage, "选择照片"), 1);

		}

		// 获取相机拍摄图片
		private void cameraImage() {
			if (!ImageUtil.isHasSdcard()) {
				// Toast.makeText(this.ThinksnsCreate,"" ,T );//.show();
				Toast.makeText(ActivityUserInfo.this, "请检查存储卡",
						Toast.LENGTH_LONG).show();
				return;
			}
			if (changeListener == null)
				changeListener = new headImageChangeListener();
			// 启动相机
			Intent myIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			String picName = System.currentTimeMillis() + ".jpg";
			try {
				String path = ImageUtil.saveFilePaht(picName);
				File file = new File(path);
				Uri uri = Uri.fromFile(file);
				changeListener.setImagePath(path);
				myIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "file saving...");
			}
			startActivityForResult(myIntent, 0);
		}

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}
	}

	// //**************************
	protected Activity getTabActivity() {
		return this;
	}

	@Override
	public String getTitleCenter() {
		return getString(R.string.user_info_tit);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this);
	}

	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		};
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_user_info;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (user != null && user.getUid() == Thinksns.getMy().getUid()) {
//			ImageLoader.getInstance().displayImage(Thinksns.getMy().getFace(), header, Thinksns.getOptions());
			
			Glide.with(ActivityUserInfo.this).load(Thinksns.getMy().getFace())
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.transform(new GlideCircleTransform(ActivityUserInfo.this))
			.crossFade()
			.into(header);
			
			tvName.setText(Thinksns.getMy().getUserName());
		}

	}
}
