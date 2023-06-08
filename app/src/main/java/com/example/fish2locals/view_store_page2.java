package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.fragmentAdapterViewStoreTabs;
import Models.Basket;
import Models.InTransitOrders;
import Models.Store;
import Objects.TextModifier;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class view_store_page2 extends AppCompatActivity {

    private static final long FIVE_MINTUES = 1000; // 5 mins in milliseconds
    private long minutes = 0, timeDifference;
    private String TARGET_TIME;
    private boolean isFiveMinutesPassed = false;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private ImageView iv_storeBanner, iv_back;
    private TextView tv_myBasketButton;

    private final List<Basket> arrBasket = new ArrayList<>();
    private final List<String> arrBasketId = new ArrayList<>();

    private DatabaseReference storeDatabase, basketDatabase;

    private String storeId, storeOwnersUserId, myUserId, fromWherePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_page2);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");

        storeOwnersUserId = getIntent().getStringExtra("storeOwnersUserId");
        storeId = getIntent().getStringExtra("storeId");
        fromWherePage = getIntent().getStringExtra("fromWherePage");

        setRef(); // initialize UI ID's
//        clicks(); // buttons
//        generateStoreData(); // generate store data
//        generateBasketData(); // generate basket data
//        autoDeleteBasketAfterFiveMinutes();
//        generateTabs(); // generate tab layout

    }

    private void clicks(){

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(arrBasket.isEmpty())
                {

                    if(fromWherePage == null || fromWherePage.isEmpty())
                    {
                        onBackPressed();
                    }
                    else if(fromWherePage.equals("fromAddToBasketPage"))
                    {
                        Intent intent = new Intent(view_store_page2.this, homepage.class);
                        startActivity(intent);

                    }


                }
                else
                {
                    new SweetAlertDialog(view_store_page2.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning!")
                            .setCancelText("No")
                            .setConfirmButton("End", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

//                                    deleteBasketData();
                                    Intent intent = new Intent(view_store_page2.this, homepage.class);
                                    startActivity(intent);
                                }
                            })
                            .setContentText("Your basket has\n" +
                                    arrBasket.size() + " item/s.\n" +
                                    "Basket will be deleted after 30000 minutes.")
                            .show();
                }

            }
        });

        tv_myBasketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(arrBasket.isEmpty())
                {
                    Toast.makeText(view_store_page2.this, "Basket is empty. Please choose a product.", Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent intent = new Intent(view_store_page2.this, my_basket_page.class);
                    intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                    intent.putExtra("storeId", storeId);
                    startActivity(intent);
                }

            }
        });

    }

    private void deleteBasketData() {

        Query query = basketDatabase.orderByChild("buyerUserId").equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        dataSnapshot.getRef().removeValue();
                    }

                    Intent intent = new Intent(view_store_page2.this, homepage.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateBasketData() {

        Query query = basketDatabase.orderByChild("storeId").equalTo(storeId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrBasket.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Basket basket = dataSnapshot.getValue(Basket.class);

                        String buyerUserId = basket.getBuyerUserId();

                        if(buyerUserId.equals(myUserId))
                        {
                            arrBasket.add(basket);

                            String basketId = dataSnapshot.getKey();
                            arrBasketId.add(basketId);
                        }

                    }

                    int basketCount = arrBasket.size();

                    if(basketCount == 0)
                    {
                        tv_myBasketButton.setText("Empty basket");
                    }

                    tv_myBasketButton.setText("GO TO MY BASKET (" + basketCount + " item/s )");

                }
                else
                {
                    arrBasket.clear();
                    tv_myBasketButton.setText("Empty basket");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateStoreData() {

        storeDatabase.child(storeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    Store store = snapshot.getValue(Store.class);

                    TextModifier textModifier = new TextModifier();
                    textModifier.setSentenceCase(store.getStoreName());

                    String storeName = textModifier.getSentenceCase();
                    String bannerUrl = store.getStoreUrl();


                    Picasso.get()
                            .load(bannerUrl)
                            .centerCrop()
                            .fit()
                            .into(iv_storeBanner);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateTabs() {

        tab_layout.addTab(tab_layout.newTab().setText("Products"));
        tab_layout.addTab(tab_layout.newTab().setText("Info"));
        tab_layout.addTab(tab_layout.newTab().setText("Reviews"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Adapters.fragmentAdapterViewStoreTabs fragmentAdapterViewStoreTabs = new fragmentAdapterViewStoreTabs(fragmentManager, getLifecycle());
        vp_viewPager2.setAdapter(fragmentAdapterViewStoreTabs);
        vp_viewPager2.setUserInputEnabled(false);


        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tab_layout.selectTab(tab_layout.getTabAt(position));
            }
        });
    }

    private void setRef() {

        tab_layout = findViewById(R.id.tab_layout);
        vp_viewPager2 = findViewById(R.id.vp_viewPager2);

        iv_storeBanner = findViewById(R.id.iv_storeBanner);
        iv_back = findViewById(R.id.iv_back);

        tv_myBasketButton = findViewById(R.id.tv_myBasketButton);
    }




    private void autoDeleteBasketAfterFiveMinutes() {
        // Execute your desired function here



            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Execute your desired function here

                            for(int i = 0; i < arrBasketId.size(); i++)
                            {
                                hasThreeHoursPassed(i);

                                if (isFiveMinutesPassed) {
                                    manageDatabase(i);
                                }
                            }


                        }
                    });


                }
            }, 0, 5000);



    }

    // check the current world time
    private void hasThreeHoursPassed(int i) {

        String[] basketIdSplit = arrBasketId.get(i).split("-");


        SimpleDateFormat formatTime = new SimpleDateFormat("yyyyMMddhhmma");

        Date dateTime = Calendar.getInstance().getTime();
        String dateTimeInString = DateFormat.getDateTimeInstance().format(dateTime);

        String currentTimeInString = formatTime.format(Date.parse(dateTimeInString));

        TARGET_TIME = basketIdSplit[2];

        try
        {
            Date currentTime = formatTime.parse(currentTimeInString);
            Date targetTime = formatTime.parse(TARGET_TIME);

            long currentTimeInMillis = currentTime.getTime();
            long targetTimeInMillis = targetTime.getTime();

            timeDifference = currentTimeInMillis - targetTimeInMillis;
            minutes = timeDifference / 60000;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        if(timeDifference >= FIVE_MINTUES)
        {
            isFiveMinutesPassed = true;
        }
        else
        {
            isFiveMinutesPassed = false;
        }
    }

    private void manageDatabase(int i) {

        String basketId = arrBasketId.get(i);

        basketDatabase.child(basketId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        dataSnapshot.getRef().removeValue();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}