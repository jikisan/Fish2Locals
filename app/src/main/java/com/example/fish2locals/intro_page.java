package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

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

public class intro_page extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference userDatabase;

    private ProgressBar progressbar;
    private String myUserId;
    private boolean sellerMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        progressbar = findViewById(R.id.progressbar);

        checkSellerModeStatus();


        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(!(user == null))
                {
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


                }else{

                    Intent intent;
                    intent = new Intent(intro_page.this, login_page.class);
                    startActivity(intent);
                    finish();
                }


            }
        }, 2000);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}