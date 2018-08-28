package com.tizzer.keepcharge.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tizzer.keepcharge.constant.ConstantsValue;

public class SPUtil {

    public static void putBoolean(Context context, String key, boolean bool) {
        SharedPreferences sp = context.getSharedPreferences(ConstantsValue.share_name, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, bool).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(ConstantsValue.share_name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
}
