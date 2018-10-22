package com.tizzer.keepcharge.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.callback.OnStoreDeleteListener;
import com.tizzer.keepcharge.constant.ConstantsValue;

public class DeleteStoreFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private OnStoreDeleteListener listener;
    private StoreBean storeBean;

    public static DeleteStoreFragment getInstance(StoreBean storeBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsValue.STORE_BEAN_TAG, storeBean);
        DeleteStoreFragment deleteStoreFragment = new DeleteStoreFragment();
        deleteStoreFragment.setArguments(bundle);
        return deleteStoreFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStoreDeleteListener) {
            listener = (OnStoreDeleteListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        storeBean = (StoreBean) getArguments().getSerializable(ConstantsValue.STORE_BEAN_TAG);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("是否要删除店铺\n\n" + storeBean.getName())
                .setPositiveButton(R.string.confirm, this)
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                listener.onStoreDelete(storeBean);
                break;
        }
    }
}
