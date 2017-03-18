package com.jfem.hackathoncarnet.carnethackathon.model;

import com.google.android.gms.maps.model.Marker;

public class MicroCityView {
    private MicroCity microCity;
    private Marker marker;

    public MicroCityView(MicroCity microCity, Marker marker) {
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
}
