package com.blueshift.sampleapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blueshift.Blueshift;
import com.blueshift.gcm.GCMRegistrar;
import com.blueshift.model.UserInfo;
import com.blueshift.sampleapp.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SignInActivity extends ActionBarActivity {

    private EditText mEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mEmailField = (EditText) findViewById(R.id.email_field);

        String email = UserInfo.getInstance(this).getEmail();
        if (!TextUtils.isEmpty(email)) {
            Intent dashboardIntent = new Intent(this, DashboardActivity.class);
            startActivity(dashboardIntent);

            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this, true);
    }

    public void onSignInClicked(View view) {
        UserInfo userInfo = UserInfo.getInstance(this);

        if (mEmailField != null && mEmailField.getText() != null) {
            String email = mEmailField.getText().toString();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                userInfo.setRetailerCustomerId(hashEmail(email));
                userInfo.setEmail(email);
                userInfo.save(this);

                Blueshift.getInstance(this).identifyUserByEmail(email, null, false);
                startActivity(new Intent(this, DashboardActivity.class));
            } else {
                mEmailField.setError("Invalid email");
            }
        }

    }

    public void onGetGCMRegIdClicked(View view) {
        String regId = GCMRegistrar.getRegistrationId(this);
        if (regId == null || regId.isEmpty()) {
            Toast.makeText(this, "Registration is not complete. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"user@example.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "GCM Registration Id");
            intent.putExtra(Intent.EXTRA_TEXT, regId);
            startActivity(intent);
        }
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
