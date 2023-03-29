package com.example.fish2locals;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterPlaceOrderItem;
import Models.Basket;
import Models.Orders;
import Models.Users;

public class order_summary_page extends AppCompatActivity {

    private List<Basket> arrBasket = new ArrayList<>();
    private List<String> arrOrderKeyRef = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView rv_myBasket;
    private TextView tv_back, tv_viewMyOrderBtn, tv_contactNum, tv_deviveryAddress;



    private AdapterPlaceOrderItem adapterPlaceOrderItem;

    private FirebaseUser user;
    private DatabaseReference basketDatabase, orderDatabase;

    private String myUserId, storeOwnersUserId, storeId, orderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_summary_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");

        storeOwnersUserId = getIntent().getStringExtra("storeOwnersUserId");
        storeId = getIntent().getStringExtra("storeId");
        orderId = getIntent().getStringExtra("orderId");

        setRef();
        generateMyData();
        clicks();

    }

    private void generateMyData() {

        Query query = orderDatabase.orderByChild("orderId").equalTo(orderId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Orders orders = dataSnapshot.getValue(Orders.class);

                        String orderKeyRef = dataSnapshot.getKey();
                        String contactNum = orders.getBuyerContactNum();
                        String address = orders.getDeliveryAddress();

                        arrOrderKeyRef.add(orderKeyRef);
                        tv_contactNum.setText(contactNum);
                        tv_deviveryAddress.setText(address);
                    }

                    generateRecyclerLayout();

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

        tv_viewMyOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(order_summary_page.this, view_my_order_page.class);
//                startActivity(intent);
                deleteBasket();

            }
        });

    }

    private void deleteBasket() {

        Query query = basketDatabase.orderByChild("buyerUserId").equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    dataSnapshot.getRef().removeValue();
                }

                Intent intent = new Intent(order_summary_page.this, view_my_order_page.class);
                intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                intent.putExtra("orderId", orderId);
                intent.putExtra("storeId", storeId);
                startActivity(intent);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateRecyclerLayout() {

        rv_myBasket.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(order_summary_page.this);
        rv_myBasket.setLayoutManager(linearLayoutManager);

        adapterPlaceOrderItem = new AdapterPlaceOrderItem(arrBasket, order_summary_page.this);
        rv_myBasket.setAdapter(adapterPlaceOrderItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        for(int i = 0; i < arrOrderKeyRef.size(); i++)
        {

            String key = arrOrderKeyRef.get(i).toString();
            orderDatabase.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists())
                    {
                        Orders orders = snapshot.getValue(Orders.class);

                        String productId = orders.getProductId();
                        String imageName = orders.getImageName();
                        String fishName = orders.getFishName();
                        double pricePerKilo = orders.getPricePerKilo();
                        boolean pickup  = orders.isPickup();
                        boolean ownDelivery = orders.isOwnDelivery();
                        boolean thirrdPartyDelivery = orders.isThirdPartyDelivery();
                        int quantityByKilo = orders.getQuantity();
                        String storeId = orders.getStoreId();
                        String sellerUserId = orders.getSellerUserId();
                        String buyerUserId = orders.getBuyerUserId();

                        Basket basket = new Basket(imageName, fishName, pricePerKilo, pickup,
                                ownDelivery, thirrdPartyDelivery, quantityByKilo, storeId,
                                sellerUserId, buyerUserId, productId);

                        arrBasket.add(basket);

                    }

                    progressBar.setVisibility(GONE);
                    adapterPlaceOrderItem.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }





    }

    private void setRef() {

        progressBar = findViewById(R.id.progressBar);
        rv_myBasket = findViewById(R.id.rv_myBasket);

        tv_back = findViewById(R.id.tv_back);
        tv_deviveryAddress = findViewById(R.id.tv_deviveryAddress);
        tv_viewMyOrderBtn = findViewById(R.id.tv_viewMyOrderBtn);
        tv_contactNum = findViewById(R.id.tv_contactNum);



    }
}