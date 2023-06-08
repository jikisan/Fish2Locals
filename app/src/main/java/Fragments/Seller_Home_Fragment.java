package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fish2locals.R;
import com.example.fish2locals.homepage;
import com.example.fish2locals.seller_homepage;
import com.example.fish2locals.seller_statistics_page;
import com.example.fish2locals.sellers_order_page;
import com.example.fish2locals.view_my_order_page;
import com.example.fish2locals.view_my_products_page;
import com.example.fish2locals.view_my_wallet_page;
import com.google.android.gms.location.LocationServices;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.AdapterMostTrusted;
import Adapters.AdapterNotificationItem;
import Adapters.AdapterPendingInTransit;
import Adapters.AdapterStoresNearMe;
import Models.InTransitOrders;
import Models.Notifications;
import Models.Orders;
import Models.Store;
import Models.TempStoreData;
import Models.Users;
import Objects.TextModifier;

public class Seller_Home_Fragment extends Fragment {

    private static final long THREE_HOURS = 1000 * 60 * 60 * 3; // 3 hours in milliseconds
    private long minutes = 0, timeDifference;
    private String TARGET_TIME;
    private List<InTransitOrders> inTransitOrdersArrayList = new ArrayList<>();
    private List<Orders> arrOrders = new ArrayList<>();
    private boolean isThirtyMinutesPassed = false;

    private AdapterPendingInTransit adapterPendingInTransit;

    private ImageView iv_userPhoto;
    private TextView tv_fName, tv_totalSales, tv_totalInTransit;
    private LinearLayout layout_myProducts, layout_myOrders, layout_myWallet, layout_myStatistics;
    private RecyclerView rv_inTransitProducts;

    private FirebaseUser user;
    private DatabaseReference userDatabase, orderDatabase, inTransitOrdersDatabase;

    private String myUserId;
    double totalPricePerKilo = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller__home_, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        inTransitOrdersDatabase = FirebaseDatabase.getInstance().getReference("InTransitOrders");


        setRef(view); //initialize UI ID's
        generateRecyclerLayout();
        generateUsersData(); //generate user data
        generateTotalSales(); // generate Total Sales
        generateInTransitOrders();
        autoReceiveOrderIfBuyerDidNotAcceptTheOrder();
        clicks();

        return view;
    }

    private void generateRecyclerLayout() {

        rv_inTransitProducts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_inTransitProducts.setLayoutManager(linearLayoutManager);

        adapterPendingInTransit = new AdapterPendingInTransit(arrOrders, getContext());
        rv_inTransitProducts.setAdapter(adapterPendingInTransit);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = orderDatabase.orderByChild("orderStatus").equalTo("1");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrOrders.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Orders orders = dataSnapshot.getValue(Orders.class);

                        arrOrders.add(orders);
                        tv_totalInTransit.setText(arrOrders.size() + "");
                    }
                }

                adapterPendingInTransit.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateUsersData() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);

                    //get image from database
                    String imageUrl = users.getImageUrl();

                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get()
                                .load(imageUrl)
                                .into(iv_userPhoto);
                    }


                    //get first name from database
                    TextModifier textModifier = new TextModifier();
                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();
                    tv_fName.setText("Hello " + fname + "!");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void generateTotalSales() {

        Query query = orderDatabase.orderByChild("sellerUserId")
                .equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    totalPricePerKilo = 0;

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Orders orders = dataSnapshot.getValue(Orders.class);
                        int quantity = orders.getQuantity();
                        double pricePerKilo = orders.getPricePerKilo();
                        String orderStatus = orders.getOrderStatus();

                        if(orderStatus.equals("2"))
                        {
                            totalPricePerKilo = totalPricePerKilo + (pricePerKilo * quantity);
                        }


                    }

                    String totalSaleInString = NumberFormat.getNumberInstance(Locale.US).format(totalPricePerKilo);

                    tv_totalSales.setText(totalSaleInString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void clicks() {

        iv_userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), seller_homepage.class);
                intent.putExtra("pageNumber", "5");
                startActivity(intent);

            }
        });

        layout_myProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), view_my_products_page.class);
                startActivity(intent);

            }
        });

        layout_myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), sellers_order_page.class);
                startActivity(intent);
            }
        });

        layout_myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), view_my_wallet_page.class);
                startActivity(intent);
            }
        });

        layout_myStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), seller_statistics_page.class);
                startActivity(intent);
            }
        });
    }

    private void setRef(View view) {


        iv_userPhoto = view.findViewById(R.id.iv_userPhoto);
        tv_fName = view.findViewById(R.id.tv_fName);
        tv_totalSales = view.findViewById(R.id.tv_totalSales);
        tv_totalInTransit = view.findViewById(R.id.tv_totalInTransit);

        layout_myProducts = view.findViewById(R.id.layout_myProducts);
        layout_myOrders = view.findViewById(R.id.layout_myOrders);
        layout_myWallet = view.findViewById(R.id.layout_myWallet);
        layout_myStatistics = view.findViewById(R.id.layout_myStatistics);

        rv_inTransitProducts = view.findViewById(R.id.rv_inTransitProducts);

    }



    private void autoReceiveOrderIfBuyerDidNotAcceptTheOrder() {


            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for(int i = 0; i < inTransitOrdersArrayList.size(); i++)
                            {
                                hasThreeHoursPassed(i);

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
    private void hasThreeHoursPassed(int i) {

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
        orderDatabase.child(orderSnapshotIds).updateChildren(hashMap);


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

                        String sellerId = inTransitOrders.getSellerId();

                        if(sellerId.equals(myUserId))
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