package com.tizzer.keepcharge.callback;

import com.tizzer.keepcharge.bean.StoreBean;

public interface OnCardClickedListener {
    /**
     * 店铺点击事件
     *
     * @param storeBean 被点击店铺原始数据
     * @param position  店铺所在的位置
     */
    void onStoreClick(StoreBean storeBean, int position);

    /**
     * 店铺长按事件
     *
     * @param storeBean
     * @param position
     */
    void onStoreLongClick(StoreBean storeBean, int position);

    /**
     * 增加店铺事件
     */
    void onAddClick();
}