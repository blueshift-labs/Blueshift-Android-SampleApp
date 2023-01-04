package com.blueshift.reads.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blueshift.Blueshift;
import com.blueshift.BlueshiftLogger;
import com.blueshift.inbox.BlueshiftInboxAdapter;
import com.blueshift.inbox.BlueshiftInboxAdapterExtension;
import com.blueshift.inbox.BlueshiftInboxFragment;
import com.blueshift.inbox.BlueshiftInboxMessage;
import com.blueshift.reads.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CustomInboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_inbox);

        Blueshift.getInstance(this).registerForInAppMessages(this);

        BlueshiftLogger.d("BlueshiftInboxActivity", "Activity started");
        if (savedInstanceState == null) {
            BlueshiftInboxFragment fragment = BlueshiftInboxFragment.newInstance();
            fragment.setInboxListItemView(R.layout.custom_inbox_list_item);
            fragment.setInboxDateFormatter(date -> new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date));
            fragment.setInboxAdapterExtension(new BlueshiftInboxAdapterExtension<Object>() {
                @Override
                public int getViewType(@NonNull BlueshiftInboxMessage message) {
                    return 0;
                }

                @Override
                public int getLayoutIdForViewType(int viewType) {
                    return R.layout.bsft_inbox_list_item_custom;
                }

                @Override
                public void onCreateViewHolder(@NonNull BlueshiftInboxAdapter.ViewHolder viewHolder, int viewType) {
                    viewHolder.setTitleTextViewColor(Color.parseColor("#FFFFFF"));
                    viewHolder.setDetailsTextViewColor(Color.parseColor("#FFFFFF"));
                    viewHolder.setDateTextViewColor(Color.parseColor("#FFFFFF"));
                    viewHolder.setUnreadIndicatorColor(Color.parseColor("#00FF00"));

                    int[] colors = new int[]{Color.parseColor("#141e30"), Color.parseColor("#243b55")};
                    GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                    viewHolder.setBackgroundDrawable(drawable);
                }

                @Override
                public Object onCreateViewHolderExtension(@NonNull View itemView, int viewType) {
                    return null;
                }

                @Override
                public void onBindViewHolder(@NonNull BlueshiftInboxAdapter.ViewHolder holder, Object viewHolderExtension, BlueshiftInboxMessage message) {

                }
            });

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(com.blueshift.R.id.inbox_container, fragment)
                    .commit();
        } else {
            BlueshiftLogger.d("BlueshiftInboxActivity", "savedinstance is not null");
        }

    }
}