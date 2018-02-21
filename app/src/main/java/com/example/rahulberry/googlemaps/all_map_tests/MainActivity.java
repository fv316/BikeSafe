package com.example.rahulberry.googlemaps.all_map_tests;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.rahulberry.googlemaps.MapFragment;
import com.example.rahulberry.googlemaps.R;

public class MainActivity extends AppCompatActivity {

    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapFragment = new MapFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mapframe, mapFragment);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MapFragment.MY_PERMISSIONS_REQUEST_LOCATION){
            mapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}