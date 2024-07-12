package com.blueshift.reads.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blueshift.Blueshift;
import com.blueshift.BlueshiftConstants;
import com.blueshift.BlueshiftExecutor;
import com.blueshift.BlueshiftLinksHandler;
import com.blueshift.BlueshiftLinksListener;
import com.blueshift.BlueshiftLogger;
import com.blueshift.inbox.BlueshiftInboxActivity;
import com.blueshift.inbox.BlueshiftInboxManager;
import com.blueshift.reads.R;
import com.blueshift.reads.ShoppingCart;
import com.blueshift.reads.TestUtils;
import com.blueshift.reads.adapter.ProductListAdapter;
import com.blueshift.reads.advanced.CustomInboxActivity;
import com.blueshift.reads.framework.ReadsApplication;
import com.blueshift.reads.framework.ReadsBaseActivity;
import com.blueshift.reads.model.Book;
import com.google.gson.Gson;

//import com.google.firebase.perf.FirebasePerformance;
//import com.google.firebase.perf.metrics.Trace;

public class ProductListActivity extends ReadsBaseActivity {

    private ProductListAdapter mAdapter;
    private Context mContext;
    private int notificationCount = 0;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshInboxCount();
        }
    };

    private void registerInboxReceiver() {
        BlueshiftInboxManager.registerForInboxBroadcasts(this, mReceiver);
    }

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
        setContentView(R.layout.activity_product_list);

        mContext = this;

        RecyclerView recyclerView = findViewById(R.id.product_list);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            mAdapter = new ProductListAdapter();
            recyclerView.setAdapter(mAdapter);
        }

        loadBooks();

        Blueshift.getInstance(this).trackScreenView(this, false);

        final Uri uri = getIntent() != null ? getIntent().getData() : null;

        new BlueshiftLinksHandler(this).handleBlueshiftUniversalLinks(getIntent(), new BlueshiftLinksListener() {
//            private Trace trace;

            @Override
            public void onLinkProcessingStart() {
                if (isShortURL(uri)) {
//                    trace = FirebasePerformance.getInstance().newTrace("universal_links_replay_url");
//                    trace.start();
                    showProgressDialog("Opening book details...");
                }
            }

            @Override
            public void onLinkProcessingComplete(Uri uri) {
//                if (trace != null) {
//                    trace.stop();
//                }
                hideProgressDialog();

                if (isProductURL(uri)) {
                    Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    showUriDialog(uri);
                }
            }

            @Override
            public void onLinkProcessingError(Exception e, Uri uri) {
//                if (trace != null) {
//                    trace.stop();
//                }
                hideProgressDialog();

                showUriDialog(uri);

                String msg = e != null ? e.getMessage() : "Something went wrong!";
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUriDialog(final Uri uri) {
        if (uri != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Universal Links");
            builder.setMessage("No app path for this URL.\n\n" + uri.toString());
            builder.setPositiveButton("Open in browser", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openLink(uri);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
    }

    private void openLink(Uri link) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(link);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);

            startActivity(intent);
        } catch (Exception e) {
            BlueshiftLogger.e(null, e);
        }
    }

    private boolean isProductURL(Uri uri) {
        if (uri != null) {
            boolean isReads = "www.blueshiftreads.com".equals(uri.getHost());
            String path = uri.getPath();
            return (isReads && path != null && path.startsWith("/products"));
        }

        return false;
    }

    private boolean isShortURL(Uri uri) {
        return uri != null && uri.getPath() != null && uri.getPath().startsWith("/z/");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        ShoppingCart cart = ShoppingCart.getInstance(this);

        MenuItem item = menu.findItem(R.id.menu_cart);
        if (item != null) {
            String string = getString(R.string.cart_0, cart.getCount());
            item.setTitle(string);
        }

        MenuItem sdkV = menu.findItem(R.id.menu_sdk_version);
        if (sdkV != null) {
            String sdkVer = getString(R.string.sdk_version, com.blueshift.BuildConfig.SDK_VERSION);
            sdkV.setTitle(sdkVer);
        }

        MenuItem inbox = menu.findItem(R.id.menu_inbox);
        if (inbox != null) {
            String inboxCount = getString(R.string.inbox_0, notificationCount);
            inbox.setTitle(inboxCount);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_cart) {
            Intent intent = new Intent(this, PlaceOrderActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_live) {
            Intent intent = new Intent(this, LiveContentActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_debug) {
            startActivity(new Intent(this, VerifierActivity.class));
        } else if (item.getItemId() == R.id.menu_inbox) {
            Intent intent = new Intent(this, BlueshiftInboxActivity.class);
            intent.putExtra(BlueshiftConstants.INBOX_ACTIVITY_TITLE, "Mobile Inbox");
            startActivity(intent);
        } else if (item.getItemId() == R.id.custom_inbox) {
            startActivity(new Intent(this, CustomInboxActivity.class));
        } else if (item.getItemId() == R.id.menu_logout) {
            ReadsApplication.logout(this);
            startActivity(new Intent(this, SplashScreenActivity.class));
            finish();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    protected void onPause() {
        Blueshift.getInstance(this).unregisterForInAppMessages(this);
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).registerForInAppMessages(this);
        refreshInboxCount();
        registerInboxReceiver();
    }

    private void refreshInboxCount() {
        BlueshiftInboxManager.getUnreadMessagesCount(this, integer -> {
            notificationCount = integer;
            invalidateOptionsMenu();
        });
    }

    private void loadBooks() {
        BlueshiftExecutor.getInstance().runOnDiskIOThread(
                new Runnable() {
                    @Override
                    public void run() {
                        Book[] books = null;

                        String json = TestUtils.readTextFileFromAssets(mContext, "products.json");
                        if (!TextUtils.isEmpty(json)) {
                            books = new Gson().fromJson(json, Book[].class);
                        }

                        final Book[] finalBooks = books;
                        BlueshiftExecutor.getInstance().runOnMainThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mAdapter != null) {
                                            mAdapter.addBooks(finalBooks);
                                        }
                                    }
                                }
                        );
                    }
                }
        );
    }
}
