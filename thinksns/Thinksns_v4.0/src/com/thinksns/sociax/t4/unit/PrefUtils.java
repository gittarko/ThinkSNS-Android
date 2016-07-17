package com.thinksns.sociax.t4.unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public class PrefUtils {
    private static final String PREF_IS_LOCAL_REGISTER = "pref_is_local_register";

    /**
     * 设置是否是本地注册
     *
     * @param context the context
     * @param isLocal 是否本地注册
     */
    public static void setPrefIsLocalRegister(@NonNull Context context, boolean isLocal) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_IS_LOCAL_REGISTER, isLocal).apply();
    }

    /**
     * 获取是否本地注册，默认为false
     *
     * @param context the context
     * @return
     */
    public static boolean getPrefIsLocalRegister(@NonNull Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_IS_LOCAL_REGISTER, false);
    }
}
