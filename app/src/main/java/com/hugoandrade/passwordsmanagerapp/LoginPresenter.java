package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;

import java.util.List;

public class LoginPresenter
       extends GenericPresenter<MVP.RequiredLoginViewOps,
                                MVP.RequiredLoginPresenterOps,
                                MVP.ProvidedLoginModelOps,
                                LoginModel>
       implements MVP.ProvidedLoginPresenterOps,
                  MVP.RequiredLoginPresenterOps{

    @Override
    public void onCreate(MVP.RequiredLoginViewOps view) {
        super.onCreate(view, LoginModel.class, this);
    }

    @Override
    public void onResume() { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }
    @Override
    public void onConfigurationChange(MVP.RequiredLoginViewOps view) { }

    @Override
    public void login(String username, String password) {
        getView().enableInputFields(false);
        getModel().login(username, password);
    }

    @Override
    public void onGetAccount(Account account) {
        if (account == null) {
            getView().enableInputFields(true);
            getView().reportMessage("Username and Password combination doesn't exist.");
        }
        else {
            GlobalData.account = account;
            getView().successfulLogin();
        }
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
