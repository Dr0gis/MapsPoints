package com.example.dr0gi.mapspoints;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class FoursquareApiManager {
    private final String CLIENT_ID = "client_id";
    private final String CLIENT_ID_VALUE = "S3N5E53AZS0P5UP2UCBDLXAA3YGFHT4TB21ANDCTWK5QE3DT";
    private final String CLIENT_SECRET = "client_secret";
    private final String VERSION = "v";
    private final String VERSION_VALUE = "20170801";
    private final String CLIENT_SECRET_VALUE = "OPJ4XQX2LC3WXT4L3AWJ454P1QKCUFRUTRUUGD0HA2S30Y4X";

    // Parameters for Get Venue Recommendations (GET https://api.foursquare.com/v2/venues/explore)
    private final String urlSearch = "https://api.foursquare.com/v2/venues/explore";
    private final String LL = "ll";            /* Required unless near is provided. Latitude and
                                                longitude of the user’s location. */
    private final String RADIUS = "radius";    /* Radius to search within, in meters. If radius is
                                                not specified, a suggested radius will be used based
                                                on the density of venues in the area. The maximum
                                                supported radius is currently 100,000 meters. */
    private final String QUERY = "query";      /* A term to be searched against a venue’s tips,
                                                category, etc. The query parameter has no effect
                                                when a section is specified. */
    private final String LIMIT = "limit";      /* Number of results to return, up to 50. */

    // Parameters for Get Venue Recommendations (GET https://api.foursquare.com/v2/venues/search)
    private final String urlRecommendations = "https://api.foursquare.com/v2/venues/search";
    private final String INTENT = "intent";     /* One of the values below, indicating your intent
                                                in performing the search. If no value is specified,
                                                defaults to checkin */

    private String formedUrlRecommendation(LatLng ll, Integer radius, String query, Integer limit) {
        Uri URI = Uri.parse(urlRecommendations)
                .buildUpon()
                .appendQueryParameter(CLIENT_ID, CLIENT_ID_VALUE)
                .appendQueryParameter(CLIENT_SECRET, CLIENT_SECRET_VALUE)
                .appendQueryParameter(VERSION, VERSION_VALUE)
                .appendQueryParameter(LL, ll.latitude + "," + ll.longitude)
                .appendQueryParameter(RADIUS, radius.toString())
                .appendQueryParameter(QUERY, query)
                .appendQueryParameter(LIMIT, limit.toString())
                .build();
        return URI.toString();
    }
    public String getVenueRecommendations(LatLng ll, Integer radius, String query, Integer limit) {
        URL url;
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            url = new URL(formedUrlRecommendation(ll, radius, query, limit));
            urlConnection = (HttpURLConnection) url.openConnection();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(urlConnection.getResponseMessage() + ": with " + urlRecommendations);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            response = out.toString();
            Log.d("GetVenueRecommendations", out.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    private String formedUrlSearch(LatLng ll, String intent, Integer radius, String query, Integer limit) {
        Uri URI = Uri.parse(urlRecommendations)
                .buildUpon()
                .appendQueryParameter(CLIENT_ID, CLIENT_ID_VALUE)
                .appendQueryParameter(CLIENT_SECRET, CLIENT_SECRET_VALUE)
                .appendQueryParameter(VERSION, VERSION_VALUE)
                .appendQueryParameter(LL, ll.latitude + "," + ll.longitude)
                .appendQueryParameter(INTENT, intent)
                .appendQueryParameter(RADIUS, radius.toString())
                .appendQueryParameter(QUERY, query)
                .appendQueryParameter(LIMIT, limit.toString())
                .build();
        return URI.toString();
    }
    public String getSearchForVenues(LatLng ll, String intent, Integer radius, String query, Integer limit) {
        URL url;
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            url = new URL(formedUrlSearch(ll, intent, radius, query, limit));
            urlConnection = (HttpURLConnection) url.openConnection();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(urlConnection.getResponseMessage() + ": with " + urlRecommendations);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            response = out.toString();
            Log.d("GetSearchForVenues", out.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }
}
