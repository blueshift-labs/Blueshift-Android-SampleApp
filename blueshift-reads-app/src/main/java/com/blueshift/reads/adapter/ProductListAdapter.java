package com.blueshift.reads.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueshift.reads.R;
import com.blueshift.reads.activity.ProductDetailsActivity;
import com.blueshift.reads.model.Book;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Rahul Raveendran V P
 *         Created on 12/10/16 @ 2:31 PM
 *         https://github.com/rahulrvp
 */


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private final ArrayList<Book> mBooks = new ArrayList<>();

    public void addBooks(Book[] books) {
        mBooks.clear();

        if (books != null) {
            Collections.addAll(mBooks, books);
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null) {
            Book book = mBooks.get(position);
            holder.setValues(book);
            holder.setOnClickListener(book);
        }
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View mContentView;
        private final ImageView mBookCover;
        private final TextView mBookName;
        private final TextView mBookPrice;
        private final TextView mBookSku;

        ViewHolder(View itemView) {
            super(itemView);

            mContentView = itemView;
            mBookCover = (ImageView) itemView.findViewById(R.id.book_cover);
            mBookName = (TextView) itemView.findViewById(R.id.book_name);
            mBookPrice = (TextView) itemView.findViewById(R.id.book_price);
            mBookSku = (TextView) itemView.findViewById(R.id.book_sku);
        }

        void setValues(Book book) {
            if (book != null) {
                if (mBookCover != null) {
                    Glide
                            .with(mBookCover.getContext())
                            .load(book.getImageUrl())
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .crossFade()
                            .into(mBookCover);
                }

                if (mBookName != null) {
                    mBookName.setText(book.getName());
                }

                if (mBookPrice != null) {
                    mBookPrice.setText(book.getPrice());
                }

                if (mBookSku != null) {
                    mBookSku.setText(book.getSku());
                }
            }
        }

        void setOnClickListener(final Book book) {
            if (mContentView != null) {
                mContentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = mContentView.getContext();
                        launchPDP(context, book);
                    }
                });
            }
        }

        private void launchPDP(Context context, Book book) {
            Intent pdpIntent = new Intent(context, ProductDetailsActivity.class);
            pdpIntent.putExtra(ProductDetailsActivity.EXTRA_BOOK, book);
            context.startActivity(pdpIntent);
        }
    }
}
