package com.blueshift.reads.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.blueshift.BlueshiftLinksActivity;
import com.blueshift.reads.R;

public class AppLinksActivity extends BlueshiftLinksActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_links);
    }
}
