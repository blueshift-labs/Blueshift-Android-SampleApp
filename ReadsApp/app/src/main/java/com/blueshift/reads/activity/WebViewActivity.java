package com.blueshift.reads.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.blueshift.reads.R;
import com.blueshift.reads.framework.EdgeToEdgeHelper;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // Setup edge-to-edge display but preserve transparency for this dialog-like activity
        EdgeToEdgeHelper.setupEdgeToEdge(this);
        
        setContentView(R.layout.activity_web_view);
        
        // For dialog-like activities, we don't apply window insets in the same way
        // since we want to maintain the floating appearance

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
