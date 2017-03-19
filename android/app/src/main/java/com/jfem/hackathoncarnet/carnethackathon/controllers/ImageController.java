package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class ImageController {

    private static final String TAG = ImageController.class.getSimpleName();
    private static final String API_BASE = "/bigiot/access/services/%s/photos";

    public static void venueImageRequest(final Context context, String serviceID, final ImageView mHeader) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, String.format(API_BASE, serviceID), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Bitmap cover = getPhoto(response);
                            mHeader.setImageBitmap(cover);
                            Toast.makeText(context, cover.toString(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException | IOException e) {
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

    private static Bitmap getPhoto(JSONObject res) throws JSONException, IOException {
        URL url = new URL(res.getJSONObject("prefix") + "500x200" + res.getJSONObject("suffix"));
        return BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }

}
