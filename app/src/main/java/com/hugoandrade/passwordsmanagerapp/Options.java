package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

public class Options {

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a toast message.
     */
    public static void showSnackBar(View view,
                                    String message) {
        Snackbar.make(view,
                message,
                Snackbar.LENGTH_SHORT).show();
    }

    public static float fromDpToPixel(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value * metrics.density;
    }
}
