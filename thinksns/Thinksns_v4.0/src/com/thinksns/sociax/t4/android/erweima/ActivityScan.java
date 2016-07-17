package com.thinksns.sociax.t4.android.erweima;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.ApiUsers;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.gimgutil.AsyncImageLoader;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.function.FunctionThirdPlatForm;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 类说明：二维码扫描
 *
 * @author wz
 * @version 1.0
 * @date 2014-8-29
 */
public class ActivityScan extends ThinksnsAbscractActivity implements
        OnCheckedChangeListener {
    private ImageView erwei_left_img;
    private String uid;
    private ImageView erweima;
    private ImageView usertouxiang;
    private ApiUsers api = new Api.Users();
    private String text;
    private TextView sao, tv_scan_username, tv_scan_intro;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        initView();
        init();
        initOnClick();
        //初始化ShareSDK
        ShareSDK.initSDK(this);
    }

    public void initView() {
        erwei_left_img = (ImageView) this.findViewById(R.id.erwei_left_img);
        erweima = (ImageView) this.findViewById(R.id.erweima);
        usertouxiang = (ImageView) this.findViewById(R.id.usertouxiang);
        sao = (TextView) this.findViewById(R.id.btn_sao);

        tv_scan_username = (TextView) this.findViewById(R.id.tv_scan_username);
        tv_scan_intro = (TextView) this.findViewById(R.id.tv_scan_intro);
    }

    public void initOnClick() {

        erwei_left_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        sao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(ActivityScan.this);
                builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            Intent openCameraIntent = new Intent(ActivityScan.this, CaptureActivity.class);
                            startActivityForResult(openCameraIntent, 0);
                        } else if (position == 1) {
                            showSharePop();
                        } else {
                            builder.dimss(view);
                        }
                    }
                });

                List<String> datas = new ArrayList<String>();
                datas.add("扫一扫");
                datas.add("分享");
                datas.add("取消");
                builder.create(datas);
            }
        });
    }

    public void showSharePop() {
        final PopupWindowListDialog.Builder builder = new PopupWindowListDialog.Builder(ActivityScan.this);
        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(ActivityScan.this,
                            ShareSDK.getPlatform(Wechat.NAME));
                    fc_share.doShareStr();
                } else if (position == 1) {
                    FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(ActivityScan.this,
                            ShareSDK.getPlatform(WechatMoments.NAME));
                    fc_share.doShareStr();
                } else if (position == 2) {
                    FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(ActivityScan.this,
                            ShareSDK.getPlatform(QQ.NAME));
                    fc_share.doShareStr();
                } else if (position == 3) {
                    FunctionThirdPlatForm fc_share = new FunctionThirdPlatForm(ActivityScan.this,
                            ShareSDK.getPlatform(SinaWeibo.NAME));
                    fc_share.doShareStr();
                } else {
                    builder.dimss(view);
                }
            }
        });

        List<String> datas = new ArrayList<String>();
        datas.add("微信");
        datas.add("朋友圈");
        datas.add("QQ");
        datas.add("新浪微博");
        datas.add("取消");
        builder.create(datas);
    }

    public void init() {

        intent = getIntent();

        if (intent != null) {
//			String userImg =intent.getString("userImg");
//					String userName=getIntentData().getString("userName");
//			String userIntro=getIntentData().getString("userIntro");
//			uid = getIntentData().getInt("uid") + "";

            String userImg = intent.getStringExtra("userImg");
            String userName = intent.getStringExtra("userName");
            String userIntro = intent.getStringExtra("userIntro");

            uid = intent.getIntExtra("uid", -1) + "";
            createErweima();
//			loadImage4header(userImg, usertouxiang);

            Glide.with(ActivityScan.this).load(userImg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideCircleTransform(ActivityScan.this))
                    .crossFade()
                    .into(usertouxiang);

            tv_scan_username.setText(userName);
            tv_scan_intro.setText("简介： " + userIntro);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            //解析二维码地址
            if(!TextUtils.isEmpty(result)) {
                //解析出地址中的uid,如果包含uid则直接跳转至用户主页
                int start = result.indexOf("uid=");
                if(start != -1) {
                    String uid;
                    int end = result.indexOf("&", start + 4);
                    if(end == -1) {
                        uid = result.substring(start + 4, result.length());
                    }else {
                        uid = result.substring(start + 4, end);
                    }

                    getIntentData().putInt("uid", Integer.parseInt(uid));
                    ActivityStack.startActivity(ActivityScan.this, ActivityUserInfo_2.class, getIntentData());

                }
//                else if(result.startsWith("http")) {
//                    //跳转浏览器
//                    Uri uri = Uri.parse(result);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.er_wei_ma, menu);
        return true;
    }

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.erweima;
    }

    public void createErweima() {

        QRCodeWriter writer = new QRCodeWriter();
        text = "uid=" + Thinksns.getMy().getUid();
        // 把输入的文本转为二维码
        try {
            DisplayMetrics metric = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;
            int height = metric.heightPixels;
            float density = metric.density;
            int densityDpi = metric.densityDpi;
            int showwidth = 500;
            Log.v("屏幕数据如下", width + "  " + height + "  " + density + " "
                    + densityDpi + "  ");
            if (width < 500) {
                showwidth = width;
            } else if (width < 780) {
                showwidth = width - 100;
            } else if (width < 1080) {
                showwidth = width - 200;
            } else if (width < 1500) {
                showwidth = width - 300;
            } else {
                showwidth = 1000;
            }
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
                    showwidth, showwidth);

            int QR_WIDTH = martix.getWidth();
            int QR_HEIGHT = martix.getHeight();
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            erweima.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public Drawable loadImage4header(final String url, final ImageView imageView) {
        Drawable cacheImage = new AsyncImageLoader().loadDrawable(url,
                new AsyncImageLoader.ImageCallback() {
                    @Override
                    public void imageLoaded(Drawable imageDrawable) {
                        imageView.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public Drawable returnImageLoaded(Drawable imageDrawable) {
                        return imageDrawable;
                    }
                });
        if (cacheImage != null) {
            imageView.setImageDrawable(cacheImage);
        }
        return cacheImage;
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

    }
}
