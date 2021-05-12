package com.shotspot.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shotspot.R;
import com.shotspot.adapter.SpotAdapter;
import com.shotspot.database.Spot_CRUD;


public class MySpotsFragment extends Fragment {


    RecyclerView rvProfileSpots;
    public MySpotsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_spots, container, false);
        rvProfileSpots = v.findViewById(R.id.recyclerSpotsProfile);
        //TODO Change de value of the userId by the current User
        SpotAdapter adapter = new SpotAdapter(Spot_CRUD.getByUserId(4));
        rvProfileSpots.setAdapter(adapter);
        rvProfileSpots.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
}