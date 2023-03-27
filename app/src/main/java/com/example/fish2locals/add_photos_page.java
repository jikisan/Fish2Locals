package com.example.fish2locals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterPhotoItem;
import Models.Photos;

public class add_photos_page extends AppCompatActivity {

    private static final int PICK_IMG = 1;
    private ArrayList<Uri> arrImageList = new ArrayList<Uri>();
    private List<Photos> arrPhotos = new ArrayList<Photos>();
    private AdapterPhotoItem adapterPhotoItem;

    private TextView tv_back, tv_placeHolder, tv_addPhotos, tv_uploadPhotos;
    private RecyclerView rv_photos;
    private String productId, category, storeOwnersUserId, storeId;
    private ProgressDialog progressDialog;

    private DatabaseReference photoDatabase;
    private StorageReference productsPhotoStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photos_page);

        productsPhotoStorage = FirebaseStorage.getInstance().getReference("Products");
        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");

        productId = getIntent().getStringExtra("productId");
        category = getIntent().getStringExtra("category");
        storeOwnersUserId = getIntent().getStringExtra("storeOwnersUserId");
        storeId = getIntent().getStringExtra("storeId");

        setRef();
        generateRecyclerLayout();
        clicks();
    }

    private void generateRecyclerLayout() {

        if(category.equals("buyer"))
        {
            tv_uploadPhotos.setVisibility(View.GONE);
            tv_addPhotos.setVisibility(View.GONE);
        }

        rv_photos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(add_photos_page.this, 3, GridLayoutManager.VERTICAL, false);
        rv_photos.setLayoutManager(gridLayoutManager);

        adapterPhotoItem = new AdapterPhotoItem(arrPhotos);
        rv_photos.setAdapter(adapterPhotoItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = photoDatabase
                .orderByChild("productId")
                .equalTo(productId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    arrPhotos.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Photos photos = dataSnapshot.getValue(Photos.class);
                        arrPhotos.add(photos);
                    }
                }

//                if(arrPhotos.isEmpty())
//                {
//                    tv_placeHolder.setVisibility(View.VISIBLE);
//                    rv_photos.setVisibility(View.GONE);
//                }

                adapterPhotoItem.notifyDataSetChanged();

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

                if(category.equals("buyer"))
                {
                    Intent intent = new Intent(add_photos_page.this,
                            add_to_basket_page.class);
                    intent.putExtra("storeOwnersUserId", storeOwnersUserId);
                    intent.putExtra("storeId", storeId);
                    intent.putExtra("productId", productId);
                    startActivity(intent);
                }
                else if(category.equals("seller"))
                {
                    Intent intent = new Intent(add_photos_page.this,
                            edit_product_page.class);
                    intent.putExtra("productId", productId);
                    startActivity(intent);
                }


            }
        });

        tv_addPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMG);

            }
        });

        tv_uploadPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( arrImageList.size() <= 0)
                {
                    Toast.makeText(add_photos_page.this, "Please choose a photo", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    uploadPhotos();
                }

            }
        });

        adapterPhotoItem.setOnItemClickListener(new AdapterPhotoItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {


                Intent intent = new Intent(add_photos_page.this, view_photo_fullscreen_page.class);
                intent.putExtra("productId", productId);
                intent.putExtra("current position", position);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void uploadPhotos() {

        progressDialog = new ProgressDialog(add_photos_page.this);
        progressDialog.setTitle("Uploading photos");
        progressDialog.show();

        for (int uploads=0; uploads < arrImageList.size(); uploads++)
        {
            Uri Image  = arrImageList.get(uploads);

            long imageTime = System.currentTimeMillis();
            String imageName = imageTime + Image.getLastPathSegment().toString();

            StorageReference imagename = productsPhotoStorage.child(productId+"/"+ imageName);

            imagename.putFile(arrImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);

                            Photos photos = new Photos(productId, url, imageName);

                            photoDatabase.push().setValue(photos);
                        }
                    });

                }
            });

        }



        Toast.makeText(add_photos_page.this, "Upload complete", Toast.LENGTH_SHORT);
        tv_addPhotos.setVisibility(View.VISIBLE);
        tv_uploadPhotos.setVisibility(View.GONE);
        adapterPhotoItem.notifyDataSetChanged();
        progressDialog.dismiss();

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;
                    arrImageList.clear();

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        arrImageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }

                    tv_addPhotos.setVisibility(View.GONE);
                    tv_uploadPhotos.setVisibility(View.VISIBLE);
                    tv_uploadPhotos.setText("Upload "+ arrImageList.size() +" Photos" );

                }
                else {
                    Uri imageuri = data.getData();
                    if (imageuri != null) {

                        arrImageList.clear();
                        arrImageList.add(imageuri);
                        tv_addPhotos.setVisibility(View.GONE);
                        tv_uploadPhotos.setVisibility(View.VISIBLE);
                        tv_uploadPhotos.setText("Upload "+ arrImageList.size() +" Photo" );
                    }
                }

            }

        }

    }

    private void setRef() {

        tv_addPhotos = findViewById(R.id.tv_addPhotos);
        tv_uploadPhotos = findViewById(R.id.tv_uploadPhotos);

        tv_back = findViewById(R.id.tv_back);
        tv_placeHolder = findViewById(R.id.tv_placeHolder);

        rv_photos = findViewById(R.id.rv_photos);

    }
}