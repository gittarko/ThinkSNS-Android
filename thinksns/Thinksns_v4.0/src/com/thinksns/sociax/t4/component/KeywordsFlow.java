package com.thinksns.sociax.t4.component;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

public class KeywordsFlow extends FrameLayout implements OnGlobalLayoutListener {

	public static final int IDX_X = 0;  
    public static final int IDX_Y = 1;  
    public static final int IDX_TXT_LENGTH = 2;  
    public static final int IDX_DIS_Y = 3;  
    /** 鐢卞鑷冲唴鐨勫姩鐢汇� */  
    public static final int ANIMATION_IN = 1;  
    /** 鐢卞唴鑷冲鐨勫姩鐢汇� */  
    public static final int ANIMATION_OUT = 2;  
    /** 浣嶇Щ鍔ㄧ敾绫诲瀷锛氫粠澶栧洿绉诲姩鍒板潗鏍囩偣銆�*/  
    public static final int OUTSIDE_TO_LOCATION = 1;  
    /** 浣嶇Щ鍔ㄧ敾绫诲瀷锛氫粠鍧愭爣鐐圭Щ鍔ㄥ埌澶栧洿銆�*/  
    public static final int LOCATION_TO_OUTSIDE = 2;  
    /** 浣嶇Щ鍔ㄧ敾绫诲瀷锛氫粠涓績鐐圭Щ鍔ㄥ埌鍧愭爣鐐广� */  
    public static final int CENTER_TO_LOCATION = 3;  
    /** 浣嶇Щ鍔ㄧ敾绫诲瀷锛氫粠鍧愭爣鐐圭Щ鍔ㄥ埌涓績鐐广� */  
    public static final int LOCATION_TO_CENTER = 4;  
    public static final long ANIM_DURATION = 800l;  
    public static final int MAX = 10;  
    public static final int TEXT_SIZE_MAX = 25;  
    public static final int TEXT_SIZE_MIN = 15;  
    private OnClickListener itemClickListener; 
    private static Interpolator interpolator;  
    private static AlphaAnimation animAlpha2Opaque;  
    private static AlphaAnimation animAlpha2Transparent;  
    private static ScaleAnimation animScaleLarge2Normal, animScaleNormal2Large, animScaleZero2Normal,  
            animScaleNormal2Zero;  
    /** 瀛樺偍鏄剧ず鐨勫叧閿瓧銆�*/  
    private Vector<String> vecKeywords;  
    private int width, height;  
    /** 
     * go2Show()涓璧嬪�涓簍rue锛屾爣璇嗗紑鍙戜汉鍛樿Е鍙戝叾寮�鍔ㄧ敾鏄剧ず銆�br/> 
     * 鏈爣璇嗙殑浣滅敤鏄槻姝㈠湪濉厖keywrods鏈畬鎴愮殑杩囩▼涓幏鍙栧埌width鍜宧eight鍚庢彁鍓嶅惎鍔ㄥ姩鐢汇�<br/> 
     * 鍦╯how()鏂规硶涓叾琚祴鍊间负false銆�br/> 
     * 鐪熸鑳藉鍔ㄧ敾鏄剧ず鐨勫彟涓�繀瑕佹潯浠讹細width 鍜�height涓嶄负0銆�br/> 
     */  
    private boolean enableShow;  
    private Random random;  

    /** 
     * @see ANIMATION_IN 
     * @see ANIMATION_OUT 
     * @see OUTSIDE_TO_LOCATION 
     * @see LOCATION_TO_OUTSIDE 
     * @see LOCATION_TO_CENTER 
     * @see CENTER_TO_LOCATION 
     * */  
    private int txtAnimInType, txtAnimOutType;  
    /** 鏈�繎涓�鍚姩鍔ㄧ敾鏄剧ず鐨勬椂闂淬� */  
    private long lastStartAnimationTime;  
    /** 鍔ㄧ敾杩愯鏃堕棿銆�*/  
    private long animDuration; 
    
    public KeywordsFlow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
    
    public KeywordsFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
    
    public KeywordsFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
    
