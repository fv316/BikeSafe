package com.example.rahulberry.googlemaps;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.maps.SupportMapFragment;

public class main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SupportMapFragment smapfragment;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        smapfragment = SupportMapFragment.newInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Makes a new mapfragmemnt;
        mapFragment = new MapFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mapframe, mapFragment);
        transaction.commit();

        // Create our Floating Action Button
        final ImageView fabIconNew = new ImageView(this);
        /*  Set the icon in the center of the Floating Action Button
            Old Deprecated way to get drawable -> getResources().getDrawable(R.drawable.ic_action_new_light)
            Instead, use -> getApplicationContext(), R.drawable.ic_action_new_light)
         */
        fabIconNew.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_new_light));

        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
                //.setPosition(FloatingActionButton.POSITION_BOTTOM_LEFT)
                .build();

        // Create your menu items which are also Floating Action Buttons
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        // Create an image view for each menu item
        ImageView menuOption1 = new ImageView(this);
        ImageView menuOption2 = new ImageView(this);
        ImageView menuOption3 = new ImageView(this);
        ImageView menuOption4 = new ImageView(this);

        // Set the icon for each menu item
        menuOption1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_chat_light));
        menuOption2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_camera_light));
        menuOption3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_video_light));
        menuOption4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_place_light));

        // Build the menu with default options: 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(menuOption1).build())
                .addSubActionView(rLSubBuilder.setContentView(menuOption2).build())
                .addSubActionView(rLSubBuilder.setContentView(menuOption3).build())
                .addSubActionView(rLSubBuilder.setContentView(menuOption4).build())
                .attachTo(rightLowerButton)
                //.setStartAngle(360)
                .build();

        // Listen for menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        // OnClickListeners for each menu item
        menuOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Option 1", Toast.LENGTH_SHORT).show();
                /*
                    Could also go to an Intent or perform whatever action you want.
                    To go to another Activity, you would use something like:
                    Intent goToActivity = new Intent(getApplicationContext(), Name_Of_Activity.class)
                    startActivity(goToActivity);
                 */
            }
        });

        menuOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Option 2", Toast.LENGTH_SHORT).show();
            }
        });

        menuOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Option 3", Toast.LENGTH_SHORT).show();
            }
        });

        menuOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Option 4", Toast.LENGTH_SHORT).show();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i;
            i = new Intent(com.example.rahulberry.googlemaps.main.this,SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            item.setCheckable(false);
            Intent i;
            i = new Intent(com.example.rahulberry.googlemaps.main.this,AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_account) {
            item.setCheckable(false);

        } else if (id == R.id.nav_feedback) {
            item.setCheckable(false);

        } else if (id == R.id.nav_legal) {
            item.setCheckable(false);
        } else if (id == R.id.nav_terms) {
            item.setCheckable(false);

        } else if (id == R.id.nav_share) {
            item.setCheckable(false);

        } else if (id == R.id.nav_send) {
            item.setCheckable(false);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}