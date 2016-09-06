package com.blueshift.sampleapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by rahul on 12/3/15.
 */
public class ProgressDialogDisplayTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog mProgressDialog;

    public ProgressDialogDisplayTask(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
