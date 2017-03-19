package com.jfem.hackathoncarnet.carnethackathon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.jfem.hackathoncarnet.carnethackathon.controllers.ServiceController;
import com.jfem.hackathoncarnet.carnethackathon.model.Coordinates;
import com.jfem.hackathoncarnet.carnethackathon.model.DistanceInfo;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCity;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCityView;
import com.jfem.hackathoncarnet.carnethackathon.model.Service;
import com.jfem.hackathoncarnet.carnethackathon.utils.Utility;

import org.json.JSONArray;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MainFragmentActivity extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        MicroCityController.MicroCityResolvedCallback, DistanceController.DistanceResolvedCallback, ServiceController.ServiceResolvedCallback {
    public static final String TAG = MainFragmentActivity.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
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

    private ArrayList<MicroCityView> microCityViewArray = null;
    private Marker markerUserLocation;
    private int numDistanceInfoRequestLeft;

    private LatLng selectedMCLatLng = null;
    private Integer selectedMCId = -1;

    private ServiceController.ServiceResolvedCallback serviceResolvedCallback;

    public static MainFragmentActivity newInstance(int position) {
        MainFragmentActivity fragment = new MainFragmentActivity();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.inflater = inflater;

        baseSnackBarView = getActivity().findViewById(R.id.drawer_layout);

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

        serviceResolvedCallback = this;

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if (location != null && microCityViewArray != null && !microCityViewArray.isEmpty()) {
                    requestDistanceMicroCity();
                } else {
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.postDelayed(r, 250);

        // TODO: delete this
        /*
        Fragment microCityInfoFragment = MicroCityInfoFragment.newInstance(MainActivity.SECTION_MICROCITY_INFO_FRAGMENT, 1, 41.4050329, 2.19103419999999);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, microCityInfoFragment, MicroCityInfoFragment.TAG)
                .addToBackStack(null)
                .commit();
                */

        /*try {
            MicroCityConsumerBigIot.getMicrocities();
        } catch (InterruptedException | ExecutionException | IOException | AccessToNonSubscribedOfferingException | IncompleteOfferingQueryException e) {
            e.printStackTrace();
        }*/

        return rootView;
    }

    private void setUpElements() {
        mapView = (MapView) rootView.findViewById(R.id.fragment_main_map_google);
        locationText = (TextView) rootView.findViewById(R.id.fragment_main_your_location_text);
        microCitiesLinearContainer = (LinearLayout) rootView.findViewById(R.id.micro_cities_container_fragment_main);
    }

    private void setUpListeners() {

    }

    public void setUserLocation(Location location) {
        locationText.setText(Html.fromHtml("<b>Your location: </b>" + location.getLatitude() + ", " + location.getLongitude()));

        addMarkerUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void addMarkerUserLocation(LatLng latLng) {
        animateCamera(latLng, 0.02d);

        Bitmap bitmapMarker = Utility.getScaledBitmap(getContext(), R.drawable.placeholder, 100, 100);

        markerUserLocation = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(location.getLatitude() + ", " + location.getLongitude())
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapMarker))
        );
        markerUserLocation.setAlpha(0.7f);

        //markerUserLocation.showInfoWindow();
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

        //Getting location of user
        location = LocationController.getInstance(getContext().getApplicationContext()).getLastLocation();
        setUserLocation(location);

        //Getting all micro cities available
        MicroCityController.microCityRequest(getContext(), this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker != markerUserLocation && microCityViewArray != null) {
            for (int i = 0; i < microCityViewArray.size(); ++i) {
                if (microCityViewArray.get(i).getMarker().getTag() == marker.getTag()) {
                    //TODO Open dialog with info instead of going
                    focusOnMarker(microCityViewArray.get(i).getMarker());
                    break;
                }
            }
        }

        return true;
    }

    public void onMicroCityResolved(ArrayList<MicroCity> microCities) {
        Log.e(TAG, "onMicroCityResolved");

        //START Test purpose only
        MicroCity fakeMicroCity = new MicroCity();
        fakeMicroCity.setName("Biblioteca Rector Gabriel Ferraté");
        fakeMicroCity.setAddress("Carrer Jordi Girona, 1-3, 08034 Barcelona");
        fakeMicroCity.setCoordinates(new Coordinates(41.387614, 2.112405));
        //microCities.add(fakeMicroCity);
        //END

        this.microCityViewArray = new ArrayList<>();
        numDistanceInfoRequestLeft = microCities.size();

        for (int i = 0; i < microCities.size(); ++i) {
            MicroCity currentMicroCity = microCities.get(i);

            final Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(microCities.get(i).getCoordinates().getLat(), microCities.get(i).getCoordinates().getLng()))
                    .title(microCities.get(i).getName())
            );
            marker.setAlpha(0.7f);

            microCityViewArray.add(new MicroCityView(currentMicroCity, marker));
        }
    }

    private void requestDistanceMicroCity() {
        for (int i = 0; i < microCityViewArray.size(); ++i) {
            DistanceController.distanceRequest(getContext(), this.location, this.microCityViewArray.get(i), this);
        }
    }

    private void startNavigationToDestination(LatLng latlng, Integer mcId) {
        this.selectedMCLatLng = latlng;
        this.selectedMCId = mcId;
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

                double dist = Utility.distanceBetween2LatLng(this.selectedMCLatLng, new LatLng(this.location.getLatitude(), this.location.getLongitude()));
                if (dist < 100000) {
                    final Integer selMCId = this.selectedMCId;
                    final LatLng selMCLoc = this.selectedMCLatLng;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                    builder.setTitle("Arrived at destination");
                    builder.setCancelable(true);
                    builder.setMessage("Do you want to see the available services?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Fragment microCityInfoFragment = MicroCityInfoFragment.newInstance(MainActivity.SECTION_MICROCITY_INFO_FRAGMENT, selMCId, selMCLoc.latitude, selMCLoc.longitude);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_container, microCityInfoFragment, MicroCityInfoFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }
                break;
        }
    }

    public void onDistanceResolved(DistanceInfo distanceProperties, MicroCityView microCityMarker) {
        --numDistanceInfoRequestLeft;

        microCityMarker.setDistance(distanceProperties.getDistance());
        microCityMarker.setTime(distanceProperties.getTime());

        if (this.numDistanceInfoRequestLeft == 0) {
            microCityViewArray = Utility.sortMicroCityByDistanceTime(microCityViewArray);

            for (int i = 0; i < microCityViewArray.size(); ++i) {
                View mcView = inflater.inflate(R.layout.item_microcity_main_fragment, null);

                TextView cityNameText = (TextView) mcView.findViewById(R.id.item_microcity_name_text);
                TextView cityAddressText = (TextView) mcView.findViewById(R.id.item_microcity_address_text);
                TextView cityNumberText = (TextView) mcView.findViewById(R.id.item_microcity_number_text);
                TextView cityTimeText = (TextView) mcView.findViewById(R.id.item_microcity_time_text);
                TextView cityKmText = (TextView) mcView.findViewById(R.id.item_microcity_distance_text);
                ImageButton cityServicesButton = (ImageButton) mcView.findViewById(R.id.item_microcity_services_button);

                cityNameText.setText(microCityViewArray.get(i).getMicroCity().getName());
                cityAddressText.setText(microCityViewArray.get(i).getMicroCity().getAddress());
                cityNumberText.setText("" + (i + 1));
                cityTimeText.setText(Utility.decimalFormat(microCityViewArray.get(i).getTime()) + " min");
                cityKmText.setText(Utility.decimalFormat(microCityViewArray.get(i).getDistance()) + " km");

                Bitmap bitmapMarker = Utility.getScaledBitmap(getContext(), R.drawable.icon_marker_microcity, 100, 100);
                bitmapMarker = Utility.addTextToBitmap(getContext(), "" + (i + 1), bitmapMarker);
                microCityViewArray.get(i).getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(bitmapMarker));
                microCityViewArray.get(i).getMarker().setTag(i);

                final int mcid = i;
                mcView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        focusOnMarker(microCityViewArray.get(mcid).getMarker());
                    }
                });
                mcView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        builder.setTitle("Navigate to this MicroCity?");
                        builder.setCancelable(true);
                        builder.setMessage("Do you want to navigate to the MicroCity " +
                                microCityViewArray.get(mcid).getMicroCity().getName() + "?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startNavigationToDestination(new LatLng(
                                                microCityViewArray.get(mcid).getMarker().getPosition().latitude,
                                                microCityViewArray.get(mcid).getMarker().getPosition().longitude),
                                        microCityViewArray.get(mcid).getMicroCity().getId());
                            }
                        });
                        builder.setNegativeButton("No", null);
                        builder.show();
                        return false;
                    }
                });
                cityServicesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (microCityViewArray.get(mcid).getMicroCity().getId() == null) {
                            Location loc = new Location("pp");
                            loc.setLatitude(microCityViewArray.get(mcid).getMicroCity().getCoordinates().getLat());
                            loc.setLongitude(microCityViewArray.get(mcid).getMicroCity().getCoordinates().getLng());
                            ServiceController.serviceByMCLocationRequest(getContext(), microCityViewArray.get(mcid).getMicroCity().getId(), loc, serviceResolvedCallback);
                        } else {
                            ServiceController.serviceByMCIdRequest(getContext(), microCityViewArray.get(mcid).getMicroCity().getId(), serviceResolvedCallback);
                        }
                    }
                });

                microCitiesLinearContainer.addView(mcView);
            }
        }
    }

    @Override
    public void onServiceResolved(final Integer idMicroCity, ArrayList<Service> serviceArray) {
        String mss = "";
        mss += "<b>Restaurants</b><br>";
        for (int i = 0; i < serviceArray.size(); ++i) {
            //Log.e(TAG, serviceArray.get(i).getName());
            JSONArray cat = serviceArray.get(i).getCategories();
            if (cat.length() > 0) {
                if (cat.toString().contains("Restaurant"))
                    mss += serviceArray.get(i).getName() + "<br>";
            }
        }
        mss += "<br>";
        mss += "<b>Shops</b><br>";
        for (int i = 0; i < serviceArray.size(); ++i) {
            //Log.e(TAG, serviceArray.get(i).getName());
            JSONArray cat = serviceArray.get(i).getCategories();
            if (cat.length() > 0) {
                if ((cat.toString().contains("Shop") || cat.toString().contains("Store")) &&
                        !cat.toString().contains("Shopping Mall")) {
                    mss += serviceArray.get(i).getName() + "<br>";
                }
            }
        }
        int posMicroCity = -1;
        for (int i = 0; i < microCityViewArray.size(); ++i) {
            if (microCityViewArray.get(i).getMicroCity().getId() == idMicroCity) {
                posMicroCity = i;
                break;
            }
        }
        if (posMicroCity >= 0) {
            final int posMC = posMicroCity;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
            builder.setTitle("Services in " + microCityViewArray.get(posMC).getMicroCity().getName());
            builder.setCancelable(true);
            builder.setMessage(Html.fromHtml(mss));
            builder.setPositiveButton("Navigate", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startNavigationToDestination(microCityViewArray.get(posMC).getMicroCity().getCoordinates().getLatLng(),
                            microCityViewArray.get(posMC).getMicroCity().getId());
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((MainActivity) context).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
