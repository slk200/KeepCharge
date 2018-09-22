package com.tizzer.keepcharge.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.Store;
import com.tizzer.keepcharge.callback.OnStoreAddedListener;
import com.tizzer.keepcharge.util.ToastUtil;

public class InputStoreNameFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String TAG = "InputStoreNameFragment";
    private EditText mStoreName;
    private OnStoreAddedListener listener;

    public static InputStoreNameFragment newInstance() {
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_input_store_name, null);
        initView(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.please_input_store_name)
                .setView(view)
                .setNegativeButton(R.string.confirm, this)
                .setPositiveButton(R.string.cancel, this)
                .setCancelable(false);
        return builder.create();
    }

    private void initView(View view) {
        mStoreName = view.findViewById(R.id.tiet_store_name);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case AlertDialog.BUTTON_NEGATIVE:
                String storeName = mStoreName.getText().toString();
//                ToastUtil.simpleToast(getContext(), storeName);
//                Log.e(TAG, "onClick: " + storeName);
                listener.onStoreAdded(new Store(7, storeName, 45, 45, 1));
                break;
            case AlertDialog.BUTTON_POSITIVE:
                ToastUtil.simpleToast(getContext(), String.valueOf(which));
                dialog.dismiss();
                break;
        }
    }
}
