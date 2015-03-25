package ch.bullfin.blueshiftandroidapp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.blueshift.Blueshift;
import com.blueshift.SubscriptionState;

import ch.bullfin.blueshiftandroidapp.ProgressDialogDisplayTask;
import ch.bullfin.blueshiftandroidapp.R;

public class SubscriptionEventsActivity extends ActionBarActivity {

    private Spinner mSubscriptionTypeSpinner;
    private TextView mSubscriptionPeriodType;
    private TextView mSubscriptionPeriodLength;
    private TextView mSubscriptionAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_events);

        mSubscriptionTypeSpinner = (Spinner) findViewById(R.id.subscription_type);
        mSubscriptionPeriodType = (TextView) findViewById(R.id.subscription_period_type);
        mSubscriptionPeriodLength = (TextView) findViewById(R.id.subscription_period_length);
        mSubscriptionAmount = (TextView) findViewById(R.id.subscription_amount);

        mSubscriptionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mSubscriptionPeriodType.setText("Period Type: Yearly");
                        mSubscriptionPeriodLength.setText("Period Length: 5");
                        mSubscriptionAmount.setText("Amount: 20.30");
                        break;

                    case 1:
                        mSubscriptionPeriodType.setText("Period Type: Monthly");
                        mSubscriptionPeriodLength.setText("Period Length: 10");
                        mSubscriptionAmount.setText("Amount: 10.24");
                        break;

                    case 2:
                        mSubscriptionPeriodType.setText("Period Type: Weekly");
                        mSubscriptionPeriodLength.setText("Period Length: 15");
                        mSubscriptionAmount.setText("Amount: 5.43");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this);
    }

    public void onInitSubscriptionClicked(View view) {
        new ProgressDialogDisplayTask(this).execute();

        String cycleLengthStr = mSubscriptionPeriodLength.getText().toString();
        int cycleLength = Integer.valueOf(cycleLengthStr.substring(cycleLengthStr.indexOf(":") + 2));

        String amountStr = mSubscriptionPeriodLength.getText().toString();
        Float amount = Float.valueOf(amountStr.substring(amountStr.indexOf(":") + 2));

        Blueshift.getInstance(this).trackSubscriptionInitialization(SubscriptionState.START,
                mSubscriptionPeriodType.getText().toString(),
                cycleLength,
                mSubscriptionTypeSpinner.getSelectedItem().toString(),
                amount,
                System.currentTimeMillis()
        );
    }

    public void onPauseSubscriptionClicked(View view) {
        Blueshift.getInstance(this).trackSubscriptionPause();
    }

    public void onUnpauseSubscriptionClicked(View view) {
        Blueshift.getInstance(this).trackSubscriptionUnpause();
    }

    public void onCancelSubscriptionClicked(View view) {
        Blueshift.getInstance(this).trackSubscriptionCancel();
    }

}
