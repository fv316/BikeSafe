package com.example.rahulberry.googlemaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class login extends AppCompatActivity {

    Button create_account;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        create_account= (Button)findViewById(R.id.create_new);
        login = (Button)findViewById(R.id.login2);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(com.example.rahulberry.googlemaps.login.this, main.class);
                startActivity(i);
            }
        });
        create_account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            Intent i = new Intent(com.example.rahulberry.googlemaps.login.this,register.class);
                startActivity(i);
            }
        });
        }
    }
//MAKE THIS FUNCTION USING THE PREMADE LOGIN MENU ANDROID STUDIO OFFERS!!!







