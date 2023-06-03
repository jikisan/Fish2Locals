package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.fragmentAdapterMyOrder;
import Models.InTransitOrders;

public class view_my_order_page extends AppCompatActivity {

    private static final long THREE_HOURS = 3 * 60 * 60 * 1000; // 3 hours in milliseconds
    private long minutes = 0, timeDifference;
    private String TARGET_TIME, myUserId;
    private List<InTransitOrders> inTransitOrdersArrayList = new ArrayList<>();
    private boolean isThirtyMinutesPassed = false;

    private TabLayout tab_layout;
    private ViewPager2 vp_viewPager2;
    private TextView tv_back;

    private FirebaseUser user;
    private DatabaseReference ordersDatabase, inTransitOrdersDatabase;

    private Adapters.fragmentAdapterMyOrder fragmentAdapterMyOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_my_order_page);
//        new CheckTimeTask().execute();

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        inTransitOrdersDatabase = FirebaseDatabase.getInstance().getReference("InTransitOrders");

        setRef();
        generateTabs();
        generateInTransitOrders();
        autoReceiveOrderIfBuyerDidNotAcceptTheOrder();
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

    tab_layout.addTab(tab_layout.newTab().setText("To Ship"));
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


    private void autoReceiveOrderIfBuyerDidNotAcceptTheOrder() {
        // Execute your desired function here

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Execute your desired function here

                        for(int i = 0; i < inTransitOrdersArrayList.size(); i++)
                        {
                            hasThirtyMinutesPassed(i);

                            if (isThirtyMinutesPassed) {
                                manageDatabase(i);
                            }
                        }


                    }
                });


            }
        }, 0, 5000);

    }

    // check the current world time
    private void hasThirtyMinutesPassed(int i) {

        SimpleDateFormat formatTime = new SimpleDateFormat("yyyyMMddhhmma");

        Date dateTime = Calendar.getInstance().getTime();
        String dateTimeInString = DateFormat.getDateTimeInstance().format(dateTime);

        String currentTimeInString = formatTime.format(Date.parse(dateTimeInString));

        TARGET_TIME = inTransitOrdersArrayList.get(i).getTimeInTransit();

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


        if(timeDifference >= THREE_HOURS)
        {
            isThirtyMinutesPassed = true;
        }
        else
        {
            isThirtyMinutesPassed = false;
        }
    }

    private void manageDatabase(int i) {


        String orderSnapshotIds = inTransitOrdersArrayList.get(i).getOrderId();

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("orderStatus", "2");
        ordersDatabase.child(orderSnapshotIds).updateChildren(hashMap);



        Query query = inTransitOrdersDatabase.orderByChild("orderId").equalTo(orderSnapshotIds);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        dataSnapshot.getRef().removeValue();
                    }

                    Intent intent = new Intent(view_my_order_page.this, homepage.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateInTransitOrders() {

        inTransitOrdersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        InTransitOrders inTransitOrders = dataSnapshot.getValue(InTransitOrders.class);

                        String buyersId = inTransitOrders.getBuyerId();

                        if(buyersId.equals(myUserId))
                        {
                            inTransitOrdersArrayList.add(inTransitOrders);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

