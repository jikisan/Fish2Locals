package Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fish2locals.R;
import com.example.fish2locals.edit_product_page;
import com.example.fish2locals.homepage;
import com.example.fish2locals.seller_application;
import com.example.fish2locals.seller_homepage;
import com.example.fish2locals.view_my_products_page;
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import Adapters.Adapter_Spinner_Fish;
import Models.Products;
import Models.Store;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Add_Product_Fragment extends Fragment {

    String[] fish={"Tilapia (Mayan Cichlids)","Tambakol (Yellowfin Tuna)",
            "Lapu lapu (Leopard Coral Grouper)","Tamban","Dilis (Anchovy)","Maya maya (Red Snapper)",
            "Tulingan (Mackerel Tuna)","Galunggong (Round Scad)","Dalagang Bukid (Yellow Tail Fusilier)",
            "Sapsap (Pony Fish or Slipmouth Fish)","Hasahasa (Short Mackerel)","Apahap (Barramundi)",
            "Pompano","Bisugo (Threadfin Bream)","Tanigue (Spanish Mackerel)", "Bangus (Milkfish)"};

    int images[] = {R.drawable.fish_tilapia_mayancichlids, R.drawable.fish_tambakol_yellowfintuna,
            R.drawable.fish_lapulapu_leopardcoralgrouper, R.drawable.fish_tamban,
            R.drawable.fish_dilis_anchovy, R.drawable.fish_mayamaya_redsnapper,
            R.drawable.fish_tulingan_mackereltuna, R.drawable.fish_galunggong_roundscad,
            R.drawable.fish_dalagangbukid_yellowtailfusilier,
            R.drawable.fish_sapsap_ponyfishorslipmouthfish, R.drawable.fish_hasahasa_shortmackerel,
            R.drawable.fish_apahap_barramundi, R.drawable.fish_pompano,
            R.drawable.fish_bisugo_threadfinbream, R.drawable.fish_tanigue_spanishmackerel,
            R.drawable.fish_bangus_milkfish };

    private LinearLayout layout4;
    private ImageView iv_fishPhoto, iv_decreaseBtn, iv_increaseBtn;
    private Spinner spinner;
    private CheckBox cb_pickUp, cb_ownDelivery, cb_3rdPartyDelivery;
    private TextView tv_quantity, tv_submitBtn;
    private EditText et_price, et_kmForFreeDelivery, et_priceForExtraKm;

    private FirebaseUser user;
    private DatabaseReference storeDatabase, productsDatabase;
    private StorageReference storeStorage;

    private ProgressDialog progressDialog;
    private String myUserID, storeId, imageName;
    private int itemNumber;
    private boolean hasPickup, hasOwnDelivery, has3rdPartyDelivery;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();
        storeStorage = FirebaseStorage.getInstance().getReference("Store").child(myUserID);
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        productsDatabase = FirebaseDatabase.getInstance().getReference("Products");

        setRef(view); // initialize UI Id's
        getStoreId(); //get store ID
        clicks(); // buttons


        Adapter_Spinner_Fish adapter_spinner_fish = new Adapter_Spinner_Fish(getContext(), images, fish);
        spinner.setAdapter(adapter_spinner_fish);

        return view;
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                itemNumber = i;

                int imageResource = images[i];
                imageName = getContext().getResources().getResourceEntryName(imageResource);

                Picasso.get()
                        .load(images[i])
                        .fit()
                        .centerCrop()
                        .into(iv_fishPhoto);



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        iv_decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String stringValue = tv_quantity.getText().toString();
                int intValue = Integer.parseInt(stringValue);

                if(intValue <= 1)
                {
                    Toast.makeText(getContext(), "Cannot be less than 1 kilo", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(),
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


//        cb_ownDelivery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(!cb_ownDelivery.isChecked())
//                {
//                    layout4.setVisibility(View.GONE);
//                }
//
//                if(cb_ownDelivery.isChecked())
//                {
//                    layout4.setVisibility(View.VISIBLE);
//                }
//
//            }
//        });

    }

    private void validate() {


        if(!cb_pickUp.isChecked() && !cb_ownDelivery.isChecked() && !cb_3rdPartyDelivery.isChecked())
        {
            Toast.makeText(getContext(), "Please choose delivery options", Toast.LENGTH_SHORT).show();
        }
        else if(et_price == null || TextUtils.isEmpty(et_price.getText()))
        {
            et_price.setError("Please enter product price");
        }
        else if(tv_quantity == null || TextUtils.isEmpty(tv_quantity.getText()))
        {
            tv_quantity.setError("Please enter product quantity");
        }
//        else if(et_kmForFreeDelivery == null || TextUtils.isEmpty(et_kmForFreeDelivery.getText()))
//        {
//            et_kmForFreeDelivery.setError("Please enter Km for free delivery");
//        }
//        else if(et_priceForExtraKm == null || TextUtils.isEmpty(et_priceForExtraKm.getText()))
//        {
//            et_kmForFreeDelivery.setError("Please enter per Km charge for delivery");
//        }
        else
        {
            new AlertDialog.Builder(getContext())
                    .setTitle("ADD PRODUCT")
                    .setMessage("Please make sure all information entered are correct")
                    .setCancelable(false)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
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

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Submitting form");
        progressDialog.setCancelable(false);
        progressDialog.show();

        int fishImageNumber = images[itemNumber];
        String fishName = fish[itemNumber];
        double pricePerKilo = Double.parseDouble(et_price.getText().toString());
        int quantityByKilo = Integer.parseInt(tv_quantity.getText().toString());
//        int kmForFreeDelivery = Integer.parseInt(et_kmForFreeDelivery.getText().toString());
//        int priceForExtraKm = Integer.parseInt(et_priceForExtraKm.getText().toString());


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
                has3rdPartyDelivery, pricePerKilo, quantityByKilo, storeId, myUserID,
                0, 0);

        productsDatabase.push().setValue(products).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressDialog.dismiss();

                SweetAlertDialog pdialog;
                pdialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                pdialog.setTitleText("PRODUCT ADDED!");
                pdialog.setContentText("Product is submitted successfully \n for admin review.");
                pdialog.setConfirmButton("View My Product/s", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        pdialog.dismiss();

                        cb_pickUp.setChecked(true);
                        cb_ownDelivery.setChecked(false);
                        cb_3rdPartyDelivery.setChecked(false);

                        hasPickup = false;
                        hasOwnDelivery = false;
                        has3rdPartyDelivery = false;

                        et_price.setText("");
                        tv_quantity.setText("1");

                        Intent intent = new Intent(getContext(), view_my_products_page.class);
                        startActivity(intent);

                    }
                });
                pdialog.show();
            }
        });

    }

    private void setRef(View view) {

        spinner = (Spinner) view.findViewById(R.id.spinner);

        iv_fishPhoto = view.findViewById(R.id.iv_fishPhoto);
        iv_decreaseBtn = view.findViewById(R.id.iv_decreaseBtn);
        iv_increaseBtn = view.findViewById(R.id.iv_increaseBtn);

        tv_submitBtn = view.findViewById(R.id.tv_submitBtn);
        tv_quantity = view.findViewById(R.id.tv_quantity);

        et_kmForFreeDelivery = view.findViewById(R.id.et_kmForFreeDelivery);
        et_priceForExtraKm = view.findViewById(R.id.et_priceForExtraKm);

        et_price = view.findViewById(R.id.et_price);

        cb_pickUp = view.findViewById(R.id.cb_pickUp);
        cb_ownDelivery = view.findViewById(R.id.cb_ownDelivery);
        cb_3rdPartyDelivery = view.findViewById(R.id.cb_3rdPartyDelivery);

        layout4 = view.findViewById(R.id.layout4);


    }
}