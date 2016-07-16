package com.thinksns.tschat.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.thinksns.tschat.R;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.unit.Bimp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ImageGridActivity extends Activity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	
	private ArrayList<ImageItem> dataList;
	private GridView gridView;
	public ImageGridAdapter adapter;
	private AlbumHelper helper;
	private Button bt;
	private ImageView back;
	private Intent intent=null;
	private int from=-1;
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivity.this, "最多选择9张图片", 400).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_grid);
		back = (ImageView) findViewById(R.id.imgBack);
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		
		int position = getIntent().getIntExtra("position", 0);
		dataList = MultilPicActivity.getImageBucket().get(position).imageList;
		
		initIntentData();
		initView();
			
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageGridActivity.this.finish();
			}
		});

		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.map.values();
				Iterator<String> it = c.iterator();

				for (; it.hasNext();) {
					list.add(it.next());
				}
				//来自于发布微博
				if (from == TSConfig.WEIBO_GET_PIC_FROM_LOCAL) {
					if (Bimp.act_bool) {
						Bimp.act_bool = false;
					}
					for (int i = 0; i < list.size(); i++) {
						if (Bimp.address.size() < 9) {
							String path = list.get(i);
							if(!Bimp.address.contains(path))	//去重
								Bimp.address.add(list.get(i));
						}
					}
					finish();
				}
				//来自于聊天
				else if (from == TSConfig.CHAT_GET_PIC_FROM_LOCAL) {
					
					Intent intent_back=new Intent();
					intent_back.putStringArrayListExtra("img_list",list);
					setResult(TSConfig.RESULT_CHAT_CODE_LOCAL, intent_back);
					finish();
				}
			}
		});
	}
	
	private void initIntentData(){
		intent=getIntent();
		if (intent!=null) {
			from=intent.getIntExtra("FROM", -1);
		}
	}

	private void initView() {
		
		bt = (Button) findViewById(R.id.bt);
		bt.setText("完成" + "(" + Bimp.address.size() + ")");
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,mHandler);
		
		if (dataList!=null&&dataList.size()!=0) {
			for (int i = 0; i < dataList.size(); i++) {
				dataList.get(i).isSelected=false;
			}
		}
		
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
			public void onListen(int count) {
				bt.setText("完成" + "(" + count + ")");
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
			}
		});
	}
}
