package com.shotspot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shotspot.R;
import com.shotspot.database.crud.Person_CRUD;
import com.shotspot.model.Comment;
import com.shotspot.model.Person;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder>{

    private List<Comment> comments;
    public CommentsAdapter(List<Comment> comments){
        this.comments = comments;
    }
    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentHolder holder, int position) {
        holder.bindData(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, commentTextView;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextViewComment);
            commentTextView = itemView.findViewById(R.id.commentTextViewComment);
        }
        public void bindData(Comment comment){
            Person person = Person_CRUD.getPerson(comment.getIdUser());
            commentTextView.setText(person.getUsername());
            commentTextView.setText(comment.getComment());

        }
    }
}
