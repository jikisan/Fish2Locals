package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fish2locals.R;
import com.example.fish2locals.about_us_page;
import com.example.fish2locals.change_password_page;
import com.example.fish2locals.edit_profile_page;
import com.example.fish2locals.homepage;
import com.example.fish2locals.intro_page;
import com.example.fish2locals.seller_application;
import com.example.fish2locals.seller_homepage;
import com.example.fish2locals.view_my_bookmarks_page;
import com.example.fish2locals.view_my_order_page;
import com.example.fish2locals.view_my_wallet_page;
import com.example.fish2locals.view_privacy_and_policy_page;
import com.example.fish2locals.view_ratings_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Models.Ratings;
import Models.Users;
import Objects.TextModifier;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Profile_Fragment extends Fragment {

    private Button btn_applyAsSeller;
    private ImageView iv_bannerPhoto;
    private Switch sw_switchBtn;
    private TextView tv_logout, tv_fName, tv_editProfile, tv_myRatings, tv_changePassword,
             tv_aboutUs, tv_privacyPolicy, tv_userRating;
    private RatingBar rb_userRating;
    private LinearLayout layout_myOrders, layout_savedStores, layout_myWallet;

    private LinearLayout layout_noSeller, layout_hasSeller;

    private FirebaseUser user;
    private DatabaseReference userDatabase, ratingDatabase;

    private String myUserId;
    private boolean hasSellerAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");


        setRef(view);
        generateReviews();
        generateUsersData();
        checkIfHasSellerAccount();
        clicks();

        return view;
    }

    private void generateReviews() {

        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;
                double totalRating = 0, tempRatingValue = 0, averageRating = 0;

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        tempRatingValue = ratings.getRatingValue();
                        totalRating = totalRating + tempRatingValue;
                        counter++;
                    }

                    averageRating = totalRating / counter;
                    String ratingCounter = "(" + String.valueOf(counter) + ")";
                    tv_userRating.setText(ratingCounter);
                    rb_userRating.setRating((float) averageRating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                                .into(iv_bannerPhoto);
                    }


                    TextModifier textModifier = new TextModifier();
                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lname = textModifier.getSentenceCase();
                    tv_fName.setText(fname + " " + lname);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkIfHasSellerAccount() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    hasSellerAccount = users.isHasSellerAccount();

                    if(hasSellerAccount == true)
                    {
                        layout_hasSeller.setVisibility(View.VISIBLE);
                        layout_noSeller.setVisibility(View.GONE);
                    }
                    else if (hasSellerAccount == false)
                    {
                        layout_hasSeller.setVisibility(View.GONE);
                        layout_noSeller.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clicks() {

        btn_applyAsSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), seller_application.class);
                startActivity(intent);
            }
        });

        sw_switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sw_switchBtn.setChecked(true);

                boolean sellerMode = true;

                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("sellerMode", sellerMode);

                userDatabase.child(myUserId).updateChildren(hashMap);

                Intent intent = new Intent(getContext(), seller_homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);
            }
        });

        layout_myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), view_my_order_page.class);
                startActivity(intent);
            }
        });

        layout_savedStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), view_my_bookmarks_page.class);
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


        tv_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), edit_profile_page.class);
                startActivity(intent);
            }
        });

        tv_myRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), view_ratings_page.class);
                intent.putExtra("ratingsId", myUserId);
                startActivity(intent);
            }
        });


        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), change_password_page.class);
                intent.putExtra("User Email", user.getEmail());
                startActivity(intent);

            }
        });

        tv_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), about_us_page.class);
                startActivity(intent);
            }
        });

        tv_privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), view_privacy_and_policy_page.class);
                startActivity(intent);
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!")
                        .setCancelText("Cancel")
                        .setConfirmButton("Log Out", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), intro_page.class));


                            }
                        })
                        .setContentText("Are you sure \nyou want to logout?")
                        .show();

            }
        });


    }

    private void setRef(View view) {

        btn_applyAsSeller = view.findViewById(R.id.btn_applyAsSeller);
        sw_switchBtn = view.findViewById(R.id.sw_switchBtn);

        layout_hasSeller = view.findViewById(R.id.layout_hasSeller);
        layout_noSeller = view.findViewById(R.id.layout_noSeller);

        iv_bannerPhoto = view.findViewById(R.id.iv_bannerPhoto);

        tv_fName = view.findViewById(R.id.tv_fName);
        tv_editProfile = view.findViewById(R.id.tv_editProfile);
        tv_myRatings = view.findViewById(R.id.tv_myRatings);
        tv_changePassword = view.findViewById(R.id.tv_changePassword);
        tv_aboutUs = view.findViewById(R.id.tv_aboutUs);
        tv_privacyPolicy = view.findViewById(R.id.tv_privacyPolicy);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_userRating = view.findViewById(R.id.tv_userRating);

        rb_userRating = view.findViewById(R.id.rb_userRating);

        layout_myOrders = view.findViewById(R.id.layout_myOrders);
        layout_savedStores = view.findViewById(R.id.layout_savedStores);
        layout_myWallet = view.findViewById(R.id.layout_myWallet);

    }
}