package org.hugoandrade.passwordsmanagerapp.common;


public interface PresenterOps <ViewOps> {
    void onCreate(ViewOps view);
    void onResume();
    void onPause();
    void onDestroy(boolean isChangingConfiguration);
    void onConfigurationChange(ViewOps view);
}
