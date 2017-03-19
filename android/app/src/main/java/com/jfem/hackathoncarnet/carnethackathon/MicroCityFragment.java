package com.jfem.hackathoncarnet.carnethackathon;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCity;
import com.jfem.hackathoncarnet.carnethackathon.model.MicroCityView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MicroCityFragment extends Fragment {
    public final static String TAG = MicroCityFragment.class.getSimpleName();
    private final static String API_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/microcities";

    private final static CharSequence[] categories = {"Food", "Coffee", "Nightlife", "Fun", "Shopping"};
    private List<MicroCityView> mData;

    public static MicroCityFragment newInstance() {
        return new MicroCityFragment();
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
        getServices(rootView);
        return rootView;
    }

    private void getServices(final View rootView) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_BASE, new Response.Listener<String>() {
            @Override
            public void onResponse(String venues) {
                try {
                    JSONArray venuesJSON = new JSONArray(venues);
                    for (int i = 0; i < venuesJSON.length(); i++) {
                        JSONObject venueJSON = venuesJSON.getJSONObject(i);
                        MicroCity microCity = new MicroCity();
                        microCity.setId(venueJSON.getInt("id"));
                        microCity.setName(venueJSON.getString("name"));
                        microCity.setAddress(venueJSON.getString("address"));

                        MicroCityView microCityView = new MicroCityView(microCity, null);
                        microCityView.setDistance(1d);
                        microCityView.setTime(1d);
                        mData.add(microCityView);
                    }

                    RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.venues_recycler_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.setAdapter(new MicroCityViewAdapter(mData));

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

    private class MicroCityViewAdapter extends RecyclerView.Adapter<MicroCityViewAdapter.ViewHolder> {

        private List<MicroCityView> data;
        private Drawable[] drawables = {
                getResources().getDrawable(R.drawable.mc_1),
                getResources().getDrawable(R.drawable.mc_2),
                getResources().getDrawable(R.drawable.mc_3)
        };

        MicroCityViewAdapter(List<MicroCityView> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_microcity_microcity_fragment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MicroCityView microCityView = data.get(position);
            MicroCity microCity = microCityView.getMicroCity();

            holder.mMicroCityViewCover.setImageDrawable(drawables[microCity.getId() - 1]);
            holder.mMicroCityViewName.setText(microCity.getName());
            holder.mMicroCityViewAddress.setText(microCity.getAddress());
            holder.mMicroCityViewDistance.setText(microCityView.getDistance() + " km");
            holder.mMicroCityViewTime.setText(microCityView.getTime() + " m");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mMicroCityViewCover;
            private TextView mMicroCityViewName;
            private TextView mMicroCityViewAddress;
            private TextView mMicroCityViewDistance;
            private TextView mMicroCityViewTime;

            ViewHolder(View itemView) {
                super(itemView);
                mMicroCityViewCover = (ImageView) itemView.findViewById(R.id.fragment_microcity_cover_image);
                mMicroCityViewName = (TextView) itemView.findViewById(R.id.fragment_microcity_name_text);
                mMicroCityViewAddress = (TextView) itemView.findViewById(R.id.fragment_microcity_address_text);
                mMicroCityViewDistance = (TextView) itemView.findViewById(R.id.fragment_microcity_distance_text);
                mMicroCityViewTime = (TextView) itemView.findViewById(R.id.fragment_microcity_time_text);
            }
        }
    }
}
