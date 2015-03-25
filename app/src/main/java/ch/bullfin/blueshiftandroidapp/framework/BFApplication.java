package ch.bullfin.blueshiftandroidapp.framework;

import android.app.Application;

import com.blueshift.Blueshift;
import com.blueshift.model.Configuration;
import com.blueshift.model.UserInfo;

import ch.bullfin.blueshiftandroidapp.R;
import ch.bullfin.blueshiftandroidapp.activity.CartActivity;
import ch.bullfin.blueshiftandroidapp.activity.OfferDisplayActivity;
import ch.bullfin.blueshiftandroidapp.activity.ProductActivity;

/**
 * Created by rahul on 17/2/15.
 */
public class BFApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration configuration = new Configuration();
        configuration.setAppIcon(R.mipmap.ic_launcher);
        configuration.setProductPage(ProductActivity.class);
        configuration.setCartPage(CartActivity.class);
        configuration.setOfferDisplayPage(OfferDisplayActivity.class);
        configuration.setApiKey("5be04919d8773728197f8bd0e2fedce2");

        Blueshift.getInstance(this).initialize(configuration);
    }
}
