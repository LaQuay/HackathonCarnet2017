package com.jfem.hackathoncarnet.carnethackathon.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jfem.hackathoncarnet.carnethackathon.R;
import com.jfem.hackathoncarnet.carnethackathon.model.Venue;

import org.json.JSONException;

import java.util.List;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {

    private List<Venue> data;
    private Context context;

    public VenueAdapter(List<Venue> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_microcity_venue_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
                    context.startActivity(mapIntent);
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