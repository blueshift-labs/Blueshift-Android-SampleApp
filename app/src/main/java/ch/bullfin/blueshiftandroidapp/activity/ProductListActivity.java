package ch.bullfin.blueshiftandroidapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.blueshift.Blueshift;

import java.util.HashMap;

import ch.bullfin.blueshiftandroidapp.ProgressDialogDisplayTask;
import ch.bullfin.blueshiftandroidapp.R;


public class ProductListActivity extends ActionBarActivity {

    private EditText mQueryText;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mContext = this;

        mQueryText = (EditText) findViewById(R.id.search_query_field);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Blueshift.getInstance(this).trackScreenView(this);
    }

    public void onProductClicked(View view) {
        startActivity(new Intent(this, ProductActivity.class));
    }

    public void onSearchClicked(View view) {
        new ProgressDialogDisplayTask(this).execute();
        if (mQueryText != null && mQueryText.getText() != null) {
            String[] skus = new String[]{"P0076", "P0907", "P5633", "P8976"};
            HashMap<String, Object> sampleFilterHash = new HashMap<>();
            sampleFilterHash.put("sample_filter_key", "sample_filter_value");

            Blueshift.getInstance(this).trackProductSearch(skus,
                    4,
                    1,
                    mQueryText.getText().toString()
                    ,sampleFilterHash
            );

            mQueryText.setText("");
        }
    }
}
