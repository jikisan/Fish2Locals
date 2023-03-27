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

import Adapters.AdapterStoreProductsItem;
import Fragments.ProductBottomSheetDialog;
import Models.Products;

public class view_my_products_page extends AppCompatActivity {

    private List<Products> arrProducts = new ArrayList<>();
    private List<String> arrProductIds = new ArrayList<>();
    private AdapterStoreProductsItem adapterStoreProductsItem;

    private ProgressBar progressBar;
    private RecyclerView rv_storeProducts;
    private TextView tv_back, tv_textPlaceholder;

    private FirebaseUser user;
    private DatabaseReference productDatabase;

    private String myUserId, storeOwnersUserId, storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_my_products_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");



        setRef();
        generateRecyclerLayout();
        click();
    }

    private void click() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view_my_products_page.this,
                        seller_homepage.class);
                startActivity(intent);
            }
        });

        adapterStoreProductsItem.setOnItemClickListener(new AdapterStoreProductsItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                arrProducts.get(position);
                String productId = arrProductIds.get(position);

                Intent intent = new Intent(view_my_products_page.this, edit_product_page.class);
                intent.putExtra("productId", productId);
                startActivity(intent);


            }
        });

    }

    private void generateRecyclerLayout() {

        rv_storeProducts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view_my_products_page.this);
        rv_storeProducts.setLayoutManager(linearLayoutManager);

        adapterStoreProductsItem = new AdapterStoreProductsItem(arrProducts, view_my_products_page.this);
        rv_storeProducts.setAdapter(adapterStoreProductsItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = productDatabase
                .orderByChild("userId")
                .equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrProducts.clear();
                    arrProductIds.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Products products = dataSnapshot.getValue(Products.class);
                        String productId = dataSnapshot.getKey();

                        arrProducts.add(products);
                        arrProductIds.add(productId);
                    }
                }


                progressBar.setVisibility(GONE);

                if(arrProducts.isEmpty())
                {
                    tv_textPlaceholder.setVisibility(View.VISIBLE);
                    tv_textPlaceholder.setText("Empty");
                }

                adapterStoreProductsItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setRef() {

        progressBar = findViewById(R.id.progressBar);
        rv_storeProducts = findViewById(R.id.rv_storeProducts);

        tv_back = findViewById(R.id.tv_back);
        tv_textPlaceholder = findViewById(R.id.tv_textPlaceholder);

    }
}