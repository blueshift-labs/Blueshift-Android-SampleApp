package com.blueshift.reads.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueshift.reads.R;
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
        }
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mBookCover;
        private TextView mBookName;
        private TextView mBookPrice;
        private TextView mBookSku;

        ViewHolder(View itemView) {
            super(itemView);

            mBookCover = (ImageView) itemView.findViewById(R.id.book_cover);
            mBookName = (TextView) itemView.findViewById(R.id.book_name);
            mBookPrice = (TextView) itemView.findViewById(R.id.book_price);
            mBookSku = (TextView) itemView.findViewById(R.id.book_sku);
        }

        public void setValues(Book book) {
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
    }
}
