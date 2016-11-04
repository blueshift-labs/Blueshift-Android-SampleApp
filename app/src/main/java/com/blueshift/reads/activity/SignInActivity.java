package com.blueshift.reads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blueshift.Blueshift;
import com.blueshift.model.UserInfo;
import com.blueshift.reads.R;
import com.github.rahulrvp.android_utils.EditTextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        // Save user info
        UserInfo currentUser = UserInfo.getInstance(this);
        currentUser.setRetailerCustomerId(hashEmail(email));
        currentUser.setEmail(email);
        currentUser.save(this);

        // Call identify
        Blueshift.getInstance(this).identifyUserByEmail(email, null, false);

        // Go to product list
        Intent productIntent = new Intent(this, ProductListActivity.class);
        startActivity(productIntent);

        finish();
    }

    private String hashEmail(String email) {
        String emailHash = "";

        if (!TextUtils.isEmpty(email)) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(email.getBytes());
                byte[] byteArray = md5.digest();

                StringBuilder sb = new StringBuilder();
                for (byte data : byteArray) {
                    sb.append(Integer.toString((data & 0xff) + 0x100, 16).substring(1));
                }

                emailHash = sb.toString();

                // Log.d("email-md5", emailHash);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return emailHash;
    }
}
