package com.jfem.hackathoncarnet.carnethackathon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jfem.hackathoncarnet.carnethackathon.controllers.MicroCityController;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCity;
import com.jfem.hackathoncarnet.carnethackathon.controllers.LocationController;
import com.jfem.hackathoncarnet.carnethackathon.utils.Utility;


import java.util.ArrayList;

public class MainFragmentActivity extends Fragment implements OnMapReadyCallback,
        LocationController.OnLocationChangedListener, MicroCityController.MicroCityResolvedCallback {
    public static final String TAG = MainFragmentActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 11;
    private View baseSnackBarView;
    private View rootView;
    private GoogleMap mMap;
    private MapView mapView;
    private TextView locationText;

    private ImageButton button;

    private Location location;
    private Snackbar snackBar;

    private MicroCityController microCityController;
    private final MicroCityController.MicroCityResolvedCallback microCityResolvedCallback = this;
    private ArrayList<MicroCity> microCities = null;

    public static MainFragmentActivity newInstance() {
        return new MainFragmentActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        baseSnackBarView = getActivity().findViewById(R.id.drawer_layout);

        button = (ImageButton) rootView.findViewById(R.id.search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s,%s&destinations=%s,%s&key=%s";
                url = String.format(url, location.getLatitude(), location.getLongitude(),
                        "41.4050329", "2.1910341999999", getResources().getString(R.string.google_maps_key));
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);
            }
        });

        setUpElements();
        setUpListeners();

        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        microCityController = new MicroCityController(getContext());
        microCityController.imageOCRRequest(microCityResolvedCallback);

        startLocation();

        /*try {
            MicroCityConsumerBigIot.getMicrocities();
        } catch (InterruptedException | ExecutionException | IOException | AccessToNonSubscribedOfferingException | IncompleteOfferingQueryException e) {
            e.printStackTrace();
        }*/

        return rootView;

    }

    private void startLocation() {
        snackBar = Utility.showSnackBar(baseSnackBarView, "Finding your position...", "action", null);
        LocationController.getInstance(getContext().getApplicationContext()).startLocation(this);
    }

    private void setUpElements() {
        mapView = (MapView) rootView.findViewById(R.id.fragment_main_map_google);
        locationText = (TextView) rootView.findViewById(R.id.fragment_main_your_location_text);
    }

    private void setUpListeners() {

    }

    public void setLocationReceived(Location location) {
        locationText.setText(location.getLatitude() + ", " + location.getLongitude());

        addMarkerUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void addMarkerUserLocation(LatLng latLng) {
        LatLng modLatLng = new LatLng(latLng.latitude + 0.02, latLng.longitude);
        animateCamera(modLatLng);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(location.getLatitude() + ", " + location.getLongitude()));

        marker.showInfoWindow();
    }

    private void animateCamera(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "New Location received" + location.getLatitude() + ", " + location.getLongitude());
        this.location = location;

        setLocationReceived(location);

        if (snackBar != null) {
            Utility.closeSnackBar(snackBar);
            snackBar = null;
        }
    }

    public void onMicroCityResolved(ArrayList<MicroCity> microCities) {
        Log.e(TAG, "onMicroCityResolved");
        this.microCities = microCities;

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_mc);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

        for (int i = 0; i < microCities.size(); ++i) {
            Log.e(TAG, microCities.get(i).toString());

            LatLng latLng = new LatLng(microCities.get(i).getCoordinates().getLat(), microCities.get(i).getCoordinates().getLng());

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(microCities.get(i).getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            );

        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_SHORT).show();
                //marker.showInfoWindow();
                startNavigationToDestination(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));
                return true;
            }
        });

        Toast.makeText(getActivity(), "MicroCities", Toast.LENGTH_SHORT).show();
    }

    private void startNavigationToDestination(LatLng latlng) {
        String newPosition = latlng.latitude + "," + latlng.longitude;
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + newPosition);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
