package com.jfem.hackathoncarnet.carnethackathon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiscountsFragment extends Fragment {
    public final static String TAG = DiscountsFragment.class.getSimpleName();
    private final static String API_BASE = " ";

    public static DiscountsFragment newInstance() {
        return new DiscountsFragment();
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
}