    private void init() {  
        lastStartAnimationTime = 0l;  
        animDuration = ANIM_DURATION;  
        random = new Random();  
        vecKeywords = new Vector<String>(MAX);  
        getViewTreeObserver().addOnGlobalLayoutListener(this);  
        interpolator = AnimationUtils.loadInterpolator(getContext(), android.R.anim.decelerate_interpolator);  
        animAlpha2Opaque = new AlphaAnimation(0.0f, 1.0f);  
        animAlpha2Transparent = new AlphaAnimation(1.0f, 0.0f);  
        animScaleLarge2Normal = new ScaleAnimation(2, 1, 2, 1);  
        animScaleNormal2Large = new ScaleAnimation(1, 2, 1, 2);  
        animScaleZero2Normal = new ScaleAnimation(0, 1, 0, 1);  
        animScaleNormal2Zero = new ScaleAnimation(1, 0, 1, 0);  
    }  

    public long getDuration() {  
        return animDuration;  
    }  
  
    public void setDuration(long duration) {  
        animDuration = duration;  
    }  
  
    public boolean feedKeyword(String keyword) {  
        boolean result = false;  
        if (vecKeywords.size() < MAX) {  
            result = vecKeywords.add(keyword);  
        }  
        return result;  
    }  
  
    /** 
     * 寮�鍔ㄧ敾鏄剧ず銆�br/> 
     * 涔嬪墠宸茬粡瀛樺湪鐨凾extView灏嗕細鏄剧ず閫�嚭鍔ㄧ敾銆�br/> 
     *  
     * @return 姝ｅ父鏄剧ず鍔ㄧ敾杩斿洖true锛涘弽涔嬩负false銆傝繑鍥瀎alse鍘熷洜濡備笅锛�br/> 
     *         1.鏃堕棿涓婁笉鍏佽锛屽彈lastStartAnimationTime鐨勫埗绾︼紱<br/> 
     *         2.鏈幏鍙栧埌width鍜宧eight鐨勫�銆�br/> 
     */  
    public boolean go2Show(int animType) {  
        if (System.currentTimeMillis() - lastStartAnimationTime > animDuration) {  
            enableShow = true;  
            if (animType == ANIMATION_IN) {  
                txtAnimInType = OUTSIDE_TO_LOCATION;  
                txtAnimOutType = LOCATION_TO_CENTER;  
            } else if (animType == ANIMATION_OUT) {  
                txtAnimInType = CENTER_TO_LOCATION;  
                txtAnimOutType = LOCATION_TO_OUTSIDE;  
            }  
            disapper();  
            boolean result = show();  
            return result;  
        }  
        return false;  
    }  

    private void disapper() {  
        int size = getChildCount();  
        for (int i = size - 1; i >= 0; i--) {  
            final TextView txt = (TextView) getChildAt(i);  
            if (txt.getVisibility() == View.GONE) {  
                removeView(txt);  
                continue;  
            }  
            FrameLayout.LayoutParams layParams = (LayoutParams) txt.getLayoutParams();  
            // Log.d("ANDROID_LAB", txt.getText() + " leftM=" +   
            // layParams.leftMargin + " topM=" + layParams.topMargin   
            // + " width=" + txt.getWidth());   
            int[] xy = new int[] { layParams.leftMargin, layParams.topMargin, txt.getWidth() };  
            AnimationSet animSet = getAnimationSet(xy, (width >> 1), (height >> 1), txtAnimOutType);  
            txt.startAnimation(animSet);  
            animSet.setAnimationListener(new AnimationListener() {  
                public void onAnimationStart(Animation animation) {  
                }  
  
                public void onAnimationRepeat(Animation animation) {  
                }  
  
                public void onAnimationEnd(Animation animation) {  
                    txt.setOnClickListener(null);  
                    txt.setClickable(false);  
                    txt.setVisibility(View.GONE);  
                }  
            });  
        }  
    }  
  
