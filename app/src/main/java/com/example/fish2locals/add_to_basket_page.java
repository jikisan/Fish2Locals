package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import Models.Basket;
import Models.Products;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class add_to_basket_page extends AppCompatActivity {

    private TextView tv_productName, tv_productQuantity, tv_productPrice, tv_hasPickup, tv_hasOwnDelivery,
            tv_has3rdPartyDelivery, tv_quantity, tv_addToBasketBtn, tv_back, tv_viewPhotos, tv_messageIfForDelivery;
    private ImageView iv_productPhoto, iv_decreaseBtn, iv_increaseBtn;
    private LinearLayout layout_hasPickup, layout_hasOwnDelivery, layout_has3rdPartyDelivery;
    private CheckBox cb_hasPickup, cb_hasOwnDelivery, cb_has3rdPartyDelivery;

    private FirebaseUser user;
    private DatabaseReference productDatabase, basketDatabase;

    private String myUserId, storeOwnersUserId, storeId, productId, deliveryMessage;
    private int productQuantity , intValue = 0;

    private String timeCreated, dateCreated, dateTimeCreated;
    private long dateTimeInMillis;

    String fishImageName;
    String productName;
    double productPrice;
    boolean hasPickup;
    boolean hasOwnDelivery;
    boolean has3rdPartyDelivery;

    boolean pickup;
    boolean ownDelivery;
    boolean thirdPartyDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_basket_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");
        basketDatabase = FirebaseDatabase.getInstance().getReference("Basket");

        productId = getIntent().getStringExtra("productId");
        storeOwnersUserId = getIntent().getStringExtra("storeOwnersUserId");
        storeId = getIntent().getStringExtra("storeId");

        setRef();
        clicks();
        generateProductData();
    }

    private void generateProductData() {

        productDatabase.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    Products products = snapshot.getValue(Products.class);

                    fishImageName = products.getImageName();
                    productName = products.getFishName();
                    productQuantity = products.getQuantityByKilo();
                    productPrice = products.getPricePerKilo();
                    hasPickup = products.isHasPickup();
                    hasOwnDelivery = products.isHasOwnDelivery();
                    has3rdPartyDelivery = products.isHas3rdPartyDelivery();

                    int freeDistance = products.getFreeKmForDelivery();
                    int chargePerKm = products.getChargePerKm();

                    if(!fishImageName.isEmpty())
                    {
                        int imageResource = add_to_basket_page.this.getResources().getIdentifier(fishImageName,
                                "drawable", add_to_basket_page.this.getPackageName());

                        Picasso.get()
                                .load(imageResource)
                                .fit()
                                .centerCrop()
                                .into(iv_productPhoto);
                    }


                    tv_productName.setText(productName);
                    tv_productQuantity.setText(productQuantity + " kilogram/s remaining.");
                    tv_productPrice.setText("₱ " + productPrice + " / Kg");
                    tv_messageIfForDelivery.setText("Delivery is free for first " + freeDistance + " km." +
                            "\n More than " + freeDistance+ " km. we charge Php" + chargePerKm + ".00 per km.");

                    if(hasPickup == true)
                    {
                        layout_hasPickup.setVisibility(View.VISIBLE);
                        tv_hasPickup.setText("• Pickup");
                    }

                    if(hasOwnDelivery == true)
                    {
                        layout_hasOwnDelivery.setVisibility(View.VISIBLE);
                        tv_hasOwnDelivery.setText("• Delivery");

                    }

                    if(has3rdPartyDelivery == true)
                    {
                        layout_has3rdPartyDelivery.setVisibility(View.GONE);
                        tv_has3rdPartyDelivery.setText("• 3rd Party Delivery (Maxim, Angkas, etc.)");
                    }

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

                Intent intent = new Intent(add_to_basket_page.this, view_store_page.class);
                intent.putExtra("fromWherePage", "fromAddToBasketPage");
                intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                intent.putExtra("storeId", storeId);
                startActivity(intent);
            }
        });

        tv_viewPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(add_to_basket_page.this, add_photos_page.class);
                intent.putExtra("productId", productId);
                intent.putExtra("category", "buyer");
                intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                intent.putExtra("storeId", storeId);
                startActivity(intent);
            }
        });

        iv_decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(intValue <= 1)
                {
                    Toast.makeText(add_to_basket_page.this, "Cannot be less than 1 kilo", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    intValue--;
                    tv_quantity.setText(intValue + "");
                }


            }
        });

        iv_increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(intValue >= productQuantity)
                {
                    Toast.makeText(add_to_basket_page.this, "Number exceeded the remaining quantity.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    intValue++;
                    tv_quantity.setText(intValue + "");
                }

            }
        });

        tv_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputEditText et_quantity = new TextInputEditText(view.getContext());
                et_quantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                et_quantity.setPadding(24, 8, 8, 8);
                et_quantity.setText("1");

                androidx.appcompat.app.AlertDialog.Builder quantityDialog = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
                quantityDialog.setTitle("Please enter quantity");
                quantityDialog.setView(et_quantity);

                quantityDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String quantity = et_quantity.getText().toString();

                        int quantityInInt = 1;
                        if(!quantity.isEmpty())
                        {
                            quantityInInt = Integer.parseInt(quantity);
                        }

                        if(quantityInInt <= 1 || quantity.isEmpty())
                        {
                            Toast.makeText(add_to_basket_page.this,
                                    "Cannot be less than 1 kilo", Toast.LENGTH_SHORT).show();

                        }
                        else if(quantityInInt > productQuantity)
                        {
                            Toast.makeText(add_to_basket_page.this, "Number exceeded the remaining quantity.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            tv_quantity.setText(quantity);
                        }

                    }
                });
                quantityDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                quantityDialog.create().show();

            }
        });

        tv_addToBasketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int quantityTemp = Integer.parseInt(tv_quantity.getText().toString());

                if(!cb_hasPickup.isChecked() && !cb_hasOwnDelivery.isChecked()
                        && !cb_has3rdPartyDelivery.isChecked())
                {
                    Toast.makeText(add_to_basket_page.this, "Please choose a delivery option", Toast.LENGTH_SHORT).show();
                }
                else if(quantityTemp < 1)
                {
                    Toast.makeText(add_to_basket_page.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                }
                else if(quantityTemp > productQuantity)
                {
                    Toast.makeText(add_to_basket_page.this, "Not enough quantity", Toast.LENGTH_SHORT).show();
                }
                else
                {
//                    SweetAlertDialog sDialog;
//
//                    sDialog = new SweetAlertDialog(add_to_basket_page.this, SweetAlertDialog.NORMAL_TYPE);
//                    sDialog.setTitleText("ADD PRODUCT");
//                    sDialog.setCancelText("Cancel");
//                    sDialog.setConfirmButton("Add", new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//
//                            sDialog.dismiss();
//                            addProductToBasket();
//
//
//                        }
//                    });
//                    sDialog.setContentText("Add this product\n to your basket?");
//                    sDialog.show();
                    addProductToBasket();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(add_to_basket_page.this);
//                    builder.setTitle("ADD PRODUCT");
//                    builder.setMessage("Add this product to your basket?");
//                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                    dialog.dismiss();
//
//                                }
//                            })
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();

                }



            }
        });

        cb_hasPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cb_hasOwnDelivery.setChecked(false);
                cb_has3rdPartyDelivery.setChecked(false);
                tv_messageIfForDelivery.setVisibility(View.GONE);

            }
        });

        cb_hasOwnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cb_hasPickup.setChecked(false);
                cb_has3rdPartyDelivery.setChecked(false);

