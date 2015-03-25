package ch.bullfin.blueshiftandroidapp.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.blueshift.Blueshift;

import ch.bullfin.blueshiftandroidapp.R;

public class DashboardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this);
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
}
