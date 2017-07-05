package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity
        extends GenericActivity<MVP.RequiredMainViewOps, MVP.ProvidedMainPresenterOps, MainPresenter>
        implements MVP.RequiredMainViewOps {

    private static final int REQUEST_CODE = 1;
    private PasswordEntryListAdapter adapterPasswordEntryList;

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeViews();

        super.onCreate(MainPresenter.class, this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        adapterPasswordEntryList = new PasswordEntryListAdapter();
        RecyclerView rvPasswordEntry = (RecyclerView) findViewById(R.id.rv_password_entry);
        rvPasswordEntry.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPasswordEntry.setAdapter(adapterPasswordEntryList);
        ((SimpleItemAnimator) rvPasswordEntry.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void populatePasswordEntries(List<PasswordEntry> passwordEntryList) {
        adapterPasswordEntryList.setAll(passwordEntryList);
    }
}
