package com.tizzer.keepcharge.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tizzer.keepcharge.constant.ConstantsValue;

public class SPUtil {

    public static void putBoolean(Context context, String key, boolean bool) {
        SharedPreferences sp = context.getSharedPreferences(ConstantsValue.SHARE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, bool).commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(ConstantsValue.SHARE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
}
