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

public class GalleryFragment extends Fragment {
    private String mAlbumID;
    private String mAlbumTitle;
    private ArrayList<Pair<String,String>> photoInfo = new ArrayList<>();
    private static final String requestTag = "AlbumThumbnailRequest";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle b = this.getArguments();
        if(b!=null){
            mAlbumID=b.getString("albumID");
            mAlbumTitle=b.getString("albumTitle");
            return;
        }
    }
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.gallery_layout,container, false);
        final GridView gv = (GridView) rootView.getChildAt(0);

        //create json request - stolen from stan
        //improvements: move to a class, that hnadles json requests.
        JsonObjectRequest thumbnailsRequest = new JsonObjectRequest(
                Request.Method.GET,
                getThumbnailInfo(),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray photoSet;
                        try {
                            photoSet = response.getJSONObject("photoInfo").getJSONArray("photo");
                            for (int i = 0; i < photoSet.length(); i++) {
                                JSONObject currentPhotoSet = photoSet.getJSONObject(i);
                                photoInfo.add(
                                        new Pair<>(currentPhotoSet.getString("url_sq"), currentPhotoSet.getString("id")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //set adapter-stole from stan. Clearner in its own class?
                        gv.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return photoInfo.size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                RelativeLayout rootView;
                                if (convertView == null) {
                                    rootView = (RelativeLayout) inflater
                                            .inflate(
                                                    R.layout.images_in_grid,
                                                    parent,
                                                    false);

                                } else {
                                    rootView = (RelativeLayout) convertView;
                                }

                                NetworkImageView img = (NetworkImageView) rootView.getChildAt(1);
                                img.setImageDrawable(null);
                                img.setImageUrl(
                                        photoInfo.get(position).first,
                                        VolleySingleton.getmInstance(getContext()).getImgLoader());

                                return rootView;
                            }
                        });

                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle b = new Bundle();
                                b.putString("photoID", photoInfo.get(position).second);

                                FullscreenFragment ff = new FullscreenFragment();
                                ff.setArguments(b);


                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_fragment, ff).addToBackStack(null).commit();
                            }
                        });
                        //visibility
                        gv.setVisibility(View.VISIBLE);
                    }
                }, new VolleyErrorListener(getContext())
        );
        thumbnailsRequest.setTag(requestTag);
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(thumbnailsRequest);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        getActivity().setTitle(mAlbumTitle);
    }

    @Override
    public void onStop(){
        super.onStop();
        if (VolleySingleton.getmInstance(getContext()) != null){
            VolleySingleton.getmInstance(getContext()).cancelAll(requestTag);
        }
    }

    private String getThumbnailInfo(){
        String baseUri = "https://api.flickr.com/services/rest/?";
        Uri u = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("method","flickr.photosets.getPhotos")
                .appendQueryParameter("api_key",Constant.FLICKR_API_KEY)
                .appendQueryParameter("photoset_id",mAlbumID)
                .appendQueryParameter("format","json")
                .appendQueryParameter("extras","url_sq")
                .appendQueryParameter("nojsoncallback","1")
                .build();
        return u.toString();
    }
}
