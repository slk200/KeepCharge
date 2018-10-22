package com.tizzer.keepcharge.callback;

import com.tizzer.keepcharge.bean.StoreBean;

public interface OnStoreDeleteListener {
    /**
     * 店铺删除事件
     *
     * @param storeBean 被删除店铺的原始数据
     */
    void onStoreDelete(StoreBean storeBean);
}
