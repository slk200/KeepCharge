package com.tizzer.keepcharge.callback;

import com.tizzer.keepcharge.bean.StoreBean;

public interface OnStoreAddedListener {
    /**
     * 店铺增加事件
     *
     * @param storeBean 增加店铺的原始数据
     */
    void onStoreAdded(StoreBean storeBean);
}
