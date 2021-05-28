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
import com.shotspot.database.crud.Spot_CRUD;
import com.shotspot.model.Spot;

import java.util.List;

import static com.shotspot.activities.MainActivity.bottomNavigationView;

public class SearchResultFragment extends Fragment {
    RecyclerView rSpots;
    private List<Spot> spotList;
    public SearchResultFragment(List <Spot> spotList) {
        this.spotList = spotList;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_result, container, false);
        //Connect layout to class
        bottomNavigationView.setVisibility(View.VISIBLE);
        rSpots = v.findViewById(R.id.recyclerSpots);
        //Set the adapter of the recycler which a List of Spots and a LayoutManager
        SpotAdapter spotAdapter = new SpotAdapter(spotList);
        rSpots.setAdapter(spotAdapter);
        rSpots.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
}