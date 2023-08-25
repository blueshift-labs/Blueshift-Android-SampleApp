package com.blueshift.reads.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.blueshift.BlueshiftLogger;
import com.blueshift.model.UserInfo;
import com.blueshift.reads.R;
import com.blueshift.reads.TestUtils;
import com.blueshift.reads.framework.ReadsApplication;
import com.blueshift.reads.model.Book;
import com.blueshift.rich_push.RichPushConstants;
import com.google.gson.Gson;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";
    private Context mContext;

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
        setContentView(R.layout.activity_splash_screen);

        mContext = this;

        String deepLinkURL = getIntent().getStringExtra(RichPushConstants.EXTRA_DEEP_LINK_URL);
        BlueshiftLogger.d("bundle:deep_link_url = ", deepLinkURL);

        if (TextUtils.isEmpty(deepLinkURL)) {
            Uri uri = getIntent().getData();

            if (uri != null) {
                deepLinkURL = uri.toString();
                BlueshiftLogger.d("data:uri = ", deepLinkURL);
            }
        }

        deepLink(deepLinkURL);
    }

    private void deepLink(String url) {
        if (TextUtils.isEmpty(url)) {
            new LoaderTask().execute();
        } else {
            if (url.endsWith("/checkout")) {
                Intent cartIntent = new Intent(mContext, PlaceOrderActivity.class);
                startActivity(cartIntent);

                finish();
            } else {
                new DeepLinkTask(url).execute();
            }
        }
    }

    private void openApp() {
        boolean isSignedIn = ReadsApplication.isSignedIn(this);
        if (isSignedIn) {
            Intent signInIntent = new Intent(mContext, ProductListActivity.class);
            startActivity(signInIntent);
        } else {
            Intent landingIntent = new Intent(mContext, SignInActivity.class);
            startActivity(landingIntent);
        }
    }

    void openLink(String url) {
        try {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setData(Uri.parse(url));
            startActivity(viewIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DeepLinkTask extends AsyncTask<Void, Void, Book> {

        private String mUrl;

        DeepLinkTask(String url) {
            mUrl = url;
        }

        @Override
        protected Book doInBackground(Void... voids) {
            Book book = null;

            try {
                if (mUrl != null) {
                    String json = TestUtils.readTextFileFromAssets(mContext, "products.json");
                    if (json != null) {
                        Book[] books = new Gson().fromJson(json, Book[].class);

                        for (Book item : books) {
                            if (item != null && item.getWebUrl() != null) {
                                if (mUrl.contains(item.getWebUrl())) {
                                    book = item;

                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {

            }

            return book;
        }

        @Override
        protected void onPostExecute(Book book) {
            if (book != null) {
                Intent pdpIntent = new Intent(mContext, ProductDetailsActivity.class);
                pdpIntent.putExtra(ProductDetailsActivity.EXTRA_BOOK, book);
                pdpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(pdpIntent);
            } else {
                if (mUrl != null) {
                    openLink(mUrl);
                } else {
                    openApp();
                }
            }

            finish();
        }
    }

    private class LoaderTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            openApp();

            finish();
        }
    }
}
