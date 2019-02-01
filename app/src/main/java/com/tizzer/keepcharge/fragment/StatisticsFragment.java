package com.tizzer.keepcharge.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.adapter.StoreSpinnerAdapter;
import com.tizzer.keepcharge.database.OrmLiteHelper;
import com.tizzer.keepcharge.util.ExcelUtil;
import com.tizzer.keepcharge.util.FileUtil;
import com.tizzer.keepcharge.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticsFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final int START_YEAR = 2018;
    private static final int REQUEST_CODE = 0;
    private String[] title = {"日期", "金额￥", "备注"};

    private int sid = -1;
    private int year = -1;
    private int month = -1;
    private List<List<String>> data;

    private TextView mAssetsView;
    private TextView mIncomeView;
    private TextView mPaymentView;

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        initView(view);
        askForPermission();
        return view;
    }

    /**
     * 查询权限
     */
    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    /**
     * 权限请求处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        ToastUtil.simpleToast(context, R.string.need_write_permission);
                    }
                }
        }
    }

    /**
     * 初始化
     *
     * @param view
     */
    private void initView(View view) {
        mAssetsView = view.findViewById(R.id.tv_assets);
        mIncomeView = view.findViewById(R.id.tv_income);
        mPaymentView = view.findViewById(R.id.tv_payment);
        loadData();

        view.findViewById(R.id.btn_output).setOnClickListener(this);

        Spinner spinnerStore = view.findViewById(R.id.spinner_store);
        spinnerStore.setAdapter(new StoreSpinnerAdapter(context, android.R.layout.simple_list_item_1,
                OrmLiteHelper.getHelper(context).getAllStoreEntity()));

        List<String> years = new ArrayList<>();
        for (int i = START_YEAR; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
            years.add(String.valueOf(i));
        }
        Spinner spinnerYear = view.findViewById(R.id.spinner_year);
        spinnerYear.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, years));

        Spinner spinnerMonth = view.findViewById(R.id.spinner_month);
        spinnerMonth.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}));

        spinnerStore.setOnItemSelectedListener(this);
        spinnerYear.setOnItemSelectedListener(this);
        spinnerMonth.setOnItemSelectedListener(this);
    }

    /**
     * 加载数据
     */
    public void loadData() {
        double totalAssets = OrmLiteHelper.getHelper(context).getTotalAssets();
        double totalIncome = OrmLiteHelper.getHelper(context).getTotalIncome();
        double totalPayment = OrmLiteHelper.getHelper(context).getTotalPayment();

        mAssetsView.setText(String.format(Locale.CHINA, "%.2f", totalAssets));
        mIncomeView.setText(String.format(Locale.CHINA, "%.2f", totalIncome));
        mPaymentView.setText(String.format(Locale.CHINA, "%.2f", totalPayment));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            loadData();
        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_output:
                writeDataToStorage();
                break;
        }
    }

    /**
     * 下拉菜单选择事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_store:
                sid = (int) id;
                break;
            case R.id.spinner_year:
                year = Integer.parseInt(parent.getSelectedItem().toString());
                break;
            case R.id.spinner_month:
                month = Integer.parseInt(parent.getSelectedItem().toString());
                break;
        }
    }

    /**
     * 下拉菜单未选择事件
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 把数据写入外部存储
     */
    private void writeDataToStorage() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            ToastUtil.simpleToast(context, R.string.no_sd_card);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String startDate = dateFormat.format(calendar.getTime()) + " 00:00:00.000000";
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(calendar.getTime()) + " 23:59:59.999999";
        List<String[]> bills = OrmLiteHelper.getHelper(context).getBills(sid, startDate, endDate);
        if (bills == null || bills.isEmpty()) {
            ToastUtil.simpleToast(context, R.string.no_record);
            return;
        }
        exchangeBills(bills);

        FileUtil.createFolder();
        String filename = FileUtil.createFileName(year, month);
        ExcelUtil.initExcel(filename, title);
        ExcelUtil.writeObjListToExcel(data, filename, context);
    }

    /**
     * 转换账单数据的类型
     * <note>
     * List<String[]>  ->  ArrayList<ArrayList<String>>
     *
     * @param bills
     */
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
}
