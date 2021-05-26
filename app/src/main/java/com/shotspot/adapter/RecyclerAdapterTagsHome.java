package com.shotspot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.shotspot.R;
import com.shotspot.database.crud.Spot_CRUD;
import com.shotspot.fragments.SearchResultFragment;
import com.shotspot.model.Spot;

import java.time.Duration;
import java.util.List;

import static com.shotspot.fragments.navigation.PostFragment.recyclerViewTags;

public class RecyclerAdapterTagsHome extends RecyclerView.Adapter<RecyclerAdapterTagsHome.TagViewHolder> {

    List<String> tags;

    public RecyclerAdapterTagsHome(List<String> tags){
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
                    //Search post by tags on click
                    String location = tag.getText().toString();
                    location = location.replaceAll("#", "");
                    List<Spot> spotList = Spot_CRUD.searchByTags(location);
                    if(spotList.size()>0) {
                        Fragment f = new SearchResultFragment(spotList);
                        ((FragmentActivity) itemView.getContext()).getSupportFragmentManager().beginTransaction().addToBackStack(null)
                                .replace(R.id.navHost, f)
                                .commit();
                    }
                    else{
                        Snackbar.make(itemView, "No results found", BaseTransientBottomBar.LENGTH_SHORT);
                    }

                }
            });

        }
        public void bindData(String tagName){
            tag.setText(tagName);

        }
    }
}