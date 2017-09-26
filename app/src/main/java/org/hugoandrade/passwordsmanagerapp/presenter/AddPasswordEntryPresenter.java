package org.hugoandrade.passwordsmanagerapp.presenter;

import android.content.Context;

import org.hugoandrade.passwordsmanagerapp.model.AddPasswordEntryModel;
import org.hugoandrade.passwordsmanagerapp.MVP;
import org.hugoandrade.passwordsmanagerapp.objects.PasswordEntry;

public class AddPasswordEntryPresenter
       extends PresenterBase<MVP.RequiredAddPasswordEntryViewOps,
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
    public void addPasswordEntry(String entryName, String accountName, String password) {
        getView().enableInputFields(false);
        getModel().addPasswordEntry(entryName, accountName, password);
    }

    @Override
    public void editPasswordEntry(PasswordEntry passwordEntry, String entryName, String accountName, String password) {
        getView().enableInputFields(false);
        passwordEntry.entryName = entryName;
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
            getView().successfulAddPasswordEntry(passwordEntry);
    }

    @Override
    public void onUpdatePasswordEntry(PasswordEntry passwordEntry) {
        if (passwordEntry == null) {
            getView().enableInputFields(true);
            getView().reportMessage("Edition failed");
        }
        else
            getView().successfulAddPasswordEntry(passwordEntry);
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
