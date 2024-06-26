package Fragments;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fish2locals.R;
import com.example.fish2locals.add_to_basket_page;
import com.example.fish2locals.add_to_basket_page2;
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
import Models.Products;

public class StoreProductsFragment extends Fragment {

    private final List<Products> arrProducts = new ArrayList<>();
    private final List<String> arrProductIds = new ArrayList<>();
    private AdapterStoreProductsItem adapterStoreProductsItem;

    private ProgressBar progressBar;
    private RecyclerView rv_storeProducts;
    private TextView tv_textPlaceholder;

    private DatabaseReference productDatabase;

    private String storeOwnersUserId;
    private String storeId, myUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_products, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");

        storeOwnersUserId = getActivity().getIntent().getStringExtra("storeOwnersUserId");
        storeId = getActivity().getIntent().getStringExtra("storeId");

        setRef(view); // initialize UI Id's
        generateRecyclerLayout(); // generate recyclerlayout
        click(); // buttons

        return view;
    }

    private void click() {

        adapterStoreProductsItem.setOnItemClickListener(new AdapterStoreProductsItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                arrProductIds.get(position);

                String productId = arrProductIds.get(position);

                Intent intent = new Intent(getContext(), add_to_basket_page.class);
                intent.putExtra("productId", productId);
                intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                intent.putExtra("storeId", storeId);
                startActivity(intent);


            }
        });
    }

    private void generateRecyclerLayout() {

        rv_storeProducts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_storeProducts.setLayoutManager(linearLayoutManager);

        adapterStoreProductsItem = new AdapterStoreProductsItem(arrProducts, getContext());
        rv_storeProducts.setAdapter(adapterStoreProductsItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = productDatabase
                .orderByChild("storeId")
                .equalTo(storeId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrProducts.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Products products = dataSnapshot.getValue(Products.class);
                        String productId = dataSnapshot.getKey();

                        int quantity = products.getQuantityByKilo();

                        if(quantity > 0)
                        {
                            arrProducts.add(products);
                            arrProductIds.add(productId);
                        }


                    }
                }


                progressBar.setVisibility(GONE);

                if(arrProducts.isEmpty())
                {
                    tv_textPlaceholder.setVisibility(View.VISIBLE);
                }

                adapterStoreProductsItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        rv_storeProducts = view.findViewById(R.id.rv_storeProducts);
        tv_textPlaceholder = view.findViewById(R.id.tv_textPlaceholder);

    }
}