package com.jfem.hackathoncarnet.carnethackathon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jfem.hackathoncarnet.carnethackathon.model.Venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VenueFragment extends Fragment {

    public static final String TAG = MainFragmentActivity.class.getSimpleName();

    private final static String API_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/services";

    private List<Venue> mData;

    public static VenueFragment newInstance() {
        return new VenueFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_venue, container, false);

        String url = API_BASE + "?ll=41.404588,2.191274";
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
                        venue.setLocation(venueJSON.getJSONObject("location"));
                        venue.setCategories(venueJSON.getJSONArray("categories"));
                        mData.add(venue);
                    }

                    RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.shopping_recycler_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.setAdapter(new VenueAdapter(mData));

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

        return rootView;
    }

    private class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {

        private List<Venue> data;

        VenueAdapter(List<Venue> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_venue, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){
            try {
                Venue cardModel = data.get(position);
                final String name = cardModel.getName();
                final String address = cardModel.getLocation().getString("address");
                final String distance = cardModel.getLocation().getString("distance") + "m";
                final String coordinates = cardModel.getLocation().getString("lat") + "," + cardModel.getLocation().getString("lng");

                holder.mVenueName.setText(name);
                holder.mVenueAddress.setText(address);
                holder.mVenueDistance.setText(distance);
                holder.mVenueMaps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("geo:" + coordinates);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mVenueName, mVenueAddress, mVenueDistance;
            private ImageView mVenueMaps;

            ViewHolder(View itemView) {
                super(itemView);
                mVenueName = (TextView) itemView.findViewById(R.id.venue_name);
                mVenueAddress = (TextView) itemView.findViewById(R.id.venue_address);
                mVenueDistance = (TextView) itemView.findViewById(R.id.venue_distance);
                mVenueMaps = (ImageView) itemView.findViewById(R.id.venue_maps);
            }

        }

    }

}
