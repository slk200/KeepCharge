package com.tizzer.keepcharge.callback;

import com.tizzer.keepcharge.bean.BillBean;

public interface OnBillClickedListener {
    void onBillClicked(BillBean billBean, int index);
}