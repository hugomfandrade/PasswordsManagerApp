package com.hugoandrade.passwordsmanagerapp;

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
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity
        extends GenericActivity<MVP.RequiredMainViewOps, MVP.ProvidedMainPresenterOps, MainPresenter>
        implements MVP.RequiredMainViewOps {

    private static final int REQUEST_CODE = 1;

    public static final int MODE_NONE = -1;
    public static final int MODE_DEFAULT = 1;
    public static final int MODE_DELETE_EDIT = 2;
    public static final int MODE_REORDER = 3;

    private int viewMode = 0;

    private RecyclerView rvPasswordEntry;
    private PasswordEntryListAdapter adapterPasswordEntryList;

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeViews();

        enableDefaultMode();

        super.onCreate(MainPresenter.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().onDestroy(isChangingConfigurations());
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
                        adapterPasswordEntryList.getItemsChecked().size() != 0);
                //Disable edit option for now
                menu.findItem(R.id.action_edit).setVisible(
                        adapterPasswordEntryList.getItemsChecked().size() == 1);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_password: {
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
                                    adapterPasswordEntryList.getItemsChecked());
                        }
                        else
                            dialog.dismiss();
                    }
                });
                return true;
            }
            case R.id.action_edit: {
                startActivityForResult(AddPasswordEntryActivity.makeIntent(
                        MainActivity.this,
                        adapterPasswordEntryList.getItemsChecked().get(0)), REQUEST_CODE);
                return true;
            }
            case android.R.id.home: {
                enableDefaultMode();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        rvPasswordEntry = (RecyclerView) findViewById(R.id.rv_password_entry);
        adapterPasswordEntryList = new PasswordEntryListAdapter(viewMode);
        adapterPasswordEntryList.setOnItemClickListener(new PasswordEntryListAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(PasswordEntry passwordEntry) {
                if (viewMode == MODE_DEFAULT) {
                    enableDeleteEditMode();
                    adapterPasswordEntryList.setItemChecked(passwordEntry);
                }
                else if (viewMode == MODE_DELETE_EDIT) {
                    adapterPasswordEntryList.setItemChecked(passwordEntry);
                    //invalidateOptionsMenu();
                    supportInvalidateOptionsMenu();
                }
            }

            @Override
            public void onClick(PasswordEntry passwordEntry) {
                if (viewMode == MODE_DELETE_EDIT) {
                    adapterPasswordEntryList.setItemChecked(passwordEntry);
                    //invalidateOptionsMenu();
                    supportInvalidateOptionsMenu();
                }
            }
        });
        adapterPasswordEntryList.setOnItemMoveListener(new PasswordEntryListAdapter.OnItemMoveListener() {
            @Override
            public void onItemMove(List<PasswordEntry> passwordEntryAffectedList) {
                enableReorderMode();
                for (PasswordEntry e : passwordEntryAffectedList)
                    getPresenter().updatePasswordEntryItem(e);
            }
            @Override
            public void onItemDropped() {
                if (viewMode == MODE_REORDER) {
                    enableDefaultMode();
                }
            }
        });

        rvPasswordEntry.setHasFixedSize(true);
        rvPasswordEntry.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPasswordEntry.setAdapter(adapterPasswordEntryList);
        rvPasswordEntry.setItemAnimator(new DefaultItemAnimator() {

            @Override
            public void onRemoveFinished(RecyclerView.ViewHolder item) {
                super.onRemoveFinished(item);

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableDefaultMode();
                    }
                }, rvPasswordEntry.getItemAnimator().getMoveDuration());
            }
        });
        rvPasswordEntry.getItemAnimator().setChangeDuration(0L);
    }

    private void enableDeleteEditMode() {
        if (viewMode != MODE_DELETE_EDIT) {
            viewMode = MODE_DELETE_EDIT;

            adapterPasswordEntryList.updateViewMode(viewMode);
            //invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
        }
    }

    private void enableDefaultMode() {
        if (viewMode != MODE_DEFAULT) {
            viewMode = MODE_DEFAULT;

            adapterPasswordEntryList.updateViewMode(viewMode);
            //invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
        }
    }

    private void enableReorderMode() {
        if (viewMode != MODE_REORDER) {
            viewMode = MODE_REORDER;

            adapterPasswordEntryList.updateViewMode(viewMode);
            //invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void populatePasswordEntries(List<PasswordEntry> passwordEntryList) {
        Collections.sort(passwordEntryList, new Comparator<PasswordEntry>() {
            @Override
            public int compare(PasswordEntry lhs, PasswordEntry rhs) {
                return lhs.order - rhs.order;
            }
        });
        adapterPasswordEntryList.setAll(passwordEntryList);
    }

    @Override
    public void removePasswordEntryListFromListAdapter(List<PasswordEntry> passwordEntryList) {
        adapterPasswordEntryList.removeItems(passwordEntryList);
    }

    @Override
    public void removePasswordEntryFromListAdapter(PasswordEntry passwordEntry) {
        adapterPasswordEntryList.removeItem(passwordEntry);
    }

    @Override
    public void onBackPressed() {
        if (viewMode == MODE_DELETE_EDIT)
            enableDefaultMode();
        else
            super.onBackPressed();

    }
}
