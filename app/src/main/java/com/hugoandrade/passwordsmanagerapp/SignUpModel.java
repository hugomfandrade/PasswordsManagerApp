package com.hugoandrade.passwordsmanagerapp;

import android.util.Log;

import java.lang.ref.WeakReference;

public class SignUpModel extends DatabaseModel implements MVP.ProvidedSignUpModelOps {

    private final static String TAG = SignUpModel.class.getSimpleName();

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredSignUpPresenterOps> mPresenter;

    @Override
    public void onCreate(MVP.RequiredSignUpPresenterOps presenter) {
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
    public void signUp(String username, String password) {
        super.insertAccount(username, password);
    }

    @Override
    protected void onInsertAccount(Account account) {
        if (mPresenter != null && mPresenter.get() != null)
            mPresenter.get().onInsertAccount(account);
    }
}
