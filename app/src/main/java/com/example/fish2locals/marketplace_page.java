package com.example.fish2locals;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import Adapters.AdapterMarketPlace;
import Models.Bookmark;
import Models.Products;
import Models.Ratings;
import Models.Store;
import Models.TempStoreData;

public class marketplace_page extends AppCompatActivity {

    String[] fish={"Tilapia (Mayan Cichlids)","Tambakol (Yellowfin Tuna)",
            "Lapu lapu (Leopard Coral Grouper)","Tamban","Dilis (Anchovy)","Maya maya (Red Snapper)",
            "Tulingan (Mackerel Tuna)","Galunggong (Round Scad)","Dalagang Bukid (Yellow Tail Fusilier)",
            "Sapsap (Pony Fish or Slipmouth Fish)","Hasahasa (Short Mackerel)","Apahap (Barramundi)",
            "Pompano","Bisugo (Threadfin Bream)","Tanigue (Spanish Mackerel)", "Bangus (Milkfish)"};

    int images[] = {R.drawable.fish_tilapia_mayancichlids, R.drawable.fish_tambakol_yellowfintuna,
            R.drawable.fish_lapulapu_leopardcoralgrouper, R.drawable.fish_tamban,
            R.drawable.fish_dilis_anchovy, R.drawable.fish_mayamaya_redsnapper,
            R.drawable.fish_tulingan_mackereltuna, R.drawable.fish_galunggong_roundscad,
            R.drawable.fish_dalagangbukid_yellowtailfusilier,
            R.drawable.fish_sapsap_ponyfishorslipmouthfish, R.drawable.fish_hasahasa_shortmackerel,
            R.drawable.fish_apahap_barramundi, R.drawable.fish_pompano,
            R.drawable.fish_bisugo_threadfinbream, R.drawable.fish_tanigue_spanishmackerel,
            R.drawable.fish_bangus_milkfish };


    private List<TempStoreData> arrTempStoreData = new ArrayList<>();
    private AdapterMarketPlace adapterMarketPlace;

    private ProgressBar progressBar;
    private RecyclerView rv_marketplace;
    private TextView tv_back, tv_textPlaceholder;

    private FirebaseUser user;
    private DatabaseReference bookmarkDatabase, storeDatabase, ratingDatabase;

    private String myUserId, storeOwnersUserId, storeId, ratingCounter;
    int counter = 0;
    double totalRating = 0, tempRatingValue = 0, averageRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        bookmarkDatabase = FirebaseDatabase.getInstance().getReference("Bookmark");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");


        setRef();
        generateRecyclerLayout();
        clicks();

    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void generateRecyclerLayout() {

        rv_marketplace.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(marketplace_page.this, 2, GridLayoutManager.VERTICAL, false);
        rv_marketplace.setLayoutManager(gridLayoutManager);

        adapterMarketPlace = new AdapterMarketPlace(fish, images, marketplace_page.this);
        rv_marketplace.setAdapter(adapterMarketPlace);
        adapterMarketPlace.notifyDataSetChanged();
        progressBar.setVisibility(GONE);



//        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = storeDatabase.orderByChild("userId").equalTo(myUserId);

        storeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrTempStoreData.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {

                        Store store = dataSnapshot.getValue(Store.class);
                        String storeId = dataSnapshot.getKey();
                        String storeOwnersId = store.getStoreOwnersUserId();

                        if(storeOwnersId.equals(myUserId))
                        {
                            continue;
                        }

                        String storeUrl = store.getStoreUrl();
                        String storeName = store.getStoreName();

                        double latDouble = Double.parseDouble(store.getStoreLat());
                        double longDouble = Double.parseDouble(store.getStoreLang());
                        LatLng location = new LatLng(latDouble, longDouble);

                        long ratings = store.getRatings();


//                        TempStoreData tempStoreData = new TempStoreData(storeUrl, storeName,
//                                distance, ratings, counter, storeId, storeOwnersUserId);

//                        arrTempStoreData.add(tempStoreData);
                    }
                }

                progressBar.setVisibility(GONE);

                if(arrTempStoreData.isEmpty())
                {
                    tv_textPlaceholder.setVisibility(View.VISIBLE);
                    tv_textPlaceholder.setText("Empty");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void setRef() {


        progressBar = findViewById(R.id.progressBar);
        rv_marketplace = findViewById(R.id.rv_marketplace);

        tv_back = findViewById(R.id.tv_back);
        tv_textPlaceholder = findViewById(R.id.tv_textPlaceholder);

    }

}