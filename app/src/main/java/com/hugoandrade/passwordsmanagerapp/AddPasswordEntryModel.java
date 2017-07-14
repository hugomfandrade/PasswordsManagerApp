package com.hugoandrade.passwordsmanagerapp;

import android.util.Log;

import java.lang.ref.WeakReference;

public class AddPasswordEntryModel extends DatabaseModel implements MVP.ProvidedAddPasswordEntryModelOps {

    private final static String TAG = AddPasswordEntryModel.class.getSimpleName();

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredAddPasswordEntryPresenterOps> mPresenter;

    @Override
    public void onCreate(MVP.RequiredAddPasswordEntryPresenterOps presenter) {
        // Set the WeakReference.
        mPresenter = new WeakReference<>(presenter);

        super.onInitialize(mPresenter.get().getActivityContext());

        open();
    }

    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        if (isChangingConfigurations)
            Log.d(TAG, "just a configuration change - unbindService() not called");
        else
            close();
    }

    @Override
    public void addPasswordEntry(String accountName, String password) {
        super.insertPasswordEntry(accountName, password);
    }

    @Override
    public void editPasswordEntry(PasswordEntry passwordEntry) {
        super.updatePasswordEntryItem(passwordEntry);
    }

    @Override
    protected void onInsertPasswordEntry(PasswordEntry passwordEntry) {
        if (mPresenter != null && mPresenter.get() != null)
            mPresenter.get().onInsertPasswordEntry(passwordEntry);
    }

    @Override
    protected void onUpdatePasswordEntry(PasswordEntry passwordEntry) {
        if (mPresenter != null && mPresenter.get() != null)
            mPresenter.get().onUpdatePasswordEntry(passwordEntry);
    }
}
