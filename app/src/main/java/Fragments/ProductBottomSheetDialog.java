package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import Models.Products;

public class ProductBottomSheetDialog extends BottomSheetDialogFragment {

    TextView tv_productName, tv_productQuantity, tv_productPrice, tv_hasPickup, tv_hasOwnDelivery,
            tv_has3rdPartyDelivery;
    ImageView iv_productPhoto;

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

                    String fishImageName = products.getImageName();
                    String productName = products.getFishName();
                    int productQuantity = products.getQuantityByKilo();
                    double productPrice = products.getPricePerKilo();
                    boolean hasPickup = products.isHasPickup();
                    boolean hasOwnDelivery = products.isHasOwnDelivery();
                    boolean has3rdPartyDelivery = products.isHas3rdPartyDelivery();

                    int imageResource = getContext().getResources().getIdentifier(fishImageName,
                            "drawable", getContext().getPackageName());


                    Picasso.get()
                            .load(imageResource)
                            .fit()
                            .centerCrop()
                            .into(iv_productPhoto);

                    tv_productName.setText(productName);
                    tv_productQuantity.setText(productQuantity + " kilogram/s remaining.");
                    tv_productPrice.setText("₱ " + productPrice + " / Kg");

                    if(hasPickup == true)
                    {
                        tv_hasPickup.setVisibility(View.VISIBLE);
                        tv_hasPickup.setText("• Pickup");
                    }

                    if(hasOwnDelivery == true)
                    {
                        tv_hasOwnDelivery.setVisibility(View.VISIBLE);
                        tv_hasOwnDelivery.setText("• We Deliver");
                    }

                    if(has3rdPartyDelivery == true)
                    {
                        tv_has3rdPartyDelivery.setVisibility(View.VISIBLE);
                        tv_has3rdPartyDelivery.setText("• 3rd Party Delivery (Maxim, Angkas, etc.)");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void clicks() {
        tv_productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( getContext(), "bottom sheet clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRef(View view) {
        tv_productName = view.findViewById(R.id.tv_productName);

        tv_productQuantity = view.findViewById(R.id.tv_productQuantity);
        tv_productPrice = view.findViewById(R.id.tv_productPrice);
        tv_hasPickup = view.findViewById(R.id.tv_hasPickup);
        tv_hasOwnDelivery = view.findViewById(R.id.tv_hasOwnDelivery);
        tv_has3rdPartyDelivery = view.findViewById(R.id.tv_has3rdPartyDelivery);

        iv_productPhoto = view.findViewById(R.id.iv_productPhoto);
    }
}
