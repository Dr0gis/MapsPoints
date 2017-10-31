package com.example.dr0gi.mapspoints;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int MY_PERMISSIONS_REQUEST_LOCATION = 1047;
    private static final int RADIUS_SEARCH = 1000;


    private GoogleMap mMap;
    private EditText searchInput;
    private RecyclerView recyclerViewHistory;
    private SearchHistoryAdapter searchHistoryAdapter;

    private List<String> historyListFull;
    private List<String> historyListLastThree;

    private LocationManager locationManager;
    private MyLocationSource locationSource;
    private ClusterManager<MarkerItem> clusterManager;

    private interface FirstDetectLocationListener {
        void zoomCamera(Location location);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchInput = (EditText) findViewById(R.id.search_input);
        searchInput.setOnEditorActionListener(new SubmitSearchInput());
        searchInput.setOnFocusChangeListener(new ShowRecyclerView());
        searchInput.addTextChangedListener(new SearchInHistory());

        recyclerViewHistory = (RecyclerView) findViewById((R.id.history_search));
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        historyListFull = new ArrayList<>();
        historyListLastThree = new ArrayList<>();

        searchHistoryAdapter = new SearchHistoryAdapter(MapsActivity.this, historyListLastThree, new SearchHistoryAdapter.HistoryItemClickListener() {
            @Override
            public void onHistoryItemClicked(int pos) {
                searchInput.setText(historyListLastThree.get(pos));
                searchInput.setSelection(searchInput.getText().length());
            }
        });
        recyclerViewHistory.setAdapter(searchHistoryAdapter);

        locationSource = new MyLocationSource();
        FirstDetectLocationListener detectLocationListener = new FirstDetectLocationListener() {

            Boolean isUsed = false;

            @Override
            public void zoomCamera(Location location) {
                if (!isUsed) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    isUsed = true;
                }
            }
        };
        locationManager = new LocationManager(locationSource, detectLocationListener);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void connectToApiFoursquaer(String strInput, Location location, FoursquareApiExecutorManager.OnFoursquareApiCompleted<List<PointInfo>> listener) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        FoursquareApiExecutorManager foursquareApiExecutorManager = new FoursquareApiExecutorManager();
        foursquareApiExecutorManager.getSearchForVenues(latLng, "checkin", RADIUS_SEARCH, strInput, 15, listener);
    }
    private void searchPoints(final String strInput, Location location) {
        connectToApiFoursquaer(strInput, location, new FoursquareApiExecutorManager.OnFoursquareApiCompleted<List<PointInfo>>() {
            @Override
            public void onSuccess(final List<PointInfo> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.clear();
                        LatLng latLng = new LatLng(locationManager.getCurrentLocation().getLatitude(), locationManager.getCurrentLocation().getLongitude());

                        mMap.addPolygon(new PolygonOptions()
                                .add(
                                        SphericalUtil.computeOffset(latLng, RADIUS_SEARCH * Math.sqrt(2), 45),
                                        SphericalUtil.computeOffset(latLng, RADIUS_SEARCH * Math.sqrt(2), -45),
                                        SphericalUtil.computeOffset(latLng, RADIUS_SEARCH * Math.sqrt(2), -135),
                                        SphericalUtil.computeOffset(latLng, RADIUS_SEARCH * Math.sqrt(2), 135),
                                        SphericalUtil.computeOffset(latLng, RADIUS_SEARCH * Math.sqrt(2), 45)
                                )
                                .strokeWidth(5)
                                .strokeColor(Color.RED)
                                .fillColor(Color.argb(64, 255, 0, 0))
                        );

                        mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(RADIUS_SEARCH)
                            .strokeWidth(5)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.argb(64, 0, 0, 255))
                        );


                        clusterManager.clearItems();
                        for (PointInfo point : result) {
                            /*mMap.addMarker(
                                new MarkerOptions()
                                .position(point.getCoordinates())
                                .title(point.getName())
                                .icon(BitmapDescriptorFactory.fromBitmap(point.getCategory().getIcon()))
                            );*/
                            clusterManager.addItem(
                                new MarkerItem(
                                    point.getCoordinates(),
                                    point.getName(),
                                    BitmapDescriptorFactory.fromBitmap(point.getCategory().getIcon())
                                )
                            );
                        }
                        clusterManager.cluster();
                    }
                });
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, "Error connection", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocationOnMap();
                    //Toast.makeText(MapsActivity.this, "Access OK", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(MapsActivity.this, "Access BAD", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Boolean permissionFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        Boolean permissionCoarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (permissionFineLocation && permissionCoarseLocation) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        setLocationOnMap();
        clusterManager = new ClusterManager<>(MapsActivity.this, mMap);
        clusterManager.setRenderer(new MyClusterRender(MapsActivity.this, mMap, clusterManager));
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float curZoom = mMap.getCameraPosition().zoom;
                        if (curZoom < 20) {
                            clusterManager.cluster();
                        }
                        else {
                            clusterManager.clearItems();
                            clusterManager.cluster();
                        }
                    }
                });
            }
        });
    }
    private void setLocationOnMap() {
        Boolean permissionFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        Boolean permissionCoarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (permissionFineLocation && permissionCoarseLocation) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setLocationSource(locationSource);
    }
    private void markOnMyLocation(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("My location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    protected void onStart() {
        locationManager.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        locationManager.disconnect();
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager.isConnect()) {
            locationManager.stopLocationUpdates();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (locationManager.isConnect() && !locationManager.isRequesting()) {
            locationManager.startLocationUpdates();
        }
    }

    private class SubmitSearchInput implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            String input = v.getText().toString();

            if (input.compareTo("") == 0) {
                v.setHint("Search");
                return false;
            }

            Location location = locationManager.getCurrentLocation();
            if (location != null) {
                searchPoints(input, location);
            }
            else {
                Toast.makeText(MapsActivity.this, "Error detecting location", Toast.LENGTH_LONG).show();
            }

            int index = historyListFull.indexOf(input);
            if (index != -1) {
                historyListFull.remove(index);
            }
            historyListFull.add(input);

            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            v.setText("");
            v.setHint(input);

            return true;
        }
    }
    private class ShowRecyclerView implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                // Add three last query search to adapter recycler view
                int count = 3;
                if (historyListFull.size() < count) {
                    count = historyListFull.size();
                }
                historyListLastThree.clear();
                for (int i = 1; i <= count; ++i) {
                    historyListLastThree.add(historyListFull.get(historyListFull.size() - i));
                    searchHistoryAdapter.notifyDataSetChanged();
                }
                recyclerViewHistory.setVisibility(View.VISIBLE);
            } else {
                recyclerViewHistory.setVisibility(View.INVISIBLE);
            }
        }
    }
    private class SearchInHistory implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int count = 0;
            historyListLastThree.clear();
            for (String str: historyListFull) {
                if (str.contains(s.toString())) {
                    historyListLastThree.add(str);
                    ++count;
                }
                if (count == 3) {
                    break;
                }
            }
            searchHistoryAdapter.notifyDataSetChanged();
        }
    }

    private class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

        private GoogleApiClient mGoogleApiClient;
        private Location mCurrentLocation;
        private Location mLastLocation;
        LocationRequest mLocationRequest;
        private String mLastUpdateTime;
        private Boolean mRequestingLocationUpdates = false;

        MyLocationSource locationSource;
        FirstDetectLocationListener detectLocationListener;

        LocationManager(MyLocationSource locationSource, FirstDetectLocationListener detectLocationListener) {
            mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            this.locationSource = locationSource;
            this.detectLocationListener = detectLocationListener;
        }

        public Location getCurrentLocation() {
            if (mCurrentLocation != null) {
                return mCurrentLocation;
            }
            if (mLastLocation != null) {
                return mLastLocation;
            }
            return null;
        }
        public Boolean isConnect() {
            return mGoogleApiClient.isConnected();
        }
        public Boolean isRequesting() {
            return  mRequestingLocationUpdates;
        }

        public void connect() {
            mGoogleApiClient.connect();
        }
        public void disconnect() {
            mGoogleApiClient.disconnect();
        }

        private void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            mRequestingLocationUpdates = true;