    private boolean show() {  
        if (width > 0 && height > 0 && vecKeywords != null && vecKeywords.size() > 0 && enableShow) {  
            enableShow = false;  
            lastStartAnimationTime = System.currentTimeMillis();  
            int xCenter = width >> 1, yCenter = height >> 1;  
            int size = vecKeywords.size();  
            int xItem = width / size, yItem = height / size;  
            // Log.d("ANDROID_LAB", "--------------------------width=" + width +   
            // " height=" + height + "  xItem=" + xItem   
            // + " yItem=" + yItem + "---------------------------");   
            LinkedList<Integer> listX = new LinkedList<Integer>(), listY = new LinkedList<Integer>();  
            for (int i = 0; i < size; i++) {  
                // 鍑嗗闅忔満鍊欓�鏁帮紝鍒嗗埆瀵瑰簲x/y杞翠綅缃�  
                listX.add(i * xItem);  
                listY.add(i * yItem + (yItem >> 2));  
            }  
            // TextView[] txtArr = new TextView[size];   
            LinkedList<TextView> listTxtTop = new LinkedList<TextView>();  
            LinkedList<TextView> listTxtBottom = new LinkedList<TextView>();  
            for (int i = 0; i < size; i++) {  
                String keyword = vecKeywords.get(i);  
                // 闅忔満棰滆壊   
                int ranColor = 0xff000000 | random.nextInt(0x0077ffff);  
                // 闅忔満浣嶇疆锛岀硻鍊�  
                int xy[] = randomXY(random, listX, listY, xItem);  
                // 闅忔満瀛椾綋澶у皬   
                int txtSize = TEXT_SIZE_MIN + random.nextInt(TEXT_SIZE_MAX - TEXT_SIZE_MIN + 1);  
                // 瀹炰緥鍖朤extView   
                final TextView txt = new TextView(getContext());  
                txt.setOnClickListener(itemClickListener);  
                txt.setText(keyword);  
                txt.setTextColor(ranColor);  
                txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);  
                txt.setShadowLayer(2, 2, 2, 0xff696969);  
                txt.setGravity(Gravity.CENTER);  
                // 鑾峰彇鏂囨湰闀垮害   
                Paint paint = txt.getPaint();  
                int strWidth = (int) Math.ceil(paint.measureText(keyword));  
                xy[IDX_TXT_LENGTH] = strWidth;  
                // 绗竴娆′慨姝�淇x鍧愭爣   
                if (xy[IDX_X] + strWidth > width - (xItem >> 1)) {  
                    int baseX = width - strWidth;  
                    // 鍑忓皯鏂囨湰鍙宠竟缂樹竴鏍风殑姒傜巼   
                    xy[IDX_X] = baseX - xItem + random.nextInt(xItem >> 1);  
                } else if (xy[IDX_X] == 0) {  
                    // 鍑忓皯鏂囨湰宸﹁竟缂樹竴鏍风殑姒傜巼   
                    xy[IDX_X] = Math.max(random.nextInt(xItem), xItem / 3);  
                }  
                xy[IDX_DIS_Y] = Math.abs(xy[IDX_Y] - yCenter);  
                txt.setTag(xy);  
                if (xy[IDX_Y] > yCenter) {  
                    listTxtBottom.add(txt);  
                } else {  
                    listTxtTop.add(txt);  
                }  
            }  
            attach2Screen(listTxtTop, xCenter, yCenter, yItem);  
            attach2Screen(listTxtBottom, xCenter, yCenter, yItem);  
            return true;  
        }  
        return false;  
    }  
  
    /** 淇TextView鐨刌鍧愭爣灏嗗皢鍏舵坊鍔犲埌瀹瑰櫒涓娿� */  
    private void attach2Screen(LinkedList<TextView> listTxt, int xCenter, int yCenter, int yItem) {  
        int size = listTxt.size();  
        sortXYList(listTxt, size);  
        for (int i = 0; i < size; i++) {  
            TextView txt = listTxt.get(i);  
            int[] iXY = (int[]) txt.getTag();  
            // Log.d("ANDROID_LAB", "fix[  " + txt.getText() + "  ] x:" +   
            // iXY[IDX_X] + " y:" + iXY[IDX_Y] + " r2="   
            // + iXY[IDX_DIS_Y]);   
            // 绗簩娆′慨姝�淇y鍧愭爣   
            int yDistance = iXY[IDX_Y] - yCenter;  
            // 瀵逛簬鏈�潬杩戜腑蹇冪偣鐨勶紝鍏跺�涓嶄細澶т簬yItem<br/>   
            // 瀵逛簬鍙互涓�矾涓嬮檷鍒颁腑蹇冪偣鐨勶紝鍒欒鍊间篃鏄叾搴旇皟鏁寸殑澶у皬<br/>   
            int yMove = Math.abs(yDistance);  
            inner: for (int k = i - 1; k >= 0; k--) {  
                int[] kXY = (int[]) listTxt.get(k).getTag();  
                int startX = kXY[IDX_X];  
                int endX = startX + kXY[IDX_TXT_LENGTH];  
                // y杞翠互涓績鐐逛负鍒嗛殧绾匡紝鍦ㄥ悓涓�晶   
                if (yDistance * (kXY[IDX_Y] - yCenter) > 0) {  
                    // Log.d("ANDROID_LAB", "compare:" +   
                    // listTxt.get(k).getText());   
                    if (isXMixed(startX, endX, iXY[IDX_X], iXY[IDX_X] + iXY[IDX_TXT_LENGTH])) {  
                        int tmpMove = Math.abs(iXY[IDX_Y] - kXY[IDX_Y]);  
                        if (tmpMove > yItem) {  
                            yMove = tmpMove;  
                        } else if (yMove > 0) {  
                            // 鍙栨秷榛樿鍊笺�   
                            yMove = 0;  
                        }  
                        // Log.d("ANDROID_LAB", "break");   
                        break inner;  
                    }  
                }  
            }  
            // Log.d("ANDROID_LAB", txt.getText() + " yMove=" + yMove);   
            if (yMove > yItem) {  
                int maxMove = yMove - yItem;  
                int randomMove = random.nextInt(maxMove);  
                int realMove = Math.max(randomMove, maxMove >> 1) * yDistance / Math.abs(yDistance);  
                iXY[IDX_Y] = iXY[IDX_Y] - realMove;  
                iXY[IDX_DIS_Y] = Math.abs(iXY[IDX_Y] - yCenter);  
                // 宸茬粡璋冩暣杩囧墠i涓渶瑕佸啀娆℃帓搴�  
                sortXYList(listTxt, i + 1);  
            }  
            FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,  
                    FrameLayout.LayoutParams.WRAP_CONTENT);  
            layParams.gravity = Gravity.LEFT | Gravity.TOP;  
            layParams.leftMargin = iXY[IDX_X];  
            layParams.topMargin = iXY[IDX_Y];  
            addView(txt, layParams);  
            // 鍔ㄧ敾   
            AnimationSet animSet = getAnimationSet(iXY, xCenter, yCenter, txtAnimInType);  
            txt.startAnimation(animSet);  
        }  
    }  
  
    public AnimationSet getAnimationSet(int[] xy, int xCenter, int yCenter, int type) {  
        AnimationSet animSet = new AnimationSet(true);  
        animSet.setInterpolator(interpolator);  
        if (type == OUTSIDE_TO_LOCATION) {  
            animSet.addAnimation(animAlpha2Opaque);  
            animSet.addAnimation(animScaleLarge2Normal);  
            TranslateAnimation translate = new TranslateAnimation(  
                    (xy[IDX_X] + (xy[IDX_TXT_LENGTH] >> 1) - xCenter) << 1, 0, (xy[IDX_Y] - yCenter) << 1, 0);  
            animSet.addAnimation(translate);  
        } else if (type == LOCATION_TO_OUTSIDE) {  
            animSet.addAnimation(animAlpha2Transparent);  
            animSet.addAnimation(animScaleNormal2Large);  
            TranslateAnimation translate = new TranslateAnimation(0,  
                    (xy[IDX_X] + (xy[IDX_TXT_LENGTH] >> 1) - xCenter) << 1, 0, (xy[IDX_Y] - yCenter) << 1);  
            animSet.addAnimation(translate);  
        } else if (type == LOCATION_TO_CENTER) {  
            animSet.addAnimation(animAlpha2Transparent);  
            animSet.addAnimation(animScaleNormal2Zero);  
            TranslateAnimation translate = new TranslateAnimation(0, (-xy[IDX_X] + xCenter), 0, (-xy[IDX_Y] + yCenter));  
            animSet.addAnimation(translate);  
        } else if (type == CENTER_TO_LOCATION) {  
            animSet.addAnimation(animAlpha2Opaque);  
            animSet.addAnimation(animScaleZero2Normal);  
            TranslateAnimation translate = new TranslateAnimation((-xy[IDX_X] + xCenter), 0, (-xy[IDX_Y] + yCenter), 0);  
            animSet.addAnimation(translate);  
        }  
        animSet.setDuration(animDuration);  
        return animSet;  
    }  
  
    /** 
     * 鏍规嵁涓庝腑蹇冪偣鐨勮窛绂荤敱杩戝埌杩滆繘琛屽啋娉℃帓搴忋� 
     *  
     * @param endIdx 
     *            璧峰浣嶇疆銆�
     * @param txtArr 
     *            寰呮帓搴忕殑鏁扮粍銆�
     *  
     */  
    private void sortXYList(LinkedList<TextView> listTxt, int endIdx) {  
        for (int i = 0; i < endIdx; i++) {  
            for (int k = i + 1; k < endIdx; k++) {  
                if (((int[]) listTxt.get(k).getTag())[IDX_DIS_Y] < ((int[]) listTxt.get(i).getTag())[IDX_DIS_Y]) {  
                    TextView iTmp = listTxt.get(i);  
                    TextView kTmp = listTxt.get(k);  
                    listTxt.set(i, kTmp);  
                    listTxt.set(k, iTmp);  
                }  
            }  
        }  
    }  
  
    /** A绾挎涓嶣绾挎鎵�唬琛ㄧ殑鐩寸嚎鍦╔杞存槧灏勪笂鏄惁鏈変氦闆嗐� */  
    private boolean isXMixed(int startA, int endA, int startB, int endB) {  
        boolean result = false;  
        if (startB >= startA && startB <= endA) {  
            result = true;  
        } else if (endB >= startA && endB <= endA) {  
            result = true;  
        } else if (startA >= startB && startA <= endB) {  
            result = true;  
        } else if (endA >= startB && endA <= endB) {  
            result = true;  
        }  
        return result;  
    }  
  
    private int[] randomXY(Random ran, LinkedList<Integer> listX, LinkedList<Integer> listY, int xItem) {  
        int[] arr = new int[4];  
        arr[IDX_X] = listX.remove(ran.nextInt(listX.size()));  
        arr[IDX_Y] = listY.remove(ran.nextInt(listY.size()));  
        return arr;  
    }  
  
    public void onGlobalLayout() {  
        int tmpW = getWidth();  
        int tmpH = getHeight();  
        if (width != tmpW || height != tmpH) {  
            width = tmpW;  
            height = tmpH;  
            show();  
        }  
    }  
  
    public Vector<String> getKeywords() {  
        return vecKeywords;  
    }  
  
    public void rubKeywords() {  
        vecKeywords.clear();  
    }  
  
    /** 鐩存帴娓呴櫎鎵�湁鐨凾extView銆傚湪娓呴櫎涔嬪墠涓嶄細鏄剧ず鍔ㄧ敾銆�*/  
    public void rubAllViews() {  
        removeAllViews();  
    }  
  
    public void setOnItemClickListener(OnClickListener listener) {  
        itemClickListener = listener;  
    }  
  
    // public void onDraw(Canvas canvas) {   
    // super.onDraw(canvas);   
    // Paint p = new Paint();   
    // p.setColor(Color.BLACK);   
    // canvas.drawCircle((width >> 1) - 2, (height >> 1) - 2, 4, p);   
    // p.setColor(Color.RED);   
    // }  

}
