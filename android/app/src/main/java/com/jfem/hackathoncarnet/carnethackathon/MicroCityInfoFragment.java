package com.jfem.hackathoncarnet.carnethackathon;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jfem.hackathoncarnet.carnethackathon.adapters.VenueAdapter;
import com.jfem.hackathoncarnet.carnethackathon.controllers.ServiceController;
import com.jfem.hackathoncarnet.carnethackathon.model.Service;
import com.jfem.hackathoncarnet.carnethackathon.model.Venue;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MicroCityInfoFragment extends Fragment implements ServiceController.ServiceResolvedCallback,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public final static String TAG = MicroCityInfoFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_ID_MICROCITY = "microcity";
    private static final String ARG_LAT_MICROCITY = "latitude";
    private static final String ARG_LNG_MICROCITY = "longitude";
    private final static String API_BASE = "";
    private static final int DEFAULT_ZOOM = 11;
    private final static CharSequence[] categories = {"Food", "Coffee", "Nightlife", "Fun", "Shopping"};
    private View rootView;
    private MapView mapView;
    private GoogleMap mMap;
    private ArrayList<Marker> servicesMarkers;
    private List<Venue> mData;
    private int idMicroCity;
    private Location locMicroCity;

    private ServiceController.ServiceResolvedCallback serviceResolvedCallback;

    public static MicroCityInfoFragment newInstance(int position, int idMicroCity, double latMicroCity, double lngMicroCity) {
        MicroCityInfoFragment fragment = new MicroCityInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position);
        args.putInt(ARG_ID_MICROCITY, idMicroCity);
        args.putDouble(ARG_LAT_MICROCITY, latMicroCity);
        args.putDouble(ARG_LNG_MICROCITY, lngMicroCity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
        this.idMicroCity = getArguments().getInt(ARG_ID_MICROCITY);

        Location loc = new Location("pp");
        loc.setLatitude(getArguments().getDouble(ARG_LAT_MICROCITY));
        loc.setLongitude(getArguments().getDouble(ARG_LNG_MICROCITY));
        this.locMicroCity = loc;

        Log.e(TAG, "ID-MC: " + this.idMicroCity + " loc: " + this.locMicroCity);

        serviceResolvedCallback = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_microcity_info, container, false);
        mapView = (MapView) rootView.findViewById(R.id.fragment_mc_info_map_google);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.filter_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> selectedItems = new ArrayList<>();
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Filter by categories")
                        .setMultiChoiceItems(categories, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    selectedItems.add(categories[indexSelected].toString());
                                } else if (selectedItems.contains(categories[indexSelected].toString())) {
                                    selectedItems.remove(Integer.valueOf(indexSelected));
                                }
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mData = new ArrayList<>();
                                getServices(selectedItems);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //  Your code when user clicked on Cancel
                            }
                        }).create();
                dialog.show();
            }
        });
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);
        getServices(new ArrayList<String>());
        return rootView;
    }

    private void getServices(List<String> selectedItems) {
        //ServiceController.serviceByMCIdRequest(getContext(), this.idMicroCity, serviceResolvedCallback);
        ServiceController.serviceByMCLocationAndQueryRequest(getContext(), this.idMicroCity, this.locMicroCity, selectedItems, serviceResolvedCallback);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((MainActivity) context).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onServiceResolved(Integer idMicroCity, ArrayList<Service> serviceArray) {
        for (int i = 0; i < serviceArray.size(); ++i) {
            Venue venue = new Venue();
            venue.setId(serviceArray.get(i).getId());
            venue.setName(serviceArray.get(i).getName());
            venue.setLocation(serviceArray.get(i).getLocation());
            venue.setCategories(serviceArray.get(i).getCategories());
            mData.add(venue);
        }
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_mc_info_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new VenueAdapter(mData, getContext(), new VenueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Venue item) {
                try {
                    double lat = Double.parseDouble(item.getLocation().getString("lat"));
                    double lng = Double.parseDouble(item.getLocation().getString("lng"));

                    Marker marker = null;
                    for (int i = 0; i < servicesMarkers.size(); ++i) {
                        Marker currentMarker = servicesMarkers.get(i);
                        if (lat == currentMarker.getPosition().latitude &&
                                lng == currentMarker.getPosition().longitude) {
                            marker = currentMarker;
                        }
                    }
                    if (marker != null) {
                        focusOnMarker(marker);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));

        addServicesMarkers(serviceArray);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMarkerClickListener(this);
    }

    private void addServicesMarkers(ArrayList<Service> services) {
        try {
            servicesMarkers = new ArrayList<>();
            for (int i = 0; i < services.size(); ++i) {
                Service currentService = services.get(i);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(currentService.getLocation().getDouble("lat"), currentService.getLocation().getDouble("lng")))
                        .title(currentService.getName())
                );
                marker.setTag(i);
                marker.setAlpha(0.7f);
                servicesMarkers.add(marker);
            }

            moveCameraToShowAllMarkers();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void moveCameraToShowAllMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : servicesMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private void focusOnMarker(Marker marker) {
        marker.showInfoWindow();
        animateCamera(marker.getPosition(), null);
    }

    private void animateCamera(LatLng latLng, Double offsetLatitude) {
        if (offsetLatitude != null) {
            latLng = new LatLng(latLng.latitude + offsetLatitude, latLng.longitude);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (servicesMarkers != null) {
            for (int i = 0; i < servicesMarkers.size(); ++i) {
                if (servicesMarkers.get(i).getTag() == marker.getTag()) {
                    //TODO Open dialog with info instead of going
                    focusOnMarker(servicesMarkers.get(i));
                    break;
                }
            }
        }
        return true;
    }
}
