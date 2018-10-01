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
import com.tizzer.keepcharge.util.MD5Util;
import com.tizzer.keepcharge.util.SharedPreferencesUtil;
import com.tizzer.keepcharge.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String ACCOUNT = "txby";
    private static final String PASSWORD = "txby123";

    @BindView(R.id.et_account)
    EditText mAccountET;
    @BindView(R.id.et_lock)
    EditText mLockET;
    @BindView(R.id.box_auto_login)
    CheckBox mAutoLoginBox;

    private boolean isAuto = false;
    private boolean isAutoTemp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        isAutoTemp = SharedPreferencesUtil.getBoolean(this, ConstantsValue.AUTO_LOGIN);
        isAuto = isAutoTemp;
        if (isAutoTemp) {
            String account = SharedPreferencesUtil.getString(this, ConstantsValue.ACCOUNT);
            String md5Password = SharedPreferencesUtil.getString(this, ConstantsValue.PASSWORD);
            Log.e(TAG, "onCreate: " + ACCOUNT + "~" + MD5Util.md5(PASSWORD));
            Log.e(TAG, "onCreate: " + account + "~" + md5Password);
            if ((ACCOUNT + MD5Util.md5(PASSWORD)).equals(account + md5Password)) {
                skip();
            } else {
                ToastUtil.simpleToast(this, R.string.auth_info_error);
            }
        }
    }

    @OnClick({R.id.btn_login, R.id.box_auto_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                verify();
                break;
            case R.id.box_auto_login:
                isAuto = mAutoLoginBox.isChecked();
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

        String password = mLockET.getText().toString();
        if (password.equals("")) {
            ToastUtil.simpleToast(this, R.string.lock_null_tip);
            return;
        }

        if (account.equals(ACCOUNT) && password.equals(PASSWORD)) {
            setAuto(account, password);
            skip();
        } else {
            ToastUtil.simpleToast(this, R.string.account_lock_error);
        }
    }

    private void skip() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * 设置是否自动登录
     */
    private void setAuto(String account, String password) {
        if (isAuto == isAutoTemp) {
            return;
        }
        if (isAuto) {
            Log.e(TAG, "setAuto: do it true");
            SharedPreferencesUtil.putString(this, ConstantsValue.ACCOUNT, account);
            SharedPreferencesUtil.putString(this, ConstantsValue.PASSWORD, MD5Util.md5(password));
            SharedPreferencesUtil.putBoolean(this, ConstantsValue.AUTO_LOGIN, true);
        } else {
            Log.e(TAG, "setAuto: do it false");
            SharedPreferencesUtil.putString(this, ConstantsValue.ACCOUNT, null);
            SharedPreferencesUtil.putString(this, ConstantsValue.PASSWORD, null);
            SharedPreferencesUtil.putBoolean(this, ConstantsValue.AUTO_LOGIN, false);
        }
    }
}
