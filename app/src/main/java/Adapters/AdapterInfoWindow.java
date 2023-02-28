package Adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class AdapterInfoWindow implements GoogleMap.InfoWindowAdapter {


//    private InfoWindowLayoutBinding binding;
    private Location location;
    private Context context;
    private String projName, projId, projImageUrl;
    private double projAverageRating;
    private int projRatingCount;

    public AdapterInfoWindow(Location location, Context context) {

        this.location = location;
        this.context = context;

//        binding = InfoWindowLayoutBinding.inflate(LayoutInflater.from(context), null, false);
    }

    public AdapterInfoWindow() {

    }

    @Override
    public View getInfoWindow(Marker marker) {

        projName = marker.getTitle();
        projId = marker.getSnippet();
        getProjId(projId);


//        binding.txtLocationName.setText(projName);

        double distance = SphericalUtil.computeDistanceBetween(new LatLng(location.getLatitude(), location.getLongitude()),
                marker.getPosition());

        DecimalFormat df = new DecimalFormat("#.00");
        df.format(distance);

        if (distance > 1000) {
            double kilometers = distance / 1000;
//            binding.txtLocationDistance.setText(df.format(kilometers) + " KM");
        } else {
//            binding.txtLocationDistance.setText(df.format(distance) + " Meters");

        }

//        return binding.getRoot();
        return null;
    }


    @Override
    public View getInfoContents(Marker marker) {


//        return binding.getRoot();
        return null;
    }

    private void getProjId(String projId) {

        DatabaseReference projDatabase = FirebaseDatabase.getInstance().getReference("Projects");

        projDatabase.child(projId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
//                    Projects projects = snapshot.getValue(Projects.class);
//
//                 //   projId = snapshot.getKey();
//                    projImageUrl = projects.getImageUrl();
//                    projAverageRating = projects.getRatingAverage();
//                    projRatingCount = projects.getRatingCount();
//                    String projPrice = projects.getPrice();
//
//                    Picasso.get()
//                            .load(projImageUrl)
//                            .into(binding.ivMarkerPhotoBinding);
//
//                    binding.tvPrice.setText(projPrice);
//                    binding.rbUserRating.setRating((float) projAverageRating);
//                    binding.tvUserRating.setText("(" + projRatingCount + ")");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
