package com.example.dr0gi.mapspoints;

import android.app.Application;
import android.location.LocationListener;
import android.location.LocationManager;

public class MyApplication extends Application {

    private MyLocationListener locationListener;
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new MyLocationListener(this, locationManager);
    }

    public MyLocationListener getLocationListener() {
        return locationListener;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }
}
