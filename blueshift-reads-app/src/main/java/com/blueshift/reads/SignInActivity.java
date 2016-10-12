package com.blueshift.reads;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blueshift.Blueshift;

public class SignInActivity extends AppCompatActivity {

    private EditText mEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mEmailField = (EditText) findViewById(R.id.email);
    }

    public void onLetMeInClick(View view) {
        String email;

        if (mEmailField != null) {
            email = mEmailField.getText().toString();

            if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailField.setError("Invalid email address.");

                return;
            }

            Blueshift.getInstance(this).identifyUserByEmail(email, null, false);
        }
    }
}
