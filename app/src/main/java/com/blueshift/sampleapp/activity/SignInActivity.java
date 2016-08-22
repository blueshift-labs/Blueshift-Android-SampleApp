package com.blueshift.sampleapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blueshift.Blueshift;
import com.blueshift.gcm.GCMRegistrar;
import com.blueshift.model.UserInfo;

import com.blueshift.sampleapp.R;


public class SignInActivity extends ActionBarActivity {

    private EditText mEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mEmailField = (EditText) findViewById(R.id.email_field);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this);
    }

    public void onSignInClicked(View view) {
        UserInfo userInfo = UserInfo.getInstance(this);
        userInfo.setRetailerCustomerId("123456");

        if (mEmailField != null && mEmailField.getText() != null) {
            String email = mEmailField.getText().toString();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                userInfo.setEmail(email);
                userInfo.save(this);

                Blueshift.getInstance(this).identifyUserByEmail(email, null);
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
}
