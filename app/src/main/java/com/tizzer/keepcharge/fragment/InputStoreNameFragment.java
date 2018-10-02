package com.tizzer.keepcharge.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnStoreAddedListener;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.database.OrmLiteHelper;
import com.tizzer.keepcharge.util.ToastUtil;

import java.util.Objects;

public class InputStoreNameFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private EditText mStoreName;
    private OnStoreAddedListener listener;

    public static InputStoreNameFragment getInstance() {
        return new InputStoreNameFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStoreAddedListener) {
            listener = (OnStoreAddedListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_input_store_name, null);
        initView(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(R.string.please_input_store_name)
                .setView(view)
                .setPositiveButton(R.string.confirm, this)
                .setNegativeButton(R.string.cancel, this);
        return builder.create();
    }

    private void initView(View view) {
        mStoreName = view.findViewById(R.id.tiet_store_name);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                SaveStore();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
        }
    }

    /**
     * 添加一个新店铺
     */
    private void SaveStore() {
        String storeName = mStoreName.getText().toString().trim();
        if (storeName.equals("")) {
            ToastUtil.simpleToast(getActivity(), getString(R.string.store_name_can_not_be_null));
            return;
        }
        int result = OrmLiteHelper.getHelper(getActivity()).saveStore(storeName);
        if (result == ConstantsValue.RIGHT_CODE) {
            int id = OrmLiteHelper.getHelper(getActivity()).getStoreId(storeName);
            if (id != ConstantsValue.FALSE_CODE) {
                listener.onStoreAdded(new StoreBean(id, storeName, 0, 0, 0, 1));
            } else {
                ToastUtil.simpleToast(getActivity(), getString(R.string.app_error));
            }
        } else if (result == ConstantsValue.ORIGINAL_CODE) {
            ToastUtil.simpleToast(getActivity(), getString(R.string.repeat_store_name));
        } else {
            ToastUtil.simpleToast(getActivity(), getString(R.string.app_error));
        }
    }
}
