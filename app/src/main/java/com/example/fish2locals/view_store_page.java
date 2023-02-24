package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Adapters.fragmentAdapterViewStoreTabs;
import Models.Store;
import Objects.TextModifier;

public class view_store_page extends AppCompatActivity {

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private ImageView iv_storeBanner, iv_back;
    private CollapsingToolbarLayout collapsingToolbar;

    private List<Store> arrStore = new ArrayList<>();
    private fragmentAdapterViewStoreTabs fragmentAdapterViewStoreTabs;

    private FirebaseUser user;
    private DatabaseReference userDatabase, storeDatabase;

    private String storeId, storeOwnersUserId, myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");

        storeOwnersUserId = getIntent().getStringExtra("storeOwnersUserId");
        storeId = getIntent().getStringExtra("storeId");

        setRef();
        clicks();
        generateStoreData();
        generateTabs();
    }

    private void clicks() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

        tab_layout.addTab(tab_layout.newTab().setText("Info"));
        tab_layout.addTab(tab_layout.newTab().setText("Products"));
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
    }
}