package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jfem.hackathoncarnet.carnethackathon.R;
import com.jfem.hackathoncarnet.carnethackathon.model.DistanceInfo;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCityMarker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DistanceController {
    private static final String TAG = DistanceController.class.getSimpleName();

    public static void distanceRequest(Context context, Location baseLocation, final MicroCityMarker microCityMarker, final DistanceController.DistanceResolvedCallback distanceResolvedCallback) {
        double markerLat = microCityMarker.getMarker().getPosition().latitude;
        double markerLon = microCityMarker.getMarker().getPosition().longitude;

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s,%s&destinations=%s,%s&key=%s";

        url = String.format(url, baseLocation.getLatitude(), baseLocation.getLongitude(),
                markerLat, markerLon, context.getResources().getString(R.string.google_maps_key));

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        DistanceInfo distanceProperties = parseDistanceJSONArray(response);
                        distanceResolvedCallback.onDistanceResolved(distanceProperties, microCityMarker);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        VolleyController.getInstance(context).addToQueue(jsonObjectRequest);
    }

    private static DistanceInfo parseDistanceJSONArray(JSONObject microCityJSONArray) {
        try {
            DistanceInfo distanceProperties = new DistanceInfo();
            JSONArray rowsArray = microCityJSONArray.getJSONArray("rows");
            if (rowsArray.length() > 0) {
                JSONArray elementsArray = rowsArray.getJSONObject(0).getJSONArray("elements");
                if (elementsArray.length() > 0) {
                    double dist = elementsArray.getJSONObject(0).getJSONObject("distance").getDouble("value") / 1000.0 ;
                    double time = elementsArray.getJSONObject(0).getJSONObject("duration").getDouble("value") / 60.0;
                    distanceProperties.setDistance(dist);
                    distanceProperties.setTime(time);

                    return distanceProperties;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface DistanceResolvedCallback {
        void onDistanceResolved(DistanceInfo distanceProperties, MicroCityMarker microCityMarker);
    }
}
