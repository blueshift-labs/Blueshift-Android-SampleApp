package com.blueshift.sampleapp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.blueshift.Blueshift;
import com.blueshift.rich_push.Message;
import com.blueshift.rich_push.RichPushConstants;
import com.blueshift.sampleapp.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class OfferDisplayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_display);

        ImageView imageView = (ImageView) findViewById(R.id.offer_image_view);
        Message message = (Message) getIntent().getSerializableExtra(RichPushConstants.EXTRA_MESSAGE);
        if (message != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(message.getImage_url(), imageView);

            Toast.makeText(OfferDisplayActivity.this, message.getUrl(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this, true);
    }
}
