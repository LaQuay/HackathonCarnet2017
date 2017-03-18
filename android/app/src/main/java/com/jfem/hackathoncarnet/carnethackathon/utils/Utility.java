package com.jfem.hackathoncarnet.carnethackathon.utils;

import android.location.Location;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by LaQuay on 18/03/2017.
 */

public class Utility {
    public static Snackbar showSnackBar(View view, String text, String actionText, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.setAction(actionText, onClickListener);
        snackbar.show();

        return snackbar;
    }

    public static void closeSnackBar(Snackbar snackbar) {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public static double distanceBetween2LatLng(LatLng latLngInit, LatLng latLngEnd) {
        float [] dist = new float[1];
        Location.distanceBetween(latLngInit.latitude, latLngInit.longitude, latLngEnd.latitude, latLngEnd.longitude, dist);
        return dist[0];
    }
}
