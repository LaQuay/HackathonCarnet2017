package com.jfem.hackathoncarnet.carnethackathon.model;

public class Coordinates {

    private double lng;
    private double lat;

    public double getLng ()
    {
        return lng;
    }

    public void setLng (double lng)
    {
        this.lng = lng;
    }

    public double getLat ()
    {
        return lat;
    }

    public void setLat (double lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "[lng = "+lng+", lat = "+lat+"]";
    }
}