//                if(cb_hasOwnDelivery.isChecked())
//                {
//                    tv_messageIfForDelivery.setVisibility(View.VISIBLE);
//                }
//                if(!cb_hasOwnDelivery.isChecked())
//                {
//                    tv_messageIfForDelivery.setVisibility(View.GONE);
//                }

            }
        });

        cb_has3rdPartyDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cb_hasPickup.setChecked(false);
                cb_hasOwnDelivery.setChecked(false);
                tv_messageIfForDelivery.setVisibility(View.GONE);

            }
        });

    }

    private void addProductToBasket() {

        setUpDate();

        int quantity = Integer.parseInt(tv_quantity.getText().toString());

        if(cb_hasPickup.isChecked())
        {
//            double pickupDiscount = .05;
//            productPrice = productPrice - (productPrice*pickupDiscount);
            pickup = true;
            ownDelivery = false;
            thirdPartyDelivery = false;

        }
        else if(cb_hasOwnDelivery.isChecked())
        {
            pickup = false;
            ownDelivery = true;
            thirdPartyDelivery = false;
        }
        else if(cb_has3rdPartyDelivery.isChecked())
        {
            pickup = false;
            ownDelivery = false;
            thirdPartyDelivery = true;
        }

        Basket basket = new Basket(fishImageName, productName, productPrice, pickup,
                ownDelivery, thirdPartyDelivery, quantity, storeId, storeOwnersUserId,
                myUserId, productId);

        String databaseName = myUserId + "-" + productName + "-" + dateTimeCreated;
        basketDatabase.child(databaseName).setValue(basket);

        basketDatabase.child(databaseName).setValue(basket).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful() )
                {

                    Intent intent = new Intent(add_to_basket_page.this, view_store_page.class);
                    intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                    intent.putExtra("storeId", storeId);
                    startActivity(intent);
                }


            }
        });


    }

    private void setRef() {

        tv_back = findViewById(R.id.tv_back);
        tv_productName = findViewById(R.id.tv_productName);
        tv_productQuantity = findViewById(R.id.tv_productQuantity);
        tv_productPrice = findViewById(R.id.tv_productPrice);
        tv_hasPickup = findViewById(R.id.tv_hasPickup);
        tv_hasOwnDelivery = findViewById(R.id.tv_hasOwnDelivery);
        tv_has3rdPartyDelivery = findViewById(R.id.tv_has3rdPartyDelivery);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_addToBasketBtn = findViewById(R.id.tv_addToBasketBtn);
        tv_viewPhotos = findViewById(R.id.tv_viewPhotos);
        tv_messageIfForDelivery = findViewById(R.id.tv_messageIfForDelivery);

        iv_productPhoto = findViewById(R.id.iv_productPhoto);
        iv_decreaseBtn = findViewById(R.id.iv_decreaseBtn);
        iv_increaseBtn = findViewById(R.id.iv_increaseBtn);

        layout_hasPickup = findViewById(R.id.layout_hasPickup);
        layout_hasOwnDelivery = findViewById(R.id.layout_hasOwnDelivery);
        layout_has3rdPartyDelivery = findViewById(R.id.layout_has3rdPartyDelivery);

        cb_hasPickup = findViewById(R.id.cb_hasPickup);
        cb_hasOwnDelivery = findViewById(R.id.cb_hasOwnDelivery);
        cb_has3rdPartyDelivery = findViewById(R.id.cb_has3rdPartyDelivery);

    }

    private void setUpDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String dateTime = DateFormat.getDateTimeInstance().format(currentTime);

        SimpleDateFormat formatDateTimeInMillis = new SimpleDateFormat("yyyyMMddhhmma");
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM-dd-yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a");

        dateTimeInMillis = Calendar.getInstance().getTimeInMillis();
        dateTimeCreated = formatDateTimeInMillis.format(Date.parse(dateTime));
        timeCreated = formatTime.format(Date.parse(dateTime));
        dateCreated = formatDate.format(Date.parse(dateTime));

    }
}