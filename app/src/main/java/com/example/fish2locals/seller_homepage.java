package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import Fragments.Home_Fragment;
import Fragments.Messages_Fragment;
import Fragments.Notification_Fragment;
import Fragments.Add_Product_Fragment;
import Fragments.Seller_Home_Fragment;
import Fragments.Seller_Profile_Fragment;

public class seller_homepage extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    private String pageNumber;

    Seller_Home_Fragment seller_home_fragment = new Seller_Home_Fragment();
    Messages_Fragment messages_fragment = new Messages_Fragment();
    Add_Product_Fragment search_fragment = new Add_Product_Fragment();
    Notification_Fragment notification_fragment = new Notification_Fragment();
    Seller_Profile_Fragment seller_profile_fragment = new Seller_Profile_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_homepage);

        pageNumber = getIntent().getStringExtra("pageNumber");

        bottomNavigationView = findViewById(R.id.seller_bottom_nav);

        if(pageNumber != null) {

            if(pageNumber.equals("5"))

            {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, seller_profile_fragment);
                fragmentTransaction.commitNow();

                bottomNavigationView.setSelectedItemId(R.id.profile);
            }

        }
        else {

            getSupportFragmentManager().beginTransaction().replace(R.id.container,seller_home_fragment).commit();
        }


        clicks();
    }

    private void clicks() {

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,seller_home_fragment).commit();
                        return true;

                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,notification_fragment).commit();
                        return true;

                    case R.id.add_product:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,search_fragment).commit();
                        return true;

                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,seller_profile_fragment).commit();
                        return true;

                    case R.id.messages:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,messages_fragment).commit();
                        return true;
                    default:




                }
                return false;

            }
        });

    }
}