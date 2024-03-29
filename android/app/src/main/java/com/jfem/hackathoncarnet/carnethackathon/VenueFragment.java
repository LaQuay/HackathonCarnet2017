package com.jfem.hackathoncarnet.carnethackathon;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jfem.hackathoncarnet.carnethackathon.adapters.VenueAdapter;
import com.jfem.hackathoncarnet.carnethackathon.model.Venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VenueFragment extends Fragment {
    public final static String TAG = VenueFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private final static String API_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/services";

    private final static CharSequence[] categories = {"Food", "Art", "College", "Sport", "Shop", "Station"};
    private final static int[] colors = {
            Color.parseColor("#DFE9C6"),
            Color.parseColor("#FFF3BA"),
            Color.parseColor("#FFD2A7"),
            Color.parseColor("#BDDCE9"),
            Color.parseColor("#DDBFE4"),
            Color.parseColor("#EEABCA"),
            Color.parseColor("#F4828C")
    };

    private List<Venue> mData;

    public static VenueFragment newInstance(int position) {
        VenueFragment fragment = new VenueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_venue, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.filter_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Integer> selectedItems = new ArrayList<>();
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Filter by categories")
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
                                getServices(rootView, selectedItems);
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
        getServices(rootView, new ArrayList<Integer>());
        return rootView;
    }

    private void getServices(final View rootView, List<Integer> selectedItems) {
        String url = API_BASE + "?ll=41.404588,2.191274";
        if (!selectedItems.isEmpty()) {
            url += "&query=";
            for (int i = 0; i < selectedItems.size(); i++) {
                if (i != 0) url += ",";
                url += categories[selectedItems.get(i)];
            }
        }
        Log.e(TAG, url);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String venues) {
                try {
                    JSONArray venuesJSON = new JSONArray(venues);
                    for (int i = 0; i < venuesJSON.length(); i++) {
                        JSONObject venueJSON = venuesJSON.getJSONObject(i);
                        Venue venue = new Venue();
                        venue.setId(venueJSON.getString("id"));
                        venue.setName(venueJSON.getString("name"));
                        venue.setRating(venueJSON.getInt("rating"));
                        venue.setLocation(venueJSON.getJSONObject("location"));
                        venue.setCategories(venueJSON.getJSONArray("categories"));
                        venue.setUrl(venueJSON.has("url") ? venueJSON.getString("url") : "-");
                        venue.setContact(venueJSON.getJSONObject("contact"));
                        mData.add(venue);
                    }

                    RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.venues_recycler_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.setAdapter(new VenueAdapter(mData, getContext(), null));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((MainActivity) context).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
