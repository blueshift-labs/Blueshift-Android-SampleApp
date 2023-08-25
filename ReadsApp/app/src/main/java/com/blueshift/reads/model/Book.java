package com.blueshift.reads.model;

import android.net.Uri;

import com.blueshift.BlueshiftLogger;

import java.io.Serializable;

/**
 * @author Rahul Raveendran V P
 * Created on 12/10/16 @ 2:09 PM
 * https://github.com/rahulrvp
 */


public class Book implements Serializable {
    private String sku;
    private String name;
    private String price;
    private String image_url;
    private String web_url;
    private int quantity = 1;

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return image_url;
    }

    public String getWebUrl() {
        return web_url;
    }

    public boolean hasSamePath(String path) {
        try {
            if (path != null) {
                Uri uri = Uri.parse(web_url);
                String localPath = uri.getPath();
                return path.equals(localPath);
            }
        } catch (Exception e) {
            BlueshiftLogger.e("", e);
        }
        return false;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
