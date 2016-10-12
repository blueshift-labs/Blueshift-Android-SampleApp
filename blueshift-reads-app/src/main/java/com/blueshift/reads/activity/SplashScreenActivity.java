package com.blueshift.reads.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blueshift.reads.R;
import com.blueshift.reads.model.User;

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
            if (User.getInstance(mContext).isSignedIn()) {
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
