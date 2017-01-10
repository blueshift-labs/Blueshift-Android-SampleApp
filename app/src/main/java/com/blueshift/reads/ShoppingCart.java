package com.blueshift.reads;

import android.content.Context;
import android.text.TextUtils;

import com.blueshift.reads.model.Book;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rahul Raveendran V P
 *         Created on 6/1/17 @ 11:49 AM
 *         https://github.com/rahulrvp
 */

public class ShoppingCart {
    private static final String PREF_FILE = "ShoppingCartFile";
    private static final String PREF_KEY = "ShoppingCartKey";

    private static ShoppingCart ourInstance = null;
    private Map<String, Book> mProductsMap;

    private ShoppingCart() {
        mProductsMap = new HashMap<>();
    }

    public static ShoppingCart getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = load(context);
        }

        if (ourInstance == null) {
            ourInstance = new ShoppingCart();
        }

        return ourInstance;
    }

    private static ShoppingCart load(Context context) {
        ShoppingCart instance = null;

        if (context != null) {
            String json = context
                    .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
                    .getString(PREF_KEY, null);

            if (!TextUtils.isEmpty(json)) {
                try {
                    instance = new Gson().fromJson(json, ShoppingCart.class);
                } catch (Exception ignore) {

                }
            }
        }

        return instance;
    }

    public void save(Context context) {
        if (context != null) {
            context
                    .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
                    .edit()
                    .putString(PREF_KEY, new Gson().toJson(this))
                    .apply();
        }
    }

    public void add(Book book) {
        if (book != null) {
            mProductsMap.put(book.getSku(), book);
        }
    }

    public void remove(Book book) {
        if (book != null) {
            mProductsMap.remove(book.getSku());
        }
    }

    public void clear() {
        mProductsMap.clear();
    }

    public List<Book> getBooks() {
        List<Book> products = new ArrayList<>();

        Set<String> keys = mProductsMap.keySet();
        for (String key : keys) {
            products.add(mProductsMap.get(key));
        }

        return products;
    }

    public int increaseQuantity(Book product) {
        int value = 1;

        if (product != null) {
            String sku = product.getSku();

            Book p1 = mProductsMap.get(sku);
            if (p1 != null) {
                value = p1.getQuantity() + 1;
                p1.setQuantity(value);

                mProductsMap.put(sku, p1);
            } else {
                add(product);
            }
        }

        return value;
    }

    public int decreaseQuantity(Book product) {
        int value = 0;

        if (product != null) {
            String sku = product.getSku();
            int qty = product.getQuantity();

            if (qty > 0) {
                Book p1 = mProductsMap.get(sku);
                if (p1 != null) {
                    value = p1.getQuantity() - 1;
                    p1.setQuantity(value);

                    mProductsMap.put(sku, p1);
                } else {
                    value = 1;
                    add(product);
                }
            } else {
                remove(product);
            }
        }

        return value;
    }

    public Double getTotalAmount() {
        Double total = 0.0;

        Set<String> keys = mProductsMap.keySet();
        for (String key : keys) {
            Book book = mProductsMap.get(key);
            Double price = Double.valueOf(book.getPrice());

            total += (price * book.getQuantity());
        }

        return total;
    }

    public int getCount() {
        return mProductsMap.size();
    }
}
