package com.blueshift.reads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.blueshift.Blueshift;
import com.blueshift.reads.R;
import com.blueshift.reads.framework.EdgeToEdgeHelper;
import com.blueshift.reads.framework.ReadsApplication;
import com.github.rahulrvp.android_utils.EditTextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignInActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mCustomerIdField;
    private EditText mEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Setup edge-to-edge display
        EdgeToEdgeHelper.setupEdgeToEdge(this);
        
        setContentView(R.layout.activity_sign_in);
        
        // Apply window insets to root view
        View rootView = findViewById(R.id.activity_sign_in);
        if (rootView != null) {
            EdgeToEdgeHelper.applyWindowInsets(rootView);
        }

        mNameField = findViewById(R.id.name);
        mCustomerIdField = findViewById(R.id.customerId);
        mEmailField = findViewById(R.id.email);

        Blueshift.getInstance(this).trackScreenView(this, false);
    }

    public void onLetMeInClick(View view) {
        String name = EditTextUtils.getText(mNameField);
        String customerId = EditTextUtils.getText(mCustomerIdField);

        String email = EditTextUtils.getText(mEmailField);
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EditTextUtils.setError(mEmailField, "Invalid email address.");

            return;
        }

        ReadsApplication.login(this, email, name, customerId);

        // Go to product list
        Intent productIntent = new Intent(this, ProductListActivity.class);
        startActivity(productIntent);

        finish();
    }

    public void onSkipSignInClick(View view) {
        ReadsApplication.setSignedInStatus(this, true);

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
