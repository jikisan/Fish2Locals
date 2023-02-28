package Fragments;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fish2locals.R;
import com.example.fish2locals.view_my_bookmarks_page;
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

import Adapters.AdapterBookmarks;
import Adapters.AdapterStoresNearMe;
import Adapters.AdapterViewAllStores;
import Adapters.Adapter_Spinner_Fish;
import Models.Bookmark;
import Models.Products;
import Models.Store;
import Models.TempStoreData;


public class ViewAllStoreListViewFragment extends Fragment {

    private FusedLocationProviderClient client;
    private double myLatDouble, myLongDouble, distance;

    private List<Store> arrStore = new ArrayList<>();
    private List<TempStoreData> arrTempStoreData = new ArrayList<>();
    private AdapterViewAllStores adapterViewAllStores;
    private ArrayAdapter<String> dataAdapter;


    private ProgressBar progressBar;
    private RecyclerView rv_storeLists;
    private TextView tv_back, tv_textPlaceholder;
    private AutoCompleteTextView autoCompleteTextView;

    private FirebaseUser user;
    private DatabaseReference bookmarkDatabase, storeDatabase;

    private String myUserId, storeOwnersUserId, storeId;

    String[] sortList ={"Near me", "Most Trusted"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_all_store_list_view, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        bookmarkDatabase = FirebaseDatabase.getInstance().getReference("Bookmark");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");

        setRef(view);
        getCurrentLocation();
        clicks();

        dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_sorts, sortList);
        autoCompleteTextView.setAdapter(dataAdapter);


        return view;
    }

    private void clicks() {

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {

                    case 0:
                        Collections.sort(arrTempStoreData, new Comparator<TempStoreData>() {
                            @Override
                            public int compare(TempStoreData tempStoreData, TempStoreData t1) {
                                return Double.compare(tempStoreData.getDistance(), t1.getDistance());
                            }
                        });

                        adapterViewAllStores.notifyDataSetChanged();
                        break;

                    case 1:
                        Collections.sort(arrTempStoreData, new Comparator<TempStoreData>() {
                            @Override
                            public int compare(TempStoreData tempStoreData, TempStoreData t1) {
                                return Double.compare(tempStoreData.getRatings(), t1.getRatings());
                            }
                        });

                        Collections.reverse(arrTempStoreData);

                        adapterViewAllStores.notifyDataSetChanged();
                        break;


                }

            }
        });

    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) getContext()
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        rv_storeLists.setLayoutManager(gridLayoutManager);

        adapterViewAllStores = new AdapterViewAllStores(arrStore, arrTempStoreData, getContext());
        rv_storeLists.setAdapter(adapterViewAllStores);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        storeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrTempStoreData.clear();
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


                    }
                }

                progressBar.setVisibility(GONE);
                adapterViewAllStores.notifyDataSetChanged();

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

    private void setRef(View view) {

        client = LocationServices.getFusedLocationProviderClient(getContext());

        progressBar = view.findViewById(R.id.progressBar);
        rv_storeLists = view.findViewById(R.id.rv_storeLists);

        autoCompleteTextView = view.findViewById(R.id.AutoCompleteTextview);


    }
}