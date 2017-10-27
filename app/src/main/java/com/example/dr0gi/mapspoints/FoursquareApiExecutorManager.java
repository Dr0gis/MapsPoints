package com.example.dr0gi.mapspoints;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

class FoursquareApiExecutorManager {

    interface OnFoursquareApiCompleted<T> {
        void onSuccess(T result);
        void onError();
    }

    private FoursquareApiManager foursquareApiManager;
    private MyExecutor myExecutor;

    FoursquareApiExecutorManager() {
        foursquareApiManager = new FoursquareApiManager();
        myExecutor = new MyExecutor();
    }

    public void getVenueRecommendations(LatLng ll, Integer radius, String query, Integer limit, OnFoursquareApiCompleted<List<PointInfo>> listener) {
        myExecutor.execute(new GetVenueRecommendationsRunnable(ll, radius, query, limit, listener));
    }
    public void getSearchForVenues(LatLng ll, String intent, Integer radius, String query, Integer limit, OnFoursquareApiCompleted<List<PointInfo>> listener) {
        myExecutor.execute(new GetSearchForVenuesRunnable(ll, intent, radius, query, limit, listener));
    }

    private class MyExecutor implements Executor {
        @Override
        public void execute(@NonNull Runnable command) {
            new Thread(command).start();
        }
    }

    private class GetVenueRecommendationsRunnable implements Runnable {
        private final LatLng ll;
        private final Integer radius;
        private final String query;
        private final Integer limit;
        private final OnFoursquareApiCompleted<List<PointInfo>> listener;

        public GetVenueRecommendationsRunnable(LatLng ll, Integer radius, String query, Integer limit, OnFoursquareApiCompleted<List<PointInfo>> listener) {
            this.ll = ll;
            this.radius = radius;
            this.query = query;
            this.limit = limit;
            this.listener = listener;
        }

        @Override
        public void run() {
            String response = foursquareApiManager.getVenueRecommendations(ll, radius, query, limit);
            List<PointInfo> pointInfoList = new ArrayList<PointInfo>();
            try {
                JSONObject jsonObject = new JSONObject(response).getJSONObject("response");
                JSONArray jsonArray = jsonObject.getJSONArray("venues");

                String id;
                String name;
                LatLng location;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject venueJsonObject = jsonArray.getJSONObject(i);
                    id = venueJsonObject.getString("id");
                    name = venueJsonObject.getString("name");
                    JSONObject locationJsonObject = venueJsonObject.getJSONObject("location");
                    location = new LatLng(locationJsonObject.getDouble("lat"), locationJsonObject.getDouble("lng"));

                    pointInfoList.add(new PointInfo(id, name, location));
                }
                listener.onSuccess(pointInfoList);
            }
            catch (JSONException e) {
                e.printStackTrace();
                listener.onError();
            }
        }
    }
    private class GetSearchForVenuesRunnable implements Runnable {
        private final LatLng ll;
        private final String intent;
        private final Integer radius;
        private final String query;
        private final Integer limit;
        private final OnFoursquareApiCompleted<List<PointInfo>> listener;

        public GetSearchForVenuesRunnable(LatLng ll, String intent, Integer radius, String query, Integer limit, OnFoursquareApiCompleted<List<PointInfo>> listener) {
            this.ll = ll;
            this.intent = intent;
            this.radius = radius;
            this.query = query;
            this.limit = limit;
            this.listener = listener;
        }

        @Override
        public void run() {
            String response = foursquareApiManager.getSearchForVenues(ll, intent, radius, query, limit);
            List<PointInfo> pointInfoList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(response).getJSONObject("response");
                JSONArray jsonArray = jsonObject.getJSONArray("venues");

                String id;
                String name;
                LatLng location;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject venueJsonObject = jsonArray.getJSONObject(i);
                    id = venueJsonObject.getString("id");
                    name = venueJsonObject.getString("name");
                    JSONObject locationJsonObject = venueJsonObject.getJSONObject("location");
                    location = new LatLng(locationJsonObject.getDouble("lat"), locationJsonObject.getDouble("lng"));

                    pointInfoList.add(new PointInfo(id, name, location));
                }
                listener.onSuccess(pointInfoList);
            }
            catch (JSONException e) {
                e.printStackTrace();
                listener.onError();
            }
        }
    }
}
