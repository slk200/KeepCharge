package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.bean.StoreBean;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.db.OrmLiteHelper;
import com.tizzer.keepcharge.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpecBillActivity extends AppCompatActivity {

    @BindView(R.id.tv_money)
    TextView mMoneyView;
    @BindView(R.id.tv_note)
    TextView mNoteView;
    @BindView(R.id.tv_time)
    TextView mTimeView;
    @BindView(R.id.et_money)
    EditText mMoneyEdit;

    private BillBean billBean;
    private StoreBean storeBean;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spec_bill);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        billBean = (BillBean) getIntent().getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
        storeBean = (StoreBean) getIntent().getSerializableExtra(ConstantsValue.STORE_BEAN_TAG);
        mMoneyView.setText((billBean.getType() ? "+" : "-") + billBean.getMoney());
        mNoteView.setText(billBean.getNote());
        mTimeView.setText(billBean.getTime());
    }

    @OnClick({R.id.iv_back, R.id.iv_submit, R.id.btn_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                break;
            case R.id.iv_submit:
                String money = mMoneyEdit.getText().toString().trim();
                if (money.equals("")) {
                    ToastUtil.simpleToast(getApplicationContext(), getString(R.string.money_can_bot_be_null));
                } else {
                    double newMoney = Double.parseDouble(money);
                    double dValue = newMoney - billBean.getMoney();
                    int result = OrmLiteHelper.getHelper(getApplicationContext()).updateBillMoney(billBean.getId(), money);
                    OrmLiteHelper.getHelper(getApplicationContext()).updateFactByMoney(storeBean.getId(), billBean.getType(), String.valueOf(dValue));
                    switch (result) {
                        case ConstantsValue.ORIGINAL_CODE:
                            ToastUtil.simpleToast(getApplicationContext(), getString(R.string.update_error));
                            break;
                        case ConstantsValue.RIGHT_CODE:
                            billBean.setMoney(Double.parseDouble(money));
                            deliverData();
                            break;
                        case ConstantsValue.FALSE_CODE:
                            ToastUtil.simpleToast(getApplicationContext(), getString(R.string.app_error));
                            break;
                    }
                }
                break;
            case R.id.btn_update:
                billBean.setType(!billBean.getType());
                int result = OrmLiteHelper.getHelper(getApplicationContext()).updateBillType(billBean.getId(), billBean.getType());
                OrmLiteHelper.getHelper(getApplicationContext()).updateFactByType(storeBean.getId(), billBean.getType(), String.valueOf(billBean.getMoney()));
                switch (result) {
                    case ConstantsValue.ORIGINAL_CODE:
                        ToastUtil.simpleToast(getApplicationContext(), getString(R.string.update_error));
                        break;
                    case ConstantsValue.RIGHT_CODE:
                        deliverData();
                        break;
                    case ConstantsValue.FALSE_CODE:
                        ToastUtil.simpleToast(getApplicationContext(), getString(R.string.app_error));
                        break;
                }
                break;
        }
    }

    private void deliverData() {
        ToastUtil.simpleToast(getApplicationContext(), getString(R.string.update_ok));
        mMoneyView.setText((billBean.getType() ? "+" : "-") + billBean.getMoney());
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra(ConstantsValue.BILL_BEAN_TAG, billBean);
        setResult(RESULT_OK, intent);
    }

}
