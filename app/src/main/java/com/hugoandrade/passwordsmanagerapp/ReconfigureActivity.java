package com.hugoandrade.passwordsmanagerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReconfigureActivity extends AppCompatActivity {

    private EditText etCode;
    private TextView tvInstructions;
    private List<TextView> tvKeyboardKeyList = new ArrayList<>();

    private int configurationStep = 0;
    private String newPIN = "";

    public static Intent makeIntent(Context context) {
        return new Intent(context, ReconfigureActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reconfigure);

        setResult(Activity.RESULT_CANCELED);

        initializeViews();

        setUpInitialActivityStep();
        setUpActivityFlow();
    }

    private void setUpInitialActivityStep() {
        configurationStep = 0;
        if (SharedPreferencesUtils.getCode(this) == null)
            configurationStep = 1;
    }

    private void setUpActivityFlow() {
        if (configurationStep == 0) {
            tvInstructions.setText("Type current PIN");
        }
        else if (configurationStep == 1) {
            tvInstructions.setText("Type new PIN");
        }
        else if (configurationStep == 2) {
            tvInstructions.setText("Confirm new PIN");
        }
        etCode.setText("");
    }

    private void initializeViews() {
        etCode = (EditText) findViewById(R.id.et_code);
        etCode.setText("");
        tvInstructions = (TextView) findViewById(R.id.tv_instructions);
        tvInstructions.setText("");

        tvKeyboardKeyList.clear();
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_0).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_1).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_2).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_3).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_4).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_5).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_6).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_7).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_8).findViewById(R.id.tv_keyboard));
        tvKeyboardKeyList.add((TextView) findViewById(R.id.layout_keyboard_key_9).findViewById(R.id.tv_keyboard));

        for (int i = 0 ; i < tvKeyboardKeyList.size() ; i++)
            tvKeyboardKeyList.get(i).setText(String.valueOf(i));

        for (int i = 0 ; i < tvKeyboardKeyList.size() ; i++)
            tvKeyboardKeyList.get(i).setOnClickListener(mOnKeyboardKeyClicked);
    }

    private View.OnClickListener mOnKeyboardKeyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0 ; i < tvKeyboardKeyList.size() ; i++)
                if (v == tvKeyboardKeyList.get(i))
                    etCode.setText(TextUtils.concat(etCode.getText(), String.valueOf(i)));

            if (etCode.getText().toString().trim().length() == 4) {
                checkTypedPIN();

            }
        }
    };

    private void checkTypedPIN() {
        if (configurationStep == 0) {
            String code = SharedPreferencesUtils.getCode(ReconfigureActivity.this);
            if (code == null || !code.equals(etCode.getText().toString().trim())) {
                reportMessage("Wrong PIN.");
                etCode.setText("");
            }
            else {
                configurationStep++;
                setUpActivityFlow();
            }
        }
        else if (configurationStep == 1) {
            newPIN = etCode.getText().toString().trim();
            configurationStep++;
            setUpActivityFlow();
        }
        else if (configurationStep == 2) {
            if (!newPIN.equals(etCode.getText().toString().trim())) {
                reportMessage("Wrong PIN. Type New PIN again");
                newPIN = "";
                configurationStep = 1;
                setUpActivityFlow();
            }
            else {
                SharedPreferencesUtils.putCode(ReconfigureActivity.this, newPIN);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public void reportMessage(String message) {
        Options.showSnackBar(findViewById(R.id.layout_activity_reconfigurate), message);
    }
}
