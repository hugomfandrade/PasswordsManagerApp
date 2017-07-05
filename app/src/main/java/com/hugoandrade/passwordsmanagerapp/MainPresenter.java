package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;

import java.util.List;

public class MainPresenter
       extends GenericPresenter<MVP.RequiredMainViewOps,
                                MVP.RequiredMainPresenterOps,
                                MVP.ProvidedMainModelOps,
                                MainModel>
       implements MVP.ProvidedMainPresenterOps,
                  MVP.RequiredMainPresenterOps{

    @Override
    public void onCreate(MVP.RequiredMainViewOps view) {
        super.onCreate(view, MainModel.class, this);
    }

    @Override
    public void onResume() {
        getAllPasswordEntries();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredMainViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    private void getAllPasswordEntries() {
        getModel().getAllPasswordEntries(GlobalData.account.id);
    }

    @Override
    public void onGetAllPasswordEntries(List<PasswordEntry> passwordEntryList) {
        getView().populatePasswordEntries(passwordEntryList);
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }

}
