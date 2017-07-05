package com.hugoandrade.passwordsmanagerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity
        extends GenericActivity<MVP.RequiredSignUpViewOps, MVP.ProvidedSignUpPresenterOps, SignUpPresenter>
        implements MVP.RequiredSignUpViewOps {

    private EditText etUsername, etPassword;
    private TextView tvSignUp;

    public static Intent makeIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        setResult(Activity.RESULT_CANCELED);

        initializeViews();

        super.onCreate(SignUpPresenter.class, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onResume();
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
        tvSignUp = (TextView) findViewById(R.id.tv_sign_up);

        etUsername.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().signUp(
                        etUsername.getText().toString(),
                        etPassword.getText().toString());
            }
        });

        checkInputFields();
        enableInputFields(true);
    }

    private void checkInputFields() {
        if (!etUsername.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
            tvSignUp.setClickable(true);
            //tvAddPasswordEntry.setTextColor(Color.WHITE);
        } else {
            tvSignUp.setClickable(false);
            //tvAddPasswordEntry.setTextColor(Color.parseColor("#3dffffff"));
        }
    }

    @Override
    public void enableInputFields(boolean areEnabled) {
        etPassword.setEnabled(areEnabled);
        etPassword.setEnabled(areEnabled);
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
    public void successfulSignUp(Account account) {
        Intent intent = new Intent();
        intent.putExtra(LoginActivity.EXTRA_SIGN_UP_DATA, account);

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    @Override
    public void reportMessage(String message) {
        Options.showSnackBar(findViewById(R.id.layout_activity_sign_up), message);
    }
}
