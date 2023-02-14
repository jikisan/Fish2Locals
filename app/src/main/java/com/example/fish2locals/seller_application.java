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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import Models.Store;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class seller_application extends AppCompatActivity {

    private TextView tv_back, tv_addValidDOCS, tv_uploadStorePhotoBtn, tv_submitBtn, tv_storeAddress;
    private ImageView iv_storePhoto, iv_validDocs;
    private EditText et_storeName, et_storeContactNum, et_contactPerson;

    private Geocoder geocoder;
    private Uri storeImageUri, validDocsUri;
    private ProgressDialog progressDialog;

    private FirebaseUser user;
    private DatabaseReference storeDatabase;
    private StorageReference storeStorage;
    private StorageTask addTask;

    private String myUserID, latLng, latString, longString;

    public static final int STORE_PIC = 1;
    public static final int VALID_DOCS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_application);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserID = user.getUid();
        storeStorage = FirebaseStorage.getInstance().getReference("Store").child(myUserID);
        storeDatabase = FirebaseDatabase.getInstance().getReference("Store");

        setRef();
        clicks();

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

        tv_addValidDOCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),VALID_DOCS);

            }
        });

        iv_validDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),VALID_DOCS);

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
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(seller_application.this);

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

        if(storeImageUri == null)
        {
            Toast.makeText(this, "Store Photo is required", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(storeName))
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
        else if(validDocsUri == null)
        {
            Toast.makeText(this, "Valid Document is required", Toast.LENGTH_SHORT).show();
        }
        else
        {
            new AlertDialog.Builder(seller_application.this)
                    .setTitle("SUBMIT FORM")
                    .setMessage("Please make sure all information entered are correct")
                    .setCancelable(true)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String storeImageName = storeImageUri.getLastPathSegment();
                            String validDocsName = validDocsUri.getLastPathSegment();


                            submit(storeImageName, storeName, storeAddress, storeContactNum,
                                    storeContactPerson, validDocsName);

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

    private void submit(String storeImageName, String storeName, String storeAddress,
                        String storeContactNum, String storeContactPerson, String validDocsName) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Submitting form");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference fileReference = storeStorage.child(storeImageUri.getLastPathSegment());

        fileReference.putFile(storeImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String storeImageUrl = uri.toString();

                        generateValidDocsUrl(storeImageName, storeName, storeAddress, storeContactNum,
                                storeContactPerson, validDocsName, storeImageUrl);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(seller_application.this, "Failed!", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void generateValidDocsUrl(String storeImageName, String storeName, String storeAddress,
                                      String storeContactNum, String storeContactPerson,
                                      String validDocsName, String storeImageUrl) {

        StorageReference fileReference = storeStorage.child(validDocsUri.getLastPathSegment());

        fileReference.putFile(validDocsUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String validDocsUrl = uri.toString();

                        addDataToDatabase(storeImageName, storeName, storeAddress, storeContactNum,
                                storeContactPerson, validDocsName, storeImageUrl, validDocsUrl);


                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(seller_application.this, "Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addDataToDatabase(String storeImageName, String storeName, String storeAddress,
                                   String storeContactNum, String storeContactPerson,
                                   String validDocsName, String storeImageUrl, String validDocsUrl) {

        long ratings = 0;

        Store store = new Store(storeImageUrl, storeImageName, storeName, storeAddress,
                latString, longString, storeContactNum, storeContactPerson, validDocsUrl,
                validDocsName, myUserID, ratings);

        storeDatabase.push().setValue(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressDialog.dismiss();

                SweetAlertDialog pdialog;
                pdialog = new SweetAlertDialog(seller_application.this, SweetAlertDialog.SUCCESS_TYPE);
                pdialog.setTitleText("SUBMITTED!");
                pdialog.setContentText("Form submitted successfully \n for admin review.");
                pdialog.setConfirmButton("Complete", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        pdialog.dismiss();
                        Intent intent = new Intent(seller_application.this, homepage.class);
                        startActivity(intent);

                    }
                });
                pdialog.show();

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
        else if (requestCode==VALID_DOCS && resultCode == Activity.RESULT_OK)
        {
            validDocsUri = data.getData();

            Picasso.get().load(validDocsUri)
                    .resize(300, 200)
                    .into(iv_validDocs);
        }
        else if(requestCode == 100 && resultCode == RESULT_OK){
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);

            List<Address> address = null;
            geocoder = new Geocoder(seller_application.this, Locale.getDefault());

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

        Places.initialize(seller_application.this, getString(R.string.API_KEY));
    }
}