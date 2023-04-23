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
import com.example.fish2locals.seller_statistics_page;
import com.example.fish2locals.sellers_order_page;
import com.example.fish2locals.view_my_order_page;
import com.example.fish2locals.view_my_products_page;
import com.example.fish2locals.view_my_wallet_page;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterMostTrusted;
import Adapters.AdapterStoresNearMe;
import Models.Orders;
import Models.Store;
import Models.TempStoreData;
import Models.Users;
import Objects.TextModifier;

public class Seller_Home_Fragment extends Fragment {

    private ImageView iv_userPhoto;
    private TextView tv_fName, tv_totalSales;
    private LinearLayout layout_myProducts, layout_myOrders, layout_myWallet, layout_myStatistics;

    private FirebaseUser user;
    private DatabaseReference userDatabase, storeDatabase, orderDatabase;

    private String myUserId;
    double totalPricePerKilo = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller__home_, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");


        setRef(view); //initialize UI ID's
        generateUsersData(); //generate user data
        generateTotalSales(); // generate Total Sales
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

                    //get image from database
                    String imageUrl = users.getImageUrl();

                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get()
                                .load(imageUrl)
                                .into(iv_userPhoto);
                    }


                    //get first name from database
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

    private void generateTotalSales() {

        Query query = orderDatabase.orderByChild("sellerUserId")
                .equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    totalPricePerKilo = 0;

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Orders orders = dataSnapshot.getValue(Orders.class);
                        double pricePerKilo = orders.getPricePerKilo();

                        totalPricePerKilo = totalPricePerKilo + pricePerKilo;

                    }

                    String totalSaleInString = NumberFormat.getNumberInstance(Locale.US).format(totalPricePerKilo);

                    tv_totalSales.setText(totalSaleInString);
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

                Intent intent = new Intent(getContext(), sellers_order_page.class);
                startActivity(intent);
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
                Intent intent = new Intent(getContext(), seller_statistics_page.class);
                startActivity(intent);
            }
        });
    }

    private void setRef(View view) {


        iv_userPhoto = view.findViewById(R.id.iv_userPhoto);
        tv_fName = view.findViewById(R.id.tv_fName);
        tv_totalSales = view.findViewById(R.id.tv_totalSales);

        layout_myProducts = view.findViewById(R.id.layout_myProducts);
        layout_myOrders = view.findViewById(R.id.layout_myOrders);
        layout_myWallet = view.findViewById(R.id.layout_myWallet);
        layout_myStatistics = view.findViewById(R.id.layout_myStatistics);


    }
}