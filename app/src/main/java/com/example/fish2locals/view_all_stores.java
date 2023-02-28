package com.example.fish2locals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import Adapters.fragmentAdapterViewAllStore;
import Adapters.fragmentAdapterViewStoreTabs;

public class view_all_stores extends AppCompatActivity {

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private TextView tv_back;

    private fragmentAdapterViewAllStore fragmentAdapterViewAllStore;

    private FirebaseUser user;

    private String storeId, storeOwnersUserId, myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_stores);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        setRef();
        generateTabs();
        clicks();
    }

    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void generateTabs() {

        tab_layout.addTab(tab_layout.newTab().setText("List View"));
        tab_layout.addTab(tab_layout.newTab().setText("Map View"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapterViewAllStore = new fragmentAdapterViewAllStore(fragmentManager, getLifecycle());
        vp_viewPager2.setAdapter(fragmentAdapterViewAllStore);
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
        tv_back = findViewById(R.id.tv_back);
    }
}