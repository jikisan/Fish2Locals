package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.AdapterStoreItem;
import Models.Ratings;
import Models.Store;
import Models.TempStoreData;
import Models.Users;

public class search_page extends AppCompatActivity {

    private TextView tv_back;
    private RecyclerView recyclerView_searches;
    private SearchView sv_search;

    private List<TempStoreData> arrTempStoreData = new ArrayList<>();
    private List<TempStoreData> arr;
    private List<Store> arrStore = new ArrayList<>();
    private List<String> arrStoreId = new ArrayList<>();

    private FirebaseUser user;
    private DatabaseReference userDatabase, storeDatabase, ratingDatabase;
    private AdapterStoreItem adapterStoreItem;

    private String myUserId, ratingCounter;
    int counter = 0;
    double totalRating = 0, tempRatingValue = 0, averageRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");


        setRef();
        generateStoreData();
        generateRecyclerLayout();
        clicks();



    }

    private void clicks() {

        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(search_page.this, homepage.class);
                startActivity(intent);
            }
        });
    }

    private void generateStoreData() {

        storeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrStore.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Store store = dataSnapshot.getValue(Store.class);

                        String storeUrl = store.getStoreUrl();
                        String storeName = store.getStoreName();

                        double latDouble = Double.parseDouble(store.getStoreLat());
                        double longDouble = Double.parseDouble(store.getStoreLang());
                        LatLng location = new LatLng(latDouble, longDouble);
                        double distance = 0.00;

                        long ratings = store.getRatings();
                        String storeId = dataSnapshot.getKey();
                        String storeOwnersUserId = store.getStoreOwnersUserId();

                        TempStoreData tempStoreData = new TempStoreData(storeUrl, storeName,
                                distance, ratings, counter, storeId, storeOwnersUserId);

                        if(storeOwnersUserId.equals(myUserId))
                        {
                            continue;
                        }


                        arrStore.add(store);
                        arrTempStoreData.add(tempStoreData);

                    }
                }



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

    private void search(String s) {

        int counter = 0;
        arr = new ArrayList<>();

        for(TempStoreData object : arrTempStoreData)
        {
            if (object.getStoreName().toLowerCase().contains(s.toLowerCase()))
            {
                arr.add(object);


            }

            if(s.isEmpty())
            {

                arr.clear();

            }

            adapterStoreItem = new AdapterStoreItem(arr, search_page.this);
            recyclerView_searches.setAdapter(adapterStoreItem);
        }

        if(adapterStoreItem != null)
        {
            recyclerView_searches.setVisibility(View.VISIBLE);
            adapterStoreItem.setOnItemClickListener(new AdapterStoreItem.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    String storeOwnersUserId = arr.get(position).getStoreOwnersUserId();
                    String storeId = arr.get(position).getStoreId();

                    Intent intent = new Intent(search_page.this, view_store_page.class);
                    intent.putExtra("storeId", storeId);
                    intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                    startActivity(intent);

                }
            });
        }

    }

    private void generateRecyclerLayout() {
        recyclerView_searches.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(search_page.this);
        recyclerView_searches.setLayoutManager(linearLayoutManager);

    }

    private void setRef() {

        tv_back = findViewById(R.id.tv_back);
        recyclerView_searches = findViewById(R.id.recyclerView_searches);
        sv_search = findViewById(R.id.sv_search);
    }
}