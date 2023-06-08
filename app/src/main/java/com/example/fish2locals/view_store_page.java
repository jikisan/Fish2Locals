package com.example.fish2locals;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
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
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.fragmentAdapterViewStoreTabs;
import Models.Basket;
import Models.Store;
import Objects.TextModifier;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class view_store_page extends AppCompatActivity {

    private static final long TWO_HOURS = 1000 * 60 * 60 * 2 ; // 2 hours in milliseconds
    private long minutes = 0, timeDifference;
    private String TARGET_TIME;
    private boolean isTwoHoursPassed = false;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private ImageView iv_storeBanner, iv_back;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tv_myBasketButton;

    private List<Store> arrStore = new ArrayList<>();
    private List<Basket> arrBasket = new ArrayList<>();
    private final List<String> arrBasketId = new ArrayList<>();
    private fragmentAdapterViewStoreTabs fragmentAdapterViewStoreTabs;

    private FirebaseUser user;
    private DatabaseReference userDatabase, storeDatabase, basketDatabase;

    private String storeId, storeOwnersUserId, myUserId, fromWherePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");

        storeOwnersUserId = getIntent().getStringExtra("storeOwnersUserId");
        storeId = getIntent().getStringExtra("storeId");
        fromWherePage = getIntent().getStringExtra("fromWherePage");

        setRef();
        clicks();
        generateStoreData();
        generateBasketData();

        generateTabs();
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
                        Intent intent = new Intent(view_store_page.this, homepage.class);
                        startActivity(intent);

                    }


                }
                else
                {
                    new SweetAlertDialog(view_store_page.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning!")
                            .setCancelText("No")
                            .setConfirmButton("End", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    Intent intent = new Intent(view_store_page.this, homepage.class);
                                    startActivity(intent);
                                }
                            })
                            .setContentText("Your basket has\n" +
                                    arrBasket.size() + " item/s.\n" +
                                    "Basket will be deleted after 2 hours.")
                            .show();
                }

            }
        });

        tv_myBasketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(arrBasket.isEmpty())
                {
                    Toast.makeText(view_store_page.this, "Basket is empty. Please choose a product.", Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent intent = new Intent(view_store_page.this, my_basket_page.class);
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

                    Intent intent = new Intent(view_store_page.this, homepage.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateBasketData() {

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Execute your desired function here


                        Query query = basketDatabase.orderByChild("storeId").equalTo(storeId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.exists())
                                {
                                    arrBasket.clear();

                                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                    {
                                        Basket basket = dataSnapshot.getValue(Basket.class);
                                        String buyerUserId = basket.getBuyerUserId();


                                        try {
                                            if(buyerUserId.equals(myUserId))
                                            {
                                                arrBasket.add(basket);

                                                String basketId = dataSnapshot.getKey();
                                                arrBasketId.add(basketId);
                                            }
                                        }catch (Exception e){
                                            System.out.println("Error basket: " + e);
                                        }


                                    }

                                    int basketCount = arrBasket.size();
                                    if(basketCount == 0)
                                    {
                                        tv_myBasketButton.setText("Empty basket");
                                    }


                                    tv_myBasketButton.setText("GO TO MY BASKET (" + basketCount + " item/s )");
                                    autoDeleteBasketAfterToHours();

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
                });


            }
        }, 0, 1000 );


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

                        collapsingToolbar.setTitle(storeName);

                        if(!bannerUrl.isEmpty())
                        {
                            Picasso.get()
                                    .load(bannerUrl)
                                    .centerCrop()
                                    .fit()
                                    .into(iv_storeBanner);
                        }

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
        fragmentAdapterViewStoreTabs = new fragmentAdapterViewStoreTabs(fragmentManager, getLifecycle());
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

        collapsingToolbar = findViewById(R.id.collapsingToolbar);

        tv_myBasketButton = findViewById(R.id.tv_myBasketButton);
    }


    private void autoDeleteBasketAfterToHours() {
        // Execute your desired function here

        for(int i = 0; i < arrBasketId.size(); i++)
        {
            hasTwoHoursPassed(i);

            if (isTwoHoursPassed) {
                manageDatabase(i);
            }
        }



    }

    // check the current world time
    private void hasTwoHoursPassed(int i) {

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


        if(timeDifference >= TWO_HOURS)
        {
            isTwoHoursPassed = true;
        }
        else
        {
            isTwoHoursPassed = false;
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