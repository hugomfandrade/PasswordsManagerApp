package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    private final static String TAG = SharedPreferencesUtils.class.getSimpleName();


    private static final String LOGIN_SHARED_PREFERENCES_NAME
            = "com.nuada.nuadaapp.LOGIN_SHARED_PREFERENCES_NAME";

    public static void putAccount(Context applicationContext, Account account) {

        SharedPreferences settings = applicationContext
                .getSharedPreferences(LOGIN_SHARED_PREFERENCES_NAME, 0);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString(Account.Entry.COL__ID, account.id);
        preferencesEditor.putString(Account.Entry.COL_USERNAME, account.username);
        preferencesEditor.putString(Account.Entry.COL_PASSWORD, account.password);
        preferencesEditor.apply();
    }

    public static Account getAccount(Context applicationContext) {

        SharedPreferences settings = applicationContext
                .getSharedPreferences(LOGIN_SHARED_PREFERENCES_NAME, 0);

        return new Account(
                settings.getString(Account.Entry.COL__ID, ""),
                settings.getString(Account.Entry.COL_USERNAME, ""),
                settings.getString(Account.Entry.COL_PASSWORD, ""));
    }
}
