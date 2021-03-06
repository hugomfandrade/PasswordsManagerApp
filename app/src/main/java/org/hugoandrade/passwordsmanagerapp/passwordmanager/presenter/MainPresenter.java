package org.hugoandrade.passwordsmanagerapp.passwordmanager.presenter;

import android.content.Context;

import org.hugoandrade.passwordsmanagerapp.MVP;
import org.hugoandrade.passwordsmanagerapp.common.PresenterBase;
import org.hugoandrade.passwordsmanagerapp.passwordmanager.model.MainModel;
import org.hugoandrade.passwordsmanagerapp.passwordmanager.PasswordEntry;

import java.util.List;

public class MainPresenter
       extends PresenterBase<MVP.RequiredMainViewOps,
                             MVP.RequiredMainPresenterOps,
                             MVP.ProvidedMainModelOps,
                             MainModel>
       implements MVP.ProvidedMainPresenterOps,
                  MVP.RequiredMainPresenterOps{

    @Override
    public void onCreate(MVP.RequiredMainViewOps view) {
        super.onCreate(view, MainModel.class, this);

        getAllPasswordEntries();
    }

    @Override
    public void onResume() { }

    @Override
    public void onConfigurationChange(MVP.RequiredMainViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void deletePasswordEntryList(List<PasswordEntry> passwordEntryList) {
        getModel().deletePasswordEntryList(passwordEntryList);
    }

    @Override
    public void updatePasswordEntryItem(PasswordEntry passwordEntry) {
        getModel().updatePasswordEntryItem(passwordEntry);
    }

    private void getAllPasswordEntries() {
        getModel().getAllPasswordEntries();
    }

    @Override
    public void onGetAllPasswordEntries(List<PasswordEntry> passwordEntryList) {
        getView().populatePasswordEntries(passwordEntryList);
    }

    @Override
    public void onDeleteAllPasswordEntries(List<PasswordEntry> passwordEntryList) {
        getView().removePasswordEntryListFromListAdapter(passwordEntryList);
    }

    @Override
    public void onDeletePasswordEntry(PasswordEntry passwordEntry) {
        getView().removePasswordEntryFromListAdapter(passwordEntry);
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
