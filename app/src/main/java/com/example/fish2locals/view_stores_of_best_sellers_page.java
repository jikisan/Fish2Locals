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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.AdapterViewAllStores;
import Adapters.AdapterViewSpecificProductList;
import Models.Products;
import Models.Store;
import Models.TempBestSellerData;
import Models.TempStoreData;

public class view_stores_of_best_sellers_page extends AppCompatActivity {

    private FusedLocationProviderClient client;
    private double myLatDouble, myLongDouble, distance;

    private List<Store> arrStore = new ArrayList<>();
    private List<TempBestSellerData> arrTempBestSeller = new ArrayList<>();
    private List<String> arrStoreIds = new ArrayList<>();

    private AdapterViewSpecificProductList adapterViewSpecificProductList;
    private ArrayAdapter<String> dataAdapter;


    private ProgressBar progressBar;
    private RecyclerView rv_storeLists;
    private TextView tv_back, tv_textPlaceholder;
    private AutoCompleteTextView autoCompleteTextView;

    private FirebaseUser user;
    private DatabaseReference bookmarkDatabase, storeDatabase, ratingDatabase, productsDatabase;

    private String myUserId, fishImageName;
    int counter = 0;
    double totalRating = 0, tempRatingValue = 0, averageRating = 0;

    String[] sortList ={"Near me", "Most Trusted", "Price", "Quantity"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_stores_of_best_sellers_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        bookmarkDatabase = FirebaseDatabase.getInstance().getReference("Bookmark");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");
        productsDatabase = FirebaseDatabase.getInstance().getReference("Products");

        fishImageName = getIntent().getStringExtra("fishImageName");


        setRef();
        generateStoreIds();
        getCurrentLocation();
        clicks();

        dataAdapter = new ArrayAdapter<String>(view_stores_of_best_sellers_page.this, R.layout.list_sorts, sortList);
        autoCompleteTextView.setAdapter(dataAdapter);
    }

    private void generateStoreIds() {


        String split[] = fishImageName.split("_");

        if(split.length >= 3 )
        {
            tv_back.setText("Store/s that are selling (" + split[1].toUpperCase() +" / "+ split[2].toUpperCase() + ")");
        }
        else
        {
            tv_back.setText("Store/s that are selling " + split[1].toUpperCase());
        }



        Query query = productsDatabase.orderByChild("imageName").equalTo(fishImageName);

        productsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Products products = dataSnapshot.getValue(Products.class);

                        String tempFishImageName = products.getImageName();
                        String storeId = products.getStoreId();

                        if(tempFishImageName.equals(fishImageName))
                        {

                            arrStoreIds.add(storeId);
                        }

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {

                    case 0:
                        Collections.sort(arrTempBestSeller, new Comparator<TempBestSellerData>() {
                            @Override
                            public int compare(TempBestSellerData o1, TempBestSellerData o2) {
                                return Double.compare(o1.getDistance(), o2.getDistance());
                            }


                        });

                        adapterViewSpecificProductList.notifyDataSetChanged();
                        break;

                    case 1:
                        Collections.sort(arrTempBestSeller, new Comparator<TempBestSellerData>() {
                            @Override
                            public int compare(TempBestSellerData o1, TempBestSellerData o2) {
                                return Double.compare(o1.getRatings(), o2.getRatings());
                            }
                        });

                        Collections.reverse(arrTempBestSeller);
                        adapterViewSpecificProductList.notifyDataSetChanged();
                        break;

                    case 2:
                        Collections.sort(arrTempBestSeller, new Comparator<TempBestSellerData>() {
                            @Override
                            public int compare(TempBestSellerData o1, TempBestSellerData o2) {
                                return Double.compare(o1.getPricePerKilo(), o2.getPricePerKilo());
                            }
                        });



                        adapterViewSpecificProductList.notifyDataSetChanged();
                        break;

                    case 3:
                        Collections.sort(arrTempBestSeller, new Comparator<TempBestSellerData>() {
                            @Override
                            public int compare(TempBestSellerData o1, TempBestSellerData o2) {
                                return Integer.compare(o1.getQuantityByKilo(), o2.getQuantityByKilo());
                            }
                        });

                        Collections.reverse(arrTempBestSeller);
                        adapterViewSpecificProductList.notifyDataSetChanged();
                        break;


                }

            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) view_stores_of_best_sellers_page.this
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

