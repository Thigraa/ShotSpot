package com.shotspot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shotspot.R;

import java.util.List;

import static com.shotspot.fragments.navigation.PostFragment.recyclerViewTags;

public class RecyclerAdapterTags extends RecyclerView.Adapter<RecyclerAdapterTags.TagViewHolder> {

    List<String> tags;

    public RecyclerAdapterTags(List<String> tags){
        this.tags = tags;
    }


    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tag, parent, false);
        return new TagViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.bindData(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder{
        EditText tag;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.tagEdittext);
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tags.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    if(tags.size() == 0){
                        recyclerViewTags.setVisibility(View.GONE);
                    }
                }
            });

        }
        public void bindData(String tagName){
            tag.setText(tagName);

        }
    }
}