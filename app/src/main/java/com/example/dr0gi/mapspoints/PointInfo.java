package com.example.dr0gi.mapspoints;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

class PointInfo {
    private String id;
    private LatLng coordinates;
    private String name;
    private PointCategory category;

    public PointInfo(String id, LatLng coordinates, String name, PointCategory category) {
        this.id = id;
        this.coordinates = coordinates;
        this.name = name;
        this.category = category;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public PointCategory getCategory() {
        return category;
    }
    public void setCategory(PointCategory category) {
        this.category = category;
    }
}