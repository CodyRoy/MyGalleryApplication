package com.example.cr554.mygalleryapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//goal: i need a grid view to hold the ,layout of the pictures within an album
// and then i need another view to hold the pictures themselves
// these pictures are taken from yale's flickr using an api
// the pictures should be clickable to blow them up.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState==null){
            GalleryFragment gf = new GalleryFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.main_activity_fragment, gf).commit();
        }
    }


}
