package com.example.dr0gi.mapspoints;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

public class MyLocationListener implements LocationListener {

    private Location imHere;
    private LocationManager locationManager;
    private Context context;

    public MyLocationListener(Context context, LocationManager locationManager) {
        super();
        this.context = context;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, this);
        imHere = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (imHere == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, this);
            imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (imHere == null) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000 * 10, 10, this);
            imHere = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        imHere = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        imHere = locationManager.getLastKnownLocation(provider);
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation() {
        return imHere;
    }
}
