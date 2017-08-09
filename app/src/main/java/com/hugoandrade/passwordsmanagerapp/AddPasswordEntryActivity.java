package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class AddPasswordEntryActivity
        extends GenericActivity<MVP.RequiredAddPasswordEntryViewOps,
                                MVP.ProvidedAddPasswordEntryPresenterOps,
                                AddPasswordEntryPresenter>
    implements MVP.RequiredAddPasswordEntryViewOps {

    public static final String PASSWORD_ENTRY = "PasswordEntry";
    private static final int MODE_ADD = 0;
    private static final int MODE_EDIT = 1;

    private EditText etEntryName, etAccountName, etPassword;
    private TextView tvAddPasswordEntry;

    private PasswordEntry passwordEntry;

    private boolean isAccountNameFieldVisible = false;
    private int mode;

    private boolean abortAppOnPause = true;

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

        setResult(RESULT_CANCELED);

        initializeUI();

        readIntent();

        enableInputFields(true);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        super.onCreate(AddPasswordEntryPresenter.class, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_password_entry, menu);

        if (isAccountNameFieldVisible) {
            menu.findItem(R.id.action_add_field).setVisible(false);
            menu.findItem(R.id.action_remove_field).setVisible(true);
        } else {
            menu.findItem(R.id.action_add_field).setVisible(true);
            menu.findItem(R.id.action_remove_field).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_field:
            case R.id.action_remove_field: {
                addRemoveAccountNameField(!isAccountNameFieldVisible);
                return true;
            }
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (abortAppOnPause) {
            etEntryName.setVisibility(View.INVISIBLE);
            etAccountName.setVisibility(View.INVISIBLE);
            etPassword.setVisibility(View.INVISIBLE);
            tvAddPasswordEntry.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (abortAppOnPause) {
            setResult(RESULT_CANCELED, new Intent().putExtra(MainActivity.ABORT_APP, true));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().onDestroy(isChangingConfigurations());
    }

    private void addRemoveAccountNameField(boolean isAccountFieldVisible) {
        isAccountNameFieldVisible = isAccountFieldVisible;
        ((View) etAccountName.getParent())
                .setVisibility(isAccountFieldVisible? View.VISIBLE : View.GONE);
        invalidateOptionsMenu();
        checkInputFields();
    }

    private void initializeUI() {
        setContentView(R.layout.activity_add_password_entry);

        etEntryName = (EditText) findViewById(R.id.et_entry_name);
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
        etEntryName.addTextChangedListener(mTextWatcher);
        etAccountName.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);

        tvAddPasswordEntry = (TextView) findViewById(R.id.tv_add_password_entry);
        tvAddPasswordEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == MODE_ADD) {
                    getPresenter().addPasswordEntry(
                            etEntryName.getText().toString(),
                            isAccountNameFieldVisible?
                                    etAccountName.getText().toString()
                                    : null,
                            etPassword.getText().toString());
                }
                else if (mode == MODE_EDIT) {
                    getPresenter().editPasswordEntry(
                            passwordEntry,
                            etEntryName.getText().toString(),
                            isAccountNameFieldVisible?
                                    etAccountName.getText().toString()
                                    : null,
                            etPassword.getText().toString());
                }
            }
        });
    }

    private void readIntent() {
        if (getIntent() != null && getIntent().getParcelableExtra(PASSWORD_ENTRY) != null) {
            mode = MODE_EDIT;
            passwordEntry = getIntent().getParcelableExtra(PASSWORD_ENTRY);

            addRemoveAccountNameField(passwordEntry.accountName != null);

            etEntryName.setText(passwordEntry.entryName);
            etEntryName.setSelection(etEntryName.getText().length());
            etAccountName.setText(passwordEntry.accountName);
            etPassword.setText(passwordEntry.password);
            tvAddPasswordEntry.setText("Save");
        }
        else {
            mode = MODE_ADD;

            addRemoveAccountNameField(false);
            etEntryName.setSelection(etEntryName.getText().length());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        abortAppOnPause = false;
    }

    @Override
    public void enableInputFields(boolean areEnabled) {
        etEntryName.setEnabled(areEnabled);
        etAccountName.setEnabled(areEnabled);
        etPassword.setEnabled(areEnabled);
        tvAddPasswordEntry.setEnabled(areEnabled);

        checkInputFields();
    }

    @Override
    public void successfulAddPasswordEntry(PasswordEntry passwordEntry) {
        abortAppOnPause = false;

        Intent intent = new Intent();
        intent.putExtra(PASSWORD_ENTRY, passwordEntry);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void checkInputFields() {
        if (!etEntryName.getText().toString().isEmpty()
                && !etPassword.getText().toString().isEmpty()
                && (!isAccountNameFieldVisible || !etAccountName.getText().toString().isEmpty())) {
            tvAddPasswordEntry.setClickable(true);
            tvAddPasswordEntry.setBackground(null);
        } else {
            tvAddPasswordEntry.setClickable(false);
            tvAddPasswordEntry.setBackgroundColor(Color.parseColor("#3dffffff"));
        }
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
    public void reportMessage(String message) {
        Options.showSnackBar(findViewById(R.id.layout_activity_add_password_entry), message);
    }

}
