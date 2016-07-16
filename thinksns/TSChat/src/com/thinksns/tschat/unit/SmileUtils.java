/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thinksns.tschat.unit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.thinksns.tschat.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmileUtils {
	public static final String aoman = "[aoman]";
	public static final String baiyan = "[baiyan]";
	public static final String bishi = "[bishi]";
	public static final String bizui = "[bizui]";
	public static final String cahan = "[cahan]";
	public static final String ciya = "[ciya]";
	public static final String dabing = "[dabing]";
	public static final String daku = "[daku]";
	public static final String deyi = "[deyi]";
	public static final String fadai = "[fadai]";
	public static final String fanu = "[fanu]";
	public static final String fendou = "[fendou]";
	public static final String ganga = "[ganga]";
	public static final String guzhang = "[guzhang]";
	public static final String haha = "[haha]";
	public static final String haixiu = "[haixiu]";
	public static final String haqian = "[haqian]";
	public static final String huaixiao = "[huaixiao]";
	public static final String jingkong = "[jingkong]";
	public static final String jingya = "[jingya]";
	public static final String keai = "[keai]";
	public static final String kelian = "[kelian]";
	public static final String ku = "[ku]";
	public static final String kuaikule = "[kuaikule]";
	public static final String kulou = "[kulou]";
	public static final String kun = "[kun]";
	public static final String lenghan = "[lenghan]";
	public static final String liuhan = "[liuhan]";
	public static final String liulei = "[liulei]";
	public static final String ma = "[ma]";
	public static final String nanguo = "[nanguo]";
	public static final String pizui = "[pizui]";
	public static final String qiang = "[qiang]";
	public static final String qiaoda = "[qiaoda]";
	public static final String qinqin = "[qinqin]";
	public static final String qioudale = "[qioudale]";
	public static final String ruo = "[ruo]";
	public static final String se = "[se]";
	public static final String shuai = "[shuai]";
	public static final String shuijiao = "[shuijiao]";
	public static final String tiaopi = "[tiaopi]";
	public static final String touxiao = "[touxiao]";
	public static final String tu = "[tu]";
	public static final String wabi = "[wabi]";
	public static final String weiqu = "[weiqu]";
	public static final String weixiao = "[weixiao]";
	public static final String xia = "[xia]";
	public static final String xu = "[xu]";
	public static final String yinxian = "[yinxian]";
	public static final String yiwen = "[yiwen]";
	public static final String youhengheng = "[youhengheng]";
	public static final String yun = "[yun]";
	public static final String zaijian = "[zaijian]";
	public static final String zhemo = "[zhemo]";
	public static final String zuohengheng = "[zuohengheng]";
	public static final String zhu = "[zhu]";

	private static final Factory spannableFactory = Factory
	        .getInstance();
	
	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {
	    addPattern(emoticons, aoman, R.drawable.aoman);
	    addPattern(emoticons, baiyan, R.drawable.baiyan);
	    addPattern(emoticons, bishi, R.drawable.bishi);
	    addPattern(emoticons, bizui, R.drawable.bizui);
	    addPattern(emoticons, cahan, R.drawable.cahan);
	    addPattern(emoticons, ciya, R.drawable.ciya);
	    addPattern(emoticons,dabing, R.drawable.dabing);
	    addPattern(emoticons, daku, R.drawable.daku);
	    addPattern(emoticons, deyi, R.drawable.deyi);
	    addPattern(emoticons, fadai, R.drawable.fadai);
	    addPattern(emoticons,fanu, R.drawable.fanu);
	    addPattern(emoticons, fendou, R.drawable.fendou);
	    addPattern(emoticons, ganga, R.drawable.ganga);
	    addPattern(emoticons, guzhang, R.drawable.guzhang);
	    addPattern(emoticons, haha, R.drawable.haha);
	    addPattern(emoticons, haixiu, R.drawable.haixiu);
	    addPattern(emoticons, haqian, R.drawable.haqian);
	    addPattern(emoticons, huaixiao, R.drawable.huaixiao);
	    addPattern(emoticons, jingkong, R.drawable.jingkong);
	    addPattern(emoticons, jingya, R.drawable.jingya);
	    addPattern(emoticons, keai, R.drawable.keai);
	    addPattern(emoticons, kelian, R.drawable.kelian);
	    addPattern(emoticons, ku, R.drawable.ku);
	    addPattern(emoticons, kuaikule, R.drawable.kuaikule);
	    addPattern(emoticons, kulou, R.drawable.kulou);
	    addPattern(emoticons, kun, R.drawable.kun);
	    addPattern(emoticons, lenghan, R.drawable.lenghan);
	    addPattern(emoticons, liuhan, R.drawable.liuhan);
	    addPattern(emoticons, liulei, R.drawable.liulei);
	    addPattern(emoticons, ma, R.drawable.ma);
	    addPattern(emoticons, nanguo, R.drawable.nanguo);
	    addPattern(emoticons, pizui, R.drawable.pizui);
	    addPattern(emoticons, qiang, R.drawable.qiang);
	    addPattern(emoticons, qiaoda, R.drawable.qiaoda);
	    addPattern(emoticons, qinqin, R.drawable.qinqin);
		addPattern(emoticons, qioudale, R.drawable.qioudale);
		addPattern(emoticons,ruo, R.drawable.ruo);
		addPattern(emoticons, se, R.drawable.se);
		addPattern(emoticons, shuai, R.drawable.shuai);
		addPattern(emoticons, shuijiao, R.drawable.shuijiao);
		addPattern(emoticons, tiaopi, R.drawable.tiaopi);
		addPattern(emoticons, touxiao, R.drawable.touxiao);
		addPattern(emoticons, tu,R.drawable.tu);
		addPattern(emoticons, wabi, R.drawable.wabi);
		addPattern(emoticons, weiqu, R.drawable.weiqu);
		addPattern(emoticons, weixiao, R.drawable.weixiao);
		addPattern(emoticons, xia, R.drawable.xia);
		addPattern(emoticons, xu, R.drawable.xu);
		addPattern(emoticons, yinxian, R.drawable.yinxian);
		addPattern(emoticons, yiwen, R.drawable.yiwen);
		addPattern(emoticons, youhengheng, R.drawable.youhengheng);
		addPattern(emoticons, yun, R.drawable.yun);
		addPattern(emoticons, zaijian, R.drawable.zaijian);
		addPattern(emoticons, zhemo, R.drawable.zhemo);
		addPattern(emoticons, zuohengheng, R.drawable.zuohengheng);
		addPattern(emoticons, zhu, R.drawable.zhu);

	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
	        int resource) {
	    map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, final TextView tv, Spannable spannable) {
	    boolean hasChanges = false;
		Resources res = context.getResources();

	    for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
					Bitmap bp = ImageUtil.makeGifTransparent(context, entry.getValue());
					// 压缩表情图片
					int size = ( int) tv.getTextSize();
					Bitmap scaleBitmap = Bitmap.createScaledBitmap(bp, size, size, true);
					spannable.setSpan(new ImageSpan(context, scaleBitmap),
	                        matcher.start(), matcher.end(),
	                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
	        }
	    }
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, final TextView tv, CharSequence text) {
		if(text == null) {
			text = "";
		}
	    Spannable spannable = new Spannable.Factory().newSpannable(text);
	    addSmiles(context, tv, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	
	
}
