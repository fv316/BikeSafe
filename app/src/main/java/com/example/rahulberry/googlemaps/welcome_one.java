package com.example.rahulberry.googlemaps;
//You can have all your gifs loaded from the internet so that you don't need to store them on the device

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class welcome_one extends AppCompatActivity{

    Button next;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_one);

        ImageView imageview = (ImageView)findViewById(R.id.bikergif);

        Glide.with(welcome_one.this)
                .load(R.drawable.bikerdribbble)
                .asGif()
                .placeholder(R.drawable.bikerdribbble)
                .crossFade()
                .into(imageview);

        next = (Button)findViewById(R.id.next_one);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(welcome_one
                        .this, welcome_two.class);
                startActivity(i);
                finish();
            }
        });
         }
    }
