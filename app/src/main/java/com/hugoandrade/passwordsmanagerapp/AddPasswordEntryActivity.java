package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class AddPasswordEntryActivity
        extends GenericActivity<MVP.RequiredAddPasswordEntryViewOps,
                                MVP.ProvidedAddPasswordEntryPresenterOps,
                                AddPasswordEntryPresenter>
    implements MVP.RequiredAddPasswordEntryViewOps {

    private static final String PASSWORD_ENTRY = "PasswordEntry";
    private static final int MODE_ADD = 0;
    private static final int MODE_EDIT = 1;

    private EditText etAccountName, etPassword;
    private TextView tvAddPasswordEntry;

    private PasswordEntry passwordEntry;
    private int mode;

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddPasswordEntryActivity.class);
    }

    public static Intent makeIntent(Context context, PasswordEntry passwordEntry) {
        Intent intent = new Intent(context, AddPasswordEntryActivity.class);
        intent.putExtra(PASSWORD_ENTRY, passwordEntry);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_password_entry);

        initializeViews();

        if (getIntent() != null && getIntent().getParcelableExtra(PASSWORD_ENTRY) != null) {
            mode = MODE_EDIT;
            passwordEntry = getIntent().getParcelableExtra(PASSWORD_ENTRY);
            etAccountName.setText(passwordEntry.accountName);
            etAccountName.setSelection(etAccountName.getText().length());
            etPassword.setText(passwordEntry.password);
            tvAddPasswordEntry.setText("Save");
        }
        else {
            mode = MODE_ADD;
        }

        super.onCreate(AddPasswordEntryPresenter.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().onDestroy(isChangingConfigurations());
    }

    private void initializeViews() {
        etAccountName = (EditText) findViewById(R.id.et_account_name);
        etPassword    = (EditText) findViewById(R.id.et_password);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tvAddPasswordEntry.performClick();
                    return true;
                }
                return false;
            }
        });
        tvAddPasswordEntry = (TextView) findViewById(R.id.tv_add_password_entry);

        etAccountName.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);
        tvAddPasswordEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == MODE_ADD) {
                    getPresenter().addPasswordEntry(
                            etAccountName.getText().toString(),
                            etPassword.getText().toString());
                }
                else if (mode == MODE_EDIT) {
                    getPresenter().editPasswordEntry(
                            passwordEntry,
                            etAccountName.getText().toString(),
                            etPassword.getText().toString());
                }
            }
        });

        checkInputFields();
        enableInputFields(true);
    }

    private void checkInputFields() {
        if (!etAccountName.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
            tvAddPasswordEntry.setClickable(true);
            tvAddPasswordEntry.setBackground(null);
        } else {
            tvAddPasswordEntry.setClickable(false);
            tvAddPasswordEntry.setBackgroundColor(Color.parseColor("#3dffffff"));
        }
    }

    @Override
    public void enableInputFields(boolean areEnabled) {
        etAccountName.setEnabled(areEnabled);
        etPassword.setEnabled(areEnabled);
        tvAddPasswordEntry.setEnabled(areEnabled);

        checkInputFields();
    }

    @Override
    public void reportMessage(String message) {
        Options.showSnackBar(findViewById(R.id.layout_activity_add_password_entry), message);
    }

    @Override
    public void successfulAddPasswordEntry() {
        finish();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void afterTextChanged(Editable s) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkInputFields();
        }
    };
}
