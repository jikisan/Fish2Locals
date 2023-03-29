package com.example.fish2locals;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.AdapterPlaceOrderItem;
import Models.Basket;
import Models.Notifications;
import Models.Orders;
import Models.Products;
import Models.Transactions;
import Models.Users;
import Models.Wallets;

public class place_order_page2 extends AppCompatActivity {

    private final List<Basket> arrBasket = new ArrayList<>();
    private final List<Orders> arrOrders = new ArrayList<>();
    private final List<Products> arrProducts = new ArrayList<>();

    private ProgressBar progressBar;
    private RecyclerView rv_myBasket;
    private TextView tv_back, tv_placeOrderBtn, tv_enterDeviveryAddress, tv_myWallet;
    private EditText et_contactNum;
    private ProgressDialog progressDialog;

    private Geocoder geocoder;
    private Task task;
    private int progress = 0;

    private AdapterPlaceOrderItem adapterPlaceOrderItem;

    private FirebaseUser user;
    private DatabaseReference basketDatabase, userDatabase, orderDatabase, walletDatabase, transactionDatabase,
            productDatabase;

    private String myUserId;
    private String storeOwnersUserId;
    private String storeId;
    private String latString;
    private String longString;
    private String myFullName;
    private String timeCreated;
    private String dateCreated;
    private String orderId;
    private String walletId;
    private String myFundAmountString;
    private double myFundAmount;
    private long dateTimeInMillis;
    double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_order_page2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");
        transactionDatabase = FirebaseDatabase.getInstance().getReference("Transactions");


        storeOwnersUserId = getIntent().getStringExtra("storeOwnersUserId");
        storeId = getIntent().getStringExtra("storeId");

        setRef();

