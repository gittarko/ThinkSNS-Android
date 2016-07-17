package com.thinksns.sociax.t4.android.function;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.component.Test;
import com.thinksns.sociax.t4.adapter.AdapterGalleryAds;
import com.thinksns.sociax.t4.android.cache.CacheManager;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.model.ModelAds;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 类说明： 广告轮播功能点 通过getView方法获取到view，一般作为listview的头部组件使用，addheaderview
 * 在页面被挂起的时候调用stopTimer功能，否则再次进入页面会一次性接受到多条信息，导致快速播放多张图片 在页面进入的时候开启轮播，否则不会运行轮播功能
 *
 * @author wz
 * @version 1.0
 * @date 2014-12-20
 */
public class FunctionAdvertise extends RelativeLayout {
    private static final String CACHE_KEY = "ads_list";
    private Context mContext;

    private TimerTask mCycleTask;        // 轮流播放任务
    private Timer mCycleTimer;            // 轮流播放定时器
    private Timer mResumingTimer;
    private TimerTask mResumingTask;

    private ViewPager viewPager;
    private AdapterGalleryAds adapterGalleryAds;    // 广告位适配器
    private ListData<SociaxItem> list_ads;          // 广告列表

    private EmptyLayout emptyLayout;
    private ImageView smalldot;                     // 广告位小圆点
    private ImageView[] smalldots;                  // 广告位所有小圆点
    private LinearLayout ll_find_ads_dots;          // 广告位红点

    private boolean mCycle;             //是否自动播放广告
    private boolean mCycleing;          //是否正在轮播
    private boolean mAutoRecover = true;    //是否手势触摸广告栏后再放开，广告自动恢复轮播

    private int mCycleDuration = 2000;  //广告切换时间间隔

    public FunctionAdvertise(Context context) {
        this(context, null);
    }

    public FunctionAdvertise(Context context,AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunctionAdvertise(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.header_ads, this, true);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        ll_find_ads_dots = (LinearLayout)findViewById(R.id.ll_find_ads_dot);
        emptyLayout = (EmptyLayout)findViewById(R.id.empty_layout);

        adapterGalleryAds = new AdapterGalleryAds(context);
        adapterGalleryAds.setOnAdvertiseClistener(new OnAdvertiseClickListener() {
            @Override
            public void onClick(View view) {
                ModelAds ads = (ModelAds)view.getTag(R.id.tag);
                String data = ads.getData();
                if(ads.getType().equals("post")) {
                    Intent intent = new Intent(mContext, ActivityPostDetail.class);
                    intent.putExtra("post_id", Integer.parseInt(ads.getData()));
                    mContext.startActivity(intent);
                }else if(ads.getType().equals("url")) {
                    //跳转浏览器
                    Uri uri = Uri.parse(data);
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    mContext.startActivity(intent);
                }
            }
        });

        viewPager.setAdapter(adapterGalleryAds);

        list_ads = new ListData<>();
        initListener();

        if(mCycle) {
            startCycle();
        }
    }


    public void startCycle() {
        startAutoCycle(mCycleDuration, mCycleDuration, mAutoRecover);
    }

    public void startAutoCycle(int cycleDuration, long delay, boolean autoRecover) {
        if(mCycleTask != null)
            mCycleTask.cancel();
        if(mCycleTimer != null)
            mCycleTimer.cancel();
        if(mResumingTask != null)
            mResumingTask.cancel();
        if(mResumingTimer != null)
            mResumingTimer.cancel();

        mCycleDuration = cycleDuration;
        mCycleTimer = new Timer();
        mCycleTask = new TimerTask() {
            @Override
            public void run() {
                cycleHandler.sendEmptyMessage(0);
            }
        };

        mCycleTimer.schedule(mCycleTask, delay, mCycleDuration);
        mAutoRecover = autoRecover;
        mCycleing = true;
        mCycle = true;
    }

    /**
     * pause auto cycle.
     */
    private void pauseAutoCycle(){
        if(mCycleing){
            mCycleTimer.cancel();
            mCycleTask.cancel();
            mCycleing = false;
        }else{
            if(mResumingTimer != null && mResumingTask != null){
                recoverCycle();
            }
        }
    }

    /**
     * stop the auto circle
     */
    public void stopAutoCycle() {
        if (mCycleTask != null) {
            mCycleTask.cancel();
        }
        if (mCycleTimer != null) {
            mCycleTimer.cancel();
        }
        if (mResumingTimer != null) {
            mResumingTimer.cancel();
        }
        if (mResumingTask != null) {
            mResumingTask.cancel();
        }

        mCycle = false;
        mCycleing = false;

    }

