package com.jfem.hackathoncarnet.carnethackathon;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.jfem.hackathoncarnet.carnethackathon.controllers.LocationController;

/**
 * Created by LaQuay on 19/03/2017.
 */

public class SplashScreenActivity extends Activity implements SplashScreenBackground.LoadingTaskFinishedListener {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    private View parentLayout;

    @Override
    public void onTaskFinished() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen_activity);
        parentLayout = findViewById(R.id.splash_screen_parent_layout);

        boolean locationServiceAvailable = LocationController.getInstance(this).checkLocationServiceAvailable();
        if (locationServiceAvailable) {
            new SplashScreenBackground(this, this).execute();
        } else {
            requestLocationPermissions();
        }
    }

    public void requestLocationPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new SplashScreenBackground(this, this).execute();
                } else {
                    finish();
                }
            }
        }
    }
}
