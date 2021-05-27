package com.shotspot.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.shotspot.R;

public class ClusterRenderer extends DefaultClusterRenderer {

    private static final int MARKER_DIMENSION = 48;  // 2
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;
    private final Context context;
    private final String iconTheme;

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager clusterManager, String iconTheme) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context);  // 3
        markerImageView = new ImageView(context);
        this.context = context;
        this.iconTheme = iconTheme;
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        iconGenerator.setContentView(markerImageView);  // 4
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull ClusterItem item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        markerImageView.setImageResource(context.getResources().getIdentifier(iconTheme,"drawable",context.getPackageName()));  // 6
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_outline_location_on_24);
        markerOptions.icon(bitmapDescriptorFromVector(context));  // 8
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        int icon = 0;
        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                icon = R.drawable.icon;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                icon = R.drawable.icon_dark;
                break;
        }
        Drawable background = ContextCompat.getDrawable(context, icon);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public String returnThemeName()
    {
        PackageInfo packageInfo;
        try
        {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            int themeResId = packageInfo.applicationInfo.theme;
            return context.getResources().getResourceEntryName(themeResId);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return null;
        }
    }

}
