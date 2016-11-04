package com.blueshift.reads.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.blueshift.model.UserInfo;
import com.blueshift.reads.R;

public class SplashScreenActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = this;

        new LoaderTask().execute();
    }

    private class LoaderTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String email = UserInfo.getInstance(mContext).getEmail();
            if (!TextUtils.isEmpty(email)) {
                Intent signInIntent = new Intent(mContext, ProductListActivity.class);
                startActivity(signInIntent);
            } else {
                Intent landingIntent = new Intent(mContext, SignInActivity.class);
                startActivity(landingIntent);
            }

            finish();
        }
    }

}
