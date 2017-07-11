package com.hugoandrade.passwordsmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @SuppressWarnings("unused") private final static String TAG = LoginActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 1;

    private EditText etCode;
    private List<TextView> tvKeyboardKeyList = new ArrayList<>();
    private ImageView ivKeyboardBackspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();

        if (SharedPreferencesUtils.getCode(this) == null)
            startActivityForResult(
                    ReconfigureActivity.makeIntent(LoginActivity.this), REQUEST_CODE);
    }

    @SuppressWarnings("ConstantConditions")
    private void initializeViews() {
        etCode = (EditText) findViewById(R.id.et_code);
        etCode.setText("");
        TextView tvReconfigure = (TextView) findViewById(R.id.tv_reconfigure);

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
        ivKeyboardBackspace = (ImageView) findViewById(R.id.iv_keyboard_backspace);

        for (int i = 0 ; i < tvKeyboardKeyList.size() ; i++)
            tvKeyboardKeyList.get(i).setText(String.valueOf(i));

        for (int i = 0 ; i < tvKeyboardKeyList.size() ; i++)
            tvKeyboardKeyList.get(i).setOnClickListener(mOnKeyboardKeyClicked);

        ivKeyboardBackspace.setOnClickListener(mOnKeyboardKeyClicked);

        assert tvReconfigure != null;
        tvReconfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        ReconfigureActivity.makeIntent(LoginActivity.this), REQUEST_CODE);
            }
        });
    }

    private View.OnClickListener mOnKeyboardKeyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == ivKeyboardBackspace && etCode.getText().length() > 0)
                etCode.setText(TextUtils.substring(etCode.getText(), 0, etCode.getText().length() - 1));

            for (int i = 0 ; i < tvKeyboardKeyList.size() ; i++)
                if (v == tvKeyboardKeyList.get(i))
                    etCode.setText(TextUtils.concat(etCode.getText(), String.valueOf(i)));

            if (etCode.getText().toString().trim().length() == 4) {
                String code = SharedPreferencesUtils.getCode(LoginActivity.this);
                if (code == null || !code.equals(etCode.getText().toString().trim())) {
                    reportMessage("Wrong PIN");
                    etCode.setText("");
                }
                else {
                    successfulLogin();
                }
            }
        }
    };

    public void successfulLogin() {
        startActivity(MainActivity.makeIntent(this));
        finish();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            etCode.setText("");
        }
    }

    public void reportMessage(String message) {
        Options.showSnackBar(findViewById(R.id.layout_activity_login), message);
    }
}
