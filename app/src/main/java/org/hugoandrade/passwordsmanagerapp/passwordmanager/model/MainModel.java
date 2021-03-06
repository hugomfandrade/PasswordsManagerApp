package org.hugoandrade.passwordsmanagerapp.passwordmanager.model;

import android.util.Log;

import org.hugoandrade.passwordsmanagerapp.MVP;
import org.hugoandrade.passwordsmanagerapp.passwordmanager.PasswordEntry;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainModel extends DatabaseModel implements MVP.ProvidedMainModelOps {

    private final static String TAG = MainModel.class.getSimpleName();

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredMainPresenterOps> mPresenter;

    @Override
    public void onCreate(MVP.RequiredMainPresenterOps presenter) {
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
    public void getAllPasswordEntries() {
        super.retrieveAllPasswordEntries();
    }

    @Override
    public void deletePasswordEntryList(List<PasswordEntry> passwordEntryList) {
        super.deleteAllPasswordEntries(passwordEntryList);

        //OU
        //for (PasswordEntry e : passwordEntryList)
        //    super.deletePasswordEntry(e);
    }

    @Override
    public void updatePasswordEntryItem(PasswordEntry passwordEntry) {
        super.updatePasswordEntryItem(passwordEntry);
    }

    @Override
    protected void onGetAllPasswordEntries(List<PasswordEntry> passwordEntryList) {
        if (mPresenter != null && mPresenter.get() != null)
            mPresenter.get().onGetAllPasswordEntries(passwordEntryList);
    }

    @Override
    protected void onDeleteAllPasswordEntries(List<PasswordEntry> passwordEntryList) {
        if (mPresenter != null && mPresenter.get() != null)
            mPresenter.get().onDeleteAllPasswordEntries(passwordEntryList);
    }

    @Override
    protected void onDeletePasswordEntry(PasswordEntry passwordEntry) {
        if (mPresenter != null && mPresenter.get() != null)
            mPresenter.get().onDeletePasswordEntry(passwordEntry);
    }
}
