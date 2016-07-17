package com.thinksns.sociax.t4.unit;

import com.thinksns.sociax.t4.android.widget.ContactItemInterface;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Comparator;

/**
 * Created by hedong on 15/12/28.
 */
public class PinyinComparator implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        if(!(lhs instanceof ContactItemInterface) || !(rhs instanceof ContactItemInterface))
            return -1;
        String first = ((ContactItemInterface)lhs).getItemForIndex();
        String second = ((ContactItemInterface)rhs).getItemForIndex();
        char c1 = first.charAt(0);
        char c2 = second.charAt(0);
        return concatPinyinStringArray(
                PinyinHelper.toHanyuPinyinStringArray(c1)).compareTo(
                concatPinyinStringArray(PinyinHelper
                        .toHanyuPinyinStringArray(c2)));
    }

    private String concatPinyinStringArray(String[] pinyinArray) {
        StringBuffer pinyinSbf = new StringBuffer();
        if ((pinyinArray != null) && (pinyinArray.length > 0)) {
            for (int i = 0; i < pinyinArray.length; i++) {
                pinyinSbf.append(pinyinArray[i]);
            }
        }
        return pinyinSbf.toString();
    }

}
