package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Fragments.Seller_Profile_Fragment;
import Models.Store;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class edit_store_page extends AppCompatActivity {

    private TextView tv_back, tv_addValidDOCS, tv_uploadStorePhotoBtn, tv_submitBtn, tv_storeAddress;
    private ImageView iv_storePhoto, iv_validDocs;
    private EditText et_storeName, et_storeContactNum, et_contactPerson;

    private Geocoder geocoder;
    private Uri storeImageUri;
    private ProgressDialog progressDialog;

    private FirebaseUser user;
    private DatabaseReference storeDatabase, userDatabase;
    private StorageReference storeStorage;
    private StorageTask addTask;

    private String myUserID, latLng, latString, longString, storeId;

    public static final int STORE_PIC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_store_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();
        storeStorage = FirebaseStorage.getInstance().getReference("Store").child(myUserID);
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef();
        generateStoreData();
        clicks();
    }

    private void generateStoreData() {

        Query query = storeDatabase.orderByChild("storeOwnersUserId")
                .equalTo(myUserID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        Store store = dataSnapshot.getValue(Store.class);

                        storeId = dataSnapshot.getKey();
                        latString = store.getStoreLat();
                        longString = store.getStoreLang();
                        String storeUrl = store.getStoreUrl();
                        String storeName = store.getStoreName();
                        String storeAddress = store.getStoreAddress();
                        String storeContactNum = store.getStoreContactNum();
                        String storeContactPerson = store.getStoreContactPerson();

                        Picasso.get()
                                .load(storeUrl)
                                .fit()
                                .centerCrop()
                                .into(iv_storePhoto);

                        et_storeName.setText(storeName);
                        tv_storeAddress.setText(storeAddress);
                        et_storeContactNum.setText(storeContactNum);
                        et_contactPerson.setText(storeContactPerson);
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

                onBackPressed();
            }
        });

        tv_uploadStorePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),STORE_PIC);

            }
        });

        tv_submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate();
            }
        });

        tv_storeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // placePicker();


                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.NAME);

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(edit_store_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);
            }
        });


    }

    private void validate() {

        String storeName = et_storeName.getText().toString();
        String storeAddress = tv_storeAddress.getText().toString();
        String storeContactNum = et_storeContactNum.getText().toString();
        String storeContactPerson = et_contactPerson.getText().toString();

         if(TextUtils.isEmpty(storeName))
        {
            et_storeName.setError("This field is required");
        }
        else if(TextUtils.isEmpty(storeAddress))
        {
            tv_storeAddress.setError("This field is required");
        }
        else if(TextUtils.isEmpty(storeContactNum))
        {
            et_storeContactNum.setError("This field is required");
        }
        else if (storeContactNum.length() != 11)
        {
            et_storeContactNum.setError("Contact number must be 11 digit");
        }
        else if(TextUtils.isEmpty(storeContactPerson))
        {
            et_contactPerson.setError("This field is required");
        }
        else
        {
            new AlertDialog.Builder(edit_store_page.this)
                    .setTitle("WARNING!")
                    .setMessage("Please make sure all information entered are correct")
                    .setCancelable(false)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            progressDialog = new ProgressDialog(edit_store_page.this);
                            progressDialog.setTitle("Processing...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            if(storeImageUri == null)
                            {
                                saveWithOutImage();

                            }else

                            {
                                saveWithImage();
                            }

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

    private void saveWithImage() {

        String storeImageName = storeImageUri.getLastPathSegment();
        StorageReference storePicRef = storeStorage.child(myUserID).child(storeImageName);

        storePicRef.putFile(storeImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String newImageUrl = uri.toString();
                        String newStoreName = et_storeName.getText().toString().trim();
                        String newStoreAddress = tv_storeAddress.getText().toString().trim();
                        String newStoreContactNum = et_storeContactNum.getText().toString().trim();
                        String newStoreContactPersone = et_contactPerson.getText().toString().trim();

                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("storeUrl", newImageUrl);
                        hashMap.put("storeImageName", storeImageName);
                        hashMap.put("storeName", newStoreName);
                        hashMap.put("storeAddress", newStoreAddress);
                        hashMap.put("storeLat", latString);
                        hashMap.put("storeLang", longString);
                        hashMap.put("storeContactNum", newStoreContactNum);
                        hashMap.put("storeContactPerson", newStoreContactPersone);

                        storeDatabase.child(storeId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    SweetAlertDialog sDialog;
                                    sDialog =  new SweetAlertDialog(edit_store_page.this, SweetAlertDialog.SUCCESS_TYPE);
                                    sDialog.setTitleText("SUCCESS!");
                                    sDialog.setCancelable(false);
                                    sDialog.setContentText("Store Updated!.");
                                    sDialog.setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                                            progressDialog.dismiss();
                                            Intent intent = new Intent(edit_store_page.this, seller_homepage.class);
                                            intent.putExtra("pageNumber", "5");
                                            startActivity(intent);
                                        }
                                    });
                                    sDialog.show();
                                }

                            }
                        });


                    }
                });

            }
        });

    }

    private void saveWithOutImage() {

        String newStoreName = et_storeName.getText().toString().trim();
        String newStoreAddress = tv_storeAddress.getText().toString().trim();
        String newStoreContactNum = et_storeContactNum.getText().toString().trim();
        String newStoreContactPersone = et_contactPerson.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("storeName", newStoreName);
        hashMap.put("storeAddress", newStoreAddress);
        hashMap.put("storeLat", latString);
        hashMap.put("storeLang", longString);
        hashMap.put("storeContactNum", newStoreContactNum);
        hashMap.put("storeContactPerson", newStoreContactPersone);

        storeDatabase.child(storeId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    SweetAlertDialog sDialog;
                    sDialog =  new SweetAlertDialog(edit_store_page.this, SweetAlertDialog.SUCCESS_TYPE);
                    sDialog.setTitleText("SUCCESS!");
                    sDialog.setCancelable(false);
                    sDialog.setContentText("Store Updated!.");
                    sDialog.setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            progressDialog.dismiss();
                            Intent intent = new Intent(edit_store_page.this, seller_homepage.class);
                            intent.putExtra("pageNumber", "5");
                            startActivity(intent);
                        }
                    });
                    sDialog.show();
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==STORE_PIC && resultCode == Activity.RESULT_OK)
        {
            storeImageUri = data.getData();

            Picasso.get().load(storeImageUri)
                    .fit()
                    .centerCrop()
                    .into(iv_storePhoto);
        }
        else if(requestCode == 100 && resultCode == RESULT_OK ){
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);

            List<Address> address = null;
            geocoder = new Geocoder(edit_store_page.this, Locale.getDefault());

            try {
                address = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                latString = String.valueOf(address.get(0).getLatitude());
                longString = String.valueOf(address.get(0).getLongitude());

                latLng = latString + "," + longString;
                String addressText =  place.getAddress().toString();

                tv_storeAddress.setText(addressText);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    private void setRef() {

        tv_back = findViewById(R.id.tv_back);
        tv_addValidDOCS = findViewById(R.id.tv_addValidDOCS);
        tv_uploadStorePhotoBtn = findViewById(R.id.tv_uploadStorePhotoBtn);
        tv_submitBtn = findViewById(R.id.tv_submitBtn);

        iv_storePhoto = findViewById(R.id.iv_storePhoto);
        iv_validDocs = findViewById(R.id.iv_validDocs);

        et_storeName = findViewById(R.id.et_storeName);
        tv_storeAddress = findViewById(R.id.tv_storeAddress);
        et_storeContactNum = findViewById(R.id.et_storeContactNum);
        et_contactPerson = findViewById(R.id.et_contactPerson);

        Places.initialize(edit_store_page.this, getString(R.string.API_KEY));
    }
}