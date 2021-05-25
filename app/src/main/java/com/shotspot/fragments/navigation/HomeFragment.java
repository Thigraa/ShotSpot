package com.shotspot.fragments.navigation;

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

import static com.shotspot.activities.MainActivity.bottomNavigationView;

public class HomeFragment extends Fragment {

    RecyclerView rHome;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        //Connect layout to class
        bottomNavigationView.setVisibility(View.VISIBLE);
        rHome = v.findViewById(R.id.recyclerHome);
        //Set the adapter of the recycler which a List of Spots and a LayoutManager
        SpotAdapter spotAdapter = new SpotAdapter(Spot_CRUD.getAll());
        rHome.setAdapter(spotAdapter);
        rHome.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
}