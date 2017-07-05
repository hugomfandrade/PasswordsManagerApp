package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddPasswordEntryActivity
        extends GenericActivity<MVP.RequiredAddPasswordEntryViewOps,
                                MVP.ProvidedAddPasswordEntryPresenterOps,
                                AddPasswordEntryPresenter>
    implements MVP.RequiredAddPasswordEntryViewOps {

    private static final int REQUEST_CODE = 1;

    private EditText etAccountName, etPassword;
    private TextView tvAddPasswordEntry;

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddPasswordEntryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_password_entry);

        initializeViews();

        super.onCreate(AddPasswordEntryPresenter.class, this);
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
        etAccountName = (EditText) findViewById(R.id.et_account_name);
        etPassword   = (EditText) findViewById(R.id.et_password);
        tvAddPasswordEntry = (TextView) findViewById(R.id.tv_add_password_entry);

        etAccountName.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);
        tvAddPasswordEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().addPasswordEntry(
                        etAccountName.getText().toString(),
                        etPassword.getText().toString());
            }
        });

        checkInputFields();
        enableInputFields(true);
    }

    private void checkInputFields() {
        if (!etAccountName.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
            tvAddPasswordEntry.setClickable(true);
            //tvAddPasswordEntry.setTextColor(Color.WHITE);
        } else {
            tvAddPasswordEntry.setClickable(false);
            //tvAddPasswordEntry.setTextColor(Color.parseColor("#3dffffff"));
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
