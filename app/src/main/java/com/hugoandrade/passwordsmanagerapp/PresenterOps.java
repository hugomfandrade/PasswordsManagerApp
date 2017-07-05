package com.hugoandrade.passwordsmanagerapp;


public interface PresenterOps <ViewOps> {
    void onCreate(ViewOps view);
    void onResume();
    void onPause();
    void onDestroy(boolean isChangingConfiguration);
    void onConfigurationChange(ViewOps view);
}
