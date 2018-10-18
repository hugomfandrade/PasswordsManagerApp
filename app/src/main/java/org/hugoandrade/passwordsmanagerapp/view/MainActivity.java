package org.hugoandrade.passwordsmanagerapp.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.hugoandrade.passwordsmanagerapp.MVP;
import org.hugoandrade.passwordsmanagerapp.presenter.MainPresenter;
import org.hugoandrade.passwordsmanagerapp.objects.PasswordEntry;
import org.hugoandrade.passwordsmanagerapp.R;
import org.hugoandrade.passwordsmanagerapp.view.dialog.SimpleBuilderDialog;
import org.hugoandrade.passwordsmanagerapp.view.listadapter.PasswordEntryListAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity
        extends ActivityBase<MVP.RequiredMainViewOps, MVP.ProvidedMainPresenterOps, MainPresenter>
        implements MVP.RequiredMainViewOps {

    private static final int REQUEST_CODE = 1;

    public static final int MODE_NONE = -1;
    public static final int MODE_DEFAULT = 1;
    public static final int MODE_DELETE_EDIT = 2;
    public static final int MODE_REORDER = 3;

    public static final String ABORT_APP = "AbortApp";

    private int viewMode = 0;

    private TextView tvEmptyMessage;

    private RecyclerView rvPasswordEntry;
    private PasswordEntryListAdapter mPasswordEntryListAdapter;

    private boolean abortAppOnPause = true;

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        initializeUI();

        super.onCreate(MainPresenter.class, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateMode(MODE_DEFAULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (viewMode) {
            case MODE_DEFAULT:
            case MODE_REORDER:
                getMenuInflater().inflate(R.menu.menu_main, menu);
                Drawable drawable = menu.findItem(R.id.action_add_password).getIcon();
                if (drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                break;
            case MODE_DELETE_EDIT:
                getMenuInflater().inflate(R.menu.menu_main_delete_mode, menu);

                menu.findItem(R.id.action_delete).setVisible(
                        mPasswordEntryListAdapter.getSelectedItems().size() != 0);
                menu.findItem(R.id.action_edit).setVisible(
                        mPasswordEntryListAdapter.getSelectedItems().size() == 1);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_password: {
                abortAppOnPause = false;
                startActivityForResult(
                        AddPasswordEntryActivity.makeIntent(MainActivity.this), REQUEST_CODE);
                return true;
            }
            case R.id.action_delete: {
                String title = "Delete Password-Entry";
                String message = "Are you sure you want to delete it?";
                SimpleBuilderDialog builderDialog =
                        new SimpleBuilderDialog(getActivityContext(), title, message);
                builderDialog.setOnDialogResultListener(new SimpleBuilderDialog.OnDialogResult() {
                    @Override
                    public void onResult(DialogInterface dialog, @SimpleBuilderDialog.Result int result) {
                        if (result == SimpleBuilderDialog.YES) {
                            getPresenter().deletePasswordEntryList(
                                    mPasswordEntryListAdapter.getSelectedItems());
                        }
                        else
                            dialog.dismiss();
                    }
                });
                return true;
            }
            case R.id.action_edit: {
                abortAppOnPause = false;
                startActivityForResult(AddPasswordEntryActivity.makeIntent(
                        MainActivity.this,
                        mPasswordEntryListAdapter.getSelectedItems().get(0)), REQUEST_CODE);
                return true;
            }
            case android.R.id.home: {
                updateMode(MODE_DEFAULT);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (abortAppOnPause) {
            finish();
        }
    }

    private void initializeUI() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvEmptyMessage = (TextView) findViewById(R.id.tv_empty_message);
        tvEmptyMessage.setText("Click '+' to add");
        tvEmptyMessage.setVisibility(View.INVISIBLE);

        rvPasswordEntry = (RecyclerView) findViewById(R.id.rv_password_entry);
        rvPasswordEntry.setHasFixedSize(true);
        rvPasswordEntry.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPasswordEntry.getItemAnimator().setChangeDuration(0L);
        rvPasswordEntry.setItemAnimator(new DefaultItemAnimator() {

            @Override
            public void onRemoveFinished(RecyclerView.ViewHolder item) {
                super.onRemoveFinished(item);

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateMode(MODE_DEFAULT);
                    }
                }, rvPasswordEntry.getItemAnimator().getMoveDuration());
            }
        });

        mPasswordEntryListAdapter = new PasswordEntryListAdapter(viewMode);
        mPasswordEntryListAdapter.setOnItemClickListener(new PasswordEntryListAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(PasswordEntry passwordEntry) {
                if (viewMode == MODE_DEFAULT) {
                    mPasswordEntryListAdapter.changeItemSelectionStatus(passwordEntry);
                    updateMode(MODE_DELETE_EDIT);
                }
                else if (viewMode == MODE_DELETE_EDIT) {
                    changeItemSelectionStatus(passwordEntry);
                }
            }

            @Override
            public void onClick(PasswordEntry passwordEntry) {
                if (viewMode == MODE_DELETE_EDIT) {
                    changeItemSelectionStatus(passwordEntry);
                }
            }
        });
        mPasswordEntryListAdapter.setOnItemMoveListener(new PasswordEntryListAdapter.OnItemMoveListener() {
            @Override
            public void onItemMove(List<PasswordEntry> passwordEntryAffectedList) {
                updateMode(MODE_REORDER);
                for (PasswordEntry e : passwordEntryAffectedList)
                    getPresenter().updatePasswordEntryItem(e);
            }
            @Override
            public void onItemDropped() {
                if (viewMode == MODE_REORDER) {
                    updateMode(MODE_DEFAULT);
                }
            }
        });
        rvPasswordEntry.setAdapter(mPasswordEntryListAdapter);
    }

    private void changeItemSelectionStatus(PasswordEntry passwordEntry) {
        mPasswordEntryListAdapter.changeItemSelectionStatus(passwordEntry);
        supportInvalidateOptionsMenu();

        if (mPasswordEntryListAdapter.getSelectedItems().size() == 0)
            updateMode(MODE_DEFAULT);
    }

    private void updateMode(int mode) {
        if (viewMode == mode)
            return;

        viewMode = mode;
        mPasswordEntryListAdapter.updateViewMode(viewMode);
        supportInvalidateOptionsMenu();

        if (viewMode == MODE_DEFAULT)
            mPasswordEntryListAdapter.resetAll();
    }

    @Override
    public void populatePasswordEntries(List<PasswordEntry> passwordEntryList) {
        Collections.sort(passwordEntryList, new Comparator<PasswordEntry>() {
            @Override
            public int compare(PasswordEntry lhs, PasswordEntry rhs) {
                return lhs.order - rhs.order;
            }
        });
        mPasswordEntryListAdapter.setAll(passwordEntryList);
        tvEmptyMessage.setVisibility(mPasswordEntryListAdapter.getItemCount() == 0?
                View.VISIBLE
                : View.INVISIBLE);
    }

    @Override
    public void removePasswordEntryListFromListAdapter(List<PasswordEntry> passwordEntryList) {
        mPasswordEntryListAdapter.removeItems(passwordEntryList);
        tvEmptyMessage.setVisibility(mPasswordEntryListAdapter.getItemCount() == 0?
                View.VISIBLE
                : View.INVISIBLE);
    }

    @Override
    public void removePasswordEntryFromListAdapter(PasswordEntry passwordEntry) {
        mPasswordEntryListAdapter.removeItem(passwordEntry);
        tvEmptyMessage.setVisibility(mPasswordEntryListAdapter.getItemCount() == 0?
                View.VISIBLE
                : View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        abortAppOnPause = true;

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            PasswordEntry passwordEntry
                    = intent.getParcelableExtra(AddPasswordEntryActivity.PASSWORD_ENTRY);
            mPasswordEntryListAdapter.addItem(passwordEntry);
        }
        if (intent != null && intent.getBooleanExtra(ABORT_APP, false)) {
            startActivity(LoginActivity.makeIntent(MainActivity.this));
            finish();
        }
        tvEmptyMessage.setVisibility(
                mPasswordEntryListAdapter.getItemCount() == 0? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (viewMode == MODE_DELETE_EDIT)
            updateMode(MODE_DEFAULT);
        else
            super.onBackPressed();
    }
}