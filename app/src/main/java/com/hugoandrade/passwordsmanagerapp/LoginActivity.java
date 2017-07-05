package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity
        extends GenericActivity<MVP.RequiredLoginViewOps, MVP.ProvidedLoginPresenterOps, LoginPresenter>
        implements MVP.RequiredLoginViewOps {

    private static final int REQUEST_CODE = 1;
    public static final String EXTRA_SIGN_UP_DATA = "SignUpData";

    private EditText etUsername, etPassword;
    private TextView tvSignUp, tvLogin;

    public static Intent makeIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalData.resetUser();

        setContentView(R.layout.activity_login);

        initializeViews();

        super.onCreate(LoginPresenter.class, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onResume();

        populateInputViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().onDestroy(isChangingConfigurations());
    }

    private void initializeViews() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword   = (EditText) findViewById(R.id.et_password);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        tvSignUp   = (TextView) findViewById(R.id.tv_sign_up);

        etUsername.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().login(
                        etUsername.getText().toString(),
                        etPassword.getText().toString());
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        SignUpActivity.makeIntent(LoginActivity.this), REQUEST_CODE);
            }
        });

        checkInputFields();
        enableInputFields(true);
    }

    private void populateInputViews() {
        Account account = SharedPreferencesUtils.getAccount(getApplicationContext());

        etUsername.setText(account.username);
        etPassword.setText(account.password);
    }

    private void checkInputFields() {
        if (!etUsername.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
            tvLogin.setClickable(true);
            //tvAddPasswordEntry.setTextColor(Color.WHITE);
        } else {
            tvLogin.setClickable(false);
            //tvAddPasswordEntry.setTextColor(Color.parseColor("#3dffffff"));
        }
    }

    @Override
    public void enableInputFields(boolean areEnabled) {
        etPassword.setEnabled(areEnabled);
        etPassword.setEnabled(areEnabled);
        tvLogin.setEnabled(areEnabled);
        tvSignUp.setEnabled(areEnabled);

        checkInputFields();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void afterTextChanged(Editable s) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkInputFields();
        }
    };


    @Override
    public void successfulLogin() {
        SharedPreferencesUtils.putAccount(this, GlobalData.account);
        startActivity(MainActivity.makeIntent(this));
        finish();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                GlobalData.account = data.getParcelableExtra(EXTRA_SIGN_UP_DATA);
                successfulLogin();
            }
        }
    }

    @Override
    public void reportMessage(String message) {
        Options.showSnackBar(findViewById(R.id.layout_activity_login), message);
    }
}
