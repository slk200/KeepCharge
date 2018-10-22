package com.tizzer.keepcharge.callback;

import com.tizzer.keepcharge.bean.BillBean;

public interface OnBillClickedListener {
    /**
     * 账单点击事件
     *
     * @param billBean 被点击账单原始数据
     * @param index    账单所在的位置
     */
    void onBillClicked(BillBean billBean, int index);
}