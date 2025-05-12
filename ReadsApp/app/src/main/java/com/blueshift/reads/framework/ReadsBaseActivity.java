package com.blueshift.reads.framework;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Rahul Raveendran V P
 * Created on 12/1/17 @ 4:02 PM
 * https://github.com/rahulrvp
 */

public class ReadsBaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    protected Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display for Android 15 compatibility
        EdgeToEdgeHelper.setupEdgeToEdge(this);
    }
    
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        
        // Apply window insets to prevent content from being hidden under system bars
        EdgeToEdgeHelper.applyWindowInsets(view, true);
    }
    
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        
        // Apply window insets to the root view
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            // Get the actual content view (the first child of the content frame)
            if (rootView instanceof ViewGroup && ((ViewGroup) rootView).getChildCount() > 0) {
                View contentView = ((ViewGroup) rootView).getChildAt(0);
                EdgeToEdgeHelper.applyWindowInsets(contentView, true);
            }
        }
    }

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
