package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jfem.hackathoncarnet.carnethackathon.model.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServiceController {

    private static final String TAG = ServiceController.class.getSimpleName();

    private final static String API_ID_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/microcities";
    private final static String API_LOC_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/services";

    public static void serviceByMCIdRequest(Context context, final Integer idMicroCity, final ServiceController.ServiceResolvedCallback serviceResolvedCallback) {
        String url = API_ID_BASE + "/" + idMicroCity + "/services";
        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Service> serviceArray = parseServiceJSONArray(response);
                        serviceResolvedCallback.onServiceResolved(idMicroCity, serviceArray);
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

    public static void serviceByMCLocationAndQueryRequest(Context context, final Integer idMicroCity, Location location, List<String> queryItems, final ServiceController.ServiceResolvedCallback serviceResolvedCallback) {
        String url = API_LOC_BASE + "?ll=" + location.getLatitude() + "," + location.getLongitude();
        if (!queryItems.isEmpty()) {
            url += "&query=";
            for (int i = 0; i < queryItems.size(); i++) {
                if (i != 0) url += ",";
                url += queryItems.get(i);
            }
        }
        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Service> serviceArray = parseServiceJSONArray(response);
                        serviceResolvedCallback.onServiceResolved(idMicroCity, serviceArray);
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

    public static void serviceByMCLocationRequest(Context context, final Integer idMicroCity, Location location, final ServiceController.ServiceResolvedCallback serviceResolvedCallback) {
        String url = API_LOC_BASE + "?ll=" + location.getLatitude() + "," + location.getLongitude();
        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Service> serviceArray = parseServiceJSONArray(response);
                        serviceResolvedCallback.onServiceResolved(idMicroCity, serviceArray);
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
        void onServiceResolved(Integer idMicroCity, ArrayList<Service> serviceArray);
    }
}
