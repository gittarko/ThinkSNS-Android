package com.thinksns.sociax.t4.android.img;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.ImageFragment;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.unit.ImageUtil;

import me.nereo.multi_image_selector.ImagePagerFragment;
import uk.co.senab.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

/*
 * 画廊浏览图片
 * 
 * */
public class ActivityViewPager extends FragmentActivity implements OnClickListener{

    private LinearLayout lyLoading;
    private ResultHandler resultHandler;
    private int currentIndex;
    private List<ModelPhoto> photolist;
    private ImageView mImgBtnSave;
    private TextView tv_index;
    private ImageUtil iu;
    public static ImageSize imageSize;
    private ImagePagerFragment pagerFragment;

    private static final String urlName = System.currentTimeMillis() + ".jpg";
    private static final String savePath = ImageUtil.getSDPath() + "/" + StaticInApp.cache;
    private Thinksns application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉信息栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_pager);

        mImgBtnSave = (ImageView) findViewById(R.id.ib_save);
        mImgBtnSave.setOnClickListener(this);
        tv_index = (TextView) findViewById(R.id.tv_index);

        application = (Thinksns) this.getApplicationContext();

        if (getIntent().hasExtra("index"))
            currentIndex = getIntent().getIntExtra("index", 0);
        if (getIntent().hasExtra("photolist")) {
            photolist = getIntent().getParcelableArrayListExtra("photolist");
        }

        List<String> paths = new ArrayList<String>();
        for(ModelPhoto photo : photolist) {
            paths.add(photo.getOriUrl());
        }

        pagerFragment = (ImagePagerFragment) getSupportFragmentManager().findFragmentById(me.nereo.multi_image_selector.R.id.photoPagerFragment);
        pagerFragment.setPhotos(new ArrayList<String>(paths), currentIndex);

        pagerFragment.getViewPager().setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tv_index.setText(position + 1 + " / " + photolist.size());
            }

            @Override
            public void onPageSelected(int i) {
                currentIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_save:
                iu = new ImageUtil();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int status = 3;
                        if(ImageUtil.getSDPath() == null) {
                            status = 1;
                        }else {
                            boolean result = iu.saveUrlImg(photolist.get(currentIndex).getUrl(), urlName, savePath);
                            if (result) {
                                status = 2;
                            }
                        }
                        saveImageResponse(status);
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    /**
     * 图片保存状态
     * @param status
     */
    private void saveImageResponse(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == 1) {
                    Toast.makeText(ActivityViewPager.this, "保存失败,没有获取到SD卡", Toast.LENGTH_SHORT).show();
                } else if (status == 2) {
                    Toast.makeText(ActivityViewPager.this, "保存成功, 目录:" + savePath, Toast.LENGTH_SHORT).show();
                } else if (status == 3) {
                    Toast.makeText(ActivityViewPager.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class SamplePagerAdapter extends FragmentStatePagerAdapter {
        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return photolist.size();
        }

        @Override
        public Fragment getItem(int i) {
            String url;
            if(photolist.size() > 1) {
                //正常情况下多图时读取小图
                url = photolist.get(i).getUrl();
            }else{
                url = photolist.get(i).getMiddleUrl();
            }

            //中等缩略图可能为空
            String oriUrl = photolist.get(i).getOriUrl();
            Fragment fragment = ImageFragment.newInstance(url, oriUrl);
            return fragment;
        }

    }


    @SuppressLint("HandlerLeak")
    private class ResultHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            lyLoading.setVisibility(View.GONE);
            PhotoView photoView = (PhotoView) msg.obj;
            if (msg.what == 1) {
                if (photoView.getTag() == null) {
                    Toast.makeText(ActivityViewPager.this, R.string.wc_itme_img_error, Toast.LENGTH_LONG).show();
                    return;
                }
                application.displayImage((String) photoView.getTag(), photoView);
            }
            if (msg.arg1 == 1) {
                Toast.makeText(ActivityViewPager.this, "保存失败,没有获取到SD卡", Toast.LENGTH_SHORT).show();
            } else if (msg.arg1 == 2) {
                Toast.makeText(ActivityViewPager.this, "保存成功, 目录:" + savePath, Toast.LENGTH_SHORT).show();
            } else if (msg.arg1 == 3) {
                Toast.makeText(ActivityViewPager.this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
