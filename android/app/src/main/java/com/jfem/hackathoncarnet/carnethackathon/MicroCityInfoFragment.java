package com.jfem.hackathoncarnet.carnethackathon;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

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
    private final static String API_BASE = "";

    private View rootView;
    private MapView mapView;
    private GoogleMap mMap;
    private TextView locationText;

    private final static CharSequence[] categories = {"Food", "Coffee", "Nightlife", "Fun", "Shopping"};
    private List<Venue> mData;
    private int idMicroCity;

    private ArrayList<Marker> servicesMarkers;

    private ServiceController.ServiceResolvedCallback serviceResolvedCallback;

    public static MicroCityInfoFragment newInstance(int position, int idMicroCity) {
        MicroCityInfoFragment fragment = new MicroCityInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position);
        args.putInt(ARG_ID_MICROCITY, idMicroCity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
        this.idMicroCity = getArguments().getInt(ARG_ID_MICROCITY);
        Log.e(TAG, "ID-MC: " + this.idMicroCity);

        serviceResolvedCallback = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_microcity_info, container, false);
        mapView = (MapView) rootView.findViewById(R.id.fragment_mc_info_map_google);
        locationText = (TextView) rootView.findViewById(R.id.fragment_mc_info_your_location_text);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.filter_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Integer> selectedItems = new ArrayList<>();
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Select The Difficulty Level")
                        .setMultiChoiceItems(categories, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    selectedItems.add(indexSelected);
                                } else if (selectedItems.contains(indexSelected)) {
                                    selectedItems.remove(Integer.valueOf(indexSelected));
                                }
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mData = new ArrayList<>();
                                getServices(rootView);
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
        getServices(rootView);
        return rootView;
    }

    private void getServices(final View rootView) {
        ServiceController.serviceByMCIdRequest(getContext(), this.idMicroCity, serviceResolvedCallback);
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
        mRecyclerView.setAdapter(new VenueAdapter(mData, getContext()));

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

                final Marker marker;
                    marker = mMap.addMarker(new MarkerOptions()
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom));
    }



    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.e(TAG, "CLICK");
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
