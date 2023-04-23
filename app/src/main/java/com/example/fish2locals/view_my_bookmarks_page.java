package com.example.fish2locals;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterBookmarks;
import Adapters.AdapterMostTrusted;
import Adapters.AdapterStoreProductsItem;
import Models.Bookmark;
import Models.Products;
import Models.Ratings;
import Models.Store;
import Models.TempStoreData;

public class view_my_bookmarks_page extends AppCompatActivity {

    private FusedLocationProviderClient client;
    private double myLatDouble, myLongDouble, distance;

    private List<Products> arrBookmarks = new ArrayList<>();
    private List<String> arrStoreId = new ArrayList<>();
    private List<Store> arrStore = new ArrayList<>();
    private List<TempStoreData> arrTempStoreData = new ArrayList<>();
    private AdapterBookmarks adapterMostTrusted;

    private ProgressBar progressBar;
    private RecyclerView rv_myBookmarks;
    private TextView tv_back, tv_textPlaceholder;

    private FirebaseUser user;
    private DatabaseReference bookmarkDatabase, storeDatabase, ratingDatabase;

    private String myUserId, ratingCounter;
    int counter = 0;
    double totalRating = 0, tempRatingValue = 0, averageRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_my_bookmarks_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        bookmarkDatabase = FirebaseDatabase.getInstance().getReference("Bookmark");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");


        setRef(); // initialize UI Id's
        getCurrentLocation(); // generate current location
        clicks(); // buttons
    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) view_my_bookmarks_page.this
                .getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location

            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(
                        @NonNull Task<Location> task) {

                    // Initialize location
                    Location location = task.getResult();                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        myLatDouble = location.getLatitude();
                        myLongDouble = location.getLongitude();
                        generateRecyclerLayout();


                    } else {
                        // When location result is null
                        // initialize location request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        // Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void
                            onLocationResult(LocationResult locationResult) {
                                // Initialize
                                // location
                                Location location1 = locationResult.getLastLocation();
                                myLatDouble = location1.getLatitude();
                                myLongDouble = location1.getLongitude();
                                generateRecyclerLayout();


                            }
                        };

                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback,
                                Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void generateRecyclerLayout() {

        rv_myBookmarks.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view_my_bookmarks_page.this, 2, GridLayoutManager.VERTICAL, false);
        rv_myBookmarks.setLayoutManager(gridLayoutManager);

        adapterMostTrusted = new AdapterBookmarks(arrTempStoreData, view_my_bookmarks_page.this);
        rv_myBookmarks.setAdapter(adapterMostTrusted);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = bookmarkDatabase.orderByChild("userId").equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrTempStoreData.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {

                        Bookmark bookmark = dataSnapshot.getValue(Bookmark.class);
                        String storeId = bookmark.getStoreId();

                        String storeUrl = bookmark.getStoreUrl();
                        String storeName = bookmark.getStoreName();

                        double latDouble = Double.parseDouble(bookmark.getStoreLat());
                        double longDouble = Double.parseDouble(bookmark.getStoreLang());
                        LatLng location = new LatLng(latDouble, longDouble);
                        double distance = generateDistance(location);

                        long ratings = bookmark.getRatings();


                        TempStoreData tempStoreData = new TempStoreData(storeUrl, storeName,
                                distance, ratings, counter, storeId, myUserId);

                        arrTempStoreData.add(tempStoreData);
                    }
                }

                progressBar.setVisibility(GONE);

                if(arrTempStoreData.isEmpty())
                {
                    tv_textPlaceholder.setVisibility(View.VISIBLE);
                    tv_textPlaceholder.setText("Empty");
                }

                adapterMostTrusted.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateReviews(String storeId) {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(storeId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        tempRatingValue = ratings.getRatingValue();
                        totalRating = totalRating + tempRatingValue;
                        counter++;
                    }

                    averageRating = totalRating / counter;
                    ratingCounter = "(" + String.valueOf(counter) + ")";


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double generateDistance(LatLng location) {

        LatLng myLatLng = new LatLng(myLatDouble, myLongDouble);

//        LatLng myLatLng = new LatLng(10.320066961476325, 123.89681572928217);


        double distanceResult = SphericalUtil.computeDistanceBetween(myLatLng, location);

        return distanceResult;
    }

    private void setRef() {

        client = LocationServices.getFusedLocationProviderClient(view_my_bookmarks_page.this);

        progressBar = findViewById(R.id.progressBar);
        rv_myBookmarks = findViewById(R.id.rv_myBookmarks);

        tv_back = findViewById(R.id.tv_back);
        tv_textPlaceholder = findViewById(R.id.tv_textPlaceholder);

    }


}