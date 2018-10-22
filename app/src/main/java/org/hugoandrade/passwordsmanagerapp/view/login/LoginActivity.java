package org.hugoandrade.passwordsmanagerapp.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

import org.hugoandrade.passwordsmanagerapp.customview.KeyboardView;
import org.hugoandrade.passwordsmanagerapp.utils.OptionUtils;
import org.hugoandrade.passwordsmanagerapp.R;
import org.hugoandrade.passwordsmanagerapp.utils.SharedPreferencesUtils;
import org.hugoandrade.passwordsmanagerapp.view.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    @SuppressWarnings("unused") private final static String TAG = LoginActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 1;

    private KeyboardView keyboardView;
    private PINDisplayListAdapter mPINDisplayAdapter;

    public static Intent makeIntent(Context context) {
        return new Intent(context, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        if (SharedPreferencesUtils.getCode(this) == null)
            reconfigurePIN();
    }

    @SuppressWarnings("ConstantConditions")
    private void initializeUI() {

        setContentView(R.layout.activity_login);

        mPINDisplayAdapter = new PINDisplayListAdapter();
        RecyclerView rvPINDisplay = (RecyclerView) findViewById(R.id.rv_pin_display);
        rvPINDisplay.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false));
        rvPINDisplay.setAdapter(mPINDisplayAdapter);

        keyboardView = (KeyboardView) findViewById(R.id.kb_keyboard);
        keyboardView.setOnKeyboardKeyListener(mOnKeyboardKeyClicked);
    }

    private void enableInputFields(boolean areEnabled) {
        keyboardView.setOnKeyboardKeyListener(areEnabled? mOnKeyboardKeyClicked : null);
    }

    private void successfulLogin() {
        startActivity(MainActivity.makeIntent(this));
        finish();
    }

    private void reconfigurePIN() {
        startActivityForResult(
                ReconfigurePINActivity.makeIntent(LoginActivity.this),
                REQUEST_CODE);
    }

    private void validatePin(final String mPIN) {
        enableInputFields(false);
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String code = SharedPreferencesUtils.getCode(LoginActivity.this);
                if (code == null || !code.equals(mPIN)) {
                    enableInputFields(true);
                    reportMessage("Wrong PIN");
                    mPINDisplayAdapter.reset();
                }
                else {
                    successfulLogin();
                }
            }
        }, 100L);
    }

    private KeyboardView.OnKeyListener mOnKeyboardKeyClicked = new KeyboardView.OnKeyListener() {
        @Override
        public void onKeyClicked(int keyboardKey) {
            switch (keyboardKey){
                case KeyboardView.KEY_BOTTOM_LEFT:
                    reconfigurePIN();
                    break;
                case KeyboardView.KEY_DELETE:
                    mPINDisplayAdapter.delete();
                    break;
                default:
                    mPINDisplayAdapter.add(keyboardKey);
                    String mPIN = mPINDisplayAdapter.getPIN();
                    if (mPIN.length() == 4)
                        validatePin(mPIN);
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mPINDisplayAdapter.reset();
        }
    }

    public void reportMessage(String message) {
        OptionUtils.showSnackBar(findViewById(R.id.layout_activity_login), message);
    }
}
