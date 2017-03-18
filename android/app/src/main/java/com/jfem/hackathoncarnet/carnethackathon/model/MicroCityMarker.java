package com.jfem.hackathoncarnet.carnethackathon.model;

import com.google.android.gms.maps.model.Marker;

public class MicroCityMarker {
    private MicroCity microCity;
    private Marker marker;
    private double time;
    private double distance;

    public MicroCityMarker(MicroCity microCity, Marker marker) {
        this.microCity = microCity;
        this.marker = marker;
    }

    public MicroCity getMicroCity() {
        return microCity;
    }

    public void setMicroCity(MicroCity microCity) {
        this.microCity = microCity;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
