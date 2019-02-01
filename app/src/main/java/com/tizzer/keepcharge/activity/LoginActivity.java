package com.tizzer.keepcharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.tizzer.keepcharge.R;
import com.tizzer.keepcharge.constant.ConstantsValue;
import com.tizzer.keepcharge.util.MD5Util;
import com.tizzer.keepcharge.util.SharedPreferencesUtil;
import com.tizzer.keepcharge.util.ToastUtil;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {
    //桐乡佰忆身份信息
    private static final String ACCOUNT = "txby";
    private static final String PASSWORD = "txby123";

    private EditText mAccountET;
    private EditText mPasswordET;
    private CheckBox mAutoLoginCB;

    //是否自动登录
    private boolean autoLoginTag = false;
    //记录是否自动登录
    private boolean autoLoginTagTemp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        autoLoginTagTemp = SharedPreferencesUtil.getBoolean(this, ConstantsValue.AUTO_LOGIN);
        autoLoginTag = autoLoginTagTemp;
        if (autoLoginTagTemp) {
            String account = SharedPreferencesUtil.getString(this, ConstantsValue.ACCOUNT);
            String md5Password = SharedPreferencesUtil.getString(this, ConstantsValue.PASSWORD);
            if ((ACCOUNT + MD5Util.md5(PASSWORD)).equals(account + md5Password)) {
                skipLogin();
            } else {
                ToastUtil.simpleToast(this, R.string.auth_info_error);
            }
        }
        mAccountET = findViewById(R.id.et_account);
        mPasswordET = findViewById(R.id.et_password);
        mAutoLoginCB = findViewById(R.id.box_auto_login);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.box_auto_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                verify();
                break;
            case R.id.box_auto_login:
                autoLoginTag = mAutoLoginCB.isChecked();
                break;
        }
    }

    /**
     * 身份校验
     */
    private void verify() {
        String account = mAccountET.getText().toString();
        if (account.equals("")) {
            ToastUtil.simpleToast(this, R.string.account_null_tip);
            return;
        }

        String password = mPasswordET.getText().toString();
        if (password.equals("")) {
            ToastUtil.simpleToast(this, R.string.lock_null_tip);
            return;
        }

        if (account.equals(ACCOUNT) && password.equals(PASSWORD)) {
            setAuto(account, password);
            skipLogin();
        } else {
            ToastUtil.simpleToast(this, R.string.account_lock_error);
        }
    }

    /**
     * 跳过登录
     */
    private void skipLogin() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * 设置是否自动登录
     */
    private void setAuto(String account, String password) {
        if (autoLoginTag == autoLoginTagTemp) {
            return;
        }
        if (autoLoginTag) {
            SharedPreferencesUtil.getInstance(this).edit()
                    .putString(ConstantsValue.ACCOUNT, account)
                    .putString(ConstantsValue.PASSWORD, MD5Util.md5(password))
                    .putBoolean(ConstantsValue.AUTO_LOGIN, true)
                    .apply();
        } else {
            SharedPreferencesUtil.getInstance(this).edit()
                    .putString(ConstantsValue.ACCOUNT, null)
                    .putString(ConstantsValue.PASSWORD, null)
                    .putBoolean(ConstantsValue.AUTO_LOGIN, false)
                    .apply();
        }
    }
}
