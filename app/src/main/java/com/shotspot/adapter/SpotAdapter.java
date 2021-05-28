package com.shotspot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.OffsetType;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.shotspot.R;
import com.shotspot.activities.MainActivity;
import com.shotspot.database.crud.Like_CRUD;
import com.shotspot.database.crud.Person_CRUD;
import com.shotspot.database.crud.SpotImage_CRUD;
import com.shotspot.fragments.CommentsFragment;
import com.shotspot.fragments.navigation.DiscoverFragment;
import com.shotspot.fragments.navigation.ProfileFragment;
import com.shotspot.helper.DoubleClickListener;
import com.shotspot.model.Like;
import com.shotspot.model.Person;
import com.shotspot.model.Spot;
import com.shotspot.model.SpotImage;
import com.shotspot.database.storage.ImageManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.shotspot.activities.MainActivity.currentUser;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotHolder> {

    private List<Spot> spots;
    private Context context;
    final Handler handler = new Handler();
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
        try {
            Spot spot = spots.get(position);
            Like like = new Like(spot.getIdSpot(),currentUser.getIdUser());
            Person user = Person_CRUD.getPerson(spot.getIdUser());
            holder.usernameTV.setText(user.getUsername());
            holder.descriptionTV.setText(spot.getDescription());;
            String[] tags = spot.getTags().split("#");
            List<String> tagsToAdapter = new ArrayList<>();
            int i = 0;
            for(String tag : tags){
                tag = "#"+tag;
                if(i != 0){
                    tagsToAdapter.add(tag);
                }
                i++;
            }
            holder.tagsRV.setAdapter(new RecyclerAdapterTagsHome(tagsToAdapter));
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(RecyclerView.HORIZONTAL);
            holder.tagsRV.setLayoutManager(manager);
            if (user.getImageURL()!=null ){
                searchUserImage(user,holder);
            }
            if (Like_CRUD.getLike(like.getId_spot(),like.getId_user()) != null){
                holder.likeButton.setLiked(true);
            }
            List<SpotImage> images = SpotImage_CRUD.getImagesBySpotId(spot.getIdSpot());
            //setting up carousel view
            holder.carouselView.setSize(images.size());
            holder.carouselView.setAutoPlay(false);
            holder.carouselView.setAutoPlayDelay(3000);
            holder.carouselView.setResource(R.layout.center_carousel_item);
            holder.carouselView.setCarouselOffset(OffsetType.CENTER);
            holder.carouselView.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    ImageView imageView = view.findViewById(R.id.imageView);
                    Thread th = new Thread(new Runnable() {
                        public void run() {
                            try {
                                handler.post(new Runnable() {
                                    public void run() {
                                        imageView.setImageBitmap(images.get(position).getBitmap());
                                        imageView.setOnClickListener(new DoubleClickListener() {
                                            @Override
                                            public void onDoubleClick() {
                                                Fragment f = new DiscoverFragment();
                                                Bundle bundle = new Bundle();
                                                double latitude = spot.getLatitde();
                                                double longitude = spot.getLongitude();
                                                bundle.putDouble("latitude", latitude);
                                                bundle.putDouble("longitude", longitude);
                                                f.setArguments(bundle);
                                                //Change fragment
                                                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().addToBackStack(null)
                                                        .replace(R.id.navHost, f)
                                                        .commit();
                                            }
                                        });
                                    }
                                });
                            }
                            catch(Exception ex) {
                                ex.printStackTrace();
                            }
                        }});
                    th.start();
                }
            });
            holder.carouselView.show();
            i= 0;
            holder.likesCount.setText(Like_CRUD.getNumLikes(spot.getIdSpot())+" likes");
            holder.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Click to comment
                    Fragment f = new CommentsFragment(spot.getIdSpot());
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.navHost, f)
                            .commit();
                }
            });

            holder.likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Like_CRUD.insert(like);
                    String likes = holder.likesCount.getText().toString().split(" ")[0]; //Only take the number part
                    int numLikes = Integer.parseInt(likes);
                    numLikes++;
                    holder.likesCount.setText(numLikes+" likes");
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Like_CRUD.delete(like);
                    String likes = holder.likesCount.getText().toString().split(" ")[0]; //Only take the number part
                    int numLikes = Integer.parseInt(likes);
                    numLikes--;
                    holder.likesCount.setText(numLikes+" likes");
                }
            });
            holder.spotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Click to move to the spot in the map
                    Fragment f = new DiscoverFragment();
                    Bundle bundle = new Bundle();
                    double latitude = spot.getLatitde();
                    double longitude = spot.getLongitude();
                    bundle.putDouble("latitude", latitude);
                    bundle.putDouble("longitude", longitude);
                    f.setArguments(bundle);
                    //Change fragment
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.navHost, f)
                            .commit();

                }
            });
            holder.profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment profileFragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("id_user",spot.getIdUser());
                    profileFragment.setArguments(bundle);
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.navHost, profileFragment)
                            .commit();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchUserImage(Person user,SpotAdapter.SpotHolder holder) {
        final String image = user.getImageURL();
        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        Thread th = new Thread(new Runnable() {
            public void run() {

                try {

                    long imageLength = 0;

                    ImageManager.getImage(image, imageStream, imageLength);

                    handler.post(new Runnable() {

                        public void run() {
                            byte[] buffer = imageStream.toByteArray();

                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                            holder.profileImg.setImageBitmap(bitmap);
                        }
                    });
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }});
        th.start();

    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    public static class SpotHolder extends RecyclerView.ViewHolder {
        TextView usernameTV, descriptionTV, likesCount;
        CircleImageView profileImg;
        CarouselView carouselView;
        ImageView commentButton;
        LikeButton likeButton;
        ImageView spotButton;
        RecyclerView tagsRV;

        public SpotHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profileUserImageItem);
            usernameTV = itemView.findViewById(R.id.usernameTextViewItem);
            carouselView = itemView.findViewById(R.id.carouselRecyclerViewItem);
            descriptionTV = itemView.findViewById(R.id.descriptionTextViewItem);
            tagsRV = itemView.findViewById(R.id.recyclerTagsHome);
            commentButton = itemView.findViewById(R.id.comment_button);
            likeButton = itemView.findViewById(R.id.likeButton);
            spotButton = itemView.findViewById(R.id.goToSpot_button);
            likesCount = itemView.findViewById(R.id.likesCount);

        }
    }



}
