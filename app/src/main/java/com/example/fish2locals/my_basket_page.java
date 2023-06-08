package com.example.fish2locals;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterMyBasketItem;
import Models.Basket;

public class my_basket_page extends AppCompatActivity {

    private final List<Basket> arrBasket = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView rv_myBasket;
    private TextView tv_back, tv_checkOutBtn;

    private AdapterMyBasketItem adapterMyBasketItem;

    private FirebaseUser user;
    private DatabaseReference basketDatabase;

    private String myUserId, storeOwnersUserId, storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_basket_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");

        storeOwnersUserId = getIntent().getStringExtra("storeOwnersUserId");
        storeId = getIntent().getStringExtra("storeId");

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

        tv_checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(my_basket_page.this, place_order_page2.class);
            intent.putExtra("storeOwnersUserId", storeOwnersUserId);
            intent.putExtra("storeId", storeId);
            startActivity(intent);

            }
        });

    }

    private void generateRecyclerLayout() {

        rv_myBasket.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(my_basket_page.this);
        rv_myBasket.setLayoutManager(linearLayoutManager);

        adapterMyBasketItem = new AdapterMyBasketItem(arrBasket, my_basket_page.this);
        rv_myBasket.setAdapter(adapterMyBasketItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = basketDatabase
                .orderByChild("storeId")
                .equalTo(storeId);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrBasket.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Basket basket = dataSnapshot.getValue(Basket.class);

                        String buyerUserId = basket.getBuyerUserId();

                        if(buyerUserId.equals(myUserId))
                        {
                            arrBasket.add(basket);
                        }

                    }

                }

                progressBar.setVisibility(GONE);
                adapterMyBasketItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void setRef() {

        progressBar = findViewById(R.id.progressBar);
        rv_myBasket = findViewById(R.id.rv_myBasket);

        tv_back = findViewById(R.id.tv_back);
        tv_checkOutBtn = findViewById(R.id.tv_checkOutBtn);

    }

}