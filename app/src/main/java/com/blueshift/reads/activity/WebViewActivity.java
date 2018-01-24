package com.blueshift.reads.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blueshift.reads.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.live_content_html);
        if (webView != null) {
            String html = getIntent().getStringExtra("html");
            if (!TextUtils.isEmpty(html)) {
                webView.setWebViewClient(new WebViewClient());
                webView.loadData(html, "text/html; charset=UTF-8", null);
            }
        }
    }

    public void onCloseClick(View view) {
        finish();
    }
}
