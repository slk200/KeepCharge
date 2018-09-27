package com.tizzer.keepcharge.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.callback.OnBillRecordListener;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.db.OrmLiteHelper;
import com.tizzer.keepcharge.entity.Bill;
import com.tizzer.keepcharge.util.ToastUtil;

import java.util.Date;

public class RecordBillFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private EditText mMoney;
    private EditText mNote;
    private boolean type = true;
    private OnBillRecordListener listener;

    public static RecordBillFragment getInstance(int sid) {
        RecordBillFragment recordBillFragment = new RecordBillFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsValue.STORE_BEAN_TAG, sid);
        recordBillFragment.setArguments(bundle);
        return recordBillFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_record_bill, null);
        initView(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.record_bill)
                .setView(view)
                .setPositiveButton(R.string.confirm, this)
                .setNegativeButton(R.string.cancel, this);
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
            case AlertDialog.BUTTON_NEGATIVE:
                break;
            case AlertDialog.BUTTON_POSITIVE:
                String money = mMoney.getText().toString().trim();
                if (money.equals("")) {
                    ToastUtil.simpleToast(getActivity(), getString(R.string.please_input_money));
                    return;
                }
                String note = mNote.getText().toString().trim();
                Bill bill = new Bill();
                bill.setSid(getArguments().getInt(ConstantsValue.STORE_BEAN_TAG));
                bill.setType(type);
                bill.setMoney(Double.parseDouble(money));
                bill.setNote(note);
                bill.setDate(new Date());
                int result = OrmLiteHelper.getHelper(getActivity()).recordBill(bill);
                if (result == 0) {
                    ToastUtil.simpleToast(getActivity(), getString(R.string.save_error));
                } else if (result == 1) {
                    listener.onBillRecord();
                    ToastUtil.simpleToast(getActivity(), getString(R.string.save_ok));
                } else {
                    ToastUtil.simpleToast(getActivity(), getString(R.string.app_error));
                }
                break;
        }
    }
}
