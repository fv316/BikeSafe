package com.example.rahulberry.googlemaps;

import android.Manifest;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

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

import com.squareup.otto.Bus;
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
    public boolean firstTime = true;
    public boolean firstSMS = true;
    public boolean firstNotification = true;
    NotificationHelper helper;

    public LatLng latLng1;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker BikeMarker;
    Marker TempMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = "Disarmed";
        BusProvider.getInstance().register(this);
        //helper = new NotificationHelper(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        helper = new NotificationHelper(getActivity());
    }

    public void isFinishing() {
        ;
        if (getActivity().isFinishing()) {
            onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity().isFinishing()) {
            BusProvider.getInstance().unregister(this);
        }
    }


  /* @Override
   public void onDestroy(){
       super.onDestroy();
       BusProvider.getInstance().unregister(this);
   }*/

    private final String UPDATE_MAP = "com.myco.myapp.UPDATE_MAP";


    private void setUpMapIfNeeded() {

        if (mGoogleMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
      Calendar time = Calendar.getInstance();
            int currentTime = time.get(Calendar.HOUR_OF_DAY);
        if (currentTime >= 18) {
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
        } else {
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
        }

    String TAG1 = "@subscribe";
    @Subscribe
    public void getMode(mode event){
        state = event.usermode;
        Log.i(TAG1, state);
    }


    @Subscribe
    public void text_received(coordinates event) {
        Log.d(TAG, "text in map");
        String bikeloc = event.bikecoordinates;
        Toast.makeText(getActivity(),bikeloc, Toast.LENGTH_LONG).show();
        //extract coordinates
        String[] parts = bikeloc.split(" ");
        Double Latitude = (Double.parseDouble(parts[0]))/1000000;
        Double Longitude = (Double.parseDouble(parts[1]))/1000000;
        LatLng latLng = new LatLng(Latitude, Longitude);
        bike = latLng;
        //need to think of an if statement that properly deletes the old marker: this didn't work;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your Bike");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        BikeMarker = mGoogleMap.addMarker(markerOptions);
        if(firstSMS){
            TempMarker = BikeMarker;
            BikeMarker = TempMarker;
            firstSMS = false;
        }
        if(BikeMarker != TempMarker) {
            TempMarker.remove();
        }
        else{
            TempMarker = BikeMarker;

        }
        if ((bike != null) && (user != null) && (state.equals("Disarmed"))) {
            Log.d(TAG1, state);
                distance_check();
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
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        double meters = valueResult * 1000;

        if(meters < 10){
            firstNotification = true;
        }
        if((meters > 10) && (firstNotification)){
            firstNotification = false;
            sendLockReminder();
        }
    }




    public void sendLockReminder(){
            NotificationCompat.Builder builder = helper.getnotificationChannelNotification("BikeSafe","Did you forget to lock your bike?");
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

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        LatLng latLng = new LatLng(lat3, lon3);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
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
        // ADD GEOCODER AND TRY-CATCH
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        user = latLng;
        //MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.position(latLng);
        //markerOptions.title("Current Position");
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        //move map camera
        if(firstTime){
            firstTime = false;
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        }

       // String TAG2 = "Compare";
      //  Log.i(TAG2, state);
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