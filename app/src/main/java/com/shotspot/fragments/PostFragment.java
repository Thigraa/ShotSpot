package com.shotspot.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.shotspot.R;
import com.shotspot.adapter.RecyclerAdapterTags;

import java.util.ArrayList;


public class PostFragment extends Fragment {

    EditText descriptionEdittext, tagsEdittext;
    RecyclerView recyclerViewTags;
    public static ArrayList <String> myTags;
    RecyclerAdapterTags adapterTags;
    LinearLayoutManager manager;

    public PostFragment() {
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
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        descriptionEdittext = v.findViewById(R.id.edittextDescription);
        tagsEdittext = v.findViewById(R.id.tagsEdittext);
        recyclerViewTags = v.findViewById(R.id.recyclerTags);
        myTags = new ArrayList<>();
        adapterTags = new RecyclerAdapterTags(myTags);
        recyclerViewTags.setAdapter(adapterTags);
        manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewTags.setLayoutManager(manager);
        tagsEdittext.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_UP)
                {
                    Toast.makeText(getContext(), ""+keyCode, Toast.LENGTH_SHORT).show();
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_SPACE:
                        case KeyEvent.KEYCODE_ENTER:
                            if(!tagsEdittext.getText().toString().equals("") && !tagsEdittext.getText().toString().equals(" ")){
                                addTag();
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        return v;
    }
    public void addTag(){
        if(recyclerViewTags.getVisibility() == View.GONE){
            recyclerViewTags.setVisibility(View.VISIBLE);
        }
        String tag =  "#"+tagsEdittext.getText().toString().trim().replaceAll(" ", "");
        myTags.add(tag);
        tagsEdittext.setText("");
        adapterTags.notifyDataSetChanged();
        recyclerViewTags.smoothScrollToPosition(myTags.size());
    }
}