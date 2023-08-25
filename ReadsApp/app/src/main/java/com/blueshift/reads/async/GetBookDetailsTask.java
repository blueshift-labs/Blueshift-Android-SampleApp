package com.blueshift.reads.async;

import android.content.Context;
import android.os.AsyncTask;

import com.blueshift.reads.TestUtils;
import com.blueshift.reads.model.Book;
import com.google.gson.Gson;

/**
 * @author Rahul Raveendran V P
 * Created on 12/1/17 @ 3:42 PM
 * https://github.com/rahulrvp
 */


public class GetBookDetailsTask extends AsyncTask<Void, Void, Book> {

    private final Context mContext;
    private Callback mCallback;
    private String mSku;
    private String mWebURL;

    public GetBookDetailsTask(Context context) {
        mContext = context;
    }

    public GetBookDetailsTask setCallback(Callback callback) {
        mCallback = callback;

        return this;
    }

    public GetBookDetailsTask setSku(String sku) {
        mSku = sku;

        return this;
    }

    public GetBookDetailsTask setURL(String webURL) {
        mWebURL = webURL;

        return this;
    }

    @Override
    protected void onPreExecute() {
        if (mCallback != null) {
            mCallback.onTaskStart();
        }
    }

    @Override
    protected Book doInBackground(Void... voids) {
        Book book = null;

        try {
            String json = TestUtils.readTextFileFromAssets(mContext, "products.json");
            if (json != null) {
                Book[] books = new Gson().fromJson(json, Book[].class);

                for (Book item : books) {
                    if (item != null && item.getWebUrl() != null) {
                        if (item.getSku().equals(mSku) || item.getWebUrl().equals(mWebURL)) {
                            book = item;

                            break;
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
        if (mCallback != null) {
            mCallback.onTaskComplete(book);
        }
    }

    public interface Callback {
        void onTaskStart();

        void onTaskComplete(Book book);
    }
}
