package com.thinksns.sociax.t4.android.img;

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
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.unit.UriUtils;
import com.thinksns.sociax.thinksnsbase.utils.Anim;


import java.util.ArrayList;
import java.util.List;

public class MultilPicActivity extends Activity {
    // ArrayList<Entity> dataList;//用来装载数据源的列表
    public static List<ImageBucket> dataList;
    private GridView gridView;
    private ImageBucketAdapter adapter;// 自定义的适配器
    private AlbumHelper helper;
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static Bitmap bimap;
    private ImageView imgBack;
    private TextView tvSubmit;

    private List<Bitmap> oldBmp;
    private ArrayList<String> oldDrr;
    private int oldMax = 0;
    private Intent intent = null;
    private int from = -1;
    private final static int REQUST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_bucket_old);
        //保存旧数据
        oldBmp = new ArrayList<Bitmap>();
        oldDrr = new ArrayList<String>();

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        initIntentData();
        initData();
        initView();
    }

    private void initIntentData() {
        intent = getIntent();
        if (intent != null) {
            from = intent.getIntExtra("FROM", -1);
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
                if (intent.getData() == null) {
                    setResult(RESULT_CANCELED);
                } else {
                    setResult(RESULT_OK, intent);
                }
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MultilPicActivity.this, ImageGridActivity.class);
                intent.putExtra("FROM", from);
                intent.putExtra("position", position);
                startActivityForResult(intent, from);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ArrayList<String> list = data.getStringArrayListExtra("img_list");
            switch (requestCode) {
                case StaticInApp.CHAT_GET_PIC_FROM_LOCAL:
                    if (list != null) {
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                    break;
                case StaticInApp.HEADER_GET_PIC_FROM_LOCAL:
                    if (list != null && list.size() > 0) {
                        intent.setData(UriUtils.pathToUri(this, list.get(0)));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static List<ImageBucket> getImageBucket() {
        return dataList;
    }
}
