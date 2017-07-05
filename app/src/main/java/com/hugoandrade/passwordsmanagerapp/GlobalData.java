package com.hugoandrade.passwordsmanagerapp;

public class GlobalData {

    @SuppressWarnings("unused")
    private static final String TAG = GlobalData.class.getSimpleName();

    public static Account account = new Account("id", "userID", "password");

    public static void resetUser() {
        //account = null;
    }
}
