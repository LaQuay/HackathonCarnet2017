package com.jfem.hackathoncarnet.carnethackathon;

import android.content.DialogInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MicroCityFragment extends Fragment {

    public final static String TAG = MainFragmentActivity.class.getSimpleName();
    private final static String API_BASE = "https://carnet-hack.herokuapp.com/bigiot/access/microcitieslist";

    private final static CharSequence[] categories = {"Food", "Coffee", "Nightlife", "Fun", "Shopping"};

    private List<MicroCity> mData;

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
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String venues) {
                try {
                    JSONArray venuesJSON = new JSONArray(venues);
                    for (int i = 0; i < venuesJSON.length(); i++) {
                        JSONObject venueJSON = venuesJSON.getJSONObject(i);
                        MicroCity microCity = new MicroCity();
                        microCity.setName(venueJSON.getString("name"));
                        mData.add(microCity);
                    }

                    RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.venues_recycler_view);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.setAdapter(new MicroCityAdapter(mData));

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

    private class MicroCityAdapter extends RecyclerView.Adapter<MicroCityAdapter.ViewHolder> {

        private List<MicroCity> data;

        MicroCityAdapter(List<MicroCity> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_microcity, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MicroCity cardModel = data.get(position);
            holder.mVenueName.setText(cardModel.getName());
            holder.mMicroCityCover.setImageDrawable(getResources().getDrawable(R.drawable.mc_2));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mVenueName;
            private ImageView mMicroCityCover;

            ViewHolder(View itemView) {
                super(itemView);
                mVenueName = (TextView) itemView.findViewById(R.id.microcity_name);
                mMicroCityCover = (ImageView) itemView.findViewById(R.id.microcity_cover);
            }

        }

    }

}
