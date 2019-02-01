package com.tizzer.keepcharge.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.tizzer.keepcharge.constant.ConstantsValue;

public class SharedPreferencesUtil {

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(ConstantsValue.SHARE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static boolean getBoolean(Context context, String key) {
        sharedPreferences = getInstance(context);
        return sharedPreferences.getBoolean(key, false);
    }

    public static String getString(Context context, String key) {
        sharedPreferences = getInstance(context);
        return sharedPreferences.getString(key, "");
    }
}
