package com.blueshift.reads.activity_backup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.blueshift.Blueshift;
import com.blueshift.reads.R;
import com.blueshift.rich_push.Message;
import com.blueshift.rich_push.RichPushConstants;
import com.bumptech.glide.Glide;

public class OfferDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_display);

        ImageView imageView = (ImageView) findViewById(R.id.offer_image_view);
        Message message = (Message) getIntent().getSerializableExtra(RichPushConstants.EXTRA_MESSAGE);
        if (message != null) {
            Glide
                    .with(this)
                    .load(message.getImageUrl())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(imageView);

            Toast.makeText(OfferDisplayActivity.this, message.getUrl(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this, true);
    }
}
