package com.shotspot.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shotspot.R;
import com.shotspot.adapter.CommentsAdapter;
import com.shotspot.database.crud.Comment_CRUD;
import com.shotspot.model.Comment;

import java.util.List;

import static com.shotspot.activities.MainActivity.bottomNavigationView;
import static com.shotspot.activities.MainActivity.currentUser;

public class CommentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID_SPOT = "param1";

    // TODO: Rename and change types of parameters
    private int idSpot;
    private EditText editTextComment;
    private Button buttonPost;
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private LinearLayoutManager manager;
    List<Comment> commentList;

    public CommentsFragment(int idSpot) {
        this.idSpot = idSpot;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_comments, container, false);
        editTextComment = v.findViewById(R.id.edittextComment);
        buttonPost = v.findViewById(R.id.buttonPostComment);
        recyclerView = v.findViewById(R.id.recyclerComments);
        commentList = Comment_CRUD.getCommentBySpotID(idSpot);
        adapter = new CommentsAdapter(commentList);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment(idSpot, currentUser.getIdUser(), editTextComment.getText().toString());
                editTextComment.setText("");
                Comment_CRUD.insert(comment);
                commentList.add(comment);
                adapter.notifyDataSetChanged();
            }
        });

        bottomNavigationView.setVisibility(View.INVISIBLE);
        return v;
    }
}