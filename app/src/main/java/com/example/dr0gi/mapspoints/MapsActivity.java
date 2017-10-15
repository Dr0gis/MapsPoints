package com.example.dr0gi.mapspoints;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity/*FragmentActivity*/ implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    EditText searchInput;
    RecyclerView recyclerViewHistory;
    SearchHistoryAdapter searchHistoryAdapter;

    List<String> historyListFull;
    List<String> historyListLastThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchInput = (EditText) findViewById(R.id.search_input);
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = v.getText().toString();

                if (input.compareTo("") == 0) {
                    v.setHint("Search");
                    return false;
                }

                searchPoints(input);

                int index = historyListFull.indexOf(input);
                if (index != -1) {
                    historyListFull.remove(index);
                }
                historyListFull.add(input);

                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                v.setText("");
                v.setHint(input);

                return false;
            }
        });
        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        });
        searchInput.addTextChangedListener(new TextWatcher() {
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
        });

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_search, menu);

        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Add a marker in Sydney and move the camera
                    LatLng pointLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(pointLocation).title("My location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pointLocation));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        });
    }

    private List<PointInfo> connectToApiFoursquaer(String strInput, LatLng currentLocation) {
        List<PointInfo> listPoints = new ArrayList<>();

        // Test data
        listPoints.add(new PointInfo("Test1", new LatLng(currentLocation.latitude - Math.random() * 0.005, currentLocation.longitude - Math.random() * 0.005)));
        listPoints.add(new PointInfo("Test2", new LatLng(currentLocation.latitude + Math.random() * 0.005, currentLocation.longitude - Math.random() * 0.005)));
        listPoints.add(new PointInfo("Test3", new LatLng(currentLocation.latitude - Math.random() * 0.005, currentLocation.longitude + Math.random() * 0.005)));
        listPoints.add(new PointInfo("Test4", new LatLng(currentLocation.latitude + Math.random() * 0.005, currentLocation.longitude + Math.random() * 0.005)));


        return listPoints;
    }
    private void searchPoints(final String strInput) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mMap.clear();

                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("My location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    List<PointInfo> listPoints = connectToApiFoursquaer(strInput, currentLocation);
                    for (PointInfo point: listPoints) {
                        mMap.addMarker(new MarkerOptions().position(point.getCoordinats()).title(point.getName()));
                    }
                }
            }
        });
    }
}
