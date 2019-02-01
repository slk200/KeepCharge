package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.database.OrmLiteHelper;
import com.tizzer.keepcharge.util.ToastUtil;

public class BillActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mMoneyView;
    private EditText mMoneyEdit;

    private BillBean billBean;
    private StoreBean storeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        billBean = (BillBean) getIntent().getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
        storeBean = (StoreBean) getIntent().getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mMoneyView = findViewById(R.id.tv_money);
        mMoneyView.setText(String.format("%s%s", billBean.getType() ? "+" : "-", billBean.getMoney()));

        TextView mNoteView = findViewById(R.id.tv_note);
        mNoteView.setText(billBean.getNote());

        TextView mTimeView = findViewById(R.id.tv_time);
        mTimeView.setText(billBean.getTime());

        mMoneyEdit = findViewById(R.id.et_money);

        findViewById(R.id.iv_submit).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_submit:
                updateBillMoney();
                break;
            case R.id.btn_update:
                updateBillType();
                break;
        }
    }

    /**
     * 修改账单金额
     */
    private void updateBillMoney() {
        String money = mMoneyEdit.getText().toString().trim();
        if (money.equals("")) {
            ToastUtil.simpleToast(this, getString(R.string.money_can_not_be_null));
            return;
        }

        double newMoney = Double.parseDouble(money);
        double dValue = newMoney - billBean.getMoney();
        int result = OrmLiteHelper.getHelper(this).updateBillMoney(billBean.getId(), money);
        OrmLiteHelper.getHelper(this).updateFactByMoney(storeBean.getId(), billBean.getType(), String.valueOf(dValue));
        switch (result) {
            case ConstantsValue.ORIGINAL_CODE:
                ToastUtil.simpleToast(this, getString(R.string.update_error));
                break;
            case ConstantsValue.RIGHT_CODE:
                billBean.setMoney(Double.parseDouble(money));
                deliverData();
                break;
            case ConstantsValue.FALSE_CODE:
                ToastUtil.simpleToast(this, getString(R.string.app_error));
                break;
        }

    }

    /**
     * 修改账单类型
     */
    private void updateBillType() {
        billBean.setType(!billBean.getType());
        int result = OrmLiteHelper.getHelper(this).updateBillType(billBean.getId(), billBean.getType());
        OrmLiteHelper.getHelper(this).updateFactByType(storeBean.getId(), billBean.getType(), String.valueOf(billBean.getMoney()));
        switch (result) {
            case ConstantsValue.ORIGINAL_CODE:
                ToastUtil.simpleToast(this, getString(R.string.update_error));
                break;
            case ConstantsValue.RIGHT_CODE:
                deliverData();
                break;
            case ConstantsValue.FALSE_CODE:
                ToastUtil.simpleToast(this, getString(R.string.app_error));
                break;
        }
    }

    /**
     * 回传数据
     * <note>
     * 当修改账单属性时触发
     */
    private void deliverData() {
        ToastUtil.simpleToast(this, getString(R.string.update_ok));
        mMoneyView.setText(String.format("%s%s", billBean.getType() ? "+" : "-", billBean.getMoney()));
        Intent intent = new Intent();
        intent.putExtra(ConstantsValue.BILL_BEAN_TAG, billBean);
        setResult(RESULT_OK, intent);
    }

}
