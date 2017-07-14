package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;

import java.util.List;

public class AddPasswordEntryPresenter
       extends GenericPresenter<MVP.RequiredAddPasswordEntryViewOps,
                                MVP.RequiredAddPasswordEntryPresenterOps,
                                MVP.ProvidedAddPasswordEntryModelOps,
                                AddPasswordEntryModel>
       implements MVP.ProvidedAddPasswordEntryPresenterOps,
                  MVP.RequiredAddPasswordEntryPresenterOps{

    @Override
    public void onCreate(MVP.RequiredAddPasswordEntryViewOps view) {
        super.onCreate(view, AddPasswordEntryModel.class, this);
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void onConfigurationChange(MVP.RequiredAddPasswordEntryViewOps view) { }

    @Override
    public void addPasswordEntry(String accountName, String password) {
        getView().enableInputFields(false);
        getModel().addPasswordEntry(accountName, password);
    }

    @Override
    public void editPasswordEntry(PasswordEntry passwordEntry, String accountName, String password) {
        getView().enableInputFields(false);
        passwordEntry.accountName = accountName;
        passwordEntry.password = password;
        getModel().editPasswordEntry(passwordEntry);
    }

    @Override
    public void onInsertPasswordEntry(PasswordEntry passwordEntry) {
        if (passwordEntry == null) {
            getView().enableInputFields(true);
            getView().reportMessage("Addition failed");
        }
        else
            getView().successfulAddPasswordEntry();
    }

    @Override
    public void onUpdatePasswordEntry(PasswordEntry passwordEntry) {
        if (passwordEntry == null) {
            getView().enableInputFields(true);
            getView().reportMessage("Edition failed");
        }
        else
            getView().successfulAddPasswordEntry();
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
