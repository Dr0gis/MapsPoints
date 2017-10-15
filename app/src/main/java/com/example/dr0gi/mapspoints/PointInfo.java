package com.example.dr0gi.mapspoints;

import com.google.android.gms.maps.model.LatLng;

public class PointInfo {
    private LatLng coordinats;
    private String name;

    public PointInfo() {
        coordinats = new LatLng(0, 0);
        name = "";
    }

    public PointInfo(String name, LatLng coordinats) {
        this.name = name;
        this.coordinats = coordinats;
    }

    public LatLng getCoordinats() {
        return coordinats;
    }
    public void setCoordinats(LatLng coordinats) {
        this.coordinats = coordinats;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
