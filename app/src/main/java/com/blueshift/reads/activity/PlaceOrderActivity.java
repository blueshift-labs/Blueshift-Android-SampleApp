package com.blueshift.reads.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blueshift.reads.R;
import com.blueshift.reads.ShoppingCart;
import com.blueshift.reads.model.Book;
import com.github.rahulrvp.android_utils.TextViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaceOrderActivity extends AppCompatActivity {

    private CartProductsAdapter mRVAdapter;
    private ShoppingCart mCart;
    private TextView mTotalNoTax;
    private TextView mTotalWithTax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        setTitle(R.string.review_products);

        mTotalNoTax = (TextView) findViewById(R.id.total_excl_tax);
        mTotalWithTax = (TextView) findViewById(R.id.total_with_tax);

        RecyclerView productRView = (RecyclerView) findViewById(R.id.cart_product_list);
        productRView.setLayoutManager(new LinearLayoutManager(this));

        mRVAdapter = new CartProductsAdapter();
        productRView.setAdapter(mRVAdapter);

        mCart = ShoppingCart.getInstance(this);
        mRVAdapter.setBooks(mCart.getBooks());

        updateSummaryView();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ShoppingCart.getInstance(this).save(this);
    }

    private void updateSummaryView() {
        Double totalAmt = mCart.getTotalAmount();
        String totalAmtStr = String.format(Locale.getDefault(), "%.2f", totalAmt);

        TextViewUtils.setText(mTotalNoTax, "$ " + totalAmtStr);
        TextViewUtils.setText(mTotalWithTax, "$ " + totalAmtStr);
    }

    private class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.ViewHolder> {

        private final List<Book> mBooks = new ArrayList<>();

        void setBooks(List<Book> books) {
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
            holder.fillValues(mBooks.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mNameText;
            private final TextView mPriceText;
            private final TextView mQuantityText;
            private final Button mPlusBtn;
            private final Button mMinusBtn;
            private final ImageButton mDeleteBtn;

            ViewHolder(View itemView) {
                super(itemView);

                mNameText = (TextView) itemView.findViewById(R.id.cart_item_book_name);
                mPriceText = (TextView) itemView.findViewById(R.id.cart_item_book_price);
                mQuantityText = (TextView) itemView.findViewById(R.id.cart_item_book_quantity);

                mPlusBtn = (Button) itemView.findViewById(R.id.qty_increase_btn);
                mMinusBtn = (Button) itemView.findViewById(R.id.qty_decrease_btn);
                mDeleteBtn = (ImageButton) itemView.findViewById(R.id.cart_item_delete_book_btn);
            }

            void fillValues(Book book, final int position) {
                if (book != null) {
                    TextViewUtils.setText(mNameText, book.getName());
                    TextViewUtils.setText(mPriceText, "$ " + book.getPrice());
                    TextViewUtils.setText(mQuantityText, String.valueOf(book.getQuantity()));

                    if (mPlusBtn != null) {
                        mPlusBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                increaseQty(position);
                            }
                        });
                    }

                    if (mMinusBtn != null) {
                        mMinusBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                decreaseQty(position);
                            }
                        });
                    }

                    if (mDeleteBtn != null) {
                        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteItem(position);
                            }
                        });
                    }
                }
            }

            private void decreaseQty(int position) {
                Book book = mBooks.get(position);
                if (book.getQuantity() > 1) {
                    int qty = mCart.decreaseQuantity(book);

                    TextViewUtils.setText(mQuantityText, String.valueOf(qty));

                    updateSummaryView();
                }
            }

            private void increaseQty(int position) {
                Book book = mBooks.get(position);

                int qty = mCart.increaseQuantity(book);

                TextViewUtils.setText(mQuantityText, String.valueOf(qty));

                updateSummaryView();
            }

            private void deleteItem(int position) {
                Book book = mBooks.get(position);
                mCart.remove(book);

                mBooks.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mBooks.size());

                updateSummaryView();
            }
        }
    }
}
