package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fish2locals.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Models.Products;

public class ProductBottomSheetDialog extends BottomSheetDialogFragment {

    private TextView tv_bottomSheet;

    private FirebaseUser user;
    private DatabaseReference productDatabase;

    private String myUserId, storeOwnersUserId, storeId, productId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_bottom_sheet,
                container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");

        productId = getArguments().getString("productId");
        storeOwnersUserId = getActivity().getIntent().getStringExtra("storeOwnersUserId");
        storeId = getActivity().getIntent().getStringExtra("storeId");

        setRef(view);
        clicks();
        generateProductData();


        return view;
    }

    private void generateProductData() {

        productDatabase.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                        Products products = snapshot.getValue(Products.class);
                        String productName = products.getFishName();

                        tv_bottomSheet.setText(productName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void clicks() {
        tv_bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( getContext(), "bottom sheet clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRef(View view) {
        tv_bottomSheet = view.findViewById(R.id.tv_bottomSheet);
    }
}
