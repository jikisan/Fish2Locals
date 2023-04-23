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

public class rate_buyer_page extends AppCompatActivity {

    private ArrayList<String> arrOrderSnapshotId = new ArrayList<>();
    private ArrayList<Double> arrBuyerRatings = new ArrayList<>();

    private ImageView iv_buyerPhoto;
    private TextView tv_backBtn, tv_buyerName, btn_ratingSubmit;
    private EditText et_ratingCommentUser;
    private RatingBar ratingBarUser;

    private FirebaseUser user;
    private DatabaseReference ratingDatabase, userDatabase, ordersDatabase;

    private String myUserId, buyerUserId, orderId;
    private int productCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_buyer_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        orderId = getIntent().getStringExtra("orderId");
        buyerUserId = getIntent().getStringExtra("buyerUserId");

        setRef(); // initialize UI Id's
        generateBuyersReviews();
        generateBuyersData();
        clicks();

    }

    private void generateBuyersReviews() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(buyerUserId);

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

                        arrBuyerRatings.add(tempRatingValue);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateBuyersData() {

        userDatabase.child(buyerUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                .into(iv_buyerPhoto);
                    }

                    tv_buyerName.setText(fname +" "+ lname);
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

                new SweetAlertDialog(rate_buyer_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("SUBMIT REVIEW")
                        .setCancelText("Back")
                        .setConfirmButton("Submit", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                final ProgressDialog progressDialog = new ProgressDialog(rate_buyer_page.this);
                                progressDialog.setTitle("Submitting review...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                submitRating(getRatingUser);

                            }
                        })
                        .setContentText("Submit your rating \n and reviews?")
                        .show();


            }
        });
    }

    private void submitRating(float getRatingUser) {

        String commentUsers = et_ratingCommentUser.getText().toString();

        Ratings ratingsUsers = new Ratings(buyerUserId, myUserId, getRatingUser, commentUsers, orderId);

        ratingDatabase.push().setValue(ratingsUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                    updateOrderStatus();


            }
        });


    }

    private void updateOrderStatus() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("rated", true);

        for(int i = 0; i < productCount; i++)
        {

            String orderSnapshotId = arrOrderSnapshotId.get(i);
            ordersDatabase.child(orderSnapshotId).updateChildren(hashMap);
        }

        updateAverageRating();



    }

    private void updateAverageRating() {

        double newAverageBuyerRating = 0, tempBuyerRatingValue;

        for(int i = 0; i < arrBuyerRatings.size(); i++)
        {
            tempBuyerRatingValue = arrBuyerRatings.get(i);
            newAverageBuyerRating = newAverageBuyerRating + tempBuyerRatingValue;

        }

        HashMap<String, Object> sellerRating = new HashMap<String, Object>();
        sellerRating.put("rating", newAverageBuyerRating);

        userDatabase.child(buyerUserId).updateChildren(sellerRating);

        SweetAlertDialog sDialog;
        sDialog = new SweetAlertDialog(rate_buyer_page.this, SweetAlertDialog.SUCCESS_TYPE);
        sDialog.setTitleText("Your review has been submitted.");
        sDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                sDialog.dismiss();
                Intent intent = new Intent(rate_buyer_page.this, seller_homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);
            }
        });
        sDialog.show();
    }

    private void setRef() {

        iv_buyerPhoto = findViewById(R.id.iv_buyerPhoto);

        tv_backBtn = findViewById(R.id.tv_backBtn);
        tv_buyerName = findViewById(R.id.tv_buyerName);

        et_ratingCommentUser = findViewById(R.id.et_ratingCommentUser);

        ratingBarUser = findViewById(R.id.ratingBarUser);

        btn_ratingSubmit = findViewById(R.id.btn_ratingSubmit);

    }
}