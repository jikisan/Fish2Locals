package Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.fish2locals.R;
import com.example.fish2locals.homepage;
import com.example.fish2locals.search_page;
import com.example.fish2locals.view_all_stores;
import com.example.fish2locals.view_store_page;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.AdapterBestSellers;
import Adapters.AdapterMostTrusted;
import Adapters.AdapterStoresNearMe;
import Models.Products;
import Models.Ratings;
import Models.Store;
import Models.TempStoreData;
import Models.Users;
import Objects.TextModifier;

public class Home_Fragment extends Fragment {

    private Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;


    private FusedLocationProviderClient client;
    private double myLatDouble, myLongDouble, distance;

    private ImageView iv_userPhoto;
    private TextView tv_fName, tv_loadingNearMe, tv_loadingMostTrusted, tv_nearMeViewAll,
            tv_mostTrustedViewAll, tv_search, tv_loadingBestSeller;
    private RecyclerView rv_nearMe, rv_mostTrusted, rv_bestSeller;

    private AdapterStoresNearMe adapterStoresNearMe;
    private AdapterMostTrusted adapterMostTrusted;
    private AdapterBestSellers adapterBestSellers;

    private List<Store> arrStore = new ArrayList<>();
    private List<TempStoreData> arrTempStoreData = new ArrayList<>();
    private List<TempStoreData> arrTempStoreData2 = new ArrayList<>();
    private List<String> arrProductNames = new ArrayList<>();
    private List<String> arrProductImageNames = new ArrayList<>();
//    private List<String> newList, newImageList;

    private FirebaseUser user;
    private DatabaseReference userDatabase, storeDatabase, ratingDatabase, productsDatabase, basketDatabase;

    private String myUserId, ratingCounter;
    int counter = 0;
    double totalRating = 0, tempRatingValue = 0, averageRating = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");
        productsDatabase = FirebaseDatabase.getInstance().getReference("Products");
        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");


        setRef(view); // initialize UI ID's
        generateUsersData(); // generate users data
        generateProductsList(); // generate product list
        getCurrentLocation(); // generate current user location
//        deleteActiveBaskets(); // Delete existing baskets
        clicks(); // buttons


