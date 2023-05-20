package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import Models.Orders;
import Models.Ratings;
import Models.Store;
import Models.Users;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class rate_store_seller_page extends AppCompatActivity {

    private ArrayList<String> arrOrderSnapshotId = new ArrayList<>();
    private ArrayList<Double> arrSellerRatings = new ArrayList<>();
    private ArrayList<Double> arrStoreRatings = new ArrayList<>();

    private ImageView iv_sellerPhoto, iv_storePhoto;
    private TextView tv_backBtn, tv_sellerName, tv_storeName, btn_ratingSubmit;
    private EditText et_ratingCommentUser, et_ratingCommentStore;
    private RatingBar ratingBarUser, ratingBarStore;

    private FirebaseUser user;
    private DatabaseReference  ratingDatabase, ordersDatabase, storeDatabase, userDatabase;

    private String myUserId, orderId, storeId, sellerUserId;
    private int productCount = 0;
    private double averageSellerRating = 0, averageStoreRating = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_store_seller_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        orderId = getIntent().getStringExtra("orderId");
        storeId = getIntent().getStringExtra("storeId");
        sellerUserId = getIntent().getStringExtra("sellerUserId");

        setRef(); // initialize UI Id's
        generateSellerReviews();
        generateStoreReviews();
        generateProductCount();
        generateSellersData();
        generateStoresData();
        clicks();
    }


    private void generateSellerReviews() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(sellerUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;
                double totalRating = 0, tempRatingValue = 0;

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        tempRatingValue = ratings.getRatingValue();
                        totalRating = totalRating + tempRatingValue;
                        counter++;

                        arrSellerRatings.add(tempRatingValue);
                    }

                    averageSellerRating = totalRating / counter;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateStoreReviews() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(storeId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;
                double totalRating = 0, tempRatingValue = 0;

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        tempRatingValue = ratings.getRatingValue();
                        totalRating = totalRating + tempRatingValue;
                        counter++;

                        arrStoreRatings.add(tempRatingValue);
                    }

                    averageStoreRating = totalRating / counter;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clicks() {

        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        btn_ratingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float getRatingUser = ratingBarUser.getRating();
                float getRatingStore = ratingBarStore.getRating();

                new SweetAlertDialog(rate_store_seller_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("SUBMIT REVIEW")
                        .setCancelText("Back")
                        .setConfirmButton("Submit", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                final ProgressDialog progressDialog = new ProgressDialog(rate_store_seller_page.this);
                                progressDialog.setTitle("Submitting review...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                submitRating(getRatingUser, getRatingStore);

                            }
                        })
                        .setContentText("Submit your rating \n and reviews?")
                        .show();


            }
        });
    }

    private void generateProductCount() {

        Query query = ordersDatabase.orderByChild("orderId").equalTo(orderId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Orders orders = dataSnapshot.getValue(Orders.class);
                        String orderSnapshotId = dataSnapshot.getKey();

                        productCount++;

                        arrOrderSnapshotId.add(orderSnapshotId);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateSellersData() {

        userDatabase.child(sellerUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    String imageUrl = users.getImageUrl();
                    String fname = users.getFname();
                    String lname = users.getLname();


                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get()
                                .load(imageUrl)
                                .into(iv_sellerPhoto);
                    }

                    tv_sellerName.setText(fname +" "+ lname);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateStoresData() {

        storeDatabase.child(storeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Store store = snapshot.getValue(Store.class);

                    String storeImageUrl = store.getStoreUrl();
                    String storeName = store.getStoreName();

                    if(!storeImageUrl.isEmpty())
                    {
                        Picasso.get()
                                .load(storeImageUrl)
                                .centerCrop()
                                .fit()
                                .into(iv_storePhoto);
                    }

                    tv_storeName.setText(storeName);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void submitRating(float getRatingUser, float getRatingStore) {

//        String commentUsers = et_ratingCommentUser.getText().toString();
//        Ratings ratingsUsers = new Ratings(sellerUserId, myUserId, getRatingUser, commentUsers, orderId);

        String commentStore = et_ratingCommentStore.getText().toString();
        Ratings ratingsStore = new Ratings(storeId, myUserId, getRatingStore, commentStore, orderId);

//        ratingDatabase.push().setValue(ratingsUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//
//
//
//
//            }
//        });

        ratingDatabase.push().setValue(ratingsStore).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateOrderStatus(getRatingUser, getRatingStore);

            }
        });


    }

    private void updateOrderStatus(float getRatingUser, float getRatingStore) {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("rated", true);


        for(int i = 0; i < productCount; i++) {

            String orderSnapshotId = arrOrderSnapshotId.get(i);
            ordersDatabase.child(orderSnapshotId).updateChildren(hashMap);
        }

        updateAverageRating(getRatingUser, getRatingStore);

    }

    private void updateAverageRating(float getRatingUser, float getRatingStore) {


        // Update Seller's Ratings
//        double newAverageSellerRating = 0, tempSellerRatingValue;
//
//        for(int i = 0; i < arrSellerRatings.size(); i++)
//        {
//            tempSellerRatingValue = arrSellerRatings.get(i);
//            newAverageSellerRating = newAverageSellerRating + tempSellerRatingValue;
//
//        }
//
//        HashMap<String, Object> sellerRating = new HashMap<String, Object>();
//        sellerRating.put("rating", newAverageSellerRating);
//
//        userDatabase.child(sellerUserId).updateChildren(sellerRating);


        double newAverageStoreRating = 0, tempStoreRatingValue;

        for(int i = 0; i < arrStoreRatings.size(); i++)
        {
            tempStoreRatingValue = arrStoreRatings.get(i);
            newAverageStoreRating = newAverageStoreRating + tempStoreRatingValue;

        }

        // Update Store's Ratings

        HashMap<String, Object> storeRating = new HashMap<String, Object>();
        storeRating.put("ratings", newAverageStoreRating);

        storeDatabase.child(storeId).updateChildren(storeRating);

        SweetAlertDialog sDialog;
        sDialog = new SweetAlertDialog(rate_store_seller_page.this, SweetAlertDialog.SUCCESS_TYPE);
        sDialog.setTitleText("Your review has been submitted.");
        sDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                sDialog.dismiss();
                Intent intent = new Intent(rate_store_seller_page.this, homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);
            }
        });
        sDialog.show();
    }

    private void setRef() {

        iv_sellerPhoto = findViewById(R.id.iv_sellerPhoto);
        iv_storePhoto = findViewById(R.id.iv_storePhoto);

        tv_backBtn = findViewById(R.id.tv_backBtn);
        tv_sellerName = findViewById(R.id.tv_sellerName);
        tv_storeName = findViewById(R.id.tv_storeName);

        et_ratingCommentUser = findViewById(R.id.et_ratingCommentUser);
        et_ratingCommentStore = findViewById(R.id.et_ratingCommentStore);

        ratingBarUser = findViewById(R.id.ratingBarUser);
        ratingBarStore = findViewById(R.id.ratingBarStore);

        btn_ratingSubmit = findViewById(R.id.btn_ratingSubmit);

    }
}