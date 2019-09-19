package com.blueshift.reads.activity_backup;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blueshift.Blueshift;
import com.blueshift.reads.ProgressDialogDisplayTask;
import com.blueshift.reads.R;

public class MailingListActivity extends AppCompatActivity {

    private EditText mEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailing_list);

        mEmailField = findViewById(R.id.mailing_list_email_field);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance().trackScreenView(this, true);
    }

    public void onSubscribeClicked(View view) {
        if (mEmailField != null && mEmailField.getText() != null && Patterns.EMAIL_ADDRESS.matcher(mEmailField.getText().toString()).matches()) {
            new ProgressDialogDisplayTask(this).execute();
            Blueshift.getInstance().trackEmailListSubscription(mEmailField.getText().toString(), false);
            mEmailField.setText("");
        } else {
            Toast.makeText(this, "Invalid email.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onUnsubscribeClicked(View view) {
        if (mEmailField != null && mEmailField.getText() != null && Patterns.EMAIL_ADDRESS.matcher(mEmailField.getText().toString()).matches()) {
            new ProgressDialogDisplayTask(this).execute();
            Blueshift.getInstance().trackEmailListUnsubscription(mEmailField.getText().toString(), false);
            mEmailField.setText("");
        } else {
            Toast.makeText(this, "Invalid email.", Toast.LENGTH_SHORT).show();
        }
    }
}
