package com.example.cr554.mygalleryapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * Created by cr554 on 1/29/2017.
 */

public class FullscreenFragment extends Fragment {
    String photoID = null;
    private static final String requestTag = "FullscreenString";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle b = this.getArguments();
        if (b == null){
            throw new NullPointerException("no info");
        }
        photoID = b.getString("photoID");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootView = (RelativeLayout) inflater.
                inflate(R.layout.fullscreen_images, container, false);
        final NetworkImageView img = (NetworkImageView) rootView.findViewById(R.id.fullscreen);
        final TextView photoTitleView = (TextView) rootView.findViewById(R.id.photo_title);
        final TextView photoDescriptionView =
                (TextView) rootView.findViewById(R.id.photo_description);
        // create a request for photo title
        JsonObjectRequest photoInfoRequest = new JsonObjectRequest(
                Request.Method.GET,
                getPhotoInfo(),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // get the biggest size available
                        try {
                            JSONObject photoInfo = response.getJSONObject("photo");
                            photoTitleView.setText(
                                    photoInfo.getJSONObject("title").getString("_content"));
                            photoDescriptionView.setText(
                                    photoInfo.getJSONObject("description").getString("_content"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new VolleyErrorListener(getContext())
        );

        // create the request for photo links and their sizes...
        JsonObjectRequest photoSizesRequest = new JsonObjectRequest(
                Request.Method.GET,
                getPhotoSize(),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // get the biggest size available
                        JSONArray sizes;
                        String photoUrl = null;
                        try {
                            sizes = response.getJSONObject("sizes").getJSONArray("size");
                            photoUrl = sizes
                                    .getJSONObject(sizes.length() - 1)
                                    .getString("source");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // finally set the url for the image
                        img.setImageUrl(
                                photoUrl,
                                VolleySingleton.getmInstance(getContext()).getImgLoader());
                    }
                },
                new VolleyErrorListener(getContext())
        );

        photoSizesRequest.setTag(requestTag);
        photoInfoRequest.setTag(requestTag);
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(photoInfoRequest);
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(photoSizesRequest);

        return rootView;
    }
    private String getPhotoSize() {
        //getContext().getResources().getString(R.string.flickr_base_url)
        String baseUri = "http://api.flickr.com/services/rest/";
        Uri u = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("method", "flickr.photos.getSizes")
                .appendQueryParameter("api_key", Constant.FLICKR_API_KEY)
                .appendQueryParameter("photo_id", photoID)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build();

        return u.toString();
    }

    private String getPhotoInfo() {
        String baseUri ="http://api.flickr.com/services/rest/";
        Uri u = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("method", "flickr.photos.getInfo")
                .appendQueryParameter("api_key", Constant.FLICKR_API_KEY)
                .appendQueryParameter("photo_id", photoID)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build();

        return u.toString();
    }
}
