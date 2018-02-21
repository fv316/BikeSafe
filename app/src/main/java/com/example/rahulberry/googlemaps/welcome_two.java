package com.example.rahulberry.googlemaps;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by rahulberry on 17/02/2018.
 */

public class welcome_two extends AppCompatActivity{

    Button nexttwo;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_two);

        ImageView imageview = (ImageView)findViewById(R.id.bikergif_two);

        Glide.with(welcome_two.this)
                .load(R.drawable.gif_three)
                .asGif()
                .placeholder(R.drawable.gif_three)
                .crossFade()
                .into(imageview);
        nexttwo = (Button)findViewById(R.id.next_two);

        nexttwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(welcome_two
                        .this, welcome_three.class);
                startActivity(i);
            }
        });
         }
    }
