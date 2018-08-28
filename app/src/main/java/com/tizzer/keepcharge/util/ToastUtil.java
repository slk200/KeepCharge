package com.tizzer.keepcharge.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void simpleToast(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
