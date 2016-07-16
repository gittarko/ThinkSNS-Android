package com.thinksns.tschat.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.thinksns.tschat.R;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.unit.Anim;
import com.thinksns.tschat.unit.Bimp;

import java.util.ArrayList;
import java.util.List;

public class MultilPicActivity extends Activity {
	public  static List<ImageBucket> dataList;
	private GridView gridView;
	private ImageBucketAdapter adapter;// 自定义的适配器
	private AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;
	private ImageView imgBack;
	private TextView tvSubmit;

	private List<Bitmap> oldBmp;
	private List<String> oldDrr;
	private int oldMax=0;
	private Intent intent=null;
	private int from=-1;
	private final static int REQUST_CODE=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_bucket);
		//保存旧数据
		oldBmp = new ArrayList<Bitmap>();
		oldDrr = new ArrayList<String>();

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		initIntentData();
		initData();
		initView();
	}
	
	private void initIntentData(){
		intent=getIntent();
		if (intent!=null) {
			from=intent.getIntExtra("FROM", -1);
		}
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		dataList = helper.getImagesBucketList(false);
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
	}

	/**
	 * 初始化view视图
	 */
	private void initView() {
		tvSubmit = (TextView) findViewById(R.id.tvSubmit);
		imgBack = (ImageView) findViewById(R.id.imgBack);
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new ImageBucketAdapter(MultilPicActivity.this, dataList);
		gridView.setAdapter(adapter);

		tvSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MultilPicActivity.this.finish();
				Anim.exit(MultilPicActivity.this);
			}
		});

		imgBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bimp.bmp = oldBmp;
				Bimp.address = oldDrr;
				Bimp.max = oldMax;

				MultilPicActivity.this.finish();
				Anim.exit(MultilPicActivity.this);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 通知适配器，绑定的数据发生了改变，应当刷新视图
				 */
				if (from!=-1) {
					if (from == TSConfig.WEIBO_GET_PIC_FROM_LOCAL) {
						Intent intent = new Intent(MultilPicActivity.this, ImageGridActivity.class);
						intent.putExtra("FROM",TSConfig.WEIBO_GET_PIC_FROM_LOCAL);
						intent.putExtra("position", position);
						startActivity(intent);
					}else if (from == TSConfig.CHAT_GET_PIC_FROM_LOCAL) {
						Intent intent = new Intent(MultilPicActivity.this,ImageGridActivity.class);
						intent.putExtra("FROM",TSConfig.CHAT_GET_PIC_FROM_LOCAL);
						intent.putExtra("position", position);
						startActivityForResult(intent,REQUST_CODE);
					}
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data==null) {
			return;
		}
		if (requestCode==REQUST_CODE && resultCode == TSConfig.RESULT_CHAT_CODE_LOCAL) {
			ArrayList<String> list=data.getStringArrayListExtra("img_list");
			if (list!=null) {
				
				Intent intent_back = new Intent();
				intent_back.putStringArrayListExtra("imgList", list);
				setResult(Activity.RESULT_OK, intent_back);
				finish();
				
			}
		}
	}
	
	public static List<ImageBucket> getImageBucket() {
		return dataList;
	}
}
