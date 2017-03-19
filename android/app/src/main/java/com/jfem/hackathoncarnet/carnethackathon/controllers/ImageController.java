package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageController {

    private static final String TAG = ImageController.class.getSimpleName();
    private static final String API_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/services/%s/photos";

    public static void venueImageRequest(final Context context, String serviceID, final ImageView mHeader) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, String.format(API_BASE, serviceID), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            getPhoto(context, response, mHeader);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }
        });

        VolleyController.getInstance(context).addToQueue(jsonObjectRequest);
    }

    private static void getPhoto(final Context context, JSONObject res, final ImageView mHeader) throws JSONException {
        final String url = res.getString("prefix") + "1000x600" + res.getString("suffix");

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mHeader.setImageBitmap(response);
            }
        }, 0, 0, null, null);

        VolleyController.getInstance(context).addToQueue(imageRequest);
    }

}
