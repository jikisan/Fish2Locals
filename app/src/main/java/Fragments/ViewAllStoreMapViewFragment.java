package Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fish2locals.R;
import com.example.fish2locals.view_store_page;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.util.ArrayList;

import Models.Store;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewAllStoreMapViewFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener{

    private FusedLocationProviderClient client;

    private LatLng location;
    private Double latDouble, longDouble;
    private double latitude, longitude;
    private SupportMapFragment supportMapFragment;
    private FirebaseUser user;
    private String myUserId;

    private ArrayList<LatLng> arrLoc;
    private ArrayList<String> arrKeyID;
    private ArrayList<String> arrName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_store_map_view, container, false);
        supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.google_map);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        client = LocationServices.getFusedLocationProviderClient(getContext());

        setRef(view);
        getCurrentLocation();

        return view;
    }



    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(
                        @NonNull Task<Location> task) {

//                    adapterInfoWindow = null;

                    // Initialize location
                    Location location = task.getResult();                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        latDouble = location.getLatitude();
                        longDouble = location.getLongitude();
                        asyncMap(latDouble, longDouble);
//                        adapterInfoWindow= new AdapterInfoWindow(location, requireContext());


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
                                latDouble = location1.getLatitude();
                                longDouble = location1.getLongitude();
                                asyncMap(latDouble, longDouble);

//                                adapterInfoWindow= new AdapterInfoWindow(location1, requireContext());

                            }
                        };

                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void asyncMap(Double latDouble, Double longDouble) {
        // Async map

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {


                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                LatLng location = new LatLng(latDouble, longDouble);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title("My Location");
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

                //googleMap.addMarker(markerOptions);
                generateStoreLocations(googleMap);


            }
        });
    }

    private void generateStoreLocations(GoogleMap googleMap) {

        DatabaseReference storeDatabase = FirebaseDatabase.getInstance().getReference("Store");

        storeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Store store = dataSnapshot.getValue(Store.class);

                        String storeOwnersUserId = store.getStoreOwnersUserId();
                        if(storeOwnersUserId.equals(myUserId))
                        {
                            continue;
                        }

                        String storeId = dataSnapshot.getKey();
                        String storeUrl = store.getStoreUrl();
                        String storeName = store.getStoreName();
                        long ratings = store.getRatings();

                        latitude = Double.parseDouble(store.getStoreLat());
                        longitude = Double.parseDouble(store.getStoreLang());
                        location = new LatLng(latitude, longitude);

                        arrLoc.add(location);
                        arrKeyID.add(storeId);
                        arrName.add(storeName);

                    }

                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.custom_marker2);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);

                    for (int i = 0; i < arrLoc.size(); i++) {

                        // below line is use to add marker to each location of our array list.
                        googleMap.addMarker(new MarkerOptions()
                                .position(arrLoc.get(i))
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                .snippet("(Click here to visit store)")
                                .title(arrName.get(i))


                        );

                        googleMap.setOnInfoWindowClickListener(ViewAllStoreMapViewFragment.this);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning!.")
                .setCancelText("Go Back")
                .setConfirmButton("Visit Store", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        String storeName = marker.getTitle();

                        generateStoreData(storeName);


                    }
                })
                .setContentText("Visit " + marker.getTitle()+ " ?")
                .show();

    }

    private void generateStoreData(String storeName) {

        DatabaseReference storeDatabase = FirebaseDatabase.getInstance().getReference("Store");

        Query query = storeDatabase.orderByChild("storeName").equalTo(storeName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {


                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Store store = dataSnapshot.getValue(Store.class);
                        String storeId = dataSnapshot.getKey();
                        String storeOwnersUserId = store.getStoreOwnersUserId();

                        Intent intent = new Intent(getContext(), view_store_page.class);
                        intent.putExtra("storeId", storeId);
                        intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                        getContext().startActivity(intent);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef(View view) {

        arrKeyID = new ArrayList<>();
        arrLoc = new ArrayList<>();
        arrName = new ArrayList<>();
    }
}