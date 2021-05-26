package com.shotspot.fragments.navigation.profile;

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

import static com.shotspot.activities.MainActivity.currentUser;


public class LikedSpotsFragment extends Fragment {



    RecyclerView rvProfileLiked;
    public LikedSpotsFragment() {
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
        View v= inflater.inflate(R.layout.fragment_liked_spots, container, false);
        setUpLayout(v);
        return v;
    }
    public void setUpLayout(View v){
        rvProfileLiked = v.findViewById(R.id.recyclerLikedProfile);
        //Create adapter based on the like of the user
        SpotAdapter adapter = new SpotAdapter(Spot_CRUD.getLikeds(currentUser.getIdUser()));
        rvProfileLiked.setAdapter(adapter);
        rvProfileLiked.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}