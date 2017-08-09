package com.hugoandrade.passwordsmanagerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ReconfigurePINActivity extends AppCompatActivity {

    private TextView tvInstructions;

    private KeyboardView keyboardView;
    private PINDisplayListAdapter mPINDisplayAdapter;

    private int configurationStep = 0;
    private String newPIN = "";

    private boolean abortAppOnPause = true;

    public static Intent makeIntent(Context context) {
        return new Intent(context, ReconfigurePINActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(Activity.RESULT_CANCELED);

        initializeUI();

        setUpInitialActivityStep();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (abortAppOnPause) {
            tvInstructions.setVisibility(View.INVISIBLE);
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (abortAppOnPause) {
            finish();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initializeUI() {
        setContentView(R.layout.activity_reconfigure);

        tvInstructions = (TextView) findViewById(R.id.tv_instructions);

        RecyclerView rvPINDisplay = (RecyclerView) findViewById(R.id.rv_pin_display);
        mPINDisplayAdapter = new PINDisplayListAdapter();
        rvPINDisplay.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false));
        rvPINDisplay.setAdapter(mPINDisplayAdapter);

        keyboardView = (KeyboardView) findViewById(R.id.kb_keyboard);
        keyboardView.setOnKeyboardKeyListener(mOnKeyboardKeyClicked);
    }

    private void setUpInitialActivityStep() {
        configurationStep = 0;
        if (SharedPreferencesUtils.getCode(this) == null)
            configurationStep = 1;

        setUpActivityFlow();
    }

    private void setUpActivityFlow() {
        if (configurationStep == 0) {
            tvInstructions.setText("type current PIN");
        }
        else if (configurationStep == 1) {
            tvInstructions.setText("type new 4-digit PIN");
        }
        else if (configurationStep == 2) {
            tvInstructions.setText("confirm new PIN");
        }
        mPINDisplayAdapter.reset();
    }

    private void checkTypedPIN(final String pin) {
        enableInputFields(false);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                enableInputFields(true);
                if (configurationStep == 0) {
                    String code = SharedPreferencesUtils.getCode(ReconfigurePINActivity.this);
                    if (code == null || !code.equals(pin)) {
                        reportMessage("Wrong PIN.");
                        mPINDisplayAdapter.reset();
                    }
                    else {
                        configurationStep++;
                        setUpActivityFlow();
                    }
                }
                else if (configurationStep == 1) {
                    newPIN = pin;
                    configurationStep++;
                    setUpActivityFlow();
                }
                else if (configurationStep == 2) {
                    if (!newPIN.equals(pin)) {
                        reportMessage("Wrong PIN. Type New PIN again");
                        mPINDisplayAdapter.reset();
                        configurationStep = 1;
                        setUpActivityFlow();
                    }
                    else {
                        abortAppOnPause = false;
                        SharedPreferencesUtils.putCode(ReconfigurePINActivity.this, newPIN);
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }
        };
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(runnable, 100L);
    }

    private void enableInputFields(boolean areEnabled) {
        keyboardView.setOnKeyboardKeyListener(areEnabled? mOnKeyboardKeyClicked : null);
    }

    private KeyboardView.OnKeyboardKeyListener mOnKeyboardKeyClicked
            = new KeyboardView.OnKeyboardKeyListener() {
        @Override
        public void onKeyboardKey(int keyboardKey) {
            if (keyboardKey == KeyboardView.KEY_DELETE)
                mPINDisplayAdapter.delete();
            else {
                mPINDisplayAdapter.add(keyboardKey);
                final String mPIN = mPINDisplayAdapter.getPIN();
                if (mPIN.length() == 4) {
                    checkTypedPIN(mPIN);
                }
            }
        }
    };

    public void reportMessage(String message) {
        Options.showSnackBar(findViewById(R.id.layout_activity_reconfigure), message);
    }
}
