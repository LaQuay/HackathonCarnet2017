package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jfem.hackathoncarnet.carnethackathon.model.Coordinates;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCity;
import com.jfem.hackathoncarnet.carnethackathon.model.Mobility;
import com.jfem.hackathoncarnet.carnethackathon.model.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MicroCityController {
    private final String TAG = MicroCityController.class.getSimpleName();
    private final Context context;

    public MicroCityController(Context context) {
        this.context = context;
    }

    public void imageOCRRequest(final MicroCityResolvedCallback microCityResolvedCallback) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("carnet-hack.herokuapp.com")
                .appendPath("bigiot")
                .appendPath("access")
                .appendPath("microCities");
        String url = builder.build().toString();

        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<MicroCity> microCityArray = parseMicroCityJSONArray(response);
                        microCityResolvedCallback.onMicroCityResolved(microCityArray);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!" + error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        VolleyController.getInstance(context).addToQueue(jsonArrayRequest);
    }

    private ArrayList<MicroCity> parseMicroCityJSONArray(JSONArray microCityJSONArray) {
        ArrayList<MicroCity> microCitiesArray = new ArrayList<MicroCity>();

        try {
            for (int i = 0; i < microCityJSONArray.length(); ++i) {
                JSONObject microCityJSONObject = microCityJSONArray.getJSONObject(i);

                MicroCity microCity = new MicroCity();

                // Name
                microCity.setName(microCityJSONObject.getString("name"));

                // Addresss
                microCity.setAddress(microCityJSONObject.getString("address"));

                // Coordinates
                JSONObject coordinatesJSON = microCityJSONObject.getJSONObject("coordinates");
                Coordinates coordinates = new Coordinates();
                coordinates.setLat(coordinatesJSON.getDouble("lat"));
                coordinates.setLng(coordinatesJSON.getDouble("lng"));
                microCity.setCoordinates(coordinates);

                // Services
                if(microCityJSONObject.has("services")){
                    JSONObject servicesJSON = microCityJSONObject.getJSONObject("services");
                    Services services = new Services();

                    // Services - mobility
                    JSONArray mobilityJSONArray = servicesJSON.getJSONArray("mobility");
                    ArrayList<Mobility> mobilityArray = new ArrayList<Mobility>();
                    for (int j = 0; j < mobilityJSONArray.length(); ++j) {
                        JSONObject mobilityJSONObject = mobilityJSONArray.getJSONObject(j);
                        Mobility mobility = new Mobility();
                        mobility.setName(mobilityJSONObject.getString("name"));
                        mobilityArray.add(mobility);
                    }
                    services.setMobility(mobilityArray);

                    // Services - car

                    // Services - leisure

                    microCity.setServices(services);
                }

                microCitiesArray.add(microCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return microCitiesArray;
    }

    public interface MicroCityResolvedCallback {
        void onMicroCityResolved(ArrayList<MicroCity> microCityArray);
    }
}
