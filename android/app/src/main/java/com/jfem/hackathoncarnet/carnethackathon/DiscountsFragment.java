package com.jfem.hackathoncarnet.carnethackathon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.jfem.hackathoncarnet.carnethackathon.controllers.DiscountController;
import com.jfem.hackathoncarnet.carnethackathon.controllers.ServiceController;
import com.jfem.hackathoncarnet.carnethackathon.model.Discount;

import java.util.ArrayList;

public class DiscountsFragment extends Fragment implements DiscountController.DiscountResolvedCallback {
    public final static String TAG = DiscountsFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private final static String API_BASE = " ";

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_discounts, container, false);


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((MainActivity) context).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDiscountResolved(ArrayList<Discount> discountArray) {
        Log.e("debug","he llegado");
    }
}
