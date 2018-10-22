package org.hugoandrade.passwordsmanagerapp.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.hugoandrade.passwordsmanagerapp.common.TextWatcherAdapter;
import org.hugoandrade.passwordsmanagerapp.presenter.AddPasswordEntryPresenter;
import org.hugoandrade.passwordsmanagerapp.MVP;
import org.hugoandrade.passwordsmanagerapp.utils.OptionUtils;
import org.hugoandrade.passwordsmanagerapp.objects.PasswordEntry;
import org.hugoandrade.passwordsmanagerapp.R;
import org.hugoandrade.passwordsmanagerapp.view.main.MainActivity;

public class AddPasswordEntryActivity

        extends ActivityBase<MVP.RequiredAddPasswordEntryViewOps,
                             MVP.ProvidedAddPasswordEntryPresenterOps,
                             AddPasswordEntryPresenter>

    implements MVP.RequiredAddPasswordEntryViewOps {

    public static final String INTENT_EXTRA_PASSWORD_ENTRY = "intent_extra_password_entry";

    private enum Mode {
        Add,
        Edit
    }

    private Mode mode;

    private EditText etEntryName;
    private EditText etAccountName;
    private EditText etPassword;
    private TextView tvAddPasswordEntry;

    private PasswordEntry passwordEntry;

    private boolean isAccountNameFieldVisible = false;
    private boolean abortAppOnPause = true;

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddPasswordEntryActivity.class);
    }

    public static Intent makeIntent(Context context, PasswordEntry passwordEntry) {
        return makeIntent(context).putExtra(INTENT_EXTRA_PASSWORD_ENTRY, passwordEntry);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        initializeUI();

        readIntent();

        enableInputFields(true);

        super.onCreate(AddPasswordEntryPresenter.class, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_password_entry, menu);

        menu.findItem(R.id.action_add_field).setVisible(!isAccountNameFieldVisible);
        menu.findItem(R.id.action_remove_field).setVisible(isAccountNameFieldVisible);

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        etEntryName   = (EditText) findViewById(R.id.et_entry_name);
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
                String accountName = etAccountName.getText().toString();
                String entryName = etEntryName.getText().toString();
                String password = etPassword.getText().toString();
                if (mode == Mode.Add) {
                    getPresenter().addPasswordEntry(
                            entryName,
                            isAccountNameFieldVisible ? accountName : null,
                            password);
                }
                else if (mode == Mode.Edit) {
                    getPresenter().editPasswordEntry(
                            passwordEntry,
                            entryName,
                            isAccountNameFieldVisible? accountName : null,
                            password);
                }
            }
        });
    }

    private void readIntent() {
        if (getIntent() != null && getIntent().getParcelableExtra(INTENT_EXTRA_PASSWORD_ENTRY) != null) {
            mode = Mode.Edit;
            passwordEntry = getIntent().getParcelableExtra(INTENT_EXTRA_PASSWORD_ENTRY);

            addRemoveAccountNameField(passwordEntry.accountName != null);

            etEntryName.setText(passwordEntry.entryName);
            etEntryName.setSelection(etEntryName.getText().length());
            etAccountName.setText(passwordEntry.accountName);
            etPassword.setText(passwordEntry.password);
            tvAddPasswordEntry.setText("Save");
        }
        else {
            mode = Mode.Add;

            addRemoveAccountNameField(false);
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

        setResult(RESULT_OK, new Intent().putExtra(INTENT_EXTRA_PASSWORD_ENTRY, passwordEntry));
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


    private TextWatcher mTextWatcher = new TextWatcherAdapter() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkInputFields();
        }
    };

    @Override
    public void reportMessage(String message) {
        OptionUtils.showSnackBar(findViewById(R.id.layout_activity_add_password_entry), message);
    }

}
