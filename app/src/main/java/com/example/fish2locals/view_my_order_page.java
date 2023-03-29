package com.example.fish2locals;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterMyOrdersItem;
import Adapters.fragmentAdapterMyOrder;
import Adapters.fragmentAdapterViewAllStore;
import Models.Basket;
import Models.Orders;
import Models.Products;

public class view_my_order_page extends AppCompatActivity {

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private TextView tv_back;

    private FirebaseUser user;
    private DatabaseReference ordersDatabase;

    private Adapters.fragmentAdapterMyOrder fragmentAdapterMyOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_my_order_page);

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
                Intent intent = new Intent(view_my_order_page.this, homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);
            }
        });
    }

    private void generateTabs() {

    tab_layout.addTab(tab_layout.newTab().setText("In Transit"));
    tab_layout.addTab(tab_layout.newTab().setText("Completed"));
    tab_layout.addTab(tab_layout.newTab().setText("Canceled"));

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentAdapterMyOrder = new fragmentAdapterMyOrder(fragmentManager, getLifecycle());
    vp_viewPager2.setAdapter(fragmentAdapterMyOrder);
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

