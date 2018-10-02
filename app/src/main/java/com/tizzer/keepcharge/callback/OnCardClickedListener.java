package com.tizzer.keepcharge.callback;

import com.tizzer.keepcharge.bean.StoreBean;

public interface OnCardClickedListener {
    void onStoreClick(StoreBean storeBean, int position);

    void onAddClick();
}