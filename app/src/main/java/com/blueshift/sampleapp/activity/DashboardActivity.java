package com.blueshift.sampleapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.blueshift.Blueshift;

import com.blueshift.model.UserInfo;
import com.blueshift.sampleapp.R;

public class DashboardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this,true);
    }

    public void onProductsClicked(View view) {
        startActivity(new Intent(this, ProductListActivity.class));
    }

    public void onPurchaseReturnAndCancelClicked(View view) {
        startActivity(new Intent(this, PurchaseReturnActivity.class));
    }

    public void onEmailListSubscriptionClicked(View view) {
        startActivity(new Intent(this, MailingListActivity.class));
    }

    public void onSubscriptionEventsClicked(View view) {
        startActivity(new Intent(this, SubscriptionEventsActivity.class));
    }

    public void OnLogoutClicked(View view) {
        UserInfo userInfo = UserInfo.getInstance(this);
        userInfo.setEmail(null);
        userInfo.save(this);

        Intent signInIntent = new Intent(this, SignInActivity.class);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(signInIntent);
    }
}
