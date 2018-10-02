package com.tizzer.keepcharge.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.StoreSpinnerAdapter;
import com.tizzer.keepcharge.database.OrmLiteHelper;
import com.tizzer.keepcharge.util.ExcelUtil;
import com.tizzer.keepcharge.util.FileUtil;
import com.tizzer.keepcharge.util.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutputActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "OutputActivity";
    private static final int REQUEST_CODE = 0;
    private static final int startYear = 2018;
    private static String[] title = {"日期", "金额￥", "备注"};

    @BindView(R.id.spinner_store)
    Spinner spinnerStore;
    @BindView(R.id.spinner_year)
    Spinner spinnerYear;
    @BindView(R.id.spinner_month)
    Spinner spinnerMonth;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int sid = -1;
    private int year = -1;
    private int month = -1;
    private List<List<String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        ButterKnife.bind(this);
        initView();
        askForPermission();
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        ToastUtil.simpleToast(getApplicationContext(), R.string.need_write_permission);
                        finish();
                    }
                }
        }
    }

    private void initView() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spinnerStore.setAdapter(new StoreSpinnerAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1, OrmLiteHelper.getHelper(getApplicationContext()).getAllStoreEntity()));
        List<String> years = new ArrayList<>();
        for (int i = startYear; i <= 2099; i++) {
            years.add(String.valueOf(i));
        }
        spinnerYear.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, years));
        List<String> months = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        spinnerMonth.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, months));

        spinnerStore.setOnItemSelectedListener(this);
        spinnerYear.setOnItemSelectedListener(this);
        spinnerMonth.setOnItemSelectedListener(this);
    }

    @OnClick(R.id.btn_output)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_output:
                writeDataToStorage();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_store:
                sid = (int) id;
                Log.e(TAG, "onItemClick: storeId: " + sid);
                break;
            case R.id.spinner_year:
                year = Integer.parseInt(parent.getSelectedItem().toString());
                Log.e(TAG, "onItemClick: year: " + year);
                break;
            case R.id.spinner_month:
                month = Integer.parseInt(parent.getSelectedItem().toString());
                Log.e(TAG, "onItemClick: month: " + month);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void writeDataToStorage() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            ToastUtil.simpleToast(getApplicationContext(), R.string.no_sd_card);
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String startDate = dateFormat.format(calendar.getTime()) + " 00:00:00.000000";
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(calendar.getTime()) + " 23:59:59.999999";
        List<String[]> bills = OrmLiteHelper.getHelper(getApplicationContext()).getBills(sid, startDate, endDate);
        exchangeBills(bills);

        FileUtil.createFolder();
        String filename = FileUtil.createFileName(year, month);
        ExcelUtil.initExcel(filename, title);
        ExcelUtil.writeObjListToExcel(data, filename, this);

    }

    private void exchangeBills(List<String[]> bills) {
        data = new ArrayList<>();
        for (String[] bill : bills) {
            List<String> billList = new ArrayList<>();
            billList.add(bill[0]);
            if (bill[3].equals("1")) {
                billList.add(bill[1]);
            } else {
                billList.add("-" + bill[1]);
            }
            billList.add(bill[2]);
            data.add(billList);
        }
    }

    public void makeDir(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
