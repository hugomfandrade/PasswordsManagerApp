package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
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

import java.util.List;

public class MainActivity
        extends GenericActivity<MVP.RequiredMainViewOps, MVP.ProvidedMainPresenterOps, MainPresenter>
        implements MVP.RequiredMainViewOps {

    private static final int REQUEST_CODE = 1;

    public static final int MODE_MAIN = 1;
    public static final int MODE_DELETE_EDIT = 2;

    private int viewMode;

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

        viewMode = MODE_MAIN;

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
            case MODE_MAIN:
                getMenuInflater().inflate(R.menu.menu_main, menu);
                Drawable drawable = menu.findItem(R.id.action_add_password).getIcon();
                if (drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                break;
            case MODE_DELETE_EDIT:
                getMenuInflater().inflate(R.menu.menu_main_delete_mode, menu);
                menu.findItem(R.id.action_delete).setVisible(
                        adapterPasswordEntryList.getItemsChecked().size() != 0);
                //Disable edit option for now
                menu.findItem(R.id.action_edit).setVisible(false);

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
                getPresenter().deletePasswordEntryList(
                        adapterPasswordEntryList.getItemsChecked());
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        adapterPasswordEntryList = new PasswordEntryListAdapter(viewMode);
        adapterPasswordEntryList.setOnLongClickListener(new PasswordEntryListAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(PasswordEntry passwordEntry) {
                if (viewMode == MODE_MAIN) {
                    enableDeleteEditMode();
                    adapterPasswordEntryList.setItemChecked(passwordEntry);
                }
                else if (viewMode == MODE_DELETE_EDIT) {
                    adapterPasswordEntryList.setItemChecked(passwordEntry);
                    invalidateOptionsMenu();
                }
            }
        });
        adapterPasswordEntryList.setOnClickListener(new PasswordEntryListAdapter.OnClickListener() {
            @Override
            public void onClick(PasswordEntry passwordEntry) {
                if (viewMode == MODE_DELETE_EDIT) {
                    adapterPasswordEntryList.setItemChecked(passwordEntry);
                    invalidateOptionsMenu();
                }
            }
        });
        rvPasswordEntry = (RecyclerView) findViewById(R.id.rv_password_entry);
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
                        disableDeleteEditMode();
                    }
                }, rvPasswordEntry.getItemAnimator().getMoveDuration());
            }
        });
    }

    private void enableDeleteEditMode() {
        if (viewMode == MODE_MAIN) {
            viewMode = MODE_DELETE_EDIT;

            adapterPasswordEntryList.updateViewMode(viewMode);
            invalidateOptionsMenu();
        }
    }

    private void disableDeleteEditMode() {
        if (viewMode == MODE_DELETE_EDIT) {
            viewMode = MODE_MAIN;

            adapterPasswordEntryList.updateViewMode(viewMode);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void populatePasswordEntries(List<PasswordEntry> passwordEntryList) {
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
            disableDeleteEditMode();
        else
            super.onBackPressed();

    }
}
