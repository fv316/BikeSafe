package com.example.rahulberry.googlemaps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;


import com.getbase.floatingactionbutton.FloatingActionButton;


import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String state;
    SupportMapFragment smapfragment;
    MapFragment mapFragment;
    FirebaseAuth mAuth;
    public Button fb, tw, St, Wb;


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, login.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
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

        final FloatingActionButton facebook = (FloatingActionButton) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = openFacebook(main.this);
                startActivity(facebookIntent);            }
        });

        final FloatingActionButton stolenbikes = (FloatingActionButton) findViewById(R.id.stolen_bikes);
        stolenbikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://stolen-bikes.co.uk/"); // missing 'http://' will cause crash
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        final FloatingActionButton strava = (FloatingActionButton) findViewById(R.id.strava);
        strava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stravaIntent = openStrava(main.this);
                startActivity(stravaIntent);            }
        });

        final FloatingActionButton twitter = (FloatingActionButton) findViewById(R.id.twitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent twitterIntent = openTwitter(main.this);
                startActivity(twitterIntent);            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        final MenuItem edit = menu.findItem(R.id.nav_item1);
        final MenuItem logout = menu.findItem(R.id.logout);
        SpannableString s = new SpannableString(logout.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        logout.setTitle(s);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.nav_item1)
                .setActionView(new Switch(this));


        final Switch user_mode = (Switch) navigationView.getMenu().findItem(R.id.nav_item1).getActionView();
        user_mode.setChecked(false
        );
        user_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (user_mode.isChecked()) {
                     state = "Secure";
                     edit.setTitle(state);
                     sendSMS("+447713606066", "Secure"); //Rahul
                    // sendSMS("+447541241808", "Cecure"); //Niall
                   // sendSMS("+447936663084", "Secure"); //Lydia
                     BusProvider.getInstance().post(new mode(state));
                } else {
                    state = "Disarmed";
                    edit.setTitle(state);
                    sendSMS("+447713606066", "Disarmed"); //Rahul
                    //sendSMS("+447541241808", "Disarmed"); //Niall
                  //  sendSMS("+447936663084", "Disarmed"); //Lydia
                    BusProvider.getInstance().post(new mode(state));
                }
            }

        });
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void shareTwitter(String message) {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message);
        tweetIntent.setType("text/plain");

        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, message);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(message)));
            startActivity(i);
            Toast.makeText(this, "Twitter app isn't found", Toast.LENGTH_LONG).show();
        }
    }
    String TAG1 = "somestuff";
    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG1, "UTF-8 should always be supported", e);
            return "";
        }
    }


    public static Intent openFacebook(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/149935285698585"));
        } catch (Exception e){

            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/BikeSafe-149935285698585/"));
        }
    }

    public static Intent openStrava(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.strava", 0);
            return new Intent(Intent.ACTION_VIEW);
        } catch (Exception e){
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.strava.com/mobile"));
        }
    }

    public static Intent openTwitter(Context context) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.twitter.android", 0);
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?user_id=377578445"));
        } catch (Exception e){
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/BikeSafe-149935285698585/"));
        }
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
        if (id == R.id.action_terms) {
            Intent i;
            i = new Intent(com.example.rahulberry.googlemaps.main.this,TermsActivity.class);
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
            i = new Intent(this,AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_account) {
            item.setCheckable(false);
            Intent i;
            i = new Intent(this,Account.class);
            startActivity(i);
        } else if (id == R.id.nav_contact) {
            item.setCheckable(false);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(main.this);
            View parentView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            bottomSheetDialog.setContentView(parentView);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
            bottomSheetBehavior.setState(3);
            bottomSheetDialog.show();
            Button fb = (Button) parentView.findViewById(R.id.button_facebook);
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent facebookIntent = openFacebook(main.this);
                    startActivity(facebookIntent);
                }
            });

            Button tw = (Button) parentView.findViewById(R.id.button_twitter);
            tw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent twitterIntent = openTwitter(main.this);
                    startActivity(twitterIntent);            }
            });

            Button St = (Button) parentView.findViewById(R.id.button_strava);
            St.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent StravaIntent = openStrava(main.this);
                    startActivity(StravaIntent);            }
            });

            Button Wb = (Button) parentView.findViewById(R.id.button_website);
            St.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.niallrees.com"));
                    startActivity(browserIntent);
                }
            });


        }else if(id == R.id.nav_settings){
            item.setCheckable(false);
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_share) {
            item.setCheckable(false);
            item.setCheckable(false);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(main.this);
            View parentView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            bottomSheetDialog.setContentView(parentView);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
            bottomSheetBehavior.setState(3);
            bottomSheetDialog.show();
            Button fb = (Button) parentView.findViewById(R.id.button_facebook);
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent facebookIntent = openFacebook(main.this);
                    startActivity(facebookIntent);
                }
            });

            Button tw = (Button) parentView.findViewById(R.id.button_twitter);
            tw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 shareTwitter("BIKESAFE IS THE BEST WOOO LIT LITLIT");
                }
            });

            Button St = (Button) parentView.findViewById(R.id.button_strava);
            St.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent StravaIntent = openStrava(main.this);
                    startActivity(StravaIntent);            }
            });

            Button Wb = (Button) parentView.findViewById(R.id.button_website);
            St.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.niallrees.com"));
                    startActivity(browserIntent);
                }
            });

        }else if(id == R.id.logout){
            item.setCheckable(false);
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, login.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}