package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fish2locals.R;
import com.example.fish2locals.seller_homepage;
import com.example.fish2locals.view_my_products_page;
import com.example.fish2locals.view_my_wallet_page;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterMostTrusted;
import Adapters.AdapterStoresNearMe;
import Models.Store;
import Models.TempStoreData;
import Models.Users;
import Objects.TextModifier;

public class Seller_Home_Fragment extends Fragment {

    private ImageView iv_userPhoto;
    private TextView tv_fName;
    private LinearLayout layout_myProducts, layout_myOrders, layout_myWallet, layout_myStatistics;

    private FirebaseUser user;
    private DatabaseReference userDatabase, storeDatabase;

    private String myUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller__home_, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef(view);
        generateUsersData();
        clicks();
        return view;
    }

    private void generateUsersData() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    String imageUrl = users.getImageUrl();

                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get()
                                .load(imageUrl)
                                .into(iv_userPhoto);
                    }


                    TextModifier textModifier = new TextModifier();
                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();
                    tv_fName.setText("Hello " + fname + "!");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void clicks() {

        iv_userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), seller_homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);

            }
        });

        layout_myProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), view_my_products_page.class);
                startActivity(intent);

            }
        });

        layout_myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( getContext(), "My Orders", Toast.LENGTH_SHORT).show();
            }
        });

        layout_myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), view_my_wallet_page.class);
                startActivity(intent);
            }
        });

        layout_myStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( getContext(), "My Statistics", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRef(View view) {


        iv_userPhoto = view.findViewById(R.id.iv_userPhoto);
        tv_fName = view.findViewById(R.id.tv_fName);

        layout_myProducts = view.findViewById(R.id.layout_myProducts);
        layout_myOrders = view.findViewById(R.id.layout_myOrders);
        layout_myWallet = view.findViewById(R.id.layout_myWallet);
        layout_myStatistics = view.findViewById(R.id.layout_myStatistics);


    }
}