package com.example.rahulberry.googlemaps;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by rahulberry on 17/02/2018.
 */

public class welcome_three extends AppCompatActivity{

    Button nextthree;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_three);

        ImageView imageview = (ImageView)findViewById(R.id.bikergif_three);

        Glide.with(welcome_three.this)
                .load(R.drawable.gif_two)
                .asGif()
                .placeholder(R.drawable.gif_two)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageview);

        nextthree = (Button)findViewById(R.id.next_three);

        nextthree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(welcome_three
                       .this, UserInfo.class);
                startActivity(i);
                finish();
        }
    });

         }
    }
