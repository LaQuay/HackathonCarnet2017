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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jfem.hackathoncarnet.carnethackathon.R;
import com.jfem.hackathoncarnet.carnethackathon.controllers.ImageController;
import com.jfem.hackathoncarnet.carnethackathon.model.Discount;
import com.jfem.hackathoncarnet.carnethackathon.model.Venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {

    private final static CharSequence[] categories = {"Food", "Art", "College", "Sport", "Shop", "Station"};
    private final static int[] colors = {
            Color.parseColor("#DFE9C6"),
            Color.parseColor("#FFF3BA"),
            Color.parseColor("#FFD2A7"),
            Color.parseColor("#BDDCE9"),
            Color.parseColor("#DDBFE4"),
            Color.parseColor("#EEABCA"),
            Color.parseColor("#F4828C")
    };//
    private List<Discount> data;
    private Context context;
    private Drawable[] icons;

    public DiscountAdapter(List<Discount> data, Context context, VenueAdapter.OnItemClickListener listener) {
        this.data = data;
        this.context = context;
        //this.listener = listener;
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
                .inflate(R.layout.item_discount_fragment, parent, false);
        return new DiscountAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            final Discount cardModel = data.get(position);
            final String name = cardModel.getServiceName();
            final String microcity = cardModel.getMicrocityId() +"";
            final String value = cardModel.getDiscount();
            final JSONArray categories = cardModel.getServiceCategoryName();

            Integer category = getCategoryColor(categories);

            holder.mDiscountName.setText(name);
            holder.mDiscountMicroCity.setText(microcity);
            holder.mDiscountValue.setText(value);
            holder.mCardView.setBackgroundColor(colors[category]);
        } catch (JSONException e) {
            Log.e("DiscountAdapter", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mDiscountName, mDiscountMicroCity, mDiscountValue;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.fragment_discount_cardview);
            mDiscountName = (TextView) itemView.findViewById(R.id.discount_name);
            mDiscountMicroCity = (TextView) itemView.findViewById(R.id.discount_microcity);
            mDiscountValue = (TextView) itemView.findViewById(R.id.discount_value);
        }
    }
}
