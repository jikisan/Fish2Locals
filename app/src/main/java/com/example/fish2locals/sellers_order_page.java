package com.example.fish2locals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Adapters.fragmentAdapterMyOrder;
import Adapters.fragmentAdapterSellersOrder;

public class sellers_order_page extends AppCompatActivity {

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private TextView tv_back;

    private FirebaseUser user;
    private DatabaseReference ordersDatabase;

    private Adapters.fragmentAdapterSellersOrder fragmentAdapterSellersOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellers_order_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");


        setRef();
        generateTabs();
        click();
    }

    private void click() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(sellers_order_page.this, seller_homepage.class);
                startActivity(intent);
            }
        });
    }

    private void generateTabs() {

        tab_layout.addTab(tab_layout.newTab().setText("To Ship"));
        tab_layout.addTab(tab_layout.newTab().setText("In Transit"));
        tab_layout.addTab(tab_layout.newTab().setText("Completed"));
        tab_layout.addTab(tab_layout.newTab().setText("Canceled"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapterSellersOrder = new fragmentAdapterSellersOrder(fragmentManager, getLifecycle());
        vp_viewPager2.setAdapter(fragmentAdapterSellersOrder);
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