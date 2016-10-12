package com.blueshift.reads.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.blueshift.reads.R;
import com.blueshift.reads.TestUtils;
import com.blueshift.reads.adapter.ProductListAdapter;
import com.blueshift.reads.model.Book;
import com.google.gson.Gson;

public class ProductListActivity extends AppCompatActivity {

    private ProductListAdapter mAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mContext = this;

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.product_list);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            mAdapter = new ProductListAdapter();
            recyclerView.setAdapter(mAdapter);
        }

        loadBooks();
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
