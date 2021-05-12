package com.shotspot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewAdapter;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;
import com.shotspot.PruebaActivity;
import com.shotspot.R;
import com.shotspot.database.Person_CRUD;
import com.shotspot.database.SpotImage_CRUD;
import com.shotspot.database.Spot_CRUD;
import com.shotspot.model.Person;
import com.shotspot.model.Spot;
import com.shotspot.model.SpotImage;
import com.shotspot.storage.ImageManager;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotHolder> {

    private List<Spot> spots;
    private Context context;

    public SpotAdapter(List<Spot> spots) {
        this.spots = spots;
    }

    public List<Spot> getSpots() {
        return spots;
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public SpotAdapter.SpotHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_recycler_home,parent,false);
        return new SpotHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull SpotAdapter.SpotHolder holder, int position) {

        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        long imageLength = 0;
        try {
            Spot spot = spots.get(position);
            Person user = Person_CRUD.getPerson(spot.getIdUser());
            holder.usernameTV.setText(user.getUsername());
            holder.descriptionTV.setText(spot.getDescription());
            holder.tagsTV.setText(spot.getTags());
            ImageManager.GetImage(user.getImageURL(), imageStream, imageLength);

            byte[] buffer = imageStream.toByteArray();

            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

            holder.profileImg.setImageBitmap(bitmap);
            List<SpotImage> images = SpotImage_CRUD.getSpotId(spot.getIdSpot());
            int numPhotos= images.size();
            if (numPhotos>10){
                numPhotos=10;
                Collections.shuffle(images);
            }
            holder.carouselView.setSize(numPhotos);
            holder.carouselView.setAutoPlay(false);
            holder.carouselView.setAutoPlayDelay(3000);
            holder.carouselView.setResource(R.layout.center_carousel_item);
            holder.carouselView.setCarouselOffset(OffsetType.CENTER);
            holder.carouselView.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    ImageView imageView = view.findViewById(R.id.imageView);
                    imageView.setImageBitmap(images.get(position).getBitmap());
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO Zoom Image onClick
                        }
                    });
                }
            });
            holder.carouselView.show();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    public static class SpotHolder extends RecyclerView.ViewHolder {
        TextView usernameTV, descriptionTV, tagsTV;
        CircleImageView profileImg;
        CarouselView carouselView;

        public SpotHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profileUserImageItem);
            usernameTV = itemView.findViewById(R.id.usernameTextViewItem);
            carouselView = itemView.findViewById(R.id.carouselRecyclerViewItem);
            descriptionTV = itemView.findViewById(R.id.descriptionTextViewItem);
            tagsTV = itemView.findViewById(R.id.tagsTextViewItem);

        }
    }

}