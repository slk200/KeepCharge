package com.tizzer.keepcharge.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tizzer.keepcharge.constant.ConstantsValue;

public class SharedPreferencesUtil {

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(ConstantsValue.SHARE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static void putBoolean(Context context, String key, boolean bool) {
        sharedPreferences = getInstance(context);
        sharedPreferences.edit().putBoolean(key, bool).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        sharedPreferences = getInstance(context);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void putString(Context context, String key, String value) {
        sharedPreferences = getInstance(context);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key) {
        sharedPreferences = getInstance(context);
        return sharedPreferences.getString(key, "");
    }
}
