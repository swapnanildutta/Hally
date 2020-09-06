package com.saif.wosafe.home;

import com.google.firebase.firestore.GeoPoint;

public class GeoPointData {
    GeoPoint latLng;

    public GeoPointData(GeoPoint latLng) {
        this.latLng = latLng;
    }

    public GeoPoint getLatLng() {
        return latLng;
    }

    public void setLatLng(GeoPoint latLng) {
        this.latLng = latLng;
    }
}
