package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;

public class SignUpPresenter
       extends GenericPresenter<MVP.RequiredSignUpViewOps,
                                MVP.RequiredSignUpPresenterOps,
                                MVP.ProvidedSignUpModelOps,
                                SignUpModel>
       implements MVP.ProvidedSignUpPresenterOps,
                  MVP.RequiredSignUpPresenterOps{

    @Override
    public void onCreate(MVP.RequiredSignUpViewOps view) {
        super.onCreate(view, SignUpModel.class, this);
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }
    @Override
    public void onConfigurationChange(MVP.RequiredSignUpViewOps view) {
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }

    @Override
    public void signUp(String username, String password) {
        getView().enableInputFields(false);
        getModel().signUp(username, password);
    }

    @Override
    public void onInsertAccount(Account account) {
        if (account == null) {
            getView().enableInputFields(true);
            getView().reportMessage("Sign up failed.");
        }
        else
            getView().successfulSignUp(account);
    }
}
