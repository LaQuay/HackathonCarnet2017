package com.jfem.hackathoncarnet.carnethackathon;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.jfem.hackathoncarnet.carnethackathon.controllers.LocationController;

/**
 * Created by LaQuay on 19/03/2017.
 */

public class SplashScreenBackground extends AsyncTask<Void, Void, Void> {
    private static final String TAG = SplashScreenBackground.class.getSimpleName();
    private final LoadingTaskFinishedListener finishedListener;
    private Context context;

    public SplashScreenBackground(LoadingTaskFinishedListener finishedListener, Context context) {
        this.finishedListener = finishedListener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(100);
            LocationController.getInstance(context.getApplicationContext()).startLocation();

            Location location = null;
            while (location == null) {
                location = LocationController.getInstance(context.getApplicationContext()).getLastLocation();
            }
            Log.e(TAG, "New Location received" + location.getLatitude() + ", " + location.getLongitude());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void status) {
        super.onPostExecute(status);

        finishedListener.onTaskFinished();
    }

    public interface LoadingTaskFinishedListener {
        void onTaskFinished();
    }
}
