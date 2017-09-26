package org.hugoandrade.passwordsmanagerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    @SuppressWarnings("unused") private final static String TAG = SharedPreferencesUtils.class.getSimpleName();

    private static final String CODE_SHARED_PREFERENCES_NAME =
            "org.hugoandrade.passwordmanagerapp.CODE_SHARED_PREFERENCES_NAME";
    private static final String CODE_KEY =
            "org.hugoandrade.passwordmanagerapp.CODE_KEY";

    public static String getCode(Context applicationContext) {
        SharedPreferences settings = applicationContext
            .getSharedPreferences(CODE_SHARED_PREFERENCES_NAME, 0);

        return settings.getString(CODE_KEY, null);
    }

    public static void putCode(Context applicationContext, String newPIN) {
        SharedPreferences settings = applicationContext
                .getSharedPreferences(CODE_SHARED_PREFERENCES_NAME, 0);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString(CODE_KEY, newPIN);
        preferencesEditor.apply();
    }

    public static void resetCode(Context applicationContext) {
        SharedPreferences settings = applicationContext
                .getSharedPreferences(CODE_SHARED_PREFERENCES_NAME, 0);
        settings.edit().clear().apply();
    }
}
