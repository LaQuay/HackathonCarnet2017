package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jfem.hackathoncarnet.carnethackathon.model.Coordinates;
import com.jfem.hackathoncarnet.carnethackathon.model.Discount;
import com.jfem.hackathoncarnet.carnethackathon.model.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francescdepuigguixe on 19/3/17.
 */

public class DiscountController {

    private static final String TAG = DiscountController.class.getSimpleName();

    private final static String API_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/promotions";

    public static void discountsRequest(Context context, final DiscountController.DiscountResolvedCallback discountResolvedCallback) {
        String url = API_BASE;
        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Discount> discountArray = parseDiscountJSONArray(response);
                        discountResolvedCallback.onDiscountResolved(discountArray);
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


    private static ArrayList<Discount> parseDiscountJSONArray(JSONArray discountJSONArray) {
        ArrayList<Discount> discountArray = new ArrayList<>();

        try {
            for (int i = 0; i < discountJSONArray.length(); i++) {
                JSONObject discountJSON = discountJSONArray.getJSONObject(i);
                Discount discount = new Discount();
                discount.setDiscount(discountJSON.getString("discount"));
                discount.setMicrocityId(discountJSON.getInt("microcity.id"));
                discount.setServiceName(discountJSON.getString("service.name"));
                discount.setServicePosition(new Coordinates(discountJSON.getLong("service.location.lat"),discountJSON.getLong("service.location.lng")));
                discount.setServiceCategoryName(discountJSON.getJSONArray("service.categories"));
                discountArray.add(discount);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return discountArray;
    }

    public interface DiscountResolvedCallback {
        void onDiscountResolved( ArrayList<Discount> discountArray);
    }

}
