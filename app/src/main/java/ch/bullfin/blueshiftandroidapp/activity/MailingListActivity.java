package ch.bullfin.blueshiftandroidapp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blueshift.Blueshift;

import ch.bullfin.blueshiftandroidapp.ProgressDialogDisplayTask;
import ch.bullfin.blueshiftandroidapp.R;

public class MailingListActivity extends ActionBarActivity {

    private EditText mEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailing_list);

        mEmailField = (EditText) findViewById(R.id.mailing_list_email_field);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this);
    }

    public void onSubscribeClicked(View view) {
        if (mEmailField != null && mEmailField.getText() != null && Patterns.EMAIL_ADDRESS.matcher(mEmailField.getText().toString()).matches()) {
            new ProgressDialogDisplayTask(this).execute();
            Blueshift.getInstance(this).trackEmailListSubscription(mEmailField.getText().toString());
            mEmailField.setText("");
        } else {
            Toast.makeText(this, "Invalid email.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onUnsubscribeClicked(View view) {
        if (mEmailField != null && mEmailField.getText() != null && Patterns.EMAIL_ADDRESS.matcher(mEmailField.getText().toString()).matches()) {
            new ProgressDialogDisplayTask(this).execute();
            Blueshift.getInstance(this).trackEmailListUnsubscription(mEmailField.getText().toString());
            mEmailField.setText("");
        } else {
            Toast.makeText(this, "Invalid email.", Toast.LENGTH_SHORT).show();
        }
    }
}
