package com.tizzer.keepcharge.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.callback.OnBillRecordListener;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.database.OrmLiteHelper;
import com.tizzer.keepcharge.entity.Bill;
import com.tizzer.keepcharge.util.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RecordBillFragment extends DialogFragment
        implements DialogInterface.OnClickListener, View.OnClickListener {

    private static final boolean IS_24_HOUR = true;
    private EditText mMoney;
    private EditText mNote;
    private EditText mDate;
    private EditText mTime;
    private boolean type = true;
    private OnBillRecordListener listener;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Context context;

    public static RecordBillFragment getInstance(int sid) {
        RecordBillFragment recordBillFragment = new RecordBillFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsValue.STORE_BEAN_TAG, sid);
        recordBillFragment.setArguments(bundle);
        return recordBillFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_record_bill, null);
        initView(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.record_bill)
                .setView(view)
                .setPositiveButton(R.string.confirm, this)
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnBillRecordListener) context;
    }

    private void initView(View view) {
        RadioGroup radioGroup = view.findViewById(R.id.rg_type);
        mMoney = view.findViewById(R.id.et_money);
        mNote = view.findViewById(R.id.et_note);
        mDate = view.findViewById(R.id.et_date);
        mTime = view.findViewById(R.id.et_time);

        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_in:
                        type = true;
                        break;
                    case R.id.rb_out:
                        type = false;
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                String money = mMoney.getText().toString().trim();
                if (money.equals("")) {
                    ToastUtil.simpleToast(context, getString(R.string.please_input_money_tip));
                    return;
                }
                String date = mDate.getText().toString();
                if (date.equals("")) {
                    ToastUtil.simpleToast(context, getString(R.string.please_input_date_tip));
                    return;
                }
                String time = mTime.getText().toString();
                if (time.equals("")) {
                    ToastUtil.simpleToast(context, getString(R.string.please_choose_time_tip));
                    return;
                }
                String note = mNote.getText().toString().trim();
                int sid = Objects.requireNonNull(getArguments()).getInt(ConstantsValue.STORE_BEAN_TAG);
                Bill bill = new Bill();
                bill.setSid(sid);
                bill.setType(type);
                bill.setMoney(Double.parseDouble(money));
                bill.setNote(note);
                bill.setDate(string2Date(date + " " + time));
                int result = OrmLiteHelper.getHelper(context).recordBill(bill);
                OrmLiteHelper.getHelper(context).updateFactByRecord(sid,
                        type, money);
                if (result == 0) {
                    ToastUtil.simpleToast(context, getString(R.string.save_error));
                } else if (result == 1) {
                    listener.onBillRecord();
                    ToastUtil.simpleToast(context, getString(R.string.save_ok));
                } else {
                    ToastUtil.simpleToast(context, getString(R.string.app_error));
                }
                break;
        }
    }

    private Date string2Date(String datetime) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_date:
                chooseDate();
                break;
            case R.id.et_time:
                chooseTime();
                break;
        }
    }

    /**
     * 选择时间
     */
    private void chooseTime() {
        Calendar calendar2 = Calendar.getInstance();
        if (timePickerDialog == null) {
            timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String time = "";
                    if (hourOfDay < 10) {
                        time += "0" + hourOfDay + ":";
                    } else {
                        time += hourOfDay + ":";
                    }
                    if (minute < 10) {
                        time += "0" + minute + ":";
                    } else {
                        time += minute + ":";
                    }
                    mTime.setText(String.format("%s00", time));
                }
            }, calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE), IS_24_HOUR);
        }
        timePickerDialog.show();
    }

    /**
     * 选择日期
     */
    private void chooseDate() {
        Calendar calendar1 = Calendar.getInstance();
        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String date = year + "-";
                    if (month + 1 < 10) {
                        date += "0" + (month + 1) + "-";
                    } else {
                        date += (month + 1) + "-";
                    }
                    if (dayOfMonth < 10) {
                        date += "0" + dayOfMonth;
                    } else {
                        date += dayOfMonth;
                    }
                    mDate.setText(date);
                }
            }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
        }
        datePickerDialog.show();
    }
}
