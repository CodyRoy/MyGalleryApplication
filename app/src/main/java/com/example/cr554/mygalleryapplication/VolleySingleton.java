package com.example.cr554.mygalleryapplication;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by cr554 on 1/29/2017.
 */

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private static Context ctx;
    private ImageLoader imgLoader;
    private RequestQueue requestQueue;

    private VolleySingleton(Context context){
        this.ctx=context;
        requestQueue = getRequestQueue();

        imgLoader = new ImageLoader(requestQueue, new LruImgCache(LruImgCache.getCacheSize(context)));
    }

    private RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VolleySingleton getmInstance(Context context){
        if (mInstance==null){
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
    public ImageLoader getImgLoader(){
        return imgLoader;
    }
    public void cancelAll(String tag){
        if (mInstance!=null){
            if(requestQueue!=null){
                requestQueue.cancelAll(tag);
            }
        }
    }
}
