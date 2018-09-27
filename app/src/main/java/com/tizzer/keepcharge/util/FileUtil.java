package com.tizzer.keepcharge.util;

import android.os.Environment;

import java.io.File;

public class FileUtil {
    private static final String folder = Environment.getExternalStorageDirectory() + File.separator + "Document";

    public static void createFolder() {
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String createFileName(int year, int month) {
        String oldFilename = folder + File.separator + year + "年" + month + "月账单.xls";
        String newFilename = oldFilename;
        int docN = 1;
        while (new File(newFilename).exists()) {
            docN++;
            newFilename = oldFilename + docN;
        }
        return newFilename;
    }
}