//            Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(MapsActivity.this).checkLocationSettings(builder.build());
//
//            result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
//                    try {
//                        LocationSettingsResponse response = task.getResult(ApiException.class);
//                        // All location settings are satisfied. The client can initialize location
//                        // requests here.
//                    } catch (ApiException exception) {
//                        switch (exception.getStatusCode()) {
//                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                // Location settings are not satisfied. But could be fixed by showing the
//                                // user a dialog.
//                                try {
//                                    // Cast to a resolvable exception.
//                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
//                                    // Show the dialog by calling startResolutionForResult(),
//                                    // and check the result in onActivityResult().
//                                    resolvable.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
//                                } catch (IntentSender.SendIntentException e) {
//                                    // Ignore the error.
//                                } catch (ClassCastException e) {
//                                    // Ignore, should be an impossible error.
//                                }
//                                break;
//                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                                // Location settings are not satisfied. However, we have no way to fix the
//                                // settings so we won't show the dialog.
//                                break;
//                        }
//                    }
//                }
//            });
        }

        private void startLocationUpdates() {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                return;
            }
            createLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        private void stopLocationUpdates() {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mRequestingLocationUpdates = false;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (!mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
        @Override
        public void onConnectionSuspended(int i) {

        }
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            LocationSource.OnLocationChangedListener listener = locationSource.getListener();
            if (listener != null) {
                listener.onLocationChanged(location);
            }
            detectLocationListener.zoomCamera(location);

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }
    private class MyLocationSource implements LocationSource {
        private OnLocationChangedListener mListener;

        public OnLocationChangedListener getListener() {
            return mListener;
        }

        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            mListener = onLocationChangedListener;
        }

        @Override
        public void deactivate() {
            mListener = null;
        }
    }

    public class MarkerItem implements ClusterItem  {
        private LatLng latLng;
        private String title;
        private BitmapDescriptor icon;
        //private MarkerOptions marker;

        public MarkerItem(LatLng latLng, String title, BitmapDescriptor icon) {
            this.latLng = latLng;
            this.title = title;
            this.icon = icon;
        }

        @Override
        public LatLng getPosition() {
            return latLng;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSnippet() {
            return null;
        }

        public BitmapDescriptor getIcon() {
            return icon;
        }

        /*public MarkerOptions getMarker() {
            return marker;
        }

        public void setMarker(MarkerOptions marker) {
            this.marker = marker;
        }*/
    }

    public class MyClusterRender extends DefaultClusterRenderer<MarkerItem> {

        public MyClusterRender(Context context, GoogleMap map, ClusterManager clusterManager) {
            super(context, map, clusterManager);
        }


        @Override
        protected void onBeforeClusterItemRendered(MarkerItem item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
            markerOptions.icon(item.getIcon());
        }
    }
}
