package com.blueshift.reads.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueshift.reads.R;
import com.blueshift.reads.ShoppingCart;
import com.blueshift.reads.model.Book;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderActivity extends AppCompatActivity {

    private CartProductsAdapter mRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        RecyclerView productRView = (RecyclerView) findViewById(R.id.cart_product_list);
        productRView.setLayoutManager(new LinearLayoutManager(this));
        productRView.setHasFixedSize(true);

        mRVAdapter = new CartProductsAdapter();
        productRView.setAdapter(mRVAdapter);
    }

    private void showCartItems() {
        ShoppingCart shoppingCart = ShoppingCart.getInstance(this);
        shoppingCart.getBooks();
    }

    private class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.ViewHolder> {

        private final List<Book> mBooks = new ArrayList<>();

        public void setBooks(List<Book> books) {
            mBooks.addAll(books);

            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_book_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
