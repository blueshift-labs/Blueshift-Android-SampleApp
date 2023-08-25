package com.blueshift.reads.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blueshift.Blueshift;
import com.blueshift.LiveContentCallback;
import com.blueshift.reads.BuildConfig;
import com.blueshift.reads.LiveContentDialog;
import com.blueshift.reads.R;
import com.github.rahulrvp.android_utils.EditTextUtils;
import com.github.rahulrvp.android_utils.JsonFormatter;
import com.github.rahulrvp.android_utils.TextViewUtils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveContentActivity extends AppCompatActivity {

    private RadioGroup mRadios;
    private TextInputLayout mTILayout;
    private TextView mOutputJsonText;
    private ProgressBar mProgress;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_content);

        mSpinner = findViewById(R.id.slot_spinner);
        mRadios = findViewById(R.id.rg_unique_key);
        mTILayout = findViewById(R.id.slot_input_layout);
        mOutputJsonText = findViewById(R.id.output_json_text);
        mProgress = findViewById(R.id.live_progress);
    }

    public void onGetLiveContentClick(View view) {
        EditText slotField = null;
        String slotName = null;
        if (mTILayout != null) {
            slotField = mTILayout.getEditText();
            slotName = EditTextUtils.getText(slotField);
        }

        if (TextUtils.isEmpty(slotName)) {
            if (mSpinner != null) {
                slotName = (String) mSpinner.getSelectedItem();
            }
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

        List<String> seedItemIds = new ArrayList<>();
        seedItemIds.add("9780307273482");
        seedItemIds.add("9780143115311");
        seedItemIds.add("9780307273345");

        HashMap<String, Object> lcContext = new HashMap<>();
        lcContext.put("seed_item_ids", seedItemIds);

        switch (selectedResId) {
            case R.id.rb_email:
                showProgress();
                blueshift.getLiveContentByEmail(slotName, lcContext, new LiveContentCallback() {
                    @Override
                    public void onReceive(String s) {
                        hideProgress();
                        showResponse(s);
                    }
                });

                break;

            case R.id.rb_customer_id:
                showProgress();
                blueshift.getLiveContentByCustomerId(slotName, lcContext, new LiveContentCallback() {
                    @Override
                    public void onReceive(String s) {
                        hideProgress();
                        showResponse(s);
                    }
                });

                break;

            case R.id.rb_device_id:
                showProgress();
                blueshift.getLiveContentByDeviceId(slotName, lcContext, new LiveContentCallback() {
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
        } else {
            // check for html content
            processHTML(s);

            s = new JsonFormatter.Builder()
                    .setOutputFormat(JsonFormatter.HTML)
                    .build()
                    .format(s);
        }

        TextViewUtils.setText(mOutputJsonText, Html.fromHtml(s));
    }

    private void processHTML(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject json = new JSONObject(response);
                if (json.has("content")) {
                    JSONObject content = json.getJSONObject("content");
                    if (content.has("html_content")) {
                        String html = content.getString("html_content");
                        String position = null;
                        if (content.has("position")) {
                            position = content.getString("position");
                        }

                        LiveContentDialog dialog = LiveContentDialog.getInstance(
                                html, LiveContentDialog.Position.fromString(position));

                        dialog.show(getSupportFragmentManager(), "TAG_POPUP");

                        /*
                        String html = content.getString("html_content");
                        if (!TextUtils.isEmpty(html)) {
                            Intent webViewIntent = new Intent(this, WebViewActivity.class);
                            webViewIntent.putExtra("html", html);

                            startActivity(webViewIntent);
                        }
                        */
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
