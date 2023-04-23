package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Adapters.Adapter_Spinner_Fish;
import Models.Products;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class edit_product_page extends AppCompatActivity {

    String[] fish={"Tilapia (Mayan Cichlids)","Tambakol (Yellowfin Tuna)",
            "Lapu lapu (Leopard Coral Grouper)","Tamban","Dilis (Anchovy)","Maya maya (Red Snapper)",
            "Tulingan (Mackerel Tuna)","Galunggong (Round Scad)","Dalagang Bukid (Yellow Tail Fusilier)",
            "Sapsap (Pony Fish or Slipmouth Fish)","Hasahasa (Short Mackerel)","Apahap (Barramundi)",
            "Pompano","Bisugo (Threadfin Bream)","Tanigue (Spanish Mackerel)", "Bangus (Milkfish)"};

    int images[] = {R.drawable.fish_tilapia_mayancichlids, R.drawable.fish_tulingan_mackereltuna,
            R.drawable.fish_lapulapu_leopardcoralgrouper, R.drawable.fish_tamban,
            R.drawable.fish_dilis_anchovy, R.drawable.fish_mayamaya_redsnapper,
            R.drawable.fish_tulingan_mackereltuna, R.drawable.fish_galunggong_roundscad,
            R.drawable.fish_dalagangbukid_yellowtailfusilier,
            R.drawable.fish_sapsap_ponyfishorslipmouthfish, R.drawable.fish_hasahasa_shortmackerel,
            R.drawable.fish_apahap_barramundi, R.drawable.fish_pompano,
            R.drawable.fish_bisugo_threadfinbream, R.drawable.fish_tanigue_spanishmackerel,
            R.drawable.fish_bangus_milkfish };

    private ImageView iv_fishPhoto, iv_decreaseBtn, iv_increaseBtn;
    private CheckBox cb_pickUp, cb_ownDelivery, cb_3rdPartyDelivery;
    private TextView tv_quantity, tv_submitBtn, tv_back, tv_productName, tv_viewPhotos;
    private EditText et_price;

    private FirebaseUser user;
    private DatabaseReference storeDatabase, productsDatabase;
    private StorageReference storeStorage;

    private int productQuantity , intValue = 1;
    String fishImageName;
    String productName;
    double productPrice;
    boolean pickup;
    boolean ownDelivery;
    boolean thirdPartyDelivery;

    private ProgressDialog progressDialog;
    private String myUserID, storeId, imageName, productId;
    private int itemNumber;
    private boolean hasPickup, hasOwnDelivery, has3rdPartyDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();
        storeStorage = FirebaseStorage.getInstance().getReference("Store").child(myUserID);
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        productsDatabase = FirebaseDatabase.getInstance().getReference("Products");

        productId = getIntent().getStringExtra("productId");

        setRef(); // initialize UI Id's
        generateProductData(); //generate product from database
        getStoreId(); // generate store id
        clicks(); // buttons



    }

    private void generateProductData() {

        productsDatabase.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Products products = snapshot.getValue(Products.class);

                    fishImageName = products.getImageName();
                    productName = products.getFishName();
                    productQuantity = products.getQuantityByKilo();
                    productPrice = products.getPricePerKilo();
                    pickup = products.isHasPickup();
                    ownDelivery = products.isHasOwnDelivery();
                    thirdPartyDelivery = products.isHas3rdPartyDelivery();

                    int imageResource = getResources().getIdentifier(fishImageName,
                            "drawable", getApplicationContext().getApplicationContext().getPackageName());

                    Picasso.get()
                            .load(imageResource)
                            .fit()
                            .centerCrop()
                            .into(iv_fishPhoto);

                    tv_productName.setText(productName);
                    tv_quantity.setText(productQuantity + "");
                    et_price.setText(productPrice + "");

                    if(pickup)
                    {
                        cb_pickUp.setChecked(true);
                        cb_ownDelivery.setChecked(false);
                        cb_3rdPartyDelivery.setChecked(false);
                    }

                    if(ownDelivery)
                    {
                        cb_pickUp.setChecked(false);
                        cb_ownDelivery.setChecked(true);
                        cb_3rdPartyDelivery.setChecked(false);
                    }

                    if(thirdPartyDelivery)
                    {
                        cb_pickUp.setChecked(false);
                        cb_ownDelivery.setChecked(false);
                        cb_3rdPartyDelivery.setChecked(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStoreId() {

        Query query = storeDatabase.orderByChild("storeOwnersUserId").equalTo(myUserID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        storeId = dataSnapshot.getKey();
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

                Intent intent = new Intent(edit_product_page.this,
                        view_my_products_page.class);
                startActivity(intent);
            }
        });

        tv_viewPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(edit_product_page.this, add_photos_page.class);
                intent.putExtra("productId", productId);
                intent.putExtra("category", "seller");
                startActivity(intent);
            }
        });

        iv_decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String stringValue = tv_quantity.getText().toString();
                int intValue = Integer.parseInt(stringValue);

                if(intValue <= 1)
                {
                    Toast.makeText(edit_product_page.this, "Cannot be less than 1 kilo", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    intValue--;

                    stringValue = String.valueOf(intValue);

                    tv_quantity.setText(stringValue.toString());
                }


            }
        });

        iv_increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stringValue = tv_quantity.getText().toString();
                int intValue = Integer.parseInt(stringValue);

                intValue++;


                stringValue = String.valueOf(intValue);

                tv_quantity.setText(stringValue.toString());
            }
        });

        tv_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputEditText et_quantity = new TextInputEditText(view.getContext());
                et_quantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                et_quantity.setPadding(24, 8, 8, 8);
                et_quantity.setText(String.valueOf(productQuantity));

                androidx.appcompat.app.AlertDialog.Builder quantityDialog = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
                quantityDialog.setTitle("Please enter quantity");
                quantityDialog.setView(et_quantity);

                quantityDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String quantity = et_quantity.getText().toString();
                        int quantityInInt = productQuantity;
                        if(!quantity.isEmpty())
                        {
                            quantityInInt = Integer.parseInt(quantity);
                        }

                        if(quantityInInt <= 1 || quantity.isEmpty())
                        {
                            Toast.makeText(edit_product_page.this,
                                    "Cannot be less than 1 kilo", Toast.LENGTH_SHORT).show();

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

        tv_submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate();
            }
        });


    }

    private void validate() {

        if(!cb_pickUp.isChecked() && !cb_ownDelivery.isChecked() && !cb_3rdPartyDelivery.isChecked())
        {
            Toast.makeText(edit_product_page.this, "Please choose delivery options", Toast.LENGTH_SHORT).show();
        }
        else if(et_price == null || TextUtils.isEmpty(et_price.getText()))
        {
            et_price.setError("Please enter product price");
        }
        else if(tv_quantity == null || TextUtils.isEmpty(tv_quantity.getText()))
        {
            tv_quantity.setError("Please enter product quantity");
        }
        else
        {
            new AlertDialog.Builder(edit_product_page.this)
                    .setTitle("UPDATE PRODUCT")
                    .setMessage("Please make sure all information entered are correct")
                    .setCancelable(false)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            addDataToDatabase();

                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                        }
                    })
                    .show();

        }

    }

    private void addDataToDatabase() {

        progressDialog = new ProgressDialog(edit_product_page.this);
        progressDialog.setTitle("Submitting form");
        progressDialog.setCancelable(false);
        progressDialog.show();

        int fishImageNumber = images[itemNumber];
        String fishName = fish[itemNumber];
        double pricePerKilo = Double.parseDouble(et_price.getText().toString());
        int quantityByKilo = Integer.parseInt(tv_quantity.getText().toString());


        if(cb_pickUp.isChecked())
        {
            hasPickup = true;
        }

        if(cb_ownDelivery.isChecked())
        {
            hasOwnDelivery = true;
        }

        if(cb_3rdPartyDelivery.isChecked())
        {
            has3rdPartyDelivery = true;
        }

        Products products = new Products(imageName, fishName, hasPickup, hasOwnDelivery,
                has3rdPartyDelivery, pricePerKilo, quantityByKilo, storeId, myUserID);


        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("hasPickup", hasPickup);
        hashMap.put("hasOwnDelivery", hasOwnDelivery);
        hashMap.put("has3rdPartyDelivery", has3rdPartyDelivery);
        hashMap.put("pricePerKilo", pricePerKilo);
        hashMap.put("quantityByKilo", quantityByKilo);

        productsDatabase.child(productId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    SweetAlertDialog sDialog;
                    sDialog =  new SweetAlertDialog(edit_product_page.this, SweetAlertDialog.SUCCESS_TYPE);
                    sDialog.setTitleText("SUCCESS!");
                    sDialog.setCancelable(false);
                    sDialog.setContentText("Product Updated!.");
                    sDialog.setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            progressDialog.dismiss();
                            Intent intent = new Intent(edit_product_page.this, view_my_products_page.class);
                            startActivity(intent);
                        }
                    });
                    sDialog.show();
                }

            }
        });
    }

    private void setRef() {

        tv_productName = findViewById(R.id.tv_productName);

        iv_fishPhoto = findViewById(R.id.iv_fishPhoto);
        iv_decreaseBtn = findViewById(R.id.iv_decreaseBtn);
        iv_increaseBtn = findViewById(R.id.iv_increaseBtn);

        tv_back = findViewById(R.id.tv_back);
        tv_submitBtn = findViewById(R.id.tv_submitBtn);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_viewPhotos = findViewById(R.id.tv_viewPhotos);

        et_price = findViewById(R.id.et_price);

        cb_pickUp = findViewById(R.id.cb_pickUp);
        cb_ownDelivery = findViewById(R.id.cb_ownDelivery);
        cb_3rdPartyDelivery = findViewById(R.id.cb_3rdPartyDelivery);


    }
}