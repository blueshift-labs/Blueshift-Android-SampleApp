package com.blueshift.reads.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blueshift.Blueshift;
import com.blueshift.LiveContentCallback;
import com.blueshift.reads.R;
import com.github.rahulrvp.android_utils.EditTextUtils;
import com.github.rahulrvp.android_utils.TextViewUtils;

public class LiveContentActivity extends AppCompatActivity {

    private RadioGroup mRadios;
    private TextInputLayout mTILayout;
    private TextView mOutputJsonText;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_content);

        mRadios = (RadioGroup) findViewById(R.id.rg_unique_key);
        mTILayout = (TextInputLayout) findViewById(R.id.slot_input_layout);
        mOutputJsonText = (TextView) findViewById(R.id.output_json_text);
        mProgress = (ProgressBar) findViewById(R.id.live_progress);
    }

    public void onGetLiveContentClick(View view) {
        EditText slotField = null;
        String slotName = null;
        if (mTILayout != null) {
            slotField = mTILayout.getEditText();
            slotName = EditTextUtils.getText(slotField);
        }

        if (TextUtils.isEmpty(slotName)) {
            EditTextUtils.setError(slotField, "Please enter a slot name.");
            return;
        }

        int selectedResId = 0;
        if (mRadios != null) {
            selectedResId = mRadios.getCheckedRadioButtonId();
        }

        Blueshift blueshift = Blueshift.getInstance(this);

        switch (selectedResId) {
            case R.id.rb_email:
                showProgress();
                blueshift.getLiveContentByEmail(slotName, new LiveContentCallback() {
                    @Override
                    public void onReceive(String s) {
                        hideProgress();
                        showResponse(s);
                    }
                });

                break;

            case R.id.rb_customer_id:
                showProgress();
                blueshift.getLiveContentByCustomerId(slotName, new LiveContentCallback() {
                    @Override
                    public void onReceive(String s) {
                        hideProgress();
                        showResponse(s);
                    }
                });

                break;

            case R.id.rb_device_id:
                showProgress();
                blueshift.getLiveContentByDeviceId(slotName, new LiveContentCallback() {
                    @Override
                    public void onReceive(String s) {
                        hideProgress();
                        showResponse(s);
                    }
                });

                break;
        }
    }

    private void showResponse(String s) {
        if (TextUtils.isEmpty(s)) {
            s = "No response received!";
        }

        TextViewUtils.setText(mOutputJsonText, s);
    }

    private void showProgress() {
        if (mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
    }
}
