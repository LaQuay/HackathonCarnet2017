package com.jfem.hackathoncarnet.carnethackathon;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.jfem.hackathoncarnet.carnethackathon.controllers.LocationController;
import com.jfem.hackathoncarnet.carnethackathon.controllers.MicroCityController;
import com.jfem.hackathoncarnet.carnethackathon.model.Coordinates;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCity;
import com.jfem.hackathoncarnet.carnethackathon.utils.Utility;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MainFragmentActivity extends Fragment implements OnMapReadyCallback,
        LocationController.OnLocationChangedListener, MicroCityController.MicroCityResolvedCallback {
    public static final String TAG = MainFragmentActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 11;
    private View baseSnackBarView;
    private View rootView;
    private LayoutInflater inflater;
    private GoogleMap mMap;
    private LinearLayout microCitiesLinearContainer;
    private MapView mapView;
    private TextView locationText;

    private ImageButton button;

    private Location location;
    private Snackbar snackBar;

    private MicroCityController microCityController;
    private ArrayList<MicroCity> microCities = null;
    private LatLng endPointLatLng = null;

    public static MainFragmentActivity newInstance() {
        return new MainFragmentActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.inflater = inflater;

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
        microCityController.imageOCRRequest(this);

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
        microCitiesLinearContainer = (LinearLayout) rootView.findViewById(R.id.micro_cities_container_fragment_main);
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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_SHORT).show();

                startNavigationToDestination(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                return true;
            }
        });
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
        MicroCity fakeMicroCity = new MicroCity();
        fakeMicroCity.setName("Biblio");
        fakeMicroCity.setCoordinates(new Coordinates(41.388669, 2.112615));
        microCities.add(fakeMicroCity);
        this.microCities = microCities;

        Bitmap smallMarker =
                Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_marker_microcity),
                        100, 100, false);

        for (int i = 0; i < microCities.size(); ++i) {
            MicroCity currentMicroCity = microCities.get(i);
            Log.e(TAG, currentMicroCity.toString());

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(microCities.get(i).getCoordinates().getLat(), microCities.get(i).getCoordinates().getLng()))
                    .title(microCities.get(i).getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            );

            View microCityView = inflater.inflate(R.layout.item_microcity_main_fragment, null);

            TextView cityNameText = (TextView) microCityView.findViewById(R.id.item_microcity_name_text);
            TextView cityAddressText = (TextView) microCityView.findViewById(R.id.item_microcity_address_text);
            TextView cityNumberText = (TextView) microCityView.findViewById(R.id.item_microcity_number_text);
            TextView cityTimeText = (TextView) microCityView.findViewById(R.id.item_microcity_time_text);
            TextView cityKmText = (TextView) microCityView.findViewById(R.id.item_microcity_distance_text);

            cityNameText.setText(currentMicroCity.getName());
            cityAddressText.setText(currentMicroCity.getAddress());
            cityNumberText.setText("" + (i + 1));
            cityTimeText.setText("1 min");
            cityKmText.setText("10 km");

            microCitiesLinearContainer.addView(microCityView);
        }
    }

    private void startNavigationToDestination(LatLng latlng) {
        this.endPointLatLng = latlng;
        String newPosition = latlng.latitude + "," + latlng.longitude;
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + newPosition);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivityForResult(mapIntent,90);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 90:
                if (resultCode == RESULT_OK) Log.e(TAG, "Navigation ok");
                else if (resultCode == RESULT_CANCELED) Log.e(TAG, "Navigation canceled");

                double dist = Utility.distanceBetween2LatLng(this.endPointLatLng, new LatLng(this.location.getLatitude(),this.location.getLongitude()));
                if (dist < 100000) {
                    Toast.makeText(getActivity(), "You have arrived at your destination!!", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Arrived at destination");
                    builder.setCancelable(true);
                    builder.setMessage("Do you want to see the services available?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "Show services", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }

                break;
        }
    }
}