        rv_storeLists.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view_stores_of_best_sellers_page.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view_stores_of_best_sellers_page.this, 2, GridLayoutManager.VERTICAL, false);
        rv_storeLists.setLayoutManager(linearLayoutManager);

        adapterViewSpecificProductList = new AdapterViewSpecificProductList(arrTempBestSeller, view_stores_of_best_sellers_page.this);
        rv_storeLists.setAdapter(adapterViewSpecificProductList);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        arrTempBestSeller.clear();
        arrStore.clear();

//        for(int i = 0; i < arrStoreIds.size(); i++)
//        {
//            String tempStoreId = arrStoreIds.get(i);
//
//            storeDatabase.child(tempStoreId).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//
//                    if(snapshot.exists())
//                    {
//                        Store store = snapshot.getValue(Store.class);
//
//                        String storeUrl = store.getStoreUrl();
//                        String storeName = store.getStoreName();
//
//                        double latDouble = Double.parseDouble(store.getStoreLat());
//                        double longDouble = Double.parseDouble(store.getStoreLang());
//                        LatLng location = new LatLng(latDouble, longDouble);
//                        double distance = generateDistance(location);
//
//                        long ratings = store.getRatings();
//                        String storeId = snapshot.getKey();
//                        String storeOwnersUserId = store.getStoreOwnersUserId();
//
//                        TempStoreData tempStoreData = new TempStoreData(storeUrl, storeName,
//                                distance, ratings, counter, storeId, storeOwnersUserId);
//
//
//                        arrStore.add(store);
//                        arrTempStoreData.add(tempStoreData);
//                        adapterViewAllStores.notifyDataSetChanged();
//                    }
//
//
//
//                }
//
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }

        productsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Products products = dataSnapshot.getValue(Products.class);

                        String tempFishImageName = products.getImageName();


                        if(tempFishImageName.equals(fishImageName))
                        {
                            String tempFishName = products.getFishName();
                            double tempPrice = products.getPricePerKilo();
                            int tempQuantity = products.getQuantityByKilo();
                            String storeId = products.getStoreId();

                            arrStoreIds.add(storeId);

                            generateStoreData(storeId, tempFishImageName, tempFishName, tempPrice,
                                    tempQuantity);

                        }


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if(arrStoreIds.isEmpty())
        {
            tv_textPlaceholder.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(GONE);


    }

    private void generateStoreData(String storeId, String tempFishImageName, String tempFishName,
                                   double tempPrice, int tempQuantity) {

        storeDatabase.child(storeId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if(snapshot.exists())
                    {
                        Store store = snapshot.getValue(Store.class);

                        String storeName = store.getStoreName();

                        double latDouble = Double.parseDouble(store.getStoreLat());
                        double longDouble = Double.parseDouble(store.getStoreLang());
                        LatLng location = new LatLng(latDouble, longDouble);

                        double distance = generateDistance(location);
                        long ratings = store.getRatings();
                        String tempStoreId = snapshot.getKey();
                        String tempStoreOwnersUserId = store.getStoreOwnersUserId();

                        TempBestSellerData tempBestSellerData = new TempBestSellerData(tempFishImageName,
                                tempFishName, tempPrice, tempQuantity, storeName, distance, ratings,
                                tempStoreId, tempStoreOwnersUserId);

                        arrStore.add(store);
                        arrTempBestSeller.add(tempBestSellerData);

                        adapterViewSpecificProductList.notifyDataSetChanged();

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

        client = LocationServices.getFusedLocationProviderClient(view_stores_of_best_sellers_page.this);

        progressBar = findViewById(R.id.progressBar);
        rv_storeLists = findViewById(R.id.rv_storeLists);

        autoCompleteTextView = findViewById(R.id.AutoCompleteTextview);

        tv_back = findViewById(R.id.tv_back);
        tv_textPlaceholder = findViewById(R.id.tv_textPlaceholder);


    }
}