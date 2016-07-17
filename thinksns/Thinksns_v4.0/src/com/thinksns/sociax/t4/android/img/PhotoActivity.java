package com.thinksns.sociax.t4.android.img;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PhotoActivity extends Activity {

	private ArrayList<View> listViews = null;
	private ViewPager pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public ArrayList<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	public int max;

	RelativeLayout photo_relativeLayout;
	private Thinksns application;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo);

		application = (Thinksns) this.getApplicationContext();
		
		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
		photo_relativeLayout.setBackgroundColor(0x70000000);

//		for (int i = 0; i < Bimp.address.size(); i++) {
//			bmp.add(Bimp.address.get(i));
//		}
//		for (int i = 0; i < Bimp.address.size(); i++) {
//			drr.add(Bimp.address.get(i));
//		}

		max = Bimp.address.size();

		//退出全屏
		Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
		photo_bt_exit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		//删除
		Button photo_bt_del = (Button) findViewById(R.id.photo_bt_del);
		photo_bt_del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Bimp.address.size() == 1) {
					//删除最后一张
//					Bimp.bmp.clear();
					Bimp.address.clear();
					finish();
				} else {
					int current = pager.getCurrentItem();
//					if(drr.get(current).contains("http://")) {
//
//					}else {
//						String newStr = drr.get(current).substring(
//							drr.get(current).lastIndexOf("/") + 1,
//							drr.get(current).lastIndexOf("."));
//						del.add(newStr);
//					}

//					bmp.remove(current);
//					drr.remove(current);
//					max--;
					Bimp.address.remove(current);
					pager.removeAllViews();
					adapter.clear();
//					pager.setAdapter(adapter);
					if(current > 0) {
						current -= 1;
					}
					pager.setCurrentItem(current);
				}
			}
		});

		Button photo_bt_enter = (Button) findViewById(R.id.photo_bt_enter);
		photo_bt_enter.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
//				Bimp.bmp = bmp;
//				Bimp.address = drr;
//				Bimp.max = max;
//				for (int i = 0; i < del.size(); i++) {
//					FileUtils.delFile(del.get(i) + ".JPEG");
//				}
				finish();
			}
		});

		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(pageChangeListener);
		pager.setOffscreenPageLimit(Bimp.address.size());

		listViews = new ArrayList<View>();
		adapter = new MyPageAdapter(this, listViews);// 构造adapter
		pager.setAdapter(adapter);		// 设置适配器

		Intent intent = getIntent();
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

		}
	};

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content
		private Context context;
		
		public MyPageAdapter(Context context, ArrayList<View> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			this.context = context;
		}
		
		public void remove(int position) {
			listViews.remove(position);
			adapter.notifyDataSetChanged();
		}

		public void clear() {
			listViews.clear();
			notifyDataSetChanged();
		}

		public int getCount() {
			// 返回数量
			return Bimp.address.size();
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			// 销毁view对象
			int index = arg1 % getCount();
			if(index < listViews.size()) {
				((ViewPager) arg0).removeView(listViews.get(arg1 % getCount()));
			}
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			// 返回view对象
			ImageView img = new ImageView(context);// 构造textView对象
			try {
				img.setBackgroundColor(0xff000000);
				img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
				Glide.with(PhotoActivity.this)
						.load(Bimp.address.get(arg1))
						.into(img);

				((ViewPager) arg0).addView(img, 0);

				listViews.add(img);
			} catch (Exception e) {
			}

			return img;
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
