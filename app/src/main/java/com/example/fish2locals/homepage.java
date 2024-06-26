package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import Fragments.Home_Fragment;
import Fragments.Messages_Fragment;
import Fragments.Notification_Fragment;
import Fragments.Profile_Fragment;
import Fragments.Add_Product_Fragment;

public class homepage extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    private String pageNumber;

    Home_Fragment home_fragment = new Home_Fragment();
    Messages_Fragment messages_fragment = new Messages_Fragment();
    Add_Product_Fragment search_fragment = new Add_Product_Fragment();
    Notification_Fragment notification_fragment = new Notification_Fragment();
    Profile_Fragment profile_fragment = new Profile_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        pageNumber = getIntent().getStringExtra("pageNumber");

        bottomNavigationView = findViewById(R.id.bottom_nav);
        fab = findViewById(R.id.fab);

        if(pageNumber != null) {

            if(pageNumber.equals("5"))

            {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, profile_fragment);
                fragmentTransaction.commitNow();

                bottomNavigationView.setSelectedItemId(R.id.profile);
            }

        }
        else {

            getSupportFragmentManager().beginTransaction().replace(R.id.container,home_fragment).commit();
        }


        clicks();
    }

    private void clicks() {

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,home_fragment).commit();
                        return true;

                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,notification_fragment).commit();
                        return true;

                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,profile_fragment).commit();
                        return true;

                    case R.id.messages:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,messages_fragment).commit();
                        return true;
                    default:


                }
                return false;

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(homepage.this, marketplace_page.class);
                startActivity(intent);

            }
        });
    }
}