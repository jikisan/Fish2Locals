package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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

import java.util.Timer;
import java.util.TimerTask;

import Models.Users;
import Models.Wallets;

public class intro_page extends AppCompatActivity {

    private FusedLocationProviderClient client;
    private double myLatDouble, myLongDouble, distance;

    private FirebaseUser user;
    private DatabaseReference userDatabase, walletDatabase, basketDatabase;

    private ProgressBar progressbar;
    private String myUserId;
    private boolean sellerMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);

        user = FirebaseAuth.getInstance().getCurrentUser();

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");


        progressbar = findViewById(R.id.progressbar);



        validatePermissionToUseLocation();

    }

    private void validatePermissionToUseLocation() {

        // check condition
        if (ContextCompat.checkSelfPermission(intro_page.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(intro_page.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            checkUserStatus();


        } else {
            // When permission is not granted
            // Call method
            Toast.makeText(this, "Location is required, please turn it on.", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(

                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

//                validatePermissionToUseLocation();
                checkUserStatus();
            }

        }
    }

    private void checkUserStatus() {

        if(!(user == null))
        {
            myUserId = user.getUid();
            checkIfWalletExist(myUserId);

        }else{

            Intent intent;
            intent = new Intent(intro_page.this, login_page.class);
            startActivity(intent);
            finish();
        }
    }

    private void deleteBasketData(String myUserId) {

        Query query = basketDatabase.orderByChild("buyerUserId").equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    checkIfWalletExist(myUserId);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkIfWalletExist(String myUserId) {

        Query query = walletDatabase.orderByChild("userID").equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    checkSellerModeStatus();


                }
                else
                {
                    createWallet(myUserId);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createWallet(String myUserId) {

        double i = 0;

        Wallets wallets = new Wallets(myUserId, i);

        walletDatabase.push().setValue(wallets).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                checkSellerModeStatus();

            }
        });
    }

    private void checkSellerModeStatus() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    sellerMode = users.isSellerMode();

                }
                    startSplashScreen();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startSplashScreen() {

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(!sellerMode)
                {
                    Intent intent = new Intent(intro_page.this, homepage.class);
                    startActivity(intent);
                    finish();
                }
                else if (sellerMode)
                {
                    Intent intent = new Intent(intro_page.this, seller_homepage.class);
                    startActivity(intent);
                    finish();
                }


            }
        }, 2000);
    }

}