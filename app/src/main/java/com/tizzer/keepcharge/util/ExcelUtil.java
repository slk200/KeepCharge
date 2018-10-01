package com.tizzer.keepcharge.util;

import android.content.Context;

import com.tizzer.keepcharge.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    private final static String UTF8_ENCODING = "UTF-8";
    private static WritableCellFormat arial10format = null;
    private static WritableCellFormat arial12format = null;

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10font.setColour(Colour.WHITE);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(Alignment.CENTRE);
            arial10format.setBorder(Border.ALL, BorderLineStyle.THIN);
            arial10format.setBackground(Colour.SEA_GREEN);

            WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 12);
            arial12format = new WritableCellFormat(arial12font);
            arial12format.setAlignment(Alignment.CENTRE);//对齐格式
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN); //设置边框

        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Excel
     *
     * @param fileName
     * @param colName
     */
    public static void initExcel(String fileName, String[] colName) {
        format();
        WritableWorkbook writableWorkbook;
        try {
            writableWorkbook = Workbook.createWorkbook(new File(fileName));
            WritableSheet sheet = writableWorkbook.createSheet("账单", 0);
            //创建标题栏
            sheet.addCell(new Label(0, 0, fileName, arial12format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            sheet.setRowView(0, 340); //设置行高

            writableWorkbook.write();
            writableWorkbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<T> objList, String fileName, Context context) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writableWorkbook;
            InputStream inputStream;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                inputStream = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(inputStream);
                writableWorkbook = Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writableWorkbook.getSheet(0);

//              sheet.mergeCells(0,1,0,objList.size()); //合并单元格
//              sheet.mergeCells()

                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i), arial12format));
                        if (list.get(i).length() <= 3) {
                            sheet.setColumnView(i, list.get(i).length() + 8); //设置列宽
                        } else {
                            sheet.setColumnView(i, list.get(i).length() + 5); //设置列宽
                        }
                    }
                    sheet.setRowView(j + 1, 350); //设置行高
                }

                writableWorkbook.write();
                writableWorkbook.close();
                inputStream.close();

                ToastUtil.simpleToast(context, R.string.output_to_folder_document_successful);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}