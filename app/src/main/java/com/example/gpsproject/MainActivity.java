package com.example.gpsproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import android.database.DataSetObserver;
import android.location.Geocoder.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView lat;
    TextView lon;
    TextView address;
    TextView totalDistance;
    List<Address> addresses;
    ArrayList<LocInfo> locInfos = new ArrayList<LocInfo>();
    Location current, previous;
    double latitude;
    double longitude;
    double distance;
    double sum;
    LocationManager locationManager;
    LocationListener locationListener;
    long previousT;
    long currentT;
    CustomAdapter adapter;
    ListView listView;


    protected void onSaveInstanceState(Bundle outState) {
        //Saving all of my information upon change of instance State
        super.onSaveInstanceState(outState);
        outState.putString("address", (String) address.getText());
        outState.putString("distance", (String) totalDistance.getText());
        outState.putString("lat", (String) lat.getText());
        outState.putString("lon", (String) lon.getText());
        outState.putDouble("sum", sum);
        outState.putParcelableArrayList("locations", locInfos);
        locationManager.removeUpdates(locationListener);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        address = findViewById(R.id.address);
        totalDistance = findViewById(R.id.totalDistance);
        listView = findViewById(R.id.listview);

//Restore Instance State upon an Orientation change
        if(savedInstanceState != null){
            address.setText(savedInstanceState.getString("address"));
            lat.setText(savedInstanceState.getString("lat"));
            lon.setText(savedInstanceState.getString("lon"));
            totalDistance.setText(savedInstanceState.getString("distance"));
            sum = savedInstanceState.getDouble("sum");
            locInfos = savedInstanceState.getParcelableArrayList("locations");

        }

        //Setting up Custom adapter and linking it to the list of LocInfo objects
        adapter = new CustomAdapter(this, R.layout.adapter_layout, locInfos);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });
        listView.setAdapter(adapter);

        //Check for Permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            //Once permissions are valid, run location manager and access location data
            startGeocoder();
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        startGeocoder();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void startGeocoder() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {

                System.out.println("here...................");
                lat.setText("Latitude: " + roundLL(location.getLatitude()));
                lon.setText("Longitude: " + roundLL(location.getLongitude()));
                latitude = location.getLatitude();
                longitude =location.getLongitude();

                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 5);
                    address.setText("Address: " + addresses.get(0).getAddressLine(0));


                    if (current != null) {
                        previousT = currentT;
                        if (previous == null) {
                            previous = current;
                        } else {
                            previous.setLongitude((current.getLongitude()));
                            previous.setLatitude(current.getLatitude());
                        }
                    }

                    current = location;
                    currentT = SystemClock.elapsedRealtime();

                    long timeSpent = 0;
                    if(current != null && previous != null) {
                        distance = current.distanceTo(previous);
                        sum += distance;
                        totalDistance.setText("Total Distance Traveled: " + roundD(sum) + " meters traveled");
                        timeSpent = (currentT-previousT)/1000;
                        locInfos.add(0,new LocInfo(timeSpent, addresses.get(0), current));
                        adapter.notifyDataSetChanged();
                        listView.invalidate();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10
                , locationListener);
    }
    public String roundLL(double a){
        DecimalFormat df = new DecimalFormat("###,###,###.####");
        return df.format(a);
    }
    public String roundD (double b){
        DecimalFormat df = new DecimalFormat("###,###,###.##");
        return df.format(b);
    }

}