    /**是否在轮播***/
    public boolean isCycling() {
        return mCycleing;
    }

    private android.os.Handler cycleHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            moveNextPosition(true);
        }
    };

    /**
     * when paused cycle, this method can weak it up.
     */
    private void recoverCycle(){
        if(!mAutoRecover || !mCycle){
            return;
        }

        if(!mCycleing){
            if(mResumingTask != null && mResumingTimer!= null){
                mResumingTimer.cancel();
                mResumingTask.cancel();
            }
            mResumingTimer = new Timer();
            mResumingTask = new TimerTask() {
                @Override
                public void run() {
                    startCycle();
                }
            };
            //4s后重新启动轮播
            mResumingTimer.schedule(mResumingTask, 4000);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseAutoCycle();
                break;
        }
        return false;
    }


    /**
     * 初始化监听事件
     */
    private void initListener() {
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获取广告资源
                new readAdsTask().execute();
            }
        });

        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        recoverCycle();
                        break;
                }
                return false;
            }
        });

        //ViewPager切换事件
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int nextRealPostion = position % adapterGalleryAds.getRealCount();
                for (int i = 0; i < smalldots.length; i++) {
                    if (nextRealPostion == i) {
                        smalldots[i].setBackgroundResource(R.drawable.page_indicator_focused);
                    } else {
                        smalldots[i].setBackgroundResource(R.drawable.page_indicator);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //初始化小圆点
    private void initSmalDots() {
        if(list_ads.size() == 0)
            return;

        ll_find_ads_dots.removeAllViews();
        // 红点提示初始化
        smalldots = new ImageView[list_ads.size()];
        for (int i = 0; i < smalldots.length; i++) {
            smalldot = new ImageView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 0, 0);
            smalldot.setLayoutParams(lp);
            smalldots[i] = smalldot;
            if (i == 0) {
                //默认第一个选中
                smalldots[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                smalldots[i].setBackgroundResource(R.drawable.page_indicator);
            }

            ll_find_ads_dots.addView(smalldots[i]);
        }
    }

    /**
     * 初始化广告位
     */
    public void initAds() {
        if (adapterGalleryAds != null) {
            new readAdsTask().execute();
        }
    }

    /**
     * 读取广告异步线程
     *
     * @author wz
     */
    class readAdsTask extends AsyncTask<String, Void, Object> {
        @Override
        protected Object doInBackground(String... arg0) {
            try {
                return new Api.Public().getAds();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result == null) {
                // 没有广告从本地读取
                if(CacheManager.isExistDataCache(mContext, CACHE_KEY)) {
                    result = CacheManager.readObject(mContext, CACHE_KEY);
                }
            }

            if(result != null) {
                executeDataSuccess((ListData<SociaxItem>) result);
            }else {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        }
    }


    /**
     * 创建广告圆点，自动开始轮播
     * <p>
     *   由于要有左右可以循环滑动的效果，将viewpager的当前位置定位到  10 * result.size
     * </p>
     *
     * @param result
     */
    private void executeDataSuccess(ListData<SociaxItem> result) {
        list_ads.clear();
        list_ads.addAll(result);
        if(adapterGalleryAds.getRealCount() != 0) {
            adapterGalleryAds.removeDatas();
        }

        //隐藏加载提示
        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        adapterGalleryAds.addAds(result);
//        viewPager.setCurrentItem(result.size() * 10);
        // 红点提示
        initSmalDots();
        //开启轮播
        startCycle();
        //缓存至本地
        CacheManager.saveObject(mContext, list_ads, "ads_list");
    }

    //广告点击监听接口
    public interface OnAdvertiseClickListener {
        public void onClick(View view);
    }

    /**
     * get the current item position
     * @return
     */
    public int getCurrentPosition(){
        if(adapterGalleryAds == null)
            throw new IllegalStateException("You did not set a slider adapter");

        return viewPager.getCurrentItem() % adapterGalleryAds.getRealCount();

    }

    /**
     * move to prev slide.
     */
    public void movePrevPosition(boolean smooth) {

        if (adapterGalleryAds == null)
            throw new IllegalStateException("You did not set a slider adapter");

        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, smooth);
    }

    public void movePrevPosition(){
        movePrevPosition(true);
    }

    /**
     * move to next slide.
     */
    public void moveNextPosition(boolean smooth) {

        if (adapterGalleryAds == null)
            throw new IllegalStateException("You did not set a slider adapter");
        if(adapterGalleryAds.getRealCount() <= 0)
            return;
        int nextPosition = viewPager.getCurrentItem() + 1;
        viewPager.setCurrentItem(nextPosition, smooth);
    }

    public void moveNextPosition() {
        moveNextPosition(true);
    }

}

