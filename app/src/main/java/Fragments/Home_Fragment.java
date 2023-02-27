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

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fish2locals.R;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.AdapterMostTrusted;
import Adapters.AdapterStoresNearMe;
import Models.Store;
import Models.TempStoreData;
import Models.Users;
import Objects.TextModifier;

public class Home_Fragment extends Fragment {

    private FusedLocationProviderClient client;
    private double myLatDouble, myLongDouble, distance;

    private ImageView iv_userPhoto;
    private TextView tv_fName, tv_loadingNearMe, tv_loadingMostTrusted;
    private RecyclerView rv_nearMe, rv_mostTrusted;

    private AdapterStoresNearMe adapterStoresNearMe;
    private AdapterMostTrusted adapterMostTrusted;
    private List<Store> arrStore = new ArrayList<>();
    private List<TempStoreData> arrTempStoreData = new ArrayList<>();
    private List<TempStoreData> arrTempStoreData2 = new ArrayList<>();

    private FirebaseUser user;
    private DatabaseReference userDatabase, storeDatabase;

    private String myUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");

        setRef(view);
        generateUsersData();
        getCurrentLocation();
        clicks();


        return view;
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
                                distance, ratings, storeId, storeOwnersUserId);

                        if(storeOwnersUserId.equals(myUserId))
                        {
                            continue;
                        }


                        arrStore.add(store);
                        arrTempStoreData.add(tempStoreData);
                        arrTempStoreData2.add(tempStoreData);


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

                Collections.sort(arrTempStoreData, new Comparator<TempStoreData>() {
                    @Override
                    public int compare(TempStoreData tempStoreData, TempStoreData t1) {
                        return Double.compare(tempStoreData.getDistance(), t1.getDistance());
                    }
                });

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

    private double generateDistance(LatLng location) {

        LatLng myLatLng = new LatLng(myLatDouble, myLongDouble);

//        LatLng myLatLng = new LatLng(10.320066961476325, 123.89681572928217);


        double distanceResult = SphericalUtil.computeDistanceBetween(myLatLng, location);

        return distanceResult;
    }

    private void clicks() {
    }

    private void setRef(View view) {

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        iv_userPhoto = view.findViewById(R.id.iv_userPhoto);
        tv_fName = view.findViewById(R.id.tv_fName);

        rv_nearMe = view.findViewById(R.id.rv_nearMe);
        rv_mostTrusted = view.findViewById(R.id.rv_mostTrusted);

        tv_loadingNearMe = view.findViewById(R.id.tv_loadingNearMe);
        tv_loadingMostTrusted = view.findViewById(R.id.tv_loadingMostTrusted);

    }
}