        return view;
    }

    private void generateProductsList() {

        productsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Products products = dataSnapshot.getValue(Products.class);

                        String productName = products.getFishName();
                        String imageName = products.getImageName();
                        arrProductNames.add(productName);
                        arrProductImageNames.add(imageName);
                    }

                    tv_loadingBestSeller.setVisibility(View.GONE);
                }


                Set<String> productNameSet = new HashSet<String>(arrProductNames);
                List<String> newProductNameList = new ArrayList<String>(productNameSet);

                Set<String> imageNameSet = new HashSet<String>(arrProductImageNames);
                List<String> newImageNameList = new ArrayList<String>(imageNameSet);

                //best seller list
                adapterBestSellers = new AdapterBestSellers(newProductNameList, newImageNameList, getContext());
                rv_bestSeller.setAdapter(adapterBestSellers);
                rv_bestSeller.setHasFixedSize(true);

                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                rv_bestSeller.setLayoutManager(linearLayoutManager2);

                adapterBestSellers.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateUsersData() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    String imageUrl = users.getImageUrl();

                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get()
                                .load(imageUrl)
                                .into(iv_userPhoto);
                    }


                    TextModifier textModifier = new TextModifier();
                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();
                    tv_fName.setText(fname + "!");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        // Initialize Location manager
        LocationManager locationManager = (LocationManager) getActivity()
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
                        generateRecyclerview();


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
                                generateRecyclerview();


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

    private void generateRecyclerview() {


        adapterStoresNearMe = new AdapterStoresNearMe(arrStore, arrTempStoreData, getContext());
        adapterMostTrusted = new AdapterMostTrusted(arrStore, arrTempStoreData2, getContext());
        rv_nearMe.setAdapter(adapterStoresNearMe);
        rv_mostTrusted.setAdapter(adapterMostTrusted);



        rv_nearMe.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rv_nearMe.setLayoutManager(linearLayoutManager);



        rv_mostTrusted.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rv_mostTrusted.setLayoutManager(linearLayoutManager1);



        adapterStoresNearMe.notifyDataSetChanged();

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        storeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrTempStoreData.clear();
                    arrTempStoreData2.clear();
                    arrStore.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Store store = dataSnapshot.getValue(Store.class);

                        String storeUrl = store.getStoreUrl();
                        String storeName = store.getStoreName();
                        double latDouble = Double.parseDouble(store.getStoreLat());
                        double longDouble = Double.parseDouble(store.getStoreLang());
                        LatLng location = new LatLng(latDouble, longDouble);
                        double distance = generateDistance(location);

                        long ratings = store.getRatings();
                        String storeId = dataSnapshot.getKey();
                        String storeOwnersUserId = store.getStoreOwnersUserId();

                        TempStoreData tempStoreData = new TempStoreData(storeUrl, storeName,
                                distance, ratings, counter, storeId, storeOwnersUserId);

                        arrStore.add(store);
                        arrTempStoreData.add(tempStoreData);
                        arrTempStoreData2.add(tempStoreData);

                        if(storeOwnersUserId.equals(myUserId))
                        {
                            continue;
                        }


                    }
                }

                if (arrTempStoreData.isEmpty()) {
                    rv_mostTrusted.setVisibility(View.GONE);
                    rv_nearMe.setVisibility(View.GONE);
                    tv_loadingMostTrusted.setVisibility(View.VISIBLE);
                    tv_loadingNearMe.setVisibility(View.VISIBLE);
                }
                else {
                    rv_mostTrusted.setVisibility(View.VISIBLE);
                    rv_nearMe.setVisibility(View.VISIBLE);
                    tv_loadingMostTrusted.setVisibility(View.INVISIBLE);
                    tv_loadingNearMe.setVisibility(View.INVISIBLE);

                }

                // sort products list by distance
                Collections.sort(arrTempStoreData, new Comparator<TempStoreData>() {
                    @Override
                    public int compare(TempStoreData tempStoreData, TempStoreData t1) {
                        return Double.compare(tempStoreData.getDistance(), t1.getDistance());
                    }
                });

                // sort products list by ratings
                Collections.sort(arrTempStoreData2, new Comparator<TempStoreData>() {
                    @Override
                    public int compare(TempStoreData tempStoreData, TempStoreData t1) {
                        return Double.compare(tempStoreData.getRatings(), t1.getRatings());
                    }
                });

                Collections.reverse(arrTempStoreData2);

                adapterStoresNearMe.notifyDataSetChanged();
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

    private void deleteActiveBaskets() {


        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Query query = basketDatabase.orderByChild("buyerUserId").equalTo(myUserId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(!snapshot.exists())
                        {
                            timer.cancel();
                        }
                        else if(snapshot.exists())
                        {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                dataSnapshot.getRef().removeValue();
                            }

                            Intent intent = new Intent(getContext(), homepage.class);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }, 0, 10000);


    }

    private void clicks() {

        tv_nearMeViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), view_all_stores.class);
                intent.putExtra("source", "1");
                startActivity(intent);
            }
        });

        tv_mostTrustedViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), view_all_stores.class);
                intent.putExtra("source", "2");
                startActivity(intent);
            }
        });

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), search_page.class);
                startActivity(intent);
            }
        });
    }

    private void setRef(View view) {

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        iv_userPhoto = view.findViewById(R.id.iv_userPhoto);
        tv_fName = view.findViewById(R.id.tv_fName);

        rv_nearMe = view.findViewById(R.id.rv_nearMe);
        rv_mostTrusted = view.findViewById(R.id.rv_mostTrusted);
        rv_bestSeller = view.findViewById(R.id.rv_bestSeller);

        tv_loadingNearMe = view.findViewById(R.id.tv_loadingNearMe);
        tv_loadingMostTrusted = view.findViewById(R.id.tv_loadingMostTrusted);
        tv_loadingBestSeller = view.findViewById(R.id.tv_loadingBestSeller);
        tv_nearMeViewAll = view.findViewById(R.id.tv_nearMeViewAll);
        tv_mostTrustedViewAll = view.findViewById(R.id.tv_mostTrustedViewAll);

        tv_search = view.findViewById(R.id.tv_search);

    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel(); // Stop the timer when the activity is paused
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();; // Stop the timer when the activity is paused

    }
}