        generateMyData();
        generateMyWalletData();
        generateRecyclerLayout();
        clicks();
    }

    private void generateMyData() {

        userDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    Users users = snapshot.getValue(Users.class);

                    String contactNum = users.getContactNum();
                    String fname = users.getFname();
                    String lname = users.getLname();

                    myFullName = fname + " " + lname;

                    et_contactNum.setText(contactNum);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateMyWalletData() {

        walletDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Wallets wallets = dataSnapshot.getValue(Wallets.class);
                        String walletUserId = wallets.getUserID();

                        if(walletUserId.equals(myUserId))
                        {
                            walletId = dataSnapshot.getKey().toString();
                            myFundAmount = wallets.getFundAmount();
                            myFundAmountString = NumberFormat.getNumberInstance(Locale.US).format(myFundAmount);
                            tv_myWallet.setText("My Balance: ₱ " + myFundAmountString);
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateRecyclerLayout() {

        rv_myBasket.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(place_order_page2.this);
        rv_myBasket.setLayoutManager(linearLayoutManager);

        adapterPlaceOrderItem = new AdapterPlaceOrderItem(arrBasket, getApplicationContext());
        rv_myBasket.setAdapter(adapterPlaceOrderItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = basketDatabase
                .orderByChild("storeId")
                .equalTo(storeId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrBasket.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Basket basket = dataSnapshot.getValue(Basket.class);

                        String productId = basket.getProductId();
                        String buyerUserId = basket.getBuyerUserId();
                        double pricePerKilo = basket.getPricePerKilo();
                        int basketQuantity = basket.getQuantityByKilo();

                        if(buyerUserId.equals(myUserId))
                        {
                            arrBasket.add(basket);
                            totalPrice = totalPrice + (pricePerKilo*basketQuantity);

                            generateProductData(productId);
                        }
                    }


                    tv_placeOrderBtn.setText("PLACE ORDER (Total Amount: ₱ " +totalPrice+ ")");

                }


                progressBar.setVisibility(GONE);
                adapterPlaceOrderItem.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateProductData(String productId) {

        productDatabase.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Products products = snapshot.getValue(Products.class);

                    arrProducts.add(products);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void clicks() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        tv_enterDeviveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // placePicker();


                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.NAME);

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(place_order_page2.this);

                //Start Activity result
                startActivityForResult(intent, 100);

            }
        });

        tv_placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String storeAddress = tv_enterDeviveryAddress.getText().toString();
                String storeContactNum = et_contactNum.getText().toString();

                if(TextUtils.isEmpty(storeContactNum))
                {
                    et_contactNum.setError("This field is required");
                }
                else if(storeContactNum.length() != 11)
                {
                    et_contactNum.setError("Contact number must be 11 digit");
                }
                else if(TextUtils.isEmpty(storeAddress))
                {
                    tv_enterDeviveryAddress.setError("This field is required");
                    Toast.makeText(place_order_page2.this, "Please enter delivery address", Toast.LENGTH_SHORT).show();
                }
                else if(myFundAmount < totalPrice)
                {
                    Toast.makeText(place_order_page2.this, "Insufficient fund.", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    addOrdersToDatabase();
                }




            }
        });

    }


    private void addOrdersToDatabase() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing order...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        setUpDate();

        for(int i = 0; i < arrBasket.size(); i++)
        {
            int orderCount = i+1;
            String childName = dateTimeInMillis + "_Order_" + orderCount;
            orderId = String.valueOf(dateTimeInMillis);

            String productId = arrBasket.get(i).getProductId();
            String imageName = arrBasket.get(i).getImageName();
            String fishName = arrBasket.get(i).getFishName();
            double price = arrBasket.get(i).getPricePerKilo();
            boolean pickup = arrBasket.get(i).isPickup();
            boolean ownDelivery = arrBasket.get(i).isOwnDelivery();
            boolean thirdPartyDelivery = arrBasket.get(i).isThirdPartyDelivery();
            int quantity = arrBasket.get(i).getQuantityByKilo();

            String deliveryAddress = tv_enterDeviveryAddress.getText().toString();
            String buyerContactNum = et_contactNum.getText().toString();

            String orderStatus = "1";
            boolean rated = false;

            Orders orders = new Orders(productId, orderId, storeId, storeOwnersUserId, myUserId,
                    timeCreated, dateCreated, imageName, fishName, price, pickup, ownDelivery,
                    thirdPartyDelivery, quantity, deliveryAddress, latString, longString,
                    buyerContactNum, totalPrice, orderStatus, rated);
            orderDatabase.child(childName).setValue(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {
                        updateProduct();
                    }

                }
            });



        }



    }

    private void updateProduct() {

        for(int i = 0; i < arrBasket.size(); i++)
        {
            String productId = arrBasket.get(i).getProductId();
            int basketQuantityCount = arrBasket.get(i).getQuantityByKilo();
            int productQuantity = arrProducts.get(i).getQuantityByKilo();

            int newQuantity = productQuantity - basketQuantityCount;

            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("quantityByKilo", newQuantity);


            productDatabase.child(productId).updateChildren(hashMap);

        }

        updateBuyersWallet();

    }



    private void updateBuyersWallet() {

        double newFundValue = myFundAmount - totalPrice;


        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userID", myUserId);
        hashMap.put("fundAmount", newFundValue);

        walletDatabase.child(walletId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {

                    updateTransactionData(totalPrice);


                }

            }
        });

    }

    private void updateTransactionData(double totalPrice) {

        generateNotification();

        String transactionType = "deduct";
        String transactionNote = "Orders Payment";

        Transactions transactions = new Transactions(dateTimeInMillis, dateCreated, timeCreated,
                transactionType, transactionNote, totalPrice, myUserId);

        transactionDatabase.push().setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Intent intent = new Intent(place_order_page2.this, order_summary_page.class);
                    intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("storeId", storeId);
                    startActivity(intent);

                }

            }
        });

    }

    private void deleteBasket() {

        Query query = basketDatabase.orderByChild("buyerUserId").equalTo(myUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Intent intent = new Intent(place_order_page2.this, order_summary_page.class);
                        intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("storeId", storeId);
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateNotification() {

        DatabaseReference notificationDatabase = FirebaseDatabase.getInstance().getReference("Notifications");

        String notificationType = "order placed";
        String notificationMessage = myFullName + " has placed an order";

        Notifications notifications = new Notifications(dateTimeInMillis, dateCreated, timeCreated, notificationType,
                notificationMessage, storeOwnersUserId);

        notificationDatabase.push().setValue(notifications);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK ){
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);

            List<Address> address = null;
            geocoder = new Geocoder(place_order_page2.this, Locale.getDefault());

            try {
                address = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                latString = String.valueOf(address.get(0).getLatitude());
                longString = String.valueOf(address.get(0).getLongitude());

                String latLng = latString + "," + longString;
                String addressText =  place.getAddress().toString();

                tv_enterDeviveryAddress.setText(addressText);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void setRef() {

        progressBar = findViewById(R.id.progressBar);
        rv_myBasket = findViewById(R.id.rv_myBasket);

        tv_back = findViewById(R.id.tv_back);
        tv_enterDeviveryAddress = findViewById(R.id.tv_enterDeviveryAddress);
        tv_placeOrderBtn = findViewById(R.id.tv_placeOrderBtn);
        tv_myWallet = findViewById(R.id.tv_myWallet);

        et_contactNum = findViewById(R.id.et_contactNum);

        Places.initialize(place_order_page2.this, getString(R.string.API_KEY));


    }
}