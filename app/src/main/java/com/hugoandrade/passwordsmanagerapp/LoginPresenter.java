package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;

import java.util.List;

public class LoginPresenter
       extends GenericPresenter<MVP.RequiredLoginViewOps,
                                MVP.RequiredLoginPresenterOps,
                                MVP.ProvidedLoginModelOps,
                                LoginModel>
       implements MVP.ProvidedLoginPresenterOps,
                  MVP.RequiredLoginPresenterOps {

    @Override
    public void onCreate(MVP.RequiredLoginViewOps view) {
        super.onCreate(view, LoginModel.class, this);
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onConfigurationChange(MVP.RequiredLoginViewOps view) {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void resetDatabase() {
        getView().enableInputFields(false);
        getModel().resetDatabase();
    }

    @Override
    public void onResetDatabase(boolean wasSuccessfullyDeleted) {
        if (!wasSuccessfullyDeleted) {
            getView().enableInputFields(true);
            getView().reportMessage("Reset failed");
        }
        else
            getView().successfulReset();
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

