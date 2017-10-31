package com.example.dr0gi.mapspoints;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject venueJsonObject = jsonArray.getJSONObject(i);

                    String idVenue = venueJsonObject.getString("id");
                    String nameVenue = venueJsonObject.getString("name");

                    JSONObject locationJsonObject = venueJsonObject.getJSONObject("location");
                    LatLng locationVenue = new LatLng(locationJsonObject.getDouble("lat"), locationJsonObject.getDouble("lng"));

                    JSONArray categoriesJsonArray = venueJsonObject.getJSONArray("categories");
                    JSONObject categoriesJsonObject = categoriesJsonArray.getJSONObject(0);
                    String idCategories = categoriesJsonObject.getString("id");
                    String nameCategories = categoriesJsonObject.getString("name");
                    String pluralNameCategories = categoriesJsonObject.getString("pluralName");
                    String shortNameCategories = categoriesJsonObject.getString("shortName");
                    JSONObject iconCategoriesJsonObject = categoriesJsonObject.getJSONObject("icon");
                    String prefixIconCategories = iconCategoriesJsonObject.getString("prefix");
                    String suffixIconCategories = iconCategoriesJsonObject.getString("suffix");
                    URL urlIconCategories = new URL(prefixIconCategories + "88" + suffixIconCategories);
                    Bitmap iconCategories = BitmapFactory.decodeStream(urlIconCategories.openConnection().getInputStream());
                    Boolean primaryCategories = categoriesJsonObject.getBoolean("primary");

                    PointCategory pointCategory = new PointCategory(idCategories, nameCategories, pluralNameCategories, shortNameCategories, iconCategories, primaryCategories);

                    PointInfo pointInfo = new PointInfo(idVenue, locationVenue, nameVenue, pointCategory);

                    pointInfoList.add(pointInfo);
                }
                listener.onSuccess(pointInfoList);
            }
            catch (JSONException e) {
                e.printStackTrace();
                listener.onError();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject venueJsonObject = jsonArray.getJSONObject(i);

                    String idVenue = venueJsonObject.getString("id");
                    String nameVenue = venueJsonObject.getString("name");

                    JSONObject locationJsonObject = venueJsonObject.getJSONObject("location");
                    LatLng locationVenue = new LatLng(locationJsonObject.getDouble("lat"), locationJsonObject.getDouble("lng"));

                    JSONArray categoriesJsonArray = venueJsonObject.getJSONArray("categories");
                    JSONObject categoriesJsonObject = categoriesJsonArray.getJSONObject(0);
                    String idCategories = categoriesJsonObject.getString("id");
                    String nameCategories = categoriesJsonObject.getString("name");
                    String pluralNameCategories = categoriesJsonObject.getString("pluralName");
                    String shortNameCategories = categoriesJsonObject.getString("shortName");
                    JSONObject iconCategoriesJsonObject = categoriesJsonObject.getJSONObject("icon");
                    String prefixIconCategories = iconCategoriesJsonObject.getString("prefix");
                    String suffixIconCategories = iconCategoriesJsonObject.getString("suffix");
                    URL urlIconCategories = new URL(prefixIconCategories + "64" + suffixIconCategories);
                    Bitmap iconCategories = BitmapFactory.decodeStream(urlIconCategories.openConnection().getInputStream());
                    Boolean primaryCategories = categoriesJsonObject.getBoolean("primary");

                    PointCategory pointCategory = new PointCategory(idCategories, nameCategories, pluralNameCategories, shortNameCategories, iconCategories, primaryCategories);
                    PointInfo pointInfo = new PointInfo(idVenue, locationVenue, nameVenue, pointCategory);

                    pointInfoList.add(pointInfo);
                }
                listener.onSuccess(pointInfoList);
            }
            catch (JSONException e) {
                e.printStackTrace();
                listener.onError();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
