package com.blueshift.reads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blueshift.Blueshift;
import com.blueshift.reads.R;
import com.blueshift.reads.model.User;

import io.github.rahulrvp.android_utils.EditTextUtils;

public class SignInActivity extends AppCompatActivity {

    private EditText mEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mEmailField = (EditText) findViewById(R.id.email);

        Blueshift.getInstance(this).trackScreenView(this, false);
    }

    public void onLetMeInClick(View view) {
        String email = EditTextUtils.getText(mEmailField);
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EditTextUtils.setError(mEmailField, "Invalid email address.");

            return;
        }

        Blueshift.getInstance(this).identifyUserByEmail(email, null, false);

        User currentUser = User.getInstance(this);
        currentUser.setEmail(email);
        currentUser.save(this);

        Intent productIntent = new Intent(this, ProductListActivity.class);
        startActivity(productIntent);

        finish();
    }
}
