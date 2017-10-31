package com.example.dr0gi.mapspoints;

import android.graphics.Bitmap;

public class PointCategory {
    private String id;
    private String name;
    private String pluralName;
    private String shortName;
    private Bitmap icon;
    private Boolean primary;

    public PointCategory(String id, String name, String pluralName, String shortName, Bitmap icon, Boolean primary) {
        this.id = id;
        this.name = name;
        this.pluralName = pluralName;
        this.shortName = shortName;
        this.icon = icon;
        this.primary = primary;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPluralName() {
        return pluralName;
    }
    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Bitmap getIcon() {
        return icon;
    }
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Boolean getPrimary() {
        return primary;
    }
    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

}