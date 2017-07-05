package com.hugoandrade.passwordsmanagerapp;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoginModel extends DatabaseModel implements MVP.ProvidedLoginModelOps {

    private final static String TAG = LoginModel.class.getSimpleName();

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredLoginPresenterOps> mPresenter;

    @Override
    public void onCreate(MVP.RequiredLoginPresenterOps presenter) {
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
    public void login(String username, String password) {
        super.retrieveAccount(username, password);
    }

    @Override
    protected void onGetAccount(Account account) {
        if (mPresenter != null && mPresenter.get() != null)
            mPresenter.get().onGetAccount(account);
    }
}
