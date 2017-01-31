package com.example.cr554.mygalleryapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by cr554 on 1/29/2017.
 */

class LruImgCache extends LruCache<String,Bitmap> implements ImageLoader.ImageCache{
    LruImgCache(int maxSize) {
        super(maxSize);
    }

    protected int sizeOf(String key, Bitmap value){
        return value.getRowBytes() * value.getHeight();
    }


    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url,bitmap);
    }

    //sloppy hard coding
    static int getCacheSize(Context context){
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final int screenWidth = dm.widthPixels;
        final int screenHeight = dm.heightPixels;
        final int screenBytes = screenHeight * screenWidth * 4;

        return screenBytes * 3; //three screens worth of pictures
    }

}
