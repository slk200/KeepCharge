package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.bean.BillBean;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.db.OrmLiteHelper;
import com.tizzer.keepcharge.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private Intent intent;
    private int isYesterday = ConstantsValue.ORIGINAL_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spec_bill);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        billBean = (BillBean) getIntent().getSerializableExtra(ConstantsValue.BILL_BEAN_TAG);
        mMoneyView.setText((billBean.getType() ? "+" : "-") + billBean.getMoney());
        mNoteView.setText(billBean.getNote());
        mTimeView.setText(billBean.getTime());
    }

    @OnClick({R.id.iv_back, R.id.iv_submit, R.id.btn_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_submit:
                String money = mMoneyEdit.getText().toString().trim();
                if (money.equals("")) {
                    ToastUtil.simpleToast(getApplicationContext(), getString(R.string.money_can_bot_be_null));
                } else {
                    int result = OrmLiteHelper.getHelper(getApplicationContext()).updateBillMoney(billBean.getId(), money);
                    switch (result) {
                        case ConstantsValue.ORIGINAL_CODE:
                            ToastUtil.simpleToast(getApplicationContext(), getString(R.string.update_error));
                            break;
                        case ConstantsValue.RIGHT_CODE:
                            double newValue = Double.parseDouble(money);
                            double d_value = newValue - billBean.getMoney();
                            billBean.setMoney(newValue);
                            deliverData(ConstantsValue.FALSE_CODE, d_value);
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
                switch (result) {
                    case ConstantsValue.ORIGINAL_CODE:
                        ToastUtil.simpleToast(getApplicationContext(), getString(R.string.update_error));
                        break;
                    case ConstantsValue.RIGHT_CODE:
                        deliverData(ConstantsValue.RIGHT_CODE, 0);
                        break;
                    case ConstantsValue.FALSE_CODE:
                        ToastUtil.simpleToast(getApplicationContext(), getString(R.string.app_error));
                        break;
                }
                break;
        }
    }

    private void deliverData(int code, double value) {
        ToastUtil.simpleToast(getApplicationContext(), getString(R.string.update_ok));
        mMoneyView.setText((billBean.getType() ? "+" : "-") + billBean.getMoney());
        if (intent == null) {
            intent = new Intent();
        }
        if (isYesterday == ConstantsValue.ORIGINAL_CODE) {
            isYesterday = judgeTime(billBean.getTime());
        }
        intent.putExtra(ConstantsValue.IS_YESTERDAY_TAG, isYesterday);
        intent.putExtra(ConstantsValue.IS_CHANGE_TYPE_TAG, code);
        intent.putExtra(ConstantsValue.D_VALUE_TAG, value);
        intent.putExtra(ConstantsValue.BILL_BEAN_TAG, billBean);
        setResult(RESULT_OK, intent);
    }

    private int judgeTime(String time) {
        /**
         * 获取当前日期的前一天
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //当前日期减一天
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String date = dateFormat.format(calendar.getTime());
        return time.startsWith(date) ? ConstantsValue.RIGHT_CODE : ConstantsValue.FALSE_CODE;
    }
}
