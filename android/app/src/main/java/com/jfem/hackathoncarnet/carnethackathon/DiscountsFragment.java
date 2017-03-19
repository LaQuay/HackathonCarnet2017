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

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.jfem.hackathoncarnet.carnethackathon.adapters.DiscountAdapter;
import com.jfem.hackathoncarnet.carnethackathon.adapters.VenueAdapter;
import com.jfem.hackathoncarnet.carnethackathon.controllers.DiscountController;
import com.jfem.hackathoncarnet.carnethackathon.controllers.DistanceController;
import com.jfem.hackathoncarnet.carnethackathon.controllers.ServiceController;
import com.jfem.hackathoncarnet.carnethackathon.model.Discount;
import com.jfem.hackathoncarnet.carnethackathon.model.Venue;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DiscountsFragment extends Fragment implements DiscountController.DiscountResolvedCallback {
    public final static String TAG = DiscountsFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private final static String API_BASE = " ";

    private View rootView;

    private DiscountController.DiscountResolvedCallback discountResolvedCallback;
    private List<Discount> mData;

    private final static CharSequence[] categories = {"Food", "Art", "College", "Sport", "Shop", "Station"};

    public static DiscountsFragment newInstance(int position) {
        DiscountsFragment fragment = new DiscountsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        discountResolvedCallback = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discounts, container, false);

        getDiscounts(rootView, new ArrayList<Integer>());

        return rootView;
    }

    private void getDiscounts(View rootView, ArrayList<Integer> integers) {
        DiscountController.discountsRequest(getContext(), discountResolvedCallback);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((MainActivity) context).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDiscountResolved(ArrayList<Discount> discountArray) {
        mData = discountArray;

        Log.e("DF", "onDiscountResolved");

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.discount_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new DiscountAdapter(mData, getContext()));
        Log.e("DF", "onDiscountResolved-2");
    }
}
