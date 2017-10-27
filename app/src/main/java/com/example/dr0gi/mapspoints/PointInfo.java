package com.example.dr0gi.mapspoints;

import com.google.android.gms.maps.model.LatLng;

class PointInfo {
    private String id;
    private LatLng coordinates;
    private String name;

    public PointInfo() {
        id = "";
        coordinates = new LatLng(0, 0);
        name = "";
    }

    PointInfo(String id, String name, LatLng coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
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
}