package com.jfem.hackathoncarnet.carnethackathon.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jfem.hackathoncarnet.carnethackathon.R;
import com.jfem.hackathoncarnet.carnethackathon.model.Venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {

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
    private final OnItemClickListener listener;
    private List<Venue> data;
    private Context context;
    private Drawable[] icons;

    public VenueAdapter(List<Venue> data, Context context, OnItemClickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;
        icons = new Drawable[]{
                context.getResources().getDrawable(R.drawable.ic_restaurant),
                context.getResources().getDrawable(R.drawable.ic_music_note),
                context.getResources().getDrawable(R.drawable.ic_school),
                context.getResources().getDrawable(R.drawable.ic_fitness_center),
                context.getResources().getDrawable(R.drawable.ic_shopping_cart),
                context.getResources().getDrawable(R.drawable.ic_directions_bus),
                context.getResources().getDrawable(R.drawable.ic_star)
        };
    }

    private int getCategoryColor(JSONArray categories) throws JSONException {
        for (int i = 0; i < categories.length(); i++) {
            JSONObject category = categories.getJSONObject(i);
            String categoryName = category.getString("name").toLowerCase();

            if (categoryName.contains("food") || categoryName.contains("restaurant") || categoryName.contains("bar"))
                return 0;
            if (categoryName.contains("art") || categoryName.contains("entertainment") || categoryName.contains("music") || categoryName.contains("museum"))
                return 1;
            if (categoryName.contains("college") || categoryName.contains("university") || categoryName.contains("school") || categoryName.contains("education"))
                return 2;
            if (categoryName.contains("sport") || categoryName.contains("field") || categoryName.contains("gym") || categoryName.contains("court"))
                return 3;
            if (categoryName.contains("shop") || categoryName.contains("market") || categoryName.contains("store") || categoryName.contains("supply"))
                return 4;
            if (categoryName.contains("travel") || categoryName.contains("port") || categoryName.contains("station") || categoryName.contains("bike"))
                return 5;
        }

        return 6;
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
            final Venue cardModel = data.get(position);
            final String name = cardModel.getName();
            final String address = cardModel.getLocation().getString("address");
            final String distance = cardModel.getLocation().getString("distance") + "m";
            final String coordinates = cardModel.getLocation().getString("lat") + "," + cardModel.getLocation().getString("lng");
            final JSONArray categories = cardModel.getCategories();

            Integer category = getCategoryColor(categories);

            holder.mVenueName.setText(name);
            holder.mVenueAddress.setText(address);
            holder.mVenueDistance.setText(distance);
            holder.mVenueCategory.setImageDrawable(icons[category]);
            holder.mVenueHeader.setBackgroundColor(colors[category]);
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(cardModel);
                }
            });

            holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                    builder.setTitle(name)
                            .setCancelable(true)
                            .setItems(new String[]{"More information", "See in Maps"}, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    switch (position) {
                                        case 0:
                                            final Dialog dialog = new Dialog(context);
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog.setContentView(R.layout.dialog_information);
                                            if (dialog.getWindow() != null) {
                                                dialog.getWindow().setLayout(RecyclerView.LayoutParams.FILL_PARENT, RecyclerView.LayoutParams.FILL_PARENT);
                                            }
                                            TextView mPhone = (TextView) dialog.findViewById(R.id.info_phone);
                                            TextView mUrl = (TextView) dialog.findViewById(R.id.info_url);
                                            TextView mAddress = (TextView) dialog.findViewById(R.id.info_address);

                                            mUrl.setText(cardModel.getUrl());

                                            dialog.show();
                                            break;
                                        case 1:
                                            Uri gmmIntentUri = Uri.parse("geo:" + coordinates + "17");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            context.startActivity(mapIntent);
                                    }
                                }
                            }).show();
                    return true;
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

    public interface OnItemClickListener {
        void onItemClick(Venue item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private LinearLayout mVenueHeader;
        private TextView mVenueName, mVenueAddress, mVenueDistance;
        private ImageView mVenueCategory;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.fragment_microcity_cardview);
            mVenueName = (TextView) itemView.findViewById(R.id.venue_name);
            mVenueAddress = (TextView) itemView.findViewById(R.id.venue_address);
            mVenueDistance = (TextView) itemView.findViewById(R.id.venue_distance);
            mVenueCategory = (ImageView) itemView.findViewById(R.id.venue_category);
            mVenueHeader = (LinearLayout) itemView.findViewById(R.id.venue_header);
        }
    }
}