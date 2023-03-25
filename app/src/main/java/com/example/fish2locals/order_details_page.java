package com.example.fish2locals;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import Adapters.AdapterPlaceOrderItem;
import Models.Basket;
import Models.Orders;
import Models.Store;
import Models.Transactions;
import Models.Wallets;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class order_details_page extends AppCompatActivity {

    private List<Orders> arrOrders = new ArrayList<>();
    private List<Basket> arrBasket = new ArrayList<>();
    private List<String> arrOrderSnapshotIds = new ArrayList<>();
    private AdapterPlaceOrderItem adapterPlaceOrderItem;

    private LinearLayout layout;
    private ProgressBar progressBar;
    private RecyclerView rv_myBasket;
    private TextView tv_back, tv_orderId, tv_dateCreated, tv_timeCreated, tv_seller,
            tv_deliveryAddress, tv_contactNum, tv_totalPrice, tv_orderReceiveBtn, tv_cancelOrderBtn,
            tv_rateBtn, tv_cancelText;


    private FirebaseUser user;
    private DatabaseReference ordersDatabase, storeDatabase, walletDatabase, transactionDatabase;

    private String myUserId, orderId, storeId, sellerUserId, buyerUserId, productId;
    private String walletId, buyerWalletId, timeCreated, dateCreated;
    private double fundAmount, buyerfundAmount;
    private long dateTimeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
        transactionDatabase = FirebaseDatabase.getInstance().getReference("Transactions");


        orderId = getIntent().getStringExtra("orderId");
        storeId = getIntent().getStringExtra("storeId");
        productId = getIntent().getStringExtra("productId");

        setRef();

        generateRecyclerLayout();
        click();

    }

    private void generateRecyclerLayout() {

        rv_myBasket.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(order_details_page.this);
        rv_myBasket.setLayoutManager(linearLayoutManager);

        adapterPlaceOrderItem = new AdapterPlaceOrderItem(arrBasket, order_details_page.this);
        rv_myBasket.setAdapter(adapterPlaceOrderItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = ordersDatabase
                .orderByChild("orderId")
                .equalTo(orderId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrBasket.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Orders orders = dataSnapshot.getValue(Orders.class);

                        String orderSnapshotIds = dataSnapshot.getKey();
                        String imageName = orders.getImageName();
                        String fishName = orders.getFishName();
                        double pricePerKilo = orders.getPricePerKilo();
                        boolean pickup  = orders.isPickup();
                        boolean ownDelivery = orders.isOwnDelivery();
                        boolean thirrdPartyDelivery = orders.isThirdPartyDelivery();
                        int quantityByKilo = orders.getQuantity();
                        String storeId = orders.getStoreId();
                        sellerUserId = orders.getSellerUserId();
                        buyerUserId = orders.getBuyerUserId();

                        Basket basket = new Basket(imageName, fishName, pricePerKilo, pickup,
                                ownDelivery, thirrdPartyDelivery, quantityByKilo, storeId,
                                sellerUserId, buyerUserId, productId);

                        arrOrderSnapshotIds.add(orderSnapshotIds);
                        arrBasket.add(basket);
                        arrOrders.add(orders);

                    }

                    String storeId = arrOrders.get(0).getStoreId();
                    generateStoreName(storeId);

                    tv_orderId.setText(arrOrders.get(0).getOrderId());
                    tv_dateCreated.setText(arrOrders.get(0).getDateCreated());
                    tv_timeCreated.setText(arrOrders.get(0).getTimeCreated());

                    tv_deliveryAddress.setText(arrOrders.get(0).getDeliveryAddress());
                    tv_contactNum.setText(arrOrders.get(0).getBuyerContactNum());
                    tv_totalPrice.setText(String.valueOf("₱ " +arrOrders.get(0).getTotalPrice()));

                    String orderStatus = arrOrders.get(0).getOrderStatus();
                    boolean rated = arrOrders.get(0).isRated();


                    if(orderStatus.equals("1"))
                    {
                        layout.setVisibility(View.VISIBLE);
                    }
                    else if(orderStatus.equals("2") && !rated)
                    {
                        tv_rateBtn.setVisibility(View.VISIBLE);
                    }
                    else if(orderStatus.equals("3"))
                    {
                        tv_cancelText.setVisibility(View.VISIBLE);
                    }

                    generateSellerWallet(arrBasket.get(0).getSellerUserId());
                    generateBuyerWallet(arrBasket.get(0).getBuyerUserId());
                }


                progressBar.setVisibility(GONE);
                adapterPlaceOrderItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void generateSellerWallet(String sellerUserId) {

        Query query = walletDatabase.orderByChild("userID").equalTo(sellerUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Wallets wallets = dataSnapshot.getValue(Wallets.class);

                        walletId = dataSnapshot.getKey().toString();
                        fundAmount = wallets.getFundAmount();
                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateBuyerWallet(String buyerUserId) {

        Query query = walletDatabase.orderByChild("userID").equalTo(buyerUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Wallets wallets = dataSnapshot.getValue(Wallets.class);

                        buyerWalletId = dataSnapshot.getKey().toString();
                        buyerfundAmount = wallets.getFundAmount();
                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateStoreName(String storeId) {

        storeDatabase.child(storeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Store store = snapshot.getValue(Store.class);
                    tv_seller.setText(store.getStoreName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void click() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(order_details_page.this, rate_store_seller_page.class);
                intent.putExtra("storeId", storeId);
                intent.putExtra("orderId", orderId);
                intent.putExtra("sellerUserId", sellerUserId);
                startActivity(intent);


            }
        });

        tv_orderReceiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SweetAlertDialog sDialog;
                sDialog = new SweetAlertDialog(order_details_page.this, SweetAlertDialog.WARNING_TYPE);
                sDialog.setTitleText("Receive and close order?");
                sDialog.setCancelText("No");
                sDialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {


                        ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(order_details_page.this);
                        progressDialog.setTitle("Processing...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        updateWallet(sDialog, progressDialog);

                    }
                });
                sDialog.show();



            }
        });

        tv_cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SweetAlertDialog sDialog;
                sDialog = new SweetAlertDialog(order_details_page.this, SweetAlertDialog.WARNING_TYPE);
                sDialog.setTitleText("Cancel Order?");
                sDialog.setCancelText("No");
                sDialog.setConfirmButton("Yes, cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sDialog.dismiss();
                        ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(order_details_page.this);
                        progressDialog.setTitle("Processing...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        updateWalletCancelOrder(sDialog, progressDialog);

                    }
                });
                sDialog.show();


            }
        });
    }




    // Order Receive
    private void updateWallet(SweetAlertDialog sDialog, ProgressDialog progressDialog) {

        double totalPrice = arrOrders.get(0).getTotalPrice();
        double percentageFee = 1;
        double grossIncome = totalPrice*percentageFee;


        double newFundValue = fundAmount + grossIncome;

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userID", sellerUserId);
        hashMap.put("fundAmount", newFundValue);

        walletDatabase.child(walletId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {

                    updateTransactionData(grossIncome, sDialog, progressDialog);


                }

            }
        });

    }

    private void updateTransactionData(double grossIncome, SweetAlertDialog sDialog,  ProgressDialog progressDialog) {

        setUpDate();

        String transactionType = "add";
        String transactionNote = "Payment Received";

        Transactions transactions = new Transactions(dateTimeInMillis, dateCreated, timeCreated,
                transactionType, transactionNote, grossIncome, sellerUserId);

        transactionDatabase.push().setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                changeOrderStatusToComplete(sDialog, progressDialog);
            }
        });

    }

    private void changeOrderStatusToComplete(SweetAlertDialog sDialog,  ProgressDialog progressDialog) {

        for(int i=0; i < arrOrderSnapshotIds.size(); i++)
        {
            String orderSnapshotIds = arrOrderSnapshotIds.get(i).toString();

            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("orderStatus", "2");
            ordersDatabase.child(orderSnapshotIds).updateChildren(hashMap);
        }

        SweetAlertDialog s2Dialog;
        s2Dialog = new SweetAlertDialog(order_details_page.this, SweetAlertDialog.SUCCESS_TYPE);
        s2Dialog.setTitleText("Order Received!");
        s2Dialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                s2Dialog.dismiss();
                sDialog.dismiss();
                progressDialog.dismiss();
                Intent intent = new Intent(order_details_page.this, view_my_order_page.class);
                startActivity(intent);

            }
        });
        s2Dialog.show();

    }


    // Cancel Order

    private void updateWalletCancelOrder(SweetAlertDialog sDialog, ProgressDialog progressDialog) {

        double totalPrice = arrOrders.get(0).getTotalPrice();

        double newFundValue = buyerfundAmount + totalPrice;

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userID", buyerUserId);
        hashMap.put("fundAmount", newFundValue);

        walletDatabase.child(buyerWalletId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {

                    updateTransactionDataCancelOrder(totalPrice, sDialog, progressDialog);


                }

            }
        });

    }

    private void updateTransactionDataCancelOrder(double totalPrice, SweetAlertDialog sDialog, ProgressDialog progressDialog) {

        setUpDate();

        String transactionType = "add";
        String transactionNote = "Refund for Cancelled Order";

        Transactions transactions = new Transactions(dateTimeInMillis, dateCreated, timeCreated,
                transactionType, transactionNote, totalPrice, buyerUserId);

        transactionDatabase.push().setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                changeOrderStatusToCancelled(sDialog, progressDialog);
            }
        });

    }

    private void changeOrderStatusToCancelled(SweetAlertDialog sDialog,  ProgressDialog progressDialog) {

        for(int i=0; i < arrOrderSnapshotIds.size(); i++)
        {
            String orderSnapshotIds = arrOrderSnapshotIds.get(i).toString();

            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("orderStatus", "3");
            ordersDatabase.child(orderSnapshotIds).updateChildren(hashMap);
        }

        progressDialog.dismiss();

        SweetAlertDialog s2Dialog;
        s2Dialog = new SweetAlertDialog(order_details_page.this, SweetAlertDialog.SUCCESS_TYPE);
        s2Dialog.setTitleText("Order Cancelled!");
        s2Dialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                s2Dialog.dismiss();
                Intent intent = new Intent(order_details_page.this, view_my_order_page.class);
                startActivity(intent);

            }
        });
        s2Dialog.show();

    }



    private void setRef() {

        layout = findViewById(R.id.layout);

        tv_back = findViewById(R.id.tv_back);

        tv_orderId = findViewById(R.id.tv_orderId);
        tv_dateCreated = findViewById(R.id.tv_dateCreated);
        tv_timeCreated = findViewById(R.id.tv_timeCreated);
        tv_seller = findViewById(R.id.tv_seller);
        tv_deliveryAddress = findViewById(R.id.tv_deliveryAddress);
        tv_contactNum = findViewById(R.id.tv_contactNum);
        tv_totalPrice = findViewById(R.id.tv_totalPrice);

        tv_orderReceiveBtn = findViewById(R.id.tv_orderReceiveBtn);
        tv_cancelOrderBtn = findViewById(R.id.tv_cancelOrderBtn);
        tv_rateBtn = findViewById(R.id.tv_rateBtn);
        tv_cancelText = findViewById(R.id.tv_cancelText);

        progressBar = findViewById(R.id.progressBar);
        rv_myBasket = findViewById(R.id.rv_myBasket);



    }

    private void setUpDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String dateTime = DateFormat.getDateTimeInstance().format(currentTime);

        SimpleDateFormat formatDateTimeInMillis = new SimpleDateFormat("yyyyMMddhhmma");
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM-dd-yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a");

        dateTimeInMillis = Calendar.getInstance().getTimeInMillis();
        timeCreated = formatTime.format(Date.parse(dateTime));
        dateCreated = formatDate.format(Date.parse(dateTime));

    }
}