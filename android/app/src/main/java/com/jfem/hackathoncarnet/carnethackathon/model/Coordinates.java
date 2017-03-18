package com.jfem.hackathoncarnet.carnethackathon.model;

public class Coordinates {

    private double lng;
    private double lat;

    public Coordinates(double lat, double lng) {this.lat = lat; this.lng = lng;}

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLatLng (double lat, double lng) {setLat(lat);setLng(lng);}

    @Override
    public String toString() {
        return "[lng = " + lng + ", lat = " + lat + "]";
    }
}
