package com.example.rahulberry.googlemaps;

import android.Manifest;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Random;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.squareup.otto.Subscribe;

import static android.content.ContentValues.TAG;

public class MapFragment extends SupportMapFragment
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public LatLng bike;
    public LatLng user;
    public String state;
    public LatLng bikesecure;
    public boolean firstTime = true;
    public boolean firstSMS = true;
    public boolean firstNotification = true;
    public boolean firstNotificationSecure = true;
    public boolean map_theme;
    public boolean firstsecure = true;

    public String UserMode;
    NotificationHelper helper;

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker BikeMarker;
    Marker TempMarker;
    public Firebase mRef;
    public Firebase mRef1;
    public String lastCoordinates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);

    }

    public void setMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your Bike");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        TempMarker = mGoogleMap.addMarker(markerOptions);
        }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        helper = new NotificationHelper(getActivity());
        try{
            if(UserMode.equals("DAY/NIGHT")){
        map_theme = true;
        }
        }catch(Exception e){
            Log.d(TAG, "Usermode not formed yet");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity().isFinishing()) {
            Log.d(TAG, "isfinishing");
            BusProvider.getInstance().unregister(this);
        }
    }

    private final String UPDATE_MAP = "com.myco.myapp.UPDATE_MAP";


    private void setUpMapIfNeeded() {

        if (mGoogleMap == null) {
            getMapAsync(this);
        }
    }

    public void setUserMode(final GoogleMap googleMap) {
        Calendar time = Calendar.getInstance();
        int currentTime = time.get(Calendar.HOUR_OF_DAY);
        if((currentTime < 18)&& (currentTime > 4)) {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.day));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }
        else {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.night_view));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setUserMode(googleMap);
        Firebase.setAndroidContext(getActivity());
            mRef = new Firebase("https://trackingapp-194914.firebaseio.com/");
            state = "Disarmed";
            final String TAG2 = "COORDINATES";
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lastCoordinates = dataSnapshot.child("Coordinates").getValue(String.class);
                    Log.i(TAG2, lastCoordinates);
                    String[] parts = lastCoordinates.split(" ");
                    Double Latitude = (Double.parseDouble(parts[0])) / 1000000;
                    Double Longitude = (Double.parseDouble(parts[1])) / 1000000;
                    LatLng latLng = new LatLng(Latitude, Longitude);
                    bike = latLng;
                    Firebase mRefChild = mRef.child("Coordinates");
                    mRefChild.setValue(parts[0] + " " + parts[1]);
                    setMarker(bike);
                    UserMode = dataSnapshot.child("UserMode").getValue(String.class);
                    Log.d(TAG, UserMode);
                   /* if(UserMode.equals("DAY/NIGHT")){
                         map_theme = true;
                    }
                    else{
                        map_theme = false;
                    }

                    if(map_theme){
                        setUserMode(googleMap);
                    }*/
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            //Initialize Google Play Services
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED)) {
                    //Location Permission already granted
                    buildGoogleApiClient();
                    mGoogleMap.setMyLocationEnabled(true);
                } else {
                    //Request Location Permission
                    checkLocationPermission();
                    checkSMSPermission();
                }
            } else {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }

          /*  if(firstTime){
                centreMap();
            }*/
        }

    String TAG1 = "bus with mode";
    @Subscribe
    public void getMode(mode event){
        state = event.usermode;
        Log.i(TAG1, state);
    }



    @Subscribe
    public void text_received(coordinates event) {
        if(firstSMS){
            TempMarker.remove();
            firstSMS = false;
        }
        if(BikeMarker != null) {
            BikeMarker.remove();
            TempMarker.remove();
        }
        Log.d(TAG, "text in map");
        String bikeloc = event.bikecoordinates;
        //extract coordinates
        String[] parts = bikeloc.split(" ");
        Double Latitude = (Double.parseDouble(parts[0]))/1000000;
        Double Longitude = (Double.parseDouble(parts[1]))/1000000;
        LatLng latLng = new LatLng(Latitude, Longitude);
        bike = latLng;
        if(firstsecure){
            bikesecure = bike;
            firstsecure = false;
        }
        Firebase mRefChild = mRef.child("Coordinates");
        mRefChild.setValue(parts[0]+" "+parts[1]);
        //need to think of an if statement that properly deletes the old marker: this didn't work;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your Bike");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        BikeMarker = mGoogleMap.addMarker(markerOptions);
          ///  TempMarker = BikeMarker;
           // BikeMarker = TempMarker;
        
        if ((bike != null) && (user != null) && (state.equals("Disarmed"))) {
            Log.d((TAG1), state);
                distance_check();
            }

        if((bike != null)&&(user!=null)&&(state.equals("Secure"))){
            Log.d((TAG1), state);
            distance_check_secure();
        }
    }

    public void distance_check(){
        int Radius = 6371;// radius of earth in Km
        double lat1 = bike.latitude;
        double lat2 = user.latitude;
        double lon1 = bike.longitude;
        double lon2 = user.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        double meter = valueResult % 1000;

        double meters = valueResult * 1000;

        if(meters < 10){
            firstNotification = true;
        }
        if((meters > 10) && (firstNotification)){
            firstNotification = false;
            sendLockReminder("Did you forget to secure your bike?");
        }
    }


    public void distance_check_secure(){
        int Radius = 6371;// radius of earth in Km
        double lat1 = bikesecure.latitude;
        Log.d(TAG, String.valueOf(lat1));
        double lat2 = bike.latitude;
        double lon1 = bikesecure.longitude;
        double lon2 = bike.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        double meter = valueResult % 1000;

        double meters = valueResult * 1000;

        if(meters < 10){
            firstNotificationSecure = true;
        }
        if((meters > 10) && (firstNotificationSecure)){
            firstNotificationSecure = false;
            sendLockReminder("Your bike has moved! Enter Panic mode!");
        }
    }



    public void sendLockReminder(String message){
            NotificationCompat.Builder builder = helper.getnotificationChannelNotification("BikeSafe",message);
        helper.getManager().notify(new Random().nextInt(),builder.build());
        return;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public void centreMap() {
        double lon1 = bike.longitude;
        double lat1 = bike.latitude;
        double lon2 = user.longitude;
        double lat2 = user.latitude;

        double dLon = Math.toRadians(lon2 - lon1);

        LatLng latLng = new LatLng((lat1 + lat2)/2, (lon1 + lon2)/2); // midpoint found

        int Radius = 6371;// radius of earth in Km
        double dLat = Math.toRadians(lat2 - lat1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        double meter = valueResult % 1000;
        double zoom = 20000/valueResult;
        zoom = Math.log(zoom)/Math.log(2);
        float floatzoom = (float)zoom;
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,floatzoom));
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        user = latLng;
        if(firstTime) {
            try {
                centreMap();
                firstTime = false;
            } catch (Exception e) {
                Log.d(TAG, "Unable to centre map");
            }
        }
       if(firstTime){
            firstTime = false;
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        }
            if((bike != null) && (user != null) && (state.equals("Disarmed"))){
            distance_check();
        }
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    public static final int REQUEST_SEND_SMS = 23;
    private void checkSMSPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("SMS Permission Needed")
                        .setMessage("This app needs SMS permissions")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.SEND_SMS},
                                        REQUEST_SEND_SMS );
                            }
                        })
                        .create()
                        .show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        REQUEST_SEND_SMS);

                // REQUEST_SEND_SMS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied, Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {
                    // permission denied, Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}