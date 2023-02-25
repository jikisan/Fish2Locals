package Fragments;

import static android.view.View.GONE;

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
import android.widget.Toast;

import com.example.fish2locals.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

    private List<Products> arrProducts = new ArrayList<>();
    private List<String> arrProductIds = new ArrayList<>();
    private AdapterStoreProductsItem adapterStoreProductsItem;

    private ProgressBar progressBar;
    private RecyclerView rv_storeProducts;
    private TextView tv_textPlaceholder;

    private FirebaseUser user;
    private DatabaseReference productDatabase;

    private String myUserId, storeOwnersUserId, storeId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_products, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");

        storeOwnersUserId = getActivity().getIntent().getStringExtra("storeOwnersUserId");
        storeId = getActivity().getIntent().getStringExtra("storeId");

        setRef(view);
        generateRecyclerLayout();
        click();
        return view;
    }

    private void click() {

        adapterStoreProductsItem.setOnItemClickListener(new AdapterStoreProductsItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                arrProductIds.get(position);

                String productId = arrProductIds.get(position);


                Bundle bundle = new Bundle();
                bundle.putString("productId", productId);

                ProductBottomSheetDialog productBottomSheetDialog = new ProductBottomSheetDialog();
                productBottomSheetDialog.setArguments(bundle);
                productBottomSheetDialog.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
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

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        rv_storeProducts = view.findViewById(R.id.rv_storeProducts);
        tv_textPlaceholder = view.findViewById(R.id.tv_textPlaceholder);

    }
}