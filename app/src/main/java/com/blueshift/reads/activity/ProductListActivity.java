package com.blueshift.reads.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blueshift.Blueshift;
import com.blueshift.inappmessage.InAppManager;
import com.blueshift.reads.BuildConfig;
import com.blueshift.reads.R;
import com.blueshift.reads.ShoppingCart;
import com.blueshift.reads.TestUtils;
import com.blueshift.reads.adapter.ProductListAdapter;
import com.blueshift.reads.model.Book;
import com.google.gson.Gson;

public class ProductListActivity extends AppCompatActivity {

    private ProductListAdapter mAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
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
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        InAppManager.registerForInAppMessages(this);
        invalidateOptionsMenu();
    }

    @Override
    protected void onStop() {
        InAppManager.unregisterForInAppMessages(this);
        super.onStop();
    }

    private void loadBooks() {
        new AsyncTask<Void, Void, Book[]>() {
            @Override
            protected Book[] doInBackground(Void... params) {
                Book[] books = null;

                String json = TestUtils.readTextFileFromAssets(mContext, "products.json");
                if (!TextUtils.isEmpty(json)) {
                    books = new Gson().fromJson(json, Book[].class);
                }

                return books;
            }

            @Override
            protected void onPostExecute(Book[] books) {
                if (mAdapter != null) {
                    mAdapter.addBooks(books);
                }
            }
        }.execute();
    }
}
