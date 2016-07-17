package com.thinksns.sociax.android;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.modle.VersionInfo;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.setting.ActivityAboutUs;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.unit.UpdateManager;
import com.thinksns.sociax.android.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

	private static final String OPT_INTERVAL = "interval";
	private static final String OPT_INTERVAL_DEF = "60000";
	private static final String OPT_AUTO_REMIND = "auto_remind";
	private static final boolean OPT_AUTO_REMINDF_DEF = true;
	private static final String FONT_SIZE = "font_size";
	private static final String FONT_SIZE_DEF = "14";
	private static final String PIC_OPEN = "pic_open";
	private static final String CLEAR_CACHE = "clear_cache";
	private static final String FEEDBACK = "feedback";
	private static final String LOGIN_OUT = "logout";
	private static final String ABOUT_US = "aboutus";
	private static final String UPDATE = "update";
	private static final boolean OPT_CLEAR_CACHE_DEF = true;

	private Preference mClearCache;
	private Preference mFeedBack;
	private Preference mCheckUpdate;
	private Preference mLoginOut;
	private Preference mAboutUs;

	private static UpdateManager mUpdateManager;
	private static MyHandler1 mMyHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_preference_main);
		addPreferencesFromResource(R.xml.settings);

		mUpdateManager = new UpdateManager(this);
		mMyHandler = new MyHandler1(this);

		initView();
		setOnClickListener();

	}

	public static boolean isAutoRemind(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_AUTO_REMIND, OPT_AUTO_REMINDF_DEF);
	}

	public static Long getTimeInterval(Context context) {
		return Long.parseLong(PreferenceManager.getDefaultSharedPreferences(
				context).getString(OPT_INTERVAL, OPT_INTERVAL_DEF));
	}

	public static int getFontSize(Context context) {
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
				context).getString(FONT_SIZE, FONT_SIZE_DEF));
	}

	public static boolean isDownloadPic(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(PIC_OPEN, OPT_CLEAR_CACHE_DEF);
	}

	private void initView() {
		// TODO Auto-generated method stub
		mClearCache = findPreference(CLEAR_CACHE);
		mFeedBack = findPreference(FEEDBACK);
		mCheckUpdate = findPreference(UPDATE);
		mLoginOut = findPreference(LOGIN_OUT);
		mAboutUs = findPreference(ABOUT_US);

		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
			// mCheckUpdate.setSummary(info.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setOnClickListener() {
		findViewById(R.id.newsfeed_flip).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});

		mClearCache
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						AlertDialog.Builder alertDialog = new Builder(
								SettingsActivity.this);
						alertDialog.setTitle(R.string.clear_cache_alert_title);
						alertDialog.setMessage(R.string.clear_cache_alert);
						alertDialog.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										((Thinksns) getApplication())
												.clearDataBase();
									}
								});
						alertDialog.setNegativeButton(R.string.cancel, null);
						alertDialog.create();
						alertDialog.show();
						return false;
					}
				});

		mFeedBack.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				// getIntentData().putString("type", "suggest");
				Intent i = null;//new Intent(SettingsActivity.this,
//						ThinksnsCreate.class);
				i.putExtra("type", "suggest");
				startActivity(i);
				return false;
			}
		});

		mCheckUpdate
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						checkVersion();
						return false;
					}
				});

		mLoginOut.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new Builder(SettingsActivity.this);
				final Activity obj = SettingsActivity.this;
				builder.setMessage("确定要注销退出吗?");
				builder.setTitle("提示");
				builder.setPositiveButton("确认",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								Thinksns app = (Thinksns) obj
										.getApplicationContext();
								app.getUserSql().clear();
								int currentVersion = android.os.Build.VERSION.SDK_INT;
								if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
									app.exitApp();
									// Thinksns.exitApp();
								} else {
									// android2.1 支持 restartPackage 结束
									ActivityManager activityManager = (ActivityManager) SettingsActivity.this
											.getSystemService(Context.ACTIVITY_SERVICE);
									activityManager
											.restartPackage("net.zhishisoft.sociax.android");
									System.exit(0);
								}
							}
						});
				builder.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
				return false;
			}
		});

		mAboutUs.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SettingsActivity.this,
						ActivityAboutUs.class);
				startActivity(i);
				return false;
			}
		});
	}

	class MyHandler1 extends Handler {

		public MyHandler1(Context context) {
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.arg1 == 1) {
				VersionInfo vInfo = (VersionInfo) msg.obj;
				// 检查版本
				if (mUpdateManager.checkUpdateInfo(vInfo) > 0) {
					Toast.makeText(getApplication(), R.string.no_new_version,
							Toast.LENGTH_SHORT).show();
				}
			} else if (msg.arg1 == 2) {

			}
		}
	}

	private void checkVersion() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Api.UpgradeApi aUpgrade = new Api.UpgradeApi();
				Message msg = mMyHandler.obtainMessage();
				try {
					VersionInfo vInfo = aUpgrade.getVersion();
					msg.obj = vInfo;
					msg.what = msg.what;
					msg.arg1 = 1;
				} catch (ApiException e) {
					e.printStackTrace();
					msg.arg1 = 2;
				}
				mMyHandler.sendMessage(msg);
			}
		}).start();
	}

}
