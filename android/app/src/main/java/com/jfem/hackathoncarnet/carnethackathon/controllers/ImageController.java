package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
        final String url = res.getString("prefix") + "500x200" + res.getString("suffix");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("ASD", url);
                        byte[] decodedString = Base64.decode(response, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        mHeader.setImageBitmap(bitmap);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "octet/stream");
                headers.put("Content-Transfer-Encoding", "base64");
                return headers;
            }
        };

        VolleyController.getInstance(context).addToQueue(stringRequest);
    }

}
