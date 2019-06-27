package com.blueshift.reads.framework;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Rahul Raveendran V P
 * Created on 12/1/17 @ 4:02 PM
 * https://github.com/rahulrvp
 */


public class ReadsBaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onStop() {
        super.onStop();

        hideProgressDialog();
    }

    protected void showProgressDialog(int resId) {
        showProgressDialog(getString(resId));
    }

    protected void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }

        progressDialog.setMessage(message);
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
