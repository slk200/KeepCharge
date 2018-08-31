package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.util.SPUtil;
import com.tizzer.keepcharge.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    //    private static final String ACCOUNT = "wanglaoban";
    //    private static final String LOCK = "wanglaobandeapp";
    private static final String ACCOUNT = "123";
    private static final String LOCK = "123";

    @BindView(R.id.et_account)
    EditText mAccountET;
    @BindView(R.id.et_lock)
    EditText mLockET;
    @BindView(R.id.box_auto_login)
    CheckBox mAutoLoginBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_login, R.id.box_auto_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                verify();
                break;
            case R.id.box_auto_login:
                setAuto();
                break;
        }
    }

    /**
     * 账号密码校验
     */
    private void verify() {
        String account = mAccountET.getText().toString();
        if (account.equals("")) {
            ToastUtil.simpleToast(this, R.string.account_null_tip);
            return;
        }

        String lock = mLockET.getText().toString();
        if (lock.equals("")) {
            ToastUtil.simpleToast(this, R.string.lock_null_tip);
            return;
        }

        if (account.equals(ACCOUNT) && lock.equals(LOCK)) {
            startActivity(new Intent(this.getApplicationContext(), MainActivity.class));
            finish();
        } else {
            ToastUtil.simpleToast(this, R.string.account_lock_error);
        }
    }

    /**
     * 设置是否自动登录
     */
    private void setAuto() {
        Log.e(TAG, "setAuto: " + mAutoLoginBox.isSelected());
        SPUtil.putBoolean(this, ConstantsValue.auto_login, mAutoLoginBox.isSelected());
    }
}
