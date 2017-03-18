package com.jfem.hackathoncarnet.carnethackathon;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jfem.hackathoncarnet.carnethackathon.controllers.DistanceController;
import com.jfem.hackathoncarnet.carnethackathon.controllers.LocationController;
import com.jfem.hackathoncarnet.carnethackathon.controllers.MicroCityController;
import com.jfem.hackathoncarnet.carnethackathon.model.Coordinates;
import com.jfem.hackathoncarnet.carnethackathon.model.DistanceInfo;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCity;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCityMarker;
import com.jfem.hackathoncarnet.carnethackathon.utils.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MainFragmentActivity extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        LocationController.OnLocationChangedListener, MicroCityController.MicroCityResolvedCallback,
        DistanceController.DistanceResolvedCallback {
    public static final String TAG = MainFragmentActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 11;
    private View baseSnackBarView;
    private View rootView;
    private LayoutInflater inflater;
    private GoogleMap mMap;
    private LinearLayout microCitiesLinearContainer;
    private MapView mapView;
    private TextView locationText;

    private Location location;
    private Snackbar snackBar;

    private MicroCityController microCityController;
    private ArrayList<MicroCityMarker> microCityMarkerArray = null;
    private LatLng endPointLatLng = null;
    private Marker markerUserLocation;
    private int numDistanceInfoRequestLeft;

    public static MainFragmentActivity newInstance() {
        return new MainFragmentActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.inflater = inflater;

        baseSnackBarView = getActivity().findViewById(R.id.drawer_layout);

        /*button.setOnClickListener(new View.OnClickListener() {
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
                        Log.e(TAG, response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);
            }
        });*/

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

        //Getting location of user
        startLocation();

        //Getting all micro cities available
        MicroCityController.microCityRequest(getContext(), this);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if (location != null && microCityMarkerArray != null && !microCityMarkerArray.isEmpty()) {
                    requestDistanceMicroCity();
                } else {
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.postDelayed(r, 250);

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
        animateCamera(latLng, 0.02d);

        markerUserLocation = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(location.getLatitude() + ", " + location.getLongitude()));

        markerUserLocation.showInfoWindow();
    }

    private void animateCamera(LatLng latLng, Double offsetLatitude) {
        if (offsetLatitude != null) {
            latLng = new LatLng(latLng.latitude + offsetLatitude, latLng.longitude);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    private void focusOnMarker(Marker marker) {
        marker.showInfoWindow();
        animateCamera(marker.getPosition(), 0.02);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker != markerUserLocation && microCityMarkerArray != null) {
            for (int i = 0; i < microCityMarkerArray.size(); ++i) {
                if (microCityMarkerArray.get(i).getMarker().getTag() == marker.getTag()) {
                    //TODO Open dialog with info instead of going
                    MicroCityMarker microCityMarkerClicked = microCityMarkerArray.get(i);
                    startNavigationToDestination(new LatLng(
                            microCityMarkerClicked.getMarker().getPosition().latitude,
                            microCityMarkerClicked.getMarker().getPosition().longitude));
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "New Location received" + location.getLatitude() + ", " + location.getLongitude());
        if (this.location == null) {
            Utility.closeSnackBar(snackBar);
        }
        this.location = location;

        setLocationReceived(location);
    }

    public void onMicroCityResolved(ArrayList<MicroCity> microCities) {
        Log.e(TAG, "onMicroCityResolved");

        //START Test purpose only
        MicroCity fakeMicroCity = new MicroCity();
        fakeMicroCity.setName("Biblioteca Rector Gabriel Ferraté");
        fakeMicroCity.setAddress("Carrer Jordi Girona, 1-3, 08034 Barcelona");
        fakeMicroCity.setCoordinates(new Coordinates(41.387614, 2.112405));
        microCities.add(fakeMicroCity);
        //END

        this.microCityMarkerArray = new ArrayList<>();
        numDistanceInfoRequestLeft = microCities.size();

        for (int i = 0; i < microCities.size(); ++i) {
            Bitmap bitmapMarker = Utility.getScaledBitmap(getContext(), R.drawable.icon_marker_microcity, 100, 100);
            bitmapMarker = Utility.addTextToBitmap(getContext(), "" + (i + 1), bitmapMarker);

            MicroCity currentMicroCity = microCities.get(i);

            final Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(microCities.get(i).getCoordinates().getLat(), microCities.get(i).getCoordinates().getLng()))
                    .title(microCities.get(i).getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapMarker))
            );
            marker.setTag(i);
            marker.setAlpha(0.7f);

            microCityMarkerArray.add(new MicroCityMarker(currentMicroCity, marker));
        }
    }

    private void requestDistanceMicroCity() {
        for (int i = 0; i < microCityMarkerArray.size(); ++i) {
            DistanceController.distanceRequest(getContext(), this.location, this.microCityMarkerArray.get(i), this);
        }
    }

    private void startNavigationToDestination(LatLng latlng) {
        this.endPointLatLng = latlng;
        String newPosition = latlng.latitude + "," + latlng.longitude;
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + newPosition);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivityForResult(mapIntent, 90);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 90:
                if (resultCode == RESULT_OK) Log.e(TAG, "Navigation ok");
                else if (resultCode == RESULT_CANCELED) Log.e(TAG, "Navigation canceled");

                double dist = Utility.distanceBetween2LatLng(this.endPointLatLng, new LatLng(this.location.getLatitude(), this.location.getLongitude()));
                if (dist < 100000) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                    builder.setTitle("Arrived at destination");
                    builder.setCancelable(true);
                    builder.setMessage("Do you want to see the available services?");
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

    @Override
    public void onDistanceResolved(DistanceInfo distanceProperties, MicroCityMarker microCityMarker) {
        Log.e(TAG, "Dist: " + distanceProperties.getDistance() + " Time: " + distanceProperties.getTime());
        --numDistanceInfoRequestLeft;

        microCityMarker.setDistance(distanceProperties.getDistance());
        microCityMarker.setTime(distanceProperties.getTime());

        if (this.numDistanceInfoRequestLeft == 0) {
            Collections.sort(microCityMarkerArray, new Comparator<MicroCityMarker>() {
                @Override
                public int compare(MicroCityMarker mcv1, MicroCityMarker mcv2) {
                    if (mcv1.getTime() > mcv2.getTime()) return 1;
                    else if (mcv1.getTime() < mcv2.getTime()) return -1;
                    else {
                        if (mcv1.getDistance() > mcv2.getDistance()) return 1;
                        else return -1;
                    }
                }
            });

            DecimalFormat df = new DecimalFormat("0.0");
            for (int i = 0; i < microCityMarkerArray.size(); ++i) {
                View mcView = inflater.inflate(R.layout.item_microcity_main_fragment, null);

                TextView cityNameText = (TextView) mcView.findViewById(R.id.item_microcity_name_text);
                TextView cityAddressText = (TextView) mcView.findViewById(R.id.item_microcity_address_text);
                TextView cityNumberText = (TextView) mcView.findViewById(R.id.item_microcity_number_text);
                TextView cityTimeText = (TextView) mcView.findViewById(R.id.item_microcity_time_text);
                TextView cityKmText = (TextView) mcView.findViewById(R.id.item_microcity_distance_text);

                cityNameText.setText(microCityMarkerArray.get(i).getMicroCity().getName());
                cityAddressText.setText(microCityMarkerArray.get(i).getMicroCity().getAddress());
                cityNumberText.setText("" + (i + 1));
                cityTimeText.setText(df.format(microCityMarkerArray.get(i).getTime()) + " min");
                cityKmText.setText(df.format(microCityMarkerArray.get(i).getDistance()) + " km");

                final int mcid = i;
                mcView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        focusOnMarker(microCityMarkerArray.get(mcid).getMarker());
                    }
                });
                mcView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        builder.setTitle("Navigate to this MicroCity?");
                        builder.setCancelable(true);
                        builder.setMessage("Do you want to navigate to this MicroCity?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startNavigationToDestination(new LatLng(
                                        microCityMarkerArray.get(mcid).getMarker().getPosition().latitude,
                                        microCityMarkerArray.get(mcid).getMarker().getPosition().longitude));
                            }
                        });
                        builder.setNegativeButton("No", null);
                        builder.show();
                        return false;
                    }
                });

                microCitiesLinearContainer.addView(mcView);
            }
        }
    }
}
