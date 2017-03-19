package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jfem.hackathoncarnet.carnethackathon.R;
import com.jfem.hackathoncarnet.carnethackathon.VenueFragment;
import com.jfem.hackathoncarnet.carnethackathon.model.DistanceInfo;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCityMarker;
import com.jfem.hackathoncarnet.carnethackathon.model.Service;
import com.jfem.hackathoncarnet.carnethackathon.model.Venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServiceController {

    private static final String TAG = ServiceController.class.getSimpleName();

    private final static String API_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/microcities";

    public static void serviceRequest(Context context, Integer idMC, final AlertDialog.Builder builder, final ServiceController.ServiceResolvedCallback serviceResolvedCallback) {
        String url = API_BASE + "/" + idMC + "/services";
        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Service> serviceArray = parseServiceJSONArray(response);
                        serviceResolvedCallback.onServiceResolved(serviceArray, builder);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        VolleyController.getInstance(context).addToQueue(jsonArrayRequest);
    }

    private static ArrayList<Service> parseServiceJSONArray(JSONArray serviceJSONArray) {
        ArrayList<Service> serviceArray = new ArrayList<Service>();

        try {
            for (int i = 0; i < serviceJSONArray.length(); i++) {
                JSONObject serviceJSON = null;
                    serviceJSON = serviceJSONArray.getJSONObject(i);
                Service service = new Service();
                service.setId(serviceJSON.getString("id"));
                service.setName(serviceJSON.getString("name"));
                service.setLocation(serviceJSON.getJSONObject("location"));
                service.setCategories(serviceJSON.getJSONArray("categories"));
                serviceArray.add(service);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return serviceArray;
    }

    public interface ServiceResolvedCallback {
        void onServiceResolved(ArrayList<Service> serviceArray, AlertDialog.Builder builder);
    }
}
